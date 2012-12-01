package de.mosgrid.msml.converter.gro;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import jgromacs.data.Atom;
import jgromacs.data.Residue;
import jgromacs.data.Structure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.converter.IToMSMLConverter;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.AtomArrayType;
import de.mosgrid.msml.jaxb.bindings.AtomType;
import de.mosgrid.msml.jaxb.bindings.MoleculeClassType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.OriginType;
import de.mosgrid.parser.gro.GROParser;

/**
 * A gro to msml converter based on the gro parser of JGromacs
 * 
 * @author Andreas Zink
 * 
 */
public class GRO2MSMLConverter implements IToMSMLConverter {
	private final Logger LOGGER = LoggerFactory.getLogger(GRO2MSMLConverter.class);

	private GROParser parser = new GROParser();
	@Override
	public MoleculeType convertFromStream(InputStream is) throws MSMLConversionException {
		Structure groStructure = null;
		try {
			groStructure = parser.readStructureFromGRO(is);

		} catch (IOException e) {
			String msg = "Conversion of gro inputstream failed!";
			LOGGER.error(msg, e);
			throw new MSMLConversionException(e.getMessage(), e);
		}
		return convert(groStructure);
	}

	@Override
	public MoleculeType convertFromString(String moleculeString) throws MSMLConversionException {
		throw new MSMLConversionException("Cannot convert GRO from String");
	}

	@Override
	public MoleculeType convertFromStructure(Object moleculeStructure) throws MSMLConversionException {
		if (moleculeStructure instanceof Structure) {
			return convert((Structure) moleculeStructure);
		} else {
			throw new MSMLConversionException("Given object does not represent a GRO structure!");
		}
	}

	private MoleculeType convert(Structure groStructure) throws MSMLConversionException {
		MoleculeType groMoleculeElement = new MoleculeType();
		setMetaData(groMoleculeElement, groStructure);
		// set count to number of atoms
		groMoleculeElement.setCount(new Double(groStructure.getNumberOfAtoms()));

		for (Residue residue : groStructure.getResiduesAsArrayList()) {
			MoleculeType residueElement = new MoleculeType();
			residueElement.setCustomId("" + residue.getIndex());
			fillResidueElement(residueElement, residue, groMoleculeElement.getId());
			if (residue.isAminoAcid()) {
				// parse amino acids
				residueElement.setMoleculeClass(MoleculeClassType.AMINO_ACID);
			} else if (residue.isWater() || residue.isOther()) {
				// parse hetero atoms
				residueElement.setMoleculeClass(MoleculeClassType.HETERO);
			}
			groMoleculeElement.getMolecule().add(residueElement);
		}

		return groMoleculeElement;
	}

	private void setMetaData(MoleculeType pdbMoleculeElement, Structure groStructure) {
		// set convention
		pdbMoleculeElement.setConvention("convention:molecular");
		// set the origin format
		pdbMoleculeElement.setOrigin(OriginType.GRO);

		if (groStructure != null && groStructure.getName() != null) {
			String name = groStructure.getName();
			pdbMoleculeElement.setTitle(name.trim());
		} else {
			pdbMoleculeElement.setTitle("Unknown molecule");
		}

		pdbMoleculeElement.setId(createRandomId());
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
	 * Helper method which fills the content of a Residue element
	 */
	protected void fillResidueElement(MoleculeType residueElement, Residue residue, String parentID) {
		String residueID = parentID + PREFIX_RESIDUE + residue.getIndex();
		residueElement.setId(residueID);
		// set title to residue name
		residueElement.setTitle(residue.getName().trim());
		// set count to number of atoms
		residueElement.setCount(new Double(residue.getAtomsAsArrayList().size()));

		// create and fill an AtomArray Element for residue
		AtomArrayType atomArrayElement = convertAtoms(residue.getAtomsAsArrayList(), residueID);
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
		String atomID = parentID + PREFIX_ATOM + atom.getIndex();
		atomElement.setId(atomID);
		atomElement.setCustomId("" + atom.getIndex());
		// atom name
		atomElement.setTitle(atom.getName().trim());
		// element name
		if (atom.getAtomType().getCode() != null && atom.getAtomType().getCode().length() > 0) {
			atomElement.setElementType(atom.getAtomType().getCode());
		}

		// set choords
		atomElement.setX3(new Float(atom.getXCoordinate()));
		atomElement.setY3(new Float(atom.getYCoordinate()));
		atomElement.setZ3(new Float(atom.getZCoordinate()));

		// set velocities
		atomElement.setVX(atom.getXVelocity());
		atomElement.setVY(atom.getYVelocity());
		atomElement.setVZ(atom.getZVelocity());

		return atomElement;
	}

}
