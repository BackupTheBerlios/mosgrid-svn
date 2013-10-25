package org.xtreemfs.portlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.xtreemfs.common.clients.File;
import org.xtreemfs.foundation.logging.Logging;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.Stat;
import org.xtreemfs.portlet.ui.AccessRightsDialog;
import org.xtreemfs.portlet.ui.Autocomplete;
import org.xtreemfs.portlet.ui.CircularByteBuffer;
import org.xtreemfs.portlet.ui.FileFieldFactory;
import org.xtreemfs.portlet.ui.FileItem;
import org.xtreemfs.portlet.ui.TextInputDialog;
import org.xtreemfs.portlet.ui.UploadDialog;
import org.xtreemfs.portlet.ui.Utils;
import org.xtreemfs.portlet.ui.YesNoDialog;
import org.xtreemfs.portlet.util.BufferedFileOutputStream;
import org.xtreemfs.portlet.util.XtreemFSConnect;
import org.xtreemfs.portlet.util.vaadin.VaadinFileDownloadResource;
import org.xtreemfs.portlet.util.vaadin.VaadinXtreemFSSession;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.AbstractSelect.TargetItemIs;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;


/**
 *
 * TODO mehrer Dateien selektieren und dann alle Löschen
 * TODO Verzeichnisse löschen
 * TODO update gui when leaving a popup (disable icons, etc.)
 *
 * TODO download as zip geht nicht bei großen Verzeichnissen?
 *
 * TODO Extend Application for XtreemFS Integration
 *
 * @author ps
 *
 */
