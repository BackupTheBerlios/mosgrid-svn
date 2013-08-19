package de.ukoeln.msml.genericparser.classes;

public class CharacterClassInfo extends PrimitiveType {

	@SuppressWarnings("rawtypes")
	public CharacterClassInfo(Class clazz) {
		super(clazz);
	}

	@Override
	public void updateObject(String textToVal) {
		setObject(textToVal.charAt(0));
	}
}
