package de.mosgrid.msml.converter;

import java.io.OutputStream;

import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;

/**
 * Interface for MSML->ANY_FORMAT converters
 * 
 * @author Andreas Zink
 * 
 */
public interface IFromMSMLConverter {
	/**
	 * Converts a given MSML molecule element to any desired format
	 * 
	 * @param moleculeElement
	 *            The MSML element to read from
	 * @param os
	 *            An optional outputstream to write to or null
	 * @return The formatted content of the molecule element
	 * @throws MSMLConversionException
	 *             If conversion fails
	 */
	String convert(MoleculeType moleculeElement, OutputStream os) throws MSMLConversionException;
}
