package de.mosgrid.msml.util.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class providing different kinds of static helper methods which are needed quite often in different classes.
 * 
 * @author Andreas Zink
 * 
 */
public class XmlHelper {
	private static XmlHelper instance = null;
	private static Pattern unqualifyPattern = Pattern.compile("(([^:]+):)?([^:]*)");

	private XmlHelper() {
		// private constructor as we must not create an instance of this helper
	}

	public static synchronized XmlHelper getInstance() {
		if (instance == null) {
			instance = new XmlHelper();
		}
		return instance;
	}

	/**
	 * 
	 * Helper method for parsing the id of a reference string which links another namespace. This is needed because the
	 * namespace prefix can be different in every docucment. No checks on validity of prefix and suffix are done.
	 * 
	 * @param qName
	 *            A fully qualified reference.
	 * @return 'xsd:integer' becomes 'integer'. 'integer' stays 'integer'. Returns null, if parameter is not a legal
	 *         fully qualified name (e.g. 'xsd:integer:integer' or ':integer')
	 */
	public String getSuffix(String qName) {
		if (qName == null || "".equals(qName))
			return null;
		Matcher m = unqualifyPattern.matcher(qName);
		if (!m.matches())
			return null;
		String res = m.group(3);
		if ("null".equals(res))
			return null;
		return res;
	}

	/**
	 * Returns the prefix of a given qualified name e.g. shall return 'cmlx' for 'cmlx:element'. No checks on validity
	 * of prefix and suffix are done.
	 * 
	 * @param qName
	 *            A fully qualified reference.
	 * @return The prefix of given qualified name. The prefix may be empty in case of the default namespace. Returns
	 *         null, if parameter is not a legal fully qualified name (e.g. 'xsd:integer:integer' or ':integer')
	 */
	public String getPrefix(String qName) {
		if (qName == null || "".equals(qName))
			return null;
		Matcher m = unqualifyPattern.matcher(qName);
		if (!m.matches())
			return null;
		if (m.group(2) == null)
			return "";
		return m.group(2);
	}
}
