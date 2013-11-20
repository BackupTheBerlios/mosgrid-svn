package de.ukoeln.msml.genericparser.classes;

public class FloatClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public FloatClassInfo(Class clazz) {
		super(clazz);
	}
	
	@Override
	public void updateObject(String textToVal) {
		setObject(new Float(textToVal));
	}
	
	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.NUMERIC);
	}
}
