package de.ukoeln.msml.genericparser.gui.interfaces.swingimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.ukoeln.msml.genericparser.gui.ValRetDelegate;

/**
 * This class should be the swing implementation of an table model used to store dictionary data.
 * 
 * TODO Better separation between data and model.
 * @author krm
 *
 */
public class DictTableModelSwingImpl extends AbstractTableModel implements ITableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum DictCols {
		DICTIONARY(0, DictRefData.class, true, new ValRetDelegate() {

			@Override
			public Object getVal(Object data) {
				return data;
			}
		}),

		PREFIX(1, String.class, new ValRetDelegate() {

			@Override
			public Object getVal(Object data) {
				return ((DictRefData)data).getPrefix();
			}
		}),
		
		NAMESPACE(2, String.class, new ValRetDelegate() {

			@Override
			public Object getVal(Object data) {
				return ((DictRefData)data).getNamespace();
			}
		});

		private int _index;
		private String _colName;
		private Class<?> _class;
		private ValRetDelegate _del;
		private boolean _isEditable;

		DictCols(int i, Class<?> colClass, ValRetDelegate del) {
			this(i, colClass, false, del);
		}

		DictCols(int i, Class<?> colClass, boolean isEditable, ValRetDelegate del) {
			_del = del;
			_index = i;
			_colName = name();
			_colName = _colName.substring(0, 1).toUpperCase() + _colName.substring(1).toLowerCase();
			_class = colClass;
			_isEditable = isEditable;
		}

		public int getIndex() {
			return _index;
		}

		public String getColName() {
			return _colName;
		}

		public Class<?> getColClass() {
			return _class;
		}

		public Object getValue(DictRefData data) {
			return _del.getVal(data);
		}
		
		public boolean isEditable() {
			return _isEditable;
		}
	}

	private static final String[] _cols = new String[DictCols.values().length];
	private static final HashMap<Integer, DictCols> _ind2col = new HashMap<Integer, DictTableModelSwingImpl.DictCols>();
	private final List<DictRefData> _data = new ArrayList<DictRefData>();
	private static DictRefData[] _dictData;

	static {
		for (DictCols v : DictCols.values()) {
			_cols[v.getIndex()] = v.getColName();
			_ind2col.put(v.getIndex(), v);
		}
	}

	public DictTableModelSwingImpl(Collection<IDictionary> dicts) {
		initDictData(dicts);
	}
	
	@Override
	public int getRowCount() {
		return _data.size();
	}

	private void initDictData(Collection<IDictionary> dicts) {
		DictRefData[] dictData = new DictRefData[dicts.size()];
		Iterator<IDictionary> iter = dicts.iterator();
		int counter = 0;
		while (iter.hasNext()) {
			dictData[counter++] = new DictRefData(iter.next());
		}
		_dictData = dictData;
	}

	@Override
	public int getColumnCount() {
		return _cols.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return _data.get(rowIndex).getValue(_ind2col.get(columnIndex));
	}

	@Override
	public String getColumnName(int column) {
		return _ind2col.get(column).getColName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return _ind2col.get(columnIndex).getColClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return _ind2col.get(columnIndex).isEditable();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex != DictCols.DICTIONARY.getIndex())
			return;
		DictRefData data = (DictRefData) aValue;
		_data.set(rowIndex, data);
		fireTableDataChanged();
	}
	
	public DictRefData[] getDictionaries() {
		return _dictData;
	}
	
	public void addData() {
		_data.add(_dictData[0]);
		fireTableRowsInserted(_data.size() - 1, _data.size() - 1);
	}
	
	public void removeData(int selectedRow) {
		if (selectedRow < 0)
			return;
		_data.remove(selectedRow);
		fireTableRowsDeleted(selectedRow, selectedRow);
	}
	
	public void removeAllData() {
		int size = _data.size();
		_data.clear();
		fireTableRowsDeleted(0, Math.max(0, size - 1));
	}
	
	public void addData(String dict) {
		DictRefData newData = getDictFromLoaded(dict);
		if (newData == null)
			return;
		
		_data.add(newData);
		fireTableRowsInserted(_data.size() - 1, _data.size() - 1);
	}
	
	private DictRefData getDictFromLoaded(String dict) {
		DictRefData newData = null;
		for (DictRefData data : _dictData) {
			if (data.equals(dict)) {
				newData = data;
				break;
			}
		}
		if (newData == null)
			System.out.println("dictionary " + dict + " not found.");
		return newData;
	}
	
	private DictRefData getDictFromActive(String dict) {
		DictRefData newData = null;
		for (DictRefData data : getActiveDicts()) {
			if (data.equals(dict)) {
				newData = data;
				break;
			}
		}
		if (newData == null)
			System.out.println("dictionary " + dict + " not found.");
		return newData;
	}

	public List<DictRefData> getActiveDicts() {
		return _data;
	}

	public String getPrefixToDict(String dict) {
		DictRefData data = getDictFromActive(dict);
		return data.getPrefix();
	}
	
	public IDictionary getIDictionaryToDict(String dict) {
		DictRefData data = getDictFromActive(dict);
		return data.getDictionary();
	}

	public void replacePrefixes(JobListEditor msmlEdit) {
		for (DictRefData data : getActiveDicts())
			data.setPref(msmlEdit);
	}
	
	public void restorePrefixes() {
		for (DictRefData data : getActiveDicts())
			data.restorePref();
	}
	
	public class DictRefData {

		private final IDictionary _dictionary;
		private final String _dict, _namespace;
		private final Set<String> _ids;
		private String _pref;

		public DictRefData(IDictionary dict) {
			_dict = dict.getToolSuite();
			_pref = dict.getDictPrefix();
			_namespace = dict.getNamespace();
			_ids = dict.getEntryIDs();
			_dictionary = dict;
		}

		public IDictionary getDictionary() {
			return _dictionary;
		}

		public Object getValue(DictCols dictCols) {
			return dictCols.getValue(this);
		}

		public String getNamespace() {
			return _namespace;
		}

		public String getPrefix() {
			return _pref;
		}

		public String getDict() {
			return _dict;
		}
		
		public void setPref(JobListEditor msmlEdit) {
			String pref = msmlEdit.getPrefixToNamespace(_namespace);
			if (pref == null) {
				IDictionary dict = DictionaryFactory.getInstance().getDictionary(_namespace);
				msmlEdit.addNamespace(dict.getDictPrefix(), dict.getNamespace());
				_pref = dict.getDictPrefix();
				return;
			}			
			_pref = pref;
		}
		
		/**
		 * Probably not needed.
		 */
		public void restorePref() {
			_pref = _dictionary.getDictPrefix();
		}

		@Override
		public String toString() {
			return getDict();
		}

		@Override
		public int hashCode() {
			return _dict.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj == this)
				return true;
			if (obj.getClass() == this.getClass()) {
				DictRefData other = (DictRefData) obj;
				if (getNamespace().equals(other.getNamespace()))
					return true;
			} else if (obj.getClass() == String.class) {
				if (getDict().equals(obj.toString()))
					return true;
			}
			return false;
		}

		public Set<String> getEntries() {
			return _ids;
		}
	}
}
