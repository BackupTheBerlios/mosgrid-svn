package de.mosgrid.adapter.defaults;

import de.mosgrid.adapter.AdapterFactoryForAdapConfig;
import de.mosgrid.adapter.AdapterForMSML;
import de.mosgrid.adapter.AdapterInfoForMSML;
import de.mosgrid.adapter.SimpleNoConstructorArgumentAdapter;

public class DefaultAdapterFactory extends AdapterFactoryForAdapConfig {

	@Override
	protected AdapterForMSML overrideAdapterIntantiation(AdapterInfoForMSML info) {
		return null;
	}

	@Override
	protected void registerAdapters() {
		register(new SimpleNoConstructorArgumentAdapter<SimpleAdapter>(
				SimpleAdapter.class, "mdadapter"));
	}

}
