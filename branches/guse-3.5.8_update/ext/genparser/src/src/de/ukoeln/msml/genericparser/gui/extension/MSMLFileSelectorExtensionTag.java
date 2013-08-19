package de.ukoeln.msml.genericparser.gui.extension;

import java.util.Hashtable;
import java.util.Set;

public class MSMLFileSelectorExtensionTag {

	private Hashtable<String, String> _loadedFiles = new Hashtable<String, String>();

	public Set<String> getLoadedFiles() {
		return _loadedFiles.keySet();
	}

	public void addLoadedFile(String file, String text) {
		_loadedFiles.put(file, text);
	}
	
	public String getTextToFile(String file) {
		return _loadedFiles.get(file);
	}
}
