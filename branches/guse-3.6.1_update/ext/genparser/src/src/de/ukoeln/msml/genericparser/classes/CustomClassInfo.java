package de.ukoeln.msml.genericparser.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.MSMLCountExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFileSelectorExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFixedTextExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLLimiterExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLRegexExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLReplaceExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;

public abstract class CustomClassInfo<V extends IMSMLExtensionVisitor, O> 
	extends ClassInfoBaseGeneric<V, O> {

	private static Hashtable<Class<? extends ClassInfoBase>, 
		List<Class<? extends IMSMLParserExtension>>> _allowedExtensions = 
			new Hashtable<Class<? extends ClassInfoBase>, 
				List<Class<? extends IMSMLParserExtension>>>();

	protected CustomClassInfo(String name) {
		super(name);
		initExtensions();
	}

	private List<Class<? extends IMSMLParserExtension>> initExtensions() {
		if (_allowedExtensions.containsKey(this.getClass()))
			return _allowedExtensions.get(this.getClass());
		List<Class<? extends IMSMLParserExtension>> res = new ArrayList<Class<? extends IMSMLParserExtension>>();
		addExtensions(res);
		_allowedExtensions.put(this.getClass(), res);
		return res;
	}

	protected void addExtensions(List<Class<? extends IMSMLParserExtension>> list) {
		list.add(MSMLCountExtension.class);
		list.add(MSMLFileSelectorExtension.class);
		list.add(MSMLFixedTextExtension.class);
		list.add(MSMLLimiterExtension.class);
		list.add(MSMLRegexExtension.class);
		list.add(MSMLReplaceExtension.class);
	}
	
	@Override
	public V runVisitor(GenericParserDocumentBase doc, MSMLTreeValueBase val) {
		V visit = getVisitorInstance(val);
		for (IMSMLParserExtension ext : doc.getExtensionsToVal(val)) {
			HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(val.getConfig(), ext);
			if (conf.containsKey("version")) {
				ext.handleExtensionVisitor(conf, visit);
			}
		}
		return visit;
	}

	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.CUSTOM;
	}
	
	@Override
	public boolean isPrimitive() {
		return false;
	}
	
	public final boolean applysTo(IMSMLParserExtension cmp) {
		List<Class<? extends IMSMLParserExtension>> exts = 
				_allowedExtensions.get(this.getClass());
		return exts.contains(cmp.getClass());
	}

	// TODO implement that mechanism for class info too.
	public abstract void absorbVisitor(V res);

	public abstract void buildUp(MSMLSimpleTreeValue val, GenericParserConfig conf);
}
