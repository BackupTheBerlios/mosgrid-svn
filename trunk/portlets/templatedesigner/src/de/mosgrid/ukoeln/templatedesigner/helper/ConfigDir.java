package de.mosgrid.ukoeln.templatedesigner.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.portlet.DomainId;
import de.mosgrid.util.MosgridProperties;

public enum ConfigDir {
	
	MD(DomainId.MOLECULAR_DYNAMICS),
	DOCKING(DomainId.DOCKING),
	REMD(DomainId.REPLICA_EXCHANGE_MOLECULAR_DYNAMICS),
	QC(DomainId.QUANTUM_CHEMISTRY);

	
	
	static {
		for (ConfigDir val : ConfigDir.values()) {
			val.init();
		}	
	}

	private DomainId _id;
	private String _dir;
	private List<ConfigBean> _files = new ArrayList<ConfigBean>();
	private final Logger LOGGER = LoggerFactory.getLogger(ConfigDir.class);
	private static String _BASEPATH;
	
	private ConfigDir(DomainId id) {
		_id = id;
	}
	
	public static ConfigDir getToID(DomainId id) {
		for (ConfigDir val : ConfigDir.values()) {
			if (val._id == id)
				return val;
		}
		
		throw new UnsupportedOperationException("Could not find config to domain: " + id);
	}
	
	public static String getCanonicalConfigBasePath() {
		if (StringH.isNullOrEmpty(_BASEPATH)) {
			try {
				_BASEPATH = doGetCanonicalConfigBasePath();
			} catch (IOException e) {
				if (ConfigDir.values().length > 0)
					ConfigDir.values()[0].LOGGER.error("Could not retrieve canonical base path.", e);
				return null;
			}
		}
		return _BASEPATH;
	}

	private static String doGetCanonicalConfigBasePath() throws IOException {
		String path = MosgridProperties.GUSE_BASE_PATH.getProperty();
		if (!path.endsWith(File.separator))
			path += File.separator;
		path += MosgridProperties.REL_PARSER_CONFIG_PATH.getProperty();
		if (!path.endsWith(File.separator))
			path += File.separator;
		File file = new File(path);
		if (file == null || !file.exists())
			return null;
		return file.getCanonicalPath() + File.separator;
	}

	private void init() {
		_dir = getCanonicalConfigBasePath();
		_dir += _id.getThemeDir();
		if (!_dir.endsWith(File.separator))
			_dir += File.separator;
		refreshFiles();
	}
	
	public void refreshFiles() {
		_files.clear();
		File dir = new File(_dir);
		if (!dir.exists())
			return;
		for (String name : dir.list()) {
			if (name.endsWith(".xml")) {
				File file = new File(dir, name);
				_files.add(new ConfigBean(_id, file));
			}
		}
	}

	public List<ConfigBean> getFiles() {
		return _files;
	}
}
