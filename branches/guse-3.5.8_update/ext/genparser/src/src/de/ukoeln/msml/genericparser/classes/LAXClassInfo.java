package de.ukoeln.msml.genericparser.classes;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class LAXClassInfo extends ClassInfo {

	protected LAXClassInfo(Class<?> clazz) {
		super(clazz);
	}

	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.LAX;
	}

	@Override
	public Hashtable<String, Field> getProperties() {
		return null;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isUntypedNonAnyList() {
		return false;
	}

	@Override
	public void setValue(String _propName, ClassInfo classInfo) {
	}

	@Override
	public void updateObject(String textToVal) {
	}
}
