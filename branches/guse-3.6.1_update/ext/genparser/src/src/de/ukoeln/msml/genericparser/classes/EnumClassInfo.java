package de.ukoeln.msml.genericparser.classes;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class EnumClassInfo extends ClassInfo {

	@SuppressWarnings("rawtypes")
	protected EnumClassInfo(Class clazz) {
		super(clazz);
	}
		
	@Override
	public Hashtable<String, Field> getProperties() {
		return new Hashtable<String, Field>();
	}

	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.PRIMITIVE;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public boolean isUntypedNonAnyList() {
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void updateObject(String textToVal) {
		setObject(Enum.valueOf((Class<Enum>)getClassObj(), textToVal));
	}

	@Override
	public void setValue(String propName, ClassInfo classInfo) {
		System.out.println("Should not reach this code: creating enum with class-info.");	
	}
}