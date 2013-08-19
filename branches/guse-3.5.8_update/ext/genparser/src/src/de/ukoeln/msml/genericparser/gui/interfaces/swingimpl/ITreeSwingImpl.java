package de.ukoeln.msml.genericparser.gui.interfaces.swingimpl;

import javax.swing.JTree;

import de.ukoeln.msml.genericparser.gui.interfaces.ITree;
import de.ukoeln.msml.genericparser.gui.interfaces.ITreeModel;

public class ITreeSwingImpl extends JTree implements ITree {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7850484240953623668L;

	public ITreeModel getIModel() {
		return (ITreeModel) getModel();
	}
}
