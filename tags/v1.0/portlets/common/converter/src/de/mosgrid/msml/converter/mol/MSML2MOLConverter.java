package de.mosgrid.msml.converter.mol;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.SDFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.converter.IFromMSMLConverter;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.AtomType;
import de.mosgrid.msml.jaxb.bindings.BondType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.OriginType;

/**
 * This is a MSML -> MDL sdf converter
 * 
 * @author Charlotta Schaerfe
 * 
 */
public class MSML2MOLConverter implements IFromMSMLConverter {
	private final Logger LOGGER = LoggerFactory.getLogger(MSML2MOLConverter.class);
	// desired mol coordinate format
	private static final DecimalFormat d3 = (DecimalFormat) NumberFormat.getInstance(java.util.Locale.UK);
	static {
		d3.setMaximumIntegerDigits(3);
		d3.setMinimumFractionDigits(3);
		d3.setMaximumFractionDigits(3);
	}

	/**
	 * Converts a given MSML molecule element to MDL sdf format
	 * 
	 * @param writer
	 *            Optional outputstream e.g. to a file
	 * @return The MSML content formatted in the MDL convention (Ctab + properties)
	 */
	@Override
	public String convert(MoleculeType moleculeElement, OutputStream os) throws MSMLConversionException {
		 LOGGER.trace("Trying to convert MSML molecule element to MDL sdf format");
		if (moleculeElement.getOrigin() == null && moleculeElement.getOrigin() != OriginType.SDF) {
			throw new MSMLConversionException("Can not create MDL sdf from MSML without known origin attribute");
		}
		if (moleculeElement.getMolecule().size() < 1 && moleculeElement.getAtomArray() == null
				&& moleculeElement.getBondArray() == null) {
			throw new MSMLConversionException("Cannot convert an empty MSML molecule element");
		}
		// convert molecules in moleculeElement into a CDK MoleculeSet Object
		try {
			MoleculeSet mols = convert(moleculeElement);
			SDFWriter writer = new SDFWriter(os);
			writer.write(mols);
			writer.close();

		} catch (CDKException e) {
			String msg = "Conversion of MSML molecule element failed! " + e.getMessage();
			 LOGGER.error(msg, e);
			e.printStackTrace();
		} catch (IOException e) {
			String msg = "Conversion of MSML molecule element failed! " + e.getMessage();
			 LOGGER.error(msg, e);
			e.printStackTrace();
		}
		return "Done.";
	}

	/**
	 * Converts a given MSML molecule element to a CDK MoleculeSet object
	 * 
	 * @param moleculeElement
	 *            The MSML object to be converted
	 * @return A MoleculeSet containing the molecules in the MoleculeElement
	 */
	private MoleculeSet convert(MoleculeType moleculeElement) {
		MoleculeSet molecules = new MoleculeSet();
		List<MoleculeType> moleculeList = moleculeElement.getMolecule();

		// a moleculeObject with multiple molecules stored inside
		if (moleculeList.size() > 0) {
			for (int i = 0; i < moleculeList.size(); i++) {
				MoleculeType mol = moleculeList.get(i);
				IMolecule cdkMol = convertMolecule(mol);
				molecules.addMolecule(cdkMol);
			}
		}
		// only 1 molecule saved in MoleculeType object
		else {
			IMolecule cdkMol = convertMolecule(moleculeElement);
			molecules.addMolecule(cdkMol);
		}
		return molecules;
	}

	/**
	 * Converts a given MSML molecule element to a CDK Molecule object
	 * 
	 * @param moleculeElement
	 *            The MSML molecule object to be converted
	 * @return A Molecule containing the molecule in the MoleculeElement
	 */
	private IMolecule convertMolecule(MoleculeType mol) {
		Molecule convertedMol = new Molecule();
		String title = mol.getTitle();
		String id = mol.getId();
		convertedMol.setID(id);
		convertedMol.setProperty("cdk:Title", title);
		// System.out.println(mol.getCount());
		List<AtomType> atoms = mol.getAtomArray().getAtom();
		ArrayList<IAtom> cdkAtoms = new ArrayList<IAtom>(atoms.size());
		for (int i = 0; i < atoms.size(); i++) {
			AtomType atom = atoms.get(i);
			String symbol = atom.getElementType();
			BigInteger charge = atom.getFormalCharge();
			double x, y, z;
			IAtom a = new Atom(atom.getAtomName());
			if (atom.getZ3() != null) {
				x = atom.getX3();
				y = atom.getY3();
				z = atom.getZ3();
				Point3d coords3 = new Point3d(x, y, z);
				a.setPoint3d(coords3);
			} else if (atom.getY2() != null) {
				x = atom.getX2();
				y = atom.getY2();
				Point2d coords2 = new Point2d(x, y);
				a.setPoint2d(coords2);
			}
			a.setSymbol(symbol);
			a.setFormalCharge(charge.intValue());
			// System.out.println(a.toString());
			convertedMol.addAtom(a);
			cdkAtoms.add(a);
		}

		List<BondType> bonds = mol.getBondArray().getBond();
		for (int j = 0; j < bonds.size(); j++) {
			BondType bond = bonds.get(j);
			List<String> refAtoms = bond.getAtomRefs2();
			String order = bond.getOrder();
			IAtom refAtom1 = cdkAtoms.get(Integer.parseInt((refAtoms.get(0).substring(1))) - 1);
			IAtom refAtom2 = cdkAtoms.get(Integer.parseInt((refAtoms.get(1).substring(1))) - 1);
			IBond b = new Bond(refAtom1, refAtom2, IBond.Order.valueOf(order));
			convertedMol.addBond(b);
		}

		// ToDo: Add property section if wanted!

		return convertedMol;
	}
}
