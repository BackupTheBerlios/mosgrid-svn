package de.mosgrid.gui.xfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import com.vaadin.data.Item;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.portlet.MoSGridPortlet;
import de.mosgrid.util.IconProvider;
import de.mosgrid.util.IconProvider.ICONS;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.XfsBridge;

/**
 * XFS file browser. Consists of a TreeTable and ok + cancel button. Allows registration of FileFilters and
 * Button-Listeners.
 * 
 * @author Andreas Zink
 * 
 */
public class XfsFileBrowser extends CustomComponent {
	/* constants */
	private static final long serialVersionUID = -5586418728219366085L;
	private final Logger LOGGER = LoggerFactory.getLogger(XfsFileBrowser.class);

	private static final ThemeResource ICON_DIR = IconProvider.getIcon(ICONS.FOLDER);
	private static final ThemeResource ICON_FILE = IconProvider.getIcon(ICONS.DOCUMENT);
	private static final ThemeResource ICON_BUTTON_OK = IconProvider.getIcon(ICONS.OK);
	private static final ThemeResource ICON_BUTTON_CANCEL = IconProvider.getIcon(ICONS.CANCEL);

	private static final String CAPTION_BUTTON_OK = "Ok";
	private static final String TOOLTIP_BUTTON_OK = "Commit selection";
	private static final String CAPTION_BUTTON_CANCEL = "Cancel";
	private static final String TOOLTIP_BUTTON_CANCEL = "Cancel selection";

	public enum ItemProperty {
		TYPE, ICON, NAME, PATH, LAST_MODIFIED, SIZE, DATA
	};

	public enum FileType {
		FILE, DIR
	}
	
	private Button okButton;
	private Button cancelButton;

	/* instance variables */
	protected XfsBridge xfsBridge;
	protected VerticalLayout mainLayout;
	protected TreeTable contentTable;
	protected List<IXfsFileFilter> fileSelectFilterList;
	protected List<IXfsBrowserListener> listenerList;
	protected String rootDir;

	/**
	 * @param rootDir
	 *            By default the users XFS home directory is choosen. But browsing can start from any other subdir.
	 */
	public XfsFileBrowser(String rootDir) {
		this.rootDir = rootDir;
		init();
	}

	public XfsFileBrowser() {
		init();
	}

	private void init() {
		fileSelectFilterList = new ArrayList<IXfsFileFilter>();
		listenerList = new ArrayList<IXfsBrowserListener>();
		buildMainLayout();
	}

	/**
	 * Adds a file filter to the browser. If ok button is clicked and selected file is filtered, a warning will be shown
	 * and call is not delegated to listeners.
	 */
	public void addFileSelectFilter(IXfsFileFilter filter) {
		fileSelectFilterList.add(filter);
	}

	/**
	 * Adds a listener for ok + cancel button events
	 */
	public void addButtonListener(IXfsBrowserListener listener) {
		listenerList.add(listener);
	}

	/**
	 * Removes a listener for ok + cancel button events
	 */
	public void removeButtonListener(IXfsBrowserListener listener) {
		listenerList.remove(listener);
	}

	/**
	 * @param itemID
	 * @return The item with given ID or null
	 */
	public Item getItem(Object itemID) {
		return contentTable.getItem(itemID);
	}

	@Override
	public void attach() {
		super.attach();
			// get xfs bridge (getApplication only works after attach)
			this.xfsBridge = ((MoSGridPortlet) getApplication()).getXfsBridge();
			fillContentTable();
			
			if(contentTable.getItemIds().isEmpty()){
				Notification notif = NotificationFactory.createNormalNotification("Sorry", "No files could be found");
				getWindow().showNotification(notif);
				okButton.setEnabled(false);
			}
	}
	

	@Override
	public void detach() {
		super.detach();
		if(!okButton.isEnabled()){
			okButton.setEnabled(true);
		}
		contentTable.removeAllItems();
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setSizeFull();
		mainLayout.setSpacing(true);

		buildContentTable();
		buildButtonFooter();
		setCompositionRoot(mainLayout);
	}

