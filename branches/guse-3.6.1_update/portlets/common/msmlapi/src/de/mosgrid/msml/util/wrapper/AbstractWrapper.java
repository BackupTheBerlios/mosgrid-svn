package de.mosgrid.msml.util.wrapper;

import java.util.List;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.util.helper.XmlHelper;

/**
 * Abstract wrapper for module elements following the CompChem convention.
 * Checks if given ModuleType has correct dictRef attribute.
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AbstractWrapper extends MSMLAddableAbstractWrapperBase {
	private final ModuleType rootElement;
	
	/**
	 * @param moduleElement
	 *            The module element to be wrapped
	 * @param elementId
	 *            The unqualified element name in the compchem dict
	 */
	public AbstractWrapper(ModuleType moduleElement, String elementId, MSMLEditor ed) {
		super(ed);
		this.rootElement = moduleElement;

		// validate dictRef attribute
		String moduleDictRef = moduleElement.getDictRef();
		String unqualifiedName = XmlHelper.getInstance().getSuffix(moduleDictRef);
		if (!(unqualifiedName.equals(elementId))) {
			throw new IllegalArgumentException(
					"Given module has illegal attribute dictRef=\""
							+ moduleDictRef + "\". Only " + elementId
							+ " is allowed.");
		}
	}

	/**
	 * @return The title attribute of this module element
	 */
	public String getTitle() {
		return rootElement.getTitle();
	}

	/**
	 * @return The id attribute of this module element
	 */
	public String getId() {
		return rootElement.getId();
	}

	/**
	 * @return A list of child elements of this module element. These can be of
	 *         type ParameterList, PropertyList, Module, AtomArray or Molecule.
	 */
	public List<Object> getChildElements() {
		return rootElement.getParserConfigurationAndAdapterConfigurationAndUploadList();
	}

	@Override
	public Object getJaxBElement() {
		return getModuleElement();
	}
	
	ModuleType getModuleElement() {
		return rootElement;
	}
}
