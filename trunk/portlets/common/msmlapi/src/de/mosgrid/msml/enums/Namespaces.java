package de.mosgrid.msml.enums;

public enum Namespaces {
	ENVIRONMENT("http://www.xml-cml.org/dictionary/environment/", "env"), 
	COMPCHEM("http://www.xml-cml.org/dictionary/compchem/", "compchem"), 
	ADAPTER("http://www.xml-cml.org/dictionary/adaptercollection", "adap"),
	PARSER("http://www.xml-cml.org/dictionary/parser/", "parser"),
	CONVENTION("http://www.xml-cml.org/convention/", "convention"),
	UNITS("http://www.xml-cml.org/unit/units/", "units"),
	UNITTYPE("http://www.xml-cml.org/unit/unitType/", "unitType"),
	SI("http://www.xml-cml.org/unit/si/", "si"),
	NONSI("http://www.xml-cml.org/unit/nonSi/", "nonsi"),
	CMLX("http://www.xml-cml.org/schema/cmlx", "cmlx"),
	XHTML("http://www.w3.org/1999/xhtml", "xhtml"),
	XSD("http://www.w3.org/2001/XMLSchema", "xsd"),
	DC("http://purl.org/dc/elements/1.1/", "dc");

	private String _namespace;
	private String _defaultPrefix;

	private Namespaces(String namespace, String defaultPrefix) {
		_namespace = namespace;
		_defaultPrefix = defaultPrefix;
	}

	public String getNamespace() {
		return _namespace;
	}

	public String getDefaultPrefix() {
		return _defaultPrefix;
	}
	
	public static Namespaces getNamespaceValue(String namespace) {
		if (namespace == null || "".equals(namespace))
			return null;
		for (Namespaces ns : values()) {
			if (namespace.equals(ns.getNamespace())) {
				return ns;
			}	
		}
		return null;
	}
}
