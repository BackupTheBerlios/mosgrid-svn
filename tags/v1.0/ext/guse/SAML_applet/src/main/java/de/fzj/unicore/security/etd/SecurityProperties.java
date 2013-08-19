package de.fzj.unicore.security.etd;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * The SecurityProperties class to store security properties (public/private key,
 * certificate chain)
 * 
 * @author v.huber
 *
 */
public class SecurityProperties {
	
	private KeyStore keystore;
	private X509Certificate publicKey;
	private X509Certificate[] certChain;
	private PrivateKey privateKey;

	/**
	 * Constructs a security object for the specified keystore
	 * 
	 * @param filename
	 *            the name of the keystore file to read
	 * @param alias
	 *            the alias of the certificate to read
	 * @param password
	 *            the password to the keystore
	 * @return SecurityProperties object
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException 
	 * @throws UnrecoverableKeyException 
	 */
	public SecurityProperties (KeyStore keystore, String passwd, String alias) 
				throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {

		Key key = keystore.getKey(alias, passwd.toCharArray());
		
		if (key instanceof PrivateKey) {
			privateKey = (PrivateKey)key;
			Certificate cert = keystore.getCertificate(alias);

			if (cert instanceof X509Certificate) {
				publicKey = (X509Certificate) cert;
				Certificate[] certs = keystore.getCertificateChain(alias);
				certChain = new X509Certificate[certs.length];
				for (int i = 0; i < certs.length; i++) {
					if (certs[i] instanceof X509Certificate)
						certChain[i] = (X509Certificate) certs[i];
					else
						throw new KeyStoreException(
								"Invalid certificate in chain: " + certs[i]);
				}
			} 
			else
				throw new KeyStoreException("Invalid certificate for " + alias);

		} 
		else
			throw new KeyStoreException("Invalid key: " + key);
	}
	
	
	public X509Certificate getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(X509Certificate publicKey) {
		this.publicKey = publicKey;
	}

	public X509Certificate[] getCertChain() {
		return certChain;
	}

	public void setCertChain(X509Certificate[] certChain) {
		this.certChain = certChain;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	
	public void setKeystore(KeyStore keystore) {
		this.keystore = keystore;
	}


	public KeyStore getKeystore() {
		return keystore;
	}


	/**
	 * Checks the certificate validity and returns the number of days until it will be expired.
	 * @param cert
	 * @return
	 * @throws CertificateExpiredException
	 * @throws CertificateNotYetValidException
	 */
	public int getCertificateValidity() throws CertificateExpiredException, CertificateNotYetValidException {
		
		publicKey.checkValidity();
		

		/* Old calculation with some overhead: 
		GregorianCalendar t1 = new GregorianCalendar();
		t1.setTime(new Date());
		
		GregorianCalendar t2 = new GregorianCalendar();
		t2.setTime(publicKey.getNotAfter());
		
		int days = calculate(t1, t2);
		return days;
		*/ 
		Date now = new Date();
		long delta = publicKey.getNotAfter().getTime() - now.getTime();
		delta /= 24l * 60l * 60l * 500l;
		return (int) ((delta + 1l) / 2l);
	}
	
	
	
	
	/**
	 * Calculate the number of days between two dates.
	 * @param t1 the fist date
	 * @param t2 the second date
	 * @return the number of days from t1 to t2
	 */
	private static int calculate(GregorianCalendar t1, GregorianCalendar t2) {
		



	    /* Inaccurate old function:
		int ndays = 0;
		int n;

		if (t1.get(Calendar.YEAR) < t2.get(Calendar.YEAR)) {
			ndays += (366 - t1.get(Calendar.DAY_OF_YEAR));

			for (n = t2.get(Calendar.YEAR) + 1; n <= t2.get(Calendar.YEAR) - 1; n++) {
				ndays += 365;
			}
		}
		ndays += t2.get(Calendar.DAY_OF_YEAR);

		if (t2.get(Calendar.YEAR) == t1.get(Calendar.YEAR)) {
			ndays = t1.get(Calendar.DAY_OF_YEAR) - t2.get(Calendar.DAY_OF_YEAR);

		}

		return ndays;
	    */
	    
	    long delta = t2.getTime().getTime() - t1.getTime().getTime();
	    delta /= 24l * 60l * 60l * 500l;
	    return (int)((delta + 1l) / 2l);
	}

}
