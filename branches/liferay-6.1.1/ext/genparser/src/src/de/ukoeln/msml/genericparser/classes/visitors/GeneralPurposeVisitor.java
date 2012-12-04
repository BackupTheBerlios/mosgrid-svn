package de.ukoeln.msml.genericparser.classes.visitors;

import de.ukoeln.msml.genericparser.MSMLSimpleTreeRootValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.ArrayPropertyClassInfo;
import de.ukoeln.msml.genericparser.gui.extension.MSMLListExtension.ListMode;

public class GeneralPurposeVisitor {
	
	// TODO replace MSMLTreeValue with a more restricting interface.
	private MSMLTreeValueBase _val;
	private GeneralPuroposeVistorMode _mode;
	private ListMode _oldListMode;
	private ListMode _newListMode = ListMode.NoList;
	private boolean _canAddChild = false;
	
	public GeneralPurposeVisitor(MSMLTreeValueBase val, GeneralPuroposeVistorMode mode) {
		_val = val;
		_mode = mode;
	}

	public MSMLTreeValueBase getValue() {
		return _val;
	}

	public GeneralPuroposeVistorMode getMode() {
		return _mode;
	}
	
	public ListMode getOldListMode() {
		return _oldListMode;
	}

	public void setOldListMode(ListMode _listMode) {
		this._oldListMode = _listMode;
	}
	
	public ListMode getNewListMode() {
		return _newListMode;
	}

	public void setNewListMode(ListMode _listMode) {
		this._newListMode = _listMode;
	}
	
	public boolean getIsArray() {
		return _val.getInfo() instanceof ArrayPropertyClassInfo;
	}

	public void setCanAddChild(boolean canAdd) {
		_canAddChild = canAdd;
	}
	
	public boolean getCanAddChild() {
		return _canAddChild;
	}
	
	public boolean getAddButtonEnabled() {
		return getNewListMode() == ListMode.Fixed;
	}
	
	public boolean getCanAddLayer() {
		return getIsArray();
	}

	public boolean getSimpleRemoveElementEnabled() {
		return !(_val instanceof MSMLSimpleTreeRootValue);
	}

	public boolean getCanAddMolecule() {
		return _val instanceof MSMLSimpleTreeRootValue;
	}

	public boolean getCanAddScalar() {
		return _val instanceof MSMLSimpleTreeRootValue;
	}

	public boolean getCanAddArray() {
		return _val instanceof MSMLSimpleTreeRootValue;
	}
}
