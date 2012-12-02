package de.mosgrid.ukoeln.templatedesigner.document;

import java.beans.PropertyChangeListener;

import de.mosgrid.msml.util.IDictionary;

public interface IPropertyBean extends IIndexedPropertyBean {

	public String getRef();
	public void setRef(String ref);
	public String getVal();
	public void setVal(String val);
	public Boolean getEditable();
	public void setEditable(Boolean editable);
	public String getTitle();
	public void setTitle(String title);
	public IDictionary getDict();
	public void setDict(IDictionary dict);

	public void addRefChangedListener(RefChangeListener listener);
	public void addDictChangedListener(DictChangeListener listener);
	public void removeRefChangedListeners();
	public void removeDictChangedListeners();
	
	public interface RefChangeListener extends PropertyChangeListener {

	}

	public interface DictChangeListener extends PropertyChangeListener {

	}
}
