package de.ukoeln.msml.genericparser;

import javax.swing.tree.DefaultMutableTreeNode;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.jaxb.bindings.Cml;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.jaxb.bindings.PropertyListType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobFinalization;
import de.mosgrid.msml.util.wrapper.PropertyListWrapper;
import de.ukoeln.msml.genericparser.classes.ClassInfo;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigAdvType;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.interfaces.ITree;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.TreeModelSwingImpl;
import de.ukoeln.msml.genericparser.worker.DoForAllChildrenDelegate;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;
import de.ukoeln.msml.genericparser.worker.StringH;

public class GenericParserDocument extends GenericParserDocumentBase {

	private TreeModelSwingImpl jtmData;
	private boolean _persistingText = false;

	public GenericParserDocument(GenericParserMainDocument genericParserMainDocument) {
		super(genericParserMainDocument);
	}

	@Override
	public void Init(StartupParams params) {

		// init advanced view
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		MSMLTreeValue rootVal = new MSMLAdvTreeRootValue("ROOT", ClassInfo.ClassInfoFactory.getRootClassInfo(), this);

		jtmData = new TreeModelSwingImpl(root);
		jtmData.setRoot(root);

		rootVal.setNode(root);
	}

	public Cml getCml() {
		Cml cml = getCml(ON_EXCEPTION.QUIT);
		JobListEditor jle = new JobListEditor(cml);
		if (jle.getJobListElement().getJobs().size() == 0) {
			jle.getJobListElement().addNewJob();
		}
		Job job = jle.getJobListElement().getJobs().get(0);
		if (job.getFinalization() == null) {
			ModuleType mod = new ModuleType();
			mod.setDictRef("compchem:" + JobFinalization.ELEMENT_ID);
			JobFinalization jf = new JobFinalization(mod, jle);
			job.setFinalization(jf);
		}
		if (job.getFinalization().getPropertyList() == null) {
			job.getFinalization().setPropertyList(new PropertyListWrapper(jle, new PropertyListType()));
		}
		return cml;
	}

	private Cml getCml(ON_EXCEPTION onparsefail) {
		ClassInfo cmlInfo = null;
		_persistingText = true;
		try {
			MSMLTreeValue val = (MSMLTreeValue) ((DefaultMutableTreeNode) jtmData.getRoot()).getUserObject();
			cmlInfo = val.getClassInfo();
			cmlInfo.clearObject();
			val.setInObj(cmlInfo);
			MSMLTreeValueBase selVal = GenericParserMainDocument.useX() ? 
					getMainView().getSelectedValue() : 
						(MSMLTreeValueBase) ((DefaultMutableTreeNode) jtmData.getRoot()).getUserObject(); 
			if (selVal != null)
				selVal.loadConfig();
		} catch (Exception e) {
			StackTraceHelper.handleException(e, onparsefail);
			return null;
		} finally {
			_persistingText = false;
			unsetTextOfAllNodes(); // cleanup
		}
		return (Cml) cmlInfo.getObject();
	}

	private void unsetTextOfAllNodes() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmData.getRoot();
		unsetTextOfAllChilds(root);
		((MSMLTreeValue) root.getUserObject()).setText(null);
	}

	private void unsetTextOfAllChilds(DefaultMutableTreeNode root) {
		doForAllChildren(new DoForAllChildrenDelegate(root) {
			@Override
			public boolean doAndContinue(DefaultMutableTreeNode child) {
				((MSMLTreeValue) child.getUserObject()).setText(null);
				unsetTextOfAllChilds(child);
				return true;
			}
		});
	}

	public String getTextToVal(MSMLTreeValueBase value) {
		MSMLTreeValue val = (MSMLTreeValue) value;
		if (val == null)
			return ""; // TODO message
		if (_persistingText && !StringH.isNullOrEmpty(val.getText()))
			return val.getText();

		ClassInfo inf = val.getClassInfo();

		MSMLExtensionVisitor visit = inf.runVisitor(this, value);
		String text = visit.getText();
		if (_persistingText)
			val.setText(text);
		return text;
	}

	public boolean isListElementOfFixedList(MSMLTreeValueBase value) {
		return ((MSMLTreeValue)value).parentIsFixedList();
	}

	public void addNewChild(MSMLTreeValue val, AfterCreationAction action) {
		val.addOneChild(action);
	}

	public void removeNode(MSMLTreeValue val) {
		val.remove();
	}

	public boolean getInMSMLSaveMode() {
		return _persistingText;
	}

	@Override
	public boolean isExtConfigEmpty(GenericParserConfig conf, String extName) {
		IMSMLParserExtension foundExt = null;
		for (IMSMLParserExtension ext : getExtensions()) {
			if (extName.equals(ext.getName())) {
				foundExt = ext;
				break;
			}
		}
		if (foundExt == null) {
			StackTraceHelper.handleException(new Throwable("Extension not found: " + extName), ON_EXCEPTION.CONTINUE);
			return false;
		}
		return foundExt.isEmpty(conf);
	}

	public void loadConfig(ConfigAdvType rootConfig) {
		// reloading model
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmData.getRoot();
		MSMLTreeValue rootVal = (MSMLTreeValue) root.getUserObject();
		rootVal.deleteAllChilds();
//		removeAllChildren(root);
		rootVal.buildUp();
		rootVal.loadConfigIntoNodes(rootConfig);
		if (GenericParserMainDocument.useX()) {
			getMainView().selectNode(root, this);
			getMainView().repaintTree(this);			
		}
	}

	@Override
	public TreeModelSwingImpl getData() {
		return jtmData;
	}

	@Override
	public void afterInit() {
		repaintTree();
	}

	@Override
	public MSMLTreeValueBase createNewValue(String prop, ClassInfo info, GenericParserDocumentBase _doc) {
		return new MSMLTreeValue(prop, info, this);
	}

	public ConfigAdvType getConfig() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jtmData.getRoot();
		MSMLTreeValue val = (MSMLTreeValue) root.getUserObject();
		return (ConfigAdvType) val.cleanConfigs();
	}

	@Override
	public ITree getTree() {
		return getMainView().getAdvTree();
	}
}
