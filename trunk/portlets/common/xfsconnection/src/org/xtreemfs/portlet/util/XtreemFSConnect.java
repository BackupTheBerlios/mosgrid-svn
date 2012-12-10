package org.xtreemfs.portlet.util;

import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.portlet.PortletRequest;

import org.globus.gsi.CertUtil;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xtreemfs.auth.plugin.SSLX509TrustManager;
import org.xtreemfs.common.clients.Client;
import org.xtreemfs.common.clients.File;
import org.xtreemfs.common.clients.Volume;
import org.xtreemfs.foundation.SSLOptions;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC.UserCredentials.Builder;
import org.xtreemfs.pbrpc.generatedinterfaces.GlobalTypes;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.Stat;

/**
 * Connection to XtreemFS can be made by either SAML assertions or Proxy Certificates.
 * 
 * To connect to XtreemFS using SAML assertions use: // Authentication: create secure connection String serviceCertFile
 * = ... // Path to host certificate on portal server as PKCS12 (.pkcs12) String serviceCertPass = ... // Password of
 * PKCS12-Container of host certificate on portal server Client c = XtreemFSConnect.connectSAML(serviceCertFile,
 * serviceCertPass);
 * 
 * // Authorization: create user credentials RenderRequest request = ... RPC.UserCredentials credentials =
 * XtreemFSConnect.createUserCredentialsSAML(request);
 * 
 * // Access a XtreemFS volume Volume v = XtreemFSConnect.getVolume(credentials, c);
 * 
 * // Read a file from XtreemFS String requestedFile = "..."; File f = v.getFile(requestedFile, credentials);
 * org.xtreemfs.portlet.util.BufferedFileInputStream(f);
 * 
 * // Write a file to XtreemFS String requestedFile = "..."; File f = v.getFile(requestedFile, credentials); new
 * org.xtreemfs.portlet.util.BufferedFileOutputStream(f);
 * 
 * // Read from a directory for (DirectoryEntry e : v.listEntries(getCurrentDir())) { System.out.println(e.getName +
 * " Directory " + XtreemFSConnect.isDirectory(e)); }
 * 
 * 
 * @author Patrick Schäfer (ZIB)
 * 
 */
public class XtreemFSConnect {

	public static final DateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

	/**
	 * Connect to XtreemFS using SAML Assertions contained in the temp/users/<userid>/x509up.assertion file.
	 * 
	 * SAML assertion support uses XtreemFS service certificates. Thus to create a connection, a valid
	 * service-certificate has to be provided. The service-certificate has to be in PKCS#12 format.
	 * 
	 * All access to the XtreemFS MRC goes through one TCP connection.
	 * 
	 * @param serviceCertFile
	 * @param serviceCertPass
	 * @return
	 * @throws Exception
	 */
	public static Client connectSAML(String serviceCertFile, String serviceCertPass) throws Exception {
		String type = SSLOptions.PKCS12_CONTAINER;
		return connect(serviceCertFile, serviceCertPass, type);
	}

