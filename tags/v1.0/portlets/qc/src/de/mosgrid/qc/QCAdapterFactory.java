package de.mosgrid.qc;

import de.mosgrid.adapter.AdapterFactoryForAdapConfig;
import de.mosgrid.adapter.AdapterForMSML;
import de.mosgrid.adapter.AdapterInfoForMSML;
import de.mosgrid.adapter.SimpleNoConstructorArgumentAdapter;
import de.mosgrid.qc.adapter.GaussianAdapter03;
import de.mosgrid.qc.adapter.GaussianAdapter09;

public class QCAdapterFactory extends AdapterFactoryForAdapConfig {

	@Override
	protected AdapterForMSML overrideAdapterIntantiation(AdapterInfoForMSML info) {
		return null;
	}

	@Override
	protected void registerAdapters() {
		register(new GaussianAdapterInitializer03());
		register(new GaussianAdapterInitializer09());
	}
	
	private class GaussianAdapterInitializer03 extends SimpleNoConstructorArgumentAdapter<GaussianAdapter03> {

		public GaussianAdapterInitializer03() {
			super(GaussianAdapter03.class, GaussianAdapter03.ID);
		}
	}

	private class GaussianAdapterInitializer09 extends SimpleNoConstructorArgumentAdapter<GaussianAdapter09> {

		public GaussianAdapterInitializer09() {
			super(GaussianAdapter09.class, GaussianAdapter09.ID);
		}
	}
}
