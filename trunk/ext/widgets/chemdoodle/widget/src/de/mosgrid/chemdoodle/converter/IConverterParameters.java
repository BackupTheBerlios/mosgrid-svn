package de.mosgrid.chemdoodle.converter;

public interface IConverterParameters {
	
	boolean isParseAminoAcidAtoms();

	/**
	 * Decides if all atoms of amino acids shall be parsed
	 */
	void setParseAminoAcidAtoms(boolean parseAminoAcidAtoms);

	/**
	 * Decides if secondary structure should be predicted from torsion angles
	 */
	 void setPredictSecStructure(boolean predictSecStructure);

	/**
	 * Sets the pdb model to be parsed
	 */
	void setModel(int model);

	int getModel();

	boolean isPredictSecStructure();

	boolean isParseWater();

	/**
	 * Decides if water molecules should be parsed
	 */
	void setParseWater(boolean parseWater);

	boolean isParseNonPolymers();

	/**
	 * Decides if non polymers (hetatm + water) shall be parsed
	 */
	void setParseNonPolymers(boolean parseNonPolymers);
}
