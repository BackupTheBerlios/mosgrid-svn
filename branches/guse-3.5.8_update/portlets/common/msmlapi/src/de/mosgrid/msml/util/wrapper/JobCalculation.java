package de.mosgrid.msml.util.wrapper;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.ParameterListType;
import de.mosgrid.msml.jaxb.bindings.ParserConfigType;
import de.mosgrid.msml.jaxb.bindings.PropertyListType;

/**
 * Wrapper for a module element carrying the dictRef="compchem:calculation" attribute.
 * 
 * @author Andreas Zink
 * 
 */
public class JobCalculation extends AbstractWrapper {
	public static final String ELEMENT_ID = "calculation";

	private ParameterListType paramList;
	private PropertyListType propertyList;
	private MoleculeType molecule;

	public JobCalculation(ModuleType moduleElement, MSMLEditor editor) {
		super(moduleElement, ELEMENT_ID, editor);
		init();
	}

	private void init() {
		for (Object child : getChildElements()) {
			// this should never happen as adapterconfig and parserconfig
			// are only allowed in initblock.
			if (child instanceof AdapterConfigType || child instanceof ParserConfigType)
				continue;
			if (child instanceof ParameterListType) {
				this.paramList = (ParameterListType) child;
			} else if (child instanceof MoleculeType) {
				this.molecule = (MoleculeType) child;
			} else if (child instanceof PropertyListType) {
				this.propertyList = (PropertyListType) child;
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
	 * @return PropertyList of this element or null if not specified
	 */
	public PropertyListType getPropertyList() {
		return propertyList;
	}

	/**
	 * @return MoleculeType of this element or null if not specified
	 */
	public MoleculeType getMoleculeType() {
		return molecule;
	}

}
