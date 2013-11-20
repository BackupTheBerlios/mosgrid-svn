package de.mosgrid.gui.tabs.monitoring;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;
import org.xtreemfs.portlet.util.vaadin.VaadinFileDownloadResource;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.CloseHandler;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

import de.mosgrid.chemdoodle.converter.IJsonConverter;
import de.mosgrid.chemdoodle.converter.JsonConverterFactory;
import de.mosgrid.chemdoodle.ui.BasicViewer;
import de.mosgrid.chemdoodle.ui.PdbViewer;
import de.mosgrid.chemdoodle.widget.DefType;
import de.mosgrid.dygraph.ui.SimpleGraphViewer;
import de.mosgrid.dygraph.util.DataConverter;
import de.mosgrid.dygraph.widget.IData;
import de.mosgrid.dygraph.widget.SimpleGraph;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.util.IconProvider;
import de.mosgrid.util.IconProvider.ICONS;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.TempFileHelper;
import de.mosgrid.util.WorkflowHelper;

/**
 * @author Andi
 * 
 */
public class MonitoringTab extends CustomComponent {
	private static final long serialVersionUID = 8745609865765223403L;
	private final Logger LOGGER = LoggerFactory.getLogger(MonitoringTab.class);

	/* tree item properties */
	public enum ItemType {
		WORKFLOW, DIRECTORY, FILE, JOB, JOB_DIRECTORY
	};

	// NAME = item display name
	// TYPE = ItemType
	// DATA = ASMWorkflow or DirectoryEntry
	// ICON = icon to display
	// XFS_PATH = root path where job files are written
	// WORKFLOW_UUID = uuid of the workflow
	// IS_PRE_GUSE_358 = boolean flag that determines if an item uses the pre gUSE 3.5.8 structure
	public enum ItemProperty {
		NAME, TYPE, DATA, ICON, XFS_PATH, WORKFLOW_UUID, IS_PRE_GUSE_358
	};

	public static final String CAPTION = "Monitoring";
	
	private static final String CAPTION_TREE = "Submitted Workflows:";
	private static final String CAPTION_BUTTON_UPDATE = "Update";
	private static final String DESC_BUTTON_UPDATE = "Updates workflow list";
	private static final String CAPTION_CHECKBOX_SUBWINDOW = "Open files in new window";
	private static final String DESC_CHECKBOX_SUBWINDOW = "If selected, files will open in a child window instead of a tab. This may give you more rendering options. You can open files over the context menu of an item (right-click).";

	private static final ThemeResource ICON_DIR = IconProvider.getIcon(ICONS.FOLDER);
	private static final ThemeResource ICON_FILE = IconProvider.getIcon(ICONS.DOCUMENT);
	private static final ThemeResource ICON_BUTTON_UPDATE = IconProvider.getIcon(ICONS.RELOAD);
	private static final ThemeResource ICON_WINDOW_DELETE = IconProvider.getIcon(ICONS.TRASH);

	/* ui components */
	private HorizontalSplitPanel splitPanel;
	private VerticalLayout leftSplit;
	private Tree tree;
	private Button updateButton;
	private CheckBox subWindowCheckBox;

	private VerticalLayout rightSplit;
	private TabSheet contentTabSheet;
	private MetaDataTab metaDataTab;

	private DomainPortlet portlet;
	private volatile boolean isUpdating;
	private AbstractContextMenuHandler contextMenuHandler;

	public MonitoringTab(DomainPortlet portlet) {
		this.portlet = portlet;
		buildMainLayout();
		setCompositionRoot(splitPanel);
	}

	protected DomainPortlet getPortlet() {
		return portlet;
	}

	protected boolean isOpenInNewWindow() {
		return (Boolean) subWindowCheckBox.getValue();
	}

	/**
	 * Allows to set a custom context menu handler
	 */
	public void setContextMenuHanlder(AbstractContextMenuHandler handler) {
		tree.removeAllActionHandlers();
		handler.initialize(this);
		contextMenuHandler = handler;
		tree.addActionHandler(handler);
	}

	/**
	 * Gets the context menu handler
	 */
	public AbstractContextMenuHandler getContextMenuHanlder() {
		return contextMenuHandler;
	}

	private void buildMainLayout() {
		splitPanel = new HorizontalSplitPanel();
		splitPanel.setImmediate(true);
		buildLeftSplit();
		buildRightSplit();

		splitPanel.setHeight("500px");
		splitPanel.setSplitPosition(30);
		splitPanel.setFirstComponent(leftSplit);
		splitPanel.setSecondComponent(rightSplit);
	}

	private void buildLeftSplit() {
		leftSplit = new VerticalLayout();
		leftSplit.setImmediate(true);
		leftSplit.setSpacing(true);
		leftSplit.setMargin(true, false, true, true);
		leftSplit.setSizeFull();

		buildTree();
		buildUpdateComponent();
	}

	private void buildUpdateComponent() {
		HorizontalLayout updateButtonContainer = new HorizontalLayout();
		updateButtonContainer.setSpacing(true);
		updateButtonContainer.setImmediate(true);
		updateButton = new Button(CAPTION_BUTTON_UPDATE, new Button.ClickListener() {
			private static final long serialVersionUID = -6003661783506154806L;

			@Override
			public void buttonClick(ClickEvent event) {
				update();
			}
		});
		updateButton.setIcon(ICON_BUTTON_UPDATE);
		updateButton.setDescription(DESC_BUTTON_UPDATE);
		updateButtonContainer.addComponent(updateButton);

		// checkbox which decides if content shall be opened in a tab or child window
		subWindowCheckBox = new CheckBox(CAPTION_CHECKBOX_SUBWINDOW);
		subWindowCheckBox.setDescription(DESC_CHECKBOX_SUBWINDOW);
		updateButtonContainer.addComponent(subWindowCheckBox);

		leftSplit.addComponent(updateButtonContainer);
		leftSplit.setExpandRatio(updateButtonContainer, 0f);
	}

