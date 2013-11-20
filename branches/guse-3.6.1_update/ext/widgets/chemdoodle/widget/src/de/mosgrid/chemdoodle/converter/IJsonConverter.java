package de.mosgrid.chemdoodle.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Interface for json converters
 * 
 * @author Andreas Zink
 * 
 */
public interface IJsonConverter {
	/**
	 * Converts content from given inputstream to json format.
	 * 
	 * @param reader
	 *            The inputstream to read from
	 * @param writer
	 *            The outputstream to write to, or null if not needed
	 * @return String representation of json formated content
	 * @throws JsonConversionException
	 *             If conversion fails
	 * @throws IOException
	 *             If conversion failed because of IO problems
	 */
	public String convert(InputStream is, OutputStream os) throws JsonConversionException, IOException;

	/**
	 * Converts content from given inputstream to json format.
	 * 
	 * @param reader
	 *            The inputstream to read from
	 * @param writer
	 *            The outputstream to write to, or null if not needed
	 * @return String representation of json formated input
	 * @throws JsonConversionException
	 *             If conversion fails
	 * @throws IOException
	 *             If conversion failed because of IO problems
	 */
	public String convert(InputStreamReader reader, OutputStreamWriter writer) throws JsonConversionException,
			IOException;

	/**
	 * Converts content from given inputstream to json format.
	 * 
	 * @param reader
	 *            The inputstream to read from
	 * @param writer
	 *            The outputstream to write to, or null if not needed
	 * @return String representation of json formated input
	 * @throws JsonConversionException
	 *             If conversion fails
	 * @throws IOException
	 *             If conversion failed because of IO problems
	 */
	public String convert(BufferedReader reader, BufferedWriter writer) throws JsonConversionException, IOException;
	
	public IConverterParameters getConverterParameters();
}
