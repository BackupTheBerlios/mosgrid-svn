package de.ukoeln.msml.genericparser.classes;

public class CLASSTYPE {
	public static final CLASSTYPE MSML = new CLASSTYPE(1);
	public static final CLASSTYPE PRIMITIVE = new CLASSTYPE(2);
	public static final CLASSTYPE LIST = new CLASSTYPE(4);
	public static final CLASSTYPE LAX = new CLASSTYPE(8);
	public static final CLASSTYPE NUMERIC = new CLASSTYPE(16);
	public static final CLASSTYPE CUSTOM = new CLASSTYPE(32);

	private final int _ord;

	private CLASSTYPE(int ord) {
		_ord = ord;
	}

	public boolean is(CLASSTYPE other) {
		return _ord == other._ord;
	}

	public boolean contains(CLASSTYPE other) {
		return (_ord & other._ord) != 0;
	}

	public static CLASSTYPE or(CLASSTYPE type1, CLASSTYPE type2) {
		return new CLASSTYPE(type1._ord | type2._ord);
	}
}

