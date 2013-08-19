package de.mosgrid.msml.util.wrapper;

import java.util.List;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.jaxb.bindings.PropertyListType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.helper.ParameterPropertyHelper;

public class PropertyListWrapper extends MSMLAddableAbstractWrapperBase {

	private PropertyListType _list;

	public PropertyListWrapper(MSMLEditor ed, PropertyListType list) {
		super(ed);
		_list = list;
	}

	protected PropertyListType getList() {
		return _list;
	}

	private PropertyType getProperty(IDictionary dict, String ref) {
		String dictRef = getDictRef(dict, ref);
		for (PropertyType entry : _list.getProperty()) {
			if (entry.getDictRef() != null && dictRef.equals(entry.getDictRef()))
				return entry;
		}
		return null;
	}

	private void deleteProperty(IDictionary dict, String ref) {
		PropertyType param = getProperty(dict, ref);
		if (param == null)
			return;
		_list.getProperty().remove(param);
	}

	private PropertyType addProperty(IDictionary dict, String ref) {
		PropertyType param = new PropertyType();
		param.setDictRef(getDictRef(dict, ref));
		_list.getProperty().add(param);
		return param;
	}

	public void setScalarParameter(IDictionary dict, String ref, String value) {
		if (value == null || "".equals(value)) {
			deleteProperty(dict, ref);
			return;
		}
		PropertyType prop = getProperty(dict, ref);
		if (prop == null)
			prop = addProperty(dict, ref);
		ParameterPropertyHelper.setScalar(getEditor(), prop, dict, ref, value);
	}
	
	public String getScalarParameter(IDictionary dict, String ref) {
		PropertyType param = getProperty(dict, ref);
		if (param == null || param.getScalar() == null || param.getScalar().getValue() == null)
			return null;
		return param.getScalar().getValue();
	}

	private String getDictRef(IDictionary dict, String ref) {
		String pref = getEditor().getPrefixToNamespace(dict.getNamespace());
		String dictRef = pref + ":" + ref;
		return dictRef;
	}
	
	public boolean hasParameter() {
		return getList() != null && getList().getProperty().size() != 0;
	}

	@Override
	public Object getJaxBElement() {
		return _list;
	}

	public List<PropertyType> getProps() {
		return _list.getProperty();
	}
}
