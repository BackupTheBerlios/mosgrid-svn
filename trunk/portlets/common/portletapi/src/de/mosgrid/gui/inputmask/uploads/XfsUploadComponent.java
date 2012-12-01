package de.mosgrid.gui.inputmask.uploads;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.gui.xfs.IXfsBrowserListener;
import de.mosgrid.gui.xfs.IXfsFileFilter;
import de.mosgrid.gui.xfs.XfsFileBrowser.FileType;
import de.mosgrid.gui.xfs.XfsFileBrowserWindow;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.portlet.PortletStatus;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.TempFileHelper;

/**
 * Upload component for uploading files from XFS. Unfortunately these files have to be downloaded, postprocessed and
 * uploaded again. This is because a user could manually upload files to XFS and thus undergo postprocessing.
 * 
 * @author Andreas Zink
 * 
 */
public class XfsUploadComponent extends AbstractUploadComponent implements Button.ClickListener, IXfsBrowserListener {
	private static final long serialVersionUID = -4816810582491473948L;
	private final Logger LOGGER = LoggerFactory.getLogger(XfsUploadComponent.class);
	private final String CAPTION_BUTTON;
	private final String TOOLTIP_BUTTON;
	/* instance variables */
	protected boolean cancelledDownload;
	/* UI Components */
	private Button uploadButton;
	protected XfsFileBrowserWindow browserWindow;

	public XfsUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement) {
		super(wkfImport, job, uploadElement);
		this.CAPTION_BUTTON = "Choose " + msmlUploadElement.getFileType().toUpperCase();
		this.TOOLTIP_BUTTON = "Please choose a *." + msmlUploadElement.getFileType() + " file from your XtreemFS storage.";
	}

	@Override
	protected void buildUploadComponent() {
		uploadButton = new Button(CAPTION_BUTTON);
		uploadButton.setDescription(TOOLTIP_BUTTON);
		uploadButton.addListener((Button.ClickListener) this);

		uploadLayout.addComponent(uploadButton);
		uploadLayout.setComponentAlignment(uploadButton, Alignment.BOTTOM_LEFT);
	}

	// called on upload button click
	@Override
	public void buttonClick(ClickEvent event) {
		cancelledDownload = false;
		browserWindow = new XfsFileBrowserWindow(TOOLTIP_BUTTON);

		browserWindow.addFileSelectFilter(new IXfsFileFilter() {
			// only allow files of correct type
			@Override
			public boolean acceptFile(Object itemID, DirectoryEntry dirEntry, String fullFilename, FileType type) {
				if (type == FileType.DIR) {
					return false;
				} else if (!dirEntry.getName().endsWith("." + msmlUploadElement.getFileType())) {
					return false;
				}
				return true;
			}

			@Override
			public String getPattern() {
				return "Only *." + msmlUploadElement.getFileType() + " files";
			}
		});
		browserWindow.addButtonListener((IXfsBrowserListener) this);
		getWindow().addWindow(browserWindow);
	}

	@Override
	protected void clickedUploadCancelButton() {
		cancelledDownload = true;
		toggleCancelButton(false);
		toggleProgressIndicator(false);
	}

	@Override
	public void clickedFileBrowsersOkButton(Object itemID, DirectoryEntry dirEntry, String fullFilePath, FileType type) {
		if (browserWindow != null) {
			getWindow().removeWindow(browserWindow);
		}
		downloadFile(dirEntry, fullFilePath);
	}

	/**
	 * Downloads a file from xfs
	 */
	private void downloadFile(final DirectoryEntry dirEntry, final String fullFilePath) {
		LOGGER.debug(portlet.getUser() + " Trying to upload " + fullFilePath);
		toggleCancelButton(true);
		toggleProgressIndicator(true);
		requestRepaint();
		// start download thread
		Runnable downloadTask = new Runnable() {
			@Override
			public void run() {
				String filename = dirEntry.getName();
				int blockSize = 4096;
				float totalSizeInBlocks = dirEntry.getStbuf().getSize() / blockSize;

				BufferedInputStream in = null;
				BufferedOutputStream out = null;

				try {
					final File file = TempFileHelper.createTempFile(filename);
					in = new BufferedInputStream(portlet.getXfsBridge().getDownloadStream(fullFilePath));
					out = new BufferedOutputStream(new FileOutputStream(file));

					byte[] buf = new byte[blockSize];
					int len;
					int loopIndex = 1;
					while ((len = in.read(buf)) > 0 && !cancelledDownload) {
						out.write(buf, 0, len);
						float progress = 0.9f * ((float) loopIndex / (float) totalSizeInBlocks);
						uploadProgressIndicator.setValue(progress);
						loopIndex++;
					}
					uploadSucceeded(file, filename);
				} catch (FileNotFoundException e) {
					uploadFailed(filename);
					LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
				} catch (IOException e) {
					uploadFailed(filename);
					LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
				} finally {
					try {
						if (in != null) {
							in.close();
						}
						if (out != null) {
							out.close();
						}
					} catch (IOException e) {

					}
				}
			}
		};
		portlet.getExecutorService().execute(downloadTask);
	}

	/*
	 * Called after upload completed successfully
	 */
	private void uploadSucceeded(File file, String filename) {
		this.uploadedFile = file;
		toggleCancelButton(false);
		uploadSucceeded = true;
		msmlUploadElement.setValue(filename);
		fileNameField.setValue(filename);

		String message = MESSAGE_UPLOAD_SUCCEEDED + filename;
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
		requestRepaint();
		
		//inform listeners
		for(IUploadListener l:listenerList){
			try {
				l.fileArrived(this, new BufferedReader(new FileReader(file)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Called if upload failed or was interrupted
	 */
	private void uploadFailed(String filename) {
		this.uploadSucceeded = false;
		toggleProgressIndicator(false);
		toggleCancelButton(false);
		fileNameField.setValue("");

		// remove old postprocessor component if already set
		if (postprocessorComponent != null) {
			mainLayout.removeComponent(postprocessorComponent);
		}

		StringBuilder msgBuilder = new StringBuilder(ERROR_UPLOAD_FAILED + filename + ". ");

		if (!filename.endsWith(msmlUploadElement.getFileType())) {
			msgBuilder.append(TOOLTIP_BUTTON);
			Notification notif = NotificationFactory.createWarningNotification("Upload failed!", msgBuilder.toString());
			getWindow().showNotification(notif);
		} else {
			Notification notif = NotificationFactory.createWarningNotification("Upload failed!", msgBuilder.toString());
			getWindow().showNotification(notif);
		}
		LOGGER.info(portlet.getUser() + msgBuilder.toString());

		removeAllComponentErrors();
		requestRepaint();
	}

	@Override
	public void clickedFileBrowserCancelButton() {
		if (browserWindow != null) {
			getWindow().removeWindow(browserWindow);
		}
	}

}
