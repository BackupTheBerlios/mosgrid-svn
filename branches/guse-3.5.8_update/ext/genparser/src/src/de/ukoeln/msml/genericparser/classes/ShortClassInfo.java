package de.ukoeln.msml.genericparser.classes;

public class ShortClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public ShortClassInfo(Class clazz) {
		super(clazz);
	}
	
	@Override
	public void updateObject(String textToVal) {
		setObject(new Short(textToVal));
	}
	
	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.NUMERIC);
	}
}
