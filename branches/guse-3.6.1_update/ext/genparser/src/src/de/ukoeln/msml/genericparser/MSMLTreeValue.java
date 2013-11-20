package de.ukoeln.msml.genericparser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.ClassInfo;
import de.ukoeln.msml.genericparser.classes.ClassInfoBase;
import de.ukoeln.msml.genericparser.classes.ClassInfoBaseGeneric;
import de.ukoeln.msml.genericparser.classes.ListClassInfo;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPuroposeVistorMode;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfigEntry;
import de.ukoeln.msml.genericparser.gui.extension.MSMLElementSeparatorExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLListExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLListExtension.ListMode;
import de.ukoeln.msml.genericparser.gui.extension.MSMLParserExtensionTextBasedBase;
import de.ukoeln.msml.genericparser.worker.ConfigHelper;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLTreeValue extends MSMLTreeValueBase {

	public MSMLTreeValue(String propName, ClassInfoBase info, GenericParserDocumentBase doc) {
		super(propName, info, doc);
	}

	public ClassInfo getClassInfo() {
		return (ClassInfo) getInfo();
	}

	@Override
	public void buildUp() {
		if (getClassInfo().isPrimitive() || getClassInfo().isListType())
			return;

		Hashtable<String, Field> props = getClassInfo().getProperties();

		for (String prop : props.keySet()) {

			ClassInfo info = ClassInfo.ClassInfoFactory.getClassInfo(props.get(prop));
			createAndAddOneNode(info, prop);
		}
	}

	@Override
	public void setNode(DefaultMutableTreeNode node) {
		super.setNode(node);
		if (!getClassInfo().isPrimitive())
			getConfig().setCanonicalClassName(getClassInfo().getClassObj().getCanonicalName());

	}

	@Override
	public String toString() {
		String type = "";
		if (getClassInfo().getType().is(CLASSTYPE.MSML))
			type = "*";
		if (getClassInfo().getType().is(CLASSTYPE.LIST))
			type = "[]";

		return getPropName() + " (" + getClassInfo().getName() + type + ")"; 
		// TODO if ready add value
	}

	public boolean parentIsFixedList() {
		MSMLTreeValue parentVal = (MSMLTreeValue) getParentsValue();
		if (parentVal == null)
			return false;
		if (!parentVal.getClassInfo().isListType())
			return false;
		return !MSMLListExtension.isDynamicList(parentVal.getConfig());
	}

	public boolean isDynamicsListChild() {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getNode().getParent();
		if (parent == null)
			return false;
		MSMLTreeValue parentVal = (MSMLTreeValue) parent.getUserObject();
		return parentVal.isDynamicList();
	}

	private boolean isDynamicList() {
		return getClassInfo().isListType() && MSMLListExtension.isDynamicList(getConfig());
	}

	private GenericParserDocument getCastDocument() {
		return (GenericParserDocument) getDocument();
	}

	public String[] getParentsTextAsDynamicList() {
		MSMLTreeValue parent = (MSMLTreeValue) getParentsValue();
		if (parent == null)
			return new String[0];

		return parent.getSeparatedText();
	}

	private String[] getSeparatedText() {
		String text = getCastDocument().getTextToVal(this);
		GenericParserExtensionConfigEntry conf = ConfigHelper.getExtensionConfigEntry(getConfig(),
				MSMLElementSeparatorExtension.class, MSMLElementSeparatorExtension.SEPARATORPATTERN);
		String[] res = text.split(conf.getVal());
		ArrayList<String> resultWithoutEmpties = new ArrayList<String>();
		for (String s : res) {
			if (!StringH.isNullOrEmpty(s)) {
				resultWithoutEmpties.add(s);
			}
		}
		String[] realRes = new String[resultWithoutEmpties.size()];
		realRes = resultWithoutEmpties.toArray(realRes);
		return realRes;
	}

	@Override
	protected MSMLTreeValueBase createNewValue(String prop, ClassInfoBase info) {
		return new MSMLTreeValue(prop, info, getDocument());
	}

	@Override
	public void setInObj(ClassInfoBaseGeneric<?, ?> holder) {
		ClassInfo inf = (ClassInfo) holder;
		// TODO speed up and find a better way. maybe introduce subclasses of
		// MSMLTreeValue
		// move childstuff into helper or so.
		if (isEmpty() && getNode().getChildCount() == 0)
			return;

		if (getClassInfo().isListType())
			inf.setValue(getPropName(), getClassInfo());

		getClassInfo().clearObject();

		// list or msml => create object and fill
		if (getNode().getChildCount() > 0) {
			if (isDynamicList()) {
				String[] texts = getSeparatedText();
				MSMLTreeValue childValAtZero = (MSMLTreeValue) ((DefaultMutableTreeNode) getNode().getChildAt(0))
						.getUserObject();
				childValAtZero.setText(texts[0]);
				childValAtZero.doSetInObj(getClassInfo());
				for (int i = 0; i < texts.length - 1; i++) {
					MSMLTreeValue tempVal = (MSMLTreeValue) createAndAddOneNode(
							ClassInfo.ClassInfoFactory.getClassInfo(childValAtZero.getClassInfo().getClassObj()), "["
									+ (i + 1) + "]", false, false);
					tempVal.setText(texts[i + 1]);
					tempVal.loadConfigIntoNodes(childValAtZero.getConfig());
					loadConfig();
					tempVal.doSetInObj(getClassInfo());
					getNode().remove(getNode().getIndex(tempVal.getNode()));
				}
			} else {
				for (int i = 0; i < getNode().getChildCount(); i++) {
					MSMLTreeValue childVal = (MSMLTreeValue) ((DefaultMutableTreeNode) getNode().getChildAt(i))
							.getUserObject();
					childVal.doSetInObj(getClassInfo());
				}
			}
		} else { // primitive
			getClassInfo().updateObject(getCastDocument().getTextToVal(this));
		}
	}

	private void doSetInObj(ClassInfo holder) {
		setInObj(holder);

		if (!getClassInfo().isListType())
			holder.setValue(getPropName(), getClassInfo());
	}

	@Override
	protected ClassInfoBase getInfoToAdd(final ClassInfoBase inf) {
		ClassInfoBase info;
		if (getClassInfo().isUntypedNonAnyList()) {
			List<ClassInfoBase> posInfos = ((ListClassInfo) getClassInfo()).getPossibleTypes();
			ClassInfo[] infos = new ClassInfo[posInfos.size()];
			posInfos.toArray(infos);

			info = (ClassInfo) JOptionPane.showInputDialog(null, "Choose type for the element: ", "Type Chooser",
					JOptionPane.PLAIN_MESSAGE, null, infos, infos[0]);

			if (info == null)
				return null;
		} else {
			info = ((ListClassInfo) getClassInfo()).getPossibleTypes().get(0);
		}
		return info;
	}

	@Override
	public boolean getExtensionEnabled(MSMLParserExtensionTextBasedBase ext, GeneralPurposeVisitor visitor) {
		if (visitor.getMode() == GeneralPuroposeVistorMode.LISTMODECHANGED
				|| (visitor.getMode() == GeneralPuroposeVistorMode.SELECTIONCHANGED && getClassInfo().isListType())) {
			return visitor.getNewListMode() == ListMode.Dynamic;
		}
		return true;
	}

	@Override
	protected GenericParserConfig createConfig() {
		return new GenericParserConfig();
	}

	@Override
	protected List<GenericParserExtensionConfig> getConfigsToLoad() {
		return getConfig().getExtensionConfigCollection();
	}

	@Override
	protected void afterCopyConfs(GenericParserConfig conf, GenericParserConfig copy) {
	}
}
