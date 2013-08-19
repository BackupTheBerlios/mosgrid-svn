package de.mosgrid.adapter;

import de.mosgrid.adapter.base.AbstractAdapterInfo;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.util.wrapper.Job;

public class AdapterInfoForMSML extends AbstractAdapterInfo {

	private final MSMLTemplate _msml;
	private final String _jobId;
	private final String _adapterId;

	public AdapterInfoForMSML(MSMLTemplate msml, String jobId, String adapterID) {
		_msml = msml;
		_jobId = jobId;
		_adapterId = adapterID;
	}

	public Job getJob() {
		return _msml.getJobWithID(_jobId);
	}

	public MSMLTemplate getMSML() {
		return _msml;
	}

	public AdapterConfigType getConfig() {
		return getJob().getInitialization().getAdapterConfig();
	}

	public String getAdapterID() {
		return _adapterId;
	}

	public String getPortName() {
		return getConfig().getPortName();
	}

}
