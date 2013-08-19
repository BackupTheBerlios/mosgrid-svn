package de.mosgrid.dygraph.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import de.mosgrid.dygraph.util.parser.IParser;
import de.mosgrid.dygraph.util.parser.SimpleXVGParser;
import de.mosgrid.dygraph.widget.IData;

/**
 * Helper class which converts any file to the desired data format, given a file parser and inputstream.
 * 
 * @author Andreas Zink
 * 
 */
public class DataConverter {
	private static DataConverter instance = null;

	public final Map<String, Class<? extends IParser>> filetype2ParserMap = new HashMap<String, Class<? extends IParser>>();

	private DataConverter() {
		filetype2ParserMap.put("xvg", SimpleXVGParser.class);
	}

	public static DataConverter getInstance() {
		if (instance == null) {
			instance = new DataConverter();
		}
		return instance;
	}

	/**
	 * Reads the given inputstream and tries to convert the content to a IData object. Also tries to choose the correct
	 * parser for given filetype.
	 */
	public IData convert(String filename, InputStream is) {
		if (filename.endsWith(".xvg")) {
			return convert(new SimpleXVGParser(), is);
		}
		return null;
	}

	/**
	 * Reads the given inputstream and tries to convert the content to a IData object with the given parser.
	 */
	public IData convert(IParser parser, InputStream is) {
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
		return convert(parser, bufReader);
	}

	/**
	 * Reads the given inputstream and tries to convert the content to a IData object with the given parser.
	 */
	public IData convert(IParser parser, BufferedReader reader) {
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				parser.parseLine(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parser.getData();
	}
}
