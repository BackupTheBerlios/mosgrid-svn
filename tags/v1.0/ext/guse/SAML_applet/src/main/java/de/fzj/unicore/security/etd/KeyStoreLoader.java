package de.fzj.unicore.security.etd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.apache.log4j.Logger;

/**
 * Loader class for keystore in PKCS12/JKS/JCEKS formats.
 * @author v.huber
 *
 */
public class KeyStoreLoader {
	
	private static Logger logger = Logger.getLogger(KeyStoreLoader.class);
	
	
	/**
	 * Load the keystore in PKCS12, JKS, JCEKS formats.
	 * @param file keystore file
	 * @param passwd password of the keystore
	 * @return keystore object
	 * @throws KeyStoreException
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public static KeyStore loadKeyStore(File file, String passwd) 
	    throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException  {
	    if (passwd == null) {
		throw new IOException("Keystore password must not be `null'");
	    } 
	    /*	    else {
		logger.trace("File: " + file);
		logger.trace("Pass: " + passwd);
	    }
	    */
	    try {			
		return loadKeyStore(file, passwd, "PKCS12");
	    }
	    catch(IOException e1) {
		logger.debug("loading failed",e1);
		try {
		    return loadKeyStore(file, passwd, "JKS");	
		}
		catch(IOException e2) {
		    logger.debug("loading failed",e2);
		    try {
			return loadKeyStore(file, passwd, "JCEKS");	
		    }
		    catch(IOException e3) {
			logger.debug("loading failed",e3);
			throw new IOException("Invalid keystore format or password was incorrect");
		    }
		}
	    }
	}
	
	
	/**
	 * Tries to load the keystore in the specified format.
	 * @param file keystore file
	 * @param passwd password of the keystore
	 * @param type type of the keystore
	 * @return keystore object
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static KeyStore loadKeyStore(File file, String passwd, String type) 
	    throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
		
		KeyStore keystore = null;
		FileInputStream inStream = null;
		if (!file.exists()) {
			throw new FileNotFoundException("Keystore " + file.getCanonicalPath() + " not found"); 
		}
		try {
			logger.info("Trying loading keystore " + file.getCanonicalPath() 
					+ "  in "+ type + " format ...");
			
			keystore = KeyStore.getInstance(type);
			inStream = new FileInputStream(file);
			keystore.load(inStream, passwd.toCharArray());	
		}
		finally {
			if (inStream != null) {
				try { inStream.close(); } 
				catch (IOException e) { }
			}
		}
		return keystore;
	}
}
