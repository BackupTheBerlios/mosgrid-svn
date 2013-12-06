package org.xtreemfs.portlet.ui;

import java.io.File;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.xtreemfs.portlet.util.XtreemFSConnect;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;

public class FileFieldFactory extends DefaultFieldFactory {
  private static final long serialVersionUID = 6649032807751544192L;

  private static final String COMMON_FIELD_WIDTH = "16em";
  private Property.ValueChangeListener listener;

  private String commonFieldWidth = COMMON_FIELD_WIDTH;

  public ACL permissions;

  public FileFieldFactory(Property.ValueChangeListener listener) {
    this.listener = listener;
  }

  public FileFieldFactory(Property.ValueChangeListener listener, String commonFieldWidth) {
    this(listener);
    this.commonFieldWidth = commonFieldWidth;
  }


  @Override
  public Field createField(Item item, Object propertyId, Component uiContext) {
    Field f = super.createField(item, propertyId, uiContext);

    if (FileItem.LABEL_FILEDATE.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Modified");
    }
    else if (FileItem.LABEL_FILENAME.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Filename");
      tf.setHeight("6em");
    }
    else if (FileItem.LABEL_FILEOWNER.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Creator");
      tf.setHeight("6em");
    }
    else if (FileItem.LABEL_FILEREPLICAS.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Replicas");
    }
    else if (FileItem.LABEL_FILESIZE.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Size");
    }
    else if (FileItem.LABEL_FILETYPE.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Typ");
    }

    else if (FileItem.LABEL_FILEPERMISSIONS.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Permissions");
    }
    else if (FileItem.LABEL_OWNER.equals(propertyId)) {
      FilePermissions permissions = new FilePermissions("Owner", true);
      return permissions;
    }
    else if (FileItem.LABEL_GROUP.equals(propertyId)) {
      FilePermissions permissions = new FilePermissions("Group", true);
      return permissions;
    }
    else if (FileItem.LABEL_OTHERS.equals(propertyId)) {
      FilePermissions permissions = new FilePermissions("Others", true);
      return permissions;
    }
    else if (FileItem.LABEL_ACL.equals(propertyId)) {
      this.permissions = new ACL("ACL", this.listener);
      return this.permissions;
    }

    else if (FileItem.LABEL_READONLY.equals(propertyId)) {
      TextField tf = (TextField) f;
      tf.setCaption("Read Only");
    }

    f.setReadOnly(true);
    f.setWidth(this.commonFieldWidth);

    return f;
  }

  static class FilePermissions extends ComboBox {
    private static final long serialVersionUID = -2702880861350039486L;

    public FilePermissions(String caption, boolean allowNone) {
      super(caption);

      setWidth(COMMON_FIELD_WIDTH);
      setFilteringMode(ComboBox.FILTERINGMODE_STARTSWITH);
      setNullSelectionAllowed(false);

      addItem("7");
      setItemCaption("7", "Read & Write & Execute");
      addItem("6");
      setItemCaption("6", "Read & Write");
      addItem("5");
      setItemCaption("5", "Read & Execute");
      addItem("4");
      setItemCaption("4", "Read");
      addItem("1");
      setItemCaption("1", "Execute");

      if (allowNone) {
        addItem("0");
        setItemCaption("0", "None");
      }

      setValue("4");
      setImmediate(true); // update the label immediatly
    }
  }

  private static ConcurrentHashMap<String, String> dns = new ConcurrentHashMap<String, String>();
  private static volatile long nextScanForDNs = 0;

  class ACL extends TwinColSelect {
    private static final long serialVersionUID = -6106863985811233456L;
    private String[] people = new String[] {};

    private Property.ValueChangeListener listener;

    public ACL (String caption, Property.ValueChangeListener listener) {
      super(caption);

      this.listener = listener;
      this.people = parseCommonNames();

      for (String dn : this.people) {
        addItem(dn);
        setItemCaption(dn, Utils.getCaptionForDN(dn));
      }
      setRows(7);
      setNullSelectionAllowed(true);
      setMultiSelect(true);
      setImmediate(true);

      // react to changes
      if (listener != null) {
        addListener(this.listener);
      }
      //      setLeftColumnCaption("Available cities");
      //      setRightColumnCaption("Selected destinations");
      setWidth("550px");
    }

    /**
     * initialize the field
     * @param fileACLRights
     */
    public void init(HashMap<String, String> fileACLRights) {
      for (Entry<String, String> entry : fileACLRights.entrySet()) {
        try {
          System.out.println("setting ACL: " + entry.getKey() + " " + entry.getValue());
          String caption = Utils.getCaptionForDN(entry.getKey());
          setValue(entry.getKey());
          setItemCaption(entry.getKey(), caption + ": " + entry.getValue());
          System.out.println("done setting ACL");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    public synchronized String[] parseCommonNames() {
      // wait for 30 seconds before scanning again
      if (System.currentTimeMillis() > FileFieldFactory.nextScanForDNs) {
        // Tomcat-Home auslesen
        String tomcatHome = System.getProperty("catalina.base");
        if (tomcatHome == null || tomcatHome.equals("")) {
          tomcatHome = System.getProperty("catalina.home");
        }
        System.out.println("reparsing assertions");

        File assertionDir = new File(tomcatHome + "/temp/users/");
        if (assertionDir.isDirectory()) {
          File[] subDirs = assertionDir.listFiles();
          for (File subDir : subDirs) {
            if (subDir.isDirectory()
                && !dns.containsKey(subDir.getAbsolutePath())) {
              for (File assertion : subDir.listFiles()) {
                if (assertion.isFile()
                    && assertion.getName().contains("x509up.")) {
                  try {
                    String dn = XtreemFSConnect.getDnFromAssertion(assertion.getAbsolutePath());
                    dns.put(subDir.getAbsolutePath(), dn);
                    System.out.println("assertion found" + assertion + "\t" + dn);
                  } catch (CertificateException e) {
                    System.out.println("assertion could not be parsed: " + assertion);
                  }
                }
              }
            }
          }
        }
        FileFieldFactory.nextScanForDNs = System.currentTimeMillis() + 30*1000;

        System.out.println("done parsing assertions");
      }
      return dns.values().toArray(new String[]{});
    }
  }


}
