package de.mosgrid.msml.converter.nonstandart;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import de.mosgrid.msml.jaxb.bindings.MoleculeType;

public class CMLLongReader {

	public MoleculeType parse(File file) throws IOException {
		return CMLCMLConverter.cmlLong2CmlShort(file);
	}

	public MoleculeType toCML(String fileName, BufferedReader buf) throws IOException {
		return CMLCMLConverter.cmlLong2CmlShort(buf);
	}

}
