package de.mosgrid.adapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.util.TempFileHelper;
import de.mosgrid.util.UploadBean;
import de.mosgrid.util.UploadCollector;

/**
 * InputInfo for more than just one input file. Can be used for parameter sweep workflows
 * 
 * @author Andreas Zink
 * 
 */
public class InputInfoForInputFiles extends InputInfo implements IUploadableInputInfo {
	private final Logger LOGGER = LoggerFactory.getLogger(InputInfoForInputFiles.class);

	private List<String> fileContentList = new ArrayList<String>();
	private String portname;
	private String extension;

	public InputInfoForInputFiles(AdapterInfoForMSML info) {
		super(info);
		this.portname = info.getPortName();
		this.extension = info.getConfig().getFileExtension();
	}

	public void addFileContent(String content) {
		fileContentList.add(content);
	}

	public void addFileContent(StringBuilder contentBuilder) {
		fileContentList.add(contentBuilder.toString());
	}

	public void addAll(StringBuilder[] contentBuilders) {
		for (StringBuilder builder : contentBuilders) {
			addFileContent(builder);
		}
	}

	@Override
	public void addUploads(UploadCollector collector) {
		UploadBean uploadBean = new UploadBean();
		uploadBean.setJobname(getJobName());
		uploadBean.setPort(portname);
		for (int i = 0; i < fileContentList.size(); i++) {
			String fileContent = fileContentList.get(i);
			BufferedWriter fileWriter = null;
			try {
				File file = TempFileHelper.createTempFile(getJobName() + "Input", extension + "_" + i);
				fileWriter = new BufferedWriter(new FileWriter(file));
				fileWriter.write(fileContent);

				uploadBean.addFile(file);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				if (fileWriter != null)
					try {
						fileWriter.close();
					} catch (IOException e) {
						LOGGER.warn("Coul not close file outputstream...", e);
					}
			}
		}
		collector.addUpload(uploadBean);
	}

}
