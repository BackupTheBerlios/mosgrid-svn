package de.mosgrid.ukoeln.templatedesigner.document;

public class IndexedPropertyBean implements IIndexedPropertyBean {

	public static final String INDEX_PROPID = "index";
	private int _index;
	
	public int getIndex() {
		return _index;
	}

	public void setIndex(int index) {
		_index = index;
	}
}
