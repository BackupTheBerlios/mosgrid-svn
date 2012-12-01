package de.mosgrid.dygraph.util.parser;

import de.mosgrid.dygraph.widget.IData;

/**
 * Interface for data parsers
 * 
 * @author Andreas Zink
 * 
 */
public interface IParser {
	/**
	 * Parse a single line of file
	 */
	public void parseLine(String line);

	/**
	 * @return The parsed data object after all lines have been read
	 */
	public IData getData();
}
