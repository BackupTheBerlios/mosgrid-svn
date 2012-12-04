package de.ukoeln.msml.genericparser.gui.extension;

import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.converter.mol.MOL2MSMLConverter;
import de.mosgrid.msml.converter.pdb.PDB2MSMLConverter;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.MoleculeExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.MSMLMoleculeExtensionComponent.ParseMethod;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLMoleculeExtension extends MSMLParserExtensionBase {

	private static Marshaller _marshaller;
	private ParseMethod _parseMethod;
	private static final String PARSEMETHODCONFIG = "parsemethod";

	static {
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance("de.mosgrid.msml.jaxb.bindings");
			_marshaller = jaxbContext.createMarshaller();
			_marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);   		
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public MSMLMoleculeExtension(MSMLExtensionHelper helper) {
		super(helper);
	}

	@Override
	public CLASSTYPE getClassInfoTypeToApply() {
		return CLASSTYPE.CUSTOM;
	}

	@Override
	public float getWeight() {
		return 4000;
	}

	@Override
	public HashMap<String, String> getConfig() {
		HashMap<String, String> conf = new HashMap<String, String>();

		updateValues();

		if (_parseMethod != ParseMethod.NONE) {
			conf.put("version", "0.1");
			conf.put(PARSEMETHODCONFIG, _parseMethod.name());
		}
		return conf;
	}

	private void updateValues() {
		MSMLMoleculeExtensionComponent comp = getCastComponent();
		comp.updateValuesInExtension();
	}

	public void updateValues(ParseMethod method) {
		_parseMethod = method;
	}

	@Override
	public void loadConfig(HashMap<String, String> config) {
		HashMap<String, String> conf = (HashMap<String, String>) config;
		if (conf.containsKey("version") && conf.get("version").equals("0.1")) {
			_parseMethod = ParseMethod.valueOf(conf.get(PARSEMETHODCONFIG));
		} else {
			_parseMethod = ParseMethod.NONE;
		}

		updateValuesInComponent();
	}

	@Override
	protected void doInitGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	protected IMSMLParserComponent doGetComponent() {
		return new MSMLMoleculeExtensionComponent(this);
	}

	private MSMLMoleculeExtensionComponent getCastComponent() {
		return (MSMLMoleculeExtensionComponent) getComponent();
	}

	@Override
	public boolean isEmpty(GenericParserConfig config) {
		HashMap<String,String> conf = ConfigHelper.getExtensionConfigAsHashMap(config, this);
		if (conf == null || conf.size() == 0 || !conf.containsKey("version") || !conf.get("version").equals("0.1"))
			return true;

		String parsemethod = conf.get(PARSEMETHODCONFIG);
		
		if (StringH.isNullOrEmpty(parsemethod))
			return true;
		return false;
	}

	@Override
	public void handleExtensionVisitor(HashMap<String, String> conf,
			IMSMLExtensionVisitor visit) {
		ParseMethod parseMethod = Enum.valueOf(ParseMethod.class, conf.get(PARSEMETHODCONFIG));
		if (conf.containsKey("version") && conf.get("version").equals("0.1") && 
				parseMethod != ParseMethod.NONE && parseMethod != ParseMethod.UNKNOWN) {
			MoleculeExtensionVisitor realVis = (MoleculeExtensionVisitor) visit;
			
			MoleculeType parsed = null;
			try {
				switch (parseMethod) {
					case PDB:
						PDB2MSMLConverter pdb2msml = new PDB2MSMLConverter();
						parsed = pdb2msml.convertFromString(realVis.getText());
						break;
					case SDF:
						MOL2MSMLConverter mol2msml = new MOL2MSMLConverter();
						parsed = mol2msml.convertFromString(realVis.getText());
						break;
//					case GAUSSIAN:
//						parsed = parseText(realVis.getText(), new GaussReader());
//						break;
//					case CML:
//						parsed = parseText(realVis.getText(), new CMLLongReader());
//						break;
					default:
						parsed = null;
						break;
				}

			} catch (MSMLConversionException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			if (parsed == null) {
				realVis.setText("");
				return;
			}

			StringWriter writer = new StringWriter();
			try {
				_marshaller.marshal(parsed, writer);
			} catch (JAXBException e) {
				// fallback to another marshall method
				JAXBElement<MoleculeType> jaxbObj = new JAXBElement<MoleculeType>(
						new QName("http://www.xml-cml.org/schema","molecule"), MoleculeType.class, parsed);
				try {
					_marshaller.marshal(jaxbObj, writer);
				} catch (JAXBException e1) {
					StackTraceHelper.handleException(e, ON_EXCEPTION.CONTINUE, "Could not parse transformed CML");
				}
			}
			
			realVis.setText(writer.toString());
		}
	}
//	
//	private MoleculeType parseText(String text, ReaderIF reader) {
//		StringReader sr = new StringReader(text);
//		BufferedReader r = new BufferedReader(sr);
//		
//		try {
//			return reader.toCML("dummyFile", r);
//		} catch (IOException e) {
//		}
//		
//		return null;
//	}

	@Override
	protected void doHandleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
	}

	@Override
	public boolean shouldBeVisibleToValue(MSMLTreeValueBase value) {		
		return ((MSMLSimpleTreeValue) value).getSelectedLayerIndex() == -1; 
	}

	@Override
	protected void doUpdateValuesInComponent() {
		getCastComponent().setValues(_parseMethod);
	}

	@Override
	protected void doClear() {
		_parseMethod = ParseMethod.UNKNOWN;
	}
}
