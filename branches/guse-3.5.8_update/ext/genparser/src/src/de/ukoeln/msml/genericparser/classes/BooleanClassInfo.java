package de.ukoeln.msml.genericparser.classes;


public class BooleanClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public BooleanClassInfo(Class clazz) {
		super(clazz);
	}

	@Override
	public void updateObject(String textToVal) {
		setObject(new Boolean(textToVal));
	}
}
