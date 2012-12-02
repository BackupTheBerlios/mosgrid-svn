package de.mosgrid.ukoeln.templatedesigner.document;

import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.jaxb.bindings.MoleculeUploadType;

public class UploadsBean extends IndexedPropertyBean {

	public static final String FILETYPE = "fileType";
	public static final String PORTID = "port";
	public static final String ISMOLECULE = "isMolecule";
	private String _fileType;
	private String _port; 
	private boolean _isMolecule = false;

	public UploadsBean() {}

	public UploadsBean(FileUpload upload) {
		_isMolecule = upload instanceof MoleculeUploadType;
		_fileType = upload.getFileType();
		_port = upload.getPort();
	}

	public void fillJAXBElement(FileUpload upload) {
		upload.setFileType(getFileType());
		upload.setPort(getPort());
	}

	public String getFileType() {
		return _fileType;
	}
	
	public void setFileType(String type) {
		_fileType = type;
	}
	
	public String getPort() {
		return _port;
	}

	public void setPort(String port) {
		_port = port;
	}
	
	public boolean getIsMolecule() {
		return _isMolecule;
	}

	public void setIsMolecule(boolean isMolecule) {
		_isMolecule = isMolecule;
	}
}
