package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.AfterCreationAction;
import de.ukoeln.msml.genericparser.MSMLTreeValue;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPuroposeVistorMode;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.VisitorCallBack;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfigEntry;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.textretriever.MSMLListExtensionTextRetriever01;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;

public class MSMLListExtension extends MSMLParserExtensionTextBasedBase implements IMSMLParserExtensionTextBased {

	private ListMode _mode;
	private String _separatorPattern;

	public MSMLListExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	public static final String DYNAMICLISTCONFENTRYNAME = "isDynamicList";
	public static final String SEPARATORPATTERN = "separatorpattern";
	
	public static boolean isDynamicList(GenericParserConfig config) {
		GenericParserExtensionConfigEntry extConf = ConfigHelper.getExtensionConfigEntry(
				config, MSMLListExtension.class, MSMLListExtension.DYNAMICLISTCONFENTRYNAME);
		return Boolean.parseBoolean(extConf.getVal());
	}
	
	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.LIST;
	}

	@Override
	public float getWeight() {
		return -1000;
	}

	@Override
	public HashMap<String, String> getConfig() {
		MSMLListExtensionComponent comp = (MSMLListExtensionComponent) getComponent();
		HashMap<String, String> conf = new HashMap<String, String>();
		
		_mode = comp.getMode();
		
		if (_mode == ListMode.Unset)
			return conf;
		
		conf.put("version", "0.1");
		if (_mode == ListMode.Dynamic) {
			conf.put(DYNAMICLISTCONFENTRYNAME, ((Boolean)true).toString());
			conf.put("separatorpattern", _separatorPattern);
		} else if (_mode == ListMode.Fixed) {
			conf.put(DYNAMICLISTCONFENTRYNAME, ((Boolean)false).toString());
		}
		
		return conf;
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		HashMap<String, String> conf = (HashMap<String, String>) config;
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			if (Boolean.parseBoolean(conf.get(DYNAMICLISTCONFENTRYNAME))) {
				_mode = ListMode.Dynamic;
				_separatorPattern = conf.get("separatorpattern");
			} else {
				_mode = ListMode.Fixed;
			}
		} else {
			_mode = ListMode.Unset;
			_separatorPattern = "";
		}
		
		updateValuesInComponent();
	}

	@Override
	protected final IMSMLParserComponent doGetComponent() {
		return new MSMLListExtensionComponent(this);
	}

	
	@Override
	public IMSMLExtensionTextRetriever getTextRetriever(String version) {
		if (version.equals("0.1"))
			return new MSMLListExtensionTextRetriever01();
		return null;
	}

	@Override
	protected final void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		if (!(visitor.getValue() instanceof MSMLTreeValue))
			return;
		
		MSMLTreeValue val = (MSMLTreeValue) visitor.getValue();
		if (visitor.getMode() == GeneralPuroposeVistorMode.LISTMODECHANGED) {			
			if (visitor.getNewListMode() == ListMode.Dynamic) {
				visitor.getValue().deleteAllChilds();
				visitor.getValue().addOneChild(AfterCreationAction.OPENPATH);
			} else {
				visitor.getValue().deleteAllChilds();
			}
		} else if (visitor.getMode() == GeneralPuroposeVistorMode.SELECTIONCHANGED) {
			boolean dynamicComponentsEnabled = !val.getClassInfo().isUntypedNonAnyList();
			MSMLListExtensionComponent component = (MSMLListExtensionComponent) getComponent();
			component.enableDynamic(dynamicComponentsEnabled);
		}
	}

	void onListModeChanged(final ListMode oldMode) {
		triggerGeneralPurposeVisitor(GeneralPuroposeVistorMode.LISTMODECHANGED, new VisitorCallBack() {
			@Override
			public void Do(GeneralPurposeVisitor visitor) {
				visitor.setOldListMode(oldMode);
			}
		});
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		MSMLListExtensionComponent comp = (MSMLListExtensionComponent) getComponent();
		visitor.setNewListMode(comp.getMode());
	}

	public enum ListMode {
		Unset, Dynamic, Fixed, NoList
	}
	
	public void setMode(ListMode mode) {
		_mode = mode;
	}
	
	public void setPattern(String pattern) {
		_separatorPattern = pattern;
	}

	public void updateValues() {
		MSMLListExtensionComponent comp = (MSMLListExtensionComponent) getComponent();
		_mode = comp.getMode();
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_mode);
	}

	private MSMLListExtensionComponent getCastComponent() {
		return (MSMLListExtensionComponent) getComponent();
	}

	@Override
	protected void doClear() {
		_mode = ListMode.Unset;
	}
}
