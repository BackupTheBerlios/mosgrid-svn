package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import de.ukoeln.msml.genericparser.gui.extension.MSMLListExtension.ListMode;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;

public class MSMLListExtensionComponent extends JPanel 
implements IMSMLParserComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MSMLListExtension _ext;
	private boolean inStateChanged;
	private JCheckBox _chkFixed;
	private JCheckBox _chkDynamic;

	public MSMLListExtensionComponent(MSMLListExtension ext) {
		this();
		_ext = ext;
	}

	public MSMLListExtensionComponent() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setMaximumSize(new Dimension(32767, 65));
		setMinimumSize(new Dimension(10, 65));
		setPreferredSize(new Dimension(350, 65));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{117, 0};
		gridBagLayout.rowHeights = new int[]{30, 30, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		_chkFixed = new JCheckBox("Fixed List");
		_chkFixed.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (inStateChanged)
					return;
				ListMode oldMode = _chkDynamic.isSelected() ? ListMode.Dynamic : ListMode.Unset;
				inStateChanged = true;
				try {
					if (_chkFixed.isSelected()) {
						_chkDynamic.setSelected(false);
					}
				} finally {
					inStateChanged = false;
				}

				_ext.setMode(getMode());
				_ext.onListModeChanged(oldMode);
			}
		});
		GridBagConstraints gbc_chkFixed = new GridBagConstraints();
		gbc_chkFixed.anchor = GridBagConstraints.WEST;
		gbc_chkFixed.insets = new Insets(0, 1, 5, 0);
		gbc_chkFixed.gridx = 0;
		gbc_chkFixed.gridy = 0;
		add(_chkFixed, gbc_chkFixed);

		_chkDynamic = new JCheckBox("Dynamic List");
		_chkDynamic.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (inStateChanged)
					return;
				ListMode oldMode = _chkFixed.isSelected() ? ListMode.Fixed : ListMode.Unset;
				inStateChanged = true;
				try {
					if (_chkDynamic.isSelected()) {
						_chkFixed.setSelected(false);
					}
				} finally {
					inStateChanged = false;
				}

				_ext.setMode(getMode());
				_ext.onListModeChanged(oldMode);
			}
		});
		GridBagConstraints gbc__chkDynamic = new GridBagConstraints();
		gbc__chkDynamic.fill = GridBagConstraints.VERTICAL;
		gbc__chkDynamic.anchor = GridBagConstraints.WEST;
		gbc__chkDynamic.gridx = 0;
		gbc__chkDynamic.gridy = 1;
		add(_chkDynamic, gbc__chkDynamic);
	}
//
//	@Override
//	public void clear() {
//		inStateChanged = true;
//		try {
//			_chkDynamic.setSelected(false);
//			_chkFixed.setSelected(false);			
//		} finally {
//			inStateChanged = false;
//		}
//	}

	public MSMLListExtension.ListMode getMode() {
		if (_chkDynamic.isSelected())
			return MSMLListExtension.ListMode.Dynamic;
		else if (_chkFixed.isSelected())
			return MSMLListExtension.ListMode.Fixed;
		return ListMode.Unset;
	}


	public void setMode(ListMode mode) {
		inStateChanged = true;
		try {
			if (mode == ListMode.Unset) {
				_chkDynamic.setSelected(false);
				_chkFixed.setSelected(false);
			} else if (mode == ListMode.Dynamic) {
				_chkDynamic.setSelected(true);
				_chkFixed.setSelected(false);				
			} else if (mode == ListMode.Fixed) {
				_chkDynamic.setSelected(false);
				_chkFixed.setSelected(true);				
			}
		} finally {
			inStateChanged = false;
		}
	}

	public void enableDynamic(boolean enabled) {
		_chkDynamic.setEnabled(enabled);
	}

	
	@Override
	public void updateValuesInExtension() {
		_ext.updateValues();
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		// Done elsewhere
	}
	
	public void setValues(ListMode mode) {
		setMode(mode);
	}
}
