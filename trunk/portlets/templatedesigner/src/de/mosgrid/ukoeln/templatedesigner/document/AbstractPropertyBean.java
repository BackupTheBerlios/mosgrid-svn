package de.mosgrid.ukoeln.templatedesigner.document;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.mosgrid.msml.util.IDictionary;

public abstract class AbstractPropertyBean extends IndexedPropertyBean implements IPropertyBean {

	public static final String REF_PROPID = "ref";
	public static final String VAL_PROPID = "val";
	public static final String EDIT_PROPID = "editable";
	public static final String TITLE_PROPID = "title";
	public static final String DICT_PROPID = "dict";

	private final PropertyChangeSupport refChanged = new PropertyChangeSupport(this);
	private final PropertyChangeSupport dictChanged = new PropertyChangeSupport(this);
	private final TDTemplateJobDocument _doc;

	public AbstractPropertyBean(TDTemplateJobDocument doc) {
		_doc = doc;
	}

	protected TDTemplateJobDocument getDoc() {
		return _doc;
	}

	public final void setRef(String ref) {
		String oldRef = getRef();
		doSetRef(ref);
		refChanged.firePropertyChange("ref", oldRef, ref);
	}

	protected abstract void doSetRef(String ref);

	public void setDict(IDictionary dict) {
		IDictionary oldDict = getDict();
		doSetDict(dict);
		if (isRefNull() && dict.getEntryIDs().size() > 0)
			doSetRef(dict.getIterator().next().getId());
		dictChanged.firePropertyChange(DICT_PROPID, oldDict, dict);
	}

	protected abstract void doSetDict(IDictionary dict);

	protected abstract boolean isRefNull();

	public void addRefChangedListener(RefChangeListener listener) {
		refChanged.addPropertyChangeListener(listener);
	}

	public void addDictChangedListener(DictChangeListener listener) {
		dictChanged.addPropertyChangeListener(listener);
	}

	public void removeRefChangedListeners() {
		PropertyChangeListener[] listeners = refChanged.getPropertyChangeListeners();
		for (int i = 0; i < listeners.length; i++) {
			refChanged.removePropertyChangeListener(listeners[i]);
		}
	}

	public void removeDictChangedListeners() {
		PropertyChangeListener[] listeners = dictChanged.getPropertyChangeListeners();
		for (int i = 0; i < listeners.length; i++) {
			dictChanged.removePropertyChangeListener(listeners[i]);
		}
	}
	
	public enum ENTRYTYPE {
		DATATYPE, UNITS;
	}
}
