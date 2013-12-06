package de.mosgrid.msml.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.Method;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.Cml;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.helper.FileHelper;
import de.mosgrid.msml.util.helper.XmlHelper;

/**
 * Enables reading and writing of a MSML file. The given msml file is
 * unmarshalled automatically during class initialization. It can then be edited
 * using setters of the generated java binding instances. After editing the
 * instances have to be written back to a msml file with the "marshall" methods
 * of this class.
 * 
 * @author Andreas Zink
 * 
 */
public class MSMLEditor {
	private final Logger LOGGER = LoggerFactory.getLogger(MSMLEditor.class);

	protected JAXBContext jaxbContext;
	protected File msmlFile;
	protected Object rootElement;
	protected Hashtable<String, String> namespace2prefix;
	protected Hashtable<String, String> prefix2namespaces;

	public MSMLEditor(File msmlFile) {
		this.msmlFile = msmlFile;
		init();
	}

	/**
	 * This constructor is needed for GenericParser and should not be used in
	 * general. Instantiating an editor in that way offers limited functionality
	 * to namespaces etc..
	 * 
	 * @param cml
	 *            A cml-Object parsed by the MSMLGenericParser.
	 */
	public MSMLEditor(Cml cml) {
		rootElement = cml;
		initJAXB();
		prefix2namespaces = new Hashtable<String, String>();
		namespace2prefix = new Hashtable<String, String>();
		for (Namespaces ns : Namespaces.values()) {
			prefix2namespaces.put(ns.getDefaultPrefix(), ns.getNamespace());
			namespace2prefix.put(ns.getNamespace(), ns.getDefaultPrefix());
		}
		for (IDictionary dict : DictionaryFactory.getInstance().getAllDictionaries()) {
			prefix2namespaces.put(dict.getDictPrefix(), dict.getNamespace());
			namespace2prefix.put(dict.getNamespace(), dict.getDictPrefix());
		}
	}

	/**
	 * This constructor is needed for GenericParser and should not be used in
	 * general. Instantiating an editor in that way offers limited functionality
	 * to namespaces etc..
	 * 
	 * @param content
	 *            The content of a msml-File.
	 */
	public MSMLEditor(String content) {
		initJAXB();

		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			rootElement = unmarshaller.unmarshal(new StringReader(content));
		} catch (JAXBException e) {
			LOGGER.error("Error while reading MSML file! " + e.getMessage());
		}
		
