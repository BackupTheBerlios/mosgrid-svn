package de.mosgrid.adapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.util.TempFileHelper;
import de.mosgrid.util.UploadCollector;

public class InputInfoForInputFile extends InputInfo implements IUploadableInputInfo {
	private final Logger LOGGER = LoggerFactory.getLogger(InputInfoForInputFile.class);

	private String _inputFilecontent;
	private String _portname;
	private String _extension;

	public InputInfoForInputFile(String inputFileContent, AdapterInfoForMSML info) {
		super(info);
		_portname = info.getPortName();
		_inputFilecontent = inputFileContent;
		_extension = info.getConfig().getFileExtension();
	}

	public InputInfoForInputFile(InputInfoForInputFile info) {
		super(info);
		InputInfoForInputFile castInfo = (InputInfoForInputFile) info;
		_portname = castInfo._portname;
		_inputFilecontent = castInfo._inputFilecontent;
		_extension = castInfo._extension;
	}

	@Override
	public void setJobParameter(AdapterInfoForMSML info) {
		super.setJobParameter(info);
		_portname = info.getPortName();
	}

	public String getPortName() {
		return _portname;
	}

	public String getFileExtension() {
		return _extension;
	}

	// TODO decide whether to return a stream or string or nothing and handle
	// upload here.
	public String getInputfileContent() {
		return _inputFilecontent;
	}

	// /**
	// * This method is used to retrieve a temporary file with the generated content.
	// *
	// * @return A temporary file.
	// * @throws IOException
	// */
	// @Override
	// public List<File> getFiles() throws IOException {
	// List<File> fileList = new ArrayList<File>();
	//
	// File file = TempFileHelper.createTempFile(getJobName() + "_input", getFileExtension());
	// FileWriter sw = null;
	// try {
	// sw = new FileWriter(file);
	// sw.write(getInputfileContent());
	// } finally {
	// if (sw != null)
	// sw.close();
	// }
	// fileList.add(file);
	// return fileList;
	// }

	@Override
	public void addUploads(UploadCollector collector) {
		FileWriter sw = null;
		try {
			File file = TempFileHelper.createTempFile(getJobName() , _extension);
			sw = new FileWriter(file);
			sw.write(getInputfileContent());

			collector.addUpload(file, _portname, getJobName());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (sw != null)
				try {
					sw.close();
				} catch (IOException e) {

				}
		}
	}
}
