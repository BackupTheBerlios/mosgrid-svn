package de.ukoeln.msml.genericparser.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.visitors.LayerExtensionVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigSimplType;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.configuration.LayerType;
import de.ukoeln.msml.genericparser.gui.extension.MSMLElementSeparatorExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFileSelectorExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;

public class LayerPropertyClassInfo extends MSMLCustomClassInfo<LayerExtensionVisitor, PropertyType>
	implements ILayerParentInfo {
	
//	private final static Logger LOGGER = LoggerFactory.getLogger(LayerPropertyClassInfo.class);
	public LayerPropertyClassInfo() {
		super("Layer-Property", PropertyType.class);
	}

	@Override
	public LayerExtensionVisitor getVisitorInstance(MSMLTreeValueBase val) {
		return new LayerExtensionVisitor(val); 
	}

	@Override
	protected void addExtensions(List<Class<? extends IMSMLParserExtension>> list) {
		super.addExtensions(list);
		list.remove(MSMLFileSelectorExtension.class);
		list.add(MSMLElementSeparatorExtension.class);
	}

	/**
	 * This function is a special case of runvistior. The LayerExtension.runVistor is
	 * explicitly called by it's parent which can be either a ArrayClassInfo or LayerClassInfo.
	 * When invoked by a testrun from the GUI it should print out each parsed element up to the
	 * current layer separated by an underline-Line.
	 * When invoked by a real Parserrun then this function sets the combinedParsedParts field
	 * in the returned LayerExtensionVisitor. The parser uses the following steps to descent through
	 * an array of layers:
	 * <ul>1. Retrieve text to split and parse from parent.</ul>
	 * <ul>2. Find the ElementSeperatorExtension in the currently used extensions (should be first)</ul>
	 * <ul>3. Let the ElementSeperatorExtension split the input text from parent.</ul>
	 * <ul>4. For each part:
	 * 		<ul>a) Run the rest of extensions</ul>
	 * 		<ul>b) Add the result to the text-Field of the current Visitor (this will be displayed on testrun)</ul>
	 * 		<ul>c) Let next layer (if any) handle parsed text</ul>
	 * </ul>
	 * <ul>5. The layer underneath returns a LayerExtensionVisitor with the set combinedParsedParts-Field.</ul>
	 * <ul>6. Combine all combinedParsedParts and set field in current visitor.</ul>
	 * 
	 * @param doc
	 * @param val
	 * @return
	 */
	@Override
	public LayerExtensionVisitor runVisitor(GenericParserDocumentBase doc, MSMLTreeValueBase val) {
		LayerExtensionVisitor vis = getVisitorInstance(val);

		fillStringsToSplit(vis, val, doc);

		List<String> result = vis.getPartsToSplit();
		String combinedResult = "";
		for (String part : result) {
			combinedResult += part;
		}
		
		vis.setText(combinedResult);
		return vis;

//		// sadly no preprocessor tags
//		if (LOGGER.isDebugEnabled()) {
//			MSMLTreeValueBase parVal = val.getParentsValue();
//			if (parVal == null)
//				throw new UnsupportedOperationException("No parent value for LayerExtension. This cannot be ok.");			
//		}
//		String parentText = val.getParentsText();
//		
//		
//		LayerExtensionVisitor visit = getVisitorInstance(val);
//		visit.setText(parentText);
//		sepExt.handleExtensionVisitor(conf, visit);
//		
//		List<String> combinedPartsFromChilds = new ArrayList<String>();
//		List<String> parsedPartsFromCurrent = new ArrayList<String>();
//		
//		// TODO: parallize
//		for (String textPart : visit.getSplitParts()) {
//			MSMLExtensionVisitor partVisitor = new MSMLExtensionVisitor(val);
//			partVisitor.setText(textPart);
//			for (IMSMLParserExtension ext : exts) {
//				if (ext instanceof MSMLElementSeparatorExtension)
//					continue;
//				HashMap<String, String> extConf = ConfigHelper.getExtensionConfigAsHashMap(val.getConfig(), sepExt);
//				ext.handleExtensionVisitor(extConf, partVisitor);
//			}
//			
//			parsedPartsFromCurrent.add(partVisitor.getText());
//			try {
//				// let child handle parsed text.
//				val.setText(partVisitor.getText());
//				if (val.getChilds() != null && val.getChilds().size() != 0) {
//					if (val.getChilds().size() > 1)
//						throw new UnsupportedOperationException("Layer should not have more than one child.");
//					MSMLTreeValueBase child = val.getChilds().get(0);
//					LayerExtensionVisitor childVis = (LayerExtensionVisitor) child.getInfo().runVisitor(doc, child);
//					combinedPartsFromChilds.add(childVis.getCombinedParts());
//				}
//			} finally {
//				val.setText(null);
//			}
//		}
//		
//		String result = "";
//		// combine lists to final result for the text field in visitor (to display in testrun,
//		// and to combinedParts with the seperator used by parent values.
//		for (int i = 0; i < parsedPartsFromCurrent.size(); i++) {
//			result += parsedPartsFromCurrent.get(i);
//			if (i < parsedPartsFromCurrent.size() - 1)
//				result += "\n\n--------------------------------------------\n\n";
//		}
//		visit.setText(result);
//		
//		String combined = "";
//		for (int i = 0; i < combinedPartsFromChilds.size(); i++) {
//			combined += combinedPartsFromChilds.get(i);
//			if (i < combinedPartsFromChilds.size() -1)
//				result += visit.getNewSep();
//		}
//		visit.setCombinedParts(combined);
//		
//		return visit;
	}
	
	@Override
	public void fillStringsToSplit(LayerExtensionVisitor vis, MSMLTreeValueBase val, GenericParserDocumentBase doc) {

		// find parent and get the msml-value just before the current one
		MSMLTreeValueBase parVal = vis.getMSMLTreeValue().getParentsValue();
		if (parVal == null)
			throw new UnsupportedOperationException("No parent value for LayerExtension. This cannot be ok.");
		
		int indexOfCurrent = parVal.getChilds().indexOf(val);
		MSMLTreeValueBase previousVal = parVal;
		for (int i = indexOfCurrent - 1; i > 0; i--) {
			MSMLTreeValueBase possiblePriviousVal = parVal.getChilds().get(i);
			if (possiblePriviousVal.getInfo() instanceof LayerPropertyClassInfo)
				previousVal = parVal;
		}
		
		// previousVal now contains either the previous layer on the same level
		// or the parents value which is (for now) the array node 
		ILayerParentInfo parInfo = (ILayerParentInfo) previousVal.getInfo();
		parInfo.fillStringsToSplit(vis, previousVal, doc);
		
		// now the visitor contains all splits and we can run the other extensions over each split
		List<IMSMLParserExtension> exts = doc.getExtensionsToVal(val);
		ExtensionAndConfig confExt = getSeperatorExtensionAndConfig(val, exts);
		confExt.getSepExt().handleExtensionVisitor(confExt.getConf(), vis);
		
		List<String> result = new ArrayList<String>();
		// vis now contains a list of strings that has been split down to the current layer
		// now run the actual value-retrieving extensions of the current layer
		for (int i = 0; i < vis.getPartsToSplit().size(); i++) {
			String part = vis.getPartsToSplit().get(i);
			MSMLExtensionVisitor partVisitor = new MSMLExtensionVisitor(val);
			partVisitor.setText(part);
			for (IMSMLParserExtension ext : exts) {
				if (ext instanceof MSMLElementSeparatorExtension)
					continue;
				HashMap<String, String> extConf = ConfigHelper.getExtensionConfigAsHashMap(val.getConfig(), ext);
				ext.handleExtensionVisitor(extConf, partVisitor);
			}
			String res = partVisitor.getText();
			if (i < vis.getPartsToSplit().size() - 1)
				res += vis.getSeperator();
			result.add(res);
		}
		vis.setPartsToSplit(result);
	}

	private ExtensionAndConfig getSeperatorExtensionAndConfig(MSMLTreeValueBase val, List<IMSMLParserExtension> exts) {
		for (IMSMLParserExtension ext : exts) {
			if (ext instanceof MSMLElementSeparatorExtension) {
				MSMLElementSeparatorExtension sepExt = (MSMLElementSeparatorExtension) ext;
				HashMap<String,String> conf = ConfigHelper.getExtensionConfigAsHashMap(val.getConfig(), sepExt);
				return new ExtensionAndConfig(sepExt, conf);
			}
		}
		throw new UnsupportedOperationException("No Seperator-Extension found.");
	}

	@Override
	public void absorbVisitor(LayerExtensionVisitor res) {
	}

	@Override 
	public void buildUp(MSMLSimpleTreeValue val, GenericParserConfig conf) {
		ConfigSimplType sc = (ConfigSimplType) conf;
		int counter = 1;
		for (LayerType layer : sc.getLayerConfigs()) {
			MSMLTreeValueBase newVal = val.createAndAddOneNode(
					new LayerPropertyClassInfo(), "Layer " + counter++, false, false);
			newVal.setConfig(layer.getLayerConfig());
		}
	}

	@Override
	public boolean getLayerEnabled() {
		return true;
	}

	private class ExtensionAndConfig {

		private final HashMap<String, String> _conf;
		private final MSMLElementSeparatorExtension _sepExt;

		public ExtensionAndConfig(MSMLElementSeparatorExtension sepExt, HashMap<String, String> conf) {
			_sepExt = sepExt;
			_conf = conf;
		}

		public HashMap<String, String> getConf() {
			return _conf;
		}

		public MSMLElementSeparatorExtension getSepExt() {
			return _sepExt;
		}
	}

	@Override
	public boolean canAddChild(MSMLTreeValueBase value) {
		return false;
	}
}
