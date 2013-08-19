package de.mosgrid.msml.tests;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.mosgrid.msml.jaxb.bindings.MoleculeType;

public class MoleculeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URL molFileURL = MoleculeTest.class.getClassLoader().getResource(
				"de/mosgrid/msml/examples/moleculetest.xml");
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("de.mosgrid.msml.jaxb.bindings");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			File molFile = new File(molFileURL.toURI());
			MoleculeType rootElement = (MoleculeType) unmarshaller
					.unmarshal(molFile);

			System.out.println("id: " + rootElement.getId());

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
