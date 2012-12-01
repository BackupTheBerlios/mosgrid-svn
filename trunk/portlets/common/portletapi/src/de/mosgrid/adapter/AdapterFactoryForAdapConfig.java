package de.mosgrid.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.base.AbstractAdapterFactoryBase;
import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.adapter.defaults.FixedCmdlineAdapter;
import de.mosgrid.adapter.defaults.SimpleCommandLineAdapter;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.util.wrapper.Job;

public abstract class AdapterFactoryForAdapConfig extends AbstractAdapterFactoryBase {

	private final Logger LOGGER = LoggerFactory.getLogger(AdapterFactoryForAdapConfig.class);

	private Map<String, AdapterInstantiatorForMSML<? extends AdapterForMSML>> _adapter =
			new HashMap<String, AdapterInstantiatorForMSML<? extends AdapterForMSML>>();

	private boolean _init;

	@Override
	public final void init() throws InitializationException {
		if (_init)
			throw new InitializationException("Init may be called only once.");

		_init = true;

		registerInternalAdapter();
		registerAdapters();
	}

	private void registerInternalAdapter() {
		JobRefInstantiator jobRefInstantiator = new JobRefInstantiator(this);
		register(jobRefInstantiator);
		register(new SimpleNoConstructorArgumentAdapter<SimpleCommandLineAdapter>(
				SimpleCommandLineAdapter.class, "cmdadapter"));
		register(new SimpleNoConstructorArgumentAdapter<FixedCmdlineAdapter>(
				FixedCmdlineAdapter.class, "fpcmdadap"));
	}

	protected void register(AdapterInstantiatorForMSML<? extends AdapterForMSML> instantiator) {
		if (_adapter.put(instantiator.getID(), instantiator) != null)
			LOGGER.warn("Duplicate adapter discovered: " + instantiator.getID());
	}

	@Override
	public final InputInfoHolder createInputInformation(MSMLTemplate msml) throws AdapterException {
		List<InputInfo> inputs = new ArrayList<InputInfo>();
		for (Job job : msml.getJobListElement().getJobs()) {
			if (job.getInitialization() == null || job.getInitialization().getAdapterConfig() == null)
				continue;
			AdapterConfigType config = job.getInitialization().getAdapterConfig();

			try {
				AdapterForMSML adap = instanciateAdapter(new AdapterInfoForMSML(msml, job.getId(),
						config.getAdapterID()));
				InputInfo inputInfo = adap.calculateAdaption();
				inputs.add(inputInfo);
			} catch (AdapterException e) {
				LOGGER.error("No adapter found for job " + job.getId() + " with adapterid " + config.getAdapterID(), e);
				throw e;
			}
		}
		return new InputInfoHolder(inputs);
	}

	AdapterForMSML instanciateAdapter(AdapterInfoForMSML info) throws AdapterException {
		AdapterForMSML res = overrideAdapterIntantiation(info);
		if (res != null)
			return res;

		AdapterInstantiatorForMSML<? extends AdapterForMSML> adapInit = _adapter.get(info.getAdapterID());
		if (adapInit == null)
			throw new AdapterException("No adapter found for " + info.getAdapterID() + 
					". Valid ids are: " + Arrays.toString(_adapter.keySet().toArray()));

		res = adapInit.createAdapterInstance();
		if (res == null)
			throw new AdapterException("Could not initialize adapter for " + info.getAdapterID());
		res.init(info);
		return res;
	}

	/**
	 * This method is used in sublcasses to override the default mechanism of
	 * creating AdapterForMSML instances. You should use this method if you do
	 * not want the adapters to be identified by their id as you might need more
	 * information to decide which adapter to instantiate. Return null in this
	 * method to use the default mechanism. If you return an instance of
	 * {@link AdapterForMSML} this adapter will be used to create inputs.
	 * 
	 * @return Returns an instance of a subclass of {@link AdapterForMSML} or
	 *         null in case you want to use the default mechanism.
	 */
	protected abstract AdapterForMSML overrideAdapterIntantiation(AdapterInfoForMSML info);

	/**
	 * This method shall be used by subclasses to register their adapters. Those
	 * adapters can be identified by the ID
	 */
	protected abstract void registerAdapters();

	// EXCEPTIONS

	public class InitializationException extends Exception {
		private static final long serialVersionUID = 3949109187787529430L;

		public InitializationException(String string) {
			super(string);
		}

	}

	private class JobRefInstantiator extends AdapterInstantiatorForMSML<JobRefAdapter> {

		private AdapterFactoryForAdapConfig _factory;

		public JobRefInstantiator(AdapterFactoryForAdapConfig factory) {
			super("jobref");
			_factory = factory;
		}

		@Override
		public JobRefAdapter createAdapterInstance() {
			return new JobRefAdapter(_factory);
		}
	}
}
