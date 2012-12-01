package de.mosgrid.chemdoodle.converter.pdb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.AminoAcidImpl;
import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.HetatomImpl;
import org.biojava.bio.structure.NucleotideImpl;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.FileParsingParameters;
import org.biojava.bio.structure.io.PDBFileParser;

import de.mosgrid.chemdoodle.converter.AbstractJsonConverter;
import de.mosgrid.chemdoodle.converter.IConverterParameters;
import de.mosgrid.chemdoodle.converter.JsonAtom;
import de.mosgrid.chemdoodle.converter.JsonAtomArray;
import de.mosgrid.chemdoodle.converter.JsonBond;
import de.mosgrid.chemdoodle.converter.JsonChain;
import de.mosgrid.chemdoodle.converter.JsonConversionException;
import de.mosgrid.chemdoodle.converter.JsonFormat;
import de.mosgrid.chemdoodle.converter.JsonResidue;

/**
 * Converts given pdb input stream to json format
 * 
 * @author Andreas Zink
 * 
 */
public class PDB2JSONConverter extends AbstractJsonConverter {
	private PDBFileParser parser;
	/* parsing parameters */
	private PdbParsingParameters parserParameters;

	public PDB2JSONConverter() {
		parser = new PDBFileParser();
		init();
	}

	private void init() {
		parserParameters = new PdbParsingParameters();
		parser.setFileParsingParameters(parserParameters);
	}

	@Override
	public IConverterParameters getConverterParameters() {
		return parserParameters;
	}

	@Override
	public String convert(BufferedReader reader, BufferedWriter writer) throws JsonConversionException, IOException {
		String jsonString = null;
		// read & parse from inputstream
		Structure pdbStructure = parser.parsePDBFile(reader);
		JsonFormat jsonFormat = new JsonFormat();
		// by default the first model is used
		List<Chain> chains = pdbStructure.getModel(parserParameters.getModel());
		// iterate over all chains
		for (Chain chain : chains) {
			List<Group> aminoGroups = chain.getAtomGroups("amino");
			if (aminoGroups.size() > 0) {
				// parse amino acid ribbons
				JsonChain aaChain = handleAminoAcids(parserParameters.getModel(), chain.getChainID(), aminoGroups);
				jsonFormat.getRibbons().addChain(aaChain);

				// parse amino acid atoms
				if (parserParameters.isParseAminoAcidAtoms()) {
					JsonAtomArray aminoAcidAtomArray = handleAminoAcidAtoms(aminoGroups);
					jsonFormat.getMolecues().appendAtoms(aminoAcidAtomArray);
				}
			}
			// parse nucleotide ribbons
			List<Group> nucleotideGroups = chain.getAtomGroups("nucleotide");
			if (nucleotideGroups.size() > 0) {
				JsonChain nucChain = handleNucleotides(parserParameters.getModel(), chain.getChainID(),
						nucleotideGroups);
				jsonFormat.getRibbons().addChain(nucChain);
			}
			// parse non polymers
			if (parserParameters.isParseNonPolymers()) {
				List<Group> hetatmGroups = chain.getAtomGroups("hetatm");
				if (hetatmGroups.size() > 0) {
					JsonAtomArray hetatmArray = handleHETATMs(hetatmGroups);
					jsonFormat.getMolecues().appendAtoms(hetatmArray);
				}
			}
		}
		// after all chains are parsed look for bonds
		parseBonds(pdbStructure, jsonFormat);

		jsonString = jsonFormat.toString();
		reader.close();

		// write if outputstream given
		if (writer != null) {
			if (jsonString != null) {
				writer.write(jsonString);
			}
			writer.close();
		}
		return jsonString;
	}

	/**
	 * Parses all atoms of given amino acids
	 */
	private JsonAtomArray handleAminoAcidAtoms(List<Group> groupList) {
		JsonAtomArray aminoAcidAtomArry = new JsonAtomArray();

		for (Group group : groupList) {
			if (group instanceof AminoAcidImpl) {
				AminoAcidImpl aa = (AminoAcidImpl) group;
				for (Atom a : aa.getAtoms()) {
					JsonAtom atom = new JsonAtom(a);
					atom.setHetatm(false);
					atom.setWater(false);
					aminoAcidAtomArry.addAtom(atom);
				}
			}
		}
		return aminoAcidAtomArry;
	}

