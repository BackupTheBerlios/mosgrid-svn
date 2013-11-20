package de.mosgrid.msml.enums;

import java.util.Hashtable;

import de.mosgrid.msml.util.MSMLProperties;

public enum TemplateDir {

	MD(MSMLProperties.REL_MD_PATH),
	QC(MSMLProperties.REL_QC_PATH),
	DOCKING(MSMLProperties.REL_DOCKING_PATH),
	REMD(MSMLProperties.REL_REMD_PATH);
	
	private static String templatePath = MSMLProperties.GUSE_BASE_PATH.getProperty() + MSMLProperties.REL_TEMPLATE_PATH.getProperty();
	private static Hashtable<MSMLProperties, TemplateDir> vals = new Hashtable<MSMLProperties, TemplateDir>();
	private String _path;
	private MSMLProperties _prop;

	static {
		for (TemplateDir dir : TemplateDir.values()) {
			dir._path = templatePath + dir._prop.getProperty();
			if (!dir._path.endsWith("/"))
				dir._path += "/";
			vals.put(dir._prop, dir);
		}
	}
	
	TemplateDir(MSMLProperties prop) {
		_prop = prop;
	}
	
	public String getPath() {
		return _path;
	}

	public static TemplateDir getByMSMLProperty(MSMLProperties prop) {
		return vals.get(prop);
	}
}
