package de.mosgrid.msml.util.wrapper;

import java.util.ArrayList;
import java.util.List;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.jaxb.bindings.DescriptionType;
import de.mosgrid.msml.jaxb.bindings.JobInputUploadType;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.MoleculeUploadType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.ObjectFactorySingelton;
import de.mosgrid.msml.util.helper.XmlHelper;

/**
 * Wrapper for a module element carrying the dictRef="compchem:job" attribute.
 * 
 * @author Andreas Zink
 * 
 */
public class Job extends AbstractWrapper {
	public static final String ELEMENT_ID = "job";

	private JobInitialization initialization;
	private List<JobCalculation> calculations;
	private JobFinalization finalization;
	private JobEnvironment environment;
	private DescriptionType description;

	public Job(ModuleType moduleElement, MSMLEditor editor) {
		super(moduleElement, ELEMENT_ID, editor);
		init();
	}

	private void init() {
		ArrayList<JobCalculation> calcChilds = new ArrayList<JobCalculation>();
		// iterate over children
		for (Object child : getChildElements()) {
			// if child is a module element
			if (child instanceof ModuleType) {
				ModuleType module = (ModuleType) child;
				String dictRef = module.getDictRef();
				String elementId = XmlHelper.getInstance().getSuffix(dictRef);
				// check dictRef and wrap child module
				if (elementId.equals(JobInitialization.ELEMENT_ID)) {
					this.initialization = new JobInitialization(module, getEditor());
				} else if (elementId.equals(JobCalculation.ELEMENT_ID)) {
					calcChilds.add(new JobCalculation(module, getEditor()));
				} else if (elementId.equals(JobFinalization.ELEMENT_ID)) {
					this.finalization = new JobFinalization(module, getEditor());
				} else if (elementId.equals(JobEnvironment.ELEMENT_ID)) {
					this.environment = new JobEnvironment(module, getEditor());
				}
			} else if (child instanceof DescriptionType) {
				this.description = (DescriptionType) child;
			}
		}
		if (!calcChilds.isEmpty()) {
			this.calculations = calcChilds;
		}
	}

	/**
	 * @return The jobs initialization element. May return null!
	 */
	public JobInitialization getInitialization() {
		return initialization;
	}

	/**
	 * @return The jobs calculation elements. May return null!
	 */
	public List<JobCalculation> getCalculations() {
		return calculations;
	}

	/**
	 * @return The jobs finalization element. May return null!
	 */
	public JobFinalization getFinalization() {
		return finalization;
	}

	public void setFinalization(JobFinalization newFinal) {
		newFinal.addSelfToList(getChildElements());
		finalization = newFinal;
	}

	/**
	 * @return The jobs environment element. May return null!
	 */
	public JobEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * @return The jobs description element. May return null!
	 */
	public DescriptionType getDescription() {
		return description;
	}
	
	public void createDescription() {
		description = ObjectFactorySingelton.getFactory().createDescriptionType();
		getChildElements().add(description);
	}