	/**
	 * Reads the CONECT section of pdb file. Will only parse bonds from HETATM to HETATM, exclusive water molecules.
	 */
	private void parseBonds(Structure pdbStructure, JsonFormat jsonFormat) {
		JsonAtomArray atomArray = jsonFormat.getMolecues().getAtoms();
		List<Map<String, Integer>> connections = pdbStructure.getConnections();
		for (Map<String, Integer> conect : connections) {
			int atomSerial = conect.get("atomserial");
			if (atomArray.contains(atomSerial)) {
				if (conect.containsKey("bond1")) {
					int bondSerial = conect.get("bond1");
					createBond(jsonFormat, atomArray, atomSerial, bondSerial);
				}
				if (conect.containsKey("bond2")) {
					int bondSerial = conect.get("bond2");
					createBond(jsonFormat, atomArray, atomSerial, bondSerial);
				}
				if (conect.containsKey("bond3")) {
					int bondSerial = conect.get("bond3");
					createBond(jsonFormat, atomArray, atomSerial, bondSerial);
				}
				if (conect.containsKey("bond4")) {
					int bondSerial = conect.get("bond4");
					createBond(jsonFormat, atomArray, atomSerial, bondSerial);
				}
			}
		}
	}

	/**
	 * Helper for creating a bond object
	 */
	private void createBond(JsonFormat jsonFormat, JsonAtomArray atomArray, int atomSerial, int bondSerial) {
		if (atomArray.contains(bondSerial)) {
			JsonAtom a = atomArray.getAtom(atomSerial);
			JsonAtom b = atomArray.getAtom(bondSerial);
			if (!a.isWater() && !b.isWater()) {
				int from = atomArray.indexOf(a);
				int to = atomArray.indexOf(b);

				JsonBond bond = new JsonBond(from, to);
				jsonFormat.getMolecues().getBonds().addBond(bond);
			}
		}
	}

	/**
	 * Helper for handling HETATM entries
	 */
	private JsonAtomArray handleHETATMs(List<Group> groupList) {
		JsonAtomArray hetatmArry = new JsonAtomArray();

		for (Group group : groupList) {
			if (group instanceof HetatomImpl) {
				HetatomImpl hetatm = (HetatomImpl) group;

				// check if this is a water molecule
				String pdbName = hetatm.getPDBName().trim();
				if (pdbName.equals("HOH") || pdbName.equals("SOL")) {
					if (parserParameters.isParseWater()) {
						JsonAtom oxygen = null;
						for (Atom a : hetatm.getAtoms()) {
							oxygen = new JsonAtom(a);
							if (oxygen.getAtomName().contains("O")) {				
								oxygen.setHetatm(true);
								oxygen.setWater(true);
								hetatmArry.addAtom(oxygen);
								break;
							}
						}
					}
				} else {
					// this is some normal non polymer
					for (Atom a : hetatm.getAtoms()) {
						// if (hetatmOffset == -1) {
						// hetatmOffset = a.getPDBserial();
						// }
						JsonAtom atom = new JsonAtom(a);
						atom.setHetatm(true);
						atom.setWater(false);
						hetatmArry.addAtom(atom);
					}
				}
			}
		}
		return hetatmArry;
	}

	/**
	 * Helper for handling nucleotide entries
	 */
	private JsonChain handleNucleotides(int model, String chain, List<Group> nucGroups) throws JsonConversionException {
		JsonChain nucChain = new JsonChain(model, chain);
		for (int i = 0; i < nucGroups.size(); i++) {
			Group nucGroup = nucGroups.get(i);
			if (nucGroup instanceof NucleotideImpl) {
				NucleotideImpl nucleotide = (NucleotideImpl) nucGroup;
				if (i == 0) {
					handleFirstNucleotide(nucChain, nucleotide);
				}
				handleNucleotide(nucChain, nucleotide);
				if (i == nucGroups.size() - 1) {
					handleLastNucleotide(nucChain, nucleotide);
				}
			}
		}
		return nucChain;
	}

