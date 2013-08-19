package de.mosgrid.msml.util.wrapper;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.PropertyListType;
import de.mosgrid.msml.util.ObjectFactorySingelton;

/**
 * Wrapper for a module element carrying the dictRef="compchem:finalization"
 * attribute.
 * 
 * @author Andreas Zink
 * 
 */
public class JobFinalization extends AbstractWrapper {
	public static final String ELEMENT_ID = "finalization";

	private static ModuleTypeDefaultCreator _defaultCreator;

	private PropertyListWrapper propertyList;
	private MoleculeType molecule;

	static {
		_defaultCreator = new ModuleTypeDefaultCreator() {
			
			@Override
			public ModuleType create(MSMLEditor ed) {
				ModuleType module = ObjectFactorySingelton.getFactory().createModuleType();
				module.setDictRef(ed.getPrefixToNamespace(Namespaces.COMPCHEM.getNamespace()) + ":" + ELEMENT_ID);
				return module;
			}
		};
	}
	
	public JobFinalization(ModuleType moduleElement, MSMLEditor editor) {
		super(moduleElement, ELEMENT_ID, editor);
		init();
	}
	
	public JobFinalization(MSMLEditor ed) {
		this(_defaultCreator.create(ed), ed);
	}

	public void setMolecule(MoleculeType newMolecule) {
		getChildElements().add(newMolecule);
		molecule = newMolecule;
	}

	private void init() {
		for (Object child : getChildElements()) {
			if (child instanceof MoleculeType) {
				this.molecule = (MoleculeType) child;
			} else if (child instanceof PropertyListType) {
				this.propertyList = new PropertyListWrapper(getEditor(), (PropertyListType) child);
			}
		}
	}

	/**
	 * @return PropertyList of this element or null if not specified
	 */
	public PropertyListWrapper getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(PropertyListWrapper properties) {
		getChildElements().add(properties.getJaxBElement());
		propertyList = properties;
	}

	/**
	 * @return MoleculeType of this element or null if not specified
	 */
	public MoleculeType getMolecule() {
		return molecule;
	}
}
