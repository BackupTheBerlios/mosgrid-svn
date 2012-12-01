package de.mosgrid.msml.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.DictDir;
import de.mosgrid.msml.jaxb.bindings.Dictionary;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.util.helper.XmlHelper;

/**
 * Simple factory for dictionaries which provide faster access on their entries than pure jaxb bindings.
 * DictionaryFactory is a singleton and can be used with its getInstance() method. Furthermore it parses every
 * dictionary only once and saves it in a map.
 * 
 * @author Andreas Zink
 * 
 */
public class DictionaryFactory {
	private final Logger LOGGER = LoggerFactory.getLogger(DictionaryFactory.class);
	private static DictionaryFactory instance = null;

	/* JAXB Stuff */
	// private JAXBContext jaxbContext;
	// private Unmarshaller unmarshaller;
	/* Mappings */
	// maps all parsed dictionaries for a DictDir
	private Map<DictDir, Collection<IDictionary>> dir2Dictionaries;
	// maps namespaces to dictionaries
	private Map<String, IDictionary> namespace2Dictionary;
	// maps filename to last modified timestemp (this is used for updating dictionaries after file changes)
	private Map<String, Long> filename2lastModified;
	// maps filename to parsed dict instance(this is used for updating dictionaries after file changes)
	private Map<String, IDictionary> filename2dictionary;

	/* synchronization */
	// locks factory mappings while updating
	private final Object updateLock = new Object();

	/**
	 * @return The instance of the singleton factory
	 */
	public static synchronized DictionaryFactory getInstance() {
		return getInstance(true);
	}

	/**
	 * With this alternate of getInstance you can decide if you want to initialize all dictionaries. Eventually this
	 * method should be removed to make a lazy load of dictionaries but the portlets have a problem with that right now.
	 * This method is used by the parser because it may not have access to the dictionaries on the portlet.
	 * 
	 * @param doInit
	 *            If true all dictionaries will be loaded according to the properties-file.
	 *            If false, then no dictionaries will be loaded. This is needed for the
	 *            GenericParser as it does not use the properties-file to locate dictionaries.
	 * 
	 * @return A singleton instance.
	 */
	public static synchronized DictionaryFactory getInstance(boolean doInit) {
		if (instance == null) {
			instance = new DictionaryFactory();
			if (doInit)
				instance.initCommonDicts();
		}
		return instance;
	}

	private DictionaryFactory() {
		dir2Dictionaries = new HashMap<DictDir, Collection<IDictionary>>();
		namespace2Dictionary = new HashMap<String, IDictionary>();
		filename2lastModified = new HashMap<String, Long>();
		filename2dictionary = new HashMap<String, IDictionary>();
		// initJAXB();
	}

	/**
	 * Initialization of JAXB for parsing xml files
	 */
	// private void initJAXB() {
	// try {
	// this.jaxbContext = JAXBContext.newInstance("de.mosgrid.msml.jaxb.bindings");
	// this.unmarshaller = jaxbContext.createUnmarshaller();
	// } catch (JAXBException e) {
	// LOGGER.error("JAXB initialization error!", e);
	// }
	// }

	/**
	 * This function loads all dictionaries that are common and not domain specific, as these might not be loaded before
	 * usage.
	 */
	private void initCommonDicts() {
		try {
			getDictionaries(DictDir.ADAPTER);
			getDictionaries(DictDir.ENVIRONMENT);
			getDictionaries(DictDir.PARSER);
		} catch (Exception e) {
			LOGGER.error("Could not initialize common dictionaries!", e);
		}

	}

	/**
	 * This method checks for updated dictionary files and creates new dictionary instances if needed
	 */
	public void update() {
		synchronized (updateLock) {
			HashMap<DictDir, Collection<IDictionary>> mapCopy = new HashMap<DictDir, Collection<IDictionary>>(
					dir2Dictionaries);
			// check already parsed dictionary directories
			for (DictDir dir : mapCopy.keySet()) {
				updateDictionaries(dir);
			}
		}
	}

