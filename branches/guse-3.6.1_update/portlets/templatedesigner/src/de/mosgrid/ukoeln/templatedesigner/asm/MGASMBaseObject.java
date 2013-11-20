package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;

/**
 * Base object for ASM-Wrappers. Inheriting objects wrap ASM-Jobs, -Workflows, -Ports etc.pp..
 * All wrapping objects need access to the ASM-Service and the current user-ID.
 * This package may be moved to portlet api.
 * 
 * @author mkruse0
 *
 */
public class MGASMBaseObject {

	private final ASMService _service;
	private final String _userID;
	
	public MGASMBaseObject(ASMService service, String userID) {
		_service = service;
		_userID = userID;
	}
	
	protected ASMService getService() {
		return _service;
	}
	
	protected String getUserID() {
		return _userID;
	}
}
