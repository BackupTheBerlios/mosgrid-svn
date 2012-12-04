package de.mosgrid.gui.inputmask.uploads;

import java.io.File;

import com.vaadin.ui.Component;

import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.util.UploadCollector;

/**
 * Interface for Upload UI components
 * 
 * @author Andreas Zink
 * 
 */
public interface IUploadComponent extends Component {
	/**
	 * @return 'true' if all user input (upload, postprocessing input, etc.) is valid
	 */
	boolean isValid();

	/**
	 * Starts the postprocessor, if any, for uploaded file
	 * 
	 * @throws PostprocessorException
	 *             May be thrown if postprocessing fails
	 */
	void startPostprocessing() throws PostprocessorException;

	/**
	 * Shall add all relevant uploads to the UploadCollector
	 */
	void collectUploads(UploadCollector collector);

	/**
	 * @return The uploaded file or 'null' if not uploaded yet
	 */
	File getUploadedFile();

	/**
	 * @return The wrapped CML upload Element
	 */
	FileUpload getUploadElement();

	/**
	 * Shall perform any necessary MSML template integration e.g. uploaded structures
	 * 
	 * @param initialization
	 *            The initialization element of the corresponding job. This is the only parent element where
	 *            integrations are allowed for upload components.
	 * @throws MSMLConversionException
	 */
	void doTemplateIntegrations(JobInitialization initialization) throws MSMLConversionException;
	
	/**
	 * Adds a listener which is notified if a file has been successfully uploaded
	 */
	void addUploadListener(IUploadListener listener);

	void removeUploadListener(IUploadListener listener);
}
