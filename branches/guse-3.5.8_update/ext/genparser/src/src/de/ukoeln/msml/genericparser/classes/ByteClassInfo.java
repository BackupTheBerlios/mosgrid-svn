package de.ukoeln.msml.genericparser.classes;

public class ByteClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public ByteClassInfo(Class clazz) {
		super(clazz);
	}

	@Override
	public void updateObject(String textToVal) {
		setObject(new Byte(textToVal));
	}
	
	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.NUMERIC);
	}
}
