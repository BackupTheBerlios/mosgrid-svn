package de.mosgrid.portlet.listener;

import de.mosgrid.exceptions.ImportFailedException;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Listener for workflow import events
 * 
 * @author Andreas Zink
 * 
 */
public interface IImportListener {
	/**
	 * Gets called after a workflow has been imported and its input mask has been created
	 */
	void importSucceeded(ImportedWorkflow newImport);

	/**
	 * Gets called if import process failed
	 */
	void importFailed(MSMLTemplate failedImport, String userImportName, ImportFailedException e);
}
