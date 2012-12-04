package de.mosgrid.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.jaxb.bindings.ParameterType;

public abstract class AdapterForMSMLWithDictionary extends AdapterForMSML {

	private Logger LOGGER = LoggerFactory.getLogger(AdapterForMSMLWithDictionary.class);

	protected abstract String getNamespace();

	protected ParameterType getParameterFromAdapterConfig(String id) {
		String prefix = getInfo().getMSML().getPrefixToNamespace(getNamespace());
		for (ParameterType param : getInfo().getConfig().getParameter()) {
			if (param.getDictRef() != null && param.getDictRef().equals(prefix + ":" + id))
				return param;
		}
		LOGGER.error("Could not find parameter for id " + id + " for Adapter " + getInfo().getAdapterID());
		return null;
	}

	protected String getParameterFromParameterList(String id) {
		return getParameterFromParameterList(id, false);
	}
	
	protected String getParameterFromParameterList(String id, boolean optional) {
		String prefix = getInfo().getMSML().getPrefixToNamespace(getNamespace());
		for (ParameterType param : getInfo().getJob().getInitialization().getParamList().getParameter()) {
			if (param.getDictRef() != null && param.getDictRef().equals(prefix + ":" + id))
				return param.getScalar().getValue();
		}
		if (!optional)
			LOGGER.error("Could not find parameter for id " + id + " for Adapter " + getInfo().getAdapterID());
		return null;
	}
}
