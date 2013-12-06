package de.mosgrid.msml.tests;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.mosgrid.msml.jaxb.bindings.Dictionary;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.Restriction;

public class DictionaryParser {

	private static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";

	public static void main(String[] args) {

		URL dictURL = DictionaryParser.class.getClassLoader()
				.getResource("de/mosgrid/msml/examples/md-input-gromacs.cml");
		URL schemaURL = DictionaryParser.class.getClassLoader()
				.getResource("de/mosgrid/msml/examples/cml_clean_extend.xsd");

		try {
			File cmlFile = new File(dictURL.toURI());
			File schemaFile = new File(schemaURL.toURI());
			if (cmlFile.exists()) {
				JAXBContext jc = JAXBContext
						.newInstance("de.mosgrid.msml.jaxb.bindings");
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

				unmarshaller.setSchema(sf.newSchema(schemaFile));

				Dictionary dict = (Dictionary) unmarshaller.unmarshal(cmlFile);
				List<EntryType> entries = dict.getEntry();
				for (EntryType entry : entries) {
					System.out.println("id=" + entry.getId() + " term="
							+ entry.getTerm() + " dataType="
							+ entry.getDataType() + " unitType="
							+ entry.getUnitType());
					for (Object child : entry.getDescription().getAny()) {
						Element jaxbElem = (Element) child;
						System.out.println("\tDescription: "
								+ jaxbElem.getTextContent());
					}
					for (Object child : entry.getDefinition().getAny()) {
						System.out.println("\tDefinition: "
								+ ((Element) child).getTextContent());
					}
					for (Restriction restr : entry.getRestriction()) {
						System.out
								.println("\tRestriction: " + restr.getValue());
					}
				}
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
