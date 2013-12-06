package de.ukoeln.msml.genericparser.classes;

public class DoubleClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public DoubleClassInfo(Class clazz) {
		super(clazz);
	}
	
	@Override
	public void updateObject(String textToVal) {
		setObject(new Double(textToVal));
	}
	
	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.NUMERIC);
	}
}
