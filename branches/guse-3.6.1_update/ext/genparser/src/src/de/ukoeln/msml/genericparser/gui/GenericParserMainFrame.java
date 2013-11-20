package de.ukoeln.msml.genericparser.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.ukoeln.msml.genericparser.AfterCreationAction;
import de.ukoeln.msml.genericparser.GenericParserDocumentBase;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.MSMLSimpleTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValue;
import de.ukoeln.msml.genericparser.MSMLTreeValueBase;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPuroposeVistorMode;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.classes.visitors.VisitorCallBack;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.interfaces.ITree;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.ITreeSwingImpl;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.TreeModelSwingImpl;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;

public class GenericParserMainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1183062574116932023L;

	private JTextPane tpResult;
	private ITreeSwingImpl jtMSMLObjectTree;
	private JPanel pnlControlsParent;

	private JPanel pnlResult;
	private JButton btnTestResult;
	private JMenuBar menuBar;
	private JMenu miFile;
	private JMenuItem miSaveConfig;

	private GenericParserMainDocument _doc;
	private JPanel pnlAdvancedView;
	private JButton btnAdd;
	private JButton btnRemove;
	private JMenuItem miLoadConfig;
	private JSeparator separator;
	private JMenuItem miExit;
	private JScrollPane scpMSMLTree;
	private JMenu miGenerate;
	private JMenuItem miGenerateMSML;
	private JMenu miSettings;
	private JMenuItem miSetBasePath;
	private JSplitPane spTreeToResult;
	private JScrollPane scpResult;
	private JScrollPane spControls;
	private JPanel pnlControls;
	private JMenu miHelp;
	private JMenuItem miManual;
	private JTabbedPane tpTabs;
	private JPanel pnlDictionaryConfig;
	private JPanel pnlViews;
	private JToggleButton tglbtnViews;
	private JLayeredPane lpViews;
	private JPanel pnlSimpleView;
	private ITreeSwingImpl jtSimpleTree;
	private JButton btnNewScalarProperty;
	private JButton btnNewMolecule;
	private JButton btnRemoveSimpleElement;
	private JScrollPane spSimpleTree;
	private JTable tblDicts;
	private JButton btnAddDict;
	private JButton btnRemoveDict;
	private JScrollPane scrollPane;
	private JTabbedPane tabCtrlLayerFilter;
	private JButton btnNewArrayProp;
	private JButton btnNewLayer;

	public GenericParserMainFrame(GenericParserMainDocument doc, TreeModelSwingImpl jtmData,
			TreeModelSwingImpl jtmSimpleData, DictTableModelSwingImpl tmDictModel) {
		this();

		jtMSMLObjectTree.setModel(jtmData);
		jtMSMLObjectTree.setCellRenderer(new MSMLObjectTreeCellRenderer());
		jtMSMLObjectTree.repaint();

		jtSimpleTree.setModel(jtmSimpleData);
		jtSimpleTree.expandRow(0);
		jtSimpleTree.setCellRenderer(new MSMLObjectTreeCellRenderer());
		jtSimpleTree.repaint();

		tblDicts.setModel(tmDictModel);
		JComboBox dictEdit = new JComboBox(tmDictModel.getDictionaries());
		tblDicts.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(dictEdit));
		tblDicts.getTableHeader().setPreferredSize(new Dimension(tblDicts.getWidth(), 25));

		tglbtnViews.setEnabled(false);
		_doc = doc;
	}

	public GenericParserMainFrame() {
		this.setTitle("MSML Generic Parser");
		this.setPreferredSize(new Dimension(1000, 600));
		this.setSize(new Dimension(800, 600));
		this.setLocation(300, 300);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		menuBar = new JMenuBar();
		GridBagConstraints gbc_menuBar = new GridBagConstraints();
		gbc_menuBar.anchor = GridBagConstraints.NORTHWEST;
		gbc_menuBar.insets = new Insets(0, 0, 5, 0);
		gbc_menuBar.gridx = 0;
		gbc_menuBar.gridy = 0;

		miFile = new JMenu("File");
		menuBar.add(miFile);
		this.setJMenuBar(menuBar);

		miSaveConfig = new JMenuItem("Save Config");
		miSaveConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveConfig();
			}
		});
		miFile.add(miSaveConfig);

		miLoadConfig = new JMenuItem("Load Config");
		miLoadConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_doc.loadConfig();
			}
		});
		miFile.add(miLoadConfig);

		separator = new JSeparator();
		miFile.add(separator);

		miExit = new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		miFile.add(miExit);

		miGenerate = new JMenu("Generate");
		menuBar.add(miGenerate);

		miGenerateMSML = new JMenuItem("MSML");
		miGenerateMSML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveMSML();
			}
		});
		miGenerate.add(miGenerateMSML);

		miSettings = new JMenu("Settings");
		menuBar.add(miSettings);

		miSetBasePath = new JMenuItem("Set base path");
		miSetBasePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_doc.chooseBasePath();
			}
		});
		miSettings.add(miSetBasePath);

		miHelp = new JMenu("Help");
		menuBar.add(miHelp);

		miManual = new JMenuItem("Manual");
		miManual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openManual();
			}
		});
		miHelp.add(miManual);
		GridBagConstraints gbc_spTreeToControls = new GridBagConstraints();
		gbc_spTreeToControls.fill = GridBagConstraints.BOTH;
		gbc_spTreeToControls.insets = new Insets(0, 0, 5, 5);
		gbc_spTreeToControls.gridx = 0;
		gbc_spTreeToControls.gridy = 0;
		GridBagConstraints gbc_pnlResult = new GridBagConstraints();
		gbc_pnlResult.insets = new Insets(0, 0, 5, 0);
		gbc_pnlResult.fill = GridBagConstraints.BOTH;
		gbc_pnlResult.gridx = 1;
		gbc_pnlResult.gridy = 0;

		tpTabs = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tpTabs);

		JPanel pnlTCandFile = new JPanel();
		tpTabs.addTab("Data", null, pnlTCandFile, null);
		GridBagLayout gbl_pnlTCandFile = new GridBagLayout();
		gbl_pnlTCandFile.columnWidths = new int[] { 650, 0 };
		gbl_pnlTCandFile.rowHeights = new int[] { 389, 0 };
		gbl_pnlTCandFile.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlTCandFile.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		pnlTCandFile.setLayout(gbl_pnlTCandFile);
		spTreeToResult = new JSplitPane();
		spTreeToResult.setResizeWeight(0.3);
		GridBagConstraints gbc_spTreeToResult = new GridBagConstraints();
		gbc_spTreeToResult.fill = GridBagConstraints.BOTH;
		gbc_spTreeToResult.gridx = 0;
		gbc_spTreeToResult.gridy = 0;
		pnlTCandFile.add(spTreeToResult, gbc_spTreeToResult);
		JSplitPane spTreeToControls = new JSplitPane();
		spTreeToControls.setResizeWeight(0.8);
		spTreeToControls.setPreferredSize(new Dimension(650, 0));
		spTreeToControls.setMinimumSize(new Dimension(0, 0));
		spTreeToResult.setLeftComponent(spTreeToControls);

		pnlViews = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		spTreeToControls.setLeftComponent(pnlViews);
		GridBagLayout gbl_pnlViews = new GridBagLayout();
		gbl_pnlViews.columnWidths = new int[] { 354, 0 };
		gbl_pnlViews.rowHeights = new int[] { 33, 259, 0 };
		gbl_pnlViews.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_pnlViews.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		pnlViews.setLayout(gbl_pnlViews);

		tglbtnViews = new JToggleButton("Simple View");
		tglbtnViews.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleViews();
			}
		});
		GridBagConstraints gbc_tglbtnViews = new GridBagConstraints();
		gbc_tglbtnViews.fill = GridBagConstraints.BOTH;
		gbc_tglbtnViews.insets = new Insets(0, 0, 5, 0);
		gbc_tglbtnViews.gridx = 0;
		gbc_tglbtnViews.gridy = 0;
		pnlViews.add(tglbtnViews, gbc_tglbtnViews);

		lpViews = new JLayeredPane();
		lpViews.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				adjustLayeredSizes();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				adjustLayeredSizes();
			}
		});
		lpViews.setLayout(null);
		GridBagConstraints gbc_lpViews = new GridBagConstraints();
		gbc_lpViews.fill = GridBagConstraints.BOTH;
		gbc_lpViews.gridx = 0;
		gbc_lpViews.gridy = 1;
		pnlViews.add(lpViews, gbc_lpViews);

		pnlAdvancedView = new JPanel();
		lpViews.setLayer(pnlAdvancedView, 0);
		pnlAdvancedView.setBounds(0, 0, 354, 484);
		lpViews.add(pnlAdvancedView);
		pnlAdvancedView.setPreferredSize(new Dimension(300, 10));
		pnlAdvancedView.setMaximumSize(new Dimension(300, 32767));
		pnlAdvancedView.setVisible(false);
		GridBagLayout gbl_pnlAdvancedView = new GridBagLayout();
		gbl_pnlAdvancedView.columnWidths = new int[] { 59, 0, 0 };
		gbl_pnlAdvancedView.rowHeights = new int[] { 1, 0, 0 };
		gbl_pnlAdvancedView.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlAdvancedView.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		pnlAdvancedView.setLayout(gbl_pnlAdvancedView);

		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnAdd.setMinimumSize(new Dimension(60, 25));
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_doc.getAdvDoc().addNewChild((MSMLTreeValue) getValToCurrentSelection(), AfterCreationAction.SELECT);
			}
		});
		btnAdd.setPreferredSize(new Dimension(80, 25));
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.fill = GridBagConstraints.BOTH;
		gbc_btnAdd.weightx = 1.0;
		gbc_btnAdd.insets = new Insets(0, 0, 0, 5);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 1;
		pnlAdvancedView.add(btnAdd, gbc_btnAdd);

		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);
		btnRemove.setMaximumSize(new Dimension(60, 25));
		btnRemove.setMinimumSize(new Dimension(60, 25));
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_doc.getAdvDoc().removeNode((MSMLTreeValue) getValToCurrentSelection());
			}
		});
		btnRemove.setPreferredSize(new Dimension(80, 25));
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.fill = GridBagConstraints.BOTH;
		gbc_btnRemove.weightx = 1.0;
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 1;
		pnlAdvancedView.add(btnRemove, gbc_btnRemove);

		jtMSMLObjectTree = new ITreeSwingImpl();
		jtMSMLObjectTree.setSize(new Dimension(100, 100));
		jtMSMLObjectTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				MSMLTreeValueBase oldVal = getValToPath(e.getOldLeadSelectionPath());
				MSMLTreeValueBase val = getValToPath(e.getNewLeadSelectionPath());
				selectionChanged(oldVal, val);
			}
		});
		jtMSMLObjectTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		jtMSMLObjectTree.setEditable(false);

		scpMSMLTree = new JScrollPane(jtMSMLObjectTree);
		GridBagConstraints gbc_scpMSMLTree = new GridBagConstraints();
		gbc_scpMSMLTree.weightx = 1.0;
		gbc_scpMSMLTree.gridwidth = 2;
		gbc_scpMSMLTree.fill = GridBagConstraints.BOTH;
		gbc_scpMSMLTree.insets = new Insets(0, 0, 5, 0);
		gbc_scpMSMLTree.gridx = 0;
		gbc_scpMSMLTree.gridy = 0;
		pnlAdvancedView.add(scpMSMLTree, gbc_scpMSMLTree);

		pnlSimpleView = new JPanel();
		pnlSimpleView.setPreferredSize(new Dimension(300, 100));
		pnlSimpleView.setBounds(0, 0, 354, 484);
		lpViews.setLayer(pnlSimpleView, 1);
		lpViews.add(pnlSimpleView);
		GridBagLayout gbl_pnlSimpleView = new GridBagLayout();
		gbl_pnlSimpleView.columnWidths = new int[] { 185, 30, 0 };
		gbl_pnlSimpleView.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_pnlSimpleView.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlSimpleView.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		pnlSimpleView.setLayout(gbl_pnlSimpleView);

		jtSimpleTree = new ITreeSwingImpl();
		jtSimpleTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					_doc.resetLayer();
			}
		});
		jtSimpleTree.setShowsRootHandles(true);
		jtSimpleTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				MSMLTreeValueBase oldVal = getValToPath(e.getOldLeadSelectionPath());
				MSMLTreeValueBase val = getValToPath(e.getNewLeadSelectionPath());
				selectionChanged(oldVal, val);
			}
		});
		GridBagConstraints gbc_tree = new GridBagConstraints();
		gbc_tree.anchor = GridBagConstraints.WEST;
		gbc_tree.gridwidth = 2;
		gbc_tree.insets = new Insets(0, 0, 5, 0);
		gbc_tree.fill = GridBagConstraints.VERTICAL;
		gbc_tree.gridx = 0;
		gbc_tree.gridy = 0;
		
		btnRemoveSimpleElement = new JButton("Remove");
		btnRemoveSimpleElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSimpleElementClicked();
			}
		});

		btnNewMolecule = new JButton("Add Molecule");
		btnNewMolecule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newMoleculeClicked();
			}
		});
		
		btnNewArrayProp = new JButton("Add Array-Property");
		btnNewArrayProp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newArrayPropertyClicked();
			}
		});
		GridBagConstraints gbc_btnNewArrayProp = new GridBagConstraints();
		gbc_btnNewArrayProp.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewArrayProp.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewArrayProp.gridx = 0;
		gbc_btnNewArrayProp.gridy = 1;
		pnlSimpleView.add(btnNewArrayProp, gbc_btnNewArrayProp);
		
		btnNewLayer = new JButton("Add Layer");
		btnNewLayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_doc.addLayer();
			}
		});
		GridBagConstraints gbc_btnNewLayer = new GridBagConstraints();
		gbc_btnNewLayer.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewLayer.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewLayer.gridx = 1;
		gbc_btnNewLayer.gridy = 1;
		pnlSimpleView.add(btnNewLayer, gbc_btnNewLayer);

		GridBagConstraints gbc_btnNewMolecule = new GridBagConstraints();
		gbc_btnNewMolecule.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewMolecule.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewMolecule.gridx = 0;
		gbc_btnNewMolecule.gridy = 2;
		pnlSimpleView.add(btnNewMolecule, gbc_btnNewMolecule);
		
				btnNewScalarProperty = new JButton("Add Scalar-Property");
				btnNewScalarProperty.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newScalarPropertyClicked();
					}
				});
				GridBagConstraints gbc_btnNewScalarProperty = new GridBagConstraints();
				gbc_btnNewScalarProperty.fill = GridBagConstraints.HORIZONTAL;
				gbc_btnNewScalarProperty.insets = new Insets(0, 0, 5, 0);
				gbc_btnNewScalarProperty.gridx = 1;
				gbc_btnNewScalarProperty.gridy = 2;
				pnlSimpleView.add(btnNewScalarProperty, gbc_btnNewScalarProperty);
		GridBagConstraints gbc_btnRemoveSimpleElement = new GridBagConstraints();
		gbc_btnRemoveSimpleElement.gridwidth = 2;
		gbc_btnRemoveSimpleElement.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveSimpleElement.gridx = 0;
		gbc_btnRemoveSimpleElement.gridy = 3;
		pnlSimpleView.add(btnRemoveSimpleElement, gbc_btnRemoveSimpleElement);

		spSimpleTree = new JScrollPane(jtSimpleTree);
		GridBagConstraints gbc_spSimpleTree = new GridBagConstraints();
		gbc_spSimpleTree.insets = new Insets(0, 0, 5, 0);
		gbc_spSimpleTree.gridwidth = 2;
		gbc_spSimpleTree.fill = GridBagConstraints.BOTH;
		gbc_spSimpleTree.gridx = 0;
		gbc_spSimpleTree.gridy = 0;
		pnlSimpleView.add(spSimpleTree, gbc_spSimpleTree);

		pnlControlsParent = new JPanel();
		pnlControlsParent.setMinimumSize(new Dimension(350, 10));
		pnlControlsParent.setMaximumSize(new Dimension(350, 32767));
		pnlControlsParent.setPreferredSize(new Dimension(350, 0));
		spTreeToControls.setRightComponent(pnlControlsParent);
		pnlControlsParent.setLayout(new BoxLayout(pnlControlsParent, BoxLayout.PAGE_AXIS));

		tabCtrlLayerFilter = new JTabbedPane(JTabbedPane.TOP);
		pnlControlsParent.add(tabCtrlLayerFilter);
		pnlControls = new JPanel();
		pnlControls.setMinimumSize(new Dimension(370, 10));
		spControls = new JScrollPane(pnlControls);
		tabCtrlLayerFilter.addTab("Filter", null, spControls, null);
		pnlControls.setLayout(new BoxLayout(pnlControls, BoxLayout.Y_AXIS));

		spTreeToControls.setDividerLocation(1.0);

		pnlResult = new JPanel();
		pnlResult.setBorder(new LineBorder(new Color(0, 0, 0)));
		spTreeToResult.setRightComponent(pnlResult);
		pnlResult.setLayout(new BorderLayout(0, 0));

		btnTestResult = new JButton("Test configuration");
		btnTestResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				pullOfParsingForCurrentProperty();
			}
		});
		btnTestResult.setPreferredSize(new Dimension(100, 25));
		btnTestResult.setMaximumSize(new Dimension(32000, 32000));
		btnTestResult.setMinimumSize(new Dimension(0, 0));
		pnlResult.add(btnTestResult, BorderLayout.SOUTH);

		tpResult = new JTextPane();
		tpResult.setMinimumSize(new Dimension(200, 0));
		tpResult.setAlignmentY(Component.TOP_ALIGNMENT);
		tpResult.setAlignmentX(Component.LEFT_ALIGNMENT);
		tpResult.setEditable(false);

		scpResult = new JScrollPane(tpResult);
		pnlResult.add(scpResult, BorderLayout.CENTER);
		spTreeToResult.setDividerLocation(1.0);

		pnlDictionaryConfig = new JPanel();
		tpTabs.addTab("Dictionaries", null, pnlDictionaryConfig, null);
		GridBagLayout gbl_pnlDictionaryConfig = new GridBagLayout();
		gbl_pnlDictionaryConfig.columnWidths = new int[] { 0, 0, 0 };
		gbl_pnlDictionaryConfig.rowHeights = new int[] { 0, 0, 0 };
		gbl_pnlDictionaryConfig.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_pnlDictionaryConfig.rowWeights = new double[] { 12.0, 1.0, Double.MIN_VALUE };
		pnlDictionaryConfig.setLayout(gbl_pnlDictionaryConfig);

		tblDicts = new JTable();
		tblDicts.setRowHeight(25);
		GridBagConstraints gbc_tblDicts = new GridBagConstraints();
		gbc_tblDicts.insets = new Insets(0, 0, 5, 0);
		gbc_tblDicts.gridwidth = 2;
		gbc_tblDicts.fill = GridBagConstraints.BOTH;
		gbc_tblDicts.gridx = 0;
		gbc_tblDicts.gridy = 0;

		btnAddDict = new JButton("Add Row");
		btnAddDict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDictrow();
			}
		});
		GridBagConstraints gbc_btnAddDict = new GridBagConstraints();
		gbc_btnAddDict.insets = new Insets(0, 0, 0, 5);
		gbc_btnAddDict.anchor = GridBagConstraints.WEST;
		gbc_btnAddDict.gridx = 0;
		gbc_btnAddDict.gridy = 1;
		pnlDictionaryConfig.add(btnAddDict, gbc_btnAddDict);

		btnRemoveDict = new JButton("Remove Row");
		btnRemoveDict.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeDictRow();
			}
		});
		GridBagConstraints gbc_btnRemoveDict = new GridBagConstraints();
		gbc_btnRemoveDict.anchor = GridBagConstraints.EAST;
		gbc_btnRemoveDict.gridx = 1;
		gbc_btnRemoveDict.gridy = 1;
		pnlDictionaryConfig.add(btnRemoveDict, gbc_btnRemoveDict);

		scrollPane = new JScrollPane(tblDicts);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		pnlDictionaryConfig.add(scrollPane, gbc_scrollPane);

		this.pack();
	}

	private void saveConfig() {
		TreePath path = getCurrentlyActiveTree().getSelectionPath();
		if (path != null)
			getValToPath(path).storeConfig();
		_doc.saveConfig();
	}

	private ITreeSwingImpl getCurrentlyActiveTree() {
		if (pnlAdvancedView.isVisible())
			return jtMSMLObjectTree;
		return jtSimpleTree;
	}

	private void saveMSML() {
		TreePath path = getCurrentlyActiveTree().getSelectionPath();
		if (path != null)
			getValToPath(path).storeConfig();
		_doc.saveMSML();
	}

	/**
	 * This method dynamically builds the filter controls in the middle section
	 * of the parser GUI. Before it switches to the new layout it stores all
	 * information in the configuration property of the {@link MSMLTreeValue}.
	 * Then all components are removed from the extension panel and the new ones
	 * are added. <b>NOTE</b>: There is only one instance of a "filter"
	 * component. They are dynamically added and removed. You will have to clear
	 * the contents of newly added extension components and you cannot rely on
	 * having the correct components with the correct content, when you have not
	 * activated the corresponding tree node. You will always get the data from
	 * the last selected node. This method is used when selection of the tree
	 * has changed.
	 * 
	 * @param oldVal
	 *            This is the tree value that was last selected in the tree. Its
	 *            configuration- information will be updated in the
	 *            MSMLTreeValue.
	 * @param newVal
	 *            The currently selected value for which the GUI elements of the
	 *            middle section of the GUI will be loaded. After loading the
	 *            configuration-values will be loaded into the extension
	 *            component.
	 * 
	 * @see MSMLTreeValue
	 */
	private void buildFilterControls(MSMLTreeValueBase oldVal, MSMLTreeValueBase newVal) {
		if (oldVal != null)
			oldVal.storeConfig();

		pnlControls.removeAll();
		List<IMSMLParserExtension> extensions = _doc.getExtensionsToVal(newVal);
		for (IMSMLParserExtension extension : extensions) {
			extension.clear();
			pnlControls.add((Component) extension.getComponent());
		}

		triggerGeneralPurposeVisitor(GeneralPuroposeVistorMode.SELECTIONCHANGED, null);

		newVal.loadConfig();
		pnlControls.repaint();
		pnlControls.revalidate();
	}

	private void pullOfParsingForCurrentProperty() {
		TreePath path = getCurrentlyActiveTree().getSelectionPath();
		MSMLTreeValueBase val = getValToPath(path);
		val.storeConfig();

		try {
			tpResult.setText(_doc.getTextToVal(val));
		} catch (Exception e) {
			String message = "Critical failure! This text has been copied to clipboard. Please paste this text into an email and send it to me. Exiting: \n\n"
					+ e.toString() + "\n\n" + e.getMessage() + "\n\n" + StackTraceHelper.getTrace(e);
			e.printStackTrace();

			System.out.println(message);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(message), null);
			JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void setText(String text) {
		tpResult.setText(text);
	}

	private DefaultMutableTreeNode getNodetoPath(TreePath path) {
		if (path == null)
			return null;
		TreeNode node = (TreeNode) path.getLastPathComponent();
		if ((node == null) || !(node instanceof DefaultMutableTreeNode))
			return null;
		return (DefaultMutableTreeNode) node;
	}

	private MSMLTreeValueBase getValToNode(DefaultMutableTreeNode mutNode) {
		if ((mutNode == null) || (mutNode.getUserObject() == null)
				|| !(mutNode.getUserObject() instanceof MSMLTreeValueBase))
			return null;
		return (MSMLTreeValueBase) mutNode.getUserObject();
	}

	private MSMLTreeValueBase getValToPath(TreePath path) {
		return getValToNode(getNodetoPath(path));
	}

	private MSMLTreeValueBase getValToCurrentSelection() {
		ITreeSwingImpl tree = getCurrentlyActiveTree();
		return getValToPath(tree.getSelectionPath());
	}

	public void triggerGeneralPurposeVisitor(GeneralPuroposeVistorMode mode, VisitorCallBack initCallback) {
		MSMLTreeValueBase val = getValToCurrentSelection();
		if (val == null)
			return;
		GeneralPurposeVisitor visitor = new GeneralPurposeVisitor(val, mode);
		if (initCallback != null)
			initCallback.Do(visitor);

		for (IMSMLParserExtension ext : _doc.getExtensionsToVal(val)) {
			ext.initGeneralPurposeVisitor(visitor);
		}

		for (IMSMLParserExtension ext : _doc.getExtensionsToVal(val)) {
			ext.handleGeneralPurposeVisitor(visitor);
		}

		handleGeneralPurposeVisitor(visitor);

		btnRemoveSimpleElement.setEnabled(!val.isRoot());
	}

	private void selectionChanged(MSMLTreeValueBase oldVal, MSMLTreeValueBase val) {
		if (val == null)
			return;

		buildFilterControls(oldVal, val);
	}

	private void handleGeneralPurposeVisitor(GeneralPurposeVisitor visitor) {
		updateButtons(visitor);
	}

	private void updateButtons(GeneralPurposeVisitor visitor) {
		MSMLTreeValueBase val = visitor.getValue();
		
		if (val instanceof MSMLSimpleTreeValue) {
			btnNewLayer.setEnabled(visitor.getCanAddLayer());
			btnRemoveSimpleElement.setEnabled(visitor.getSimpleRemoveElementEnabled());
			btnNewMolecule.setEnabled(visitor.getCanAddMolecule());
			btnNewScalarProperty.setEnabled(visitor.getCanAddScalar());
			btnNewArrayProp.setEnabled(visitor.getCanAddArray());
		} else {
			btnAdd.setEnabled(visitor.getAddButtonEnabled());			
			boolean btnsEnabled = visitor.getMode() == GeneralPuroposeVistorMode.SELECTIONCHANGED
					&& _doc.getAdvDoc().isListElementOfFixedList(val);
			btnRemove.setEnabled(btnsEnabled);
		}		
	}

	public void repaintTree(GenericParserDocumentBase doc) {
		doc.getTree().validate();
		doc.getTree().repaint();
	}

	public void selectNode(DefaultMutableTreeNode node, GenericParserDocumentBase doc) {
		TreePath path = new TreePath(node.getPath());
		doc.getTree().expandPath(path);
		doc.getTree().setSelectionPath(path);
		doc.getTree().scrollPathToVisible(path);
	}

	public void openPathTo(DefaultMutableTreeNode node, GenericParserDocumentBase doc) {
		TreePath path = new TreePath(node.getPath());

		if (doc.getTree().getIModel().isLeaf(path.getLastPathComponent())) {
			if (node.getParent() == null)
				return;
			path = new TreePath(node.getParent());
		}

		doc.getTree().expandPath(path);
	}

	public MSMLTreeValueBase getSelectedValue() {
		return getValToCurrentSelection();
	}

	private void toggleViews() {
		MSMLTreeValueBase oldVal = getValToCurrentSelection();
		if (tglbtnViews.isSelected()) {
			lpViews.moveToFront(pnlAdvancedView);
			pnlAdvancedView.setVisible(true);
			pnlSimpleView.setVisible(false);
			tglbtnViews.setText("Advanced View");
		} else {
			lpViews.moveToFront(pnlSimpleView);
			pnlAdvancedView.setVisible(false);
			pnlSimpleView.setVisible(true);
			tglbtnViews.setText("Simple View");
		}
		MSMLTreeValueBase newVal = getValToCurrentSelection();
		selectionChanged(oldVal, newVal);
	}

	private void adjustLayeredSizes() {
		pnlAdvancedView.setSize(lpViews.getSize());
		pnlSimpleView.setSize(lpViews.getSize());

		pnlAdvancedView.revalidate();
		pnlSimpleView.revalidate();
	}

	private void newScalarPropertyClicked() {
		_doc.getSimpleDoc().addNewScalarProperty();
	}

	private void newArrayPropertyClicked() {
		_doc.getSimpleDoc().addNewArrayProperty();
	}
	
	private void newMoleculeClicked() {
		_doc.getSimpleDoc().addNewMolecule();
	}

	private void removeSimpleElementClicked() {
		_doc.getSimpleDoc().removeSelectedSimpleElement((MSMLSimpleTreeValue) getValToCurrentSelection());
	}

	private void openManual() {
		InputStream s = de.ukoeln.msml.genericparser.resources.Resources.class
				.getResourceAsStream("genparser-manual.pdf");
		File f = null;
		BufferedInputStream is = null;
		FileOutputStream fs = null;
		boolean success = true;
		try {
			try {
				byte[] cbuf = new byte[1024];
				f = File.createTempFile("~gp_man", ".pdf");
				f.deleteOnExit();
				is = new BufferedInputStream(s);
				fs = new FileOutputStream(f);
				int count;
				do {
					count = is.read(cbuf);
					fs.write(cbuf);
				} while (count > 0);
			} finally {
				if (fs != null)
					fs.close();
				if (is != null)
					is.close();
			}
		} catch (FileNotFoundException e) {
			success = false;
			e.printStackTrace();
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}

		if (!success)
			return;

		String start = (OsUtils.isWindows() ? "rundll32 url.dll,FileProtocolHandler " : "see ") + f.getAbsoluteFile();
		try {
			Runtime.getRuntime().exec(start);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class OsUtils {
		private static String OS = null;

		public static String getOsName() {
			if (OS == null) {
				OS = System.getProperty("os.name");
			}
			return OS;
		}

		public static boolean isWindows() {
			return getOsName().startsWith("Windows");
		}
	}

	public ITree getAdvTree() {
		return jtMSMLObjectTree;
	}

	public ITree getSimplTree() {
		return jtSimpleTree;
	}

	private void addDictrow() {
		_doc.getDictDoc().addDictRow();
	}

	private void removeDictRow() {
		_doc.getDictDoc().removeDictRow(tblDicts.getSelectedRow());
	}

	public void refreshFilter() {
		selectionChanged(null, getSelectedValue());
	}
}
