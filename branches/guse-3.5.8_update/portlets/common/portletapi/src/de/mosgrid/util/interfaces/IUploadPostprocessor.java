package de.mosgrid.util.interfaces;

import java.io.File;
import java.util.Collection;

import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.exceptions.PostprocessorValidationException;
import de.mosgrid.gui.inputmask.uploads.IPostprocessorComponent;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.util.UploadCollector;

/**
 * Interface for Upload Postprocessors. After an upload has been done, the file may need postprocessing. The
 * corresponding Postprocessor is created from the PostprocessorFactory which can be set in the DomainPortlet.
 * 
 * @author Andreas Zink
 * 
 */
public interface IUploadPostprocessor {

	/**
	 * Gets called after file has been successfully uploaded. May already start postprocessing.
	 * 
	 * @param file
	 *            The uploaded file
	 * @throws PostprocessorException
	 *             May be thrown if reading the file already fails
	 */
	void readUploadedFile(File file) throws PostprocessorException;

	/**
	 * @return An additional ui component which is added below the upload field. This can be used for user input which
	 *         may be necessary for postprocessing. May return null if not needed.
	 */
	IPostprocessorComponent getUIComponent();

	/**
	 * Validates the Postprocessor and user input, if any
	 * 
	 * @return 'true' if postprocessor is ready for postprocessing
	 * 
	 * @throws PostprocessorValidationException
	 *             May be thrown if error occurs while validation
	 */
	boolean isValidAndReady() throws PostprocessorValidationException;

	/**
	 * Starts postprocessing if postprocessor is in valid state
	 * 
	 * @throws PostprocessorException
	 */
	void start() throws PostprocessorException;

	/**
	 * @return The original file which was set using the read method
	 */
	File getOriginalFile();

	/**
	 * @return The postprocessed version(s) of the original file which was set using the read method
	 */
	Collection<File> getPostprocessedFiles();

	/**
	 * Shall add all relevant uploads to the UploadCollector
	 * 
	 * @param collector
	 *            The collecotor to add uploads
	 * @param jobID
	 *            The job where uploads shall go to
	 * @param portID
	 *            The port where uploads shall go to
	 */
	void collectUploads(UploadCollector collector, String jobID, String portID);

	/**
	 * Shall perform any necessary MSML template integration e.g. postprocessed molecule structures. Note that an upload
	 * component delegates its 'MSML integration' call to the postprocessor (if any). Thus nothing will be integrated to
	 * MSML if postprocessor won't handle it.
	 * 
	 * @param initialization
	 *            The initialization element of the corresponding job. This is the only parent element where
	 *            integrations are allowed for upload components.
	 * @throws MSMLConversionException
	 */
	void doTemplateIntegrations(JobInitialization initialization) throws MSMLConversionException;

}
