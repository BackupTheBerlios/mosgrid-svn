package de.mosgrid.adapter;

import de.mosgrid.util.UploadCollector;

public interface IUploadableInputInfo extends IInputInfo {

	// public List<File> getFiles() throws IOException;
	//
	// public String getPortName();
	//
	// public String getFileExtension();

	// NOTE: Visitor-Pattern for upload-collector is much more flexible
	public void addUploads(UploadCollector collector);
}