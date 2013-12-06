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

import org.globus.gsi.*;
import org.globus.gsi.gssapi.*;
import org.ietf.jgss.*;
import org.globus.gsi.CertUtil;

public class SZGCredential {

	private GlobusCredential gCred = null;
	private String downloadedFrom = null;
	private String description;
	private String id = null;

	/**
	 * Constructor.
	 * 
	 * @param credential
	 * @param downloadedFrom
	 *            the name of the host from where this credential has been
	 *            downloaded.
	 * @throws SZGCredentialException
	 */
	public SZGCredential(GSSCredential credential, String downloadedFrom, // NO_UCD
			String description, String credId) throws SZGCredentialException {
		if (credential instanceof GlobusGSSCredentialImpl) {
			this.gCred = ((GlobusGSSCredentialImpl) credential)
					.getGlobusCredential();
			this.downloadedFrom = downloadedFrom;
			this.description = description;
			this.id = credId;
		} else {
			throw new SZGCredentialException(
					"Casting credential to GlobusCredential failed!");
		}
	}

	public GlobusCredential getGlobusCredential() {
		return this.gCred;
	}

	public GSSCredential getGSSCredential() {
		try {
			return new GlobusGSSCredentialImpl(this.gCred,
					GSSCredential.INITIATE_AND_ACCEPT);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public long getTimeLeftInSeconds() throws GSSException {
		return this.gCred.getTimeLeft();
	}

	public String getTimeLeft() {
		try {
			long timeleft = this.gCred.getTimeLeft();
			return this.timeleftToHHMMSS(timeleft);
		} catch (Exception ex) {
			System.out.println("SZGCredential.getTimeLeft() - Exception :"
					+ ex.getMessage());
			return "unknown";
		}
	}

	private String timeleftToHHMMSS(long lifetime) {
		long hours = (int) Math.floor(lifetime / 3600);
		int left = (int) (lifetime - hours * 3600);
		int mins = (int) Math.floor(left / 60);
		int seconds = left - mins * 60;
		return hours + ":" + mins + ":" + seconds;
	}

	public String getIssuer() throws GSSException {
		return this.gCred.getIssuer();
	}

	public String getProxyType() throws GSSException {
		return CertUtil.getProxyTypeAsString(this.gCred.getProxyType());
	}

	public int getStrength() throws GSSException {
		return this.gCred.getStrength();
	}

	public String getSubject() throws GSSException {
		return this.gCred.getSubject();
	}

	public String getDescription() {
		if (this.description == null) {
			return "";
		}
		return this.description;
	}

	public String getDownloadedFrom() {
		if (this.downloadedFrom == null) {
			return "";
		}
		return this.downloadedFrom;
	}

	public String getId() {
		return this.id;
	}

	public String toString() {
		try {
			StringBuffer reply = new StringBuffer();
			reply.append("description: " + this.getDescription());
			reply.append("\ndownloaded from: " + this.getDownloadedFrom());
			reply.append("\nissuer: " + this.getIssuer());
			reply.append("\nproxy type: " + this.getProxyType());
			reply.append("\nstrength: " + this.gCred.getStrength() + "[bits]");
			reply.append("\ntimeLeft: " + this.getTimeLeft() + "[sec]");
			return reply.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
