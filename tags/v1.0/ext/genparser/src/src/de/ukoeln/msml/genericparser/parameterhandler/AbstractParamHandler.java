package de.ukoeln.msml.genericparser.parameterhandler;

import de.mosgrid.msml.jaxb.bindings.ParameterType;

public abstract class AbstractParamHandler implements IParamHandler {
	
	protected ParameterType _param;

	public AbstractParamHandler() {}
	
	public AbstractParamHandler(ParameterType param) {
		super();
		_param = param;
	}
}
