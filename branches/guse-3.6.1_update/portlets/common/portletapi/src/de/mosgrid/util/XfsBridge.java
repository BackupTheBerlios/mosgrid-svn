package de.mosgrid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.common.clients.Volume;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC.UserCredentials;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;
import org.xtreemfs.portlet.util.BufferedFileOutputStream;
import org.xtreemfs.portlet.util.FileInputStream;
import org.xtreemfs.portlet.util.XfsProperties;
import org.xtreemfs.portlet.util.XtreemFSConnect;
import org.xtreemfs.portlet.util.vaadin.VaadinXtreemFSSession;

import de.mosgrid.exceptions.PortletInitializationException;

/**
 * Bridge to XTreemFS.
 * 
 */
public class XfsBridge {
	/* constants */
	private final Logger LOGGER = LoggerFactory.getLogger(XfsBridge.class);

	// prefix for xfs urls
	public static final String XFS_URL_PREFIX = "xtreemfs://";

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

	private PortletRequest request;
	private static XfsClientWrapper clientWrapper = null;
	private static Object client_lock = new Object();
	private UserCredentials credentials = null;
	private String userBasePath = null;

	public XfsBridge(PortletRequest request) throws PortletInitializationException {
		this.request = request;
		init();
	}

	private synchronized void init() throws PortletInitializationException {
		synchronized (client_lock) {
			if (clientWrapper == null || clientWrapper.getClient() == null) {
				setCredentials(request);
				try {
					String certFile = MosgridProperties.SERVICE_CERT_FILE_OVERRIDE.getProperty();
					if (certFile == null || "".equals(certFile))
						certFile = MosgridProperties.SERVICE_CERT_FILE.getProperty();
					String serviceCertPass = MosgridProperties.SERVICE_CERT_PASS.getProperty();
					this.userBasePath = MosgridProperties.GUSE_BASE_PATH.getProperty()
							+ MosgridProperties.REL_GUSE_USERS_PATH.getProperty();

					clientWrapper = new XfsClientWrapper(XtreemFSConnect.connectSAML(certFile, serviceCertPass));
					if (clientWrapper.getClient() == null) {
						throw new RuntimeException("Could not connect XtreemFS client!");
					}

					LOGGER.trace("XtreemFS client is connected");

					setVolume();

				} catch (Exception e) {
					LOGGER.info("Initialization failed.", e);
					throw new PortletInitializationException(e.getMessage(), e);
				}
			} else if (credentials == null) {
				setCredentials(request);
				setVolume();
			}
		}
	}

	private void setCredentials(PortletRequest request) throws PortletInitializationException {
		try {
			if (userBasePath == null || "".equals(userBasePath)) {
				credentials = XtreemFSConnect.createUserCredentialsSAML(request);
			} else {
				credentials = XtreemFSConnect.createUserCredentialsSAML(request, userBasePath);
			}
		} catch (CertificateException e) {
			LOGGER.error(e.getMessage(), e);
			throw new PortletInitializationException(e.getMessage(), e);
		}
	}

