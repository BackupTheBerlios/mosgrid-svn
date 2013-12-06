package de.mosgrid.msml.util;

import java.util.Iterator;
import java.util.Set;

import de.mosgrid.msml.jaxb.bindings.EntryType;

/**
 * Public interface for dictionaries. Instances which implement this interface
 * are created by the DictionaryFactory. This interface can be used in the
 * portlet API.
 * 
 * @author Andreas Zink
 * 
 */
public interface IDictionary {

	/**
	 * Returns an entry by its id, or null if entry does not exist
	 */
	EntryType getEntryById(String id);

	/**
	 * Returns an entry directly from the dictRef value which is converted to an
	 * id before, or null if entry does not exist
	 */
	EntryType getEntryByQualifiedName(String dictRef);

	/**
	 * @return The number of entries in the dictionary
	 */
	int getSize();

	/**
	 * @return An iterator of all entries
	 */
	Iterator<EntryType> getIterator();

	/**
	 * @return The set of entry id's
	 */
	Set<String> getEntryIDs();

	/**
	 * @return The toolsuite name of this dict e.g. Gaussian, Gromacs etc.
	 */
	String getToolSuite();

	/**
	 * @return The namespace of this dict
	 */
	String getNamespace();

	/**
	 * @return The dict prefix
	 */
	String getDictPrefix();
	
	public String getPrefixToNamespace(String nameSpace);

	public String getNamespaceToPrefix(String prefix);

}
