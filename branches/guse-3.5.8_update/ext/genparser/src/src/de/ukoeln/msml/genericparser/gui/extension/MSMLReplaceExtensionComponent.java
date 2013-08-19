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

public class MSMLReplaceExtensionComponent extends JPanel implements
		IMSMLParserComponent {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtPattern;
	private MSMLReplaceExtension _ext;
	private JTextField txtReplacement;
	
	public MSMLReplaceExtensionComponent(MSMLReplaceExtension ext) {
		this();
		_ext = ext;
	}
	
	public MSMLReplaceExtensionComponent() {
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
		
		JLabel lblPattern = new JLabel("Pattern:");
		GridBagConstraints gbc_lblPattern = new GridBagConstraints();
		gbc_lblPattern.anchor = GridBagConstraints.EAST;
		gbc_lblPattern.insets = new Insets(0, 5, 5, 5);
		gbc_lblPattern.gridx = 0;
		gbc_lblPattern.gridy = 0;
		add(lblPattern, gbc_lblPattern);
		
		txtPattern = new JTextField();
		txtPattern.setColumns(10);
		GridBagConstraints gbc_txtPattern = new GridBagConstraints();
		gbc_txtPattern.insets = new Insets(0, 0, 5, 0);
		gbc_txtPattern.fill = GridBagConstraints.BOTH;
		gbc_txtPattern.gridx = 1;
		gbc_txtPattern.gridy = 0;
		add(txtPattern, gbc_txtPattern);
		
		JLabel lblReplacement = new JLabel("Replacement:");
		GridBagConstraints gbc_lblReplacement = new GridBagConstraints();
		gbc_lblReplacement.anchor = GridBagConstraints.EAST;
		gbc_lblReplacement.insets = new Insets(0, 0, 0, 5);
		gbc_lblReplacement.gridx = 0;
		gbc_lblReplacement.gridy = 1;
		add(lblReplacement, gbc_lblReplacement);
		
		txtReplacement = new JTextField();
		GridBagConstraints gbc_txtReplacement = new GridBagConstraints();
		gbc_txtReplacement.fill = GridBagConstraints.BOTH;
		gbc_txtReplacement.gridx = 1;
		gbc_txtReplacement.gridy = 1;
		add(txtReplacement, gbc_txtReplacement);
		txtReplacement.setColumns(10);
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		txtPattern.setEnabled(enabled);
		txtReplacement.setEnabled(enabled);
	}

	public void updateValuesInExtension() {
		_ext.updateValues(txtPattern.getText(), txtReplacement.getText());
	}

	public void setValues(String separatorPattern, String replacement) {
		txtPattern.setText(separatorPattern);
		txtReplacement.setText(replacement);
	}

}
