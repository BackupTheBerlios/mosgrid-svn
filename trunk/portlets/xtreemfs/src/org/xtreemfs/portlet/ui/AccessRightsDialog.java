package org.xtreemfs.portlet.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xtreemfs.common.clients.File;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

/**
 *
 *
 * @author bzcschae
 *
 */
public class AccessRightsDialog extends Window implements Button.ClickListener, Property.ValueChangeListener {

  private static final long serialVersionUID = -5293337445327535031L;

  Button yes = new Button("Apply", this);
  Button no = new Button("Discard", this);

  File f;
  FileItem fileItem;

  // Form & elements
  Form accessRightsForm = new Form();
  FileFieldFactory fileFieldFactory;

  // Callback
  ConfirmCallBack callback;

  public AccessRightsDialog(String caption, File file, ConfirmCallBack callback) {
    super(caption);
    setModal(true);
    setWidth("650px");
    setClosable(false);

    // the file in xtreemfs
    this.f = file;
    this.fileItem = new FileItem(this.f);
    this.callback = callback;
    this.fileFieldFactory = new FileFieldFactory(this, "40em");

    BeanItem<FileItem> fileItem = new BeanItem<FileItem>(this.fileItem); // item from

    // the form to change access rights
    this.accessRightsForm.setCaption("Details for " + Utils.getFileName(file));
    this.accessRightsForm.setWriteThrough(false); // we want explicit 'apply'
    this.accessRightsForm.setInvalidCommitted(true);
    this.accessRightsForm.setFormFieldFactory(this.fileFieldFactory);
    this.accessRightsForm.setItemDataSource(fileItem);

    this.accessRightsForm.setVisibleItemProperties(Arrays.asList(new String[] {
        //        FileItem.LABEL_FILENAME,
        FileItem.LABEL_OWNER,
        //        FileItem.LABEL_GROUP, group has no function for d-grid certificates
        FileItem.LABEL_OTHERS,
        FileItem.LABEL_ACL}));

    // Add form to layout
    addComponent(this.accessRightsForm);

    // sets the correct labels for the acl entries, i.e. 'patrick: rwx'
    this.fileFieldFactory.permissions.init(this.fileItem.getFileACLRights());

    // Add yes, no buttons
    HorizontalLayout hl = new HorizontalLayout();
    hl.addComponent(this.yes);
    hl.addComponent(this.no);
    hl.setComponentAlignment(this.yes, Alignment.MIDDLE_CENTER);
    hl.setComponentAlignment(this.no, Alignment.MIDDLE_CENTER);
    hl.setSizeUndefined();
    addComponent(hl);
  }

  @Override
  public void buttonClick(ClickEvent event) {
    if (event.getSource() == this.yes) {
      this.accessRightsForm.commit();
      commitFileChanges();
    }
    else if (event.getSource() == this.no) {
      //      this.accessRightsForm.discard();
    }
    if (getParent() != null) {
      (getParent()).removeWindow(this);
    }

    // call the callback
    if (this.callback != null) {
      this.callback.commit();
    }
  }

  /*
   * Shows a notification when a selection is made.
   */
  public void valueChange(ValueChangeEvent event) {
    if (getParent() != null) {
      HashSet<String> selectedAclEntries = new HashSet<String>();
      HashMap<String, String> oldAclEntries = this.fileItem.getFileACLRights();
      final List<String> newAclEntries = new ArrayList<String>();
      String user = "";

      // at least one user has been set
      if (!event.getProperty().toString().equals("[]")) {
        user = event.getProperty().toString();

        // remove trailing and ending []
        user = user.substring(1, user.length()-1);

        // check for the new users in the list
        String[] allUsers = user.split(", ?CN="); // TODO ' ' oder nicht egal
        for (String currentUser : allUsers) {
          String u = !currentUser.startsWith("CN=")? "CN="+currentUser.trim() : currentUser.trim();
          if (oldAclEntries.get(u) == null) {
            newAclEntries.add(u);
          }
          selectedAclEntries.add(u);
        }
      }

      // check for users to remove
      final List<String> removeAclEntries = new ArrayList<String>();
      for (Entry<String, String> a : oldAclEntries.entrySet()) {
        if (!selectedAclEntries.contains(a.getKey())) {
          String u = a.getKey();
          removeAclEntries.add(u);
          this.fileFieldFactory.permissions.setItemCaption(u, Utils.getCaptionForDN(u));
        }
      }
      for (String remove : removeAclEntries) {
        oldAclEntries.remove(remove);
      }

      // one popup for all changes
      if (!newAclEntries.isEmpty()) {
        Window window
        = new ConfirmAccessRightsDialog(
            "Access rights for selected users",
            new ConfirmAbortCallBack() {
              @Override
              public void commit(String selectedValue) {
                // use the new value
                HashMap<String, String> acl = AccessRightsDialog.this.fileItem.getFileACLRights();
                for (String u : newAclEntries) {
                  acl.put(u, selectedValue);

                  // change the display text
                  AccessRightsDialog.this.fileFieldFactory.permissions.setItemCaption(
                      u,
                      Utils.getCaptionForDN(u) + ": " + Utils.getReadAbleFilePermissions(selectedValue));
                }
              }
              @Override
              public void abort() {
                // remove the value
                HashMap<String, String> acl = AccessRightsDialog.this.fileItem.getFileACLRights();
                for (String u : newAclEntries) {
                  acl.remove(u);
                  AccessRightsDialog.this.fileFieldFactory.permissions.setItemCaption(u, u);
                  AccessRightsDialog.this.fileFieldFactory.permissions.unselect(u);
                  AccessRightsDialog.this.fileFieldFactory.permissions.addItem(u);
                }
              }
            });
        (getParent()).addWindow(window);
      }
    }
  }


