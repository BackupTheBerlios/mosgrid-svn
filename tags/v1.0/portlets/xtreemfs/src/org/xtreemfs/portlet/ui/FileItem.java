package org.xtreemfs.portlet.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xtreemfs.common.clients.File;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.Stat;
import org.xtreemfs.portlet.util.XtreemFSConnect;

/**
 * Repräsentiert eine Datei in XtreemFS.
 */
public class FileItem implements Serializable {
  private static final long serialVersionUID = -6087919310064063951L;

  public static final String LABEL_FILETYPE = "fileType";
  public static final String LABEL_FILESIZE = "fileSize";
  public static final String LABEL_FILENAME = "fileName";

  public static final String LABEL_FILEDATE= "fileDate";
  public static final String LABEL_FILEOWNER = "fileOwner";
  public static final String LABEL_FILEREPLICAS = "fileReplicas";

  public static final String LABEL_FILEPERMISSIONS = "filePermissions";
  public static final String LABEL_OWNER = "filePermissionsOwner";
  public static final String LABEL_GROUP = "filePermissionsGroup";
  public static final String LABEL_OTHERS = "filePermissionsOther";
  public static final String LABEL_ACL = "fileACL";

  public static final String LABEL_READONLY = "fileReadOnly";

  private File file;
  private Stat stat;
  private Map<String,Object> acl;

  private String fileName = "";
  private String fileSize = "";
  private String fileType = "";

  private String fileDate = null;
  private String fileOwner = "";
  private int fileReplicas;

  private String filePermissions = "";
  private String filePermissionsOwner = "";
  private String filePermissionsGroup = "";
  private String filePermissionsOther = "";

  private String fileReadOnly = "";

  private Set<String> fileACL = new HashSet<String>();
  private HashMap<String, String> fileACLRights = new HashMap<String, String>();


  public int getFileReplicas() {
    return this.fileReplicas;
  }

  public void setFileReplicas(int fileReplicas) {
    this.fileReplicas = fileReplicas;
  }

  public FileItem(File f) {
    try {
      this.file = f;
      this.stat = f.stat();

      this.fileName = f.getPath();

      if (this.fileName == null || this.fileName.equals("")) {
        this.fileName = "/";
      }
      else {
        this.fileName = "xtreemfs:/"+this.fileName.replaceAll(" ", "%20");
      }
      this.fileSize = Math.ceil(this.stat.getSize() / 1024.0) + " KiB";

      if (this.file.isDirectory()) {
        this.fileType = "Verzeichnis";
        this.fileReplicas = 1;
      }
      else {
        this.fileType = "Datei";
        this.fileReplicas = f.getNumReplicas();
      }

      this.fileDate = XtreemFSConnect.formatDate(this.stat);
      this.fileOwner = this.stat.getUserId();
      this.filePermissions = Utils.getFilePermissions(this.stat);

      try {
        this.filePermissionsOwner = ""+Utils.getFilePermissions(this.stat).charAt(1);
      } catch (Exception e) {
        this.filePermissionsOwner = "0";
      }
      try {
        this.filePermissionsOther = ""+Utils.getFilePermissions(this.stat).charAt(3);
      } catch (Exception e) {
        this.filePermissionsOther = "0";
      }

      this.fileReadOnly = Utils.formatReadOnly(f.isReadOnly());

      // parse ACL entries
      this.acl = f.getACL();
      for (Entry<String, Object> aclEntry : this.acl.entrySet()) {
        if (aclEntry.getKey().startsWith("u:")) {
          String[] parts = aclEntry.getKey().split(":");
          if (parts.length > 1
              && parts[1]!= null
              && !parts[1].trim().equals("")) {
            this.fileACL.add(parts[1]);
            this.fileACLRights.put(parts[1], String.valueOf(aclEntry.getValue()));
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getFileType() {
    return this.fileType;
  }

  public void setFileType(String typ) {
    this.fileType = typ;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String firstName) {
    this.fileName = firstName;
  }

  public String getFileSize() {
    return this.fileSize;
  }

  public void setFileSize(String lastName) {
    this.fileSize = lastName;
  }

  public String getFileDate() {
    return this.fileDate;
  }

  public void setFileDate(String fileDate) {
    this.fileDate = fileDate;
  }

  public String getFileOwner() {
    return this.fileOwner;
  }

  public void setFileOwner(String fileOwner) {
    this.fileOwner = fileOwner;
  }

  public String getFilePermissions() {
    return this.filePermissions;
  }

  public void setFilePermissions(String filePermissions) {
    this.filePermissions = filePermissions;
  }

  public String getFileReadOnly() {
    return this.fileReadOnly;
  }

  public void setFileReadOnly(String fileReadOnly) {
    this.fileReadOnly = fileReadOnly;
  }

  public String getFilePermissionsOwner() {
    return this.filePermissionsOwner;
  }

  public void setFilePermissionsOwner(String filePermissionsOwner) {
    this.filePermissionsOwner = filePermissionsOwner;
  }

  public String getFilePermissionsGroup() {
    return this.filePermissionsGroup;
  }

  public void setFilePermissionsGroup(String filePermissionsGroup) {
    this.filePermissionsGroup = filePermissionsGroup;
  }

  public String getFilePermissionsOther() {
    return this.filePermissionsOther;
  }

  public void setFilePermissionsOther(String filePermissionsOther) {
    this.filePermissionsOther = filePermissionsOther;
  }

  public Set<String> getFileACL() {
    return this.fileACL;
  }

  public void setFileACL(Set<String> fileACL) {
    this.fileACL = fileACL;
  }

  public HashMap<String, String> getFileACLRights() {
    return this.fileACLRights;
  }

  public void setFileACLRights(HashMap<String, String> fileACLRights) {
    this.fileACLRights = fileACLRights;
  }
}
