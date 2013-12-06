package org.xtreemfs.portlet.util.vaadin;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import org.xtreemfs.common.clients.Volume;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * This class is needed to store XtreemFS configuration parameters in the users session, i.e. - the XtreemFS volume -
 * the current directory - the user credentials for authorization
 * 
 * It follows the thread local pattern for user session data in a vaadin application.
 * 
 * To initialize this class, call initialize(this) in the init()-method of the Vaadin-portlet.
 * 
 * http://devblog.mycorner.fi/55/threadlocal-pattern-with-vaadin/
 * 
 * @author Patrick Schaefer
 * 
 */
public class VaadinXtreemFSSession implements TransactionListener {
	private static final long serialVersionUID = 5936942290863994902L;

	Volume volume = null;
	RPC.UserCredentials credentials = null;
	String currentDir = "";

	// Ids der Verzeichnisse im aktuellen Verzeichnis
	private ArrayList<Object> directoryIds = new ArrayList<Object>();

	private Application application;
	// Use InheritableThreadLocal in order to use child threads in portlet application
	private static InheritableThreadLocal<VaadinXtreemFSSession> instance = new InheritableThreadLocal<VaadinXtreemFSSession>();

	/**
	 * This class should be initialized using the initialize method.
	 * 
	 * @param application
	 */
	private VaadinXtreemFSSession(Application application) {
		this.application = application;
		// Set a value for the ThreadLocal to avoid any NPEs
		instance.set(this);
	}

	@Override
	public void transactionEnd(Application application, Object transactionData) {
		// Clear thread local instance at the end of the transaction
		if (this.application == application) {
			instance.set(null);
		}
	}

	@Override
	public void transactionStart(Application application, Object transactionData) {
		// Set the thread local instance
		if (this.application == application) {
			instance.set(this);
		}
	}

	/**
	 * This method has to be called for initialization in the init()-method of the Vaadin-portlet:
	 * 
	 * public void init() { initialize(this); ... }
	 * 
	 * @param application
	 */
	public static void initialize(Application application) {
		if (application == null) {
			throw new IllegalArgumentException("Application may not be null");
		}
		VaadinXtreemFSSession appSettings = new VaadinXtreemFSSession(application);
		application.getContext().addTransactionListener(appSettings);
	}

	/**
	 * Removes this VaadinXtreemFSSession instance. This has to be done in order to prevent memory leaks.
	 */
	public static void destroy() {
		instance.remove();
	}

	/**
	 * Returns the XtreemFS volume
	 * 
	 * @return
	 */
	public static Volume getVolume() {
		return instance.get().volume;
	}

	public static void setVolume(Volume volume) {
		instance.get().volume = volume;
	}

	/**
	 * Returns the XtreemFS user credential corresponding to the SAML assertion of the liferay user for authorizsation.
	 * 
	 * @return
	 */
	public static RPC.UserCredentials getCredentials() {
		return instance.get().credentials;
	}

	public static void setCredentials(RPC.UserCredentials credentials) {
		instance.get().credentials = credentials;
	}

	/**
	 * Returns the current directory of the liferay user
	 * 
	 * @return
	 */
	public static String getCurrentDir() {
		if (instance.get() != null) {
			return instance.get().currentDir;
		}
		return null;
	}

	public static void setCurrentDir(String currentDir) {
		instance.get().currentDir = currentDir;
	}

	/**
	 * Returns the home directory of the user, i.e. this equals the DN string name of the user's certificate.
	 * 
	 * @return
	 */
	public static String getHomeDir() {
		if (instance.get().credentials != null) {
			return instance.get().credentials.getUsername();
		}
		return "";
	}

	/**
	 * Initialize the volume and the user-credentials in the session.
	 * 
	 * 
	 * @param credentials
	 *            The XtreemFS user credentials
	 * @param v
	 *            The XtreemFS volume
	 * @return
	 * @throws CertificateException
	 *             Throws an exception if the assertion can not be found or parsed
	 * @throws IOException
	 */
	public static Volume initVolume(RPC.UserCredentials credentials, Volume v) throws CertificateException, IOException {
		// Set the user credentials
		setCredentials(credentials);

		// Memorize the settings in user session
		setVolume(v);

		return v;
	}

	/**
	 * Returns the ids of the directories within the current directory.
	 * 
	 * @return
	 */
	public static ArrayList<Object> getDirectoryIds() {
		if (instance.get() != null) {
			ArrayList<Object> directoryIds = instance.get().directoryIds;
			if (directoryIds != null) {
				return directoryIds;
			}
		}
		return new ArrayList<Object>();
	}

	/**
	 * Returns the ids of the directories within the current directory
	 * 
	 * @return
	 */
	public static Object[] getDirectoryIdsAsArray() {
		if (instance.get() != null && instance.get().directoryIds != null) {
			return instance.get().directoryIds.toArray(new Object[] {});
		}
		return new Object[] {};
	}

	public static void setDirectoryIds(ArrayList<Object> directoryIds) {
		instance.get().directoryIds = directoryIds;
	}

	public static void addDirectoryId(Object id) {
		instance.get().directoryIds.add(id);
	}
	
}
