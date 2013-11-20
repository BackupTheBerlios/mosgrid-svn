package de.mosgrid.adapter.base;

import de.mosgrid.adapter.AdapterException;
import de.mosgrid.adapter.AdapterFactoryForAdapConfig.InitializationException;
import de.mosgrid.adapter.InputInfoHolder;
import de.mosgrid.msml.editors.MSMLTemplate;

public abstract class AbstractAdapterFactoryBase {

	public abstract InputInfoHolder createInputInformation(MSMLTemplate msml) throws AdapterException;
	
	public abstract void init() throws InitializationException;

}
