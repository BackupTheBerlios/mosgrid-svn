package de.ukoeln.msml.genericparser.classes;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.ClassInfo.ClassInfoFactory;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.MoleculeExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.MSMLMoleculeExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtensionTextBased;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StringH;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public class MoleculeClassInfo extends MSMLCustomClassInfo<MoleculeExtensionVisitor, MoleculeType> {

	private static final String SPIN = "Spin";
	private static final String FORMAL_CHARGE = "Formal Charge";
	private static final String ID = "Id";
	private static final String TITLE = "Title";
	private static Unmarshaller _marshaller;

	static {
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance("de.mosgrid.msml.jaxb.bindings");
			_marshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public MoleculeClassInfo() {
		super("Molecule", MoleculeType.class);
	}

	@Override
	public void init(MSMLSimpleTreeValue res) {
		super.init(res);
		res.createAndAddOneNode(ClassInfoFactory.getClassInfo(String.class), TITLE, true, false);
		res.createAndAddOneNode(ClassInfoFactory.getClassInfo(String.class), ID, true, false);
		res.createAndAddOneNode(ClassInfoFactory.getClassInfo(Integer.class), FORMAL_CHARGE, true, false);
		res.createAndAddOneNode(ClassInfoFactory.getClassInfo(Integer.class), SPIN, true, false);
	}

	@Override
	protected void addExtensions(List<Class<? extends IMSMLParserExtension>> list) {
		super.addExtensions(list);
		list.add(MSMLMoleculeExtension.class);
	}

	@Override
	public MoleculeExtensionVisitor getVisitorInstance(MSMLTreeValueBase val) {
		return new MoleculeExtensionVisitor(val);
	}

	@Override
	public MoleculeExtensionVisitor runVisitor(GenericParserDocumentBase doc, MSMLTreeValueBase val) {
		MoleculeExtensionVisitor res = super.runVisitor(doc, val);
		val.setText(res.getText());
		try {
			for (MSMLTreeValueBase child : val.getChilds()) {
				MSMLExtensionVisitor visit = new MSMLExtensionVisitor(child);
				for (IMSMLParserExtension ext : doc.getExtensionsToVal(child)) {
					HashMap<String, String> conf = ConfigHelper.getExtensionConfigAsHashMap(child.getConfig(), ext);
					if (conf.containsKey("version")) {
						((IMSMLParserExtensionTextBased) ext).getTextRetriever(conf.get("version")).getTextToConfig(
								visit, conf);
					}
				}

				String propname = child.getPropName();
				if (propname.equals(TITLE)) {
					res.setTitle(visit.getText());
				} else if (propname.equals(ID)) {
					res.setID(visit.getText());
				} else if (propname.equals(SPIN)) {
					res.setSpin(visit.getText());
				} else if (propname.equals(FORMAL_CHARGE)) {
					res.setCharge(visit.getText());
				} else {
					throw new UnsupportedOperationException(propname + " not found for moleculeclassinfo.");
				}
			}

			return res;
		} finally {
			val.setText("");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void absorbVisitor(MoleculeExtensionVisitor res) {
		initObject();
		
		String text = res.getText();
		text = text.replaceAll("<\\?xml[^>]*>", "");
		StringReader r = new StringReader(text);
		MoleculeType parsed = null;
		try {
			Object obj = _marshaller.unmarshal(r);
			if (obj instanceof JAXBElement) {
				JAXBElement<MoleculeType> jaxbMol = (JAXBElement<MoleculeType>) obj;
				parsed = jaxbMol.getValue();
			} else if (obj instanceof MoleculeType) {
				parsed = (MoleculeType) obj;
			}
			if (parsed == null)
				throw new UnsupportedOperationException("New way to handle molecule jaxb needed.");
		} catch (JAXBException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.CONTINUE, "Could not unmarshall molecule in textform.");
		}

		// no molecule found
		if (parsed == null)
			return;
		
		// many molecules
		if (parsed.getMolecule() != null && parsed.getMolecule().size() > 0) {
			if (parsed.getMolecule().size() == 1) {
				setMolecule(res, parsed.getMolecule().get(0), null);				
			} else {
				int counter = 1;
				for (MoleculeType mol : parsed.getMolecule()) {
					setMolecule(res, mol, counter++);
				}				
			}
		} else { // single moleculed parsed
			setMolecule(res, parsed, null);
		}
	}

	private void setMolecule(MoleculeExtensionVisitor res, MoleculeType mol, Integer index) {
		if (mol == null || ((mol.getAtomArray() == null || mol.getAtomArray().getAtomID() == null
				|| mol.getAtomArray().getAtom().size() == 0) && (mol.getMolecule() == null || mol.getMolecule().size() == 0)))
			return;

		MoleculeType m = getObject();
		
		// Title
		if (StringH.isNullOrEmpty(mol.getTitle())) {
			String title = res.getTitle();
			if (StringH.isNullOrEmpty(title))
				title = "Molecule";
			if (index != null)
				title += index;
			mol.setTitle(title);			
		}
		
		// ID
		if (StringH.isNullOrEmpty(mol.getId())) {
			String id = res.getId();
			if (StringH.isNullOrEmpty(id))
				id = "MoleculeID";
			if (index != null)
				id += index;
			mol.setId(id);			
		}
		
		if (res.getCharge() != null)
			mol.setFormalCharge(res.getCharge());
		if (res.getSpin() != null)
			mol.setSpinMultiplicity(res.getSpin());
		mol.setConvention("convention:molecular");
		
		m.getMolecule().add(mol);
	}

	@Override
	public void buildUp(MSMLSimpleTreeValue val, GenericParserConfig conf) {
		for (GenericParserConfig cconf : conf.getChildConfig()) {
			for (MSMLTreeValueBase child : val.getChilds()) {
				if (child.getPropName().equals(cconf.getPropertyName()))
					child.setConfig(cconf.getExtensionConfigCollection());
			}
		}
	}
}
