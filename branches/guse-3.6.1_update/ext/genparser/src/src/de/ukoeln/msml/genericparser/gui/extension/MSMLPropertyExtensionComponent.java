package de.ukoeln.msml.genericparser.gui.extension;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserComponent;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl.DictRefData;

public class MSMLPropertyExtensionComponent extends JPanel implements
		IMSMLParserComponent {
	
	private static final long serialVersionUID = 1L;
	private MSMLPropertyExtension _ext;
	private JTextField txtTitle;
	private JTextField txtID;
	private JComboBox cmbDict;
	private JComboBox cmbRef;
	private DefaultComboBoxModel _dictModel;
	private DefaultComboBoxModel _refModel;
	private JLabel lblID;
	
	public MSMLPropertyExtensionComponent(MSMLPropertyExtension ext) {
		this();
		_ext = ext;
		
		_dictModel = new DefaultComboBoxModel();
		cmbDict.setModel(_dictModel);
		
		_refModel = new DefaultComboBoxModel();
		cmbRef.setModel(_refModel);
	}
	
	public MSMLPropertyExtensionComponent() {
		setMinimumSize(new Dimension(350, 95));
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setPreferredSize(new Dimension(350, 124));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 30, 0};
		gridBagLayout.rowHeights = new int[]{30, 30, 30, 30, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblDict = new JLabel("Dict:");
		GridBagConstraints gbc_lblDict = new GridBagConstraints();
		gbc_lblDict.anchor = GridBagConstraints.EAST;
		gbc_lblDict.gridx = 0;
		gbc_lblDict.gridy = 0;
		add(lblDict, gbc_lblDict);
		
		cmbDict = new JComboBox();
		cmbDict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dictSelectionChanged();
			}
		});
		GridBagConstraints gbc_cmbDict = new GridBagConstraints();
		gbc_cmbDict.insets = new Insets(2, 0, 2, 0);
		gbc_cmbDict.fill = GridBagConstraints.BOTH;
		gbc_cmbDict.gridx = 1;
		gbc_cmbDict.gridy = 0;
		add(cmbDict, gbc_cmbDict);
		
		lblID = new JLabel("ID:");
		GridBagConstraints gbc_lblID = new GridBagConstraints();
		gbc_lblID.anchor = GridBagConstraints.EAST;
		gbc_lblID.gridx = 0;
		gbc_lblID.gridy = 1;
		add(lblID, gbc_lblID);
		
		cmbRef = new JComboBox();
		GridBagConstraints gbc_cmbRef = new GridBagConstraints();
		gbc_cmbRef.insets = new Insets(2, 0, 2, 0);
		gbc_cmbRef.fill = GridBagConstraints.BOTH;
		gbc_cmbRef.gridx = 1;
		gbc_cmbRef.gridy = 1;
		add(cmbRef, gbc_cmbRef);
		
		JLabel lblTitle = new JLabel("Title:");
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.anchor = GridBagConstraints.EAST;
		gbc_lblTitle.gridx = 0;
		gbc_lblTitle.gridy = 2;
		add(lblTitle, gbc_lblTitle);
		
		txtTitle = new JTextField();
		GridBagConstraints gbc_txtTitle = new GridBagConstraints();
		gbc_txtTitle.insets = new Insets(2, 0, 2, 0);
		gbc_txtTitle.fill = GridBagConstraints.BOTH;
		gbc_txtTitle.gridx = 1;
		gbc_txtTitle.gridy = 2;
		add(txtTitle, gbc_txtTitle);
		txtTitle.setColumns(10);
		
		JLabel lblId = new JLabel("Id:");
		GridBagConstraints gbc_lblId = new GridBagConstraints();
		gbc_lblId.anchor = GridBagConstraints.EAST;
		gbc_lblId.gridx = 0;
		gbc_lblId.gridy = 3;
		add(lblId, gbc_lblId);
		
		txtID = new JTextField();
		GridBagConstraints gbc_txtID = new GridBagConstraints();
		gbc_txtID.insets = new Insets(2, 0, 2, 0);
		gbc_txtID.fill = GridBagConstraints.BOTH;
		gbc_txtID.gridx = 1;
		gbc_txtID.gridy = 3;
		add(txtID, gbc_txtID);
		txtID.setColumns(10);
	}
	
	private void dictSelectionChanged() {
		cmbRef.setEnabled(cmbDict.getSelectedItem() != null);
		_refModel.removeAllElements();
		if (cmbDict.getSelectedItem() == null)
			return;
		for (String entry : ((DictRefData) cmbDict.getSelectedItem()).getEntries()) {
			_refModel.addElement(entry);
		}
	}
//
//	@Override
//	public void clear() {
//		cmbDict.setSelectedItem(null);
//		cmbRef.setSelectedItem(null);
//		txtID.setText(null);
//		txtTitle.setText(null);
//	}

	@Override
	public void setChildComponentsEnabled(boolean enabled) {
		cmbDict.setEnabled(enabled);
		cmbRef.setEnabled(enabled);
		txtID.setEnabled(enabled);
		txtTitle.setEnabled(enabled);
	}

	public void updateValuesInExtension() {
		String dict = cmbDict.getSelectedItem() == null ? null : cmbDict.getSelectedItem().toString();
		String ref = cmbRef.getSelectedItem() == null ? null : cmbRef.getSelectedItem().toString();

		_ext.updateValues(dict, ref, txtID.getText(), txtTitle.getText());
	}

	public void setValues(String dict, String ref, String id, String title) {
		for (int i = 0; i < _dictModel.getSize(); i++) {
			Object elem = _dictModel.getElementAt(i);
			if (elem != null && !elem.equals(dict))
				continue;
			cmbDict.setSelectedItem(elem);
		}

		cmbRef.setSelectedItem(ref);
		txtID.setText(id);
		txtTitle.setText(title);
		cmbRef.setEnabled(cmbDict.getSelectedItem() != null);
	}

	public void dictsChanged(List<DictRefData> dicts) {
		_dictModel.removeAllElements();
		_dictModel.addElement(null);
		for (DictRefData data : dicts) {
			_dictModel.addElement(data);
		}
		dictSelectionChanged();
	}
}
