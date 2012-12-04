package de.ukoeln.msml.genericparser.classes;

public class LongClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public LongClassInfo(Class clazz) {
		super(clazz);
	}

	@Override
	public void updateObject(String textToVal) {
		setObject(new Long(textToVal));
	}
	
	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.NUMERIC);
	}
}
