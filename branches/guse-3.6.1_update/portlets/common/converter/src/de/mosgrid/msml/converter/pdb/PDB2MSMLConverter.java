package de.mosgrid.msml.converter.pdb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.biojava.bio.structure.AminoAcid;
import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.Chain;
import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.HetatomImpl;
import org.biojava.bio.structure.NucleotideImpl;
import org.biojava.bio.structure.PDBHeader;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.mosgrid.msml.converter.IToMSMLConverter;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.AtomArrayType;
import de.mosgrid.msml.jaxb.bindings.AtomType;
import de.mosgrid.msml.jaxb.bindings.MoleculeClassType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.OriginType;

/**
 * This is a BioJava based PDB -> MSML converter
 * 
 * @author Andreas Zink
 * 
 */
public class PDB2MSMLConverter implements IToMSMLConverter {
	private final Logger LOGGER = LoggerFactory.getLogger(PDB2MSMLConverter.class);

	@Override
	public MoleculeType convertFromString(String moleculeString) throws MSMLConversionException {
		ByteArrayInputStream bais = new ByteArrayInputStream(moleculeString.getBytes());
		return convertFromStream(bais);
	}

	@Override
	public MoleculeType convertFromStream(InputStream is) throws MSMLConversionException {
		try {
			PDBFileParser pdbParser = new PDBFileParser();
			Structure pdbStructure = pdbParser.parsePDBFile(is);
			return convert(pdbStructure);
		} catch (IOException e) {
			String msg = "Conversion of pdb inputstream failed!";
			LOGGER.error(msg, e);
			throw new MSMLConversionException(msg, e);
		}
	}

	@Override
	public MoleculeType convertFromStructure(Object moleculeStructure) throws MSMLConversionException {
		if (moleculeStructure instanceof Structure) {
			return convert((Structure) moleculeStructure);
		} else {
			throw new MSMLConversionException("Given object does not represent a PDB structure!");
		}
	}

	/**
	 * Converts a Molecule Element from a BioJava Structure object
	 */
	public MoleculeType convert(Structure pdbStructure) throws MSMLConversionException {
		try {
			MoleculeType pdbMoleculeElement = new MoleculeType();
			setMetaData(pdbMoleculeElement, pdbStructure);
			for (int i = 0; i < pdbStructure.nrModels(); i++) {
				List<Chain> model = pdbStructure.getModel(i);
				MoleculeType modelElement = convertModel(model, i + 1, pdbMoleculeElement.getId());
				pdbMoleculeElement.getMolecule().add(modelElement);
			}
			return pdbMoleculeElement;
		} catch (Exception e) {
			String msg = "Conversion of pdb structure failed! " + e.getMessage();
			LOGGER.error(msg, e);
			throw new MSMLConversionException(msg, e);
		}
	}

	public MoleculeType convert(List<Chain> chainList) throws MSMLConversionException {
		try {
			MoleculeType pdbMoleculeElement = new MoleculeType();
			setMetaData(pdbMoleculeElement, null);
			MoleculeType modelElement = convertModel(chainList, 1, pdbMoleculeElement.getId());
			pdbMoleculeElement.getMolecule().add(modelElement);

			return pdbMoleculeElement;
		} catch (Exception e) {
			String msg = "Conversion of pdb structure failed! " + e.getMessage();
			LOGGER.error(msg, e);
			throw new MSMLConversionException(msg, e);
		}
	}

