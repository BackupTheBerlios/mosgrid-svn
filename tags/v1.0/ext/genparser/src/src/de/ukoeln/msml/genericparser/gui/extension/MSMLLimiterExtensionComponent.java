package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLLimiterExtensionComponent extends JPanel 
	implements IMSMLParserComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MSMLLimiterExtension _ext;
	private JLabel lblNewLabel;
	private JFormattedTextField txtFromLine;
	private JTextField txtFromRegex;
	private JFormattedTextField txtToLine;
	private JTextField txtToRegex;
	private JCheckBox chkLines;
	private JToggleButton tglUseLast;

	public MSMLLimiterExtensionComponent(MSMLLimiterExtension ext) {
		this();
		_ext = ext;
		NumberFormatter nmbrFormatter = new NumberFormatter();
		nmbrFormatter.setAllowsInvalid(false);
		nmbrFormatter.setCommitsOnValidEdit(false);
		nmbrFormatter.setMaximum(Integer.MAX_VALUE);
		nmbrFormatter.setMinimum(0);
		nmbrFormatter.setOverwriteMode(false);
		nmbrFormatter.setValueClass(Integer.class);
		nmbrFormatter.setFormat(NumberFormat.getIntegerInstance(Locale.GERMAN));
		nmbrFormatter.install(txtFromLine);
		nmbrFormatter.install(txtToLine);
	}
	
	public MSMLLimiterExtensionComponent() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setMaximumSize(new Dimension(32767, 190));
		setMinimumSize(new Dimension(10, 190));
		setPreferredSize(new Dimension(350, 190));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{94, 0, 0};
		gridBagLayout.rowHeights = new int[]{35, 30, 30, 30, 30, 30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblFromRegex = new JLabel("From regex:");
		lblFromRegex.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblFromRegex = new GridBagConstraints();
		gbc_lblFromRegex.anchor = GridBagConstraints.EAST;
		gbc_lblFromRegex.fill = GridBagConstraints.VERTICAL;
		gbc_lblFromRegex.insets = new Insets(5, 0, 5, 5);
		gbc_lblFromRegex.gridx = 0;
		gbc_lblFromRegex.gridy = 0;
		add(lblFromRegex, gbc_lblFromRegex);
		
		txtFromRegex = new JTextField();
		txtFromRegex.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				fromRegexChanged();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				fromRegexChanged();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				fromRegexChanged();
			}
		});

		GridBagConstraints gbc_txtFromRegex = new GridBagConstraints();
		gbc_txtFromRegex.fill = GridBagConstraints.BOTH;
		gbc_txtFromRegex.insets = new Insets(5, 5, 5, 0);
		gbc_txtFromRegex.gridx = 1;
		gbc_txtFromRegex.gridy = 0;
		add(txtFromRegex, gbc_txtFromRegex);
		
		JLabel lblToRegex = new JLabel("To regex:");
		lblToRegex.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblToRegex = new GridBagConstraints();
		gbc_lblToRegex.fill = GridBagConstraints.VERTICAL;
		gbc_lblToRegex.anchor = GridBagConstraints.EAST;
		gbc_lblToRegex.insets = new Insets(0, 0, 5, 5);
		gbc_lblToRegex.gridx = 0;
		gbc_lblToRegex.gridy = 1;
		add(lblToRegex, gbc_lblToRegex);
		
		txtToRegex = new JTextField();
