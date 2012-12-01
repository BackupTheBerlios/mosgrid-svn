package de.mosgrid.adapter;

import de.mosgrid.adapter.base.InputInfo;

/**
 * Basic adapter class which generates more than just one input file e.g. in parameter sweep case
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AdapterForMSMLForInputFiles extends AdapterForMSML {

	@Override
	public InputInfo calculateAdaption() {
		return getInputInfoForInputFiles();
	}

	protected abstract InputInfoForInputFiles getInputInfoForInputFiles();

}