	/**
	 * 
	 * Creates a UserCredentials from a SAML Assertion. The SAML assertion is extracted from the home folder of the
	 * liferay user.
	 * 
	 * @throws CertificateException
	 *             Throws an exception if the assertion can not be found or parsed
	 */
	public static RPC.UserCredentials createUserCredentialsSAML(PortletRequest request, String userDir)
			throws CertificateException {
		// remote user auslesen
		if (request.getRemoteUser() == null || request.getRemoteUser().equals("")) {
			throw new CertificateException("User not found!");
		}

		//set dir for current user
		if (userDir.endsWith("/")) {
			userDir += (request.getRemoteUser() + "/");
		} else {
			userDir += ("/" + request.getRemoteUser() + "/");
		}

		java.io.File dir = new java.io.File(userDir);
		if (dir != null && dir.exists()) {
			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept(java.io.File file) {
					return file.getName().startsWith("x509up");
				}
			};
			java.io.File[] assertionFiles = dir.listFiles(filter);
			if (assertionFiles == null || assertionFiles.length == 0) {
				String message = "No assertion-candidates could be found.\n";
				message += "To use the computational chemistry portlets, please make sure that an assertion is generated in the certificate portlet.\n";
				message += "If you are sure that you have a valid assertion uploaded, then please contact support";
				throw new CertificateException(message);
			} else {
				String validAssertion = null;
				Assertion assertion;
				for (java.io.File file : assertionFiles) {
					try {
						if (!file.isFile())
							continue;
						assertion = readAssertionFromFile(file.getAbsolutePath());
						DateTime notBefore = assertion.getConditions().getNotBefore();
						DateTime notAfter = assertion.getConditions().getNotOnOrAfter();
						if (notBefore.isBeforeNow() && notAfter.isAfterNow()) {
							validAssertion = file.getAbsolutePath();
							break;
						}
					} catch (CertificateException e) {
						// assertion could not be parsed... ignore
						continue;
					}
				}
				if (validAssertion == null) {
					String message = "Could not find a valid SAML-Assertion.\n";
					message += "Has your assertion expired?\n";
					message += "To use the computational chemistry portlets, please make sure that a valid assertion is generated in the certificate portlet.\n";
					message += "If you are sure that you have a valid assertion uploaded, then please contact support";
					throw new CertificateException(message);					
				}
				// assertion
				return createUserCredentialsSAML(validAssertion);
			}
		} else {
			String message = "UserDir could not be found on server.\n";
			message += "To use the computational chemistry portlets, please make sure that an assertion is generated in the certificate portlet.\n";
			message += "If you are sure that you have a valid assertion uploaded, then please contact support";
			throw new CertificateException(message);
		}
	}

	/**
	 * 
	 * Creates a UserCredentials from a SAML Assertion. The SAML assertion is extracted from the home folder of the
	 * liferay user.
	 * 
	 * @throws CertificateException
	 *             Throws an exception if the assertion can not be found or parsed
	 */
	public static RPC.UserCredentials createUserCredentialsSAML(PortletRequest request) throws CertificateException {
		String userDir = XfsProperties.getProperty(XfsProperties.GUSE_BASE_PATH)
				+ XfsProperties.getProperty(XfsProperties.REL_USER_PATH);
		return createUserCredentialsSAML(request, userDir);
	}

	/**
	 * Connect to XtreemFS using Proxy Certificates contained in the temp/users/<userid>/x509up file.
	 * 
	 * The proxy certificate has to be in PEM-format.
	 * 
	 * Each proxy certificate has to build up a new TCP connection XtreemFS.
	 * 
	 * @param proxyCertFile
	 * @param proxyCertPass
	 * @return
	 * @throws Exception
	 */
	public static Client connectProxyCert(String proxyCertFile, String proxyCertPass) throws Exception {
		// connect using proxy-cert
		String type = SSLOptions.PEM_CONTAINER;
		return connect(proxyCertFile, proxyCertPass, type);
	}

	/**
	 * Creates a UserCredentials from a SAML Assertion.
	 * 
	 * @param assertionFile
	 * @return
	 * @throws CertificateException
	 */
	public static RPC.UserCredentials createUserCredentialsSAML(String assertionFile) throws CertificateException {
		try {
			if (assertionFile != null && !assertionFile.equals("")) {

				// auf Assertion prüfen
				String dn = XtreemFSConnect.getDnFromAssertion(assertionFile);

				// append groups and dn
				Builder builder = RPC.UserCredentials.newBuilder().setUsername(dn);
				for (String group : extractGroupsFromDN(dn.split(","))) {
					builder.addGroups(group);
				}
				return builder.build();
			}
		} catch (Exception e) {
			throw new CertificateException(e.getLocalizedMessage());
		}
		throw new CertificateException("No valid SAML assertion found");
	}

	/**
	 * Creates a UserCredential from a Proxy Certificate.
	 * 
	 * @param proxyCertFile
	 * @return
	 * @throws CertificateException
	 */
	public static RPC.UserCredentials createUserCredentialsProxyCert(String proxyCertFile) throws CertificateException {
		// load proxy certificate
		try {
			X509Certificate[] certs = CertUtil.loadCertificates(proxyCertFile);
			X509Certificate cert = certs[0];

			// check, if the cert is still valid
			Date date = new Date();
			if (date.before(cert.getNotBefore()) || date.after(cert.getNotAfter())) {
				throw new CertificateException("Certificate expired. Please obtain a new proxy certificate.");
			}

			String dn = cert.getIssuerDN().getName();

			// append groups and dn
			Builder builder = RPC.UserCredentials.newBuilder().setUsername(dn);
			for (String group : extractGroupsFromDN(dn.split(","))) {
				builder.addGroups(group);
			}
			return builder.build();
		} catch (Exception e) {
			throw new CertificateException(e.getLocalizedMessage());
		}
	}

	/**
	 * Extrahiert aus dem zerlegten DN-Namen die Gruppen. Gruppen werden anhand des "ou="-Eintrags bestimmt.
	 */
	public static ArrayList<String> extractGroupsFromDN(String[] parts) {
		ArrayList<String> groups = new ArrayList<String>();
		for (String part : parts) {
			if (part != null) {
				part = part.trim();
				if (part != null && part.toLowerCase().startsWith("ou=")) {
					String group = part.substring(3, part.length());
					groups.add(group);
				}
			}
		}
		return groups;
	}

	/**
	 * Creates a connection to XtreemFS. Should be called either using SAML or Proxy-Certs.
	 * 
	 * @param certFile
	 * @param certPass
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected static Client connect(String certFile, String certPass, String type) throws Exception {

		// read the trusted.jks from within the jar
		InputStream trustedCAstream = null;
		try {
			trustedCAstream = new FileInputStream("trusted_xtreemfs.jks");
		} catch (Exception e) {
			trustedCAstream = XtreemFSConnect.class.getResourceAsStream("include/trusted_xtreemfs.jks");
			if (trustedCAstream == null) {
				trustedCAstream = XtreemFSConnect.class.getResourceAsStream("/include/trusted_xtreemfs.jks");
				if (trustedCAstream == null) {
					trustedCAstream = XtreemFSConnect.class.getClassLoader().getResourceAsStream(
							"include/trusted_xtreemfs.jks");
					if (trustedCAstream == null) {
						trustedCAstream = XtreemFSConnect.class.getClassLoader().getResourceAsStream(
								"/include/trusted_xtreemfs.jks");
						if (trustedCAstream == null) {
							trustedCAstream = XtreemFSConnect.class.getClassLoader().getResourceAsStream(
									"trusted_xtreemfs.jks");
							if (trustedCAstream == null) {
								trustedCAstream = XtreemFSConnect.class.getClassLoader().getResourceAsStream(
										"/trusted_xtreemfs.jks");
								if (trustedCAstream == null) {
									trustedCAstream = XtreemFSConnect.class.getResourceAsStream("trusted_xtreemfs.jks");
									if (trustedCAstream == null) {
										trustedCAstream = XtreemFSConnect.class
												.getResourceAsStream("/trusted_xtreemfs.jks");
										if (trustedCAstream == null) {
											throw new CertificateException(
													"Could not find truststore: trusted_xtreemfs.jks!");
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// SSLOptionen für XtreemFS: gridssl
		String trustedCAsPass = XfsProperties.getProperty(XfsProperties.XFS_PASS);
		SSLOptions sslOptions = new SSLOptions(new FileInputStream(certFile), certPass, type, trustedCAstream,
				trustedCAsPass, SSLOptions.JKS_CONTAINER, true, true, new SSLX509TrustManager());

		// Connection Timeout nach 2 Stunden
		String url = XfsProperties.getProperty(XfsProperties.MRC_URL);
		int port = Integer.parseInt(XfsProperties.getProperty(XfsProperties.MRC_PORT));
		InetSocketAddress dirAddr = new InetSocketAddress(url, port);
		Client c = new Client(new InetSocketAddress[] { dirAddr }, 60 * 1000, 120 * 1000, sslOptions);
		c.start();

		return c;
	}

	/**
	 * 
	 * Retrieves the volume and creates a user home directory in XtreemFS based on the credentials user-name, i.e. DN of
	 * the proxy-cert or SAML assertion.
	 * 
	 * @param credentials
	 * @param c
	 * @return
	 * @throws IOException
	 */
	public static Volume getVolume(RPC.UserCredentials credentials, Client c) throws IOException {

		// Volume öffnen
		String volumeName = XfsProperties.getProperty(XfsProperties.VOLUME_NAME);
		Volume v = c.getVolume(volumeName, credentials);

		// Prüfen, ob Simulations-Verzeichnis existiert
		checkForHomeDir(credentials.getUsername(), v, credentials);

		return v;
	}

	/**
	 * Checks if the home dir exists. Otherwise a directory corresponding to the DN of the user is created.
	 */
	protected static void checkForHomeDir(String homeDir, Volume v, RPC.UserCredentials credential) throws IOException {
		// prüfen, ob Simulations-ID-Verzeichnis existiert
		if (homeDir != null && !homeDir.equals("")) {
			if (!homeDir.endsWith("/")) {
				homeDir = homeDir + "/";
			}
			if (!homeDir.startsWith("/")) {
				homeDir = "/" + homeDir;
			}

			File simDirectory = v.getFile(homeDir, credential);
			if (!simDirectory.exists()) {
				simDirectory.mkdir(0700); // Verzeichnis anlegen
			}
		}
	}

	/**
	 * Check if the provided DirectoryEntry is a directory.
	 * 
	 * @see java.io.File
	 * @return true if it is a directory, false otherwise (also if path does not exist)
	 */
	public static boolean isDirectory(DirectoryEntry e) throws IOException {
		if (e.getStbuf() != null) {
			return (e.getStbuf().getMode() & GlobalTypes.SYSTEM_V_FCNTL.SYSTEM_V_FCNTL_H_S_IFDIR.getNumber()) > 0;
		} else {
			return false;
		}
	}

	/**
	 * Extracts the SAML Assertion from the given File
	 * 
	 * @throws CertificateException
	 * 
	 */
	protected static Assertion readAssertionFromFile(String filename) throws CertificateException {
		try {
			DefaultBootstrap.bootstrap();
			InputStream in = new java.io.FileInputStream(filename);

			BasicParserPool pool = new BasicParserPool();
			pool.setNamespaceAware(true);

			Document doc = pool.parse(in);
			Element rootElement = doc.getDocumentElement();

			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(rootElement);

			Assertion assertion = (Assertion) unmarshaller.unmarshall(rootElement);
			assertion.validate(true);

			// AssertionMarshaller marshaller = new AssertionMarshaller();
			// Element assertionElement = marshaller.marshall(assertion);
			// System.out.println(XMLHelper.prettyPrintXML(assertionElement));

			return assertion;
		} catch (Exception e) {
			throw new CertificateException(e.getLocalizedMessage());
		}
	}

	/**
	 * Extracts the DN from an assertion.
	 * 
	 * @param filename
	 * @return
	 * @throws CertificateException
	 *             if the assertion is not valid or does not exist
	 */
	public static String getDnFromAssertion(String filename) throws CertificateException {
		Assertion assertion = readAssertionFromFile(filename);
		if (assertion != null) {
			return assertion.getIssuer().getValue();
		} else {
			throw new CertificateException("No valid SAML assertion found");
		}
	}

	/**
	 * Remove one CN-Entry from the DN. This is required for Proxy-Certificates, as those add one CN-Entry to the DN.
	 */
	public static String removeFirstCnFromDN(String distinguishedName, String[] parts) {
		// Prüfen Anzahl an CN= Einträgen
		int count = 0;
		for (String p : parts) {
			if (p.trim().toLowerCase().startsWith("cn=")) {
				count++;
			}
		}

		// entferne den ersten cn= Eintrag
		if (count > 1) {
			String dnName = "";
			for (int i = count - 1; i < parts.length; i++) {
				dnName += parts[i].trim() + ",";
			}
			return dnName.substring(0, dnName.length() - 1);
		} else {
			return distinguishedName;
		}
	}

	/**
	 * Returns the date in a human readable - format
	 */
	public static String formatDate(Stat e) {
		return format.format(e.getMtimeNs() / 1000000);
	}

	/**
	 * Returns the file size in KiB.
	 */
	public static String formatSize(Stat e) {
		return Math.ceil(e.getSize() / 1024.0) + "KiB";
	}
}
