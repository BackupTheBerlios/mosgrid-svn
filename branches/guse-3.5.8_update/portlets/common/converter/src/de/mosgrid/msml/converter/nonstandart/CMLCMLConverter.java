package de.mosgrid.msml.converter.nonstandart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import de.mosgrid.msml.jaxb.bindings.AtomArrayType;
import de.mosgrid.msml.jaxb.bindings.AtomType;
import de.mosgrid.msml.jaxb.bindings.BondArrayType;
import de.mosgrid.msml.jaxb.bindings.BondType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.ObjectFactory;

/**
 * This class can be used to convert CML in long format to the short format.
 * Long format refers to using one entry in an atom array per atom.
 * Short format means packing all atoms as array into one string.
 * 
 * This class does not handle file-information for MSML like the other converters.
 * 
 * @author krm
 *
 */
public class CMLCMLConverter {

	private static Unmarshaller _unmarshaller = null;
	private static ObjectFactory _fact = null;
	private static Marshaller _marshaller;
	
	static {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("de.mosgrid.msml.jaxb.bindings");
			_unmarshaller = jaxbContext.createUnmarshaller();
			_marshaller = jaxbContext.createMarshaller();
			_marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		_fact = new ObjectFactory();
	}

	public static MoleculeType cmlLong2CmlShort(File info) {
		try {
			return cmlLong2CmlShort(new FileInputStream(info));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static MoleculeType cmlLong2CmlShort(InputStream info) {
		InputStreamReader isr = new InputStreamReader(info);
		BufferedReader br = new BufferedReader(isr);
		return cmlLong2CmlShort(br);
	}
	
	public static MoleculeType cmlLong2CmlShort(BufferedReader info) {
		try {
			String line = info.readLine(), total = "";
			do {
				total += line + "\n";
				line = info.readLine();
			} while (line != null);
			return cmlLong2CmlShort(total);
		} catch (IOException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static MoleculeType cmlLong2CmlShort(String info) {
		StringReader sr = new StringReader(info);
		try {
			MoleculeType m = null;
			Object obj = _unmarshaller.unmarshal(sr);
			if (obj instanceof JAXBElement) {
				JAXBElement<MoleculeType> r = (JAXBElement<MoleculeType>) obj;
				m = r.getValue();
			} else if (obj instanceof MoleculeType) {
				m = (MoleculeType) obj;
			}
			if (m == null)
				throw new UnsupportedOperationException("New way to handle molecule jaxb needed.");
			return cmlLong2CmlShort(m);
		} catch (JAXBException e) {
			return null;
		}
	}

	/**
	 * This function is able to convert MSML molecules from long format to short format. It is restricted
	 * to one molecule only or a root-molecule with only one child-element.
	 * 
	 * @param info The MSML molecule in long format.
	 * @return Returns an MSML molecule in short format. Returns null if molecule does not have the
	 * correct format.
	 */
	public static MoleculeType cmlLong2CmlShort(MoleculeType info) {
		if (info == null)
			return null;

		MoleculeType res = new MoleculeType();

		if (info.getAtomArray() == null && info.getMolecule().size() == 0)
			return null;
		else if (info.getMolecule().size() == 1)
			info = info.getMolecule().get(0);
		
		AtomArrayType aa = _fact.createAtomArrayType();
		res.setAtomArray(aa);
		for (AtomType a : info.getAtomArray().getAtom()) {
			aa.getAtomID().add(a.getId());
			aa.getFormalCharge().add(a.getFormalCharge() == null ? new BigInteger("0") : a.getFormalCharge());
			aa.getElementType().add(a.getElementType());
			aa.getX3().add(Double.parseDouble(a.getX3().toString()));
			aa.getY3().add(Double.parseDouble(a.getY3().toString()));
			aa.getZ3().add(Double.parseDouble(a.getZ3().toString()));
		}

		int counter = 1;
		BondArrayType ba = _fact.createBondArrayType(); 
		res.setBondArray(ba);
		for (BondType b : info.getBondArray().getBond()) {
			ba.getAtomRef1().add(b.getAtomRefs2().get(0));
			ba.getAtomRef2().add(b.getAtomRefs2().get(1));
			ba.getBondID().add(Integer.toString(counter++));
			ba.getOrder().add(convertOrder(b.getOrder()));
		}
		res.setTitle(info.getTitle());
		res.setChirality(info.getChirality());
		res.setFileRef(info.getFileRef());
		res.setFormalCharge(info.getFormalCharge());
		res.setFormula(info.getFormula());
		res.setSpinMultiplicity(info.getSpinMultiplicity());
		return res;
	}

	private static String convertOrder(String order) {
		Integer iord;
		try {
			iord = Integer.parseInt(order);
		} catch(Exception e) {
			return order;
		}
		switch (iord) {
			case 1: return "S";
			case 2: return "D";
			case 3: return "T";
			case 4: return "Q";
			default: return "A"; // not sure if one should do that as A cannot be modeled with numbers
		}
	}

	public static String molecule2String(MoleculeType info) {
		StringWriter sw = new StringWriter();
		try {
			_marshaller.marshal(info, sw);
		} catch (JAXBException e) {
			return "Error marshalling molecule.";
		}
		return sw.toString();
	}
}