	private void buildTree() {
		tree = new Tree(CAPTION_TREE);
		tree.setSizeFull();
		tree.setContainerDataSource(createTreeContainer());
		tree.setImmediate(true);
		tree.setMultiSelect(false);

		tree.setItemCaptionPropertyId(ItemProperty.NAME);
		tree.setItemIconPropertyId(ItemProperty.ICON);
		tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

		// selection listener
		tree.addListener(new ValueChangeListener() {
			private static final long serialVersionUID = -3198783668064965660L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				// switch to metadata tab of selected file
				if (contentTabSheet != null && metaDataTab != null) {
					if (contentTabSheet.getSelectedTab() != metaDataTab) {
						contentTabSheet.setSelectedTab(metaDataTab);
					}
				}
			}
		});
		// expand listener, lazy loading of xfs data
		tree.addListener(new ExpandListener() {
			private static final long serialVersionUID = -162238804802341366L;

			@Override
			public void nodeExpand(ExpandEvent event) {
				try {
					loadChildItems(event.getItemId());
				} catch (IOException e) {
					String msg = "Error while retrieving content form XTreemFS!";
					Notification notif = NotificationFactory.createErrorNotification("XTreemFS Error!", msg);
					getWindow().showNotification(notif);
					LOGGER.error(portlet.getUser() + " " + msg, e);
				}
			}
		});
		// listen for item double clicks
		tree.addListener(new ItemClickListener() {
			private static final long serialVersionUID = 6781218931157275779L;

			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					Object itemId = event.getItemId();
					if (itemId != null) {
						// expand item
						if (tree.areChildrenAllowed(itemId)) {
							tree.expandItem(itemId);
						} else {
							// open file in raw text mode
							Item fileItem = event.getItem();
							ItemType type = (ItemType) fileItem.getItemProperty(ItemProperty.TYPE).getValue();
							if (type == ItemType.FILE) {
								String filename = (String) fileItem.getItemProperty(ItemProperty.NAME).getValue();
								String fullFilePath = (String) fileItem.getItemProperty(ItemProperty.XFS_PATH)
										.getValue();
								openAndShowFile(filename, fullFilePath);
							}
						}
					}
				}
			}
		});

		// right-click context menu
		contextMenuHandler = new DefaultContextMenuHandler(this);
		tree.addActionHandler(contextMenuHandler);

		Panel treePanel = new Panel(new VerticalLayout());
		treePanel.setStyleName(Reindeer.PANEL_LIGHT);
		treePanel.setScrollable(true);
		treePanel.addComponent(tree);
		treePanel.setSizeFull();

		leftSplit.addComponent(treePanel);
		leftSplit.setExpandRatio(treePanel, 1f);
	}

	private HierarchicalContainer createTreeContainer() {
		HierarchicalContainer container = new HierarchicalContainer();
		container.addContainerProperty(ItemProperty.NAME, String.class, null);
		container.addContainerProperty(ItemProperty.TYPE, ItemType.class, null);
		container.addContainerProperty(ItemProperty.DATA, Object.class, null);
		container.addContainerProperty(ItemProperty.ICON, ThemeResource.class, null);
		container.addContainerProperty(ItemProperty.XFS_PATH, String.class, null);
		container.addContainerProperty(ItemProperty.WORKFLOW_UUID, String.class, null);
		container.addContainerProperty(ItemProperty.IS_PRE_GUSE_358, Boolean.class, false);

		return container;
	}

	private void buildRightSplit() {
		rightSplit = new VerticalLayout();
		rightSplit.setImmediate(true);
		rightSplit.setSpacing(true);
		rightSplit.setSizeFull();

		buildContentTabSheet();
	}

	private void buildContentTabSheet() {
		contentTabSheet = new TabSheet();
		contentTabSheet.setSizeFull();
		contentTabSheet.setImmediate(true);
		contentTabSheet.setCloseHandler(new CloseHandler() {
			private static final long serialVersionUID = -985777029769485717L;

			@Override
			public void onTabClose(TabSheet tabsheet, Component tabContent) {
				if (tabContent instanceof JmolEmbedded) {
					// delete temp jmol file on tab close
					((JmolEmbedded) tabContent).deleteAssociatedFile();
				}
				tabsheet.removeComponent(tabContent);
			}
		});
		contentTabSheet.addListener(new ComponentAttachListener() {
			private static final long serialVersionUID = 101700055524852674L;

			@Override
			public void componentAttachedToContainer(ComponentAttachEvent event) {
				// switch to newly added tab
				contentTabSheet.setSelectedTab(event.getAttachedComponent());
			}
		});
		metaDataTab = new MetaDataTab(this);
		tree.addListener((ValueChangeListener) metaDataTab);
		contentTabSheet.addTab(metaDataTab, MetaDataTab.CAPTION_TAB_METADATA);

		rightSplit.addComponent(contentTabSheet);
	}

	/**
	 * Returns the tree item with given id or null
	 */
	protected Item getItem(Object itemID) {
		return tree.getItem(itemID);
	}

	/**
	 * Updates the complete workflow tree
	 */
	public void update() {
		// will not perform update twice if already updating
		if (!isUpdating) {
			tree.select(null);

			LOGGER.debug(portlet.getUser() + " Updating Monitoring tab");
			updateToggle(true);

			HierarchicalContainer newTreeContainer = createTreeContainer();

			try {
				// get all workflows which are NOT in INIT status
				Collection<ASMWorkflow> workflows = portlet.getAllWorkflows(StatusConstants.INIT, false);

				if (workflows.size() == 0) {
					// there are no submitted workflows...
					Notification notif = NotificationFactory.createTrayNotification("Info",
							"No submitted workflow instances available.");
					portlet.getMainWindow().showNotification(notif);
				}

				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace(portlet.getUser() + " Found " + workflows.size() + " workflow instances");
				}
				for (ASMWorkflow workflowInstance : workflows) {
					// get the name that the user entered (not the one given by gUSE)
					final String workflowDisplayName = WorkflowHelper.getInstance().getUserChosenName(workflowInstance);
					final String workflowName = workflowInstance.getWorkflowName();
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace(portlet.getUser() + " Updating " + workflowName + " ("
							+ workflowInstance.getStatusbean().getStatus() + ")");
					}
					// add tree item
					final Item item = newTreeContainer.addItem(workflowName);
					// when update is invoked, the tree is regenerated, make sure that all items are collapsed
					tree.collapseItem(workflowName);

					if (item != null) {
						newTreeContainer.setChildrenAllowed(workflowName, true);
						item.getItemProperty(ItemProperty.NAME).setValue(workflowDisplayName);
						item.getItemProperty(ItemProperty.TYPE).setValue(ItemType.WORKFLOW);
						item.getItemProperty(ItemProperty.DATA).setValue(workflowInstance);
						// find status icon
						updateWkfStatus(workflowInstance, item);
						// set xfs root
						final String runtimeID = portlet.getAsmService().getRuntimeID(portlet.getUser().getUserID(),
								workflowInstance.getWorkflowName());
						String xfsRootPath = portlet.getXfsBridge().getResultsDir();
						item.getItemProperty(ItemProperty.WORKFLOW_UUID).setValue(runtimeID);
						if (isWorkflow_before_gUSE_358(runtimeID)) {
							// the root will be "xtreemfs://.../results/<runtimeID>/
							xfsRootPath = xfsRootPath + '/' + runtimeID;
						}
						if (LOGGER.isTraceEnabled()) {
							LOGGER.trace("root path for workflow [" + workflowName + "] with uuid [" + runtimeID + "] is [" + xfsRootPath + ']');
						}
						item.getItemProperty(ItemProperty.XFS_PATH).setValue(xfsRootPath);
					} else {
						LOGGER.info(portlet.getUser() + " Failed to update " + workflowName);
					}
				}
				tree.requestRepaint();
			} catch (Exception e) {
				String msg = "Error while updating workflows!";
				Notification notif = NotificationFactory.createFailedNotification(msg);
				getWindow().showNotification(notif);
				LOGGER.error(portlet.getUser() + " " + msg, e);
			} finally {
				updateToggle(false);
				tree.setContainerDataSource(newTreeContainer);
			}
		}
	}

	/**
	 * @param runtimeID
	 * @param xfsRootPath
	 * @return
	 * @throws IOException 
	 */
	private boolean isWorkflow_before_gUSE_358(final String runtimeID) throws IOException {
		// determine if there is a folder under "results" named exactly like runtimeID
		final String resultsDir = portlet.getXfsBridge().getResultsDir();
		if (portlet.getXfsBridge().exists(resultsDir + '/' + runtimeID)) {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Workflow with uuid [" + runtimeID + "] has the pre-3.5.8 structure");
			}
			return true;
		} else {
			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Workflow with uuid [" + runtimeID + "] has the post-3.5.8 structure");
			}
			return false;
		}
	}

	/**
	 * Helper method which sets the correct status icon for wkf items
	 */
	private ThemeResource updateWkfStatus(ASMWorkflow workflowInstance, Item item) {
		ThemeResource newIcon = IconProvider.getIcon(ICONS.GRAY_DOT);
		String wkfStatus = workflowInstance.getStatusbean().getStatus();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(portlet.getUser() + " status of workflow (" + WorkflowHelper.getInstance().getUserChosenName(workflowInstance) + ") is [" + wkfStatus + ']');
		}
		if (wkfStatus.equals(StatusConstants.getStatus(StatusConstants.RUNNING))
				|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.READY))
				|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.INIT))) {
			LOGGER.trace("yellow");
			newIcon = IconProvider.getIcon(ICONS.YELLOW_DOT);
		} else if (wkfStatus.equals(StatusConstants.getStatus(StatusConstants.FINISHED))) {
			LOGGER.trace("green");
			newIcon = IconProvider.getIcon(ICONS.GREEN_DOT);
		} else if (wkfStatus.equals(StatusConstants.getStatus(StatusConstants.ERROR))
				|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.ABORTED))
				|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.CANCELLED))) {
			LOGGER.trace("red");
			newIcon = IconProvider.getIcon(ICONS.RED_DOT);
		} else if (wkfStatus.equals(StatusConstants.getStatus(StatusConstants.SUSPENDED))
				|| wkfStatus.equals(StatusConstants.getStatus(StatusConstants.WORKFLOW_SUSPENDED))) {
			LOGGER.trace("blue");
			newIcon = IconProvider.getIcon(ICONS.BLUE_DOT);
		} else {
			LOGGER.trace("status for WF not found!");
		}

		item.getItemProperty(ItemProperty.ICON).setValue(newIcon);

		return newIcon;
	}

	/**
	 * Helper methods which toggles some ui elements before and after updating
	 */
	private void updateToggle(boolean toggle) {
		isUpdating = toggle;
		updateButton.setEnabled(!toggle);
		tree.setEnabled(!toggle);
		contentTabSheet.setEnabled(!toggle);
	}

	/**
	 * Updates a single wkf item. The item with given itemID MUST be of ItemType=Workflow
	 * 
	 * @param wkfItem
	 *            The item id of the workflow to be updated
	 */
	protected void updateWorkflow(Object itemID) {

		// first collapse item
		if (tree.isExpanded(itemID)) {
			tree.collapseItemsRecursively(itemID);
		}
		Item item = tree.getItem(itemID);
		if (item != null) {
			ASMWorkflow wkfInstance = (ASMWorkflow) item.getItemProperty(ItemProperty.DATA).getValue();
			LOGGER.debug(portlet.getUser() + " Updating workflow " + itemID + " ("
					+ wkfInstance.getStatusbean().getStatus() + ")");
			ThemeResource icon = updateWkfStatus(wkfInstance, item);
			tree.setItemIcon(itemID, icon);
			tree.requestRepaint();
			metaDataTab.update(item);
		}
	}

	/**
	 * Loading of child items on expand
	 */
	private void loadChildItems(Object parentItemID) throws IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(portlet.getUser() + " Loading child items for " + parentItemID);
		}
		final Item parentItem = tree.getItem(parentItemID);
		if (parentItem != null) {
			// ALWAYS get the LATEST, don't do lazy loading
			// get all items to remove, including children items and children of the children and so on...			
			final Queue<Object> parentsToProcess = new LinkedList<Object>(Arrays.asList(parentItemID));
			final Queue<Object> childrenToRemove = new LinkedList<Object>();			
			while(!parentsToProcess.isEmpty()) {
				final Collection<?> children = tree.getChildren(parentsToProcess.remove());
				if (children != null) {
					for (final Object id : children) {
						childrenToRemove.add(id);
						parentsToProcess.add(id);
					}
				}
			}
			// remove
			for (final Object id : childrenToRemove) {
				tree.removeItem(id);
			}
			tree.requestRepaint();
			// get all xfs entries
			final String xfsParentPath = (String) parentItem.getItemProperty(ItemProperty.XFS_PATH).getValue();
			final Collection<CustomDirectoryEntry> customEntries = getChildrenEntriesForItem(parentItem);
			// now process each entry iff it doesn't exist already
			for (final CustomDirectoryEntry customEntry : customEntries) {
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("loadChildItems - adding " + customEntry);
				}
				createTreeItem(customEntry, xfsParentPath, parentItemID);				
			}
		} else {
			LOGGER.warn("parentItem == null!");
		}
		tree.requestRepaint();
	}
	
	private Collection<CustomDirectoryEntry> getChildrenEntriesForItem(final Item parentItem) throws IOException {
		// first, assume that the old structure is present
		final Collection<CustomDirectoryEntry> entries;
		if (isWorkflowItem(parentItem)) {
			entries = getChildrenEntriesForWorkflowItem(parentItem);
		} else if (isJobItem(parentItem)) {
			entries = getChildrenEntriesForJobItem(parentItem);
		} else if (isJobFolderItem(parentItem)) {
			entries = getChildrenEntriesForJobFolderItem(parentItem);
		} else {
			// no idea what it could be... maybe a folder that was generated... simply load everything underneath
			final String xfsParentPath = (String) parentItem.getItemProperty(ItemProperty.XFS_PATH).getValue();
			LOGGER.info("getCustomEntriesForItem - unexpected item type. Loading everything for item [" + parentItem + ']');
			entries = convertDirectoryEntries(listEntriesIgnoreHiddenFiles(xfsParentPath), xfsParentPath);			
		}
		return entries;
	}

	// gUSE structure for a Job folder is as follows:
	// JobName/
	//   |
	//   |- 0/
	//   |  |
	//   |  |- adf637-34827f-234cd-234de3
	//   |       |
	//   |       |- stdout.txt
	//   |       |- stderr.txt
	//   |       |...
	//   |- 1/
	//   |  |
	//   |  |- ba4679-bead1-009889-99999
	//   |       |...
	//   |- N/
	//      |
	//      |...
	// under each job folder, there's a numerated folder (0-N) for each job that was executed (in the case of jobs run after a generator job)
	// under ach numerated folder, the guse-id of the run job is found and under this folder the generated files can be done
	// this is true for pre 3.5.8 and 3.5.8 gUSE versions
	// what we want is a simple logical structure where each job gets its own folder (i.e. JobName-0, JobName-1) and under these folders the
	// generated files will be found
	private Collection<CustomDirectoryEntry> getChildrenEntriesForJobItem(final Item parentItem) throws IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getChildrenEntriesForJobItem - getting children entries for [" + parentItem + ']');
		}
		final Collection<CustomDirectoryEntry> customEntries = new LinkedList<CustomDirectoryEntry>();
		// first, we need to determine if several instances of this job were run. we want jobs with only one instance to display their
		// results without that awkward "0/" folder
		final String xfsPath = (String) parentItem.getItemProperty(ItemProperty.XFS_PATH).getValue();
		final Collection<DirectoryEntry> entries = listEntriesIgnoreHiddenFiles(xfsPath);
		// small trick: if there's only one entry, don't show the entry as Job/0/item, rather, Job/item
		// if, however, there's more than one entry, load normally (Job/0/item, Job/0/item2, Job/1/item)
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getChildrenEntriesForJobItem - obtained " + entries.size() + " entries for " + parentItem);
		}
		if (entries.size() == 1) {
			customEntries.addAll(extractJobOutputs(xfsPath + '/' + entries.iterator().next().getName()));
		} else {
			customEntries.addAll(convertDirectoryEntries(entries, xfsPath));
		}
		return customEntries;
	}
	
	// given JobName/X extracts all files under JobName/X/<hash>
	private Collection<CustomDirectoryEntry> getChildrenEntriesForJobFolderItem(final Item parentItem) throws IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getChildrenEntriesForJobFolderItem - getting children entries for [" + parentItem + ']');
		}
		return extractJobOutputs((String) parentItem.getItemProperty(ItemProperty.XFS_PATH).getValue());
	}
	
	// from JobName/X extracts all files under JobName/X/<hash>/
	private Collection<CustomDirectoryEntry> extractJobOutputs(final String jobSubFolderLocation) throws IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("extractJobOutputs - extracting job outputs for [" + jobSubFolderLocation + ']');
		}
		final Collection<DirectoryEntry> subFolders = listEntriesIgnoreHiddenFiles(jobSubFolderLocation);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("extractJobOutputs - got " + subFolders.size() + " subfolders for [" + jobSubFolderLocation + ']');
		}
		final Collection<CustomDirectoryEntry> customEntries;
		if (subFolders.size() == 1) {
			// JobName/X should have only one entry, namely the folder with a hash-code containing all entries
			final String jobOutputLocation = jobSubFolderLocation + '/' + subFolders.iterator().next().getName();
			final Collection<DirectoryEntry> jobOutputs = listEntriesIgnoreHiddenFiles(jobOutputLocation);
			customEntries = convertDirectoryEntries(jobOutputs, jobOutputLocation);
		} else {
			LOGGER.warn("extractJobOutpus - path [" + jobSubFolderLocation + "] contains more than one folder!");
			customEntries = convertDirectoryEntries(subFolders, jobSubFolderLocation);
		}
		return customEntries;
	}

	// converts a collection of DirectoryEntry to CustomDirectoryEntry, nothing fancy
	private Collection<CustomDirectoryEntry> convertDirectoryEntries(final Collection<DirectoryEntry> entries, final String parentPath) throws IOException {
		final Collection<CustomDirectoryEntry> customEntries = new LinkedList<CustomDirectoryEntry>();
		for (final DirectoryEntry entry : entries) {
			final CustomDirectoryEntry customEntry = 
					new CustomDirectoryEntry(entry.getName(), parentPath + '/' + entry.getName(), entry.getName(), portlet.getXfsBridge().isDirectory(entry));
			customEntries.add(customEntry);
		}
		return customEntries;
	}
	
	// list xfs entries ignoring hidden files
	private Collection<DirectoryEntry> listEntriesIgnoreHiddenFiles(final String xfsPath) throws IOException {
		final Collection<DirectoryEntry> entries = new LinkedList<DirectoryEntry>();
		for (final DirectoryEntry entry : portlet.getXfsBridge().listEntries(xfsPath)) {
			if (!isHiddenFile(entry.getName())) {
				entries.add(entry);
			}
		}
		return entries;
	}
	

	private boolean isWorkflowItem(final Item item) {
		final Property typeProperty = item.getItemProperty(ItemProperty.TYPE);
		final boolean isWorkflow = (typeProperty != null) && (ItemType.WORKFLOW == (ItemType)typeProperty.getValue());
		return isWorkflow;
	}
	
	private boolean isJobItem(final Item item) {
		final Property typeProperty = item.getItemProperty(ItemProperty.TYPE);
		final boolean isJob = (typeProperty != null) && (ItemType.JOB == (ItemType)typeProperty.getValue());
		return isJob;
	}
	
	private boolean isJobFolderItem(final Item item) {
		final Property typeProperty = item.getItemProperty(ItemProperty.TYPE);
		final boolean isJobFolder = (typeProperty != null) && (ItemType.JOB_DIRECTORY == (ItemType)typeProperty.getValue());
		return isJobFolder;
	}
	
	private Collection<CustomDirectoryEntry> getChildrenEntriesForWorkflowItem(final Item parentItem) throws IOException {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("getChildrenEntriesForWorkflowItem - getting children entries for [" + parentItem + ']');
		}
		// first, assume that the old structure is present
		Collection<CustomDirectoryEntry> entries = getChildrenEntriesForWorkflowItem_before_gUSE_358(parentItem);		
		if (entries.isEmpty()) {
			// if there's nothing, assume that the new folder structure is there
			entries = getChildrenEntriesForWorkflowItem_after_gUSE_358(parentItem);
		}
		return entries;
	}
	
	// this is the structure of the results folder before the 3.5.8 update
	// results/
	//     |
	//     | - 5518344960123268zentest
	//            |
	//            |- 0/
	//               |
	//               | - Job01/
	//               |     |
	//               |     |...
	//               | - Job02/
    //               |     |
    //               |     |...
	// each workflow has its own folder and jobs are found under the subfolder '0' 
	private Collection<CustomDirectoryEntry> getChildrenEntriesForWorkflowItem_before_gUSE_358(final Item parentItem) throws IOException {
		final String xfsFilePath = (String) parentItem.getItemProperty(ItemProperty.XFS_PATH).getValue() + "/0";
		// xfsFilePath should be xtreemfs://.../results/5518344960123268zentest for workflows
		final Collection<DirectoryEntry> entries = listEntriesIgnoreHiddenFiles(xfsFilePath);
		final Collection<CustomDirectoryEntry> customEntries = new LinkedList<CustomDirectoryEntry>();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("before_guse_358 - found " + entries.size() + " entries for item [" + parentItem + "]");
		}
		for (final DirectoryEntry entry : entries) {
			final CustomDirectoryEntry customEntry = 
					new CustomDirectoryEntry(entry.getName(), xfsFilePath + '/' + entry.getName(), entry.getName(), portlet.getXfsBridge().isDirectory(entry));
			customEntries.add(customEntry);
		}
		return customEntries;
	}
	
	// for version 3.5.8 of guse, the structure of the results folder looks like:
	// results/
	//     |
	//     |- 5518344960123268zentest-Job01
	//     |      |
	//     |      |...
	//     |- 5518344960123268zentest-Job02
	//     |      |
	//     |...   |...
	// each job has a separate folder. there is no exclusive folder for a workflow. all job folder names start with the UUID of the workflow
	private Collection<CustomDirectoryEntry> getChildrenEntriesForWorkflowItem_after_gUSE_358(final Item parentItem) throws IOException {
		final String xfsFilePath = (String) parentItem.getItemProperty(ItemProperty.XFS_PATH).getValue();
		// xfsFilePath should be xtreemfs://.../results
		final String uuid = (String) parentItem.getItemProperty(ItemProperty.WORKFLOW_UUID).getValue();
		final Collection<DirectoryEntry> entries =  getXfsEntriesStartingWith(xfsFilePath, uuid);
		final Collection<CustomDirectoryEntry> customEntries = new LinkedList<CustomDirectoryEntry>();
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("after_guse_358 - found " + entries.size() + " entries for item [" + parentItem + "]");
		}
		for (final DirectoryEntry entry : entries) {
			// the name of the entry is something like 5518344960123268zentest-Job02, but we need only "Job02"
			final String fixedName = entry.getName().substring(uuid.length() + 1);
			final CustomDirectoryEntry customEntry = 
					new CustomDirectoryEntry(entry.getName(), xfsFilePath + '/' + entry.getName(), fixedName, portlet.getXfsBridge().isDirectory(entry));
			customEntries.add(customEntry);
		}
		return customEntries;
	}
	
	
	// returns XFS entries that start with the given parameter
	private Collection<DirectoryEntry> getXfsEntriesStartingWith(final String rootPath, final String start) throws IOException {
		Collection<DirectoryEntry> matchingEntries = new LinkedList<DirectoryEntry>();
		for (DirectoryEntry dirEntry : portlet.getXfsBridge().listEntries(rootPath)) {
			if (dirEntry.getName().startsWith(start)) {
				matchingEntries.add(dirEntry);
			}
		}
		return matchingEntries;
	}

	/**
	 * Creates a tree item
	 * 
	 * @param dirEntry
	 * @param parentPath
	 * @param parentID
	 * @throws IOException 
	 */
	private void createTreeItem(CustomDirectoryEntry dirEntry, String parentPath, Object parentID) throws IOException {
		String filename = dirEntry.getName();		
		if (!isHiddenFile(filename)) {
			// we need to determine if we need to fix the name and path of the element, for this,
			// it is easier if we examine the parent
			final Item parentItem = tree.getItem(parentID);

			try {
				// add item to table
				Item tableItem = tree.addItem(dirEntry.getFullPath());
				if (parentID != null) {
					tree.setParent(dirEntry.getFullPath(), parentID);
				}
				// set type and icon
				if (dirEntry.isDirectory()) {
					tree.setChildrenAllowed(dirEntry.getFullPath(), true);
					if (isWorkflowItem(parentItem)) {
						tableItem.getItemProperty(ItemProperty.TYPE).setValue(ItemType.JOB);
					} else if (isJobItem(parentItem)){
						tableItem.getItemProperty(ItemProperty.TYPE).setValue(ItemType.JOB_DIRECTORY);						
					} else {
						tableItem.getItemProperty(ItemProperty.TYPE).setValue(ItemType.DIRECTORY);
					}
					tableItem.getItemProperty(ItemProperty.ICON).setValue(ICON_DIR);
				} else {
					tree.setChildrenAllowed(dirEntry.getFullPath(), false);
					tableItem.getItemProperty(ItemProperty.TYPE).setValue(ItemType.FILE);
					tableItem.getItemProperty(ItemProperty.ICON).setValue(ICON_FILE);
				}
				// set name, path
				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace("createTreeItem - parentId=" + parentID + 
							", name=" + dirEntry.getName() + 
							", type=" + tableItem.getItemProperty(ItemProperty.TYPE).getValue() + 
							", path=" + dirEntry.getFullPath());
				}
				tableItem.getItemProperty(ItemProperty.NAME).setValue(dirEntry.getDisplayName());
				tableItem.getItemProperty(ItemProperty.XFS_PATH).setValue(dirEntry.getFullPath());
				tableItem.getItemProperty(ItemProperty.DATA).setValue(dirEntry);

			} catch (Exception e) {
				String msg = "Error while retrieving content form XTreemFS!";
				Notification notif = NotificationFactory.createErrorNotification("XTreemFS Error!", msg);
				getWindow().showNotification(notif);
				LOGGER.error(portlet.getUser() + " " + msg, e);
			}
		}
	}

	private boolean isHiddenFile(final String fileName) {
		return fileName.startsWith(".");
	}

	/**
	 * @return A new empty window
	 */
	private Window createChildWindow() {
		Window childWindow = new Window();
		((VerticalLayout) childWindow.getContent()).setMargin(false);
		((VerticalLayout) childWindow.getContent()).setSizeFull();
		childWindow.setWidth("80%");
		childWindow.setHeight("80%");
		childWindow.center();

		return childWindow;
	}

	/**
	 * Creates and shows a new child window with given content
	 */
	private Window showChildWindow(Component c, String windowCaption) {
		Window childWindow = createChildWindow();
		childWindow.setCaption(windowCaption);
		childWindow.addComponent(c);

		portlet.getMainWindow().addWindow(childWindow);
		return childWindow;
	}

	/**
	 * Deletes a workflow instance behind item with given id. The item MUST be of ItemType=Workflow
	 */
	protected void deleteWorkflow(Object itemID) {
		LOGGER.debug(portlet.getUser() + " Aborting " + itemID);
		Item wkfItem = tree.getItem(itemID);
		if (wkfItem != null) {
			ASMWorkflow wkfInstance = (ASMWorkflow) wkfItem.getItemProperty(ItemProperty.DATA).getValue();
			if (wkfInstance != null) {
				try {
					String importName = (String) wkfItem.getItemProperty(ItemProperty.NAME).getValue();
					String runtimeID = ASMService.getInstance().getRuntimeID(portlet.getUser().getUserID(),
							wkfInstance.getWorkflowName());
					createModalDialog(importName, runtimeID);
					portlet.removeASMInstance(wkfInstance);
				} catch (Exception e) {
					LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
					Notification notif = NotificationFactory
							.createFailedNotification("An error occured while deleting your workflow.");
					portlet.getMainWindow().showNotification(notif);
				} finally {
					update();
				}
			}
		}
	}

	/**
	 * Creates a model dialog which askes if all files of workflow shall be deleted from xfs
	 */
	private void createModalDialog(final String importName, final String runtimeID) {
		final Window modalDialog = new Window();
		modalDialog.setClosable(false);
		modalDialog.setIcon(ICON_WINDOW_DELETE);
		modalDialog.setModal(true);
		VerticalLayout layout = (VerticalLayout) modalDialog.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeUndefined();

		Label message = new Label("Also delete uploads and results of this workflow?");
		message.setSizeFull();
		modalDialog.addComponent(message);

		final ProgressIndicator deletionIndicator = new ProgressIndicator();
		deletionIndicator.setIndeterminate(true);
		deletionIndicator.setPollingInterval(500);
		deletionIndicator.setVisible(false);
		deletionIndicator.setEnabled(false);

		final Button yesButton = new Button("Yes");
		final Button noButton = new Button("No");

		yesButton.addListener(new ClickListener() {
			private static final long serialVersionUID = -8947394556315548281L;

			@Override
			public void buttonClick(ClickEvent event) {
				yesButton.setEnabled(false);
				noButton.setEnabled(false);
				deletionIndicator.setVisible(true);
				deletionIndicator.setEnabled(true);
				Runnable deleteTask = new Runnable() {
					@Override
					public void run() {
						try {
							deleteUploads(importName);
							deleteResults(runtimeID);
						} catch (Exception e) {
							LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
							Notification notif = NotificationFactory.createWarningNotification("Failed!",
									"Could not delete workflow files.");
							portlet.getMainWindow().showNotification(notif);
						} finally {
							portlet.getMainWindow().removeWindow(modalDialog);
						}
					}
				};
				portlet.getExecutorService().execute(deleteTask);

			}
		});

		noButton.addListener(new ClickListener() {
			private static final long serialVersionUID = -3373293782386379748L;

			@Override
			public void buttonClick(ClickEvent event) {
				portlet.getMainWindow().removeWindow(modalDialog);
			}
		});

		HorizontalLayout buttonContainer = new HorizontalLayout();
		buttonContainer.setSpacing(true);
		buttonContainer.setSizeFull();
		buttonContainer.addComponent(yesButton);
		buttonContainer.addComponent(noButton);
		buttonContainer.addComponent(deletionIndicator);
		layout.addComponent(buttonContainer);

		modalDialog.center();
		modalDialog.setResizable(false);
		portlet.getMainWindow().addWindow(modalDialog);
	}

	private void deleteUploads(String importName) throws Exception {
		String uploadPath = portlet.getXfsBridge().getUploadDir() + "/" + importName;
		LOGGER.trace(portlet.getUser() + " Trying to delete workflow uploads for " + importName + "\n\t" + uploadPath);
		deleteRecursiv(uploadPath);
	}

	private void deleteResults(String workflowName) throws Exception {
		String resultPath = portlet.getXfsBridge().getResultsDir() + "/" + workflowName;
		LOGGER.trace(portlet.getUser() + " Ttrying to delete workflow results for " + workflowName + "\n\t"
				+ resultPath);
		deleteRecursiv(resultPath);
	}

	private void deleteRecursiv(String fileName) throws IOException {
		if (!fileName.endsWith(".") || !fileName.endsWith("..")) {
			org.xtreemfs.common.clients.File file = portlet.getXfsBridge().getFile(fileName);
			if (file != null && file.exists()) {
				if (file.isFile()) {
					// LOGGER.trace(portlet.getUser() + " Deleting file from XFS:\n\t" + fileName);
					file.delete();
				} else {
					// delete child files
					List<String> files = portlet.getXfsBridge().getFileList(fileName);
					for (String childFile : files) {
						deleteRecursiv(fileName + (fileName.endsWith("/") ? "" : "/") + childFile);
					}
					// delete dir
					// LOGGER.trace(portlet.getUser() + " Deleting directory from XFS:\n\t" + fileName);
					file.delete();
				}
			} else {
				// LOGGER.info(portlet.getUser() + " Trying to delete XFS file, but does not exist:\n\t" + fileName);
			}
		}
	}

	/**
	 * Aborts a workflow instance behind item with given id. The item MUST be of ItemType=Workflow
	 */
	protected void abortWorkflow(Object itemID) {
		LOGGER.debug(portlet.getUser().getUserID() + " Aborting " + itemID);
		Item wkfItem = tree.getItem(itemID);
		if (wkfItem != null) {
			ASMWorkflow wkfInstance = (ASMWorkflow) wkfItem.getItemProperty(ItemProperty.DATA).getValue();
			if (wkfInstance != null) {
				try {
					portlet.getAsmService().abort(portlet.getUser().getUserID(), wkfInstance.getWorkflowName());
				} catch (Exception e) {
					LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
					Notification notif = NotificationFactory.createFailedNotification("Could not abort workflow.");
					portlet.getMainWindow().showNotification(notif);
				} finally {
					updateWkfStatus(wkfInstance, wkfItem);
				}
			}
		}
	}

	/**
	 * Download a file from xfs
	 * 
	 * @param filename
	 *            The filename
	 * @param fullFilePath
	 *            The full filename
	 */
	protected void download(String filename, String fullFilePath) {
		LOGGER.debug(portlet.getUser() + " Trying to download " + fullFilePath);
		StreamSource streamSoucre = getFileSource(fullFilePath);
		StreamResource streamResource;
		try {
			streamResource = new VaadinFileDownloadResource(streamSoucre, filename, portlet.getXfsBridge()
					.getFile(fullFilePath).stat().getSize(), portlet);
			streamResource.setCacheTime(5000); // no cache (<=0) does not work with IE8

			portlet.getMainWindow().open(streamResource, "_top");
		} catch (Exception e) {
			LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
			Notification notif = NotificationFactory.createFailedNotification("Could not start download.");
			portlet.getMainWindow().showNotification(notif);
		}
	}

	/**
	 * Helper method for downloading files
	 */
	private StreamSource getFileSource(final String fullFilePath) {
		StreamSource fileSource = new StreamSource() {
			private static final long serialVersionUID = -4279918185154715594L;

			public InputStream getStream() {
				try {
					return portlet.getXfsBridge().getDownloadStream(fullFilePath);
				} catch (IOException e) {
					LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
				}
				return null;
			}
		};
		return fileSource;
	}

	/**
	 * Opens the desired file
	 */
	protected void openAndShowFile(String filename, String fullFilePath) {
		String fileContent = readFileContent(fullFilePath);

		Panel mainPanel = new Panel();
		mainPanel.setSizeFull();
		mainPanel.setStyleName(Reindeer.PANEL_LIGHT);
		mainPanel.setScrollable(true);
		Label contentLabel = new Label(fileContent, Label.CONTENT_PREFORMATTED);
		contentLabel.setSizeFull();
		mainPanel.addComponent(contentLabel);

		if (isOpenInNewWindow()) {
			showChildWindow(mainPanel, filename);
		} else {
			Tab newTab = contentTabSheet.addTab(mainPanel, filename);
			newTab.setClosable(true);
			contentTabSheet.requestRepaint();
		}
	}

	/**
	 * Opens the desired image file in an embedded component
	 */
	protected void openAndShowImage(String filename, String fullFilePath) {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		File tempImageFile = null;
		try {
			tempImageFile = TempFileHelper.createTempFile(filename);
			in = new BufferedInputStream(portlet.getXfsBridge().getDownloadStream(fullFilePath));
			out = new BufferedOutputStream(new FileOutputStream(tempImageFile));

			byte[] buf = new byte[4096];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (Exception e) {
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
				LOGGER.error(portlet.getUser() + " Could not close filestream", e);
			}
		}
		try {
			FileResource imageFileResource = new FileResource(tempImageFile, portlet);
			Embedded embeddedImage = new Embedded();
			embeddedImage.setType(Embedded.TYPE_IMAGE);
			embeddedImage.setSource(imageFileResource);
			embeddedImage.setSizeFull();

			// wrap in scrollable panel
			Panel mainPanel = new Panel();
			mainPanel.setSizeFull();
			mainPanel.setStyleName(Reindeer.PANEL_LIGHT);
			mainPanel.setScrollable(true);
			mainPanel.addComponent(embeddedImage);

			if (isOpenInNewWindow()) {
				showChildWindow(mainPanel, filename);
			} else {
				Tab newTab = contentTabSheet.addTab(mainPanel, filename);
				newTab.setClosable(true);
			}
		} catch (Exception e) {
			LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
		}
	}

	/**
	 * Reads the content of a file from xfs
	 */
	private String readFileContent(String filePath) {
		final String newLine = "\n";
		StringBuilder contentBuilder = new StringBuilder(500);
		BufferedReader fileReader = null;
		try {
			fileReader = portlet.getXfsBridge().getDownloadStreamReader(filePath);
			String line = null;
			while ((line = fileReader.readLine()) != null) {
				contentBuilder.append(line);
				contentBuilder.append(newLine);
			}

		} catch (IOException e) {
			LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
			Notification notif = NotificationFactory.createFailedNotification("Could not load requested file.");
			portlet.getMainWindow().showNotification(notif);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					LOGGER.info(portlet.getUser() + " Could not close stream.");
				}
			}
		}
		return contentBuilder.toString();
	}

	protected void openWithChemdoodle(String filename, String fullFilePath) {
		LOGGER.debug(portlet.getUser() + " Trying to show " + fullFilePath + " with Chemdoodle.");

		String parentFilePath = fullFilePath.substring(0, fullFilePath.lastIndexOf("/"));

		String jsonFormatedContent = null;
		try {
			// first check if there is a converted json file for this molecule
			boolean isJsonConverted = false;
			DirectoryEntry[] dirEntries = portlet.getXfsBridge().listEntries(parentFilePath);
			String jsonFileName = "." + filename + ".json";
			for (DirectoryEntry entry : dirEntries) {
				if (entry.getName().equals(jsonFileName)) {
					isJsonConverted = true;
					break;
				}
			}
			if (isJsonConverted) {
				LOGGER.trace(portlet.getUser() + " Trying to read " + jsonFileName);
				StringBuilder jsonBuilder = new StringBuilder();
				BufferedReader reader = portlet.getXfsBridge().getDownloadStreamReader(
						parentFilePath + (parentFilePath.endsWith("/") ? "" : "/") + jsonFileName);
				String line = null;
				while ((line = reader.readLine()) != null) {
					jsonBuilder.append(line);
				}
				jsonFormatedContent = jsonBuilder.toString();
			} else {
				// build json file for this molecule
				LOGGER.trace(portlet.getUser() + " Trying to convert " + filename + " to JSON format.");
				InputStream is = portlet.getXfsBridge().getDownloadStream(fullFilePath);
				OutputStream os = portlet.getXfsBridge().getUploadStream(jsonFileName, parentFilePath);

				// set some parsing params
				IJsonConverter converter = JsonConverterFactory.getInstance().create(filename);
				converter.getConverterParameters().setParseAminoAcidAtoms(true);
				converter.getConverterParameters().setParseNonPolymers(true);
				converter.getConverterParameters().setParseWater(true);
				converter.getConverterParameters().setPredictSecStructure(false);

				jsonFormatedContent = converter.convert(is, os);
			}

		} catch (Exception e) {
			LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
			Notification notif = NotificationFactory.createFailedNotification("Could not load requested file.");
			portlet.getMainWindow().showNotification(notif);
		}

		if (jsonFormatedContent != null) {
			LOGGER.trace(portlet.getUser() + " Creating ChemDoodle canvas");
			Panel wrapperPanel = new Panel();
			wrapperPanel.setScrollable(true);
			wrapperPanel.setSizeFull();

			if (isOpenInNewWindow()) {
				PdbViewer molViewer = new PdbViewer();
				molViewer.getCanvas().setMoleculeDefinition(jsonFormatedContent, DefType.JSON);
				wrapperPanel.addComponent(molViewer);

				showChildWindow(wrapperPanel, filename);
			} else {
				BasicViewer molViewer = new BasicViewer();
				molViewer.getCanvas().setMoleculeDefinition(jsonFormatedContent, DefType.JSON);
				wrapperPanel.addComponent(molViewer);

				Tab graphTab = contentTabSheet.addTab(wrapperPanel, filename);
				graphTab.setClosable(true);
			}
		}
	}

	protected void openWithDygraph(String filename, String fullFilePath) {
		LOGGER.debug(portlet.getUser() + " Trying to show " + fullFilePath + " with Dygraph.");
		try {
			IData data = DataConverter.getInstance().convert(filename,
					portlet.getXfsBridge().getDownloadStream(fullFilePath));
			if (data != null) {
				if (isOpenInNewWindow()) {
					SimpleGraphViewer graphViewer = new SimpleGraphViewer();
					graphViewer.getGraph().setData(data);

					Panel wrapperPanel = new Panel();
					wrapperPanel.setStyleName(Reindeer.PANEL_LIGHT);
					wrapperPanel.setScrollable(true);
					wrapperPanel.setSizeFull();
					wrapperPanel.addComponent(graphViewer);
					showChildWindow(wrapperPanel, filename);
				} else {
					VerticalLayout layout = new VerticalLayout();
					layout.setMargin(true);
					layout.setSizeFull();
					SimpleGraph graph = new SimpleGraph(data);
					layout.addComponent(graph);
					// TODO: resolve full-size bug in dygraph
					graph.setWidth("99%");
					graph.setHeight("99%");

					Tab graphTab = contentTabSheet.addTab(layout, filename);
					graphTab.setClosable(true);
				}
			}
		} catch (Exception e) {
			LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
			Notification notif = NotificationFactory.createFailedNotification("Could not load requested file.");
			portlet.getMainWindow().showNotification(notif);
		}
	}

	protected void openWithJMOL(String filename, String fullFilePath) {
		String tempFileName = System.currentTimeMillis() + "_" + filename;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;

		final File file = new File("/usr/local/guseuser/tomcat/webapps/ROOT/html/jmol/jmol/files/" + tempFileName);

		try {
			in = new BufferedInputStream(portlet.getXfsBridge().getDownloadStream(fullFilePath));
			out = new BufferedOutputStream(new FileOutputStream(file));

			byte[] buf = new byte[4096];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(portlet.getUser() + " " + e.getMessage(), e);
		} catch (IOException e) {
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
		Resource source = new ExternalResource("http://mosgrid.de:8080/html/jmol/jmol.html?file=jmol/files/"
				+ tempFileName);
		final JmolEmbedded jmolCanvas = new JmolEmbedded(null, source, file);
		jmolCanvas.setType(Embedded.TYPE_BROWSER);
		jmolCanvas.setSizeFull();

		if (isOpenInNewWindow()) {
			// Open new subwindow with Jmol
			Window w = showChildWindow(jmolCanvas, filename);
			w.addListener(new Window.CloseListener() {
				private static final long serialVersionUID = 2495407479585329726L;

				@Override
				public void windowClose(CloseEvent e) {
					jmolCanvas.deleteAssociatedFile();
				}
			});
		} else {
			Tab jmolTab = contentTabSheet.addTab(jmolCanvas, filename);
			jmolTab.setClosable(true);
			// deleting temp file is handled in tabsheet close listener
		}
	}

	private class JmolEmbedded extends Embedded {
		private static final long serialVersionUID = 3421619029646532609L;
		private File file;

		protected JmolEmbedded(String caption, Resource source, File file) {
			super(caption, source);
			this.file = file;
		}

		public void deleteAssociatedFile() {
			if (file.exists()) {
				if (file.delete()) {
					LOGGER.trace(portlet.getUser() + " Deleted temp file:\n\t" + file);
				} else {
					LOGGER.info(portlet.getUser() + " Could not delete temp file:\n\t" + file);
				}
			}
		}
	}
	
	private static class CustomDirectoryEntry {
		private final String name;
		private final String fullPath;
		private final String displayName;
		private final boolean isDirectory;
		
		/**
		 * @param name
		 * @param fullPath
		 * @param displayName
		 */
		public CustomDirectoryEntry(final String name, final String fullPath, final String displayName, final boolean isDirectory) {
			this.name = name;
			this.fullPath = fullPath;
			this.displayName = displayName;
			this.isDirectory = isDirectory;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the fullPath
		 */
		public String getFullPath() {
			return fullPath;
		}
		/**
		 * @return the displayName
		 */
		public String getDisplayName() {
			return displayName;
		}
		/**
		 * @return the isDirectory
		 */
		public boolean isDirectory() {
			return isDirectory;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CustomDirectoryEntry [name=" + name + ", fullPath="
					+ fullPath + ", displayName=" + displayName + "]";
		}
		
		
		
	}
}
