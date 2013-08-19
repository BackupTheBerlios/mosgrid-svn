package de.mosgrid.msml.enums;

import java.util.Hashtable;

import de.mosgrid.msml.util.MSMLProperties;

public enum DictDir {

	MD(MSMLProperties.REL_MD_PATH),
	QC(MSMLProperties.REL_QC_PATH),
	DOCKING(MSMLProperties.REL_DOCKING_PATH),
	REMD(MSMLProperties.REL_REMD_PATH),

	PARSER(MSMLProperties.REL_PARSER_PATH),
	ADAPTER(MSMLProperties.REL_ADAPTER_PATH),
	ENVIRONMENT(MSMLProperties.REL_ENV_PATH);
	
	private static String dictPath = MSMLProperties.GUSE_BASE_PATH.getProperty() + MSMLProperties.REL_DICTIONARY_PATH.getProperty();
	private static Hashtable<MSMLProperties, DictDir> vals = new Hashtable<MSMLProperties, DictDir>();
	private String _path;
	private MSMLProperties _prop;

	
	static {
		for (DictDir dir : DictDir.values()) {
			dir._path = dictPath + dir._prop.getProperty();
			if (!dir._path.endsWith("/"))
				dir._path += "/";
			vals.put(dir._prop, dir);
		}
	}
	
	DictDir(MSMLProperties prop) {
		_prop = prop;
	}
	
	public String getPath() {
		return _path;
	}
	
	public static DictDir getByMSMLProperty(MSMLProperties prop) {
		return vals.get(prop);
	}
}
