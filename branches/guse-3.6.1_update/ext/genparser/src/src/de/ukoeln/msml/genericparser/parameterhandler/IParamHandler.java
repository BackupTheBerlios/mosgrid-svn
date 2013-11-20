package de.ukoeln.msml.genericparser.parameterhandler;

import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;

public interface IParamHandler {
	String getParameterName();
	void handle(GenericParserMainDocument doc);
	float getWeight();
	IParamHandler getInstance(ParameterType param);
}
