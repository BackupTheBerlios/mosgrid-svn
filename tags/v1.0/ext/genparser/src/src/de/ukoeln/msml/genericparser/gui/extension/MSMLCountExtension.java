package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.textretriever.MSMLCountExtensionTextRetriever01;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLCountExtension extends MSMLParserExtensionTextBasedBase
implements IMSMLParserExtensionTextBased {

	private String _countPattern;

	public MSMLCountExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.NUMERIC;
	}

	@Override
	public float getWeight() {
		return 3000;
	}

	@Override
	public HashMap<String, String> getConfig() {
		HashMap<String, String> conf = new HashMap<String, String>();

		updateValues();

		if (!StringH.isNullOrEmpty(_countPattern)) {
			conf.put("version", "0.1");
			conf.put("countPattern", _countPattern);
		}
		return conf;
	}

	private void updateValues() {
		MSMLCountExtensionComponent comp = getCastComponent();
		comp.updateValuesInExtension();
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		HashMap<String, String> conf = (HashMap<String, String>) config;
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_countPattern = conf.get("countPattern");
		} else {
			_countPattern = "";
		}

		updateValuesInComponent();
	}

	@Override
	public IMSMLExtensionTextRetriever getTextRetriever(String version) {
		if ("0.1".equals(version))
			return new MSMLCountExtensionTextRetriever01();
		return null;
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	protected IMSMLParserComponent doGetComponent() {
		return new MSMLCountExtensionComponent(this);
	}

	private MSMLCountExtensionComponent getCastComponent() {
		return (MSMLCountExtensionComponent) getComponent();
	}

	public void updateValues(String text) {
		_countPattern = text;
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_countPattern);
	}

	@Override
	protected void doClear() {
		_countPattern = "";
	}
}
