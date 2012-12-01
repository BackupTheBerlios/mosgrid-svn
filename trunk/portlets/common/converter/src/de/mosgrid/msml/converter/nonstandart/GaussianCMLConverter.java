package de.mosgrid.msml.converter.nonstandart;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mosgrid.msml.jaxb.bindings.AtomArrayType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.ObjectFactory;

/**
 * This class is able to convert from structure information used in gaussian
 * inputfiles to CML and vice versa.
 * 
 * @author krm
 *
 */
public class GaussianCMLConverter {
	
	private static final ObjectFactory fact = new ObjectFactory();
	private static final int ELEMGROUP = 1;
	private static final int XGROUP = 2;
	private static final int YGROUP = 4;
	private static final int ZGROUP = 6;
	
	private static final Pattern gaussianPattern;
	
	static {
		String numericFieldPattern = "-?\\d(\\.\\d*)?";
		String elementFieldPattern = "([a-zA-Z]+)\\d*";
		String pattern = elementFieldPattern + "\\s+(" +
				numericFieldPattern + ")\\s+(" + numericFieldPattern + ")\\s+(" +
				numericFieldPattern + ")\\s*";
		gaussianPattern = Pattern.compile(pattern);
	}
	
	public static MoleculeType gaussian2CML(String gaussian) {
		Matcher m = gaussianPattern.matcher(gaussian);
		if (!m.find())
			return null;

		MoleculeType res = new MoleculeType();
		MoleculeType mol = new MoleculeType();
		res.getMolecule().add(mol);
		AtomArrayType atomArray = fact.createAtomArrayType();
		mol.setAtomArray(atomArray);

		int counter = 1;
		do {
			System.out.println("Match; " + m.group(0));
			String elem = m.group(ELEMGROUP);
			Double x = Double.parseDouble(m.group(XGROUP));
			Double y = Double.parseDouble(m.group(YGROUP));
			Double z = Double.parseDouble(m.group(ZGROUP));
			atomArray.getAtomID().add("m1:" + counter++);
			atomArray.getElementType().add(elem);
			atomArray.getFormalCharge().add(BigInteger.valueOf(0));
			atomArray.getX3().add(x);
			atomArray.getY3().add(y);
			atomArray.getZ3().add(z);
		} while (m.find());
		
		return res;
	}
	
	public static String CML2Gaussian(MoleculeType molList) {
		MoleculeType mol = molList.getMolecule().get(0);
		AtomArrayType atoms = mol.getAtomArray();
		
		String res = "";
		for (int i = 0; i < atoms.getElementType().size(); i++) {
			res += atoms.getElementType().get(i) + "     " +
					atoms.getX3().get(i) + "     " +
					atoms.getY3().get(i) + "     " +
					atoms.getZ3().get(i) + "\n";
		}
		return res;
	}
}
