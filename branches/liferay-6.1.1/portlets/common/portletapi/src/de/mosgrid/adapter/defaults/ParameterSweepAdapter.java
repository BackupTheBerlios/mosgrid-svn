package de.mosgrid.adapter.defaults;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.AdapterForMSMLForInputFiles;
import de.mosgrid.adapter.InputInfoForInputFiles;
import de.mosgrid.msml.jaxb.bindings.ArrayType;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.ScalarType;
import de.mosgrid.msml.util.wrapper.JobInitialization;

/**
 * A default adapter for parameter sweep workflows. This adapter can produce several input files. Therefore parameters
 * which shall differ in input files have to be given as array instead of scalar elements. The size of each parameter
 * array has to be equal!
 * 
 * @author Andreas Zink
 * 
 */
public class ParameterSweepAdapter extends AdapterForMSMLForInputFiles {
	private final Logger LOGGER = LoggerFactory.getLogger(ParameterSweepAdapter.class);

	@Override
	public void doInit() {

	}

	@Override
	protected InputInfoForInputFiles getInputInfoForInputFiles() {
		InputInfoForInputFiles inputInfo = new InputInfoForInputFiles(getInfo());

		JobInitialization init = getInfo().getJob().getInitialization();
		List<ParameterType> parameterList = init.getParamList().getParameter();
		int numberOfFiles = parseNumberOfInputFiles(parameterList);
		StringBuilder[] fileContentBuilderArray = createStringBuilderArray(numberOfFiles);

		for (ParameterType parameter : parameterList) {
			EntryType entry = getInfo().getMSML().getDictEntry(parameter.getDictRef());
			if (entry == null) {
				LOGGER.warn("Could not resolve dictref: " + parameter.getDictRef());
				continue;
			}

			if (parameter.getScalar() != null) {
				ScalarType scalar = parameter.getScalar();
				// only set parameter if not null or empty
				if (scalar.getValue() != null) {
					String value = scalar.getValue().trim();
					if (!value.equals("")) {
						// append all content builders with this value
						appendAllContentBuilders(fileContentBuilderArray, entry, value);
					}
				} else if (parameter.isOptional() != null && !parameter.isOptional()) {
					// if parameter is not given but actually required
					LOGGER.warn("Missing required parameter value for job " + getInfo().getJob().getTitle()
							+ " with parameter " + entry.getTerm());
				}
			} else if (parameter.getArray() != null) {
				ArrayType array = parameter.getArray();
				if (array.getValue() != null) {
					String value = array.getValue();
					String[] values = value.split("\\s");
					if (values.length == numberOfFiles) {
						appendAllContentBuilders(fileContentBuilderArray, entry, values);
					} else {
						LOGGER.warn("Wrong parameter array size in job " + getInfo().getJob().getTitle()
								+ " with parameter " + entry.getTerm());
					}
				} else if (parameter.isOptional() != null && !parameter.isOptional()) {
					// if parameter is not given but actually required
					LOGGER.warn("Missing required parameter value for job " + getInfo().getJob().getTitle()
							+ " with parameter " + entry.getTerm());
				}
			}

		}
		inputInfo.addAll(fileContentBuilderArray);
		return inputInfo;
	}

	protected void appendAllContentBuilders(StringBuilder[] fileContentBuilderArray, EntryType entry, String value) {
		for (int i = 0; i < fileContentBuilderArray.length; i++) {
			StringBuilder contentBuilder = fileContentBuilderArray[i];
			contentBuilder.append(entry.getTerm() + " = " + value + "\n");
		}
	}

	protected void appendAllContentBuilders(StringBuilder[] fileContentBuilderArray, EntryType entry, String[] values) {
		for (int i = 0; i < fileContentBuilderArray.length; i++) {
			StringBuilder contentBuilder = fileContentBuilderArray[i];
			contentBuilder.append(entry.getTerm() + " = " + values[i] + "\n");
		}
	}

	protected StringBuilder[] createStringBuilderArray(int size) {
		StringBuilder[] fileContentBuilderArray = new StringBuilder[size];
		for (int i = 0; i < size; i++) {
			fileContentBuilderArray[i] = new StringBuilder();
		}
		return fileContentBuilderArray;
	}

	protected Integer parseNumberOfInputFiles(List<ParameterType> parameterList) {
		for (ParameterType parameter : parameterList) {
			if (parameter.getArray() != null) {
				return parameter.getArray().getSize().intValue();
			}
		}
		return null;
	}
}
