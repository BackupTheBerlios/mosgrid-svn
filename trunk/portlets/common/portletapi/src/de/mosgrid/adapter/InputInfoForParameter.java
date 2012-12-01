package de.mosgrid.adapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.mosgrid.adapter.base.InputInfo;

public class InputInfoForParameter extends InputInfo implements ICommandLineParameterInputInfo {

	public static final String CMD_DELETE_PARAM = "$_DP";
	public static final String CMD_DELETE_FLAG = "$_DF";

	private Map<String, String> args;

	public InputInfoForParameter(Map<String, String> args, AdapterInfoForMSML info) {
		super(info);
		this.args = args;
	}

	public InputInfoForParameter(InputInfoForParameter info) {
		super(info);
		this.args = info.args;
	}

	@Override
	public String adjustCommandLine(String commandline) {
		// create working copy of input params
		Map<String, String> inputParams = new HashMap<String, String>(args);

		// split current cmd line at any whitespace char (one ore more)
		String[] split = commandline.split("\\s+");

		// at first try to replace existing values
		for (int i = 0; i < split.length; i++) {
			String key = split[i];
			if (inputParams.containsKey(key)) {
				String newValue = inputParams.get(key);
				// NOTE: if newValue == null, this is a flag and nothing has to be replaced
				if (newValue != null) {
					if (newValue.equals(CMD_DELETE_FLAG)) {
						// delete existing flag
						split[i] = null;
					} else if (newValue.equals(CMD_DELETE_PARAM)) {
						// delete existing param and value
						split[i++] = null;
						split[i] = null;
					} else if (newValue.length() > 0) {
						// replace existing param value
						split[++i] = newValue;
					}
				}
				// remove this param
				inputParams.remove(key);
			}
		}
		// build updated cmd line
		StringBuilder cmdLineBuilder = new StringBuilder();
		for (String part : split) {
			if (part != null) {
				cmdLineBuilder.append(part + " ");
			}
		}

		// extend cmd line with new params which did not occur in given 'commandline' string
		if (inputParams.size() > 0) {
			Iterator<Entry<String, String>> entryIterator = inputParams.entrySet().iterator();
			// iterate over params which are still in map
			while (entryIterator.hasNext()) {
				Entry<String, String> entry = entryIterator.next();

				if (entry.getValue() == null) {
					// extend cmd line by flag parameter
					cmdLineBuilder.append(entry.getKey() + " ");
				} else if (!entry.getValue().equals(CMD_DELETE_FLAG) && !entry.getValue().equals(CMD_DELETE_PARAM)) {
					// if value is no DELETE cmd, extend cmd line with value
					cmdLineBuilder.append(entry.getKey() + " " + entry.getValue() + " ");
				}
			}
		}

		return cmdLineBuilder.toString().trim();
	}
}
