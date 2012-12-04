package de.ukoeln.msml.genericparser.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.helper.ParameterPropertyHelper;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.ClassInfo.ClassInfoFactory;
import de.ukoeln.msml.genericparser.classes.visitors.ArrayPropertyExtensionVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.LayerExtensionVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigSimplType;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.MSMLElementCountExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLPropertyExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;

public class ArrayPropertyClassInfo extends MSMLCustomClassInfo<ArrayPropertyExtensionVisitor, PropertyType> 
	implements ILayerParentInfo {
	private static final String ID = "Id";
	
	public ArrayPropertyClassInfo() {
		super("Array-Property", PropertyType.class);
	}

	@Override
	public void init(MSMLSimpleTreeValue res) {
		super.init(res);
		res.createAndAddOneNode(ClassInfoFactory.getClassInfo(String.class), ID, true, false);
	}

	@Override
	public ArrayPropertyExtensionVisitor getVisitorInstance(MSMLTreeValueBase val) {
		return new ArrayPropertyExtensionVisitor(val); 
	}

	@Override
	protected void addExtensions(List<Class<? extends IMSMLParserExtension>> list) {
		super.addExtensions(list);
		list.add(MSMLElementCountExtension.class);
		list.add(MSMLPropertyExtension.class);
	}

	@Override
	public void absorbVisitor(ArrayPropertyExtensionVisitor res) {
		initObject();
		PropertyType obj = getObject();
		
		if (res.getDict() == null)
			throw new UnsupportedOperationException("Cannot set array without proper dictionary set.");
		
		String ref = XmlHelper.getInstance().getSuffix(res.getDictRef());
		ParameterPropertyHelper.setArray(res.getDoc().getCurrentHandleInfo().getCurEditor(), 
				obj, res.getDict(), ref, res.getTextForArray(), res.getDelimiter());
		obj.setId(res.getId());
		obj.setTitle(res.getTitle());
	}

	@Override 
	public void buildUp(MSMLSimpleTreeValue val, GenericParserConfig conf) {
		ConfigSimplType sc = (ConfigSimplType) conf;
		int counter = 0;
		for (GenericParserConfig cconf : sc.getChildConfig()) {
			// ID and other stuff
			if (cconf.getCanonicalClassName().equals(StringClassInfo.class.getCanonicalName())) {
				for (MSMLTreeValueBase child : val.getChilds()) {
					if (child.getPropName().equals(cconf.getPropertyName()))
						child.setConfig(cconf.getExtensionConfigCollection());
				}
			// child-layers
			} else {
				MSMLTreeValueBase newVal = val.createAndAddOneNode(
						new LayerPropertyClassInfo(), "Layer " + counter++, false, false);
				newVal.setConfig(cconf.getExtensionConfigCollection());				
			}
		}
	}

	@Override
	public boolean getLayerEnabled() {
		return true;
	}

	@Override
	public void fillStringsToSplit(LayerExtensionVisitor vis, MSMLTreeValueBase parVal, GenericParserDocumentBase doc) {
		ArrayPropertyExtensionVisitor res = super.runVisitor(doc, parVal);
		List<String> result = new ArrayList<String>();
		result.add(res.getText());
		vis.setPartsToSplit(result);
	}

	@Override
	public boolean canAddChild(MSMLTreeValueBase value) {
		return true;
	}

	@Override
	public ArrayPropertyExtensionVisitor runVisitor(GenericParserDocumentBase doc, MSMLTreeValueBase val) {
		ArrayPropertyExtensionVisitor vis = super.runVisitor(doc, val);
		
		MSMLTreeValueBase lastChild = val.getChilds().get(val.getChilds().size() - 1);
		if (lastChild.getInfo() instanceof LayerPropertyClassInfo) {
			LayerPropertyClassInfo lastLayer = (LayerPropertyClassInfo) lastChild.getInfo();
			LayerExtensionVisitor lastLayerResult = lastLayer.runVisitor(doc, lastChild);
			
			vis.setTextForArray(lastLayerResult.getText());			
		}
		
		// ID and stuff
		val.setText(vis.getText());
		try {
			for (MSMLTreeValueBase child : val.getChilds()) {
				if (child.getInfo() instanceof LayerPropertyClassInfo)
					continue;
				MSMLExtensionVisitor visit = new MSMLExtensionVisitor(child);
				for (IMSMLParserExtension ext : doc.getExtensionsToVal(child)) {
					HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(child.getConfig(), ext);
					if (conf.containsKey("version")) {
						((IMSMLParserExtensionTextBased) ext).getTextRetriever(conf.get("version")).getTextToConfig(
								visit, conf);
					}
				}

				// add other props here
				vis.setId(visit.getText());
			}
		} finally {
			val.setText("");
		}
		
		return vis;
	}
}
