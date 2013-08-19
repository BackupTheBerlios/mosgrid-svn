package de.mosgrid.remd.uploadpostprocessing;

/**
 * Helper class which defines the different types of pdb group types
 * 
 * @author Andreas Zink
 * 
 */
public enum PDBGroup {
	AA, NUC, HETATM, WATER;

	@Override
	public String toString() {
		switch (this) {
		case AA:
			return "Amino Acids";
		case NUC:
			return "Nucleotides";
		case HETATM:
			return "HETATM";
		case WATER:
			return "Water";
		default:
			return super.toString();
		}
	}
}
