package de.mosgrid.gui.tabs.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.gui.CardLayout;
import de.mosgrid.gui.tabs.monitoring.MonitoringTab.ItemProperty;
import de.mosgrid.gui.tabs.monitoring.MonitoringTab.ItemType;
import de.mosgrid.util.NotificationFactory;

/**
 * Constantly shown tab which provides meta data for the currently selected tree item. Consists of a CardLayout which
 * holds a component for each tree item type (workflows, files, etc). These components are shown and updated on tree
 * selection changes.
 * 
 */
public class MetaDataTab extends CustomComponent implements ValueChangeListener {
	private static final long serialVersionUID = 645061087294005635L;
	private final Logger LOGGER = LoggerFactory.getLogger(MetaDataTab.class);
	public static final String CAPTION_TAB_METADATA = "Metadata";

	private CardLayout mainLayout = new CardLayout();
	private WorkflowMetaDataComponent workflowMetaDataComponent = new WorkflowMetaDataComponent();
	private FileMetaDataComponent fileMetaDataComponent = new FileMetaDataComponent();
	private MonitoringTab monitoringTab;

	public MetaDataTab(MonitoringTab monitoringTab) {
		this.monitoringTab = monitoringTab;

		setSizeFull();
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void buildMainLayout() {
		mainLayout.setMargin(true);
		mainLayout.setImmediate(true);
		mainLayout.setSizeFull();
		mainLayout.addComponent(workflowMetaDataComponent);
		mainLayout.addComponent(fileMetaDataComponent);
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Object itemId = event.getProperty().getValue();
		Item currentItem = monitoringTab.getItem(itemId);
		update(currentItem);

	}

	/**
	 * Show metadata for given item
	 */
	public void update(Item item) {
		try {
			if (item != null) {
				ItemType type = (ItemType) item.getItemProperty(ItemProperty.TYPE).getValue();
				if (type == ItemType.WORKFLOW) {
					workflowMetaDataComponent.setInstance(item);
					mainLayout.showComponent(workflowMetaDataComponent);
				} else if (type == ItemType.DIRECTORY || type == ItemType.FILE) {
					fileMetaDataComponent.setFile(item);
					mainLayout.showComponent(fileMetaDataComponent);
				}
			} else {
				// show empty component
				mainLayout.showComponent(null);
			}
		} catch (Exception e) {
			String msg = "Error while updating meta data!";
			Notification notif = NotificationFactory.createFailedNotification(msg);
			getWindow().showNotification(notif);
			LOGGER.error(monitoringTab.getPortlet().getUser() + " " + msg, e);
		}
	}

	/**
	 * Metadata component for workflow items (child of MetaDataTab)
	 * 
	 */
	private class WorkflowMetaDataComponent extends CustomComponent {
		private static final long serialVersionUID = 7033536623581383150L;

		private VerticalLayout mainLayout = new VerticalLayout();
		private CustomLabel importNameField = new CustomLabel("Import-name:");
		private CustomLabel workflowIDField = new CustomLabel("Instance-ID:");
		private CustomLabel runtimeIDField = new CustomLabel("Runtime-ID:");
		private CustomLabel statusField = new CustomLabel("Status:");

		public WorkflowMetaDataComponent() {
			setCompositionRoot(mainLayout);
			buildMainLayout();
		}

		private void buildMainLayout() {
			mainLayout.setSpacing(true);
			mainLayout.setSizeFull();
			mainLayout.addComponent(importNameField);
			mainLayout.addComponent(workflowIDField);
			mainLayout.addComponent(runtimeIDField);
			mainLayout.addComponent(statusField);

		}

		public void setInstance(Item wkfItem) {
			this.importNameField.setValue(wkfItem.getItemProperty(ItemProperty.NAME).getValue());

			Object data = wkfItem.getItemProperty(ItemProperty.DATA).getValue();
			if (data instanceof ASMWorkflow) {
				ASMWorkflow wkfInstance = (ASMWorkflow) data;
				this.workflowIDField.setValue(wkfInstance.getWorkflowName());

				String userID = monitoringTab.getPortlet().getUser().getUserID();
				String runtimeID = monitoringTab.getPortlet().getRuntimeID(userID, wkfInstance.getWorkflowName());
				this.runtimeIDField.setValue(runtimeID);

				String wkfStatus = wkfInstance.getStatusbean().getStatus();
				this.statusField.setValue(wkfStatus);
			}
		}
	}

	/**
	 * Metadata component for file items (child of MetaDataTab)
	 * 
	 */
	private class FileMetaDataComponent extends CustomComponent {
		private static final long serialVersionUID = 4434830524821023764L;
		private VerticalLayout mainLayout;
		private CustomLabel fileNameField;
		private CustomLabel filePathField;
		private CustomLabel fileSizeField;
		private CustomLabel modifiedField;

		public FileMetaDataComponent() {
			buildMainLayout();
			setCompositionRoot(mainLayout);
		}

		private void buildMainLayout() {
			mainLayout = new VerticalLayout();
			mainLayout.setSpacing(true);
			mainLayout.setSizeFull();

			fileNameField = new CustomLabel("Name:");
			fileNameField.setImmediate(true);
			filePathField = new CustomLabel("Path:");
			filePathField.setImmediate(true);
			fileSizeField = new CustomLabel("Size:");
			fileSizeField.setImmediate(true);
			modifiedField = new CustomLabel("Last Modified:");
			modifiedField.setImmediate(true);

			mainLayout.addComponent(fileNameField);
			mainLayout.addComponent(filePathField);
			mainLayout.addComponent(fileSizeField);
			mainLayout.addComponent(modifiedField);
		}

		public void setFile(Item fileItem) {
			String fileName = fileItem.getItemProperty(ItemProperty.NAME).getValue().toString();
			fileNameField.setValue(fileName);
			filePathField.setValue(fileItem.getItemProperty(ItemProperty.XFS_PATH).getValue().toString());

			try {
				DirectoryEntry dirEntry = (DirectoryEntry) fileItem.getItemProperty(ItemProperty.DATA).getValue();
				String fileSizeString = monitoringTab.getPortlet().getXfsBridge().getSize(dirEntry);
				fileSizeField.setValue(true);
				if (fileSizeString.length() > 0) {
					fileSizeField.setVisible(true);
				} else {
					fileSizeField.setVisible(false);
				}
				fileSizeField.setValue(fileSizeString);
			} catch (Exception e) {
				// disable field
				fileSizeField.setVisible(false);
			}

			try {
				if (!modifiedField.isVisible()) {
					modifiedField.setValue(true);
				}
				DirectoryEntry dirEntry = (DirectoryEntry) fileItem.getItemProperty(ItemProperty.DATA).getValue();
				String lastModified = monitoringTab.getPortlet().getXfsBridge().getLastModified(dirEntry);
				modifiedField.setValue(lastModified);
			} catch (Exception e) {
				// disable field
				modifiedField.setVisible(false);
			}
		}
	}

	/**
	 * Custom Label with bold caption (used in metadata components)
	 */
	private class CustomLabel extends Label {
		private static final long serialVersionUID = 1607265219639076610L;

		private String caption;

		public CustomLabel(String caption) {
			this.caption = "<b>" + caption + "</b><br>";
			setContentMode(Label.CONTENT_XHTML);
			super.setValue(this.caption);
		}

		@Override
		public void setValue(Object newValue) {
			String value = caption + newValue;
			super.setValue(value);
		}

	}
}
