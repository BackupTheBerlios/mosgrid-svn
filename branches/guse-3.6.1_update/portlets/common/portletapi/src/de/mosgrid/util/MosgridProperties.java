package de.mosgrid.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public enum MosgridProperties {

	// Certificate keys
	SERVICE_CERT_FILE("serviceCertFile"),
	SERVICE_CERT_FILE_OVERRIDE("serviceCertFileOverride"),
	SERVICE_CERT_PASS("serviceCertPass"),
	// gUSE keys
	GUSE_BASE_PATH("guseBasePath"),
	REL_GUSE_USERS_PATH("guseUserPath"),
	// Domain specific keys
	REL_MD_PATH("mdPath"),
	REL_QC_PATH("qcPath"),
	REL_DOCKING_PATH("dockingPath"),
	REL_REMD_PATH("remdPath"),
	// Common dictionary keys
	REL_PARSER_PATH("parserPath"),
	REL_ADAPTER_PATH("adapterPath"),
	REL_ENV_PATH("environmentPath"),
	//MSML keys
	REL_TEMPLATE_PATH("msmlTemplatesPath"),
	REL_DICTIONARY_PATH("msmlDictionariesPath"),
	REL_PARSER_CONFIG_PATH("parserConfigsPath"),
	
	//Help URL keys
	MD_HELP_URL("mdHelpUrl"),
	DOCKING_HELP_URL("dockingHelpUrl"),
	QC_HELP_URL("qcHelpUrl"),
	REMD_HELP_URL("remdHelpUrl"),
	HELP_BASE_URL("helpBaseUrl"),
	ADVANCED_HELP_BASE_URL("advancedHelpBaseUrl"),
	//Theme
	CUSTOM_THEME_NAME("customThemeName"),
	REL_CUSTOM_THEME_WKF_GRAPHS_PATH("workflowGraphPath"),
	REL_CUSTOM_THEME_ICONS_PATH("iconsPath");

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

	private MosgridProperties(String key) {
		_key = key;
	}

	public String getProperty() {
		return properties.getProperty(_key);
	}

}
