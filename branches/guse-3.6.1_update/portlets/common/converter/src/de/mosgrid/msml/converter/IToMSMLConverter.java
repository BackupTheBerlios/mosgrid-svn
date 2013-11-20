package de.mosgrid.msml.converter;

import java.io.InputStream;

import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;

/**
 * Interface for ANY_FORMAT->MSML converters
 * 
 * @author Andreas Zink
 * 
 */
public interface IToMSMLConverter {
	/*
	 * MSML element id prefixes
	 */
	final String PREFIX_MOLECULE = "MOL.";
	final String PREFIX_MODEL = "_M.";
	final String PREFIX_CHAIN = "_C.";
	final String PREFIX_RESIDUE = "_R.";
	final String PREFIX_ATOM_ARRAY = "_AtomArray";
	final String PREFIX_ATOM = "_A.";
	final String PREFIX_BOND_ARRAY = "_BondArray";
	final String PREFIX_BOND = "_B.";

	/**
	 * Creates a MSML molecule element by reading given inputstream
	 * 
	 * @param is
	 *            The stream to read from
	 * @return The MSML molecule element
	 * @throws MSMLConversionException
	 *             If conversion fails
	 */
	MoleculeType convertFromStream(InputStream is) throws MSMLConversionException;

	/**
	 * Creates a MSML molecule element from given String
	 * 
	 * @param moleculeString
	 *            A molecule String to be formatted
	 * @return The MSML molecule element
	 * @throws MSMLConversionException
	 *             If conversion fails
	 */
	MoleculeType convertFromString(String moleculeString) throws MSMLConversionException;

	/**
	 * Creates a MSML molecule element from given structure object.
	 * 
	 * @param moleculeStructure
	 *            Object which represents the content of the structure e.g. obtained from BioJava, CDK etc.
	 * @return The MSML molecule element
	 * @throws MSMLConversionException
	 *             If conversion fails
	 */
	MoleculeType convertFromStructure(Object moleculeStructure) throws MSMLConversionException;
}