public class Xtreemfs_portletApplication
extends XtreemFSApplication
implements ItemClickListener, DropHandler,  Handler {

  private static final long serialVersionUID = -3515439615517450125L;

  static {
    Logging.start(Logging.LEVEL_INFO);
  }

  public static final boolean DEBUG = false;

  public static final String ICON_DOWNLOAD = "images/download-icon.png";
  public static final String ICON_UPLOAD = "images/upload-icon.png";
  public static final String ICON_RENAME = "images/rename32.png";
  public static final String ICON_ADD_DIR = "../runo/icons/32/folder-add.png";
  public static final String ICON_ACL = "../runo/icons/32/settings.png";
  public static final String ICON_DELETE = "../runo/icons/32/trash.png";
  public static final String ICON_DOCUMENT = "../runo/icons/32/document-txt.png";
  public static final String ICON_FOLDER = "../runo/icons/32/folder.png";

  // Layout-Elemente
  private HorizontalLayout toolbar = new HorizontalLayout();

  private Button removalButton;
  private Button downloadButton;
  private Button renameButton;
  private Button aclButton;

  private Form fileDetails = new Form();

  // Upload
  private Upload upload;

  // Upload Status
  private UploadDialog subwindow;

  // Zertifikat fehlt
  private Window missingCertWindow;

  // Tabelle
  private Table table = new Table();
  private Autocomplete autocompleteFilter = new Autocomplete("Filter: ");

  private static final String LABEL_ACCESS_MODES = "Permissions";
  private static final String LABEL_DATUM = "Date";
  private static final String LABEL_GROESSE = "Size";
  private static final String LABEL_DATEINAME = "Filename";
  private static final String LABEL_ICON = "Symbol";
  private static String[] fields = { LABEL_ICON, LABEL_DATEINAME, LABEL_GROESSE, LABEL_DATUM, LABEL_ACCESS_MODES};

  // Aktionen für das Kontextmenü
  static final Action ACTION_UPLOAD = new Action("Upload");
  static final Action ACTION_DOWNLOAD = new Action("Download");
  static final Action ACTION_DOWNLOAD_ZIP = new Action("Download as .zip");
  static final Action ACTION_RENAME = new Action("Rename");
  static final Action ACTION_REMOVE = new Action("Delete");
  static final Action ACTION_PROPERTIES = new Action("Change access rights");

  static final Action[] ACTIONS_DIRECTORY = new Action[] {
    ACTION_DOWNLOAD_ZIP, /*ACTION_UPLOAD,*/ ACTION_RENAME, ACTION_REMOVE, ACTION_PROPERTIES };
  static final Action[] ACTIONS_FILE = new Action[] {
    ACTION_DOWNLOAD, ACTION_RENAME, ACTION_REMOVE, ACTION_PROPERTIES };



  private static final String PREV_DIRECTORY = "..";
  private static final String CURRENT_DIRECTORY = ".";

  /**
   * Diese Methode wird beim Start des Portlets aufgerufen
   * Initialisiert das Portlet.
   */
  @Override
  public void init() {
    super.init();

    setTheme("xtreemfs");

    initLayout();
    initTable();
    initFileDetails();
    initButtons();
    initFilteringControls();
    initUploadState();
  }


  @Override
  public void start(
      URL applicationUrl,
      Properties applicationProperties,
      ApplicationContext context) {
    super.start(applicationUrl, applicationProperties, context);
  }


  @Override
  public void handleRenderRequest(
      RenderRequest request,
      RenderResponse response,
      Window window) {
    try {
      super.handleRenderRequest(request, response, window);

      if (this.missingCertWindow != null) {
        getMainWindow().getWindow().removeWindow(this.missingCertWindow);
      }

      // Dateien laden
      loadXtreemFSData();

      // Activate all Controlls
      this.toolbar.setEnabled(true);
    } catch (Exception e) {
      this.toolbar.setEnabled(false); // deactivate all controlls
      showNotification("Error", e.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE, null);

      // Im Fehlerfall Fenster anzeigen mit Fehlermeldung
      openErrorOnMissingCertificate();
    }
  }


  @Override
  public void handleResourceRequest(ResourceRequest request,
      ResourceResponse response, Window window) {
    request.setAttribute("org.apache.tomcat.sendfile.support", Boolean.TRUE);
    //    System.out.println("handleResourceRequest");
  }

  /**
   * Layout-Manager initialisieren
   */
  public void initLayout() {
    VerticalLayout left = new VerticalLayout();
    setMainWindow(new Window("XtreemFS", left));
    left.setSizeFull();

    this.toolbar.setMargin(true);
    this.toolbar.setSpacing(true);
    this.toolbar.setStyleName("toolbar");
    this.toolbar.setWidth("100%");
    this.toolbar.setHeight("90px");

    left.addComponent(this.toolbar);

    HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
    left.addComponent(splitPanel);
    splitPanel.setFirstComponent(this.table);
    splitPanel.setSecondComponent(this.fileDetails);
    splitPanel.setSplitPosition(70, HorizontalSplitPanel.UNITS_PERCENTAGE);

    left.setExpandRatio(splitPanel, 1);

    left.setHeight("500px");
  }

  /**
   * Felder für die Details zu einer Datei initialisieren
   */
  private void initFileDetails() {
    this.fileDetails.setSizeFull();
    this.fileDetails.getLayout().setMargin(true);
    this.fileDetails.setImmediate(false);

    this.fileDetails.setCaption("Details");
    this.fileDetails.setWriteThrough(false); // we want explicit 'apply'
    this.fileDetails.setInvalidCommitted(false); // no invalid values in datamodel

    this.fileDetails.setFormFieldFactory(new FileFieldFactory(null));

    this.fileDetails.setReadOnly(true);
  }

  /**
   * Tabelle mit Dateilisting initialisieren
   */
  public void initTable() {
    this.table.setSelectable(true);
    this.table.setImmediate(true);
    this.table.setWidth("100%");
    this.table.addListener(this);
    this.table.setDropHandler(this);
    this.table.addActionHandler(this);
    this.table.setDragMode(TableDragMode.ROW);
    this.table.setSizeFull();

    this.table.setColumnCollapsingAllowed(true);
    //    table.setColumnReorderingAllowed(true);

    // BReiten definieren
    this.table.setColumnExpandRatio(LABEL_ICON, 1);
    this.table.setColumnExpandRatio(LABEL_DATEINAME, 3);
    this.table.setColumnExpandRatio(LABEL_DATUM, 2);
    this.table.setColumnExpandRatio(LABEL_GROESSE, 1);
    this.table.setColumnExpandRatio(LABEL_ACCESS_MODES, 1);
    this.table.setColumnHeader(LABEL_ICON, "");
  }


  public void initUploadState () {
    // Subwindow
    this.subwindow = new UploadDialog(this.upload);

    // ...and make it modal
    this.subwindow.setModal(true);
    this.subwindow.setClosable(false);
  }

  /**
   * Das Suchfeld über der Liste wird initialisiert
   */
  @SuppressWarnings("serial")
  private void initFilteringControls() {
    // for (final String pn : fields) {
    final String pn = LABEL_DATEINAME;

    this.autocompleteFilter.setInputPrompt("Filter files...");

    this.autocompleteFilter.setItemCaptionPropertyId(LABEL_DATEINAME);
    this.autocompleteFilter.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

    this.autocompleteFilter.setImmediate(true);
    this.autocompleteFilter.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
    this.autocompleteFilter.setInvalidAllowed(true);
    this.autocompleteFilter.setNewItemsAllowed(true);
    this.autocompleteFilter.setNullSelectionAllowed(true);

    // Feld in das Layout einfügen
    this.toolbar.addComponent(this.autocompleteFilter);
    this.toolbar.setComponentAlignment(this.autocompleteFilter, Alignment.MIDDLE_RIGHT);
    this.toolbar.setExpandRatio(this.autocompleteFilter, 1);

    // Listener für die Verarbeitung von Suchen
    this.autocompleteFilter.addListener(new Property.ValueChangeListener() {
      public void valueChange(ValueChangeEvent event) {
        IndexedContainer content = (IndexedContainer) Xtreemfs_portletApplication.this.table.getContainerDataSource();
        content.removeContainerFilters(pn);
        String value = (String)Xtreemfs_portletApplication.this.autocompleteFilter.getValue();
        if (value != null && value.length() > 0) {
          content.addContainerFilter(pn, value, true, false);
        }
        showNotification("Filter", "" + content.size() + " files found.", Notification.TYPE_TRAY_NOTIFICATION, null);
      }
    });
  }



  @SuppressWarnings("serial")
  private void initButtons() {
    this.downloadButton = new Button("Download", new Button.ClickListener() {
      public void buttonClick(ClickEvent event) {
        try {
          downloadFile(getFilename(null));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    this.downloadButton.setIcon(new ThemeResource(ICON_DOWNLOAD));
    this.downloadButton.setDescription("Download File");
    this.downloadButton.setEnabled(false);
    this.toolbar.addComponent(this.downloadButton);

    // Upload-Button
    this.upload = new Upload("", new Upload.Receiver() {
      public OutputStream receiveUpload(String filename, String MIMEType) {
        return uploadFile(filename);
      }
    });
    this.upload.setImmediate(true);
    this.upload.setIcon(new ThemeResource(ICON_UPLOAD));
    this.upload.setDescription("Upload File");
    this.upload.setButtonCaption("Upload File");

    // Upload nur in Unterverzeichnis erlauben
    String dir = getCurrentDir();
    this.upload.setEnabled(dir!=null && (!dir.equals("") && !dir.equals("/")));

    this.upload.addListener(new Upload.FinishedListener() {
      public void uploadFinished(FinishedEvent event) {
        Xtreemfs_portletApplication.this.subwindow.state.setValue("Uploaded");
        Xtreemfs_portletApplication.this.subwindow.pi.setVisible(false);
        Xtreemfs_portletApplication.this.subwindow.cancelProcessing.setVisible(false);
        (Xtreemfs_portletApplication.this.subwindow.getParent()).removeWindow(Xtreemfs_portletApplication.this.subwindow);
      }
    });
    this.upload.addListener(new Upload.SucceededListener() {
      public void uploadSucceeded(SucceededEvent event) {
        uploadFileFinished(event);
      }
    });
    this.upload.addListener(new Upload.FailedListener() {
      public void uploadFailed(FailedEvent event) {
        Xtreemfs_portletApplication.this.subwindow.state.setValue("Failed");
      }
    });

    this.upload.addListener(new Upload.StartedListener() {
      public void uploadStarted(StartedEvent event) {
        // Verlaufsfenster anzeigen
        getMainWindow().getWindow().addWindow(Xtreemfs_portletApplication.this.subwindow);

        Xtreemfs_portletApplication.this.subwindow.pi.setValue(0f);
        Xtreemfs_portletApplication.this.subwindow.pi.setVisible(true);
        Xtreemfs_portletApplication.this.subwindow.pi.setPollingInterval(500); // hit server frequently
        Xtreemfs_portletApplication.this.subwindow.textualProgress.setVisible(true);

        // updates to client
        Xtreemfs_portletApplication.this.subwindow.state.setValue("Uploading");
        Xtreemfs_portletApplication.this.subwindow.fileName.setValue(event.getFilename());

        Xtreemfs_portletApplication.this.subwindow.cancelProcessing.setVisible(true);
      }
    });

    this.upload.addListener(new Upload.ProgressListener() {
      public void updateProgress(long readBytes, long contentLength) {
        // this method gets called several times during the update
        Xtreemfs_portletApplication.this.subwindow.pi.setValue(new Float(readBytes / (float) contentLength));
        Xtreemfs_portletApplication.this.subwindow.textualProgress.setValue(+ readBytes/1024 + " out of " + contentLength/1024 + "KiB uploaded.");
      }

    });

    this.toolbar.addComponent(this.upload);
    //this.toolbar.setComponentAlignment(this.upload, Alignment.BOTTOM_LEFT);

    // New item button
    Button addButton = new Button("Create", new Button.ClickListener() {
      public void buttonClick(ClickEvent event) {
        createNewDirectory();
      }
    });
    addButton.setIcon(new ThemeResource(ICON_ADD_DIR));
    addButton.setDescription("Create Directory");
    this.toolbar.addComponent(addButton);

    // New rename button
    this.renameButton = new Button("Rename", new Button.ClickListener() {
      public void buttonClick(ClickEvent event) {
        renameFile(getFilename(null));
      }
    });
    this.renameButton.setIcon(new ThemeResource(ICON_RENAME));
    this.renameButton.setDescription("Rename");
    this.renameButton.setEnabled(false);
    this.toolbar.addComponent(this.renameButton);

    // Change the access rights
    this.aclButton = new Button("Permissions", new Button.ClickListener() {
      public void buttonClick(ClickEvent event) {
        setAccessRightsForFile(getFilename(null));
      }
    });
    this.aclButton.setIcon(new ThemeResource(ICON_ACL));
    this.aclButton.setDescription("Permissions");
    this.aclButton.setEnabled(false);
    this.toolbar.addComponent(this.aclButton);

    // Remove item button
    this.removalButton = new Button("Remove", new Button.ClickListener() {
      public void buttonClick(ClickEvent event) {
        removeFile(getFilename(null));
      }
    });
    this.removalButton.setIcon(new ThemeResource(ICON_DELETE));
    this.removalButton.setEnabled(false);
    this.removalButton.setDescription("Remove");
    this.toolbar.addComponent(this.removalButton);

  }

  /**
   * Prüft, ob es sich um eine Datei handelt
   * ( und nicht um ein Verzeichnis )
   */
  protected static boolean isValidFile(String fileName) {
    return fileName != null
        && isSelectable(fileName)
        && !fileName.equals(CURRENT_DIRECTORY);
  }

  /**
   * Prüft, ob es sich um eine Datei handelt
   * ( und nicht um ein Verzeichnis )
   */
  protected static boolean isSelectable(String fileName) {
    return fileName != null
        && !fileName.equals("")
        && !fileName.equals(PREV_DIRECTORY)
        && !fileName.equals("/");
  }

  @Override
  public void itemClick(ItemClickEvent event) {
    String label = getFilename(event);
    final File requestedFile = getFile(label);

    // Details zur aktuellen Datei anzeigen
    this.fileDetails.setItemDataSource(new BeanItem<FileItem>(new FileItem(requestedFile)));

    // muss nach setItemDataSource stehen!
    this.fileDetails.setVisibleItemProperties(Arrays.asList(new String[] {
        FileItem.LABEL_FILENAME,
        FileItem.LABEL_FILESIZE,
        FileItem.LABEL_FILETYPE,
        FileItem.LABEL_FILEDATE,
        FileItem.LABEL_FILEOWNER,
        FileItem.LABEL_FILEREPLICAS}));

    boolean changeDirectory = false;

    // Doppelklick für Aktionen abfangen
    if (event.isDoubleClick()) {
      try {
        // Vorheriges Verzeichnis
        if (PREV_DIRECTORY.equals(label)) {
          if (VaadinXtreemFSSession.getCurrentDir().lastIndexOf("/") >= 0) {
            VaadinXtreemFSSession.setCurrentDir(
                VaadinXtreemFSSession.getCurrentDir().substring(0, VaadinXtreemFSSession.getCurrentDir().lastIndexOf("/")));
          }
          loadXtreemFSData();
          changeDirectory = true;
        }
        else if (CURRENT_DIRECTORY.equals(label))  {
          // do nothing
        }
        else if (requestedFile.exists()) {
          // Verzeichnis öffnen/auslesens
          if (requestedFile.isDirectory()) {
            VaadinXtreemFSSession.setCurrentDir(VaadinXtreemFSSession.getCurrentDir()+"/"+label);
            loadXtreemFSData();
            changeDirectory = true;
          }
          // Datei auslesen und in Fenster darstellen
          else if (requestedFile.isFile()) {
            downloadFile(label);
          }
        }
      } catch (IOException e) {
        showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
      }
    }

    try {
      // Download-Button aktivieren/deaktivieren
      this.downloadButton.setEnabled(requestedFile != null && requestedFile.isFile() && !changeDirectory);
      this.removalButton.setEnabled(requestedFile != null && isValidFile(label) && !changeDirectory);
      this.renameButton.setEnabled(requestedFile != null && isValidFile(label) && !changeDirectory);
      this.aclButton.setEnabled(requestedFile != null && isSelectable(label) && !changeDirectory);

      // Upload nur in Unterverzeichnis erlauben
      String dir = getCurrentDir();
      this.upload.setEnabled(dir!=null && !dir.equals("") && !dir.equals("/"));

    } catch (IOException e) {
      showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
    }


  }


  /**
   * Fehlermeldung ausgeben, falls Zertifikat nicht gefunden werden kann
   * oder ungültig ist.
   */
  protected void openErrorOnMissingCertificate () {
    if (this.missingCertWindow != null) {
      getMainWindow().getWindow().removeWindow(this.missingCertWindow);
    }

    this.missingCertWindow = new Window( "A valid SAML-assertion could not be found. ");
    this.missingCertWindow.setWidth("350px");

    Label plainText = new Label("To access the grid file system, please make sure that a valid SAML-assertion is uploaded to the portal.");
    plainText.setContentMode(Label.CONTENT_TEXT);
    this.missingCertWindow.addComponent(plainText);
    this.missingCertWindow.center();

    getMainWindow().getWindow().addWindow(this.missingCertWindow);

    //    Notification notification = new Notification(
    //        "A valid proxy-certificate could not be found. ",
    //        "To access the grid file system, please make sure that a valid proxy-certificate is uploaded to the portal.",
    //        Notification.TYPE_TRAY_NOTIFICATION);
    //    notification.setDelayMsec(-1);
    //    notification.setPosition(Notification.POSITION_CENTERED);
    //    getMainWindow().showNotification(notification);
  }

  /**
   * Liefert die Datei als Stream zur Weiterverarbeitung
   */
  private StreamSource getFileSource(final File requestedFile) {
    StreamSource fileSource = new StreamSource() {
      private static final long serialVersionUID = 3275933556191707490L;
      public InputStream getStream() {
        try {
          return new org.xtreemfs.portlet.util.BufferedFileInputStream(requestedFile);
        } catch (IOException e) {
          showNotification("Error", e.getMessage(), Notification.TYPE_ERROR_MESSAGE, e);
        }
        return null;
      }
    };
    return fileSource;
  }

  /**
   * Packt ein Verzeichnis als ZIP und liefert es als Stream zur Weiterverarbeitung.
   */
  private StreamSource getZipSource(final File requestedDirectory) {
    StreamSource zippedDirSource = new StreamSource() {
      private static final long serialVersionUID = 7060525220398731933L;
      public InputStream getStream() {
        // Pipe bauen, um Steam als Download umzulenken
        final CircularByteBuffer cbb = new CircularByteBuffer(1024);
        new Thread(
            new Runnable(){
              public void run(){
                ZipOutputStream zos = new ZipOutputStream(cbb.getOutputStream());
                Utils.zipDir(getVolume(), requestedDirectory.getPath(), zos);

                try {
                  if (zos != null)  {
                    zos.flush();
                    zos.close();
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }
            ).start();

        return cbb.getInputStream();
      }
    };
    return zippedDirSource;
  }

  /**
   * Meldungen für den Anwender ausgeben
   */
  public void showNotification (String caption, String description, int type, Exception e) {
    if (DEBUG) {
      Notification n = new Notification(caption, description, type);
      n.setPosition(Notification.POSITION_BOTTOM_RIGHT);
      n.setDelayMsec(5000); // sec->msec
      getMainWindow().showNotification(n);
    }
    if (e != null) {
      Logging.logMessage(Logging.LEVEL_WARN, this,"%s, %s, %s",caption, description, e);
      e.printStackTrace();
    }
  }

  /**
   * Drag und Drop Behandlung für die Tabelle
   */
  @Override
  public void drop(DragAndDropEvent dropEvent) {
    DataBoundTransferable t = (DataBoundTransferable) dropEvent.getTransferable();

    // Quelle
    Container sourceContainer = t.getSourceContainer();
    Object sourceItemId = t.getItemId();
    Item sourceItem = sourceContainer.getItem(sourceItemId);
    String sourceName = sourceItem.getItemProperty(LABEL_DATEINAME).toString();

    // Ziel
    AbstractSelectTargetDetails dropData = ((AbstractSelectTargetDetails) dropEvent.getTargetDetails());
    Object targetItemId = dropData.getItemIdOver();
    Item targetItem = sourceContainer.getItem(targetItemId);
    String targetName = targetItem.getItemProperty(LABEL_DATEINAME).toString();

    // Verschieben initiieren
    final File sourceFile = getFile(sourceName);
    final File targetFolder = getFile(targetName);
    try {
      if (sourceFile != null
          && sourceFile.exists()
          && !sourceFile.isDirectory()
          && targetFolder != null
          && targetFolder.isDirectory()
          ) {
        File targetFile = getFile(targetName + "/" + sourceName);
        sourceFile.renameTo(targetFile);

        this.table.removeItem(sourceItem);
        this.table.select(null);

        // Update ausführen
        loadXtreemFSData();
        showNotification("Completed", "File "+ sourceName +" moved to directory " + targetName, Notification.TYPE_TRAY_NOTIFICATION, null);
      }
      else {
        showNotification("Error", "Directories can't be moved in current version.", Notification.TYPE_WARNING_MESSAGE, null);
      }
    } catch (IOException e) {
      if (e.getMessage().contains("access denied")) {
        showNotification("Error", "You don't have the rights to access this directory/file.", Notification.TYPE_ERROR_MESSAGE, e);
      }
      else {
        showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
      }
    }

  }


  /**
   * Prüft, ob das Zielobjekt ein Verzeichnis ist
   */
  @Override
  public AcceptCriterion getAcceptCriterion() {
    return new TargetItemIs(this.table, VaadinXtreemFSSession.getDirectoryIdsAsArray());
  }

  /**
   * Bestimmt die wählbaren Aktionen für das Kontextmenü.
   * Ein Verzeichnis kann z.B. nicht heruntergeladen werden.
   */
  @Override
  public Action[] getActions(Object target, Object sender) {
    if (VaadinXtreemFSSession.getDirectoryIds().contains(target)) {
      return ACTIONS_DIRECTORY;
    }
    else {
      return ACTIONS_FILE;
    }
  }

  /**
   * Verarbeitet die Aktionen zum Kontextmenü.
   */
  @Override
  public void handleAction(Action action, Object sender, Object target) {
    // Zuerst selektieren, damit Wert genutzt werden kann
    this.table.setValue(target);

    String fileName = getFilename(null);

    if (isSelectable(fileName)) {
      // ACL setzen für eine Datei
      if (ACTION_PROPERTIES.equals(action)) {
        setAccessRightsForFile(fileName);
      }
    }

    if (isValidFile(fileName)) {
      if (ACTION_UPLOAD.equals(action)) {
        // upload kann leider nicht aus dem Menü gestartet werden.
      }
      // Download einer Datei
      else if (ACTION_DOWNLOAD.equals(action)) {
        try {
          downloadFile(fileName);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      // Download eines Verzeichnisses als gepackte Datei (.zip)
      else if (ACTION_DOWNLOAD_ZIP.equals(action)) {
        downloadZippedDirectory(fileName);
      }
      // Umbenennen einer Datei
      else if (ACTION_RENAME.equals(action)) {
        renameFile(fileName);
      }
      // Entfernen einer Datei
      else if (ACTION_REMOVE.equals(action)) {
        removeFile(fileName);
      }
    }

  }

  /**
   * Liefert die Datei mit dem Namen 'fileName' aus dem aktuellen Verzeichnis.
   */
  private File getFile(String fileName) {
    String path = "";
    if (fileName.equals(PREV_DIRECTORY)) {
      if (getCurrentDir().lastIndexOf("/") >= 0) {
        path = getCurrentDir().substring(0, getCurrentDir().lastIndexOf("/"));
        if (fileName.length() > PREV_DIRECTORY.length()) {
          path += fileName.substring(2, fileName.length());
        }
      }
      else {
        path += fileName.substring(2, fileName.length());
      }
    }
    else if (fileName.equals(CURRENT_DIRECTORY)) {
      path = getCurrentDir();
    }
    else {
      path = getCurrentDir()+"/"+fileName;
    }

    // Datei laden
    return getFile(path, getCredentials());
  }

  /**
   * Liefert den Dateinamen der gewählten Datei in der List
   */
  private String getFilename(ItemClickEvent event) {
    Object index = event != null? event.getItemId() : this.table.getValue();
    Item selectedItem = this.table.getItem(index);
    if (selectedItem != null) {
      Property name = selectedItem.getItemProperty(LABEL_DATEINAME);
      String label = (String) name.getValue();
      return label;
    }
    else {
      return null;
    }
  }

  /**
   * Laden des Inhalts des aktuellen XtreemFS-Verzeichnisses
   */
  private void loadXtreemFSData() {
    IndexedContainer xtreemFSData = createFields();

    // Die IDs werden zum Verschieben von Dateien gebraucht.
    VaadinXtreemFSSession.setDirectoryIds(new ArrayList<Object>());

    if (getVolume() != null) {
      try {
        int i = 1;

        // Alle Elemente einfügen
        String currentDir = getCurrentDir();
        for (DirectoryEntry e : listEntries(currentDir)) {
          // .. im Home-Dir ausblenden
          if (getHomeDir() == null
              || getHomeDir() != null
              && (currentDir != null && !currentDir.equals(mergePaths(getHomeDir(), "")))
              || !e.getName().equals("..")) {
            createItems(xtreemFSData, e.getName(), XtreemFSConnect.isDirectory(e), e.getStbuf(), i++);
          }
        }

      } catch (IOException e) {
        if (e.getMessage().contains("access denied")) {
          showNotification("Error", "You don't have the rights to access '"+getCurrentDir()+"'.", Notification.TYPE_ERROR_MESSAGE, e);
        }
        else {
          showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
        }
      }
    }

    this.table.setContainerDataSource(xtreemFSData);
    this.autocompleteFilter.setContainerDataSource(xtreemFSData);
  }


  /**
   * Erstellt die Spalten
   */
  protected IndexedContainer createFields() {
    IndexedContainer ic = new IndexedContainer();
    ic.addContainerProperty(LABEL_ICON, Embedded.class, null);

    for (String p : fields) {
      ic.addContainerProperty(p, String.class, "");
    }
    return ic;
  }


  /**
   * Erstellt den Inhalt
   */
  protected Object createItems (IndexedContainer ic, String name, boolean directory, Stat s, int i) throws ReadOnlyException, IOException {
    Object id = ic.addItem();
    ic.getContainerProperty(id, LABEL_ICON).setValue(Utils.getIconByEntry(directory));
    ic.getContainerProperty(id, LABEL_DATEINAME).setValue(name);
    if (directory) {
      VaadinXtreemFSSession.addDirectoryId(new Integer(i));
      ic.getContainerProperty(id, LABEL_GROESSE).setValue("Directory");
    }
    else {
      ic.getContainerProperty(id, LABEL_GROESSE).setValue(XtreemFSConnect.formatSize(s));
    }
    ic.getContainerProperty(id, LABEL_DATUM).setValue(XtreemFSConnect.formatDate(s));
    ic.getContainerProperty(id, LABEL_ACCESS_MODES).setValue(Utils.getFilePermissions(s));

    return ic;

  }

  /**
   * Dialog zum Umbennennen von Dateien und Ausführen der
   * Operation in XtreemFS
   */
  private void renameFile(final String selectedFile) {
    getMainWindow().addWindow(
        new TextInputDialog(
            "Rename file/directory",
            "New name",
            getFilename(null),
            new TextInputDialog.Callback() {
              public void onDialogResult(boolean happy, String newDirectory) {
                if (happy
                    && isValidFile(newDirectory)
                    && isValidFile(selectedFile)
                    ) {
                  try {
                    // Datei umbenennen
                    File oldFileDir = getFile(selectedFile);
                    if (oldFileDir.exists()) {
                      oldFileDir.renameTo(getFile(newDirectory));
                    }
                    showNotification("Success", "Directory " + selectedFile + " moved to " + newDirectory, Notification.TYPE_TRAY_NOTIFICATION, null);

                    // Update durchführen
                    loadXtreemFSData();
                  } catch (IOException e) {
                    showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
                  }
                }
              }
            }));
  }

  /**
   * Dialog zum Löschen von Dateien und Operationen
   * in XtreemFS
   */
  private void removeFile(final String selectedFile) {
    getMainWindow().addWindow(
        new YesNoDialog(
            "Caution",
            "Do you really want to delete '"+selectedFile+"'?",
            new YesNoDialog.Callback() {
              public void onDialogResult(boolean happy) {
                if (happy) {
                  final File requestedFile = getFile(selectedFile);
                  try {
                    // Datei nur löschen, falls sie auch existiert.
                    if (requestedFile != null
                        && requestedFile.exists()) {
                      removeAll(requestedFile);
                      Xtreemfs_portletApplication.this.table.removeItem(Xtreemfs_portletApplication.this.table.getValue());
                      Xtreemfs_portletApplication.this.table.select(null);

                      updateGui();
                    }
                  } catch (IOException e) {
                    showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
                  }
                }
              }
            }));
  }

  public void removeAll(final File requestedFile) throws IOException {
    if (!requestedFile.getPath().endsWith(PREV_DIRECTORY)
        && !requestedFile.getPath().endsWith(CURRENT_DIRECTORY)) {
      //System.out.println("Removing " + requestedFile.getPath());
      if (requestedFile.isDirectory()) {
        for (DirectoryEntry e : listEntries(requestedFile.getPath())) {
          removeAll(getVolume().getFile(requestedFile.getPath()+"/"+e.getName(), getCredentials()));
        }
        requestedFile.delete(getCredentials());
      }
      else {
        requestedFile.delete(getCredentials());
      }
    }
  }

  private void updateGui() {
    Xtreemfs_portletApplication.this.downloadButton.setEnabled(false);
    Xtreemfs_portletApplication.this.removalButton.setEnabled(false);
    Xtreemfs_portletApplication.this.renameButton.setEnabled(false);
  }

  private void setAccessRightsForFile(final String selectedFile) {
    // get the file
    File file = getFile(selectedFile);

    if (file != null) {
      getMainWindow().addWindow(
          new AccessRightsDialog(
              "Access rights",
              file,
              new AccessRightsDialog.ConfirmCallBack() {
                @Override
                public void commit() {
                  // update the directory
                  loadXtreemFSData();

                  // disable buttons
                  updateGui();
                }

              })
          );
    }
  }

  /**
   * Hochladen einer Datei nach XtreemFS
   */
  private OutputStream uploadFile(String filename) {
    try {
      // Output stream to write to
      File file = getFile(getCurrentDir() + "/" + filename, getCredentials());

      return new BufferedFileOutputStream(file);
    } catch (IOException e) {
      showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
    }
    return null;
  }


  /**
   * Aktion nach Upload.
   * Die Datei wird auf Read-Only gesetzt und die Tabelle wird aktuallisiert.
   */
  private void uploadFileFinished(SucceededEvent event) {
    try {
      //      String fileName = event.getFilename();
      //      File f = getFile(fileName);
      //      f.setReadOnly(READONLY_REPLICATION_MODE);
    } catch (Exception e) {
      Logging.logMessage(Logging.LEVEL_WARN, this,"%s, %s, %s", "Readonly Exception", "Can't set file " + event.getFilename() + " to ReadOnly.", e);
    }

    // Update der Liste durchführen
    loadXtreemFSData();
  }

  /**
   * Herunterladen einer Datei aus XtreemFS.
   * Dafür wird ein Stream geöffnet, der an den Browser weitergereicht wird.
   * @throws IOException
   */
  private void downloadFile(String selectedFile) throws IOException {
    if (selectedFile != null) {
      File file = getFile(selectedFile);
      StreamSource fileSource = getFileSource(file);

      Embedded e = new Embedded("", new VaadinFileDownloadResource(fileSource, selectedFile, file.stat().getSize(), getMainWindow().getApplication()));
      e.setType(Embedded.TYPE_BROWSER);
      e.setSizeFull();

      VerticalLayout l = new VerticalLayout();
      l.setSizeFull();
      l.addComponent(e);

      Window w = new Window(selectedFile, l);
      w.center();
      w.setWidth("90%");
      w.setHeight("90%");
      getMainWindow().addWindow(w);
      //      FileResource resource = new FileResource(new java.io.File("/home/pschaefe/Downloads/eclipse-jee-helios-SR1-linux-gtk-x86_64.tar.gz"), getMainWindow().getApplication());

      //      getMainWindow().open(new FileDownloadResource(fileSource, selectedFile, fileSize, getMainWindow().getApplication()), "_top");

    }
  }

  /**
   * Herunterladen eines Verzeichnisses als ZIP-Datei
   */
  private void downloadZippedDirectory(String selectedFolder) {
    if (selectedFolder != null) {
      File file = getFile(selectedFolder);
      StreamSource zipSource = getZipSource(file);
      getMainWindow().open(new VaadinFileDownloadResource(zipSource, selectedFolder+".zip", 0, getMainWindow().getApplication()), "_self");
    }
  }


  /**
   * Dialog zum Anlegen eines Verzeichnisses und Operationen
   * in XtreemFS.
   */
  private void createNewDirectory() {
    getMainWindow().addWindow(
        new TextInputDialog(
            "Create Directory",
            "Create Directory",
            "New Folder",
            new TextInputDialog.Callback() {
              public void onDialogResult(boolean happy, String newDirectory) {
                if ( happy
                    && isValidFile(newDirectory)) {
                  try {
                    // Verzeichnis anlegen
                    File newDir = getFile(newDirectory);
                    if (!newDir.exists()) {
                      newDir.mkdir(0755);
                    }
                    // Prüfen, ob das Verzeichnis angelegt wurde
                    if (newDir.exists()) {
                      loadXtreemFSData();
                      showNotification("Success", "Directory " + newDirectory + " created.", Notification.TYPE_TRAY_NOTIFICATION, null);
                    }
                    else {
                      showNotification("Error", "Directory " + newDirectory + " could not be created.", Notification.TYPE_WARNING_MESSAGE, null);
                    }
                  } catch (IOException e) {
                    showNotification("Error", e.toString(), Notification.TYPE_ERROR_MESSAGE, e);
                  }
                }
              }
            }));
  }

}
