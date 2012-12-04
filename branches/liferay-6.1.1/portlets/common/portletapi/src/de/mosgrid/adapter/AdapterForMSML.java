package de.mosgrid.adapter;

import de.mosgrid.adapter.base.AbstractAdapterBase;

public abstract class AdapterForMSML extends AbstractAdapterBase {

	private AdapterInfoForMSML _info;

	public AdapterForMSML() {

	}

	void init(AdapterInfoForMSML info) {
		_info = info;
	}

	public abstract void doInit();

	protected AdapterInfoForMSML getInfo() {
		return _info;
	}
}
