package de.ukoeln.msml.genericparser.classes;

import java.lang.reflect.Field;
import java.util.Hashtable;

public class MSMLClassInfo extends ClassInfo {

	private Hashtable<String, Field> _fields;

	@SuppressWarnings("rawtypes")
	protected MSMLClassInfo(Class clazz) {
		super(clazz);
	
		Hashtable<String, Field> declaredFields = new Hashtable<String, Field>();
		for (Field field : clazz.getDeclaredFields()) {
			declaredFields.put(field.getName(), field);
		}
		_fields = declaredFields;
	}
		
	@Override
	public Hashtable<String, Field> getProperties() {
		return _fields;
	}

	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.MSML;
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
	public void updateObject(String textToVal) {
	}
}
