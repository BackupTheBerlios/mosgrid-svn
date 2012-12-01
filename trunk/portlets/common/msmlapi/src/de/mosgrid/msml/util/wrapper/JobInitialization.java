package de.mosgrid.msml.util.wrapper;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.jaxb.bindings.JobInputUploadType;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.MoleculeUploadType;
import de.mosgrid.msml.jaxb.bindings.ParameterListType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.ParserConfigType;
import de.mosgrid.msml.jaxb.bindings.UploadList;

/**
 * Wrapper for a module element carrying the dictRef="compchem:initialization" attribute.
 * 
 * @author Andreas Zink
 * 
 */
public class JobInitialization extends AbstractWrapper {
	public static final String ELEMENT_ID = "initialization";

	private ParameterListType paramList;
	private UploadList uploadList;
	private MoleculeType molecule;

	private AdapterConfigType adapterConfig;

	private JobParserConfig parserConfig;

	public JobInitialization(ModuleType moduleElement, MSMLEditor editor) {
		super(moduleElement, ELEMENT_ID, editor);
		init();
	}

	private void init() {
		for (Object child : getChildElements()) {
			if (child instanceof AdapterConfigType) {
				adapterConfig = (AdapterConfigType) child;
			} else if (child instanceof ParserConfigType) {
				parserConfig = new JobParserConfig(getEditor(), (ParserConfigType) child);
			} else if (child instanceof ParameterListType) {
				this.paramList = (ParameterListType) child;
			} else if (child instanceof MoleculeType) {
				this.molecule = (MoleculeType) child;
			} else if (child instanceof UploadList) {
				this.uploadList = (UploadList) child;
			}
		}
	}

	/**
	 * @return ParameterList of this element or null if not specified
	 */
	public ParameterListType getParamList() {
		return paramList;
	}

	/**
	 * @return UploadList of this element or null if not specified
	 */
	public UploadList getUploadList() {
		return uploadList;
	}

	/**
	 * @return 'true' if there are any upload elements for this job
	 */
	public boolean hasAnyUploads() {
		if (uploadList != null) {
			if (uploadList.getJobInputUpload() != null && uploadList.getJobInputUpload().size() > 0) {
				return true;
			}
			if (uploadList.getMoleculeUpload() != null && uploadList.getMoleculeUpload().size() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Molecule of this element or null if not specified
	 */
	public MoleculeType getMolecule() {
		return molecule;
	}

	/**
	 * This method returns the adapterConfig-Block of the given initialization block.
	 * 
	 * @return Returns an AdapterConfigType-Object or null if the adapterconfig is not present.
	 */
	public AdapterConfigType getAdapterConfig() {
		return adapterConfig;
	}

	/**
	 * This method returns the parserconfig-Block of the given initialization block.
	 * 
	 * @return Returns an ParserConfigType-Object or null if the parserconfig is not present.
	 */
	public JobParserConfig getParserConfig() {
		return parserConfig;
	}

	public void setMolecule(MoleculeType m) {
		molecule = m;
		getChildElements().add(molecule);
	}

	// -------------------------- PARAMETER -----------------------------
	
	public void initParameter() {
		if (hasParameterXML())
			return;
		ParameterListType list = new ParameterListType();
		setParamterList(list);
	}

	public void removeParameter() {
		if (hasParameterXML())
			getChildElements().remove(paramList);
		paramList = null;
	}

	public boolean hasParameterElements() {
		return hasParameterXML() && getParamList().getParameter().size() > 0;
	}

	public boolean hasParameterXML() {
		return getParamList() != null;
	}

	public ParameterType createAndAddParameter() {
		initParameter();
		ParameterType param = new ParameterType();
		getParamList().getParameter().add(param);
		return param;
	}
	
	private void setParamterList(ParameterListType list) {
		paramList = list;
		getChildElements().add(list);
	}

	// -------------------------- UPLOADS -----------------------------

	
	public void initUploads() {
		if (hasUploadsXML())
			return;
		UploadList list = new UploadList();
		setUploadList(list);
	}

	public void removeUploads() {
		if (hasUploadsXML())
			getChildElements().remove(uploadList);
		uploadList = null;
	}

	public boolean hasUploadsElements() {
		return hasUploadsXML() && getUploadList().getJobInputUpload().size() > 0;
	}

	public boolean hasUploadsXML() {
		return getUploadList() != null;
	}

	public JobInputUploadType createAndAddInputUpload() {
		initUploads();
		JobInputUploadType param = new JobInputUploadType();
		getUploadList().getJobInputUpload().add(param);
		return param;
	}
	
	public MoleculeUploadType createAndAddMoleculeUpload() {
		initUploads();
		MoleculeUploadType param = new MoleculeUploadType();
		getUploadList().getMoleculeUpload().add(param);
		return param;
	}
	
	private void setUploadList(UploadList list) {
		uploadList = list;
		getChildElements().add(list);
	}
	// -------------------------- ADAPTER -----------------------------

	public void initAdapterConfig() {
		if (hasAdapterXML())
			return;
		AdapterConfigType conf = new AdapterConfigType();
		setAdapterConfig(conf);
	}
	
	public void removeAdapterConfig() {
		if (!hasAdapterXML())
			return;
		getChildElements().remove(adapterConfig);
		adapterConfig = null;
	}

	public boolean hasAdapterXML() {
		 return getAdapterConfig() != null;
	}

	public boolean hasAdapterID() {
		return hasAdapterXML() && getAdapterConfig().getAdapterID() != null
				&& !"".equals(getAdapterConfig().getAdapterID());

	}

	public ParameterType createAndAddAdapParam() {
		initAdapterConfig();
		ParameterType param = new ParameterType();
		getAdapterConfig().getParameter().add(param);
		return param;
	}

	private void setAdapterConfig(AdapterConfigType conf) {
		adapterConfig = conf;
		getChildElements().add(conf);
	}
	
	// -------------------------- PARSER -----------------------------

	public void initParserConfig() {
		if (hasParserXML())
			return;
		ParserConfigType jaxbconf = new ParserConfigType();
		JobParserConfig conf = new JobParserConfig(getEditor(), jaxbconf);
		setParserConfig(conf);
	}

	public void removeParser() {
		if (!hasParserXML())
			return;
		getChildElements().remove(parserConfig.getJaxBElement());
		parserConfig = null;
	}

	public boolean hasParserXML() {
		return getParserConfig() != null;
	}

	public boolean hasParserConfig() {
		return hasParserXML() && getParserConfig().hasParameter();
	}

	private void setParserConfig(JobParserConfig conf) {
		parserConfig = conf;
		getChildElements().add(conf.getJaxBElement());
	}
}
