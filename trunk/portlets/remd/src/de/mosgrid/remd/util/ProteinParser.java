package de.mosgrid.remd.util;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A parser which counts the number of protein atoms, water molecules and ion atoms of a pdb file.
 * 
 * @author Andreas Zink
 * 
 */
public class ProteinParser {
	private final Logger LOGGER = LoggerFactory.getLogger(ProteinParser.class);
	private static final String ATOM = "ATOM";
	private static final String SOLVENT = "SOL";
	private static final String WATER = "HOH";
	private static final String ION_NA = "NA";
	private static final String ION_CA = "CA";

	private String lastAALine = null;
	private int lastAASequenceNumber;
	private int lastSolventSequenceNumber;
	private int lastIonSequenceNumber;

	public Result parse(BufferedReader reader) {
		Result result = new Result();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(ATOM)) {
					parseAtomLine(line, result);
				}
			}
			// check
			if (!result.isProteinAtomsSet()) {
				// there seems to be no solvent nor ions
				result.setWaterMolecules(0);
				if (lastAALine != null) {
					result.setProteinAtoms(getAtomSerial(lastAALine));
				} else {
					// there seems to be no ATOM line at all
					result.setProteinAtoms(0);
				}
			}
			result.setIonAtoms(lastIonSequenceNumber - lastSolventSequenceNumber);
		} catch (IOException e) {
			LOGGER.error("Error while reading pdb file!", e);
		}
		return result;
	}

	/**
	 * Parse one atom line after each other. The first part of atom lines belongs to Amino Acids (AA), the secon are
	 * solvent molecues and the last are ions.
	 */
	private void parseAtomLine(String line, Result result) {
		String residueName = getResidueName(line);
		if (residueName.equals(SOLVENT) || residueName.equals(WATER)) {
			// start of solvent section
			if (!result.isProteinAtomsSet()) {
				result.setProteinAtoms(getAtomSerial(lastAALine));
				lastAASequenceNumber = getSequenceNumber(lastAALine);
			}
			lastSolventSequenceNumber = getSequenceNumber(line);
		} else if (residueName.equals(ION_CA) || residueName.equals(ION_NA)) {
			// check
			if (!result.isProteinAtomsSet()) {
				// there seems to be no solvent
				result.setProteinAtoms(getAtomSerial(lastAALine));
				lastAASequenceNumber = getSequenceNumber(lastAALine);
				lastSolventSequenceNumber = lastAASequenceNumber;
			}
			// start of ion section
			if (!result.isWaterMoleculesSet()) {
				result.setWaterMolecules(lastSolventSequenceNumber - lastAASequenceNumber);
			}
			lastIonSequenceNumber = getSequenceNumber(line);
		} else {
			// still in aa section
			lastAALine = line;
		}
	}

	private String getResidueName(String line) {
		return line.substring(17, 20).trim();
	}

	private int getAtomSerial(String line) {
		return Integer.valueOf(line.substring(6, 11).trim());
	}

	private int getSequenceNumber(String line) {
		return Integer.valueOf(line.substring(22, 26).trim());
	}

	public class Result {
		private int proteinAtoms = -1;
		private int waterMolecules = -1;
		private int ionAtoms = -1;

		public int getProteinAtoms() {
			return proteinAtoms;
		}

		private void setProteinAtoms(int proteinAtoms) {
			this.proteinAtoms = proteinAtoms;
		}

		public boolean isProteinAtomsSet() {
			return proteinAtoms >= 0;
		}

		public int getWaterMolecules() {
			return waterMolecules;
		}

		private void setWaterMolecules(int waterMolecules) {
			this.waterMolecules = waterMolecules;
		}

		public boolean isWaterMoleculesSet() {
			return waterMolecules >= 0;
		}

		public int getIonAtoms() {
			return ionAtoms;
		}

		private void setIonAtoms(int ionAtoms) {
			this.ionAtoms = ionAtoms;
		}

		public boolean isIonAtomsSet() {
			return ionAtoms >= 0;
		}

	}
}
