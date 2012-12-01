package de.mosgrid.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets passed with the 'beforeSubmit' method in input-masks. Shall collect all uploaded files which user has uploaded
 * in corresponding input-mask. Also gets passed further to the 'submit' method in the portlet. There it further
 * collects the msml file or any other input files which are created by the adapter.
 * 
 * @author Andreas Zink
 * 
 */
public class UploadCollector {

	private final Logger LOGGER = LoggerFactory.getLogger(UploadCollector.class);

	private List<UploadBean> allUploads;
	private Map<String, Collection<File>> job2uploads;
	private Map<File, String> upload2port;

	public UploadCollector() {
		allUploads = new ArrayList<UploadBean>();
		job2uploads = new HashMap<String, Collection<File>>();
		upload2port = new HashMap<File, String>();
	}

	/**
	 * Adds an uploaded file to collector
	 * 
	 * @param upload
	 *            The uploaded file
	 * @param port
	 *            The asm job port where this file belongs to
	 * @param jobId
	 *            The job where this file belongs to
	 */
	public void addUpload(File upload, String port, String jobId) {
		UploadBean uploadBean = new UploadBean(upload, jobId, port);
		allUploads.add(uploadBean);
		if (!job2uploads.containsKey(jobId)) {
			job2uploads.put(jobId, new ArrayList<File>());
		}
		job2uploads.get(jobId).add(upload);
		upload2port.put(upload, port);
		
		LOGGER.trace("Adding upload "+uploadBean);
	}

	// /**
	// * This method adds the generated inputfiles to the upload-collector.
	// *
	// * @param info
	// * The InputInfo for inputfile-objects.
	// * @throws InputSupplyException
	// */
	// public void addUpload(IUploadableInputInfo info) throws InputSupplyException {
	// if (info.getJobName() == null || info.getPortName() == null || info.getFileExtension() == null)
	// throw new InputSupplyException("Neither jobname, portname nor file extension may be null.");
	// File file = null;
	// try {
	// file = info.getFile();
	// } catch (IOException e) {
	// LOGGER.error("Could not create file for inputinfo: " + info.getJobName() + " : " + info.getPortName());
	// return;
	// }
	// addUpload(file, info.getPortName(), info.getJobName());
	// }

	/**
	 * Adds a xfs link to an already uploaded file
	 * 
	 * @param xfsURL
	 *            The full xfs url to file
	 * @param port
	 *            The asm job port where this file belongs to
	 * @param jobId
	 *            The job where this file belongs to
	 */
	public void addUploaded(String xfsURL, String port, String jobId) {
		UploadBean uploadBean = new UploadBean(xfsURL, jobId, port);
		allUploads.add(uploadBean);
		LOGGER.trace("Adding upload "+uploadBean);
	}

	public void addUpload(UploadBean uploadBean) {
		allUploads.add(uploadBean);
	}

	/**
	 * @return All uploads of input mask
	 */
	// public Collection<File> getAllFiles() {
	// ArrayList<File> allFiles = new ArrayList<File>();
	// for (Collection<File> fileList : job2uploads.values()) {
	// allFiles.addAll(fileList);
	// }
	// return Collections.unmodifiableCollection(allFiles);
	// }

	/**
	 * @return All uploads of input mask
	 */
	public Collection<UploadBean> getAllUploads() {
		return Collections.unmodifiableCollection(allUploads);
	}

	/**
	 * @return All uploads of a job
	 */
	public Collection<File> getAllUploads(String jobId) {
		return job2uploads.get(jobId);
	}

	/**
	 * @return 'true' if the job has assigned uploads
	 */
	public boolean hasUploads(String jobId) {
		return job2uploads.containsKey(jobId);
	}

	/**
	 * @return The port where the file shall go to
	 */
	public String getPort(File upload) {
		return upload2port.get(upload);
	}

	/**
	 * @return 'true' if one or more uploads have been collected
	 */
	public boolean hasUplaods() {
		return upload2port.size() > 0;
	}

	/**
	 * Marks all files for deletion
	 */
	public void deleteTempFiles() {
		for (Collection<File> jobFiles : job2uploads.values()) {
			for (File file : jobFiles) {
				file.delete();
			}
		}
	}
}
