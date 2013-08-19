package de.mosgrid.adapter;

import de.mosgrid.adapter.base.InputInfo;

public abstract class AdapterForMSMLForInputFile extends AdapterForMSML {

	@Override
	public final InputInfo calculateAdaption() throws AdapterException {
		return getInputInfoForInputFile();
	}

	protected abstract InputInfoForInputFile getInputInfoForInputFile() throws AdapterException;
}
