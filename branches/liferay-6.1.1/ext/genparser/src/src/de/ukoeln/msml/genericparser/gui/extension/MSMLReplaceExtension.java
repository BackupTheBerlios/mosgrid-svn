package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.textretriever.MSMLReplaceExtensionTextRetriever01;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLReplaceExtension extends MSMLParserExtensionTextBasedBase
	implements IMSMLParserExtensionTextBased {

	public static final String REPLACEPATTERN = "pattern";
	public static final String REPLACEMENT = "replacement";
	private String _pattern;
	private String _replacement;

	public MSMLReplaceExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.or(CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.MSML), CLASSTYPE.MSML);
	}

	@Override
	public float getWeight() {
		return 3000;
	}

	@Override
	public HashMap<String, String> getConfig() {
		HashMap<String, String> conf = new HashMap<String, String>();

		updateValues();

		if (!StringH.isNullOrEmpty(_pattern)) {
			conf.put("version", "0.1");
			conf.put(REPLACEPATTERN, _pattern);
			conf.put(REPLACEMENT, _replacement);
		}
		return conf;
	}

	private void updateValues() {
		MSMLReplaceExtensionComponent comp = getCastComponent();
		comp.updateValuesInExtension();
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		HashMap<String, String> conf = (HashMap<String, String>) config;
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_pattern = conf.get("pattern");
			_replacement = conf.get("replacement");
		} else {
			_pattern = "";
			_replacement = "";
		}

		updateValuesInComponent();
	}

	@Override
	public IMSMLExtensionTextRetriever getTextRetriever(String version) {
		if ("0.1".equals(version))
			return new MSMLReplaceExtensionTextRetriever01();
		return null;
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	protected IMSMLParserComponent doGetComponent() {
		return new MSMLReplaceExtensionComponent(this);
	}

	private MSMLReplaceExtensionComponent getCastComponent() {
		return (MSMLReplaceExtensionComponent) getComponent();
	}

	public void updateValues(String pattern, String replacement) {
		_pattern = pattern;
		_replacement = replacement;
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_pattern, _replacement);
	}

	@Override
	protected void doClear() {
		_pattern = "";
		_replacement = "";
	}
}
