package de.mosgrid.msml.util.wrapper;

import java.util.ArrayList;
import java.util.List;

import de.mosgrid.msml.editors.MSMLEditor;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.ParameterListType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.helper.ParameterPropertyHelper;

public class ParameterListWrapper extends MSMLAddableAbstractWrapperBase {

	private ParameterListType _list;

	public ParameterListWrapper(MSMLEditor ed, ParameterListType list) {
		super(ed);
		_list = list;
	}

	protected ParameterListType getList() {
		return _list;
	}

	protected ParameterType getParameter(Namespaces namespace, String ref) {
		String dictRef = getDictRef(namespace, ref);
		for (ParameterType entry : _list.getParameter()) {
			if (entry.getDictRef() != null && dictRef.equals(entry.getDictRef()))
				return entry;
		}
		return null;
	}
	
	private List<ParameterType> getAllParameter(Namespaces namespace, String ref) {
		List<ParameterType> res = new ArrayList<ParameterType>();
		String dictRef = getDictRef(namespace, ref);
		for (ParameterType entry : _list.getParameter()) {
			if (entry.getDictRef() != null && dictRef.equals(entry.getDictRef()))
				res.add(entry);
		}
		return res;
	}

	protected void deleteParameter(Namespaces namespace, String ref) {
		ParameterType param = getParameter(namespace, ref);
		if (param == null)
			return;
		_list.getParameter().remove(param);
	}

	protected ParameterType addParameter(Namespaces namespace, String ref) {
		ParameterType param = new ParameterType();
		param.setDictRef(getDictRef(namespace, ref));
		_list.getParameter().add(param);
		return param;
	}

	protected void setScalarParameter(Namespaces namespace, String ref, String value) {
		if (value == null || "".equals(value)) {
			deleteParameter(namespace, ref);
			return;
		}
		ParameterType param = getParameter(namespace, ref);
		if (param == null)
			param = addParameter(namespace, ref);
		IDictionary dict = DictionaryFactory.getInstance().getDictionary(namespace.getNamespace());
		ParameterPropertyHelper.setScalar(getEditor(), param, dict, ref, value);
	}
	
	protected void addScalarParameter(Namespaces namespace, String ref, String value) {
		ParameterType param = addParameter(namespace, ref);
		IDictionary dict = DictionaryFactory.getInstance().getDictionary(namespace.getNamespace());
		ParameterPropertyHelper.setScalar(getEditor(), param, dict, ref, value);
	}
	
	protected String getScalarParameter(Namespaces namespace, String ref) {
		ParameterType param = getParameter(namespace, ref);
		if (param == null || param.getScalar() == null || param.getScalar().getValue() == null)
			return null;
		return param.getScalar().getValue();
	}
	
	protected List<String> getAllScalarParameter(Namespaces namespace, String ref) {
		List<String> filteredParam = new ArrayList<String>();
		List<ParameterType> params = getAllParameter(namespace, ref);
		for (ParameterType param : params) {
			if (param == null || param.getScalar() == null || param.getScalar().getValue() == null)
				continue;
			filteredParam.add(param.getScalar().getValue());		
		}
		return filteredParam;
	}

	private String getDictRef(Namespaces namespace, String ref) {
		String pref = getEditor().getPrefixToNamespace(namespace.getNamespace());
		String dictRef = pref + ":" + ref;
		return dictRef;
	}
	
	public boolean hasParameter() {
		return getList() != null && getList().getParameter().size() != 0;
	}

	@Override
	public Object getJaxBElement() {
		return _list;
	}
}
