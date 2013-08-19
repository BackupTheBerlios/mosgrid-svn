package de.mosgrid.adapter;

import de.mosgrid.adapter.base.AdapterInstantiator;

public abstract class AdapterInstantiatorForMSML<T extends AdapterForMSML> extends AdapterInstantiator {

	private String _id;

	public AdapterInstantiatorForMSML(String id) {
		_id = id;
	}

	public abstract T createAdapterInstance();

	public String getID() {
		return _id;
	}
}
