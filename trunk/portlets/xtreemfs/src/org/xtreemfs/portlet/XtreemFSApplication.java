package org.xtreemfs.portlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.xtreemfs.common.clients.Client;
import org.xtreemfs.common.clients.File;
import org.xtreemfs.common.clients.Volume;
import org.xtreemfs.foundation.TimeSync;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC.UserCredentials;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;
import org.xtreemfs.portlet.util.XtreemFSConnect;
import org.xtreemfs.portlet.util.vaadin.VaadinXtreemFSSession;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2.PortletListener;
import com.vaadin.ui.Window;

public class XtreemFSApplication extends Application implements PortletListener {
	private static final long serialVersionUID = 5568511797248671665L;

	String serviceCertFile = null; // "/usr/local/guseuser/tomcat/temp/users/mosgrid.p12";
	String serviceCertPass = null; // "tue-sg243";
	String userBasePath = null;
	String propertiesFile = System.getProperty("catalina.base") +
			java.io.File.separator + "conf/mosgrid.properties";

	// the client is a singleton, i.e. it is shared between all users
	private static Client c;
	private static Object client_lock = new Object();

	@Override
	public void start(URL applicationUrl, Properties applicationProperties, ApplicationContext context) {
		super.start(applicationUrl, applicationProperties, context);

		try {
			Properties properties = new Properties();
			java.io.File propFile = new java.io.File(this.propertiesFile);
			InputStream s = new java.io.FileInputStream(propFile);
			properties.load(s);

			this.serviceCertFile = properties.getProperty("serviceCertFileOverride");
			if (this.serviceCertFile == null || "".equals(this.serviceCertFile)) {
				this.serviceCertFile = properties.getProperty("serviceCertFile");
			}

			this.serviceCertPass = properties.getProperty("serviceCertPass");
			String guseBasePath = properties.getProperty("guseBasePath");
			String userPath = properties.getProperty("guseUserPath");
			this.userBasePath = guseBasePath + userPath;

			System.out.println("Use service cert file: " + this.serviceCertFile);
			System.out.println("Use base directory: " + this.userBasePath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		// Memorize the Settings
		VaadinXtreemFSSession.initialize(this);

		// Prüfen, ob wir als Portlet laufen
		if (getContext() instanceof PortletApplicationContext2) {
			PortletApplicationContext2 ctx = (PortletApplicationContext2) getContext();
			// Request-Listeneer hinzufügen
			ctx.addPortletListener(this, this);
		}
	}

	@Override
  public void handleRenderRequest(RenderRequest request, RenderResponse response, Window window) {
    request.setAttribute("org.apache.tomcat.sendfile.support", Boolean.TRUE);

    try {
      // Assertion initialisieren
      UserCredentials credentials = XtreemFSConnect.createUserCredentialsSAML(request, this.userBasePath);

      // Verbindung zu XtreemFS herstellen
      synchronized (client_lock) {
        if (c==null) {
          c = XtreemFSConnect.connectSAML(this.serviceCertFile, this.serviceCertPass);
          if (c==null) {
            throw new RuntimeException("Client not connected");
          }
        }
      }

      // get volume
      Volume v = XtreemFSConnect.getVolume(credentials, c);

      // Open Volume & Credentials setzen
      VaadinXtreemFSSession.initVolume(credentials, v);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

	/**
	 * Liefert den Namen des aktuelle Verzeichnisses inklusive
	 * Simulations-Verzeichnis und den Unterverzeichnissen
	 */
	public String getCurrentDir() {
		return mergePaths(VaadinXtreemFSSession.getHomeDir(), VaadinXtreemFSSession.getCurrentDir());
	}

	/**
	 * Fügt die beiden Pfade zu einem Pfad zusammen
	 */
	protected String mergePaths(String path1, String path2) {
		String path = "";
		if (path1 != null && !path1.equals("")) {
			path += (path1.startsWith("/") ? "" : "/") + path1;
		}
		if (path2 != null && !path2.equals("")) {
			path += (!path.endsWith("/") && !path2.startsWith("/") ? "/" : "") + path2;
		}
		return path;
	}

	public File getFile(String path, UserCredentials credentials) {
		// Output stream to write to
		return getVolume().getFile(path, credentials);
	}

	public Volume getVolume() {
		return VaadinXtreemFSSession.getVolume();
	}

	public UserCredentials getCredentials() {
		return VaadinXtreemFSSession.getCredentials();
	}

	public DirectoryEntry[] listEntries(String path) throws IOException {
		return VaadinXtreemFSSession.getVolume().listEntries(path, getCredentials());
	}

	public String getHomeDir() {
		return VaadinXtreemFSSession.getHomeDir();
	}

	public void closeClientConnection() {
		// XtreemFS beenden
		synchronized (client_lock) {
			if (c != null) {
				c.stop();
				c = null;

				try {
					// Time Synchronization is a Singleton - it has to be closed
					// on its own
					TimeSync.close();
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public void close() {
		super.close();
		// do not close here, as a user logout would close the connection
		// closeClientConnection();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		closeClientConnection();
	}

	@Override
	public void handleActionRequest(ActionRequest request, ActionResponse response, Window window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEventRequest(EventRequest request, EventResponse response, Window window) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleResourceRequest(ResourceRequest request, ResourceResponse response, Window window) {
		// TODO Auto-generated method stub

	}
}
