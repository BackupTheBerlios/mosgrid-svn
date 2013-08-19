/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2008 MTA SZTAKI
 *
 */

package hu.sztaki.lpds.pgportal.services.credential;

import org.apache.log4j.Logger;
import org.globus.gsi.*;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.gssapi.*;

import org.globus.myproxy.*;
import org.ietf.jgss.*;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.security.auth.login.CredentialException;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;

public class SZGCredentialManager {

	private SZGCredentialStorage credStore = null;
	private String message = "No message.";
	volatile protected static Logger log = Logger.getLogger(SZGCredentialManager.class);


	/**
	 * Constructor.
	 * 
	 * @param host
	 *            MyProxy server hostname.
	 * @param port
	 *            MyProxy server port.
	 */
	private SZGCredentialManager() {
		this.credStore = SZGCredentialStorageFactory.getSZGCredentialStorage();
	}

	private static SZGCredentialManager instance = null;

	public static SZGCredentialManager getInstance() {
		if (SZGCredentialManager.instance == null)
			SZGCredentialManager.instance = new SZGCredentialManager();
		return SZGCredentialManager.instance;
	}

	public void setLog(Logger log) {
		//this.log = log;
	}

    public void uploadToServerInputStream(InputStream portalCertFile,
            InputStream portalKeyFile, String keyPass, String username,
            String passphrase, int lifetime, String url, int port, boolean useDN,boolean RFCEnabled)
            throws SZGCredentialException, MyProxyException, GSSException {
        GSSCredential cred = null;
        try {
            try {
                if (RFCEnabled)
                cred = this.loadCredentialFromInputStreams(portalCertFile, portalKeyFile, keyPass,true);
                else
                cred = this.loadCredentialFromInputStreams(portalCertFile, portalKeyFile, keyPass,false);

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                throw new SZGCredentialException("Cannot decode key:" + e.getMessage());
            }
            if (cred == null) {
                throw new SZGCredentialException("Credential is null after CredentialManager.loadCredentialFromInputStream().");
            }

            MyProxy myproxy = new MyProxy(url, port);

            InitParams initP = new InitParams();

            initP.setLifetime(lifetime);
            if (useDN) {
                initP.setUserName(cred.getName().toString().replaceAll("/E=", "/emailAddress="));
                myproxy.put(cred, initP);
            } else {
                initP.setUserName(username);
                initP.setPassphrase(passphrase);
                myproxy.put(cred, username, passphrase, lifetime);
            }
            cred.dispose();
        } catch (SZGCredentialException cex) {
            try {
                if (cred != null) {
                    cred.dispose();
                }
            } catch (GSSException gex) { /* ignore !!! */
            }
            throw cex;
        } catch (MyProxyException mex) {
            try {
                if (cred != null) {
                    cred.dispose();
                }
            } catch (GSSException gex) { /* ignore !!! */
            }
            throw mex;
        } catch (GSSException gex) {
            try {
                if (cred != null) {
                    cred.dispose();
                }
            } catch (GSSException gex2) { /* ignore !!! */
            }
            throw gex;
        }
    }

    /**
     * Downloads delegated credential from MyProxy server.
     *
     * @param username
     *            The username of the credential to retrieve.
     * @param passphrase
     *            The passphrase of the credential to retrieve.
     * @param lifetime
     *            The requested lifetime of the retrieved credential.
     */

    public void downloadFromServer(String username, String passphrase,
            int lifetime, SZGStoreKey key, String description, String url, int port) throws Exception {
        MyProxy myProxy = new MyProxy(url, port);

        GSSCredential cred = myProxy.get(null, username, passphrase, lifetime);

        SZGCredential szgCred = new SZGCredential(cred, myProxy.getHost(), description, key.getCredId());
        this.credStore.put(szgCred, key);
        if (this.getCredentials(key.getUserId()).length == 1) {
            this.credStore.setUseThis(key);
        }

    }



//    public void downloadFromServer(String username, String passphrase,
//            int lifetime, SZGStoreKey key, String description, String url, int port) throws Exception {
//        WaitElement downloaded = new WaitElement();
//        MyProxy myProxy = new MyProxy(url, port);
//        DownloadThread credDownloadThread = new DownloadThread(username, passphrase, lifetime, myProxy, downloaded, log);
//        credDownloadThread.start();
//        TimeOutTask task = new TimeOutTask(credDownloadThread, downloaded, log);
//        Date date = new Date();
//        date.setTime(date.getTime() + timeout);
//        //timerService.schedule(key.toString(), task, date); //TimerService
//        System.out.println("SZGCredentialManager.downloadFromServer() --> wait()");
//        downloaded.isWait();
//        //timerService.cancel(key.toString());//TimerService
//        System.out.println("SZGCredentialManager.downloadFromServer() --> Thread finished!");
//        GSSCredential cred = credDownloadThread.getProxy();
//        if (cred == null) {
//            System.out.println("SZGCredentialManager.downloadFromServer ERROR:" + credDownloadThread.getMessage());
//            throw new Exception(credDownloadThread.getMessage());
//        }
//        SZGCredential szgCred = new SZGCredential(cred, myProxy.getHost(), description, key.getCredId());
//        this.credStore.put(szgCred, key);
//        if (this.getCredentials(key.getUserId()).length == 1) {
//            this.credStore.setUseThis(key);
//        }
//
//    }

