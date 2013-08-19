package de.mosgrid.msml.tests;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.jaxb.bindings.Cml;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.ParameterListType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.ParserConfigType;
import de.mosgrid.msml.jaxb.bindings.ScalarType;

public class Unmarshal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URL cmlFileURL = Unmarshal.class.getClassLoader().getResource(
				"de/mosgrid/msml/examples/joblist_test.msml");
		// URL schemaURL = Unmarshal.class.getClass().getClassLoader()
		// .getResource("de/mosgrid/msml/examples/cml_clean_extend.xsd");

		try {
			File cmlFile = new File(cmlFileURL.toURI());
			if (cmlFile.exists()) {
				JAXBContext jc = JAXBContext
						.newInstance("de.mosgrid.msml.jaxb.bindings");
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				Cml cmlInstance = (Cml) unmarshaller.unmarshal(cmlFile);
				for (ModuleType module : cmlInstance.getModule()) {
					printModuleTree(module);
				}
			}
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	public static void printModuleTree(ModuleType module) {
		System.out.println(module.getDictRef());
		for (Object child : module.getParserConfigurationAndAdapterConfigurationAndUploadList()) {
			if (child instanceof ModuleType) {
				ModuleType childModule = (ModuleType) child;
				printModuleTree(childModule);
			} else if (child instanceof AdapterConfigType) {
				ParameterListType params = (ParameterListType) child;
				System.out.println("\tAdapterConfigType:");
				for (ParameterType param : params.getParameter()) {
					ScalarType scalar = (ScalarType) param.getScalar();
					System.out.println("\t\tParameter " + param.getDictRef()
							+ " " + scalar.getValue() + " editable="
							+ param.isEditable());

				}
			} else if (child instanceof ParserConfigType) {
				ParameterListType params = (ParserConfigType) child;
				System.out.println("\tParserConfig:");
				for (ParameterType param : params.getParameter()) {
					ScalarType scalar = (ScalarType) param.getScalar();
					System.out.println("\t\tParameter " + param.getDictRef()
							+ " " + scalar.getValue() + " editable="
							+ param.isEditable());

				}
			} else if (child instanceof ParameterListType) {
				ParameterListType params = (ParameterListType) child;
				System.out.println("\tParameterList:");
				for (ParameterType param : params.getParameter()) {
					ScalarType scalar = (ScalarType) param.getScalar();
					System.out.println("\t\tParameter " + param.getDictRef()
							+ " " + scalar.getValue() + " editable="
							+ param.isEditable());

				}
			}
		}

	}
}