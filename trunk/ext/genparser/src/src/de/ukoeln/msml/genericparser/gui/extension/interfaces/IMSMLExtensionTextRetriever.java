package de.ukoeln.msml.genericparser.gui.extension.interfaces;

import java.util.HashMap;


public interface IMSMLExtensionTextRetriever {
	
	public void getTextToConfig(IMSMLExtensionVisitor val, HashMap<String, String> config);

	public Object getTag();

	public boolean isEmpty(HashMap<String, String> config);
}
