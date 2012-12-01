package de.mosgrid.chemdoodle.converter.pdb;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.StructureException;

import de.mosgrid.chemdoodle.converter.JsonConversionException;

/**
 * Helper class for getting atoms from residues. Atoms are often not complete or missing in pdb format. These helper
 * methods search for near atoms if the desired could not be found.
 * 
 * @author Andreas Zink
 * 
 */
public class AtomHelper {
	public static final String AA_O = "O";
	public static final String AA_C = "C";
	public static final String AA_CA = "CA";
	public static final String AA_CB = "CB";
	public static final String AA_N = "N";
	public static final String NUC_P = "P";
	public static final String NUC_O5prime = "O5'";
	public static final String NUC_O4prime = "O4'";
	public static final String NUC_N9 = "N9";
	public static final String NUC_N8 = "N8";
	public static final String NUC_N7 = "N7";
	public static final String NUC_N6 = "N6";
	public static final String NUC_N5 = "N5";
	public static final String NUC_N4 = "N4";
	public static final String NUC_N3 = "N3";
	public static final String NUC_N2 = "N2";
	public static final String NUC_N1 = "N1";
	public static final String NUC_C1 = "C1";
	public static final String NUC_C2 = "C2";
	public static final String NUC_C3 = "C3";
	public static final String NUC_C4 = "C4";
	public static final String NUC_C5 = "C5";
	public static final String NUC_C6 = "C6";

	public static Atom findO(Group amino) throws JsonConversionException {
		Atom atom = null;
		try {
			if (amino.hasAtom(AA_O)) {
				atom = amino.getAtom(AA_O);
			} else if (amino.hasAtom(AA_C)) {
				atom = amino.getAtom(AA_C);
			} else if (amino.hasAtom(AA_CA)) {
				atom = amino.getAtom(AA_CA);
			} else if (amino.hasAtom(AA_N)) {
				atom = amino.getAtom(AA_N);
			} else {
				throw new StructureException("Amino acid " + amino.getResidueNumber() + " in chain "
						+ amino.getChainId() + " has no O atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findCA(Group amino) throws JsonConversionException {
		Atom atom = null;
		try {
			if (amino.hasAtom(AA_CA)) {
				atom = amino.getAtom(AA_CA);
			} else if (amino.hasAtom(AA_CB)) {
				atom = amino.getAtom(AA_CB);
			} else if (amino.hasAtom(AA_N)) {
				atom = amino.getAtom(AA_N);
			} else if (amino.hasAtom(AA_O)) {
				atom = amino.getAtom(AA_O);
			} else if (amino.hasAtom(AA_C)) {
				atom = amino.getAtom(AA_C);
			} else {
				throw new StructureException("Amino acid " + amino.getResidueNumber() + " in chain "
						+ amino.getChainId() + " has no CA atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findN(Group amino) throws JsonConversionException {
		Atom atom = null;
		try {
			if (amino.hasAtom(AA_N)) {
				atom = amino.getAtom(AA_N);
			} else if (amino.hasAtom(AA_CA)) {
				atom = amino.getAtom(AA_CA);
			} else if (amino.hasAtom(AA_O)) {
				atom = amino.getAtom(AA_O);
			} else {
				throw new StructureException("Amino acid " + amino.getResidueNumber() + " in chain "
						+ amino.getChainId() + " has no N atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findN9(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_N9)) {
				atom = nucleotide.getAtom(NUC_N9);
			} else if (nucleotide.hasAtom(NUC_N8)) {
				atom = nucleotide.getAtom(NUC_N8);
			} else if (nucleotide.hasAtom(NUC_N7)) {
				atom = nucleotide.getAtom(NUC_N7);
			} else if (nucleotide.hasAtom(NUC_N6)) {
				atom = nucleotide.getAtom(NUC_N6);
			} else if (nucleotide.hasAtom(NUC_N5)) {
				atom = nucleotide.getAtom(NUC_N5);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no N9 atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findO5prime(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_O5prime)) {
				atom = nucleotide.getAtom(NUC_O5prime);
			} else if (nucleotide.hasAtom(NUC_O4prime)) {
				atom = nucleotide.getAtom(NUC_O4prime);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no O5' atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findP(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_P)) {
				atom = nucleotide.getAtom(NUC_P);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no P atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findC6(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_C6)) {
				atom = nucleotide.getAtom(NUC_C6);
			} else if (nucleotide.hasAtom(NUC_C5)) {
				atom = nucleotide.getAtom(NUC_C5);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no C6 atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findN3(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_N3)) {
				atom = nucleotide.getAtom(NUC_N3);
			} else if (nucleotide.hasAtom(NUC_N2)) {
				atom = nucleotide.getAtom(NUC_N2);
			} else if (nucleotide.hasAtom(NUC_N4)) {
				atom = nucleotide.getAtom(NUC_N4);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no N3 atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findN1(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_N1)) {
				atom = nucleotide.getAtom(NUC_N1);
			} else if (nucleotide.hasAtom(NUC_N2)) {
				atom = nucleotide.getAtom(NUC_N2);
			} else if (nucleotide.hasAtom(NUC_N3)) {
				atom = nucleotide.getAtom(NUC_N3);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no N1 atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findC2(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_C2)) {
				atom = nucleotide.getAtom(NUC_C2);
			} else if (nucleotide.hasAtom(NUC_C1)) {
				atom = nucleotide.getAtom(NUC_C1);
			} else if (nucleotide.hasAtom(NUC_C3)) {
				atom = nucleotide.getAtom(NUC_C3);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no C2 atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}

	public static Atom findC4(Group nucleotide) throws JsonConversionException {
		Atom atom = null;
		try {
			if (nucleotide.hasAtom(NUC_C4)) {
				atom = nucleotide.getAtom(NUC_C4);
			} else if (nucleotide.hasAtom(NUC_C3)) {
				atom = nucleotide.getAtom(NUC_C3);
			} else if (nucleotide.hasAtom(NUC_C5)) {
				atom = nucleotide.getAtom(NUC_C5);
			} else {
				throw new StructureException("Nucleotide " + nucleotide.getResidueNumber() + " in chain "
						+ nucleotide.getChainId() + " has no C4 atom.");
			}
		} catch (StructureException e) {
			throw new JsonConversionException(e);
		}
		return atom;
	}
}
