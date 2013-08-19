package de.mosgrid.remd.properties;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Loads properties for monitoring tab. The properties file contains file formats which are used to decide how to open
 * files in the monitoring tab.
 * 
 * @author Andreas Zink
 * 
 */
public class MonitoringProperties {

	private static final String KEY_RAW_TEXT = "RAW_TEXT_TYPES";
	private static final String KEY_CHEMDOODLE = "CHEMDOODLE_TYPES";
	private static final String KEY_JMOL = "JMOL_TYPES";
	private static final String KEY_DYGRAPH = "DYGRAPH_TYPES";
	private static final String KEY_IMAGES = "IMAGE_TYPES";

	/* list of filetypes */
	// can be opened in raw text mode
	private List<String> rawTextFormats;
	// can be opened witch chemdoodle
	private List<String> chemdoodleFormats;
	// can be opened with jmol
	private List<String> jmolFormats;
	// can be opened with dygraph
	private List<String> graphFormats;
	// will be opened as Embedded instead of raw text
	private List<String> imageFormats;

	private Properties properties;

	public MonitoringProperties() {
		super();
		init();
	}

	private void init() {
		loadProperties();
		rawTextFormats = loadFileTypes(properties.getProperty(KEY_RAW_TEXT));
		chemdoodleFormats = loadFileTypes(properties.getProperty(KEY_CHEMDOODLE));
		jmolFormats = loadFileTypes(properties.getProperty(KEY_JMOL));
		graphFormats = loadFileTypes(properties.getProperty(KEY_DYGRAPH));
		imageFormats = loadFileTypes(properties.getProperty(KEY_IMAGES));
	}

	private void loadProperties() {
		this.properties = new Properties();

		try {
			BufferedInputStream bis = new BufferedInputStream(
					MonitoringProperties.class.getResourceAsStream("monitoring.properties"));
			properties.load(bis);
			bis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<String> loadFileTypes(String values) {
		ArrayList<String> valueList = new ArrayList<String>();
		if (values != null) {
			String[] split = values.split(",");

			for (String value : split) {
				value = value.trim();
				if (value.length() > 0) {
					valueList.add(value);
				}
			}
		}
		return valueList;
	}

	public List<String> getRawTextFormats() {
		return rawTextFormats;
	}

	public List<String> getChemdoodleFormats() {
		return chemdoodleFormats;
	}

	public List<String> getJmolFormats() {
		return jmolFormats;
	}

	public List<String> getGraphFormats() {
		return graphFormats;
	}

	public List<String> getImageFormats() {
		return imageFormats;
	}

}
