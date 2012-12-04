package de.ukoeln.msml.genericparser.classes;

import java.util.ArrayList;
import java.util.List;

import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.ClassInfo.ClassInfoFactory;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFileSelectorExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;


public class SimpleRootInfo extends CustomClassInfo<MSMLExtensionVisitor, Object> {

	public SimpleRootInfo() {
		super("ROOT");
	}

	@Override
	protected void addExtensions(List<Class<? extends IMSMLParserExtension>> list) {
		list.add(MSMLFileSelectorExtension.class);
	}

	public void updateAllFromRoot(MSMLSimpleTreeValue val) {
		ArrayList<Object> obj = new ArrayList<Object>();
		setObject(obj);
		
		for (MSMLTreeValueBase child : val.getChilds()) {
			child.setInObj(val.getClassInfo());
		}
	}

	@Override
	public MSMLExtensionVisitor getVisitorInstance(MSMLTreeValueBase val) {
		return new MSMLExtensionVisitor(val);
	}

	@Override
	public void absorbVisitor(MSMLExtensionVisitor res) {
		throw new UnsupportedOperationException("Dont call this on simple root.");
	}

	@Override
	public void buildUp(MSMLSimpleTreeValue val, GenericParserConfig conf) {
		for (GenericParserConfig cconf : conf.getChildConfig()) {
			Class<?> clazz = null;
			try {
				clazz = Class.forName(cconf.getCanonicalClassName());
			} catch (ClassNotFoundException e) {
				System.out.println("could not find class " + cconf.getCanonicalClassName());
				continue;
			}
			
			MSMLSimpleTreeValue newVal = (MSMLSimpleTreeValue) val.createAndAddOneNode(
					ClassInfoFactory.getClassInfo(clazz), cconf.getPropertyName(), false, true);
			newVal.setConfig(cconf.getExtensionConfigCollection());
			newVal.getClassInfo().buildUp(newVal, cconf);
		}
	}
}
