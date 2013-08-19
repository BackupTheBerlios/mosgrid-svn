package de.mosgrid.chemdoodle.converter.gro;

import java.io.BufferedReader;
import java.io.IOException;

public class GroParser {

	private GroFormat format = new GroFormat();
	private int expectedAtomSerial = 1;
	private GroResidue currentResidue;

	public void parseFromStreamReader(BufferedReader reader) {
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				parseLine(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void parseLine(String line) {
		GroAtom atom = new GroAtom(line);
		if (atom.getAtomNumber() == expectedAtomSerial) {
			// only increas if upper condition was true!
			expectedAtomSerial++;
			if (currentResidue == null || atom.getResidueNumber() != currentResidue.getResidueNumber()) {
				currentResidue = new GroResidue(line);
				format.addResidue(currentResidue);
			}
			currentResidue.getAtoms().put(atom.getAtomNumber(), atom);
		}
	}
}
