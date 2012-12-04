package de.ukoeln.msml.genericparser.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import de.ukoeln.msml.genericparser.MSMLTreeValueBase;

public class MSMLObjectTreeCellRenderer extends DefaultTreeCellRenderer
				implements TreeCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2609079520223086471L;

	public MSMLObjectTreeCellRenderer() {
		super();
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component cmp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		MSMLTreeValueBase val = (MSMLTreeValueBase) ((DefaultMutableTreeNode)value).getUserObject();
		if (val.isEmpty())
			cmp.setForeground(Color.RED);
		else
			cmp.setForeground(Color.BLACK);
		return cmp;		
	}

}
