package de.mosgrid.util.interfaces;

import de.mosgrid.exceptions.PostprocessorCreationException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Interface for a UploadPostprocessor Factory. After a file has been uploaded by the user, a PostprocessorFactory
 * defined in the Domainportlet is called. If there is any postprocessing which has to be done for the uploaded file, a
 * Postprocessor is returned.
 * 
 * @author Andreas Zink
 * 
 */
public interface IUploadPostprocessorFactory {

	/**
	 * Creates an UploadPostprocessor
	 * 
	 * @param wkfImport
	 *            The postprocessing may be different on workflow level
	 * @param job
	 *            The postprocessing may be different on job level
	 * @param uploadElement
	 *            The postprocessing mainly depends on the given upload filetype
	 * @return A Postprocessor for the given arguments or null if no Postprocessor defined
	 * @throws PostprocessorCreationException
	 *             If the creation of the Postprocessor fails. This could happen because of IOExceptions etc.
	 */
	IUploadPostprocessor createPostprocessor(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws PostprocessorCreationException;

	/**
	 * @return 'true' if factory knows a postprocessor for given arguments
	 */
	boolean knowsPostprocessor(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement);

}
