package de.ukoeln.msml.genericparser;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.jaxb.bindings.Cml;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.wrapper.JobFinalization;
import de.ukoeln.msml.genericparser.classes.ArrayPropertyClassInfo;
import de.ukoeln.msml.genericparser.classes.ClassInfo;
import de.ukoeln.msml.genericparser.classes.ClassInfo.ClassInfoFactory;
import de.ukoeln.msml.genericparser.classes.ClassInfoBase;
import de.ukoeln.msml.genericparser.classes.LayerPropertyClassInfo;
import de.ukoeln.msml.genericparser.classes.MoleculeClassInfo;
import de.ukoeln.msml.genericparser.classes.PropertyClassInfo;
import de.ukoeln.msml.genericparser.classes.SimpleRootInfo;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigSimplType;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.interfaces.ITree;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.TreeModelSwingImpl;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;
import de.ukoeln.msml.genericparser.worker.StringH;

public class GenericParserDocumentSimple extends GenericParserDocumentBase {

	private TreeModelSwingImpl jtmSimpleData;

	public GenericParserDocumentSimple(GenericParserMainDocument genericParserMainDocument) {
		super(genericParserMainDocument);
	}

	public void addNewScalarProperty() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmSimpleData.getRoot();
		ClassInfoBase info = ClassInfo.ClassInfoFactory.getClassInfo(PropertyClassInfo.class);
		((MSMLSimpleTreeValue) root.getUserObject()).addOneChild(AfterCreationAction.SELECT, info);
	}
	
	public void addNewMolecule() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmSimpleData.getRoot();
		MSMLSimpleTreeValue rootVal = (MSMLSimpleTreeValue) root.getUserObject();
		ClassInfoBase info = ClassInfo.ClassInfoFactory.getClassInfo(MoleculeClassInfo.class);
		rootVal.addOneChild(AfterCreationAction.SELECT, info);
	}

	public void addNewArrayProperty() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmSimpleData.getRoot();
		ClassInfoBase info = ClassInfo.ClassInfoFactory.getClassInfo(ArrayPropertyClassInfo.class);
		((MSMLSimpleTreeValue) root.getUserObject()).addOneChild(AfterCreationAction.SELECT, info);
	}
	
	public void addLayer() {
		MSMLTreeValueBase selval = getMainView().getSelectedValue();
		ClassInfoBase conf = selval.getInfo();
		if (!(conf instanceof ArrayPropertyClassInfo) && !(conf instanceof LayerPropertyClassInfo))
			return;
		if (conf instanceof LayerPropertyClassInfo)
			selval = selval.getParentsValue();
		selval.createAndAddOneNode(ClassInfoFactory.getClassInfo(LayerPropertyClassInfo.class), 
				"Layer "+ getNextChildIndex(selval.getNode()), true, true);
	}

	@Override
	public TreeModelSwingImpl getData() {
		return jtmSimpleData;
	}

	public void loadConfig(ConfigSimplType rootConfig) {
		MSMLSimpleTreeValue rootval = ((MSMLSimpleTreeValue) ((DefaultMutableTreeNode) jtmSimpleData.getRoot())
				.getUserObject());

		rootval.deleteAllChilds();
		rootval.setConfig(rootConfig.getExtensionConfigCollection());
		rootval.getClassInfo().buildUp(rootval, rootConfig);
		if (GenericParserMainDocument.useX()) {
			repaintTree();
			rootval.selectNode();
		}
		rootval.loadConfig();
	}

	@Override
	public void afterInit() {
		((MSMLSimpleTreeValue) ((DefaultMutableTreeNode) jtmSimpleData.getRoot()).getUserObject()).selectNode();
	}

	@Override
	public MSMLTreeValueBase createNewValue(String prop, ClassInfo info, GenericParserDocumentBase _doc) {
		return new MSMLSimpleTreeValue(prop, info, this);
	}

	@Override
	public void Init(StartupParams params) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		MSMLSimpleTreeValue rootVal = new MSMLSimpleTreeRootValue("ROOT",
				ClassInfo.ClassInfoFactory.getClassInfo(SimpleRootInfo.class), this);

		jtmSimpleData = new TreeModelSwingImpl(root);
		jtmSimpleData.setRoot(root);

		rootVal.setNode(root);
	}

	public IMSMLExtensionVisitor runValueRetrieverVisitor(MSMLTreeValueBase value) {
		IMSMLExtensionVisitor visit = value.getInfo().runVisitor(this, value);
		return visit;
	}

	public ConfigSimplType getConfig() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmSimpleData.getRoot();
		MSMLTreeValueBase val = (MSMLTreeValueBase) root.getUserObject();
		GenericParserConfig ownConfigs = val.cleanConfigs();
		return (ConfigSimplType) ownConfigs;
	}

	@SuppressWarnings("unchecked")
	public void adjustCml(Cml cml) {
		JobListEditor jle = new JobListEditor(cml);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmSimpleData.getRoot();
		MSMLSimpleTreeValue rootval = (MSMLSimpleTreeValue) root.getUserObject();
		((SimpleRootInfo) rootval.getInfo()).updateAllFromRoot(rootval);

		JobFinalization finalBlock = jle.getJobListElement().getJobs().get(0).getFinalization();
		// move that into class-info
		for (Object obj : (List<Object>) rootval.getClassInfo().getObject()) {
			if (obj instanceof PropertyType) {
				finalBlock.getPropertyList().getProps().add((PropertyType)obj);
			} else if (obj instanceof MoleculeType) {
				finalBlock.setMolecule((MoleculeType) obj);
			} else {
				StackTraceHelper.handleException(new Throwable("New type needed."), ON_EXCEPTION.CONTINUE);
			}
		}
	}

	public void removeSelectedSimpleElement(MSMLSimpleTreeValue val) {
		val.remove();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmSimpleData.getRoot();
		MSMLSimpleTreeValue rootVal = (MSMLSimpleTreeValue) root.getUserObject();
		rootVal.selectNode();
	}

	@Override
	public ITree getTree() {
		return getMainView().getSimplTree();
	}

	public String getTextToVal(MSMLTreeValueBase val) {
		if (!StringH.isNullOrEmpty(val.getText()))
			return val.getText();
		IMSMLExtensionVisitor res = runValueRetrieverVisitor(val);
		return res.getText();
	}

	public void resetLayer() {
		MSMLSimpleTreeValue val = (MSMLSimpleTreeValue) getMainView().getSelectedValue();
		val.setSelectedLayerIndex(-1);
		getMainView().refreshFilter();
	}

	public void selectLayer(int rowAtPoint) {
		MSMLSimpleTreeValue val = (MSMLSimpleTreeValue) getMainView().getSelectedValue();
		val.setSelectedLayerIndex(rowAtPoint);
		getMainView().refreshFilter();
	}

}
