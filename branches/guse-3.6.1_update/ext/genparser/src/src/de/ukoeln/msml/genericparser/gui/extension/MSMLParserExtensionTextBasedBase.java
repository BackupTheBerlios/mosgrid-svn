package de.ukoeln.msml.genericparser.gui.extension;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;

public abstract class MSMLParserExtensionTextBasedBase extends MSMLParserExtensionBase 
	implements IMSMLParserExtensionTextBased{
	
	public MSMLParserExtensionTextBasedBase(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	protected void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		boolean enabled = visitor.getValue().getExtensionEnabled(this, visitor);
		getComponent().setChildComponentsEnabled(enabled);
	}

	@Override
	public final boolean isEmpty(GenericParserConfig config) {
		HashMap<String,String> confHash = ConfigHelper.getExtensionConfigAsHashMap(config, this);
		return isEmpty(confHash);
	}

	private boolean isEmpty(HashMap<String, String> confHash) {
		if (confHash == null || confHash.size() == 0)
			return true;
		return getTextRetriever(confHash.get("version")).isEmpty(confHash);
	}

	@Override
	public void handleExtensionVisitor(HashMap<String, String> conf, IMSMLExtensionVisitor visit) {
		if (isEmpty(conf))
			return;
		getTextRetriever(conf.get("version")).getTextToConfig(visit, conf);	
	}

	@Override
	public boolean shouldBeVisibleToValue(MSMLTreeValueBase value) {
		return true; 
	}
}