	/**
	 * Sets the format origin and tries to find a good title
	 */
	protected void setMetaData(MoleculeType pdbMoleculeElement, Structure pdbStructure) {
		// set convention
		pdbMoleculeElement.setConvention("convention:molecular");
		// set the origin format
		pdbMoleculeElement.setOrigin(OriginType.PDB);

		if (pdbStructure != null) {
			PDBHeader header = pdbStructure.getPDBHeader();
			if (header != null) {
				// try to set title and id from header
				if (header.getIdCode() != null && header.getIdCode().trim().length() > 0) {
					pdbMoleculeElement.setTitle(header.getIdCode().trim());
					pdbMoleculeElement.setId("PDB." + header.getIdCode().trim());
				} else if (header.getClassification() != null && header.getClassification().trim().length() > 0) {
					pdbMoleculeElement.setTitle(header.getClassification().trim());
				} else if (header.getTitle() != null && header.getTitle().trim().length() > 0) {
					pdbMoleculeElement.setTitle(header.getTitle().trim());
				}
			}
		}
		// set some title and id if nothing could be found
		if (pdbMoleculeElement.getId() == null || pdbMoleculeElement.getId().length() <= 0) {
			pdbMoleculeElement.setId(createRandomId());
		}
		if (pdbMoleculeElement.getTitle() == null || pdbMoleculeElement.getTitle().length() <= 0) {
			pdbMoleculeElement.setTitle("Unknown molecule");
		}
	}

	/**
	 * Creates a random id for this molecule element if no id information is given. As an ID must be set and must not
	 * occur more than once, it is created randomly.
	 */
	protected String createRandomId() {
		Random r = new Random();
		int randomId = r.nextInt(1000);
		return PREFIX_MOLECULE + randomId;
	}

	/**
	 * Converts a pdb model (list of chains) to a MSML model element
	 */
	protected MoleculeType convertModel(List<Chain> chainList, int modelNr, String parentID) {
		MoleculeType modelElement = new MoleculeType();
		modelElement.setMoleculeClass(MoleculeClassType.MODEL);
		String modelID = parentID + PREFIX_MODEL + modelNr;
		modelElement.setId(modelID);
		modelElement.setCustomId("" + modelNr);
		modelElement.setTitle("Model " + modelNr);
		// set count to number of chains
		modelElement.setCount(new Double(chainList.size()));

		for (int i = 0; i < chainList.size(); i++) {
			Chain chain = chainList.get(i);
			MoleculeType chainElement = convertChain(chain, i + 1, modelID);
			modelElement.getMolecule().add(chainElement);
		}
		return modelElement;
	}

	/**
	 * Converts a chain to a MSML chain element
	 */
	protected MoleculeType convertChain(Chain chain, int chainNr, String parentID) {
		MoleculeType chainElement = new MoleculeType();
		chainElement.setMoleculeClass(MoleculeClassType.CHAIN);
		String chainID = parentID + PREFIX_CHAIN + chain.getChainID().trim();
		chainElement.setId(chainID);
		chainElement.setTitle(chain.getChainID().trim());
		// set count to number of atoms
		chainElement.setCount(new Double(chain.getAtomLength()));

		List<Group> aminos = chain.getAtomGroups("amino");
		if (aminos.size() > 0) {
			List<MoleculeType> residueElements = convertAminos(aminos, chainID);
			chainElement.getMolecule().addAll(residueElements);
		}
		List<Group> nucs = chain.getAtomGroups("nucleotide");
		if (nucs.size() > 0) {
			List<MoleculeType> residueElements = convertNucleotides(nucs, chainID);
			chainElement.getMolecule().addAll(residueElements);
		}
		List<Group> hetatms = chain.getAtomGroups("hetatm");
		if (hetatms.size() > 0) {
			List<MoleculeType> residueElements = convertHetatms(hetatms, chainID);
			chainElement.getMolecule().addAll(residueElements);
		}

		return chainElement;
	}

	/**
	 * Converts a List of Amino Acids to a list of MSML Amino Acid elements
	 */
	protected List<MoleculeType> convertAminos(List<Group> aminos, String parentID) {
		List<MoleculeType> aminoElements = new ArrayList<MoleculeType>();
		for (int i = 0; i < aminos.size(); i++) {
			Group aminoGroup = aminos.get(i);
			if (aminoGroup instanceof AminoAcid) {
				AminoAcid aminoAcid = (AminoAcid) aminoGroup;
				MoleculeType aminoElement = new MoleculeType();
				aminoElement.setMoleculeClass(MoleculeClassType.AMINO_ACID);
				fillResidueElement(aminoElement, aminoAcid, parentID);
				aminoElements.add(aminoElement);
			}
		}
		return aminoElements;
	}

