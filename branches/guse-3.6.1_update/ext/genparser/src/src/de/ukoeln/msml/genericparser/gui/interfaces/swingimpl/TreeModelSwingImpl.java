package de.ukoeln.msml.genericparser.gui.interfaces.swingimpl;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import de.ukoeln.msml.genericparser.gui.interfaces.ITreeModel;

public class TreeModelSwingImpl extends DefaultTreeModel implements ITreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3977278835460587L;

	public TreeModelSwingImpl(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}

	public TreeModelSwingImpl(TreeNode root) {
		super(root);
	}	
}
