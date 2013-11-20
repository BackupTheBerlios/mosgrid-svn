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

	private static WorkflowHelper instance = new WorkflowHelper();
	private static final Pattern _templateFindPattern = Pattern.compile("[^_]+_(\\d|-)+_(.*)");
	private static final int TEMPLATEGROUP = 2;

	public static final String SEPARATOR = "__";

	private WorkflowHelper() {
	}

	public static synchronized WorkflowHelper getInstance() {
		return instance;
	}

	/**
	 * Creates the full name which is used for import. Consists of template name, user chosen name, userId, current time and
	 * the string {@code mosgrid}, each being delimited by the separator
	 */
	public synchronized String generateFullWorkflowName(String userChosenName, MSMLTemplate template, final String userId) {
		return template.getName() + SEPARATOR + userChosenName + SEPARATOR + userId + SEPARATOR + System.currentTimeMillis() + SEPARATOR + "mosgrid";
	}

	/**
	 * Retrieves the user given name from a workflow instance
	 */
	public synchronized String getUserChosenName(ASMWorkflow inst) {
		return getPart(inst, Retrievable.USERNAMEGROUP);
	}

	/**
	 * Retrieves the user given name from a workflow instance
	 */
	public synchronized String getUserChosenName(String fullWorkflowName) {
		return getPart(fullWorkflowName, Retrievable.USERNAMEGROUP);
	}

	/**
	 * Retrieves the template name from a workflow instance
	 */
	public synchronized String getRepName(ASMWorkflow instance) {
		return getPart(instance, Retrievable.TEMPLATEGROUP);
	}

	private synchronized String getPart(ASMWorkflow inst, Retrievable group) {
		return getPart(inst.getWorkflowName(), group);
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
	private synchronized String getPart(String workflowName, Retrievable group) {
		return resolve(workflowName, group);
	}

	private String resolve(final String workflowName, final Retrievable group) {
		// a typical wf name would be: NWChem__NWChemTest__23996__4568796465788965__mosgrid_2013-11-14-155903
		final String split[] = workflowName.split(SEPARATOR);
		String res = "";
		if (group == Retrievable.USERNAMEGROUP) {
			res = split[1];
		} 
		if (group == Retrievable.TEMPLATEGROUP) {
			res = split[0];
			// if we have more than two groups the template name contains SEPARATOR
			// thus we have to concatenate everything but the date and the last split.			
			final int extraContent = split.length - 5; 
			if (extraContent > 0) {
				LOGGER.warn("Invalid WF name [" + workflowName + "]!!! Does the template's name contain the separator [" + SEPARATOR + "]? This is HIGHGLY discouraged.");
				final StringBuilder tmp = new StringBuilder(split[0]);
				// retrieve from the beginning of the name until we are done with the extra content
				for (int i = 1; i <= extraContent; i++)
					tmp.append(split[i]);
				res = tmp.toString();
			}
	
			// res guaranteed contains now concrete_DATE_TEMPLATE (maybe with __ or _ in TEMPLATE)
			final Matcher m = _templateFindPattern.matcher(res);
			if (!m.find())
				return res;
			return m.group(TEMPLATEGROUP);
		}
		return res;
	}
}
