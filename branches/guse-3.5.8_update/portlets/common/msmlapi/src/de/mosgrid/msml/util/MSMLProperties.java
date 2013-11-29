package de.mosgrid.msml.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public enum MSMLProperties {

	GUSE_BASE_PATH("guseBasePath"),
	REL_TEMPLATE_PATH("msmlTemplatesPath"),
	REL_DICTIONARY_PATH("msmlDictionariesPath"),
	// Domain specific keys
	REL_MD_PATH("mdPath"),
	REL_QC_PATH("qcPath"),
	REL_DOCKING_PATH("dockingPath"),
	REL_REMD_PATH("remdPath"),
	// Common dictionary keys
	REL_PARSER_PATH("parserPath"),
	REL_ADAPTER_PATH("adapterPath"),
	REL_ENV_PATH("environmentPath"),
	// xtreem folders that are "allowed" to be declared in input ports
	WHITE_LIST_FOLDERS("whitelistFolders");

	private static Properties properties;

	static {
		properties = new Properties();
		String catalinaBasePath = System.getProperty("catalina.base");
		if (catalinaBasePath == null || catalinaBasePath.equals("")) {
			catalinaBasePath = System.getProperty("catalina.home");
		}

		try {
			InputStream s = new FileInputStream(new File(catalinaBasePath + "/conf/mosgrid.properties"));
			properties.load(new BufferedInputStream(s));
			s.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String _key;

	MSMLProperties(String key) {
		_key = key;
	}

	public String getProperty() {
		return properties.getProperty(_key);
	}
}
