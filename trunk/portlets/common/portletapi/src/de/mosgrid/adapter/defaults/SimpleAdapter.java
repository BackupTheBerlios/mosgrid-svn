package de.mosgrid.adapter.defaults;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.AdapterException;
import de.mosgrid.adapter.AdapterForMSMLForInputFile;
import de.mosgrid.adapter.InputInfoForInputFile;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.ScalarType;
import de.mosgrid.msml.util.wrapper.JobInitialization;

/**
 * Creates file content in simple Java properties-file style i.e. each line consists of PARAMETER_NAME = VALUE. For
 * example Gromacs supports this type of input files (mdp format).
 * 
 */
public class SimpleAdapter extends AdapterForMSMLForInputFile {
	private final Logger LOGGER = LoggerFactory.getLogger(SimpleAdapter.class);

	public SimpleAdapter() {
	}

	@Override
	public void doInit() {
	}

	@Override
	protected InputInfoForInputFile getInputInfoForInputFile() throws AdapterException {
		StringBuilder fileContentBuilder = new StringBuilder();

		JobInitialization init = getInfo().getJob().getInitialization();
		List<ParameterType> params = init.getParamList().getParameter();
		if (params == null)
			throw new AdapterException("Job " + getInfo().getJob().getId() + 
					" has an adapter set, but no parameters. This does not make sense.");
		for (ParameterType parameter : params) {
			EntryType entry = getInfo().getMSML().getDictEntry(parameter.getDictRef());
			if (entry == null) {
				LOGGER.warn("Could not resolve dictref: " + parameter.getDictRef());
				continue;
			}
			ScalarType scalar = parameter.getScalar();
			if (scalar == null) {
				LOGGER.warn("Invalid parameter found for job " + getInfo().getJob().getTitle()
						+ ". Parameter must be of type 'Scalar'");
				continue;
			}
			// only set parameter if not null or empty
			if (scalar.getValue() != null) {
				String value = scalar.getValue().trim();
				if (!value.equals("")) {
					fileContentBuilder.append(entry.getTerm() + " = " + value + "\n");
				}
			} else if (parameter.isOptional() != null && !parameter.isOptional()) {
				// if parameter is not given but actually required
				LOGGER.warn("Missing required parameter value for job " + getInfo().getJob().getTitle()
						+ " with parameter " + entry.getTerm());
			}
		}
		InputInfoForInputFile info = new InputInfoForInputFile(fileContentBuilder.toString(), getInfo());
		return info;
	}
}
