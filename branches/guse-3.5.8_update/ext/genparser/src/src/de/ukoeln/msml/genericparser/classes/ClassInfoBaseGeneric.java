package de.ukoeln.msml.genericparser.classes;

import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;

public abstract class ClassInfoBaseGeneric<V extends IMSMLExtensionVisitor, O> extends ClassInfoBase {
	
	protected O _object;

	public ClassInfoBaseGeneric(String name) {
		super(name);
	}

	protected abstract V getVisitorInstance(MSMLTreeValueBase val);
	
	public O getObject() {
		return _object;
	}
	
	protected void setObject(O obj) {
		_object = obj;
	}
	
	public void clearObject() {
		_object = null;
	}
	
	public abstract V runVisitor(GenericParserDocumentBase doc, MSMLTreeValueBase val);

	public boolean getLayerEnabled() {
		return false;
	}
}
