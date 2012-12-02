package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;

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
