package de.ukoeln.msml.genericparser.worker;

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class DoForAllChildrenDelegate {

	private  DefaultMutableTreeNode _node;

	public DoForAllChildrenDelegate(DefaultMutableTreeNode _node) {
		super();
		this._node = _node;
	}
	
	public DefaultMutableTreeNode getNode() {
		return _node;
	}

	public abstract boolean doAndContinue(DefaultMutableTreeNode child);
}
