//package de.mosgrid.ukoeln.templatedesigner.document;
//
//import java.beans.PropertyChangeListener;
//import java.beans.PropertyChangeSupport;
//
//import de.mosgrid.msml.jaxb.bindings.ParameterType;
//import de.mosgrid.msml.jaxb.bindings.PropertyType;
//import de.mosgrid.msml.util.IDictionary;
//import de.mosgrid.msml.util.helper.XmlHelper;
//
//public class PropertyBean_Save implements IPropertyBean {
//
//	public static final String REF_PROPID = "ref";
//	public static final String VAL_PROPID = "val";
//	public static final String EDIT_PROPID = "editable";
//	public static final String INDEX_PROPID = "index";
//	public static final String TITLE_PROPID = "title";
//	public static final String DICT_PROPID = "dict";
//
//	private int index;
//	private String ref;
//	private String val;
//	private Boolean editable;
//	private String title;
//	private IDictionary dict;
//
//	private final PropertyChangeSupport refChanged = new PropertyChangeSupport(this);
//	private final PropertyChangeSupport dictChanged = new PropertyChangeSupport(this);;
//
//	public void absorbProperty(PropertyType prop, TDTemplateJobDocument doc) {
//		setEditable(prop.isEditable());
//		setDict(doc.resolveDict(XmlHelper.getInstance().getPrefix(prop.getDictRef())));
//		setRef(XmlHelper.getInstance().getSuffix(prop.getDictRef()));
//		setTitle(prop.getTitle());
//		if (prop.getScalar() != null && prop.getScalar().getValue() != null)
//			setVal(prop.getScalar().getValue());
//	}
//
//	public void absorbParameter(ParameterType param, TDTemplateJobDocument doc) {
//		setEditable(param.isEditable());
//		setDict(doc.resolveDict(XmlHelper.getInstance().getPrefix(param.getDictRef())));
//		setRef(XmlHelper.getInstance().getSuffix(param.getDictRef()));
//		setTitle(param.getTitle());
//		if (param.getScalar() != null && param.getScalar().getValue() != null)
//			setVal(param.getScalar().getValue());
//	}
//
//	public String getRef() {
//		return ref;
//	}
//
//	public void setRef(String ref) {
//		String oldRef = this.ref;
//		this.ref = ref;
//		refChanged.firePropertyChange("ref", oldRef, ref);
//	}
//
//	public String getVal() {
//		return val;
//	}
//
//	public void setVal(String val) {
//		this.val = val;
//	}
//
//	public Boolean getEditable() {
//		return editable;
//	}
//
//	public void setEditable(Boolean editable) {
//		this.editable = editable;
//	}
//
//	public int getIndex() {
//		return index;
//	}
//
//	public void setIndex(int index) {
//		this.index = index;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public IDictionary getDict() {
//		return dict;
//	}
//
//	public void setDict(IDictionary dict) {
//		IDictionary oldDict = this.dict;
//		this.dict = dict;
//		dictChanged.firePropertyChange(DICT_PROPID, oldDict, dict);
//	}
//
//	public void addRefChangedListener(RefChangeListener listener) {
//		refChanged.addPropertyChangeListener(listener);
//	}
//	
//	public void addDictChangedListener(DictChangeListener listener) {
//		dictChanged.addPropertyChangeListener(listener);
//	}
//
//	public void removeRefChangedListeners() {
//		PropertyChangeListener[] listeners = refChanged.getPropertyChangeListeners();
//		for (int i = 0; i < listeners.length; i++) {
//			refChanged.removePropertyChangeListener(listeners[i]);
//		}
//	}
//	
//	public void removeDictChangedListeners() {
//		PropertyChangeListener[] listeners = dictChanged.getPropertyChangeListeners();
//		for (int i = 0; i < listeners.length; i++) {
//			dictChanged.removePropertyChangeListener(listeners[i]);
//		}
//	}
//
//	public interface RefChangeListener extends PropertyChangeListener {
//
//	}
//	
//	public interface DictChangeListener extends PropertyChangeListener {
//
//	}
//
//}
