package de.ukoeln.msml.genericparser.gui.interfaces;

import javax.swing.tree.TreePath;

public interface ITree {

	void validate();

	void repaint();

	void expandPath(TreePath path);

	void setSelectionPath(TreePath path);

	void scrollPathToVisible(TreePath path);

	ITreeModel getIModel();

}
