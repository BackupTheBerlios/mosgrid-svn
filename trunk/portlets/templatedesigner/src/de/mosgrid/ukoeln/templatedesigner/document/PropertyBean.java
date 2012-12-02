package de.mosgrid.ukoeln.templatedesigner.document;

import java.util.List;

import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.helper.ParameterPropertyHelper;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.mosgrid.ukoeln.templatedesigner.helper.StringH;

public class PropertyBean extends AbstractPropertyBean {

	private final PropertyType _prop;

	public PropertyBean(PropertyType prop, TDTemplateJobDocument doc) {
		super(doc);
		_prop = prop;
	}

	@Override
	public String getRef() {
		return XmlHelper.getInstance().getSuffix(_prop.getDictRef());
	}

	@Override
	protected void doSetRef(String suffix) {
		String prefix = XmlHelper.getInstance().getPrefix(_prop.getDictRef());
//		if (StringH.isNullOrEmpty(suffix)) {
//			_prop.setDictRef(null);
//		} else {
			if (StringH.isNullOrEmpty(prefix))
				_prop.setDictRef(suffix);
			else
				_prop.setDictRef(prefix + ":" + suffix);			
//		}
	}

	@Override
	public IDictionary getDict() {
		if (StringH.isNullOrEmpty(_prop.getDictRef()))
			return null;
		return getDoc().resolveDict(XmlHelper.getInstance().getPrefix(_prop.getDictRef()));
	}

	@Override
	protected void doSetDict(IDictionary dict) {
		String prefix = null;
		if (dict != null) {
			if (!getDoc().getTemplate().hasNamespace(dict.getNamespace()))
				getDoc().getTemplate().addNamespace(dict.getDictPrefix(), dict.getNamespace());
			prefix = getDoc().getTemplate().getPrefixToNamespace(dict.getNamespace());
		}
		
		String suffix = XmlHelper.getInstance().getSuffix(_prop.getDictRef());
		if (StringH.isNullOrEmpty(prefix))
			_prop.setDictRef(suffix);
		else
			_prop.setDictRef(prefix + ":" + suffix);
	}

	@Override
	public String getVal() {
		if (_prop.getScalar() != null && _prop.getScalar().getValue() != null)
			return _prop.getScalar().getValue();
		else
			return null;
	}

	@Override
	public void setVal(String val) {
		ParameterPropertyHelper.setScalar(getDoc().getTemplate(), _prop, getDict(), getRef(), val);
	}

	@Override
	public Boolean getEditable() {
		return _prop.isEditable() != null && _prop.isEditable();
	}

	@Override
	public void setEditable(Boolean editable) {
		_prop.setEditable(editable);
	}
	
	public void removeFromJob(List<PropertyType> list) {
		list.remove(_prop);
	}

	@Override
	public String getTitle() {
		return _prop.getTitle();
	}

	@Override
	public void setTitle(String title) {
		_prop.setTitle(title);
	}
	
	@Override
	protected boolean isRefNull() {
		return StringH.isNullOrEmpty(getRef());
	}
}