	private void handleNucleotide(JsonChain nucChain, NucleotideImpl nucleotide) throws JsonConversionException {
		JsonResidue residue = new JsonResidue(nucleotide);

		if (residue.getName().equals("C") || residue.getName().equals("T")) {
			JsonAtom atomO5prime = new JsonAtom(AtomHelper.findO5prime(nucleotide));
			residue.getAtoms().add(atomO5prime);
			JsonAtom atomC6 = new JsonAtom(AtomHelper.findC6(nucleotide));
			residue.getAtoms().add(atomC6);
			JsonAtom atomN3 = new JsonAtom(AtomHelper.findN3(nucleotide));
			residue.getAtoms().add(atomN3);
			JsonAtom atomC2 = new JsonAtom(AtomHelper.findC2(nucleotide));
			residue.getAtoms().add(atomC2);
			JsonAtom atomC4 = new JsonAtom(AtomHelper.findC4(nucleotide));
			residue.getAtoms().add(atomC4);
		} else {
			JsonAtom atomP = new JsonAtom(AtomHelper.findP(nucleotide));
			residue.getAtoms().add(atomP);
			JsonAtom atomN9 = new JsonAtom(AtomHelper.findN9(nucleotide));
			residue.getAtoms().add(atomN9);
			JsonAtom atomN1 = new JsonAtom(AtomHelper.findN1(nucleotide));
			residue.getAtoms().add(atomN1);
			JsonAtom atomC2 = new JsonAtom(AtomHelper.findC2(nucleotide));
			residue.getAtoms().add(atomC2);
			JsonAtom atomC6 = new JsonAtom(AtomHelper.findC6(nucleotide));
			residue.getAtoms().add(atomC6);
		}

		nucChain.addResidue(residue);

	}

	private void handleLastNucleotide(JsonChain nucChain, Group nucleotide) throws JsonConversionException {
		Atom atom = AtomHelper.findN9(nucleotide);
		JsonResidue tailResidue1 = new JsonResidue(nucleotide);
		tailResidue1.getAtoms().add(new JsonAtom(atom));
		tailResidue1.getAtoms().add(new JsonAtom(atom));
		nucChain.addResidue(tailResidue1);
		JsonResidue tailResidue2 = new JsonResidue(nucleotide);
		tailResidue2.getAtoms().add(new JsonAtom(atom));
		tailResidue2.getAtoms().add(new JsonAtom(atom));
		nucChain.addResidue(tailResidue2);
		JsonResidue tailResidue3 = new JsonResidue(nucleotide);
		tailResidue3.getAtoms().add(new JsonAtom(atom));
		tailResidue3.getAtoms().add(new JsonAtom(atom));
		nucChain.addResidue(tailResidue3);
		JsonResidue tailResidue4 = new JsonResidue(nucleotide);
		tailResidue4.getAtoms().add(new JsonAtom(atom));
		tailResidue4.getAtoms().add(new JsonAtom(atom));
		nucChain.addResidue(tailResidue4);

	}

	private void handleFirstNucleotide(JsonChain nucChain, Group nucleotide) throws JsonConversionException {
		Atom firstAtom = AtomHelper.findO5prime(nucleotide);
		JsonResidue residue1 = new JsonResidue(nucleotide);
		residue1.getAtoms().add(new JsonAtom(firstAtom));
		residue1.getAtoms().add(new JsonAtom(firstAtom));
		nucChain.addResidue(residue1);
		JsonResidue residue2 = new JsonResidue(nucleotide);
		residue2.getAtoms().add(new JsonAtom(firstAtom));
		residue2.getAtoms().add(new JsonAtom(firstAtom));
		nucChain.addResidue(residue2);

	}

	/**
	 * Helper for handling amino acid entries
	 */
	private JsonChain handleAminoAcids(int model, String chain, List<Group> aminoGroups) throws JsonConversionException {
		JsonChain aminoChain = new JsonChain(model, chain);
		for (int i = 0; i < aminoGroups.size(); i++) {
			Group aminoGroup = aminoGroups.get(i);
			if (aminoGroup instanceof AminoAcid) {
				AminoAcid amino = (AminoAcid) aminoGroup;
				if (i == 0) {
					handleFirstAminoAcid(aminoChain, amino);
				}
				JsonResidue residue = new JsonResidue(amino);
				JsonAtom atomCA = new JsonAtom(AtomHelper.findCA(amino));
				residue.getAtoms().add(atomCA);
				JsonAtom atomO = new JsonAtom(AtomHelper.findO(amino));
				residue.getAtoms().add(atomO);
				aminoChain.addResidue(residue);
				if (parserParameters.isPredictSecStructure()) {
					// TODO:
					// try {
					// Calc.getPhi(null, null);
					// } catch (StructureException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
				}

				if (i == aminoGroups.size() - 1) {
					handleLastAminoAcid(aminoChain, amino);
				}
			}
		}
		aminoChain.setArrowFields();
		return aminoChain;
	}

