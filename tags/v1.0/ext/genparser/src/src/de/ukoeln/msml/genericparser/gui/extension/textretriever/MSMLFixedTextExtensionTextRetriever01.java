package de.ukoeln.msml.genericparser.gui.extension.textretriever;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.gui.extension.MSMLFixedTextExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLFixedTextExtensionTextRetriever01 implements
		IMSMLExtensionTextRetriever {

	@Override
	public void getTextToConfig(IMSMLExtensionVisitor val,
			HashMap<String, String> config) {
		String text = config.get(MSMLFixedTextExtension.FIXEDTEXT);
		val.setText(text);
	}

	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public boolean isEmpty(HashMap<String, String> config) {
		return StringH.isNullOrEmpty(config.get(MSMLFixedTextExtension.FIXEDTEXT));
	}
}
