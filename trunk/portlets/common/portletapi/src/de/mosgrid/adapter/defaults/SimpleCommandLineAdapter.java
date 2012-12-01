package de.mosgrid.adapter.defaults;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.AdapterException;
import de.mosgrid.adapter.AdapterForMSML;
import de.mosgrid.adapter.InputInfoForParameter;
import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.ScalarType;
import de.mosgrid.msml.util.wrapper.JobInitialization;

/**
 * Adapter for command line parameters. Fills a Map with <PARAM_NAME,VALUE> pairs. For Flags, null is set as value. The
 * value for empty parameters (may be optional) is a special DELETE constant which is processed in the
 * InputInfoForParameter class. This is necessary to delete parameters which may be set in the gUSE workflow
 * configuration.
 * 
 */
public class SimpleCommandLineAdapter extends AdapterForMSML {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAdapter.class);

	public SimpleCommandLineAdapter() {
	}

	@Override
	public void doInit() {
	}

	@Override
	public InputInfo calculateAdaption() throws AdapterException {
		Map<String, String> args = new HashMap<String, String>();
		JobInitialization init = getInfo().getJob().getInitialization();
		for (ParameterType parameter : init.getParamList().getParameter()) {
			EntryType entry = getInfo().getMSML().getDictEntry(parameter.getDictRef());
			if (entry == null) {
				LOGGER.warn("Could not resolve dictref '" + parameter.getDictRef() + "' for job "
						+ getInfo().getJob().getTitle());
				continue;
			}
			ScalarType scalar = parameter.getScalar();
			if (scalar == null) {
				LOGGER.warn("Invalid cmd line parameter found for job " + getInfo().getJob().getTitle()
						+ ". Parameter must be of type 'Scalar'");
				continue;
			}

			// check if entry is a flag param
			if (entry.isFlag() != null && entry.isFlag() ) {
				if (scalar.getValue() != null) {
					String value = scalar.getValue().trim();

					if (value.equalsIgnoreCase("on")) {
						args.put(entry.getTerm(), null);
					} else {
						args.put(entry.getTerm(), InputInfoForParameter.CMD_DELETE_FLAG);
					}
				}
			} else {
				// if normal param i.e. no flag
				if (scalar.getValue() != null) {
					String value = scalar.getValue().trim();
					if (!value.equals("")) {
						args.put(entry.getTerm(), value);
					} else {
						args.put(entry.getTerm(), InputInfoForParameter.CMD_DELETE_PARAM);
					}
				}
			}
		}
		InputInfoForParameter info = new InputInfoForParameter(args, getInfo());
		return info;
	}
}
