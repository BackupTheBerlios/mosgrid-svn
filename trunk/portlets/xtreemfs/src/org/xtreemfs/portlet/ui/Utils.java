package org.xtreemfs.portlet.ui;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.xtreemfs.common.clients.File;
import org.xtreemfs.common.clients.Volume;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC.UserCredentials;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.Stat;
import org.xtreemfs.portlet.Xtreemfs_portletApplication;
import org.xtreemfs.portlet.util.XtreemFSConnect;
import org.xtreemfs.portlet.util.vaadin.VaadinXtreemFSSession;

import com.liferay.portal.util.PortalUtil;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Embedded;

public class Utils {


  /**
   * Liefert die Dateirechte in einem lesbaren Format
   * (als 4 Oktal-Werte: 0777).
   */
  public static String getFilePermissions(Stat s) {
    String octal = Integer.toOctalString(s.getMode());
    octal = octal.substring(octal.length()-4, octal.length());
    return octal;
  }

  /**
   * Liefert die Dateirechte als rwx STring
   * @param permissions
   * @return
   */
  public static String getReadAbleFilePermissions(String permissions) {
    if (permissions != null
        && !permissions.contains("r")
        && !permissions.contains("w")
        && !permissions.contains("x")
        && !permissions.contains("-")) {
      String readable = "";
      int filePermissions = Integer.decode(permissions);
      if ((filePermissions & 4) != 0) {
        readable += "r";
      }
      if ((filePermissions & 2) != 0) {
        readable += "w";
      }
      if ((filePermissions & 1) != 0) {
        readable += "x";
      }
      return readable;
    }
    return permissions;
  }

  public static String getCaptionForDN(String dn) {
    if (dn != null && dn.indexOf(",")>0) {
      return dn.substring(3, dn.indexOf(","));
    }
    return dn;
  }

  /**
   * Gibt an, ob die Datei Readonly ist.
   */
  public static String formatReadOnly (boolean readOnly) {
    return readOnly? "Yes":"No";
  }

  /**
   * Liefert die Informationen zum ANzeigen des Icons zu einer Datei und einem
   * Verzeichnis
   */
  public static Embedded getIconByEntry (boolean directory) throws IOException {
    String file = "";
    String type = "";
    if ( directory ) {
      file = Xtreemfs_portletApplication.ICON_FOLDER;
      type = "Directory";
    }
    else {
      file = Xtreemfs_portletApplication.ICON_DOCUMENT;
      type = "File";
    }
    return new Embedded(type, new ThemeResource(file));
  }


  /**
   * Liest aus dem Request den Parameter "param" aus.
   * Es werden sowohl GET als auch POST Parameter überprüft.
   */
  public static String parseRequest(RenderRequest request, String param) {
    String value = null;
    HttpServletRequest realRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(request));
    if (realRequest.getParameter(param)!=null
        || realRequest.getAttribute(param)!=null) {
      value = realRequest.getParameter(param);
      if (value == null || value.equals("")) {
        value = (String) realRequest.getAttribute(param);
      }
    }
    return value;
  }


  /**
   * Returns the file name without the directories
   * @return
   */
  public static String getFileName (File file) {
    if (file != null) {
      String path = file.getPath();
      return path.substring(path.lastIndexOf("/")+1);
    }
    return "";
  }

  /**
   * Packt ein Verzeichnis in XtreemFS als .zip
   */
  public static void zipDir(Volume v, String dir2Zip, ZipOutputStream zos) {
    try {
      byte[] readBuffer = new byte[8096*8];
      int bytesIn = 0;

      UserCredentials credentials = VaadinXtreemFSSession.getCredentials();

      // Verzeichnis holen und Dateien iterieren
      for(DirectoryEntry e : v.listEntries(dir2Zip, credentials))  {
        // Rekursiver Aufruf, falls enhaltene Datei Verzeichnis ist
        if(XtreemFSConnect.isDirectory(e)) {
          String filePath = dir2Zip + "/" + e.getName();
          zipDir(v, filePath, zos);
        }
        // Datei
        else {
          // Datei einlesen
          File f = v.getFile(dir2Zip + "/" + e.getName(), credentials);
          org.xtreemfs.portlet.util.FileInputStream fis = new org.xtreemfs.portlet.util.FileInputStream(f);

          // ZIP-Eintrag erstellen
          String path = f.getPath();
          ZipEntry anEntry = new ZipEntry(path);
          zos.putNextEntry(anEntry);

          // Datei in ZIP schreiben
          while((bytesIn = fis.read(readBuffer)) > -1) {
            zos.write(readBuffer, 0, bytesIn);
          }

          // Stream schließen
          fis.close();
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


}
