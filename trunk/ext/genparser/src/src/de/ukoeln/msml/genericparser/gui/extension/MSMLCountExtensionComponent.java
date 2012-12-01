package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;

public class MSMLCountExtensionComponent extends JPanel implements
		IMSMLParserComponent {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtCountPattern;
	private MSMLCountExtension _ext;
	
	public MSMLCountExtensionComponent(MSMLCountExtension ext) {
		this();
		_ext = ext;
	}
	
	public MSMLCountExtensionComponent() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setMaximumSize(new Dimension(32767, 35));
		setMinimumSize(new Dimension(10, 35));
		setPreferredSize(new Dimension(350, 35));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{117, 0};
		gridBagLayout.rowHeights = new int[]{30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblCountPattern = new JLabel("Count Pattern:");
		GridBagConstraints gbc_lblCountPattern = new GridBagConstraints();
		gbc_lblCountPattern.anchor = GridBagConstraints.WEST;
		gbc_lblCountPattern.insets = new Insets(0, 5, 0, 5);
		gbc_lblCountPattern.gridx = 0;
		gbc_lblCountPattern.gridy = 0;
		add(lblCountPattern, gbc_lblCountPattern);
		
		txtCountPattern = new JTextField();
		txtCountPattern.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateValuesInExtension();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateValuesInExtension();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateValuesInExtension();
			}
		});
		txtCountPattern.setColumns(10);
		GridBagConstraints gbc_txtCountPattern = new GridBagConstraints();
		gbc_txtCountPattern.fill = GridBagConstraints.BOTH;
		gbc_txtCountPattern.gridx = 1;
		gbc_txtCountPattern.gridy = 0;
		add(txtCountPattern, gbc_txtCountPattern);
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		txtCountPattern.setEnabled(enabled);
	}

	public void setValues(String countPattern) {
		txtCountPattern.setText(countPattern);
	}

	@Override
	public void updateValuesInExtension() {
		_ext.updateValues(txtCountPattern.getText());
	}
}
