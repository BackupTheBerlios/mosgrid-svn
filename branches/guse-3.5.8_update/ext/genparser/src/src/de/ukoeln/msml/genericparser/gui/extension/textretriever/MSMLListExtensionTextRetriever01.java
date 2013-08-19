package de.ukoeln.msml.genericparser.gui.extension.textretriever;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;

public class MSMLListExtensionTextRetriever01 implements
		IMSMLExtensionTextRetriever {

	@Override
	public void getTextToConfig(IMSMLExtensionVisitor val,
			HashMap<String, String> config) {
	}

	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public boolean isEmpty(HashMap<String, String> config) {
		return false;
	}
}
