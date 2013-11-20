package de.ukoeln.msml.genericparser.worker;

import java.util.HashMap;

import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfigEntry;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;

public class ConfigHelper {

	private ConfigHelper() {

	}

	public static GenericParserExtensionConfigEntry getExtensionConfigEntry(GenericParserConfig config,
			Class<?> extension, String name) {
		GenericParserExtensionConfig c = getExtensionConfig(config, extension);
		return getExtensionConfigEntry(c, name);
	}

	public static GenericParserExtensionConfigEntry getExtensionConfigEntry(GenericParserExtensionConfig config,
			String name) {
		for (GenericParserExtensionConfigEntry c : config.getExtensionConfigs()) {
			if (c.getKey().equals(name))
				return c;
		}
		return null;

	}

	public static GenericParserExtensionConfig getExtensionConfig(GenericParserConfig config, Class<?> extension) {
		for (GenericParserExtensionConfig c : config.getExtensionConfigCollection()) {
			if (c.getExtensionName().equals(extension.getCanonicalName()))
				return c;
		}
		return null;
	}

	public static HashMap<String, String> getExtensionConfigAsHashMap(GenericParserConfig config,
			IMSMLParserExtension ext) {
		GenericParserExtensionConfig conf = getExtensionConfig(config, ext.getClass());
		HashMap<String, String> map = new HashMap<String, String>();
		if (conf == null)
			return map;
		for (GenericParserExtensionConfigEntry entry : conf.getExtensionConfigs()) {
			map.put(entry.getKey(), entry.getVal());
		}
		return map;
	}

}
