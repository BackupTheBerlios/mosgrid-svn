package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.textretriever.MSMLFixedTextExtensionTextRetriever01;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLFixedTextExtension extends MSMLParserExtensionTextBasedBase
implements IMSMLParserExtensionTextBased {

	public static final String FIXEDTEXT = "fixedText";
	private String _fixedText;

	public MSMLFixedTextExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.PRIMITIVE;
	}

	@Override
	public float getWeight() {
		return 3000;
	}

	@Override
	public HashMap<String, String> getConfig() {
		HashMap<String, String> conf = new HashMap<String, String>();

		updateValues();

		if (!StringH.isNullOrEmpty(_fixedText)) {
			conf.put("version", "0.1");
			conf.put(FIXEDTEXT, _fixedText);
		}
		return conf;
	}

	private void updateValues() {
		MSMLFixedTextExtensionComponent comp = getCastComponent();
		comp.updateValuesInExtension();
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		HashMap<String, String> conf = (HashMap<String, String>) config;
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_fixedText = conf.get(FIXEDTEXT);
		} else {
			_fixedText = "";
		}

		updateValuesInComponent();
	}

	@Override
	public IMSMLExtensionTextRetriever getTextRetriever(String version) {
		if ("0.1".equals(version))
			return new MSMLFixedTextExtensionTextRetriever01();
		return null;
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	protected IMSMLParserComponent doGetComponent() {
		return new MSMLFixedTextExtensionComponent(this);
	}

	private MSMLFixedTextExtensionComponent getCastComponent() {
		return (MSMLFixedTextExtensionComponent) getComponent();
	}

	public void updateValues(String text) {
		_fixedText = text;
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_fixedText);
	}

	@Override
	protected void doClear() {
		_fixedText = "";
	}
}
