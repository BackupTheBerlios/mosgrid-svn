package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLFileSelectorExtensionComponent extends JPanel 
implements IMSMLParserComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField _txtFilePattern;
	private JCheckBox _chkUseParentText;
	private MSMLFileSelectorExtension _ext;
	private JCheckBox _chkIsMSML;
	private JTextField _txtJobPattern;
	private JLabel _lblJobPattern;
	
	public MSMLFileSelectorExtensionComponent(MSMLFileSelectorExtension ext) {
		this();
		_ext = ext;
	}
	
	public MSMLFileSelectorExtensionComponent() {
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setMaximumSize(new Dimension(32767, 130));
		setMinimumSize(new Dimension(10, 130));
		setPreferredSize(new Dimension(350, 130));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{117, 224, 0};
		gridBagLayout.rowHeights = new int[]{30, 30, 30, 30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);

		_chkUseParentText = new JCheckBox("Use parent's text");
		_chkUseParentText.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateParentControls();
			}
		});
		GridBagConstraints gbc_chkUseParentText = new GridBagConstraints();
		gbc_chkUseParentText.fill = GridBagConstraints.VERTICAL;
		gbc_chkUseParentText.anchor = GridBagConstraints.WEST;
		gbc_chkUseParentText.gridwidth = 2;
		gbc_chkUseParentText.insets = new Insets(0, 0, 5, 0);
		gbc_chkUseParentText.gridx = 0;
		gbc_chkUseParentText.gridy = 0;
		add(_chkUseParentText, gbc_chkUseParentText);
		
		JLabel _lblFilePattern = new JLabel("File pattern:");
		_lblFilePattern.setSize(new Dimension(0, 30));
		GridBagConstraints gbc__lblFilePattern = new GridBagConstraints();
		gbc__lblFilePattern.anchor = GridBagConstraints.EAST;
		gbc__lblFilePattern.weightx = 0.7;
		gbc__lblFilePattern.weighty = 1.0;
		gbc__lblFilePattern.fill = GridBagConstraints.VERTICAL;
		gbc__lblFilePattern.insets = new Insets(0, 0, 5, 5);
		gbc__lblFilePattern.gridx = 0;
		gbc__lblFilePattern.gridy = 1;
		add(_lblFilePattern, gbc__lblFilePattern);

		_txtFilePattern = new JTextField();
		_txtFilePattern.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateMSMLControls();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateMSMLControls();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateMSMLControls();
			}
		});
		_txtFilePattern.setSize(new Dimension(0, 30));
		GridBagConstraints gbc_txtFilePattern = new GridBagConstraints();
		gbc_txtFilePattern.insets = new Insets(0, 0, 5, 0);
		gbc_txtFilePattern.weightx = 1.0;
		gbc_txtFilePattern.weighty = 1.0;
		gbc_txtFilePattern.fill = GridBagConstraints.BOTH;
		gbc_txtFilePattern.gridx = 1;
		gbc_txtFilePattern.gridy = 1;
		add(_txtFilePattern, gbc_txtFilePattern);
		_txtFilePattern.setColumns(10);
		
		_chkIsMSML = new JCheckBox("File is MSML");
		_chkIsMSML.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateMSMLControls();
			}
		});
		GridBagConstraints gbc_chkIsMSML = new GridBagConstraints();
		gbc_chkIsMSML.gridwidth = 2;
		gbc_chkIsMSML.fill = GridBagConstraints.VERTICAL;
		gbc_chkIsMSML.anchor = GridBagConstraints.WEST;
		gbc_chkIsMSML.insets = new Insets(0, 0, 5, 0);
		gbc_chkIsMSML.gridx = 0;
		gbc_chkIsMSML.gridy = 2;
		add(_chkIsMSML, gbc_chkIsMSML);
		
		_lblJobPattern = new JLabel("Job Pattern:");
		GridBagConstraints gbc_lblJobPattern = new GridBagConstraints();
		gbc_lblJobPattern.fill = GridBagConstraints.VERTICAL;
		gbc_lblJobPattern.insets = new Insets(0, 0, 0, 5);
		gbc_lblJobPattern.anchor = GridBagConstraints.EAST;
		gbc_lblJobPattern.gridx = 0;
		gbc_lblJobPattern.gridy = 3;
		add(_lblJobPattern, gbc_lblJobPattern);
		
		_txtJobPattern = new JTextField();
		GridBagConstraints gbc_txtJobPattern = new GridBagConstraints();
		gbc_txtJobPattern.fill = GridBagConstraints.BOTH;
		gbc_txtJobPattern.gridx = 1;
		gbc_txtJobPattern.gridy = 3;
		add(_txtJobPattern, gbc_txtJobPattern);
		_txtJobPattern.setColumns(10);
	}

	public void updateValuesInExtension() {
		_ext.updateValues(_txtFilePattern.getText(), 
				_chkUseParentText.isSelected(),
				_chkIsMSML.isSelected(),
				_txtJobPattern.getText());
	}
	
	public void setValues(String pattern, boolean useParentsText, boolean isMSML, String jobPattern) {
		_chkUseParentText.setSelected(useParentsText);
		updateParentControls();
		_txtFilePattern.setText(pattern);
		_chkIsMSML.setSelected(isMSML);
		_txtJobPattern.setText(jobPattern);
		updateMSMLControls();
	}
	
	private void updateParentControls() {
		_txtFilePattern.setEnabled(!_chkUseParentText.isSelected());
		updateMSMLControls();
	}
	
	private void updateMSMLControls() {
		boolean msmlEnabled = !StringH.isNullOrEmpty(_txtFilePattern.getText()) &&
				!_chkUseParentText.isSelected();  
		boolean patternEnabled = msmlEnabled && _chkIsMSML.isSelected();
		_chkIsMSML.setEnabled(msmlEnabled);
		_txtJobPattern.setEnabled(patternEnabled);
	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		_chkUseParentText.setEnabled(enabled);
		_txtFilePattern.setEnabled(enabled);
		_chkIsMSML.setEnabled(enabled);
		_txtJobPattern.setEnabled(enabled);
	}
}
