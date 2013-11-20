package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;
import java.util.List;

import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.IDictChangeAware;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IPropertyExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl.DictRefData;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLPropertyExtension extends MSMLParserExtensionBase implements IDictChangeAware {

	private static final String IDCONFNAME = "id";
	private static final String TITLECONFNAME = "title";
	private static final String DICTCONFNAME = "dict";
	private static final String REFCONFNAME = "ref";
	private String _title;
	private String _id;
	private String _ref;
	private String _dict;

	public MSMLPropertyExtension(MSMLExtensionHelper helper) {
		super(helper);
		getDoc().getDictDoc().registerDictChange(this);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.CUSTOM;
	}

	@Override
	public float getWeight() {
		return 4000;
	}

	@Override
	public HashMap<String, String> getConfig() {
		HashMap<String, String> conf = new HashMap<String, String>();

		updateValues();

		if (!StringH.isNullOrEmpty(_dict) && !StringH.isNullOrEmpty(_ref)) {
			conf.put("version", "0.1");
			conf.put(DICTCONFNAME, _dict);
			conf.put(REFCONFNAME, _ref);
			conf.put(IDCONFNAME, _id);
			conf.put(TITLECONFNAME, _title);
		}
		return conf;
	}

	private void updateValues() {
		MSMLPropertyExtensionComponent comp = getCastComponent();
		comp.updateValuesInExtension();
	}

	public void updateValues(String dict, String ref, String id, String title) {
		_dict = dict;
		_ref = ref;
		_id = id;
		_title = title;
	}

	@Override
	public void loadConfig(HashMap<String, String> conf) {
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_dict = conf.get(DICTCONFNAME);
			_ref = conf.get(REFCONFNAME);
			_id = conf.get(IDCONFNAME);
			_title = conf.get(TITLECONFNAME);
		} else {
			clear();
		}

		updateValuesInComponent();
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	protected IMSMLParserComponent doGetComponent() {
		return new MSMLPropertyExtensionComponent(this);
	}

	private MSMLPropertyExtensionComponent getCastComponent() {
		return (MSMLPropertyExtensionComponent) getComponent();
	}

	@Override
	public boolean isEmpty(GenericParserConfig config) {
		HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(config, this);
		if (conf == null || conf.size() == 0 || !conf.containsKey("version") || !conf.get("version").equals("0.1"))
			return true;

		String dict = conf.get(DICTCONFNAME);
		String ref = conf.get(REFCONFNAME);

		if (StringH.isNullOrEmpty(dict) || StringH.isNullOrEmpty(ref))
			return true;
		return false;
	}

	@Override
	public void handleExtensionVisitor(HashMap<String, String> conf, IMSMLExtensionVisitor visit) {
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			IPropertyExtensionVisitor realVis = (IPropertyExtensionVisitor) visit;
			realVis.setDoc(getDoc());
			String dict = conf.get(DICTCONFNAME);
			String ref = conf.get(REFCONFNAME);
			realVis.setDict(getDoc().getDictDoc().getIDictionaryToDict(dict));
			if (!StringH.isNullOrEmpty(dict) && !StringH.isNullOrEmpty(ref)) {
				String prefix = visit.getMSMLTreeValue().getPrefixToDict(dict);
				realVis.setDictRef(prefix + ":" + ref);
			}

			String id = conf.get(IDCONFNAME);
			String title = conf.get(TITLECONFNAME);
			if (!StringH.isNullOrEmpty(id))
				realVis.setId(id);
			if (!StringH.isNullOrEmpty(title))
				realVis.setTitle(title);				
		}
	}

	@Override
	protected void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	public void dictsChanged(List<DictRefData> data) {
		if (GenericParserMainDocument.useX())
			getCastComponent().dictsChanged(data);
	}

	@Override
	public boolean shouldBeVisibleToValue(MSMLTreeValueBase value) {
		return ((MSMLSimpleTreeValue) value).getSelectedLayerIndex() == -1;
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_dict, _ref, _id, _title);
	}

	@Override
	protected void doClear() {
		_dict = null;
		_ref = null;
		_id = null;
		_title = null;
	}
}
