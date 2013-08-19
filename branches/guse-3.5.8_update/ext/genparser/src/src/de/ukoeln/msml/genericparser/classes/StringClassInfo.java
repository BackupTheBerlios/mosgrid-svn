package de.ukoeln.msml.genericparser.classes;

public class StringClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public StringClassInfo(Class clazz) {
		super(clazz);
	}
	
	@Override
	public void updateObject(String textToVal) {
		setObject(new String(textToVal));
	}
}