	public synchronized Object[] loadFromFile(InputStream credfile,
			String host, int lifetime, SZGStoreKey key, String description) {
		try {
			GlobusCredential hostCred = new GlobusCredential(credfile);

			GSSCredential cred = new GlobusGSSCredentialImpl(hostCred,
					GSSCredential.INITIATE_AND_ACCEPT);

			SZGCredential szgCred = new SZGCredential(cred, host, description,
					key.getCredId());
			this.credStore.put(szgCred, key);
			if (this.getCredentials(key.getUserId()).length == 1)
				this.credStore.setUseThis(key);
			this.message = "Load successful.";
			return new Object[] { new Boolean(true), null };
		} catch (Exception e) {
			System.out.println("SZGCredentialManager.loadFromFile ERROR:"
					+ this.getCredential(key));
			this.message = "Load failed. ";
			e.printStackTrace();
			return new Object[] { new Boolean(false), e };

		}
	}

	/**
	 * Removes locally stored credential.
	 * 
	 * @return True on success.
	 */
	public boolean removeCredential(SZGStoreKey key) {
		String userId = key.getUserId();
		SZGCredential used = this.getUseThis(userId);
		if (used.getId().equals(key.getCredId())) {
			SZGCredential[] creds = this.getCredentials(userId);
			if (creds.length != 1) {
				for (int i = 0; i < creds.length; i++) {
					if (!creds[i].getId().equals(used.getId())) {
						this.setUseThis(new SZGStoreKey(userId, creds[i]
								.getId()));
						break;
					}
				}
			}
		}
		try {
			this.credStore.remove(key);
			return true;
		} catch (SZGCredentialException cex) {
			cex.printStackTrace();
			return false;
		}
	}

	/**
	 * Gets locally stored credential belonging to the portalUser.
	 * 
	 * @return The portaluser's credential.
	 */
	public SZGCredential getCredential(SZGStoreKey key) {
		try {
			return this.credStore.get(key);
		} catch (SZGCredentialException cex) {
			cex.printStackTrace();
			return null;
		}
	}

	/**
	 * Flexibility precaution - for when multiple credentials per key are
	 * stored.
	 * 
	 * @param storeKey
	 * @return null If no credential is available on the portal.
	 */
	public SZGCredential[] getCredentials(String userId) {

		try {
			return this.credStore.getCredentials(userId);
		} catch (SZGCredentialException cex) {
			cex.printStackTrace();
			return null;
		}
	}

