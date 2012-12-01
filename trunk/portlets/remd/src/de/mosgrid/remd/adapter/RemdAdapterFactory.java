package de.mosgrid.remd.adapter;

import de.mosgrid.adapter.SimpleNoConstructorArgumentAdapter;
import de.mosgrid.adapter.defaults.DefaultAdapterFactory;

public class RemdAdapterFactory extends DefaultAdapterFactory {

	@Override
	protected void registerAdapters() {
		super.registerAdapters();

		register(new SimpleNoConstructorArgumentAdapter<RemdAdapter>(RemdAdapter.class, "mdpAdapterForRemd"));
	}

}