  public void commitFileChanges() {
    String others = this.fileItem.getFilePermissionsOther();
    String owner = this.fileItem.getFilePermissionsOwner();

    // perform changes to access rights
    try {
      int originalMode = this.f.stat().getMode();
      String octal = Integer.toOctalString(originalMode);
      char[] chars = octal.toCharArray();
      try {
        // others
        chars[chars.length-1] = others.charAt(0);
      } catch (Exception e) {}
      try {
        // owner
        chars[chars.length-3] = owner.charAt(0);
      } catch (Exception e) {}
      // set the new access rights
      int mode = Integer.parseInt(new String(chars), 8);
      this.f.chmod(mode);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // commit changes to the ACLs
    try {
      Map<String, Object> aclEntries = new HashMap<String, Object>();

      // parse entries
      HashMap<String, String> acls = this.fileItem.getFileACLRights();
      if (acls != null) {
        for (Entry<String, String> acl : acls.entrySet()) {
          String user = acl.getKey();
          String rights = acl.getValue();
          // generates entry of the form u:name:mode, e.g. u:patrick:7
          if (user.contains(":")) {
            System.out.println("ERROR: user u " + user + " contains :" );
            continue;
          }
          String aclEntryKey = "u:" + user;
          String aclEntryValue =  Utils.getReadAbleFilePermissions(rights);
          aclEntries.put(aclEntryKey,  aclEntryValue);

          System.out.println("setting " + aclEntryKey + ":" + aclEntryValue);
        }
      }
      // perform changes. the old acl will be replaced
      this.f.setACL(aclEntries);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * A callback for updates with an abort and commit
   */
  public interface ConfirmAbortCallBack {
    public void commit(String value);
    public void abort();
  }

  /**
   * A callback for updates with a commit.
   */
  public interface ConfirmCallBack {
    public void commit();
  }

  class ConfirmAccessRightsDialog
  extends Window
  implements Button.ClickListener  {
    private static final long serialVersionUID = -1683852361677682751L;

    Button yes = new Button("Apply", this);
    Button no = new Button("Discard", this);
    FileFieldFactory.FilePermissions permissions;

    private ConfirmAbortCallBack callback;

    public ConfirmAccessRightsDialog(String caption, ConfirmAbortCallBack callback) {
      super(caption);
      this.callback = callback;

      setModal(true);
      setClosable(false);
      setWidth("300px");

      this.permissions = new FileFieldFactory.FilePermissions("Access rights", false);
      this.permissions.setNullSelectionAllowed(false);
      this.permissions.setImmediate(true);
      addComponent(this.permissions);

      // Add buttons
      HorizontalLayout hl = new HorizontalLayout();
      hl.addComponent(this.yes);
      hl.addComponent(this.no);
      hl.setComponentAlignment(this.yes, Alignment.MIDDLE_CENTER);
      hl.setComponentAlignment(this.no, Alignment.MIDDLE_CENTER);
      hl.setSizeUndefined();
      addComponent(hl);
    }

    @Override
    public void buttonClick(ClickEvent event) {
      String selectedValue = (String) this.permissions.getValue();

      if (event.getSource() == this.yes) {
        //        accessRightsForm.commit();
        this.callback.commit(selectedValue);
      }
      else if (event.getSource() == this.no) {
        //        accessRightsForm.discard();
        this.callback.abort();
      }
      // close the window
      if (getParent() != null) {
        (getParent()).removeWindow(this);
      }
    }
  }
}
