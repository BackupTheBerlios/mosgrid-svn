package de.ukoeln.msml.genericparser.classes;

public class IntegerClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public IntegerClassInfo(Class clazz) {
		super(clazz);
	}
	
	@Override
	public void updateObject(String textToVal) {
		setObject(new Integer(textToVal));
	}
	
	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.NUMERIC);
	}
}
