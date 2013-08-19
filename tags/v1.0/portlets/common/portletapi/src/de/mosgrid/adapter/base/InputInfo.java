package de.mosgrid.adapter.base;

import java.lang.reflect.InvocationTargetException;

import de.mosgrid.adapter.AdapterInfoForMSML;
import de.mosgrid.adapter.ICommandLineParameterInputInfo;
import de.mosgrid.adapter.IInputInfo;
import de.mosgrid.adapter.IUploadableInputInfo;

public abstract class InputInfo implements IInputInfo {

//	private static final Logger LOGGER = LoggerFactory.getLogger(InputInfo.class);
	private String _jobname;
	
	public InputInfo(AdapterInfoForMSML info) {
		_jobname = info.getJob().getId();
	}

	public InputInfo(InputInfo info) {
		_jobname = info._jobname;
	}
	
	public void setJobParameter(AdapterInfoForMSML info) {
		_jobname = info.getJob().getId();
	}
	
	public static InputInfo createDeepCopy(InputInfo info) throws ShallowCopyException  {
		InputInfo clone;
		try {
			clone = info.getClass().getConstructor(info.getClass()).newInstance(info);
		} catch (InstantiationException e) {
			throw new ShallowCopyException("InstantiationException while deepcloning.", e);
		} catch (IllegalAccessException e) {
			throw new ShallowCopyException("IllegalAccessException while deepcloning.", e);
		} catch (IllegalArgumentException e) {
			throw new ShallowCopyException("IllegalArgumentException while deepcloning.", e);
		} catch (InvocationTargetException e) {
			throw new ShallowCopyException("InvocationTargetException while deepcloning.", e);
		} catch (NoSuchMethodException e) {
			throw new ShallowCopyException("NoSuchMethodException while deepcloning.", e);
		} catch (SecurityException e) {
			throw new ShallowCopyException("SecurityException while deepcloning.", e);
		}
		return clone;
	}

	public String getJobName() {
		return _jobname;
	}

	/**
	 * This function returns a value to indicate whether or not this InputInfo
	 * can be uploaded and thus should be passed to the upload-collector;
	 * 
	 * @return Returns true, if
	 */
	public boolean mustBeUploaded() {
		return this instanceof IUploadableInputInfo;
	}

	public boolean mustBeSetToParams() {
		return this instanceof ICommandLineParameterInputInfo;
	}

	protected void shallowCopySetJob(InputInfo info) {
		_jobname = info._jobname;
	}
}
