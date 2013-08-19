package de.ukoeln.msml.genericparser.gui.extension.textretriever;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLCountExtensionTextRetriever01 implements IMSMLExtensionTextRetriever {

	@Override
	public void getTextToConfig(IMSMLExtensionVisitor val, HashMap<String, String> config) {
		String pattern = config.get("countPattern");
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(val.getText());
		Integer count = 0;
		while (m.find())
			count++;
		val.setText(count.toString());
	}

	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public boolean isEmpty(HashMap<String, String> config) {
		return StringH.isNullOrEmpty(config.get("countPattern"));
	}

}
