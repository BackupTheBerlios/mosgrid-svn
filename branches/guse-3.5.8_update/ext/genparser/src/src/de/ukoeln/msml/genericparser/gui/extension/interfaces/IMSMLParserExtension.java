package de.ukoeln.msml.genericparser.gui.extension.interfaces;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;

public interface IMSMLParserExtension {
	public CLASSTYPE getClassInfoTypeToApply();
	
	public float getWeight();
	
	public HashMap<String, String> getConfig();
	
	public void loadConfig(HashMap<String, String> config);
	
	public IMSMLParserComponent getComponent();
		
	public boolean isEmpty(GenericParserConfig extConf);
	
	public String getName();

	public void handleExtensionVisitor(HashMap<String, String> conf, IMSMLExtensionVisitor visit);

	public void initGeneralPurposeVisitor(GeneralPurposeVisitor visitor);

	public void handleGeneralPurposeVisitor(GeneralPurposeVisitor visitor);

	public void clear();

	public boolean shouldBeVisibleToValue(MSMLTreeValueBase value); 
}
