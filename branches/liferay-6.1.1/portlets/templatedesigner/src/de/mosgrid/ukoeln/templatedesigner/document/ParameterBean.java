package de.mosgrid.ukoeln.templatedesigner.document;

import java.util.List;

import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.helper.ParameterPropertyHelper;
import de.mosgrid.msml.util.helper.XmlHelper;
import de.mosgrid.ukoeln.templatedesigner.helper.StringH;

public class ParameterBean extends AbstractPropertyBean {

	private final ParameterType _param;

	public ParameterBean(ParameterType param, TDTemplateJobDocument doc) {
		super(doc);
		_param = param;
	}

	@Override
	public String getRef() {
		return XmlHelper.getInstance().getSuffix(_param.getDictRef());
	}

	@Override
	protected void doSetRef(String suffix) {
		String prefix = XmlHelper.getInstance().getPrefix(_param.getDictRef());
//		if (StringH.isNullOrEmpty(suffix)) {
//			_param.setDictRef(null);
//		} else {
			if (StringH.isNullOrEmpty(prefix))
				_param.setDictRef(suffix);
			else
				_param.setDictRef(prefix + ":" + suffix);
//		}
	}

	@Override
	public IDictionary getDict() {
		if (StringH.isNullOrEmpty(_param.getDictRef()))
			return null;
		return getDoc().resolveDict(XmlHelper.getInstance().getPrefix(_param.getDictRef()));
	}

	@Override
	protected void doSetDict(IDictionary dict) {
		String prefix = null;
		if (dict != null) {
			if (!getDoc().getTemplate().hasNamespace(dict.getNamespace()))
				getDoc().getTemplate().addNamespace(dict.getDictPrefix(), dict.getNamespace());
			prefix = getDoc().getTemplate().getPrefixToNamespace(dict.getNamespace());
		}
		String suffix = XmlHelper.getInstance().getSuffix(_param.getDictRef());
		if (StringH.isNullOrEmpty(prefix))
			_param.setDictRef(suffix);
		else
			_param.setDictRef(prefix + ":" + suffix);
	}

	@Override
	public String getVal() {
		if (_param.getScalar() != null && _param.getScalar().getValue() != null)
			return _param.getScalar().getValue();
		else
			return null;
	}

	@Override
	public void setVal(String val) {
		ParameterPropertyHelper.setScalar(getDoc().getTemplate(), _param, getDict(), getRef(), val);
	}

	@Override
	public Boolean getEditable() {
		return _param.isEditable() != null && _param.isEditable();
	}

	@Override
	public void setEditable(Boolean editable) {
		_param.setEditable(editable);
	}

	@Override
	public String getTitle() {
		return _param.getTitle();
	}

	@Override
	public void setTitle(String title) {
		_param.setTitle(title);
	}

	@Override
	protected boolean isRefNull() {
		return StringH.isNullOrEmpty(getRef());
	}

	public void removeFromJob(List<ParameterType> list) {
		list.remove(_param);
	}
	
	public ParameterType getParam() {
		return _param;
	}
}