	private void handleFirstAminoAcid(JsonChain aminoChain, Group amino) throws JsonConversionException {
		Atom firstNAtom = AtomHelper.findN(amino);
		JsonResidue residue1 = new JsonResidue(amino);
		residue1.getAtoms().add(new JsonAtom(firstNAtom));
		residue1.getAtoms().add(new JsonAtom(firstNAtom));
		aminoChain.addResidue(residue1);
		JsonResidue residue2 = new JsonResidue(amino);
		residue2.getAtoms().add(new JsonAtom(firstNAtom));
		residue2.getAtoms().add(new JsonAtom(firstNAtom));
		aminoChain.addResidue(residue2);
	}

	private void handleLastAminoAcid(JsonChain aminoChain, Group amino) throws JsonConversionException {
		Atom lastNAtom = AtomHelper.findN(amino);
		JsonResidue tailResidue1 = new JsonResidue(amino);
		tailResidue1.getAtoms().add(new JsonAtom(lastNAtom));
		tailResidue1.getAtoms().add(new JsonAtom(lastNAtom));
		aminoChain.addResidue(tailResidue1);
		JsonResidue tailResidue2 = new JsonResidue(amino);
		tailResidue2.getAtoms().add(new JsonAtom(lastNAtom));
		tailResidue2.getAtoms().add(new JsonAtom(lastNAtom));
		aminoChain.addResidue(tailResidue2);
		JsonResidue tailResidue3 = new JsonResidue(amino);
		tailResidue3.getAtoms().add(new JsonAtom(lastNAtom));
		tailResidue3.getAtoms().add(new JsonAtom(lastNAtom));
		aminoChain.addResidue(tailResidue3);
		JsonResidue tailResidue4 = new JsonResidue(amino);
		tailResidue4.getAtoms().add(new JsonAtom(lastNAtom));
		tailResidue4.getAtoms().add(new JsonAtom(lastNAtom));
		aminoChain.addResidue(tailResidue4);
	}

	public class PdbParsingParameters extends FileParsingParameters implements IConverterParameters {
		private static final long serialVersionUID = 7745269635052323530L;
		// the pdb model to be parsed
		private int model = 0;
		// try to predict secondary structure if not given in pdb file
		private boolean predictSecStructure = false;
		// parse water molecules
		private boolean parseWater = true;
		// parse non polymers (hetatm + water)
		private boolean parseNonPolymers = true;
		// parse all atoms of amino acids
		private boolean parseAminoAcidAtoms = true;

		private PdbParsingParameters() {
			super();
			setParseSecStruc(true);
		}

		public boolean isParseAminoAcidAtoms() {
			return parseAminoAcidAtoms;
		}

		/**
		 * Decides if all atoms of amino acids shall be parsed
		 */
		public void setParseAminoAcidAtoms(boolean parseAminoAcidAtoms) {
			this.parseAminoAcidAtoms = parseAminoAcidAtoms;
		}

		/**
		 * Decides if secondary structure should be predicted from torsion angles
		 */
		public void setPredictSecStructure(boolean predictSecStructure) {
			this.predictSecStructure = predictSecStructure;
		}

		/**
		 * Sets the pdb model to be parsed
		 */
		public void setModel(int model) {
			this.model = model;
		}

		public int getModel() {
			return model;
		}

		public boolean isPredictSecStructure() {
			return predictSecStructure;
		}

		public boolean isParseWater() {
			return parseWater;
		}

		/**
		 * Decides if water molecules should be parsed
		 */
		public void setParseWater(boolean parseWater) {
			this.parseWater = parseWater;
		}

		public boolean isParseNonPolymers() {
			return parseNonPolymers;
		}

		/**
		 * Decides if non polymers (hetatm + water) shall be parsed
		 */
		public void setParseNonPolymers(boolean parseNonPolymers) {
			this.parseNonPolymers = parseNonPolymers;
		}

	}

}