	/**
	 * This method checks for updated dictionary files and creates new dictionary instances if needed in given dir
	 */
	public void updateDictionaries(DictDir dir) {
		synchronized (updateLock) {
			LOGGER.debug("Updating dictionaries in " + dir);
			// iterate over files and check timestemp
			for (File dictFile : getDictionaryFiles(dir)) {
				String filename = dictFile.getName();
				if (filename2lastModified.containsKey(filename)) {
					long lastModified = dictFile.lastModified();

					if (lastModified != filename2lastModified.get(filename)) {
						LOGGER.trace("Updating changes in dictionary" + filename);
						IDictionary oldDict = filename2dictionary.get(filename);
						if (dir2Dictionaries.get(dir).remove(oldDict)) {
							IDictionary updatedDict = parseDictionary(dictFile);
							dir2Dictionaries.get(dir).add(updatedDict);
						}
					}
				} else {
					// create new dict if new file found
					LOGGER.trace("Adding new dictionary " + filename);
					IDictionary newDict = parseDictionary(dictFile);
					dir2Dictionaries.get(dir).add(newDict);
				}
			}
		}
	}

	/**
	 * Helper method which gets all dictionary files from given directory
	 */
	private File[] getDictionaryFiles(DictDir dictDir) {
		File dictFolder = new File(dictDir.getPath());
		File[] files = null;
		if (dictFolder.exists() && dictFolder.isDirectory()) {
			files = dictFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File fileName) {
					String name = fileName.getName();
					return name.endsWith(".xml") || name.endsWith(".cml") || name.endsWith(".msml");
				}
			});
		} else {
			LOGGER.warn("The desired dictionary directory " + dictFolder + "does not exist!");
		}
		return files;
	}

	public Collection<IDictionary> getAllDictionaries() {
		List<IDictionary> dicts = new ArrayList<IDictionary>();
		for (Entry<DictDir, Collection<IDictionary>> dirDictEntry : dir2Dictionaries.entrySet()) {
			for (IDictionary dict : dirDictEntry.getValue()) {
				dicts.add(dict);
			}
		}
		return dicts;
	}

	/**
	 * 
	 * @param dictDir
	 *            The directory to read from
	 * @return An array of dictionaries contained in given directory
	 */
	public Collection<IDictionary> getDictionaries(DictDir dir) {
		// wait until updates are done
		synchronized (updateLock) {
			// check if already loaded
			if (!dir2Dictionaries.containsKey(dir)) {
				// load dicts
				List<IDictionary> dicts = new ArrayList<IDictionary>();

				for (File dictFile : getDictionaryFiles(dir)) {
					IDictionary dict = parseDictionary(dictFile);
					dicts.add(dict);
				}
				// map all dicts to DictDir
				dir2Dictionaries.put(dir, dicts);
			}

			return dir2Dictionaries.get(dir);
		}
	}

	/**
	 * This function is used by the generic parser to retrieve instances of IDictionary. The parser passes inputstreams
	 * to the resources within its classpath in contrary to the portlets that have a path to the dictionaries. Within
	 * this function dictionaries are not cached.
	 * 
	 * @param streams
	 *            This paramameter contains a list of inputstreams from which the dictionary xml-files can be read.
	 * 
	 * @return An array of dictionaries contained in given directory.
	 */
	public synchronized Collection<IDictionary> getDictionaries(List<String> resources) {
		// wait until updates are done, this method also writes to mappings indirectly
		synchronized (updateLock) {
			List<IDictionary> dicts = new ArrayList<IDictionary>();
			for (String resource : resources) {
				InputStream stream = getClass().getClassLoader().getResourceAsStream(resource);
				IDictionary dict = parseDictionary(stream);

				// after parsing JAXB will have closed the stream. to init maps
				// a new stream is needed as the old stream cannot be reseted.
				stream = getClass().getClassLoader().getResourceAsStream(resource);
				((DictionaryWrapper) dict).initMaps(stream);
				dicts.add(dict);
				try {
					if (stream != null)
						stream.close();
				} catch (IOException e) {
					LOGGER.error("Could not close reopened stream.", e);
				}
			}
			return dicts;
		}
	}

	/**
	 * Helper method for parsing dictionary files
	 */
	private IDictionary parseDictionary(File dictFile) {
		// try {
		DictionaryWrapper dict = new DictionaryWrapper(dictFile);
		namespace2Dictionary.put(dict.getNamespace(), dict);

		// FileInputStream stream = new FileInputStream(dictFile);
		// IDictionary dict = parseDictionary(stream);
		// create mappings for possible updates
		filename2lastModified.put(dictFile.getName(), dictFile.lastModified());
		filename2dictionary.put(dictFile.getName(), dict);
		return dict;
		// } catch (FileNotFoundException e) {
		// LOGGER.error("Could not parse Dictionary.", e);
		// }
		// return null;
	}

	/**
	 * Helper method for parsing dictionary files
	 */
	private IDictionary parseDictionary(InputStream stream) {
		// try {
		DictionaryWrapper dict = new DictionaryWrapper(stream);
		namespace2Dictionary.put(dict.getNamespace(), dict);

		// FileInputStream stream = new FileInputStream(dictFile);
		// IDictionary dict = parseDictionary(stream);
		// create mappings for possible updates
		// filename2lastModified.put(dictFile.getName(), dictFile.lastModified());
		// filename2dictionary.put(dictFile.getName(), dict);
		return dict;
		// } catch (FileNotFoundException e) {
		// LOGGER.error("Could not parse Dictionary.", e);
		// }
		// return null;
	}

	/**
	 * Helper method for parsing dictionary files
	 */
	// private IDictionary parseDictionary(InputStream stream) {
	// DictionaryWrapper dictionary = null;
	// try {
	// // unmarshall dictionary file
	// Dictionary dictElement = (Dictionary) unmarshaller.unmarshal(stream);
	//
	// dictionary = new DictionaryWrapper(dictElement);
	// namespace2Dictionary.put(dictionary.getNamespace(), dictionary);
	// } catch (JAXBException e) {
	// LOGGER.error("Could not parse Dictionary.", e);
	// }
	// return dictionary;
	// }

	/**
	 * @param namespace
	 * @return The dictionary with given namespace
	 */
	public IDictionary getDictionary(String namespace) {
		// wait until updates are done
		synchronized (updateLock) {
			return namespace2Dictionary.get(namespace);
		}
	}

	/**
	 * Private inner dictionary class. Hides implementation, provides interface.
	 * 
	 * @author Andreas Zink
	 * 
	 */
	private class DictionaryWrapper extends MSMLEditor implements IDictionary {
		@SuppressWarnings("unused")
		private static final long serialVersionUID = 277852834594347511L;
		private Dictionary dictElement;
		private HashMap<String, EntryType> entries;

		private DictionaryWrapper(File dictFile) {
			super(dictFile);
			this.dictElement = (Dictionary) getRootElement();
			init();
		}

		private DictionaryWrapper(InputStream stream) {
			super(stream);
			this.dictElement = (Dictionary) getRootElement();
			init();
		}

		private void init() {
			entries = new HashMap<String, EntryType>();
			// iterate over entries and fill wrapper
			for (EntryType entry : dictElement.getEntry()) {
				addEntry(entry);
			}
		}

		private void addEntry(EntryType entry) {
			entries.put(entry.getId(), entry);
		}

		@Override
		public EntryType getEntryById(String id) {
			return entries.get(id);
		}

		@Override
		public EntryType getEntryByQualifiedName(String dictRef) {
			String id = XmlHelper.getInstance().getSuffix(dictRef);
			return getEntryById(id);
		}

		@Override
		public int getSize() {
			return entries.size();
		}

		@Override
		public Iterator<EntryType> getIterator() {
			return entries.values().iterator();
		}

		@Override
		public Set<String> getEntryIDs() {
			return entries.keySet();
		}

		@Override
		public String getToolSuite() {
			return dictElement.getToolsuite();
		}

		@Override
		public String getNamespace() {
			return dictElement.getNamespace();
		}

		@Override
		public String getDictPrefix() {
			return dictElement.getDictionaryPrefix();
		}

		@Override
		public int hashCode() {
			if (getNamespace() == null)
				return super.hashCode();
			return getNamespace().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (obj.getClass() == this.getClass()) {
				DictionaryWrapper other = (DictionaryWrapper) obj;
				if (getNamespace().equals(other.getNamespace()))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return getToolSuite();
		}
	}

}