		initMaps(content);
	}

	public MSMLEditor(InputStream stream) {
		initJAXB();

		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			rootElement = unmarshaller.unmarshal(stream);
		} catch (JAXBException e) {
			LOGGER.error("Error while reading MSML file! " + e.getMessage());
		}
	}

	private void init() {
		initJAXB();
		unmarshallTemplate();
		initMaps();
	}

	private void initJAXB() {
		try {
			// create jaxb context
			this.jaxbContext = JAXBContext.newInstance("de.mosgrid.msml.jaxb.bindings");
		} catch (JAXBException e) {
			LOGGER.error("Error while loading MSML file! " + e.getMessage(), e);
		}
	}

	private void initMaps() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(msmlFile);
			initMaps(fis);
		} catch (FileNotFoundException e) {
			LOGGER.error("MSML-File not found: " + e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error("Error parsing namespaces.", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error("Closing file failed: " + e.getMessage(), e);
				}
			}
		}
	}

	public String getPath() {
		if (msmlFile == null)
			return "";
		return msmlFile.getAbsolutePath();
	}

	/**
	 * @return The root element of this cml file
	 */
	public Object getRootElement() {
		return rootElement;
	}

	/**
	 * This method looks through the MSML-File to retrieve the prefix of a
	 * namespace. This is necessary to identify the dictionary prefixes used in
	 * dictRef attributes of the current MSML-File.
	 * 
	 * @param nameSpace
	 *            The namespace to look for in the MSML-File.
	 * @return This method returns the portion of text that lies between
	 *         "xmlns:" and the namespace name. If no prefix is defined for the
	 *         given namespace (default namespace is used) an empty string is
	 *         returned. If no namespace has been found or an error occured,
	 *         null is returned.
	 */
	public String getPrefixToNamespace(String nameSpace) {
		if (!namespace2prefix.containsKey(nameSpace))
			return null;
		return namespace2prefix.get(nameSpace);
	}

	/**
	 * This method looks through the MSML-File to retrieve the namespace of a
	 * given prefix. This is necessary to identify the dictionary that is used
	 * in templates by the prefix used in dictRef attributes.
	 * 
	 * @param prefix
	 *            The prefix to look for in the MSML-File. If you want to
	 *            retrieve the default namespace, an empty string should be
	 *            provided.
	 * @return This method returns the portion of text that lies between the
	 *         quotation marks of the namespace definition:
	 *         'xmlns(:PREFIX)="(NAMESPACE)"'. If no namespace has been found or
	 *         an error occured, null is returned.
	 */
	public String getNamespaceToPrefix(String prefix) {
		if (!prefix2namespaces.containsKey(prefix))
			return null;
		return prefix2namespaces.get(prefix);
	}

	public Set<String> getNamespaces() {
		// TODO: should use unmodif. set
		return namespace2prefix.keySet();
	}

	public boolean hasNamespace(String namespace) {
		return namespace2prefix.containsKey(namespace);
	}

	public void addNamespace(Namespaces ns) {
		addNamespace(ns.getDefaultPrefix(), ns.getNamespace());
	}

	public void addNamespace(String dictPrefix, String namespace) {
		if (hasNamespace(namespace))
			return;
		prefix2namespaces.put(dictPrefix, namespace);
		namespace2prefix.put(namespace, dictPrefix);
	}

	/**
	 * Creates a copy which is read from the same origin
	 */
	public MSMLEditor copy() {
		if (msmlFile != null) {
			return new MSMLEditor(msmlFile);
		} else if (rootElement != null && rootElement instanceof Cml) {
			return new MSMLEditor((Cml) rootElement);
		} else {
			throw new UnsupportedOperationException(
					"Can't create copy because instance was not created from File or Cml");
		}
	}

	private void unmarshallTemplate() {
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			// get root cml element
			rootElement = unmarshaller.unmarshal(msmlFile);
		} catch (JAXBException e) {
			LOGGER.error("Error while reading MSML file! " + e.getMessage(), e);
		}
	}

	/**
	 * Writes java bindings to given msml file which MUST NOT be the same file
	 * given in constructor.
	 * 
	 * @param outputFile
	 *            The msml file to which shall be written
	 *            
	 * @return True if save was successful, false otherwise.
	 */
	public boolean marshallTo(File outputFile) {
		if (outputFile == msmlFile) {
			throw new IllegalArgumentException("Cannot write to " + outputFile.getName()
					+ " because already given in constructor!");
		}
		return doMarshallToFile(outputFile);
	}

	/**
	 * Writes java bindings back to the msml file given in constructor.
	 * 
	 * @return True if save was successful, false otherwise.
	 */
	public boolean marshall() {
		if (msmlFile == null) {
			String msg = "Marshalling not allowed for editors constructed directly from cml.";
			LOGGER.error(msg);
			throw new UnsupportedOperationException(msg);

		}
		return doMarshallToFile(msmlFile);
	}

	/**
	 * This function marshals the current CML object to the specified output
	 * file. Since JAXB only keeps namespaces in the XML that are used by
	 * elements and attributes there has do be done some extra work to preserve
	 * dictionary-namespaces used by CML within IDs and such. The DOM serializer
	 * is used to rename elements and attributes and add the missing namespaces.
	 * 
	 * @param outputFile
	 *            The file where to put the generated and adjusted MSML.
	 * @return True if save was successful, false otherwise.
	 */
	private boolean doMarshallToFile(File outputFile) {
		try {
			// The JAXB-marshaller is used to fill the DOM document.
			// DOM document has not dictionary namespaces and n1, n2 as
			// prefixnames.
			// Maybe use NamespaceMapper for the latter one.
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(rootElement, outputFile);
			marshaller.marshal(rootElement, doc);

			// All remaining namespaces within the JAXB-document are going to be
			// replaced with the prefixes from the local hashmap => build
			// map mit prefixes to replace.
			NodeList nodes = doc.getChildNodes();
			Hashtable<String, String> renames = new Hashtable<String, String>();

			Element cmlElem = ((Element) nodes.item(0));
			NamedNodeMap attribs = cmlElem.getAttributes();
			for (int i = 0; i < attribs.getLength(); i++) {
				Attr attr = (Attr) attribs.item(i);
				if (!attr.getName().startsWith("xmlns"))
					continue;
				String prefix = XmlHelper.getInstance().getSuffix(attr.getName());
				if (prefix == null || "xmlns".equals(prefix)) // for namespace
																// definitions
																// the
																// getSuffix()
																// resolves to
																// xmlns
					prefix = "";
				if (!prefix.equals(getPrefixToNamespace(attr.getValue())))
					renames.put(prefix, getPrefixToNamespace(attr.getValue()));
			}

			for (String rename : renames.keySet()) {
				String oldName = "".equals(rename) ? "xmlns" : "xmlns:" + rename;
				String newName = "".equals(renames.get(rename)) ? "xmlns" : "xmlns:" + renames.get(rename);
				String namespace = getNamespaceToPrefix(renames.get(rename));
				cmlElem.removeAttribute(oldName);
				cmlElem.setAttribute(newName, namespace);
			}

			// Now set the new prefix on all elements recursively.
			renameElement(doc.getDocumentElement(), renames);

			// Add remaining namespaces.
			for (String ns : getNamespaces()) {
				String pref = getPrefixToNamespace(ns);
				if (renames.containsKey(pref))
					continue;
				String name = "".equals(pref) ? "xmlns" : "xmlns:" + pref;
				cmlElem.setAttribute(name, ns);
			}

			// Create file using pretty print.
			FileWriter fw = new FileWriter(outputFile);
			XMLSerializer serializer = new XMLSerializer(fw, new OutputFormat(Method.XML, "UTF-8", true));
			serializer.serialize(doc.getDocumentElement());
		} catch (JAXBException e) {
			LOGGER.error("Error while marshalling file! " + e.getMessage(), e);
			return false;
		} catch (ParserConfigurationException e) {
			LOGGER.error("Problem with parser configuration: " + e.getMessage(), e);
			return false;
		} catch (IOException e) {
			LOGGER.error("Could not write file: " + e.getMessage(), e);
			return false;
		}
		return true;
	}

	/**
	 * This function is used recursively to rename all elements under the
	 * specified element. The XML-tree is handled by a depth-first search
	 * starting on the leaves. When all childs of an element have been handled,
	 * then the elements prefix itself and its attributes' prefixes are changed
	 * accordingly to the renames-hashtable.
	 * 
	 * @param elem
	 *            The element to rename prefixes for.
	 * @param renames
	 *            A dictionary that maps old-prefixes (like ns1, ns2) to the
	 *            actual prefixes (like qc or cmlx).
	 */
	private void renameElement(Element elem, Hashtable<String, String> renames) {

		// Depth first search - recursion.
		NodeList childs = elem.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			if (!(childs.item(i) instanceof Element))
				continue;
			renameElement((Element) childs.item(i), renames);
		}
		String oldElemPref = elem.getPrefix();
		// No prefix is represented by an empty String here, but in DOM it is
		// null.
		if (oldElemPref == null)
			oldElemPref = "";

		// Rename attributes if necessary.
		NamedNodeMap attrs = elem.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);
			String oldPref = attr.getPrefix();
			// if no prefix has been set, then the namespace had
			// allready been changed by changing the elements namespace.
			// => continue
			if (oldPref == null || !renames.containsKey(oldPref))
				continue;

			String attrPrefix = "".equals(renames.get(oldPref)) ? null : renames.get(oldPref);
			attr.setPrefix(attrPrefix);
		}

		if (!renames.containsKey(oldElemPref))
			return;

		// Rename element's prefix.
		String prefix = "".equals(renames.get(oldElemPref)) ? null : renames.get(oldElemPref);
		elem.setPrefix(prefix);
	}

	public void initMaps(InputStream stream) {
		initMaps(FileHelper.getInstance().getConcatinatedLines(stream));
	}
	
	private void initMaps(String content) {
		// work on just a first part of the whole content... 2000 characters should be enough
		String firstPart = content.substring(0, Math.min(content.length(), 2000));
		prefix2namespaces = new Hashtable<String, String>();
		namespace2prefix = new Hashtable<String, String>();

		Pattern p = Pattern.compile("\\s*xmlns(:([^=]*))?=\"([^\"]*)");
		Matcher m = p.matcher(firstPart);
		while (m.find()) {
			String prefix = m.group(2);
			String namespace = m.group(3);
			if (prefix == null)
				prefix = "";
			prefix2namespaces.put(prefix, namespace);
			namespace2prefix.put(namespace, prefix);
		}
	}
}
