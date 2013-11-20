package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.textretriever.MSMLLimiterExtensionTextRetriever01;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;

public class MSMLLimiterExtension extends MSMLParserExtensionTextBasedBase implements
IMSMLParserExtensionTextBased {

	private String _regexFrom = "";
	private String _regexTo = "";
	private int _lineFrom = 0;
	private int _lineTo = 0;
	private boolean _isLine = false;
	private boolean _useLastMatch = false;

	public MSMLLimiterExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.or(
				CLASSTYPE.PRIMITIVE, 
				CLASSTYPE.or(
						CLASSTYPE.LIST, 
						CLASSTYPE.MSML));
	}

	@Override
	public float getWeight() {
		return 0 - 500;
	}

	@Override
	public HashMap<String, String> getConfig() {
		MSMLLimiterExtensionComponent comp = (MSMLLimiterExtensionComponent) getComponent();
		HashMap<String, String> conf = new HashMap<String, String>();
		comp.updateValuesInExtension();
		
		conf.put("version", "0.1");
		conf.put("isLine", ((Boolean)_isLine).toString());
		conf.put("useLastMatch", ((Boolean)_useLastMatch).toString());
		conf.put("lineFrom", ((Integer)_lineFrom).toString());
		conf.put("lineTo", ((Integer)_lineTo).toString());
		conf.put("regexFrom", _regexFrom);		
		conf.put("regexTo", _regexTo);
		
		if (getTextRetriever("0.1").isEmpty(conf))
			return new HashMap<String, String>();
		return conf;
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		HashMap<String, String> conf = (HashMap<String, String>) config;
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_isLine = Boolean.parseBoolean(conf.get("isLine"));
			_useLastMatch = Boolean.parseBoolean(conf.get("useLastMatch"));
			_lineFrom = Integer.parseInt(conf.get("lineFrom"));
			_lineTo = Integer.parseInt(conf.get("lineTo"));
			_regexFrom = conf.get("regexFrom");
			_regexTo = conf.get("regexTo");
		} else {
			_isLine = false;
			_useLastMatch = false;
			_lineFrom = 0;
			_lineTo = 0;
			_regexFrom = "";
			_regexTo = "";
		}
		updateValuesInComponent();
	}

	@Override
	protected final IMSMLParserComponent doGetComponent() {
		return new MSMLLimiterExtensionComponent(this);
	}

	@Override
	public IMSMLExtensionTextRetriever getTextRetriever(String version) {
		if (version.equals("0.1"))
			return new MSMLLimiterExtensionTextRetriever01();
		return null;
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	public void updateValues(String txtRegexFrom, 
			String txtRegexTo, 
			int lineFrom, 
			int lineTo, 
			boolean isLine, 
			boolean useLastMatch) {
		_regexFrom = txtRegexFrom;
		_regexTo = txtRegexTo;
		_lineFrom = lineFrom;
		_lineTo = lineTo;
		_isLine = isLine;
		_useLastMatch = useLastMatch;
	}

	@Override
	protected void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		super.doHandleGeneralPurposeVisitor(visitor);
		MSMLLimiterExtensionComponent comp = (MSMLLimiterExtensionComponent) getComponent();
		comp.updateEnabled();
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_regexFrom, _regexTo, _lineFrom, _lineTo, _isLine, _useLastMatch);
	}

	private MSMLLimiterExtensionComponent getCastComponent() {
		return (MSMLLimiterExtensionComponent) getComponent();
	}

	@Override
	protected void doClear() {
		_isLine = false;
		_useLastMatch = false;
		_lineFrom = 0;
		_lineTo = 0;
		_regexFrom = "";
		_regexTo = "";
	}
}
