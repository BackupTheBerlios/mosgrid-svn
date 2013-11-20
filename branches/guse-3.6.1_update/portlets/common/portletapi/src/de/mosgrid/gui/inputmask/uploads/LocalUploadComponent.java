package de.mosgrid.gui.inputmask.uploads;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.portlet.PortletStatus;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.TempFileHelper;

/**
 * Upload component for this from local machine.
 * 
 * @author Andreas Zink
 * 
 */
public class LocalUploadComponent extends AbstractUploadComponent implements Upload.StartedListener,
		Upload.SucceededListener, Upload.FailedListener, Upload.Receiver, Upload.ProgressListener {
	private static final long serialVersionUID = -2297251630890220001L;
	private final Logger LOGGER = LoggerFactory.getLogger(LocalUploadComponent.class);
	private final String CAPTION_BUTTON;
	private final String TOOLTIP_BUTTON;

	/* ui components */
	private Upload uploadComponent;
	private IPostprocessorComponent postprocessorComponent;

	/**
	 * Default constructor
	 * 
	 * @param wkfImport
	 *            The parent wkf import
	 * @param job
	 *            The parent job
	 * @param uploadElement
	 *            The corresponding MSML upload element
	 */
	public LocalUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload msmlUploadElement) {
		super(wkfImport, job, msmlUploadElement);
		this.CAPTION_BUTTON = "Upload " + msmlUploadElement.getFileType().toUpperCase();
		this.TOOLTIP_BUTTON = "Please upload a *." + msmlUploadElement.getFileType() + " file from your computer.";
	}

	@Override
	protected void buildUploadComponent() {
		uploadComponent = new Upload(null, this);
		// set immediate in order to display the upload button only!
		uploadComponent.setImmediate(true);
		uploadComponent.setButtonCaption(CAPTION_BUTTON);
		uploadComponent.setDescription(TOOLTIP_BUTTON);
		uploadComponent.addListener((SucceededListener) this);
		uploadComponent.addListener((FailedListener) this);
		uploadComponent.addListener((StartedListener) this);
		uploadComponent.addListener((ProgressListener) this);
		// set custom error handler
		// uploadComponent.setErrorHandler(new UploadErrorHandler());

		uploadLayout.addComponent(uploadComponent);
		uploadLayout.setComponentAlignment(uploadComponent, Alignment.BOTTOM_LEFT);
	}

	@Override
	public void updateProgress(long readBytes, long contentLength) {
		if (uploadProgressIndicator.isEnabled()) {
			float progress = 0.9f * ((float) readBytes / (float) contentLength);
			uploadProgressIndicator.setValue(progress);
		}
	}

	/*
	 * Called after upload started. Checks if upload has correct filetype and iterrupts if not.
	 */
	@Override
	public void uploadStarted(StartedEvent event) {
		removeAllComponentErrors();
		String filename = event.getFilename();
		if (filename.endsWith(msmlUploadElement.getFileType())) {
			toggleProgressIndicator(true);
			toggleCancelButton(true);
		} else {
			// interrupt Upload if wrong filetype, fires FailedEvent
			LOGGER.trace(portlet.getUser() + " Tried to upload file with wrong filetype.");
			uploadComponent.interruptUpload();
		}
	}

	/*
	 * Called if upload failed or was interrupted
	 */
	@Override
	public void uploadFailed(FailedEvent event) {
		this.uploadSucceeded = false;
		toggleProgressIndicator(false);
		toggleCancelButton(false);
		fileNameField.setValue("");

		// remove old postprocessor component if already set
		if (postprocessorComponent != null) {
			mainLayout.removeComponent(postprocessorComponent);
		}

		StringBuilder msgBuilder = new StringBuilder(ERROR_UPLOAD_FAILED + event.getFilename() + ". ");

		if (!event.getFilename().endsWith(msmlUploadElement.getFileType())) {
			msgBuilder.append(TOOLTIP_BUTTON);
			Notification notif = NotificationFactory.createWarningNotification("Upload failed!", msgBuilder.toString());
			getWindow().showNotification(notif);
		} else {
			if (event.getReason() != null) {
				msgBuilder.append(event.getReason().getMessage());
			}
			Notification notif = NotificationFactory.createWarningNotification("Upload failed!", msgBuilder.toString());
			getWindow().showNotification(notif);
		}
		LOGGER.info(portlet.getUser() + msgBuilder.toString());

		removeAllComponentErrors();
	}

	/*
	 * Called after upload completed successfully
	 */
	@Override
	public void uploadSucceeded(SucceededEvent event) {
		toggleCancelButton(false);
		uploadSucceeded = true;
		msmlUploadElement.setValue(event.getFilename());
		fileNameField.setValue(event.getFilename());

		String message = MESSAGE_UPLOAD_SUCCEEDED + event.getFilename();
		portlet.setStatusMessage(message, PortletStatus.SUCCEEDED);
		LOGGER.debug(portlet.getUser() + message + " to " + uploadedFile);

		if (postprocessor != null) {
			try {
				postprocessor.readUploadedFile(uploadedFile);
				uploadProgressIndicator.setValue(0.95f);
				// remove old component if already set
				if (postprocessorComponent != null) {
					mainLayout.removeComponent(postprocessorComponent);
				}
				// set new component if any
				postprocessorComponent = postprocessor.getUIComponent();
				if (postprocessorComponent != null) {
					mainLayout.addComponent(postprocessorComponent);
				}
				uploadProgressIndicator.setValue(1f);
			} catch (PostprocessorException e) {
				uploadSucceeded = false;
				Notification notif = NotificationFactory.createWarningNotification("Postprocessing failed!",
						"The postprocessing of your upload failed. <br>" + e.getMessage());
				getWindow().showNotification(notif);
			}
		}
		toggleProgressIndicator(false);

		// inform listeners
		for (IUploadListener l : listenerList) {
			try {
				l.fileArrived(this, new BufferedReader(new FileReader(uploadedFile)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		BufferedOutputStream bos = null;

		String msg = " Trying to upload " + filename;
		if (portlet != null) {
			LOGGER.trace(portlet.getUser() + msg);
		} else {
			LOGGER.trace(msg);
		}

		try {
			this.uploadedFile = TempFileHelper.createTempFile(filename);

			FileOutputStream fos = new FileOutputStream(uploadedFile);
			bos = new BufferedOutputStream(fos);
		} catch (IOException e) {
			String errorMsg = " Could not create temporary file " + filename;
			if (portlet != null) {
				LOGGER.error(portlet.getUser() + errorMsg, e);
			} else {
				LOGGER.error(errorMsg, e);
			}
		}

		return bos;
	}

	@Override
	protected void clickedUploadCancelButton() {
		uploadComponent.interruptUpload();
	}

	// private class ExtendedUpload extends Upload {
	// private static final long serialVersionUID = 1706218106050953386L;
	//
	// private ExtendedUpload(Receiver r) {
	// super(null, r);
	// setImmediate(true);
	// }
	//
	// public void removeAllComponentErrors() {
	// setComponentError(null);
	// Iterator<Component> iterator = getComponentIterator();
	// while (iterator.hasNext()) {
	// Component childComponent = iterator.next();
	// if (childComponent instanceof AbstractComponent) {
	// ((AbstractComponent) childComponent).setComponentError(null);
	// }
	// }
	// }
	//
	// @Override
	// public void setErrorHandler(ComponentErrorHandler errorHandler) {
	// super.setErrorHandler(errorHandler);
	//
	// Iterator<Component> iterator = getComponentIterator();
	// while (iterator.hasNext()) {
	// Object child = iterator.next();
	// if (child instanceof AbstractComponent) {
	// AbstractComponent c = (AbstractComponent) child;
	// c.setErrorHandler(errorHandler);
	// }
	// }
	// }
	//
	// @Override
	// public void startUpload() {
	// try {
	// super.startUpload();
	// } catch (Exception e) {
	// LOGGER.error("Caught silly e!", e);
	// }
	//
	// }
	//
	// @Override
	// public void submitUpload() {
	// try {
	// super.submitUpload();
	// } catch (Exception e) {
	// LOGGER.error("Caught silly e!", e);
	// }
	// }
	//
	// }

	// private class UploadErrorHandler implements ComponentErrorHandler {
	// private static final long serialVersionUID = 3620071162174658897L;
	//
	// @Override
	// public boolean handleComponentError(ComponentErrorEvent event) {
	// return true;
	// }
	// }

}