	/**
         * GUSE
	 * Flexibility precaution - for when multiple credentials per key are
	 * stored.
	 *
	 * @param storeKey
	 * @return null If no credential is available on the portal.
	 */
        public Vector getCredentialsList(String userId) {
            Vector re = new Vector();
            try {
                for (SZGCredential c : this.credStore.getCredentials(userId)) {
                        HashMap cm = new HashMap();
                        cm.put("id", c.getId());
                        cm.put("issuer", c.getIssuer());
                        cm.put("tleft", c.getTimeLeft());
                        cm.put("set", getSetGridsForCredential(userId, c.getId()));
                        re.add(cm);
                }
            } catch (Exception cex) {//no creds set
                return null;
            }
            return re;
        }

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Loads credential from files.
	 * 
	 * @param portalCertFile
	 *            The absolute path of the certificate file.
	 * @param portalKeyFile
	 *            The absolute path of the UNENCRYPTED key file.
	 * @param key
	 *            The key to be used to store the loaded credential.
	 * @return
	 * @throws CredentialException
	 */
	private GSSCredential loadCredentialFromInputStreams(InputStream certFile,
			InputStream keyFile, String pass,boolean RFCenabled) throws GeneralSecurityException {
            
            int proxytype  = 0;
            if (RFCenabled){
                proxytype = GSIConstants.GSI_4_IMPERSONATION_PROXY;
            }
            else{
                proxytype = GSIConstants.GSI_2_PROXY;
            }

            
            BouncyCastleCertProcessingFactory certFactory = BouncyCastleCertProcessingFactory.getDefault();
		try {
                        X509Certificate cert = CertUtil.loadCertificate(certFile);
                        
			OpenSSLKey key = new BouncyCastleOpenSSLKey(keyFile);
            
                        //GlobusCredential hostCred = new GlobusCredential(key.getPrivateKey(), new X509Certificate[] { cert });

                        GSSCredential cred = null;
            
                    
			if (key.isEncrypted()) {
				key.decrypt(pass);
			}
                        Date enddate = cert.getNotAfter();
                        Date now = new Date();
                        System.out.println("ENDDATE IS : " + enddate.toString());

                        int lifetime = (int)((enddate.getTime() - now.getTime())/1000);
                        System.out.println("lifetime in sec is : " + lifetime);
			GlobusCredential hostCred = certFactory.createCredential(new X509Certificate[]{cert},key.getPrivateKey(),512, lifetime, proxytype);
			cred = new GlobusGSSCredentialImpl(hostCred,GSSCredential.INITIATE_AND_ACCEPT);

                    
                    return cred;
		} catch (GSSException e) {
			e.printStackTrace(); // "Unable to create portal credential!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SZGCredential getUseThis(String userId) {
		return this.credStore.getUseThis(userId);
	}

	public void setUseThis(SZGStoreKey key) {
		this.credStore.setUseThis(key);
	}

	public void setCredentialForGrid(String userId, String credId,
			String gridName) {
		this.credStore.setCredentialForGrid(userId, credId, gridName);
	}

	public SZGCredential getCredentialForGrid(String userId, String gridName) {
		return this.credStore.getCredentialForGrid(userId, gridName);
	}

	public String[] getSetGridsForCredential(String userId, String credId,
			boolean returnArray) {
		String[] gs = this.credStore.getSetGridsForCredential(userId, credId);
		if (gs == null) {
			return null;
		}
		if (returnArray) {
			return gs;
		} else {
			StringBuffer gsVal = new StringBuffer("");
			if (gs != null && gs.length != 0) {
				for (int i = 0; i < gs.length; i++) {
					gsVal.append(gs[i]);
					if (i != (gs.length - 1)) {
						gsVal.append(", ");
					}
				}
			}
			return new String[] { gsVal.toString() };
		}
	}

	/**
	 * Givs grids for credential. GUSE
	 *
	 * @param userId
	 *            userId
	 * @param credId
	 *            credId
	 * @return 
         *            grid list, if empty: ""
	 */
    public String getSetGridsForCredential(String userId, String credId) {
        String[] gs = this.credStore.getSetGridsForCredential(userId, credId);
        if (gs == null) {
            return "";
        }
        StringBuffer gsVal = new StringBuffer("");
        if (gs != null && gs.length != 0) {
            for (int i = 0; i < gs.length; i++) {
                gsVal.append(gs[i]);
                if (i != (gs.length - 1)) {
                    gsVal.append(", ");
                }
            }
        }
        return gsVal.toString();
    }

    /**
     * Get Information about credential from Myproxy server. GUSE
     *
     * @param host
     * @param port
     * @param login
     * @param pass
     *
     * @return HashMap {desc=Description, name=Name, owner=Owner, sDate=StartTimeAsDate, eDate=EndTimeAsDate}
     */
    public HashMap MyProxyInfo(String host, int port, String login, String pass) throws MyProxyException {
        HashMap ret = new HashMap();
        MyProxy mProxy = new MyProxy(host, port);
        GetParams params = new GetParams(login, pass);
        GSSCredential cred = mProxy.get(null, params);
        CredentialInfo inf = mProxy.info(cred, login, pass);

        if (inf.getDescription() != null) {
            ret.put("desc", inf.getDescription());
        } else {
            ret.put("desc", "");
        }
        if (inf.getName() != null) {
            ret.put("name", inf.getName());
        } else {
            ret.put("name", "");
        }
        if (inf.getOwner() != null) {
            ret.put("owner", inf.getOwner());
        } else {
            ret.put("owner", "");
        }
        ret.put("sDate", inf.getStartTimeAsDate().toString());
        ret.put("eDate", inf.getEndTimeAsDate().toString());
        return ret;
    }

    /**
     * Change credential's password in Myproxy server. GUSE
     *
     * @param host
     * @param port
     * @param login
     * @param pass
     * @param newPass
     */
    public void MyProxyChangePassword(String host, int port, String login, String pass, String newPass) throws MyProxyException {
        MyProxy mProxy = new MyProxy(host, port);
        GetParams params = new GetParams(login, pass);
        GSSCredential cred = mProxy.get(null, params);
        ChangePasswordParams chParams = new ChangePasswordParams();
        chParams.setUserName(login);
        chParams.setPassphrase(pass);
        chParams.setNewPassphrase(newPass);
        mProxy.changePassword(cred, chParams);
    }

    /**
     * Destroy credential in Myproxy server. GUSE
     *
     * @param host
     * @param port
     * @param login
     * @param pass
     */
    public void MyProxyDestroy(String host, int port, String login, String pass) throws MyProxyException {
        MyProxy mProxy = new MyProxy(host, port);
        GetParams params = new GetParams(login, pass);
        GSSCredential cred = mProxy.get(null, params);
        mProxy.destroy(cred, login, pass);
    }


	public void finalize() throws java.lang.Throwable {
		System.out.println("SZGCredentialManager.finalize() called.");
		super.finalize();
	}

}
