package de.ukoeln.msml.genericparser.classes;

import java.util.HashMap;
import java.util.List;

import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.helper.ParameterPropertyHelper;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.ClassInfo.ClassInfoFactory;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.PropertyExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.MSMLPropertyExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IPropertyExtensionVisitor;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;

public class PropertyClassInfo extends MSMLCustomClassInfo<IPropertyExtensionVisitor, PropertyType> {
	private static final String ID = "Id";

	public PropertyClassInfo() {
		super("Property", PropertyType.class);
	}

	@Override
	public void init(MSMLSimpleTreeValue res) {
		super.init(res);
		res.createAndAddOneNode(ClassInfoFactory.getClassInfo(String.class), ID, true, false);
	}

	@Override
	public PropertyExtensionVisitor getVisitorInstance(MSMLTreeValueBase val) {
		return new PropertyExtensionVisitor(val); 
	}

	@Override
	protected void addExtensions(List<Class<? extends IMSMLParserExtension>> list) {
		super.addExtensions(list);
		list.add(MSMLPropertyExtension.class);
	}

	@Override
	public void absorbVisitor(IPropertyExtensionVisitor res) {
		initObject();
		PropertyType obj = getObject();

		if (res.getDict() == null)
			throw new UnsupportedOperationException("Cannot set array without proper dictionary set.");

		ParameterPropertyHelper.setScalar(res.getDoc().getCurrentHandleInfo().getCurEditor(), 
				obj, res.getDict(), XmlHelper.getInstance().getSuffix(res.getDictRef()), res.getText());
		obj.setId(res.getId());
		obj.setTitle(res.getTitle());
	}

	@Override
	public void buildUp(MSMLSimpleTreeValue val, GenericParserConfig conf) {
		for (GenericParserConfig cconf : conf.getChildConfig()) {
			for (MSMLTreeValueBase child : val.getChilds()) {
				if (child.getPropName().equals(cconf.getPropertyName()))
					child.setConfig(cconf.getExtensionConfigCollection());
			}
		}
	}

	@Override
	public IPropertyExtensionVisitor runVisitor(GenericParserDocumentBase doc, MSMLTreeValueBase val) {
		IPropertyExtensionVisitor res = super.runVisitor(doc, val);
		val.setText(res.getText());
		try {
			for (MSMLTreeValueBase child : val.getChilds()) {
				MSMLExtensionVisitor visit = new MSMLExtensionVisitor(child);
				for (IMSMLParserExtension ext : doc.getExtensionsToVal(child)) {
					HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(child.getConfig(), ext);
					if (conf.containsKey("version")) {
						((IMSMLParserExtensionTextBased) ext).getTextRetriever(conf.get("version")).getTextToConfig(
								visit, conf);
					}
				}

				// add other props here
				res.setId(visit.getText());
			}

			return res;
		} finally {
			val.setText("");
		}
	}
}
