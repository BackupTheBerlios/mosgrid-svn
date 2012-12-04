package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.ArrayPropertyExtensionVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLElementCountExtension extends MSMLParserExtensionBase {

	private static final String COUNT_PATTERN = "countPattern";
	private String _countPattern;

	public MSMLElementCountExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.CUSTOM;
	}

	@Override
	public float getWeight() {
		return 6000;
	}

	@Override
	public HashMap<String, String> getConfig() {
		HashMap<String, String> conf = new HashMap<String, String>();

		updateValues();

		if (!StringH.isNullOrEmpty(_countPattern)) {
			conf.put("version", "0.1");
			conf.put(COUNT_PATTERN, _countPattern);
		}
		return conf;
	}

	private void updateValues() {
		MSMLElementCountExtensionComponent comp = getCastComponent();
		comp.updateValuesInExtension();
	}

	@Override
	public void loadConfig(HashMap<String, String> conf) {
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_countPattern = conf.get(COUNT_PATTERN);
		} else {
			_countPattern = "";
		}

		updateValuesInComponent();
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	protected IMSMLParserComponent doGetComponent() {
		return new MSMLElementCountExtensionComponent(this);
	}

	private MSMLElementCountExtensionComponent getCastComponent() {
		return (MSMLElementCountExtensionComponent) getComponent();
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
	
	@Override
	public boolean isEmpty(GenericParserConfig extConf) {
		HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(extConf, this);
		if (conf.containsKey("version") && conf.get("version").equals("0.1") && 
				!StringH.isNullOrEmpty(conf.get(COUNT_PATTERN)))
				return false;
		return true;
	}

	@Override
	public void handleExtensionVisitor(HashMap<String, String> conf, IMSMLExtensionVisitor visit) {
		ArrayPropertyExtensionVisitor realVis = (ArrayPropertyExtensionVisitor) visit;
		realVis.setDelimiter(conf.get(COUNT_PATTERN));
	}

	@Override
	public boolean shouldBeVisibleToValue(MSMLTreeValueBase value) {
		return true;
	}

	@Override
	protected void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}
}
