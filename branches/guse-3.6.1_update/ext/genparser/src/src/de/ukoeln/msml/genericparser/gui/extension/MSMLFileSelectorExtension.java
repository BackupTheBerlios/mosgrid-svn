package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.MSMLTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.textretriever.MSMLFileSelectorExtensionTextRetriever01;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;

public class MSMLFileSelectorExtension extends MSMLParserExtensionTextBasedBase implements IMSMLParserExtensionTextBased {

	public static final String CLASSNAME = MSMLFileSelectorExtension.class.getCanonicalName();
	public static final String FILEPATTERN = "file";
	public static final String USEPARENTSTEXT = "useParentsText";
	public static final String ISMSML = "isMSML";
	public static final String JOBPATTERN = "jobPattern";

	public MSMLFileSelectorExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	private String _filepattern;
	private boolean _useParentsText;
	private boolean _isMSML;
	private String _jobPattern;

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.or(CLASSTYPE.or(CLASSTYPE.LIST, CLASSTYPE.MSML), CLASSTYPE.PRIMITIVE);
	}

	@Override
	public float getWeight() {
		return 0;
	}

	@Override
	public HashMap<String, String> getConfig() {
		updateValues();

		HashMap<String, String> conf = new HashMap<String, String>();
		conf.put("version", "0.1");
		conf.put(FILEPATTERN, _filepattern);
		conf.put(USEPARENTSTEXT, Boolean.toString(_useParentsText));
		conf.put(ISMSML, Boolean.toString(_isMSML));
		conf.put(JOBPATTERN, _jobPattern);
		return conf;
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		if (config.get("version").equals("0.1")) {
			_filepattern = config.get(FILEPATTERN);
			_useParentsText = Boolean.parseBoolean(config.get(USEPARENTSTEXT));
			_isMSML = Boolean.parseBoolean(config.get(ISMSML));
			_jobPattern = config.get(JOBPATTERN);
			if (_jobPattern == null)
				_jobPattern = "";
		} else {
			_filepattern = "";
			_useParentsText = false;
			_isMSML = false;
			_jobPattern = "";
		}
		updateValuesInComponent();
	}

	@Override
	public IMSMLParserComponent doGetComponent() {
		return new MSMLFileSelectorExtensionComponent(this);
	}


	@Override
	public IMSMLExtensionTextRetriever getTextRetriever(String version) {
		if ("0.1".equals(version))
			return new MSMLFileSelectorExtensionTextRetriever01();
		return null;
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	protected void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		super.doHandleGeneralPurposeVisitor(visitor);
		MSMLTreeValueBase val = visitor.getValue();
		if (val instanceof MSMLTreeValue && ((MSMLTreeValue)visitor.getValue()).isDynamicsListChild()) {
			_useParentsText = true;
			MSMLFileSelectorExtensionComponent comp = getCastCompontent();
			updateValuesInComponent();
			comp.setChildComponentsEnabled(false);
		}

	}

	private void updateValues() {
		getCastCompontent().updateValuesInExtension();
	}

	private MSMLFileSelectorExtensionComponent getCastCompontent() {
		return (MSMLFileSelectorExtensionComponent) getComponent();
	}

	public void updateValues(String pattern, boolean useParentsText, boolean isMSML, String jobPattern) {
		_filepattern = pattern;
		_useParentsText = useParentsText;
		_isMSML = isMSML;
		_jobPattern = jobPattern;
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastCompontent().setValues(_filepattern, _useParentsText, _isMSML, _jobPattern);
	}

	@Override
	protected void doClear() {
		_filepattern = "";
		_useParentsText = false;
		_jobPattern = "";
		_isMSML = false;
	}
}
