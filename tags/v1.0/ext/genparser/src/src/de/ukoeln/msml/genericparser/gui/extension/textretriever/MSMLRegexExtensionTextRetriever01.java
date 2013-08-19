package de.ukoeln.msml.genericparser.gui.extension.textretriever;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLRegexExtensionTextRetriever01 implements
IMSMLExtensionTextRetriever {

	@Override
	public void getTextToConfig(IMSMLExtensionVisitor val, HashMap<String, String> config) {
		if (StringH.isNullOrEmpty(val.getText()))
			return;
		TreeMap<Integer, String> patterns = new TreeMap<Integer, String>();
		for (String key : config.keySet()) {
			if (StringH.isInteger(key))
				patterns.put(Integer.parseInt(key), config.get(key));
		}

		for (Entry<Integer, String> pattern : patterns.entrySet()) {
			if (StringH.isNullOrEmpty(pattern.getValue()))
				continue;
			Pattern p = Pattern.compile(pattern.getValue(), Pattern.MULTILINE);
			Matcher m = p.matcher(val.getText());
			if (m.find())
				val.setText(m.group());
		}	
	}

	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public boolean isEmpty(HashMap<String, String> config) {
		for (String key : config.keySet()) {
			if (StringH.isInteger(key) && !StringH.isNullOrEmpty(config.get(key)))
				return false;
		}
		return true;
	}
}
