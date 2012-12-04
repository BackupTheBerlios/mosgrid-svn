package de.ukoeln.msml.genericparser.classes;

import java.lang.reflect.Field;
import java.util.Hashtable;

public abstract class PrimitiveType extends ClassInfo {

	protected PrimitiveType(Class<?> clazz) {
		super(clazz);
	}

	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.PRIMITIVE;
	}

	@Override
	public Hashtable<String, Field> getProperties() {
		return new Hashtable<String, Field>();
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public boolean isUntypedNonAnyList() {
		return false;
	}

	@Override
	public void setValue(String _propName, ClassInfo classInfo) {
		System.out.println("Should not reach this code: creating primitive with class-info.");
	}
	
	public abstract void updateObject(String textToVal);
}
