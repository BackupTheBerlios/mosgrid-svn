package de.mosgrid.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.adapter.base.ShallowCopyException;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.wrapper.Job;

/**
 * This adapter holds a reference to another job and copies its adapter.
 * 
 */
public class JobRefAdapter extends AdapterForMSMLWithDictionary {
	private final Logger LOGGER = LoggerFactory.getLogger(JobRefAdapter.class);

	private AdapterFactoryForAdapConfig _factory;

	public JobRefAdapter(AdapterFactoryForAdapConfig factory) {
		_factory = factory;
	}

	@Override
	public InputInfo calculateAdaption() throws AdapterException{
		ParameterType param = getParameterFromAdapterConfig("jobref");
		String val = param.getScalar().getValue();
		Job job = getInfo().getMSML().getJobWithID(val);
		String adaptID = job.getInitialization().getAdapterConfig().getAdapterID();

		AdapterInfoForMSML info = new AdapterInfoForMSML(getInfo().getMSML(), job.getId(), adaptID);
		try {
			AdapterForMSML referredAdapter = _factory.instanciateAdapter(info);
			InputInfo foreignResult = referredAdapter.calculateAdaption();
			InputInfo realResult = InputInfo.createDeepCopy(foreignResult);
			realResult.setJobParameter(getInfo());
			return realResult;
		} catch (AdapterException e) {
			LOGGER.error("Could not instantiate refferenced logger.", e);
			throw e;
		} catch (ShallowCopyException e) {
			LOGGER.error("Exception on creating shallow copy for job: " + info.getJob().getId(), e);
			throw new AdapterException("Exception on creating shallow copy for job: " + info.getJob().getId());
		}
	}

	@Override
	protected String getNamespace() {
		return "http://www.xml-cml.org/dictionary/adapter/jobref";
	}

	@Override
	public void doInit() {
		// nothing to do.
	}

}
