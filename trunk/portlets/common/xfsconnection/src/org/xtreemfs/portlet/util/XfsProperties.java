package org.xtreemfs.portlet.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class XfsProperties {

	public static final String MRC_PORT = "xfsPort";
	public static final String MRC_URL = "xfsUrl";
	public static final String VOLUME_NAME = "xfsVolumeName";
	public static final String XFS_PASS = "xfsPass";
	
	public static final String GUSE_BASE_PATH = "guseBasePath";
	public static final String REL_USER_PATH = "guseUserPath";
	
	public static final String XFS_UPLOADS_DIR = "xfsUploadDir";
	public static final String XFS_RESULTS_DIR = "xfsResultDir";

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

	private XfsProperties() {
		super();
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}

}
