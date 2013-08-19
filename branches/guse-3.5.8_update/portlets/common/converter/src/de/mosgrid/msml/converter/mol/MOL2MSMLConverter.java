package de.mosgrid.msml.converter.mol;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.converter.IToMSMLConverter;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.AtomArrayType;
import de.mosgrid.msml.jaxb.bindings.AtomType;
import de.mosgrid.msml.jaxb.bindings.BondArrayType;
import de.mosgrid.msml.jaxb.bindings.BondType;
import de.mosgrid.msml.jaxb.bindings.CustomProperty;
import de.mosgrid.msml.jaxb.bindings.MoleculeClassType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.OriginType;
import de.mosgrid.msml.util.ObjectFactorySingelton;

/**
 * This is a BioJava based MDL mol and sdf -> MSML converter
 * 
 * @author Charlotta Schaerfe
 * 
 */
public class MOL2MSMLConverter implements IToMSMLConverter {
	private final Logger LOGGER = LoggerFactory.getLogger(MOL2MSMLConverter.class);
	private static final List<String> _excludedProperties;
	
	static {
		_excludedProperties = new ArrayList<String>();
		_excludedProperties.add("cdk:Title");
	}

	@Override
	public MoleculeType convertFromString(String moleculeString) throws MSMLConversionException {
		ByteArrayInputStream bais = new ByteArrayInputStream(moleculeString.getBytes());
		return convertFromStream(bais);
	}

	@Override
	public MoleculeType convertFromStream(InputStream is) throws MSMLConversionException {
		ArrayList<IMolecule> sdfMolecules = new ArrayList<IMolecule>();
		boolean skip = true;
		IteratingMDLReader reader = new IteratingMDLReader(is, DefaultChemObjectBuilder.getInstance(), skip);
		while (reader.hasNext()) {
			IMolecule sdfMolecule = (IMolecule) reader.next();
			sdfMolecules.add(sdfMolecule);
		}
		MoleculeType converted = new MoleculeType();
		if (sdfMolecules.size() < 1) {
			throw new MSMLConversionException("Cannot convert molecule(s) because MDL file (.mol or .sdf) is empty");
		} else if (sdfMolecules.size() == 1) {
			converted = convert(sdfMolecules.get(0));
		} else {
			converted = convert(sdfMolecules);
		}
		converted.setMoleculeClass(MoleculeClassType.MOLECULE_LIST);
		// set convention
		converted.setConvention("convention:molecular");
		// set the origin format
		converted.setOrigin(OriginType.SDF);
		return converted;
	}

	@Override
	public MoleculeType convertFromStructure(Object moleculeStructure) throws MSMLConversionException {
		if (moleculeStructure instanceof IMolecule) {
			return convert((IMolecule) moleculeStructure);
		} else {
			throw new MSMLConversionException("Given object does not represent a MDL mol/sdf structure!");
		}
	}

	/**
	 * Converts a Molecule Element from a CDK Molecule object
	 */
	public MoleculeType convert(IMolecule molecule) throws MSMLConversionException {
		try {
			MoleculeType molMoleculeElement = ObjectFactorySingelton.getFactory().createMoleculeType();
			setMetaData(molMoleculeElement, molecule);
			// molMoleculeElement.setMoleculeClass(MoleculeClassType.HETERO);//?
			// Add Atom block:
			Iterable<IAtom> atomBlock = molecule.atoms();
			AtomArrayType atomArrayElement = convertAtoms(atomBlock, molMoleculeElement.getId());

			// Save hashes of atoms to map the atom back to the atom ID
			ArrayList<String> hashes = generateHashCodes(atomBlock);
			molMoleculeElement.setAtomArray(atomArrayElement);

			// Add bond block
			Iterable<IBond> bondBlock = molecule.bonds();
			BondArrayType bondArrayElement = convertBonds(bondBlock, molMoleculeElement.getId(), hashes);
			molMoleculeElement.setBondArray(bondArrayElement);
			
			// Add properties block
			for (Entry<Object, Object> prop : molecule.getProperties().entrySet()) {
				if (_excludedProperties.contains(prop.getKey()))
						continue;
				CustomProperty cmlProp = ObjectFactorySingelton.getFactory().createCustomProperty();
				cmlProp.setKey((String) prop.getKey());
				cmlProp.setValue((String) prop.getValue());
				molMoleculeElement.getCustomProperty().add(cmlProp);
			}

			return molMoleculeElement;
		} catch (Exception e) {
			String msg = "Conversion of MDL molecule structure failed! " + e.getMessage();
			LOGGER.error(msg, e);
			throw new MSMLConversionException(msg, e);
		}
	}

	/**
	 * Converts a List of Molecule Elements from a CDK Molecule object
	 */
	public MoleculeType convert(ArrayList<IMolecule> molecules) throws MSMLConversionException {
		try {
			MoleculeType molMolecules = new MoleculeType();
			for (int i = 0; i < molecules.size(); i++) {
				MoleculeType molecule = convert(molecules.get(i));
				molecule.setCustomId("" + i);
				molMolecules.getMolecule().add(molecule);
			}
			return molMolecules;
		} catch (Exception e) {
			String msg = "Conversion of MDL molecules failed! " + e.getMessage();
			LOGGER.error(msg, e);
			throw new MSMLConversionException(msg, e);
		}

	}

