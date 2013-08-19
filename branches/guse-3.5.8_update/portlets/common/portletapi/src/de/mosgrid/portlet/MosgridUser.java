package de.mosgrid.portlet;

import java.security.Principal;

/**
 * Simple Object which represents a user
 * 
 * @author Andreas Zink
 * 
 */
public class MosgridUser {

	private String userID;
	private String userName;

	public MosgridUser(String userID, Principal p) {
		super();
		this.userID = userID;
		this.userName = p.getName();
	}

	/**
	 * @return The user id or null if not given
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @return The user name or null if not given
	 */
	public String getUserName() {
		return userName;
	}

	@Override
	public String toString() {
		if (userID != null) {
			return "(" + userID + ")";
		} else {
			return "(UNKNOWN USER)";
		}
	}

}