	/**
	 * Converts a List of Nucleotides to a list of MSML Nucleotide elements
	 */
	protected List<MoleculeType> convertNucleotides(List<Group> nucs, String parentID) {
		List<MoleculeType> nucElements = new ArrayList<MoleculeType>();
		for (int i = 0; i < nucs.size(); i++) {
			Group nucGroup = nucs.get(i);
			if (nucGroup instanceof NucleotideImpl) {
				NucleotideImpl nucleotide = (NucleotideImpl) nucGroup;
				MoleculeType nucElement = new MoleculeType();
				nucElement.setMoleculeClass(MoleculeClassType.NUCLEOTIDE);
				fillResidueElement(nucElement, nucleotide, parentID);
				nucElements.add(nucElement);
			}
		}
		return nucElements;
	}

	/**
	 * Converts a List of Hetatms to a list of MSML Hetatm elements
	 */
	protected List<MoleculeType> convertHetatms(List<Group> hetatms, String parentID) {
		List<MoleculeType> hetatmElements = new ArrayList<MoleculeType>();
		for (int i = 0; i < hetatms.size(); i++) {
			Group hetatmGroup = hetatms.get(i);
			if (hetatmGroup instanceof HetatomImpl) {
				HetatomImpl hetatm = (HetatomImpl) hetatmGroup;
				MoleculeType hetatmElement = new MoleculeType();
				hetatmElement.setMoleculeClass(MoleculeClassType.HETERO);
				fillResidueElement(hetatmElement, hetatm, parentID);
				hetatmElements.add(hetatmElement);
			}
		}
		return hetatmElements;
	}

	/**
	 * Helper method which fills the content of a Residue element
	 */
	protected void fillResidueElement(MoleculeType residueElement, Group residueGroup, String parentID) {
		String residueID = parentID + PREFIX_RESIDUE + residueGroup.getResidueNumber();
		residueElement.setId(residueID);
		residueElement.setCustomId("" + residueGroup.getResidueNumber());
		// set title to residue name
		residueElement.setTitle(residueGroup.getPDBName().trim());
		// set count to number of atoms
		residueElement.setCount(new Double(residueGroup.getAtoms().size()));

		// create and fill an AtomArray Element for residue
		AtomArrayType atomArrayElement = convertAtoms(residueGroup.getAtoms(), residueID);
		residueElement.setAtomArray(atomArrayElement);
	}

	/**
	 * Converts a list of Atoms to a MSML AtomArray element
	 */
	protected AtomArrayType convertAtoms(List<Atom> atoms, String parentID) {
		AtomArrayType atomArrayElement = new AtomArrayType();
		String arrayID = parentID + PREFIX_ATOM_ARRAY;
		atomArrayElement.setId(arrayID);

		for (int i = 0; i < atoms.size(); i++) {
			Atom atom = atoms.get(i);
			AtomType atomElement = convertAtom(atom, parentID);
			atomArrayElement.getAtom().add(atomElement);
		}

		return atomArrayElement;
	}

	/**
	 * Converts an Atom to a MSML Atom element
	 */
	protected AtomType convertAtom(Atom atom, String parentID) {
		AtomType atomElement = new AtomType();
		String atomID = parentID + PREFIX_ATOM + atom.getPDBserial();
		atomElement.setId(atomID);
		atomElement.setCustomId("" + atom.getPDBserial());
		// atom name
		atomElement.setTitle(atom.getName().trim());
		// element name
		atomElement.setElementType(atom.getElement().name().trim());
		// set alt loc
		Character altLoc = atom.getAltLoc();
		if (altLoc != null && !altLoc.equals(' ')) {
			atomElement.setAltLoc(altLoc.toString());
		}
		// set choords
		atomElement.setX3(new Float(atom.getX()));
		atomElement.setY3(new Float(atom.getY()));
		atomElement.setZ3(new Float(atom.getZ()));

		return atomElement;
	}

}
