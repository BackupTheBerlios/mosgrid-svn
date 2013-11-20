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


import dci.data.Middleware;
import dci.data.Certificate;
import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.InformationBase;
import java.util.*;
import java.io.*;
import org.gridforum.jgss.*;
import org.globus.gsi.gssapi.*;
import hu.sztaki.lpds.information.local.PropertyLoader;

class SZGCredentialMemoryStore implements SZGCredentialStorage {

	private Hashtable<String, SZGCredential[]> store = null;
	private Hashtable<String, SZGStoreKey> useThisStore = null;
	private static SZGCredentialMemoryStore instance = null;
	private Hashtable<String, SZGStoreKey> credentialMappingsG2C = null;

	// value for the loaded property: 'credential.proxy.save.dir'
	private String proxySaveDir = null;
	// value for the loaded property: 'credential.proxy.name'
	private String proxyName = null;

	private SZGCredentialMemoryStore() {
		this.store = new Hashtable<String, SZGCredential[]>();
		this.useThisStore = new Hashtable<String, SZGStoreKey>();
		this.credentialMappingsG2C = new Hashtable<String, SZGStoreKey>();
		try {
			this.loadProperties();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected static SZGCredentialMemoryStore getInstance() {
		if (instance == null)
			instance = new SZGCredentialMemoryStore();
		return instance;
	}

	/**
	 * Stores credential using the key. If there is already a credential under<br>
	 * that key, than it will be overwritten by this one. If this is the first
	 * credential of the user, than<br>
	 * it will be selected for usage (setUseThis).
	 * 
	 * @param credential
	 * @param key
	 */
	public synchronized void put(SZGCredential cred, SZGStoreKey key)
			throws SZGCredentialException {
		if (cred == null) {
			throw new SZGCredentialException("Called with null credential!");
		}
		SZGCredential[] alreadyIn = this.getCredentials(key.getUserId());
		if (alreadyIn == null) {
			SZGCredential[] creds = new SZGCredential[1];
			creds[0] = cred;
			this.store.put(key.getUserId(), creds);
		} else {
			for (int i = 0; i < alreadyIn.length; i++)
				if (alreadyIn[i].getId().equals(cred.getId()))
					return;
			SZGCredential[] extended = new SZGCredential[alreadyIn.length + 1];
			int i = 0;
			for (; i < alreadyIn.length; i++) {
				extended[i] = alreadyIn[i];
			}
			extended[i] = cred;
			this.store.put(key.getUserId(), extended);
		}
	}

	/**
	 * Gets credential belonging to the given key.
	 * 
	 * @param key
	 * @return Credential, or null if credential not found with this key.
	 */
	public SZGCredential[] getCredentials(String userId) {
		return this.store.get(userId);
	}

	/**
	 * Gets credential belonging to the given key.
	 * 
	 * @param key
	 * @return Credential, or null if credential not found with this key.
	 */
	public SZGCredential get(SZGStoreKey key) {
		SZGCredential[] userCreds = this.getCredentials(key.getUserId());
		if (userCreds == null) {
			return null;
		}
		for (int i = 0; i < userCreds.length; i++) {
			SZGCredential cred = userCreds[i];
			if (cred.getId().equals(key.getCredId().toString())) {
				return cred;
			}
		}
		return null;
	}

	public void remove(SZGStoreKey key) {
		SZGCredential[] userCreds = this.getCredentials(key.getUserId());
		if (userCreds == null) {
			return;
		}
		if (userCreds.length == 1) {
			this.store.remove(key.getUserId());
			return;
		}
		SZGCredential[] newStore = new SZGCredential[userCreds.length - 1];
		int j = 0;
		for (int i = 0; i < userCreds.length; i++) {
			SZGCredential cred = userCreds[i];
			if (!(cred.getId().equals("" + key.getCredId()))) {
				newStore[j] = cred;
				j++;
			} else {
			}
		}
		this.store.remove(key.getUserId());
		this.store.put(key.getUserId(), newStore);
	}

	/**
	 * Removes credential belonging to the given key.
	 * 
	 * @param key
	 */
	public synchronized void removeCredentials(String userId) {
		this.store.remove(userId);
	}

	public synchronized void setUseThis(SZGStoreKey key) {
		this.useThisStore.put(key.getUserId(), key);
		// save proxy to a file
		String userId = (String) key.getUserId();
		String absFilePath = this.proxySaveDir + "/" + userId + "/"
				+ this.proxyName;
		this.saveProxyToFile(this.get(key), absFilePath);
	}

	private synchronized boolean saveProxyToFile(SZGCredential cred,
			String absFilePath) {
		try {
			GlobusGSSCredentialImpl gCred = (GlobusGSSCredentialImpl) cred
					.getGSSCredential();
			byte[] data = gCred.export(ExtendedGSSCredential.IMPEXP_OPAQUE);
			File proxy = new File(absFilePath);
			if (!proxy.getParentFile().exists()) {
				if (!proxy.getParentFile().mkdirs()) {
					return false;
				}
			}
			if (proxy.exists()) {
				proxy.delete();
			}
			proxy.createNewFile();
			FileOutputStream out = new FileOutputStream(proxy);
			out.write(data);
			out.close();
			Process p = Runtime.getRuntime().exec(
					"/bin/chmod 400 " + proxy.getAbsolutePath());
			p.waitFor();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return false;
		}
	}

	private void loadProperties() throws Exception {
		try {
			this.proxySaveDir = PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/";
			this.proxyName = "x509up";//pl.getProperty("credential.proxy.name");
		} catch (Exception ex) {
			throw ex;
		}
	}

	public synchronized SZGCredential getUseThis(String userId) {
		if (this.useThisStore.get(userId) == null) {
			return null;
		} else {
			return (SZGCredential) this.get((SZGStoreKey) this.useThisStore
					.get(userId));
		}
	}

	public synchronized void setCredentialForGrid(String userId, String certId,
			String gridName) {
		SZGStoreKey key = new SZGStoreKey(userId, certId);
		SZGCredential cred = this.get(key);
		this.credentialMappingsG2C.put(gridName + ".$_ % _$." + userId, key);
		String absFilePath = this.proxySaveDir + "/" + userId + "/"
				+ this.proxyName + "." + gridName;
		this.saveProxyToFile(cred, absFilePath);
	}

	public SZGCredential getCredentialForGrid(String userId, String gridName) {
		SZGStoreKey key = (SZGStoreKey) this.credentialMappingsG2C.get(gridName
				+ ".$_ % _$." + userId);
		if (key == null) {
			return null;
		}
		// this is null if the credential have been removed
		return this.get(key);
	}

	public String[] getSetGridsForCredential(String userId, String credId) {
		Vector<String> res = new Vector<String>();
                Vector grids = getGridNames(userId);
		//String[] grids = getGridNames(userId);
		if (grids == null || grids.size() == 0) {
			return null;
		}
		for (int i = 0; i < grids.size(); i++) {
			SZGCredential cred = this.getCredentialForGrid(userId, ""+grids.get(i));
			if (cred != null && cred.getId() == credId) {
				res.add(""+grids.get(i));
			}
		}
		return (String[]) res.toArray(new String[] {});
	}

    private Vector getGridNames(String userId) {
        // elerheto eroforras konfiguracio lekerdezese
        Vector<String> gridNames = new Vector<String>();
        try {
            ResourceConfigurationFace rc = (ResourceConfigurationFace) InformationBase.getI().getServiceClient("resourceconfigure", "portal");
            List<Middleware> tmp_r = rc.get();
            gridNames = ConfigHandler.getAllGroupsforProxy(
                    tmp_r, new Certificate[]{Certificate.X_509_GSI, Certificate.X_509_RFC});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gridNames;
    }

}
