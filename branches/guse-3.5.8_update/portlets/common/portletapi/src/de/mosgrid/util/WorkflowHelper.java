package de.mosgrid.util;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.editors.MSMLTemplate;

/**
 * Singleton TODO: please comment
 */
public class WorkflowHelper {

	private enum Retrievable {
		USERNAMEGROUP, TEMPLATEGROUP
	}

	private final Logger LOGGER = LoggerFactory.getLogger(WorkflowHelper.class);

	private static WorkflowHelper instance = null;
	private static final Pattern _templateFindPattern = Pattern.compile("[^_]+_(\\d|-)+_(.*)");
	private static final int TEMPLATEGROUP = 2;

	public static final String SEPARATOR = "__";

	private WorkflowHelper() {
	}

	public static synchronized WorkflowHelper getInstance() {
		if (instance == null) {
			instance = new WorkflowHelper();
		}
		return instance;
	}

	/**
	 * Creates the full name which is used for import. Consists of template name + SEPARATOR + user given name
	 */
	public synchronized String getFullWorkflowName(String userChosenName, MSMLTemplate template) {
		return template.getName() + SEPARATOR + userChosenName;
	}

	/**
	 * Retrieves the user given name from a workflow instance
	 */
	public synchronized String getUserChosenName(ASMWorkflow inst) {
		return getPart(inst, Retrievable.USERNAMEGROUP, "userchosen name");
	}

	/**
	 * Retrieves the user given name from a workflow instance
	 */
	public synchronized String getUserChosenName(String fullWorkflowName) {
		return getPart(fullWorkflowName, Retrievable.USERNAMEGROUP, "userchosen name");
	}

	/**
	 * Retrieves the template name from a workflow instance
	 */
	public synchronized String getRepName(ASMWorkflow instance) {
		return getPart(instance, Retrievable.TEMPLATEGROUP, "repository workflow name");
	}

	private synchronized String getPart(ASMWorkflow inst, Retrievable group, String what) {
		return getPart(inst.getWorkflowName(), group, what);
	}

	/**
	 * This function splits the actual workflowname of form "concrete_DATE_TEMPLATE__USER-GIVEN-NAME according to the
	 * SEPARATOR into groups that can be retrieved. Since SEPARATOR may be a word instead of only a single character, a
	 * regular expression is not used for simplicity.
	 * 
	 * @param workflowName
	 *            The actual workflow name
	 * @param group
	 *            The group of the split to retrieve.
	 * @param what
	 *            String for errormessage.
	 * @return The "group"th element of the split workflowname.
	 */
	private synchronized String getPart(String workflowName, Retrievable group, String what) {
		String[] split = workflowName.split(SEPARATOR);
		String res = resolve(split, group);
		return res;
	}

	private String resolve(String[] split, Retrievable group) {
		if (group == Retrievable.USERNAMEGROUP)
			return split[split.length - 1];

		String res = split[0];
		// if we have more than two groups the template name contains SEPARATOR
		// thus we have to concatenate everything but the date and the last split.
		if (split.length != 2) {
			LOGGER.warn("Does the template's name contain '" + SEPARATOR + "'? This is discouraged.");
			String tmp = "";
			for (int i = 0; i < split.length - 2; i++)
				tmp += split[i];
			res = tmp;
		}

		// res guaranteed contains now concrete_DATE_TEMPLATE (maybe with __ or _ in TEMPLATE)

		Matcher m = _templateFindPattern.matcher(res);
		if (!m.find())
			return res;
		return m.group(TEMPLATEGROUP);
	}
}
