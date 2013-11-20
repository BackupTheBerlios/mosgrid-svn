package de.ukoeln.msml.genericparser.gui.extension.textretriever;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ukoeln.msml.genericparser.gui.extension.MSMLReplaceExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLReplaceExtensionTextRetriever01 implements
		IMSMLExtensionTextRetriever {

	@Override
	public void getTextToConfig(IMSMLExtensionVisitor val,
			HashMap<String, String> config) {
		if (StringH.isNullOrEmpty(val.getText()))
			return;

		String pattern = config.get("pattern");
		String replacement = config.get("replacement");

		if (StringH.isNullOrEmpty(pattern))
			return;

		Pattern p = Pattern.compile(pattern, Pattern.MULTILINE);
		Matcher m = p.matcher(val.getText());
		if (m.find())
			val.setText(m.replaceAll(replacement));
	}

	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public boolean isEmpty(HashMap<String, String> config) {
		return StringH.isNullOrEmpty(config.get(MSMLReplaceExtension.REPLACEPATTERN))
				&& StringH.isNullOrEmpty(config.get(MSMLReplaceExtension.REPLACEMENT));
	}
}
