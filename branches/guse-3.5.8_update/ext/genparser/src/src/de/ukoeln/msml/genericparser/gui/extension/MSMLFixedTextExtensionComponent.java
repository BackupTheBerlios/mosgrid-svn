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

public class MSMLFixedTextExtensionComponent extends JPanel implements
		IMSMLParserComponent {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtFixedText;
	private MSMLFixedTextExtension _ext;
	
	public MSMLFixedTextExtensionComponent(MSMLFixedTextExtension ext) {
		this();
		_ext = ext;
	}
	
	public MSMLFixedTextExtensionComponent() {
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
		
		JLabel lblFixedText = new JLabel("Fixed Text:");
		GridBagConstraints gbc_lblFixedText = new GridBagConstraints();
		gbc_lblFixedText.anchor = GridBagConstraints.WEST;
		gbc_lblFixedText.insets = new Insets(0, 5, 0, 5);
		gbc_lblFixedText.gridx = 0;
		gbc_lblFixedText.gridy = 0;
		add(lblFixedText, gbc_lblFixedText);
		
		txtFixedText = new JTextField();
		txtFixedText.getDocument().addDocumentListener(new DocumentListener() {
			
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
		txtFixedText.setColumns(10);
		GridBagConstraints gbc_txtFixedText = new GridBagConstraints();
		gbc_txtFixedText.fill = GridBagConstraints.BOTH;
		gbc_txtFixedText.gridx = 1;
		gbc_txtFixedText.gridy = 0;
		add(txtFixedText, gbc_txtFixedText);
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		txtFixedText.setEnabled(enabled);
	}

	public void updateValuesInExtension() {
		_ext.updateValues(txtFixedText.getText());
	}

	public void setValues(String separatorPattern) {
		txtFixedText.setText(separatorPattern);
	}

}
