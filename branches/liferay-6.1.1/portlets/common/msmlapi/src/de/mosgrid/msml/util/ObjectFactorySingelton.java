package de.mosgrid.msml.util;

import de.mosgrid.msml.jaxb.bindings.ObjectFactory;

public class ObjectFactorySingelton {

	private static ObjectFactory _fact;

	private ObjectFactorySingelton() {

	}

	public static ObjectFactory getFactory() {
		if (_fact == null)
			_fact = new ObjectFactory();
		return _fact;
	}
}