	/**
	 * Checks whether this job has parameters or uploads which can be edited by the user
	 */
	public boolean needsUserInput() {
		// check init element
		if (initialization != null) {
			// check for uploads
			if (initialization.hasAnyUploads()) {
				return true;
			}
			// check for editable params
			if (initialization.getParamList() != null) {
				for (ParameterType parameter : initialization.getParamList().getParameter()) {
					if (parameter.isEditable() != null && parameter.isEditable()) {
						return true;
					}
				}
			}
		}
		// check environment element
		if (environment != null) {
			if (environment.getProperties() != null) {
				// check for editable properties
				for (PropertyType property : environment.getProperties().getProperty()) {
					if (property.isEditable() != null && property.isEditable()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public void setEnvironment(JobEnvironment jobEnvironment) {
		environment = jobEnvironment;
		jobEnvironment.addSelfToList(getChildElements());
	}

	public void setId(String id) {
		((ModuleType) getJaxBElement()).setId(id);
	}

	// -------------------------- INITIALIZATION -----------------------------

	public void initInitialization() {
		if (getInitialization() != null)
			return;
		ModuleType mod = new ModuleType();
		mod.setDictRef(getEditor().getPrefixToNamespace(Namespaces.COMPCHEM.getNamespace()) + ":"
				+ JobInitialization.ELEMENT_ID);
		JobInitialization init = new JobInitialization(mod, getEditor());
		init.addSelfToList(getChildElements());
		initialization = init;
	}

	public void removeInitialization() {
		if (getInitialization() == null)
			return;
		getChildElements().remove(initialization.getJaxBElement());
		initialization = null;
	}
	
	private boolean hasInitialization() {
		return getInitialization() != null;
	}
	
	// -------------------------- PARAMETER -----------------------------

	public void initParameter() {
		if (!hasParameter()) {
			initInitialization();
			getInitialization().initParameter();
		}
	}

	public void removeParameter() {
		if (hasParameter())
			getInitialization().removeParameter();
	}
	
	public boolean hasParameter() {
		return getInitialization() != null && getInitialization().hasParameterXML();
	}

	public boolean hasParameterElements() {
		return getInitialization() != null && getInitialization().hasParameterElements();
	}

	public ParameterType createAndAddParameter() {
		if (!hasInitialization())
			initInitialization();
		return getInitialization().createAndAddParameter();
	}

	// -------------------------- UPLOADS -----------------------------
	
	public void initUploads() {
		if (!hasParameter()) {
			initInitialization();
			getInitialization().initUploads();
		}
	}

	public void removeUploads() {
		if (hasUploads())
			getInitialization().removeUploads();
	}
	
	public boolean hasUploads() {
		return getInitialization() != null && getInitialization().hasUploadsXML();
	}

	public boolean hasUploadsElements() {
		return getInitialization() != null && getInitialization().hasUploadsElements();
	}

	public JobInputUploadType createAndAddInputUpload() {
		if (!hasInitialization())
			initInitialization();
		return getInitialization().createAndAddInputUpload();
	}

	public MoleculeUploadType createAndAddMoleculeUpload() {
		if (!hasInitialization())
			initInitialization();
		return getInitialization().createAndAddMoleculeUpload();
	}

	// -------------------------- ENVIRONMENT -----------------------------

	public void initEnvironment() {
		if (getEnvironment() != null)
			return;
		ModuleType mod = new ModuleType();
		mod.setDictRef(getEditor().getPrefixToNamespace(Namespaces.COMPCHEM.getNamespace()) + ":"
				+ JobEnvironment.ELEMENT_ID);
		JobEnvironment env = new JobEnvironment(mod, getEditor());
		env.addSelfToList(getChildElements());
		environment = env;
	}

	public void removeEnvironment() {
		if (getEnvironment() == null)
			return;
		getChildElements().remove(environment.getJaxBElement());
		environment = null;
	}

	public boolean hasEnvironment() {
		return getEnvironment() != null;
	}

	public boolean hasEnvironmentElements() {
		return hasEnvironment() && 
				getEnvironment().getProperties() != null &&
				getEnvironment().getProperties().getProperty().size() > 0;
	}

	public PropertyType createAndAddEnvProperty() {
		if (!hasEnvironment())
			initEnvironment();
		return getEnvironment().createAndAddProperty();
	}

	// -------------------------- ADAPTER -----------------------------

	public void initAdapterConfig() {
		if (!hasInitialization())
			initInitialization();
		getInitialization().initAdapterConfig();
	}

	public void removeAdapterConfig() {
		if (!hasInitialization())
			return;
		getInitialization().removeAdapterConfig();
	}

	public Boolean hasAdapter() {
		return hasInitialization() && getInitialization().hasAdapterXML();
	}

	public Boolean hasAdapterID() {
		return hasInitialization() && getInitialization().hasAdapterID();
	}

	public AdapterConfigType getAdapterConfig() {
		return getInitialization().getAdapterConfig();
	}

	public ParameterType createAndAddAdapParam() {
		if (!hasInitialization())
			initInitialization();
		return getInitialization().createAndAddAdapParam();
	}

	// -------------------------- PARSER -----------------------------

	public void initParserConfig() {
		if (!hasInitialization())
			initInitialization();
		getInitialization().initParserConfig();
	}
	
	public void removeParser() {
		if (!hasInitialization())
			return;
		getInitialization().removeParser();
	}

	public boolean hasParserConfig() {
		return getInitialization() != null && getInitialization().hasParserConfig();
	}

	@Override
	public String toString() {
		return getId();
	}
}