	private void buildContentTable() {
		contentTable = new TreeTable();
		contentTable.setWidth(44f, UNITS_EM);
		contentTable.setHeight(22f, UNITS_EM);

		contentTable.addContainerProperty(ItemProperty.TYPE, FileType.class, null, null, null, null);
		contentTable.addContainerProperty(ItemProperty.ICON, ThemeResource.class, null, null, null, null);
		contentTable.addContainerProperty(ItemProperty.NAME, String.class, "", "Name", null, null);
		contentTable.addContainerProperty(ItemProperty.PATH, String.class, "", null, null, null);
		contentTable.addContainerProperty(ItemProperty.SIZE, String.class, "", "Size", null, null);
		contentTable.addContainerProperty(ItemProperty.LAST_MODIFIED, String.class, "", "Last Modified", null, null);
		contentTable.addContainerProperty(ItemProperty.DATA, DirectoryEntry.class, null, null, null, null);

		Object[] visColumns = new Object[] { ItemProperty.NAME, ItemProperty.SIZE, ItemProperty.LAST_MODIFIED };
		contentTable.setVisibleColumns(visColumns);
		contentTable.setColumnExpandRatio(ItemProperty.NAME, 0.6f);
		contentTable.setColumnExpandRatio(ItemProperty.SIZE, 0.17f);
		contentTable.setColumnExpandRatio(ItemProperty.LAST_MODIFIED, 0.23f);
		contentTable.setItemIconPropertyId(ItemProperty.ICON);
		contentTable.setSelectable(true);

		// lazy loading of child elements
		contentTable.addListener(new ExpandListener() {
			private static final long serialVersionUID = -2994291607451119662L;

			@Override
			public void nodeExpand(ExpandEvent event) {
				try {
					loadChildItems(event.getItemId());
				} catch (IOException e) {
					String msg = "Error while retrieving content form XTreemFS!";
					Notification notif = NotificationFactory.createErrorNotification("XTreemFS Error!", msg);
					getWindow().showNotification(notif);
					LOGGER.error(msg, e);
				}
			}
		});

		mainLayout.addComponent(contentTable);
	}