	private void setVolume() throws PortletInitializationException {
		try {
			Volume volume = XtreemFSConnect.getVolume(credentials, clientWrapper.getClient());
			VaadinXtreemFSSession.initVolume(credentials, volume);
		} catch (IOException e) {
			String msg = "Could not retrieve XtreemFS storage volume";
			LOGGER.error(msg, e);
			throw new PortletInitializationException(msg, e);
		} catch (CertificateException e) {
			String msg = "Could not retrieve XtreemFS storage volume";
			LOGGER.error(msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * Returns the home directory of the user i.e. i.e. 'xtreemfs://USER_HOME'
	 * 
	 * @return home directory
	 */
	public String getHomeDir() {
		return VaadinXtreemFSSession.getHomeDir();
	}

	/**
	 * Returns the url of the upload dir for input files i.e. i.e. 'xtreemfs://USER_HOME/UPLOADS'
	 */
	public String getUploadDir() {
		return getHomeDir() + "/" + XfsProperties.getProperty(XfsProperties.XFS_UPLOADS_DIR);
	}

	/**
	 * Returns the url of the results dir for input files i.e. 'xtreemfs://USER_HOME/RESULTS'
	 */
	public String getResultsDir() {
		return getHomeDir() + "/" + XfsProperties.getProperty(XfsProperties.XFS_RESULTS_DIR);
	}

	/**
	 * Returns a OutputStream to xtreemFS
	 * 
	 * @param filename
	 *            - name of the file to write
	 * @param path
	 *            - path in xtreemfs to write to
	 * @return - OutputStream to write to
	 * @throws IOException
	 */
	public OutputStream getUploadStream(String filename, String path) throws IOException {
		if (!path.endsWith("/")) {
			path = path + "/";
		}

		createDir(path);

		org.xtreemfs.common.clients.File file = getVolume().getFile(path + filename, getCredentials());
		return new BufferedFileOutputStream(file);
	}

	/**
	 * Creates directories in xtreemfs
	 * 
	 * @param path
	 *            - a string with a filesystem path (only directories, separated by "/") beginning with the home
	 *            directory of the user
	 * @throws IOException
	 */
	public void createDir(String path) throws IOException {
		LOGGER.debug("Creating path: " + path);
		String[] dirs = path.split("/");
		StringBuilder sb = new StringBuilder();
		for (String dir : dirs) {
			sb.append(dir);
			org.xtreemfs.common.clients.File file = getVolume().getFile(sb.toString(), getCredentials());
			if (!file.exists()) {
				file.mkdir(0755);
				LOGGER.trace("Created pathpart: " + sb);
			}
			sb.append("/");
		}
	}

	/**
	 * Checks whether given path exists
	 * 
	 * @param path
	 *            The path to be checked
	 * @return 'true' if path exists
	 * @throws IOException
	 */
	public boolean exists(String path) throws IOException {
		org.xtreemfs.common.clients.File file = getVolume().getFile(path, getCredentials());
		return file.exists();
	}

	/**
	 * Renames a file. Important for parameter sweep workflows were files need a trailing index i, e.g. "_i"
	 */
	public void rename(String filename, String newFilename, String path) throws IOException {
		LOGGER.trace("Trying to rename '" + filename + "' to '" + newFilename + "' in:\n\t" + path);
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		org.xtreemfs.common.clients.File oldFile = getVolume().getFile(path + filename, getCredentials());
		if (oldFile.exists()) {
			org.xtreemfs.common.clients.File newFile = getVolume().getFile(path + newFilename, getCredentials());
			oldFile.renameTo(newFile, getCredentials());
		} else {
			LOGGER.info("Failed to rename " + path + filename + ". File does not exist!");
		}
	}

	/**
	 * Returns a InputStream from xtreemFS
	 * 
	 * @param path
	 *            - path of file to read from
	 * @return - InputStream to read from
	 * @throws IOException
	 */
	public InputStream getDownloadStream(String path) throws IOException {
		org.xtreemfs.common.clients.File xfsFile = getVolume().getFile(path, getCredentials());
		FileInputStream bufFileIs = new FileInputStream(xfsFile);

		return bufFileIs;
	}

	/**
	 * Returns a buffered reader for a download stream
	 * 
	 * @param path
	 *            path of file to read from
	 * @return BufferedReader wrapped around InputStream from getDownloadStream
	 * @throws IOException
	 */
	public BufferedReader getDownloadStreamReader(String path) throws IOException {
		InputStream inputStream = getDownloadStream(path);
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
		return bufReader;
	}

	/**
	 * Returns a list of all files in the directory given by path
	 * 
	 * @param path
	 *            - path of the directory to list the files from
	 * @throws IOException
	 */
	public List<String> getFileList(String path) throws IOException {
		ArrayList<String> list = new ArrayList<String>();

		DirectoryEntry[] entries = getVolume().listEntries(path, getCredentials());
		if (entries == null)
			return list;
		for (DirectoryEntry e : entries) {
			list.add(e.getName());
		}

		return list;
	}

	/**
	 * List all child entries of given path. This may be much faster than retrieving File objects.
	 */
	public DirectoryEntry[] listEntries(String path) throws IOException {
		DirectoryEntry[] entries = getVolume().listEntries(path, getCredentials());
		if (entries == null) {
			entries = new DirectoryEntry[0];
		}
		return entries;
	}

	/**
	 * Checks whether given entry is a directory
	 */
	public boolean isDirectory(DirectoryEntry dirEntry) throws IOException {
		return XtreemFSConnect.isDirectory(dirEntry);
	}

	/**
	 * Returns the date of last modification as pretty string
	 */
	public String getLastModified(DirectoryEntry dirEntry) {
		return DATE_FORMAT.format(Long.valueOf(dirEntry.getStbuf().getMtimeNs() / 1000000L));
	}

	/**
	 * Returns the size of an entry as pretty string with size unit. If size is 0 an empty string is returned.
	 */
	public String getSize(DirectoryEntry dirEntry) {
		long sizeInByte = dirEntry.getStbuf().getSize();
		if (sizeInByte >= 1024) {
			long sizeInKB = sizeInByte / 1024;
			if (sizeInKB >= 1024) {
				long sizeInMB = sizeInKB / 1024;
				return sizeInMB + "MB";
			} else {
				return sizeInKB + " kB";
			}
		} else if (sizeInByte == 0) {
			return "";
		} else {
			return sizeInByte + " Byte";
		}
	}

	public Volume getVolume() {
		return VaadinXtreemFSSession.getVolume();
	}

	public UserCredentials getCredentials() {
		return VaadinXtreemFSSession.getCredentials();
	}

	public org.xtreemfs.common.clients.File getFile(String path) {
		org.xtreemfs.common.clients.File file = getVolume().getFile(path, getCredentials());
		return file;
	}

	public String getParent(String path) {
		int fromIndex = path.length() - 1;
		if (path.endsWith("/")) {
			fromIndex--;
		}
		int endIndex = path.lastIndexOf("/", fromIndex);

		return path.substring(0, endIndex);
	}

	/**
	 * Creates a XFS-URL for given path. All whitespaces are replaced by '%20'
	 * 
	 * @param path
	 *            XFS file path
	 * @return URL in the form of xtreemfs://path
	 */
	public static String createURL(String path) {
		return XFS_URL_PREFIX + path.replaceAll(" ", "%20");
	}

}
