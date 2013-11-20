package de.mosgrid.ukoeln.templatedesigner.helper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.portlet.DomainId;


public class ConfigBean {

	private DomainId _id;
	private File _file;
	private String _canonicalPath;
	private Logger LOGGER = LoggerFactory.getLogger(ConfigBean.class);
	private String _fileName;
	
	public ConfigBean(DomainId id, File file) {
		super();
		_id = id;
		_file = file;
		_fileName = file.getName();
		try {
			_canonicalPath = file.getCanonicalPath();			
		} catch (IOException e) {
			LOGGER.error("Could not get canonical path for " + file.getAbsolutePath(), e);
		}
	}

	public boolean matches(String pat) {
		String basePath = ConfigDir.getCanonicalConfigBasePath();
		String shortFileName = _canonicalPath.replaceFirst(Pattern.quote(basePath), "");
		return shortFileName.matches(pat);
	}

	public String getPattern() {
		return Pattern.quote(_id.getThemeDir() + _file.getName()); 
	}

	@Override
	public String toString() {
		return _fileName;
	}
}
