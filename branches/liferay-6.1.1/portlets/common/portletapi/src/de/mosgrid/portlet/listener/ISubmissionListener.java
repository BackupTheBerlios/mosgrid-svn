package de.mosgrid.portlet.listener;

import de.mosgrid.exceptions.RemovingFailedException;
import de.mosgrid.exceptions.SubmissionFailedException;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Listener for workflow submission events
 * 
 * @author Andreas Zink
 * 
 */
public interface ISubmissionListener {

	/**
	 * Gets called if workflow has been successfully submitted
	 */
	void submissionSucceeded(ImportedWorkflow wkfImport);

	/**
	 * Gets called if workflow submission fails
	 */
	void submissionFailed(ImportedWorkflow failedImport,SubmissionFailedException e);

	/**
	 * Gets called if workflow has been successfully removed
	 */
	void removalSucceeded(ImportedWorkflow wkfImport);

	/**
	 * Gets called if workflow removal fails
	 */
	void removalFailed(ImportedWorkflow failedImport,RemovingFailedException e);
}
