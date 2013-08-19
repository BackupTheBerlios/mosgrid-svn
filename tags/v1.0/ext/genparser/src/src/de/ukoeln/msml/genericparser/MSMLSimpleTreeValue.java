package de.ukoeln.msml.genericparser;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import de.ukoeln.msml.genericparser.classes.ClassInfoBase;
import de.ukoeln.msml.genericparser.classes.ClassInfoBaseGeneric;
import de.ukoeln.msml.genericparser.classes.CustomClassInfo;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigSimplType;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfig;
import de.ukoeln.msml.genericparser.gui.configuration.LayerType;
import de.ukoeln.msml.genericparser.gui.extension.MSMLParserExtensionTextBasedBase;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;

public class MSMLSimpleTreeValue extends MSMLTreeValueBase {

	private int _selectedLayer;

	public MSMLSimpleTreeValue(String propName, ClassInfoBase info, GenericParserDocumentBase doc) {
		super(propName, info, doc);
		_selectedLayer = -1;
	}

	@Override
	protected void buildUp() {
		getInfo().init(this);
	}

	@Override
	protected MSMLTreeValueBase createNewValue(String prop, ClassInfoBase info) {
		return new MSMLSimpleTreeValue(prop, info, getDocument());
	}

	@Override
	protected ConfigSimplType createConfig() {
		return new ConfigSimplType();
	}

	@Override
	public void setInObj(ClassInfoBaseGeneric<? extends IMSMLExtensionVisitor, ?> holder) {
		@SuppressWarnings("unchecked")
		List<Object> objs = (List<Object>) holder.getObject();
		IMSMLExtensionVisitor res = getCastDocument().runValueRetrieverVisitor(this);
		getClassInfo().absorbVisitor(res);
		objs.add(getClassInfo().getObject());
	}

	private GenericParserDocumentSimple getCastDocument() {
		return (GenericParserDocumentSimple) getDocument();
	}

	@Override
	protected ClassInfoBase getInfoToAdd(final ClassInfoBase inf) {
		return inf;
	}

	@SuppressWarnings("unchecked")
	public CustomClassInfo<IMSMLExtensionVisitor, ?> getClassInfo() {
		return (CustomClassInfo<IMSMLExtensionVisitor, ?>) getInfo();
	}

	@Override
	public boolean getExtensionEnabled(MSMLParserExtensionTextBasedBase ext, GeneralPurposeVisitor visitor) {
		return true;
	}

	@Override
	public String toString() {
		return getInfo().getName() + "  (" + getPropName() + ")"; // TODO if ready: add value
	}

	@Override
	public void setNode(DefaultMutableTreeNode node) {
		super.setNode(node);
		getConfig().setCanonicalClassName(getInfo().getClass().getCanonicalName());
	}

	public void setSelectedLayerIndex(int i) {
		storeConfig();
		_selectedLayer = i;
	}

	public int getSelectedLayerIndex() {
		return _selectedLayer;
	}

	@Override
	protected List<GenericParserExtensionConfig> getConfigsToLoad() {
		if (_selectedLayer == -1)
			return getConfig().getExtensionConfigCollection();
		return ((ConfigSimplType) getConfig()).getLayerConfigs().get(_selectedLayer).getLayerConfig();
	}

	@Override
	protected void afterCopyConfs(GenericParserConfig conf, GenericParserConfig copy) {
		ConfigSimplType cconf = (ConfigSimplType) conf;
		ConfigSimplType ccopy = (ConfigSimplType) copy;
		for (LayerType lconf : cconf.getLayerConfigs()) {
			ccopy.getLayerConfigs().add(lconf);
		}
	}
}