//		txtToRegex.getDocument().addDocumentListener(new DocumentListener() {
//			@Override
//			public void insertUpdate(DocumentEvent e) {
//				toRegexChanged();
//			}
//
//			@Override
//			public void removeUpdate(DocumentEvent e) {
//				toRegexChanged();
//			}
//
//			@Override
//			public void changedUpdate(DocumentEvent e) {
//				toRegexChanged();
//			}
//		});
		GridBagConstraints gbc_txtToRegex = new GridBagConstraints();
		gbc_txtToRegex.insets = new Insets(0, 5, 5, 0);
		gbc_txtToRegex.fill = GridBagConstraints.BOTH;
		gbc_txtToRegex.gridx = 1;
		gbc_txtToRegex.gridy = 1;
		add(txtToRegex, gbc_txtToRegex);
		
		tglUseLast = new JToggleButton("First Match");
		tglUseLast.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (tglUseLast.isSelected())
					tglUseLast.setText("Last Match");
				else
					tglUseLast.setText("First Match");
			}
		});
		GridBagConstraints gbc_tglReverse = new GridBagConstraints();
		gbc_tglReverse.fill = GridBagConstraints.HORIZONTAL;
		gbc_tglReverse.gridwidth = 2;
		gbc_tglReverse.insets = new Insets(0, 0, 5, 0);
		gbc_tglReverse.gridx = 0;
		gbc_tglReverse.gridy = 2;
		add(tglUseLast, gbc_tglReverse);
		
		chkLines = new JCheckBox("Use line numbers (absolute)");
		chkLines.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setLineChecked(chkLines.isSelected());
			}
		});
		GridBagConstraints gbc_chkLines = new GridBagConstraints();
		gbc_chkLines.anchor = GridBagConstraints.WEST;
		gbc_chkLines.fill = GridBagConstraints.VERTICAL;
		gbc_chkLines.gridwidth = 2;
		gbc_chkLines.insets = new Insets(0, 0, 5, 0);
		gbc_chkLines.gridx = 0;
		gbc_chkLines.gridy = 3;
		add(chkLines, gbc_chkLines);
		
		lblNewLabel = new JLabel("From line#:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.fill = GridBagConstraints.VERTICAL;
		gbc_lblNewLabel.weightx = 0.2;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 4;
		add(lblNewLabel, gbc_lblNewLabel);
		
		txtFromLine = new JFormattedTextField();
		txtFromLine.setEnabled(false);
		txtFromLine.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_txtFromLine = new GridBagConstraints();
		gbc_txtFromLine.insets = new Insets(0, 5, 5, 0);
		gbc_txtFromLine.weightx = 1.0;
		gbc_txtFromLine.fill = GridBagConstraints.BOTH;
		gbc_txtFromLine.gridx = 1;
		gbc_txtFromLine.gridy = 4;
		add(txtFromLine, gbc_txtFromLine);
		txtFromLine.setColumns(10);
		
		JLabel lblToLine = new JLabel("To line#:");
		lblToLine.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblToLine = new GridBagConstraints();
		gbc_lblToLine.fill = GridBagConstraints.VERTICAL;
		gbc_lblToLine.insets = new Insets(0, 0, 0, 5);
		gbc_lblToLine.anchor = GridBagConstraints.EAST;
		gbc_lblToLine.gridx = 0;
		gbc_lblToLine.gridy = 5;
		add(lblToLine, gbc_lblToLine);
		
		txtToLine = new JFormattedTextField();
		txtToLine.setEnabled(false);
		txtToLine.setHorizontalAlignment(SwingConstants.LEFT);
		txtToLine.setColumns(10);
		GridBagConstraints gbc_txtToLine = new GridBagConstraints();
		gbc_txtToLine.insets = new Insets(0, 5, 0, 0);
		gbc_txtToLine.fill = GridBagConstraints.BOTH;
		gbc_txtToLine.gridx = 1;
		gbc_txtToLine.gridy = 5;
		add(txtToLine, gbc_txtToLine);
	}
	
//	@Override
//	public void clear() {
//		chkLines.setSelected(false);
//		tglUseLast.setSelected(false);
//		txtFromLine.setValue(0);
//		txtToLine.setValue(0);
//		txtFromRegex.setText("");
//		txtToRegex.setText("");
//	}

	private void fromRegexChanged() {
		if (!StringH.isNullOrEmpty(txtFromRegex.getText()))
			chkLines.setText("Use line numbers (relative)");
		else
			chkLines.setText("Use line numbers (absolute)");
	}

//	private void toRegexChanged() {
//		boolean enabled = StringH.isNullOrEmpty(txtToRegex.getText());
//		txtFromLine.setEnabled(enabled);
//		txtToLine.setEnabled(enabled);
//		chkLines.setEnabled(enabled);
//	}

	private void setLineChecked(boolean selected) {
//		if (selected) {
//			txtToRegex.setEnabled(false);
//			txtToRegex.setText("");
//		}
		txtFromLine.setEnabled(selected);
		txtToLine.setEnabled(selected);
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		chkLines.setEnabled(enabled);
		tglUseLast.setEnabled(enabled);
		txtFromLine.setEnabled(enabled);
		txtToLine.setEnabled(enabled);
		txtFromRegex.setEnabled(enabled);
		txtToRegex.setEnabled(enabled);
	}

	public void setValues(String txtRegexFrom, String txtRegexTo,
			int lineFrom, int lineTo, boolean useLines, boolean useLastMatch) {
		txtFromRegex.setText(txtRegexFrom);
		txtToRegex.setText(txtRegexTo);
		txtFromLine.setValue(lineFrom);
		txtToLine.setValue(lineTo);
		chkLines.setSelected(useLines);
		tglUseLast.setSelected(useLastMatch);
	}
	
	public void updateValuesInExtension() {
		_ext.updateValues(txtFromRegex.getText(), txtToRegex.getText(), 
				txtFromLine.getValue() == null ? 0 : (Integer) txtFromLine.getValue(), 
				txtToLine.getValue() == null ? 0 : (Integer) txtToLine.getValue(), 
				chkLines.isSelected(), tglUseLast.isSelected());
	}

	public void updateEnabled() {
//
//		if (enabled)
			setLineChecked(chkLines.isSelected());
//		else
//			chkLines.setSelected(false);
	}
}
