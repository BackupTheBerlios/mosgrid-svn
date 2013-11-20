package de.ukoeln.msml.genericparser.classes;

import java.math.BigInteger;

public class BigIntegerClassInfo extends PrimitiveType {
	
	@SuppressWarnings("rawtypes")
	public BigIntegerClassInfo(Class clazz) {
		super(clazz);
	}

	@Override
	public void updateObject(String textToVal) {
		setObject(new BigInteger(textToVal));
	}
	
	@Override
	public CLASSTYPE getType() {
		return CLASSTYPE.or(CLASSTYPE.PRIMITIVE, CLASSTYPE.NUMERIC);
	}
}