	private void buildButtonFooter() {
		HorizontalLayout buttonFooter = new HorizontalLayout();
		buttonFooter.setSpacing(true);

		okButton = new Button(CAPTION_BUTTON_OK);
		okButton.setStyleName("small");
		okButton.setIcon(ICON_BUTTON_OK);
		okButton.setDescription(TOOLTIP_BUTTON_OK);
		okButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -8275444879400515098L;

			@Override
			public void buttonClick(ClickEvent event) {
				clickedOkButton();
			}

		});
		buttonFooter.addComponent(okButton);
		buttonFooter.setComponentAlignment(okButton, Alignment.BOTTOM_RIGHT);

		cancelButton = new Button(CAPTION_BUTTON_CANCEL);
		cancelButton.setStyleName("small");
		cancelButton.setIcon(ICON_BUTTON_CANCEL);
		cancelButton.setDescription(TOOLTIP_BUTTON_CANCEL);
		cancelButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7906994204687616498L;

			@Override
			public void buttonClick(ClickEvent event) {
				clickedCancelButton();
			}
		});
		buttonFooter.addComponent(cancelButton);
		buttonFooter.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);

		mainLayout.addComponent(buttonFooter);
		mainLayout.setComponentAlignment(buttonFooter, Alignment.BOTTOM_RIGHT);
	}

	/**
	 * Called when ok Button is clicked. Calls all listeners if selected file passes all file-filters. Shows
	 * notification otherwise.
	 */
	protected void clickedOkButton() {
		Object currentItemID = contentTable.getValue();
		if (currentItemID != null) {
			Item currentItem = contentTable.getItem(currentItemID);
			if (currentItem != null) {
				String fullFilePath = (String) currentItem.getItemProperty(ItemProperty.PATH).getValue();
				FileType fileType = (FileType) currentItem.getItemProperty(ItemProperty.TYPE).getValue();
				DirectoryEntry dirEntry = (DirectoryEntry) currentItem.getItemProperty(ItemProperty.DATA).getValue();
				// check if file passes all filters
				boolean isAccepted = true;
				for (IXfsFileFilter filter : fileSelectFilterList) {
					isAccepted &= filter.acceptFile(currentItemID, dirEntry, fullFilePath, fileType);
				}
				if (isAccepted) {
					clickedOkButtonNotification(currentItemID, dirEntry, fullFilePath, fileType);
				} else {
					// create a notification which tells the user what selections are allowed
					final String LIST_TAG = "<LI>";
					StringBuilder notifBuilder = new StringBuilder();
					notifBuilder.append("<UL>");
					for (IXfsFileFilter filter : fileSelectFilterList) {
						notifBuilder.append(LIST_TAG + filter.getPattern());
					}
					notifBuilder.append("</UL>");

					Notification notif = NotificationFactory.createWarningNotification("Invalid!",
							"Please change your selection:<br>" + notifBuilder.toString());
					getWindow().showNotification(notif);
				}
			}
		}
	}

	/**
	 * Notifies all listeners after ok button was clicked. Can be overridden.
	 * 
	 * @param currentItemID
	 *            The id of selected item
	 * @param dirEntry
	 *            The selected xfs dir entry
	 * @param fullFilePath
	 *            Full path to selected file
	 * @param fileType
	 *            Type of selected file
	 */
	protected void clickedOkButtonNotification(Object currentItemID, DirectoryEntry dirEntry, String fullFilePath,
			FileType fileType) {
		for (IXfsBrowserListener listener : listenerList) {
			listener.clickedFileBrowsersOkButton(currentItemID, dirEntry, fullFilePath, fileType);
		}
	}

	/**
	 * Called when cancel Button is clicked. Calls all listeners.
	 */
	protected void clickedCancelButton() {
		for (IXfsBrowserListener listener : listenerList) {
			listener.clickedFileBrowserCancelButton();
		}
	}

	/**
	 * Fills the content table by browsing xfs file storage
	 */
	protected void fillContentTable() {
		if (rootDir == null) {
			rootDir = xfsBridge.getHomeDir();
		}

		try {
			for (DirectoryEntry dirEntry : xfsBridge.listEntries(rootDir)) {
				addItemToContentTable(dirEntry, rootDir, null);
			}
		} catch (Exception e) {
			String msg = "Error while retrieving content form XTreemFS!";
			Notification notif = NotificationFactory.createErrorNotification("XTreemFS Error!", msg);
			getWindow().showNotification(notif);
			LOGGER.error(msg, e);
		}
	}

	public void loadPaths(List<String> paths) {
		try {
			contentTable.removeAllItems();
			for (String path : paths) {
				addItemToContentTable(path, path);
			}
		} catch (Exception e) {
			String msg = "Error while retrieving content form XTreemFS!";
			Notification notif = NotificationFactory.createErrorNotification("XTreemFS Error!", msg);
			getWindow().showNotification(notif);
			LOGGER.error(msg, e);
		}
	}

	/**
	 * Lazy loading of child items on expand
	 */
	protected void loadChildItems(Object parentItemID) throws IOException {
		Item parentItem = contentTable.getItem(parentItemID);
		// only load if item has no children yet
		if (parentItem != null && !contentTable.hasChildren(parentItemID)) {
			String xfsFilePath = (String) parentItem.getItemProperty(ItemProperty.PATH).getValue();
			for (DirectoryEntry dirEntry : xfsBridge.listEntries(xfsFilePath)) {
				addItemToContentTable(dirEntry, xfsFilePath, parentItemID);
			}
		}
	}

	/**
	 * Adds an item to the table
	 * 
	 * @param filename
	 *            The filename of file item to be added
	 * @param parentPath
	 *            Parent dir path
	 * @param parentID
	 *            Parent item id
	 * @return The item id of the newly added item
	 */
	protected Object addItemToContentTable(DirectoryEntry dirEntry, String parentPath, Object parentID) {
		String filename = dirEntry.getName();
		String fullFilePath = parentPath + "/" + filename;
		if (!filename.startsWith(".")) {
			try {
				// add item to table
				Item tableItem = contentTable.addItem(fullFilePath);
				if (parentID != null) {
					contentTable.setParent(fullFilePath, parentID);
				}
				// set type and icon
				boolean isDir = xfsBridge.isDirectory(dirEntry);
				if (isDir) {
					contentTable.setChildrenAllowed(fullFilePath, true);
					tableItem.getItemProperty(ItemProperty.TYPE).setValue(FileType.DIR);
					tableItem.getItemProperty(ItemProperty.ICON).setValue(ICON_DIR);
				} else {
					contentTable.setChildrenAllowed(fullFilePath, false);
					tableItem.getItemProperty(ItemProperty.TYPE).setValue(FileType.FILE);
					tableItem.getItemProperty(ItemProperty.ICON).setValue(ICON_FILE);
				}
				// set name, path, last modified
				tableItem.getItemProperty(ItemProperty.NAME).setValue(filename);
				tableItem.getItemProperty(ItemProperty.PATH).setValue(fullFilePath);
				tableItem.getItemProperty(ItemProperty.SIZE).setValue(xfsBridge.getSize(dirEntry));
				tableItem.getItemProperty(ItemProperty.LAST_MODIFIED).setValue(xfsBridge.getLastModified(dirEntry));
				tableItem.getItemProperty(ItemProperty.DATA).setValue(dirEntry);

			} catch (Exception e) {
				String msg = "Error while retrieving content form XTreemFS!";
				Notification notif = NotificationFactory.createErrorNotification("XTreemFS Error!", msg);
				getWindow().showNotification(notif);
				LOGGER.error(msg, e);
			}
		}
		return fullFilePath;
	}

	protected Object addItemToContentTable(String dirName, String fullFilePath) {
		try {
			// add item to table
			Item tableItem = contentTable.addItem(fullFilePath);

			contentTable.setChildrenAllowed(fullFilePath, true);
			tableItem.getItemProperty(ItemProperty.TYPE).setValue(FileType.DIR);
			tableItem.getItemProperty(ItemProperty.ICON).setValue(ICON_DIR);

			// set name, path, last modified
			tableItem.getItemProperty(ItemProperty.NAME).setValue(dirName);
			tableItem.getItemProperty(ItemProperty.PATH).setValue(fullFilePath);

		} catch (Exception e) {
			String msg = "Error while retrieving content form XTreemFS!";
			Notification notif = NotificationFactory.createErrorNotification("XTreemFS Error!", msg);
			getWindow().showNotification(notif);
			LOGGER.error(msg, e);
		}

		return fullFilePath;
	}
}
