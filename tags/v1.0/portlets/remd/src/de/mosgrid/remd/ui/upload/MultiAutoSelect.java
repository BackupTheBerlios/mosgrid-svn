package de.mosgrid.remd.ui.upload;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.gui.inputmask.AbstractJobForm;
import de.mosgrid.gui.xfs.IXfsBrowserListener;
import de.mosgrid.gui.xfs.XfsFileBrowser.FileType;
import de.mosgrid.gui.xfs.XfsFileBrowserWindow;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.remd.properties.RemdProperties;
import de.mosgrid.util.NotificationFactory;

/**
 * Basic AutoSelect GUI component for multiple files
 * 
 * @author Andreas Zink
 * 
 */
public abstract class MultiAutoSelect extends AbstractJobForm implements Button.ClickListener, IXfsBrowserListener {
	private static final long serialVersionUID = 7277744473921019109L;
	private final Logger LOGGER = LoggerFactory.getLogger(MultiAutoSelect.class);

	private final String CAPTION_BUTTON_AUTO_SELECT = "Auto Search";
	private final String TOOLTIP_BUTTON_AUTO_SELECT = "Detects matching files from previously submitted jobs.";
	private final String CAPTION_LABEL_SELECTION = "Selection:";
	private final String CAPTION_DEF_CONTENT = "none";

	protected final DomainPortlet portlet;
	protected final String jobSearchId;
	protected final ImportedWorkflow wkfImport;

	// results
	private int replicaCount;
	private String absoluteResultPath;
	private boolean filesAlreadyRenamed;

	// for validation
	private boolean foundResultFiles;

	/* UI compontents */
	protected VerticalLayout mainLayout;
	protected Label selectedLabel;
	protected XfsFileBrowserWindow browserWindow;

	public MultiAutoSelect(DomainPortlet portlet, Job job, ImportedWorkflow wkfImport, String jobSearchId) {
		super(job);
		this.portlet = portlet;
		this.wkfImport = wkfImport;
		this.jobSearchId = jobSearchId;
		fillLeftColumn();
	}

	private void fillLeftColumn() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setImmediate(true);

		Button autoSearchButton = new Button(CAPTION_BUTTON_AUTO_SELECT);
		autoSearchButton.setDescription(TOOLTIP_BUTTON_AUTO_SELECT);
		autoSearchButton.addListener((Button.ClickListener) this);
		mainLayout.addComponent(autoSearchButton);

		this.selectedLabel = new Label(CAPTION_DEF_CONTENT);
		this.selectedLabel.setCaption(CAPTION_LABEL_SELECTION);
		selectedLabel.setImmediate(true);
		mainLayout.addComponent(selectedLabel);

		addComponentToLeftColumn(mainLayout);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		browserWindow = new XfsFileBrowserWindow("Please select a result directory");
		browserWindow.setBrowser(new AutoSelectBrowser(jobSearchId));
		browserWindow.addButtonListener((IXfsBrowserListener) this);
		getWindow().addWindow(browserWindow);
	}

	@Override
	public void clickedFileBrowsersOkButton(Object itemID, DirectoryEntry dirEntry, String fullFilePath, FileType type) {
		if (browserWindow != null) {
			getWindow().removeWindow(browserWindow);
		}
		// init

		foundResultFiles = false;
		absoluteResultPath = null;
		filesAlreadyRenamed = false;
		selectedLabel.setValue(dirEntry.getName());

		fullFilePath += "/0";
		// iterate over job dirs
		try {
			for (DirectoryEntry jobEntry : portlet.getXfsBridge().listEntries(fullFilePath)) {
				String jobName = jobEntry.getName();
				if (!jobName.startsWith(".")) {
					if (jobName.equals(jobSearchId)) {
						String baseTprName = RemdProperties.get(RemdProperties.REMD_TPR_NAME);
						absoluteResultPath = find(baseTprName + "0.tpr", fullFilePath + "/" + jobName);
						if (absoluteResultPath == null) {
							absoluteResultPath = find(cut(baseTprName) + ".tpr_0", fullFilePath + "/" + jobName);
							filesAlreadyRenamed = true;
						}
						if (absoluteResultPath != null) {
							countReplicas();
							// update label
							selectedLabel.setValue(dirEntry.getName() + ", " + replicaCount + " replicas");
							// set valid
							foundResultFiles = true;
						} else {
							throw new RuntimeException("The chosen directory does not contain the correct files");
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(portlet.getUser() + " Error while parsing REMD result dir.", e);
			Notification notif = NotificationFactory.createErrorNotification("Error!",
					"Could not parse selected result directory.<br>" + e.getMessage());
			getWindow().showNotification(notif);
		}
	}

	/**
	 * Recursive search for files
	 * 
	 * @return The parent path in which the file lives
	 * 
	 */
	protected String find(String filename, String parentPath) throws IOException {
		for (DirectoryEntry child : portlet.getXfsBridge().listEntries(parentPath)) {
			if (!child.getName().startsWith(".")) {
				if (portlet.getXfsBridge().isDirectory(child)) {
					return find(filename, parentPath + "/" + child.getName());
				} else {
					if (child.getName().equals(filename)) {
						return parentPath;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Counts the number of replica result files in given path
	 */
	protected void countReplicas() throws IOException {
		String baseTprName = RemdProperties.get(RemdProperties.REMD_TPR_NAME);
		replicaCount = 0;
		boolean oneMorePlease = true;
		DirectoryEntry[] files = portlet.getXfsBridge().listEntries(absoluteResultPath);
		while (oneMorePlease) {
			oneMorePlease = false;
			for (DirectoryEntry file : files) {
				if (!file.getName().startsWith(".")) {
					if ((filesAlreadyRenamed && file.getName().equals(cut(baseTprName) + ".tpr_" + replicaCount))
							|| (file.getName().equals(baseTprName + replicaCount + ".tpr"))) {
						replicaCount++;
						oneMorePlease = true;
						break;

					}
				}
			}
		}
	}

	/**
	 * Shifts the index to end of file for parameter sweep workflows for all files which match the given criteria
	 */
	protected void renameAll(String baseFilename, String filetype) throws IOException {
		LOGGER.debug(portlet.getUser() + " Moving index to end of name for " + filetype + " files in "
				+ absoluteResultPath);
		String newBaseFilename = cut(baseFilename);
		for (int i = 0; i < replicaCount; i++) {
			String oldName = baseFilename + i + filetype;
			String newName = newBaseFilename + filetype + "_" + i;
			portlet.getXfsBridge().rename(oldName, newName, absoluteResultPath);
		}
	}

	/**
	 * Cuts trailing '_' from filenames
	 */
	protected String cut(String filename) {
		return (filename.endsWith("_") ? filename.substring(0, (filename.length() - 1)) : filename);
	}

	@Override
	public void clickedFileBrowserCancelButton() {
		if (browserWindow != null) {
			getWindow().removeWindow(browserWindow);
		}
	}

	public int getReplicaCount() {
		return replicaCount;
	}

	public String getAbsoluteResultPath() {
		return absoluteResultPath;
	}

	public boolean foundResultFiles() {
		return foundResultFiles;
	}

	public boolean areFilesAlreadyRenamed() {
		return filesAlreadyRenamed;
	}

}
