package de.mosgrid.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class which gathers all information for an upload i.e. file, job, port and xfs-url
 * 
 */
public class UploadBean {
	private List<File> fileList;
	private String jobname;
	private String port;
	private String xfsUrl;

	public UploadBean() {
		fileList = new ArrayList<File>();
	}

	/**
	 * Bean for a file which shall get uploaded to XFS
	 * 
	 * @param file
	 *            The local file
	 * @param jobname
	 *            The name of the job where the file shall go
	 * @param port
	 *            The input port name
	 */
	public UploadBean(File file, String jobname, String port) {
		this();
		addFile(file);
		setJobname(jobname);
		setPort(port);
	}

	/**
	 * Bean which acts as a link for an already uploaded XFS file
	 * 
	 * @param xfsURL
	 *            The full xfs url to file
	 * @param jobname
	 *            The name of the job where the file shall go
	 * @param port
	 *            The input port name
	 */
	public UploadBean(String xfsURL, String jobname, String port) {
		this();
		setUrl(xfsURL);
		setJobname(jobname);
		setPort(port);
	}

	public List<File> getFiles() {
		return fileList;
	}

	public void addFile(File file) {
		this.fileList.add(file);
	}

	public boolean hasFiles() {
		return fileList.size() > 0;
	}

	public String getJobname() {
		return jobname;
	}

	public void setJobname(String jobname) {
		this.jobname = jobname.trim();
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port.trim();
	}

	public String getUrl() {
		return xfsUrl;
	}

	public void setUrl(String url) {
		this.xfsUrl = url.trim();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		if (jobname != null) {
			stringBuilder.append("\n\tJOB: " + jobname);
		}
		if (port != null) {
			stringBuilder.append("\n\tPORT: " + port);
		}
		if (hasFiles()) {
			for (File file : fileList) {
				stringBuilder.append("\n\tFILE: " + file);
			}
		}
		if (xfsUrl != null) {
			stringBuilder.append("\n\tURL: " + xfsUrl);
		}

		return stringBuilder.toString();
	}

}
