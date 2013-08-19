package de.mosgrid.remd.adapter;

import de.mosgrid.adapter.defaults.ParameterSweepAdapter;
import de.mosgrid.msml.jaxb.bindings.EntryType;

public class RemdAdapter extends ParameterSweepAdapter {

	@Override
	protected void appendAllContentBuilders(StringBuilder[] fileContentBuilderArray, EntryType entry, String[] values) {
		for (int i = 0; i < fileContentBuilderArray.length; i++) {
			StringBuilder contentBuilder = fileContentBuilderArray[i];
			contentBuilder.append(entry.getTerm() + " = " + values[i] + " " + values[i] + "\n");
		}
	}
}
