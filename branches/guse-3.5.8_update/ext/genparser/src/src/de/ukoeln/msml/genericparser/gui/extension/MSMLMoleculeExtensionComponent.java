package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;

public class MSMLMoleculeExtensionComponent extends JPanel implements
		IMSMLParserComponent {
	
	public enum ParseMethod {
		SDF, PDB, UNKNOWN, 
		//GAUSSIAN, CML, 
		NONE;
	}

	private static final long serialVersionUID = 1L;
	private MSMLMoleculeExtension _ext;
	private ButtonGroup _btnGroup;
	private Dictionary<ParseMethod, JRadioButton> _method2Button = 
			new Hashtable<ParseMethod, JRadioButton>();
	private Dictionary<ButtonModel, ParseMethod> _model2Method = 
			new Hashtable<ButtonModel, ParseMethod>();
	
	public MSMLMoleculeExtensionComponent(MSMLMoleculeExtension ext) {
		this();
		
		_btnGroup = new ButtonGroup();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0};

		int rows = (int) Math.ceil((ParseMethod.values().length - 1)/ 2.0d);
		gridBagLayout.rowHeights = new int[rows];
		gridBagLayout.rowWeights = new double[rows];
		for (int i = 0; i < rows; i++) {
			gridBagLayout.rowHeights[i] = 30;
			gridBagLayout.rowWeights[i] = 0;
		}
		setLayout(gridBagLayout);
		
		int pos = 0;
		for (ParseMethod val : ParseMethod.values()) {
			if (val.equals(ParseMethod.UNKNOWN))
				continue;
			JRadioButton rb = new JRadioButton(val.name());
			_method2Button.put(val, rb);
			_model2Method.put(rb.getModel(), val);
			
			rb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					selectionChanged();
				}
			});
			GridBagConstraints gbcRB = new GridBagConstraints();
			gbcRB.insets = new Insets(0, 0, 5, 5);
			gbcRB.gridx = pos % 2;
			gbcRB.gridy = pos / 2;
			gbcRB.anchor = GridBagConstraints.LINE_START;
			add(rb, gbcRB);
			_btnGroup.add(rb);
			pos++;
		}
		this.setPreferredSize(new Dimension(0, rows * (30 + 5 + 5)));
		_ext = ext;
	}
	
	public MSMLMoleculeExtensionComponent() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setPreferredSize(new Dimension(350, 65));
	}
//
//	@Override
//	public void clear() {
//		_btnGroup.clearSelection();
//	}
//	
	private void selectionChanged() {
		updateValuesInExtension();
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		Enumeration<AbstractButton> iter = _btnGroup.getElements();
		while (iter.hasMoreElements()) {
			AbstractButton but = iter.nextElement();
			but.setEnabled(enabled);
		}
	}

	public void updateValuesInExtension() {
		if (_btnGroup.getSelection() == null) {
			_ext.updateValues(ParseMethod.NONE);
			return;
		}
		_ext.updateValues(_model2Method.get(_btnGroup.getSelection()));
	}

	public void setValues(ParseMethod parseMethod) {
		if (parseMethod == ParseMethod.UNKNOWN) {
			_btnGroup.clearSelection();
			return;
		}	
		_method2Button.get(parseMethod).setSelected(true);
	}
}
