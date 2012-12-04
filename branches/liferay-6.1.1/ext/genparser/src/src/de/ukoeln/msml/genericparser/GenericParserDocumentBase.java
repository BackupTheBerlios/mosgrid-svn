package de.ukoeln.msml.genericparser;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import de.ukoeln.msml.genericparser.classes.ClassInfo;
import de.ukoeln.msml.genericparser.classes.ClassInfoBase;
import de.ukoeln.msml.genericparser.gui.GenericParserMainFrame;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.extension.MSMLParserExtensionBase;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.interfaces.ITree;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.TreeModelSwingImpl;
import de.ukoeln.msml.genericparser.worker.DoForAllChildrenDelegate;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public abstract class GenericParserDocumentBase {

	private GenericParserMainDocument _doc;

	public GenericParserDocumentBase(GenericParserMainDocument genericParserMainDocument) {
		_doc = genericParserMainDocument;
	}

	public void doForAllChildren(DoForAllChildrenDelegate del) {
		@SuppressWarnings("unchecked")
		Enumeration<? extends DefaultMutableTreeNode> childs = (Enumeration<? extends DefaultMutableTreeNode>) del
				.getNode().children();
		while (childs.hasMoreElements()) {
			if (!del.doAndContinue(childs.nextElement()))
				break;
		}
	}

	protected List<MSMLParserExtensionBase> getExtensions() {
		return _doc.getExtensions();
	}

	public List<IMSMLParserExtension> getExtensionsToVal(MSMLTreeValueBase val) {
		return _doc.getExtensionsToVal(val);
	}

	protected GenericParserMainFrame getMainView() {
		return _doc.getMainView();
	}

	public int getNextChildIndex(DefaultMutableTreeNode parent) {
		int index = parent.getChildCount() == 0 ? 0 : parent.getIndex(parent.getLastChild()) + 1;
		return index;
	}

	protected StartupParams getParams() {
		return _doc.getParams();
	}

	public boolean isExtConfigEmpty(GenericParserConfig conf, String extName) {
		MSMLParserExtensionBase foundExt = null;
		for (MSMLParserExtensionBase ext : getExtensions()) {
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

	public final void repaintTree() {
		getMainView().repaintTree(this);
	}

	public final void selectNode(DefaultMutableTreeNode node) {
		getMainView().selectNode(node, this);
	}

	public final void openPathTo(DefaultMutableTreeNode node) {
		getMainView().openPathTo(node, this);
	}

	public final void addOneNode(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
		int index = getNextChildIndex(parent);
		getData().insertNodeInto(child, parent, index);
	}

	public final void removeAllChildren(DefaultMutableTreeNode _node) {
		doForAllChildren(new DoForAllChildrenDelegate(_node) {
			@Override
			public boolean doAndContinue(DefaultMutableTreeNode child) {
				getData().removeNodeFromParent(child);
				return true;
			}
		});
	}

	public final void removeFromData(MSMLTreeValueBase child) {
		getData().removeNodeFromParent(child.getNode());
		if (getMainView() != null)
			getMainView().repaintTree(this);
	}

	protected boolean componentApplysToClassInfo(IMSMLParserExtension ext, ClassInfoBase inf, MSMLTreeValueBase val) {
		return _doc.componentApplysToClassInfo(ext, inf, val);
	}

	protected String getNamespaceToDictref(String dictRef) {
		return _doc.getDictDoc().getNamespaceToDictref(dictRef);
	}

	public abstract void afterInit();

	public abstract MSMLTreeValueBase createNewValue(String prop, ClassInfo info, GenericParserDocumentBase _doc);

	public abstract TreeModelSwingImpl getData();

	public abstract void Init(StartupParams params);

	public abstract ITree getTree();

	public abstract String getTextToVal(MSMLTreeValueBase parentsValue);

	public String getPrefixToDict(String dict) {
		return _doc.getDictDoc().getData().getPrefixToDict(dict);
	}
}
