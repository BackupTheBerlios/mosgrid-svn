package de.mosgrid.msml.converter.nonstandart;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import de.mosgrid.msml.jaxb.bindings.MoleculeType;

public class GaussReader {

	public MoleculeType parse(File file) throws IOException {
		throw new UnsupportedOperationException("Do not use that.");
	}

	public MoleculeType toCML(String fileName, BufferedReader buf)
			throws IOException {
		String total = "";
		String line;
		do {
			line = buf.readLine();
			total += line + "\n";
		} while (line != null);
		MoleculeType res = GaussianCMLConverter.gaussian2CML(total);
		if (res != null && res.getMolecule() != null && res.getMolecule().size() > 0)
			return res.getMolecule().get(0);
		return null;
	}
	
}