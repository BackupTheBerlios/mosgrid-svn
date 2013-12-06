package de.ukoeln.msml.genericparser.gui.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.ILayerParentInfo;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.LayerExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLElementSeparatorExtension extends MSMLParserExtensionBase {

	public static final String SEPARATORPATTERN = "pattern";
	public static final String NEWSEPERATOR = "newseperator";
	private String _separatorPattern;
	private String _newSep;

	public MSMLElementSeparatorExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.CUSTOM;
	}

	@Override
	public float getWeight() {
		return 0;
	}

	@Override
	public HashMap<String, String> getConfig() {
		HashMap<String, String> conf = new HashMap<String, String>();

		updateValues();

		if (!StringH.isNullOrEmpty(_separatorPattern)) {
			conf.put("version", "0.1");
			conf.put(SEPARATORPATTERN, _separatorPattern);
			conf.put(NEWSEPERATOR, _newSep);
		}
		return conf;
	}

	private void updateValues() {
		MSMLElementSeparatorExtensionComponent comp = getCastComponent();
		comp.updateValuesInExtension();
	}

	@Override
	public void loadConfig(HashMap<String, String> conf) {
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_separatorPattern = conf.get(SEPARATORPATTERN);
			_newSep = conf.get(NEWSEPERATOR);
		} else {
			_separatorPattern = "";
			_newSep  = "";
		}

		updateValuesInComponent();
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		ILayerParentInfo inf = (ILayerParentInfo) visitor.getValue().getInfo();
		boolean canAdd = inf.canAddChild(visitor.getValue());
		visitor.setCanAddChild(canAdd);
	}

	@Override
	protected IMSMLParserComponent doGetComponent() {
		return new MSMLElementSeparatorExtensionComponent(this);
	}

	private MSMLElementSeparatorExtensionComponent getCastComponent() {
		return (MSMLElementSeparatorExtensionComponent) getComponent();
	}

	public void updateValues(String separator, String newSep) {
		_separatorPattern = separator;
		_newSep = newSep;
		
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_separatorPattern, _newSep);
	}

	@Override
	protected void doClear() {
		_separatorPattern = null;
		_newSep = null;
	}

	@Override
	public boolean isEmpty(GenericParserConfig config) {
		HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(config, this);
		return isEmpty(conf);
	}

	private boolean isEmpty(HashMap<String, String> conf) {
		if (conf == null || conf.size() == 0 || !conf.containsKey("version") || !conf.get("version").equals("0.1"))
			return true;

		String pattern = conf.get(SEPARATORPATTERN);

		if (StringH.isNullOrEmpty(pattern))
			return true;
		return false;
	}

	@Override
	public void handleExtensionVisitor(HashMap<String, String> conf, IMSMLExtensionVisitor visit) {
		if (isEmpty(conf))
			return;
		
		LayerExtensionVisitor realVis = (LayerExtensionVisitor) visit;
		List<String> newParts = new ArrayList<String>();
		for (String part : realVis.getPartsToSplit()) {
			List<String> splits = Arrays.asList(part.split(conf.get(SEPARATORPATTERN)));
			newParts.addAll(splits);
		}
		realVis.setSeperator(conf.get(NEWSEPERATOR));
		realVis.setPartsToSplit(newParts);
	}

	@Override
	public boolean shouldBeVisibleToValue(MSMLTreeValueBase value) {
		return true; // maybe change if this has to be invisible for last layer
	}

	@Override
	protected void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}
}