	/**
	 * Sets the format origin and tries to find a good title
	 */
	protected void setMetaData(MoleculeType molMolecule, IMolecule molStructure) {
		if (molStructure != null) {
			if (molStructure.getProperty("cdk:Title") != null) {
				molMolecule.setTitle(molStructure.getProperty("cdk:Title").toString());
			} else {
				molMolecule.setTitle("Unknown molecule");
			}
			if (molStructure.getID() != null) {
				molMolecule.setId(molStructure.getID().toString());
			} else {
				molMolecule.setId("ID" + Integer.toString(molStructure.hashCode()));
			}
			if (molStructure.getAtomCount() > 0) {
				molMolecule.setCount((double) molStructure.getAtomCount());
			}
			// ToDo add additional properties from the >property block and/or M
			// block
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
	 * Converts a list of Atoms to a MSML AtomArray element
	 */
	protected AtomArrayType convertAtoms(Iterable<IAtom> atoms, String parentID) {
		AtomArrayType atomArrayElement = new AtomArrayType();
		String arrayID = parentID + PREFIX_ATOM_ARRAY;
		atomArrayElement.setId(arrayID);
		for (IAtom atom : atoms) {
			AtomType atomElement = convertAtom(atom, parentID);
			atomArrayElement.getAtom().add(atomElement);
		}
		return atomArrayElement;
	}

	/**
	 * Converts an Atom to a MSML Atom element
	 */
	protected AtomType convertAtom(IAtom atom, String parentID) {
		AtomType atomElement = new AtomType();
		String id = "";
		if (atom.getID() != null) {
			id = atom.getID();
		} else {
			id = Integer.toString(atom.hashCode());
		}
		String atomID = parentID + PREFIX_ATOM + id;
		atomElement.setId(atomID);
		atomElement.setCustomId("" + id);
		// atom name
		atomElement.setTitle(atom.getAtomTypeName());
		// element name
		atomElement.setElementType(atom.getSymbol());
		double x, y, z;
		// set choords
		if (atom.getPoint3d() != null) {
			x = atom.getPoint3d().x;
			y = atom.getPoint3d().y;
			z = atom.getPoint3d().z;
		} else {// what if mol in 2d? -> has to use getPoint2d
			x = atom.getPoint2d().x;
			y = atom.getPoint2d().y;
			z = 0.0;
		}
		if (atom.getFormalCharge() != null) {
			atomElement.setFormalCharge(BigInteger.valueOf(atom.getFormalCharge()));
		}
		atomElement.setX3(new Float(x));
		atomElement.setY3(new Float(y));
		atomElement.setZ3(new Float(z));

		return atomElement;
	}

	/**
	 * Converts a list of Bonds to a MSML BondArray element
	 */
	protected BondArrayType convertBonds(Iterable<IBond> bonds, String parentID, ArrayList<String> hashes) {
		BondArrayType bondArrayElement = new BondArrayType();
		String arrayID = parentID + PREFIX_BOND_ARRAY;
		bondArrayElement.setId(arrayID);
		for (IBond bond : bonds) {
			BondType bondElement = convertBond(bond, parentID, hashes);
			bondArrayElement.getBond().add(bondElement);
		}
		return bondArrayElement;
	}

	/**
	 * Converts a Bond to a MSML Bond element
	 */
	protected BondType convertBond(IBond bond, String parentID, ArrayList<String> hashes) {
		BondType bondElement = new BondType();
		String bondID = parentID + PREFIX_BOND + bond.hashCode();
		bondElement.setId(bondID);
		int atom1 = hashes.indexOf(Integer.toString(bond.getAtom(0).hashCode())) + 1;
		int atom2 = hashes.indexOf(Integer.toString(bond.getAtom(1).hashCode())) + 1;
		// bondElement.setCustomId("" + bond.getID());
		// atom name
		// bondElement.setTitle(bond.getID());
		/*
		 * System.out.println(bond.getAtom(0).getSymbol()); System.out.println(bond.getAtom(1).getSymbol());
		 */

		String a1 = "a" + atom1;
		String a2 = "a" + atom2;
		List<String> atomrefs = bondElement.getAtomRefs2();
		atomrefs.add(a1); // add first atom in bond
		atomrefs.add(a2); // add second atom in bond
		if (bond.getOrder() != null) {
			bondElement.setOrder(bond.getOrder().toString());
		}
		/*
		 * if (bond.getStereo() != null){ bondElement.setStereo(bond.getStereo().toString()); //no stereo property as of
		 * now }
		 */

		return bondElement;
	}

	protected ArrayList<String> generateHashCodes(Iterable<IAtom> atoms) {
		ArrayList<String> hashes = new ArrayList<String>();
		for (IAtom atom : atoms) {
			hashes.add(Integer.toString(atom.hashCode()));
		}
		return hashes;
	}

}
