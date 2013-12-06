package de.mosgrid.adapter;

import java.util.ArrayList;
import java.util.List;

import de.mosgrid.adapter.base.InputInfo;

public class InputInfoForFixedCmdLine extends InputInfo implements ICommandLineParameterInputInfo {

	public static final String CMD_DELETE_PARAM = "$_DP";
	public static final String CMD_DELETE_FLAG = "$_DF";

	private List<String> args;

	public InputInfoForFixedCmdLine(List<String> args, AdapterInfoForMSML info) {
		super(info);
		this.args = args;
	}

	public InputInfoForFixedCmdLine(InputInfo info) {
		super(info);
		this.args = ((InputInfoForFixedCmdLine) info).args;
	}

	/**
	 * This function adjusts the commandline to the extracted arguments from
	 * msml. Every parameter from msml is put to the exact place in the
	 * commandline. If the value of the parameter in msml cannot be matched to a
	 * dictionary entry then the corresponding value from the commandline is
	 * used otherwise the value is replaced.
	 * 
	 * @param commandline
	 *            The commandline from gUSE workflow.
	 * 
	 * @return The adjusted commandline.
	 */
	@Override
	public String adjustCommandLine(String commandline) {

		List<String> res = new ArrayList<String>();

		// split current cmd line at any whitespace char (one ore more)
		String[] split = commandline.split("\\s+");

		int maxIndex = Math.max(split.length, args.size());
		for (int i = 0; i < maxIndex; i++) {
			// if msml-args are set and commandline not, just add msml-args
			if ((i <= args.size() - 1) && (i > split.length - 1)) {
				res.add(args.get(i));
				continue;
				// if commandline is set and msml-args not, just add
				// commandline-arg
			} else if ((i > args.size() - 1) && (i <= split.length - 1)) {
				res.add(split[i]);
				continue;
			}

			// at this point both args - msml-args and commandline - have args
			// left.
			// If args from msml is null, then use existing commandline args.
			String curArg = args.get(i);
			if (curArg == null)
				curArg = split[i];
			res.add(curArg);
		}

		String modifiedCommandline = "";
		for (String r : res) {
			modifiedCommandline += r + " ";
		}
		return modifiedCommandline.trim();
	}
}
