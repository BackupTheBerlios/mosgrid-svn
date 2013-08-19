package de.mosgrid.adapter;

import de.mosgrid.adapter.base.InputInfo;

public abstract class AdapterForMSMLForParameter extends AdapterForMSML {

	@Override
	public final InputInfo calculateAdaption() {
		return getInputInfoForParameter();
	}

	protected abstract InputInfoForParameter getInputInfoForParameter();
}
