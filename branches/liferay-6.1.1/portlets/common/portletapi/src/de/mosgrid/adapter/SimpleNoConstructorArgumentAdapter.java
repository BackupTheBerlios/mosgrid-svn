package de.mosgrid.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleNoConstructorArgumentAdapter<T extends AdapterForMSML> extends AdapterInstantiatorForMSML<T> {

	private final Logger LOGGER = LoggerFactory.getLogger(SimpleNoConstructorArgumentAdapter.class);
	private Class<T> _class;

	public SimpleNoConstructorArgumentAdapter(Class<T> clazz, String adapterID) {
		super(adapterID);
		_class = clazz;
	}

	@Override
	public T createAdapterInstance() {
		try {
			return _class.newInstance();
		} catch (InstantiationException e) {
			LOGGER.error("Could not instantiate " + _class.getName() + ". A constructor with no arguments is needed.");
		} catch (IllegalAccessException e) {
			LOGGER.error("Could not access constructor of " + _class.getName());
		}
		return null;
	}
}
