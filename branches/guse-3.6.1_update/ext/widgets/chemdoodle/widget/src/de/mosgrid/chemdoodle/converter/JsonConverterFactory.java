package de.mosgrid.chemdoodle.converter;

import de.mosgrid.chemdoodle.converter.pdb.PDB2JSONConverter;

public class JsonConverterFactory {
	private static JsonConverterFactory instance = null;

	public static JsonConverterFactory getInstance() {
		if (instance == null) {
			instance = new JsonConverterFactory();
		}
		return instance;
	}

	private JsonConverterFactory() {
		super();

	}

	public IJsonConverter create(String filename) {
		if (filename.endsWith(".pdb")) {
			return new PDB2JSONConverter();
		}
		return null;
	}

}
