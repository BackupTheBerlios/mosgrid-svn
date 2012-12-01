package de.ukoeln.msml.genericparser.gui.extension;

import java.util.ArrayList;
import java.util.HashMap;

import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.textretriever.MSMLRegexExtensionTextRetriever01;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;

public class MSMLRegexExtension extends MSMLParserExtensionTextBasedBase implements IMSMLParserExtensionTextBased {

	private ArrayList<String> regs = new ArrayList<String>();
	
	public MSMLRegexExtension(MSMLExtensionHelper helper) {
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
		return 100;
	}

	@Override
	public HashMap<String, String> getConfig() {
		updateLocalValues();

		HashMap<String, String> conf = new HashMap<String, String>();
		conf.put("version", "0.1");
		for (int i = 0; i < regs.size(); i++) {
			conf.put(Integer.toString(i), regs.get(i));
		}
		return conf;
	}

	private void updateLocalValues() {
		regs.clear();
		
		MSMLRegexExtensionComponent comp = (MSMLRegexExtensionComponent) getComponent();
		
		for (int i = 0; i < comp.getCount(); i++) {
			if (comp.isSet(i))
				regs.add(comp.getRegex(i));
		}
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		regs.clear();
		MSMLRegexExtensionComponent comp = (MSMLRegexExtensionComponent) getComponent();
		HashMap<String, String> conf = (HashMap<String, String>) config;
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			for (int i = 0; i < comp.getCount(); i++) {
				if (conf.containsKey(Integer.toString(i)))
					regs.add(conf.get(Integer.toString(i)));
			}
		}
		updateValuesInComponent();
	}

	@Override
	protected final IMSMLParserComponent doGetComponent() {
		return new MSMLRegexExtensionComponent(this);
	}

	@Override
	public IMSMLExtensionTextRetriever getTextRetriever(String version) {
		if ("0.1".equals(version))
			return new MSMLRegexExtensionTextRetriever01();
		return null;
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	public void updateValues() {
		updateLocalValues();
	}

	@Override
	protected void doUpdateValuesInComponent() {
		for (int i = 0; i < regs.size(); i++) {
			getCastComponent().setRegex(i, regs.get(i));
		}
	}

	private MSMLRegexExtensionComponent getCastComponent() {
		return (MSMLRegexExtensionComponent) getComponent();
	}

	@Override
	protected void doClear() {
		for (int i = 0; i < regs.size(); i++) {
			regs.set(i, "");
		}
	}
}
