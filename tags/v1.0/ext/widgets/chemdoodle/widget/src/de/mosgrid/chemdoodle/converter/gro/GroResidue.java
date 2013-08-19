package de.mosgrid.chemdoodle.converter.gro;

import java.util.HashMap;
import java.util.Map;

public class GroResidue {
	public enum ResidueType {
		AA, ION, SOL
	};

	private static final String[] AA_NAMES = new String[] { "ALA", "CYS", "ASP", "GLU", "PHE", "GLY", "HIS", "ILE",
			"LYS", "LEU", "MET", "ASN", "PRO", "GLN", "ARG", "SER", "THR", "VAL", "TRP", "TYR" };
	private static final String SOLVENT_NAME = "SOL";

	private ResidueType type;
	private Map<Integer, GroAtom> atoms = new HashMap<Integer, GroAtom>();
	private int residueNumber;
	private String residueName;

	public GroResidue(String line) {
		setResidueName(line);
		setResidueNumber(line);
	}

	private void setResidueNumber(String line) {
		try {
			String residueNumberString = line.substring(0, 5).trim();
			residueNumber = new Integer(residueNumberString);
		} catch (Exception e) {

		}
	}

	private void setResidueName(String line) {
		residueName = line.substring(5, 10).trim();
		parseResidueType();
	}

	public ResidueType getType() {
		return type;
	}

	public Map<Integer, GroAtom> getAtoms() {
		return atoms;
	}

	public int getResidueNumber() {
		return residueNumber;
	}

	public String getResidueName() {
		return residueName;
	}

	private void parseResidueType() {
		if (residueName.equalsIgnoreCase(SOLVENT_NAME)) {
			type = ResidueType.SOL;
		} else {
			for (String aa : AA_NAMES) {
				if (residueName.equalsIgnoreCase(aa)) {
					type = ResidueType.AA;
				}
			}
		}
		if (type == null) {
			type = ResidueType.ION;
		}
	}
}
