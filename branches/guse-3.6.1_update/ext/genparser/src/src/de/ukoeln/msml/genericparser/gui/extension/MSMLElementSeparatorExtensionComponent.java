package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;

public class MSMLElementSeparatorExtensionComponent extends JPanel implements
		IMSMLParserComponent {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtSeparator;
	private JTextField txtNewSep;
	private MSMLElementSeparatorExtension _ext;
	
	public MSMLElementSeparatorExtensionComponent(MSMLElementSeparatorExtension ext) {
		this();
		_ext = ext;
	}
	
	public MSMLElementSeparatorExtensionComponent() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setMaximumSize(new Dimension(32767, 65));
		setMinimumSize(new Dimension(10, 65));
		setPreferredSize(new Dimension(350, 65));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{117, 0};
		gridBagLayout.rowHeights = new int[]{30, 30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblSeparator = new JLabel("Separator Pattern:");
		GridBagConstraints gbc_lblSeparator = new GridBagConstraints();
		gbc_lblSeparator.anchor = GridBagConstraints.WEST;
		gbc_lblSeparator.insets = new Insets(0, 5, 5, 5);
		gbc_lblSeparator.gridx = 0;
		gbc_lblSeparator.gridy = 0;
		add(lblSeparator, gbc_lblSeparator);
		
		txtSeparator = new JTextField();
		txtSeparator.setColumns(10);
		GridBagConstraints gbc_txtSeparator = new GridBagConstraints();
		gbc_txtSeparator.insets = new Insets(0, 0, 5, 0);
		gbc_txtSeparator.fill = GridBagConstraints.BOTH;
		gbc_txtSeparator.gridx = 1;
		gbc_txtSeparator.gridy = 0;
		add(txtSeparator, gbc_txtSeparator);
		
		JLabel lblNewSeparator = new JLabel("New Separator:");
		GridBagConstraints gbc_lblNewSeparator = new GridBagConstraints();
		gbc_lblNewSeparator.anchor = GridBagConstraints.EAST;
		gbc_lblNewSeparator.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewSeparator.gridx = 0;
		gbc_lblNewSeparator.gridy = 1;
		add(lblNewSeparator, gbc_lblNewSeparator);
		
		txtNewSep = new JTextField();
		txtNewSep.setColumns(10);
		GridBagConstraints gbc_txtNewSep = new GridBagConstraints();
		gbc_txtNewSep.fill = GridBagConstraints.BOTH;
		gbc_txtNewSep.gridx = 1;
		gbc_txtNewSep.gridy = 1;
		add(txtNewSep, gbc_txtNewSep);
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		txtSeparator.setEnabled(enabled);
		txtNewSep.setEnabled(enabled);
	}

	public void updateValuesInExtension() {
		_ext.updateValues(txtSeparator.getText(), txtNewSep.getText());
	}

	public void setValues(String separatorPattern, String newSep) {
		txtSeparator.setText(separatorPattern);
		txtNewSep.setText(newSep);
	}

}
