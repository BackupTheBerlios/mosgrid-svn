package de.ukoeln.msml.genericparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import de.ukoeln.msml.genericparser.classes.ClassInfo;
import de.ukoeln.msml.genericparser.classes.ClassInfoBase;
import de.ukoeln.msml.genericparser.classes.ClassInfoBaseGeneric;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPurposeVisitor;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfig;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserExtensionConfigEntry;
import de.ukoeln.msml.genericparser.gui.extension.MSMLParserExtensionTextBasedBase;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.worker.DoForAllChildrenDelegate;

public abstract class MSMLTreeValueBase {

//	private static final Logger LOGGER = LoggerFactory.getLogger(MSMLTreeValueBase.class);
	
	private DefaultMutableTreeNode _node;
	private ClassInfoBase _info;
	private String _propName;
	private GenericParserConfig _config;
	private GenericParserDocumentBase _doc;
	private boolean _isEmpty = true;
	private String _text;
	private List<MSMLTreeValueBase> _childs = new ArrayList<MSMLTreeValueBase>();
	private MSMLTreeValueBase _parent;
	
	public MSMLTreeValueBase(String propName, ClassInfoBase info, GenericParserDocumentBase doc) {
		_info = info;
		_propName = propName;
		_doc = doc;
	}

	public void setNode(DefaultMutableTreeNode node) {
		_node = node;
		_node.setUserObject(this);
		_config = createConfig();
		_config.setPropertyName(_propName);
	}

	protected GenericParserConfig createConfig() {
		return new GenericParserConfig();
	}

	public GenericParserConfig getConfig() {
		return _config;
	}
	
	public void setConfig(List<GenericParserExtensionConfig> extConfigs) {
		getConfigsToLoad().clear();
		getConfigsToLoad().addAll(extConfigs);
		updateIsEmpty();
	}

	public void setConfig(GenericParserExtensionConfig[] extConfigs) {
		setConfig(Arrays.asList(extConfigs));
	}

	/**
	 * This method is used to update the configuration within the
	 * MSMLTreeValueBase with the current values from the currently active
	 * extensions.
	 */
	public void storeConfig() {
		List<IMSMLParserExtension> exts = _doc.getExtensionsToVal(this);
		GenericParserExtensionConfig[] conf = new GenericParserExtensionConfig[exts.size()];
		_isEmpty = true;
		for (int i = 0; i < exts.size(); i++) {
			IMSMLParserExtension ext = exts.get(i);
			HashMap<String, String> extConfigs = ext.getConfig();
			ArrayList<GenericParserExtensionConfigEntry> extConfigsAsList = new ArrayList<GenericParserExtensionConfigEntry>();

			for (String k : extConfigs.keySet()) {
				GenericParserExtensionConfigEntry entry = GenericParserMainDocument.ConfigObjectFactory
						.createGenericParserExtensionConfigEntry();
				entry.setKey(k);
				entry.setVal(extConfigs.get(k));
				extConfigsAsList.add(entry);
			}

			conf[i] = GenericParserMainDocument.ConfigObjectFactory.createGenericParserExtensionConfig();
			conf[i].setExtensionName(ext.getClass().getCanonicalName());
			conf[i].getExtensionConfigs().clear();
			conf[i].getExtensionConfigs().addAll(extConfigsAsList);
		}
		setConfig(conf);
		updateIsEmpty();
	}

	/**
	 * This method instructs the currently active extensions to load their
	 * configurations. It therefore goes through all available extensions and
	 * then iterates through the configurations to find the corresponding
	 * configuration. The found configuration is then translated from the
	 * configuration-JAXB-object to the generic dictionary and passed to the
	 * extension.
	 */
	public void loadConfig() {
		List<IMSMLParserExtension> exts = _doc.getExtensionsToVal(this);
		List<GenericParserExtensionConfig> confs = getConfigsToLoad();
		_isEmpty = true;
		for (IMSMLParserExtension ext : exts) {
			ext.clear();
			String name = ext.getName();
			for (GenericParserExtensionConfig conf : confs) {
				if (conf.getExtensionName().equals(name)) {
					List<GenericParserExtensionConfigEntry> confAsList = conf.getExtensionConfigs();
					HashMap<String, String> confAsDict = new HashMap<String, String>();
					for (GenericParserExtensionConfigEntry confAsListElem : confAsList) {
						confAsDict.put(confAsListElem.getKey(), confAsListElem.getVal());
					}
					ext.loadConfig(confAsDict);
					break;
				}
			}
		}
		updateIsEmpty();
	}

	protected abstract List<GenericParserExtensionConfig> getConfigsToLoad();

	private void updateIsEmpty() {
		if (getConfig() == null) {
			_isEmpty = true;
			return;
		}	
		List<IMSMLParserExtension> exts = _doc.getExtensionsToVal(this);
		boolean isEmpty = true;
		for (IMSMLParserExtension ext : exts) {
			isEmpty = isEmpty & ext.isEmpty(getConfig());
			if (!isEmpty)
				break;
		}
		_isEmpty = isEmpty;
	}

	public boolean isEmpty() {
		return _isEmpty;
	}

	public void deleteAllChilds() {
		List<MSMLTreeValueBase> childs = new ArrayList<MSMLTreeValueBase>();
		for (MSMLTreeValueBase child : _childs) {
			childs.add(child);
		}
		for (MSMLTreeValueBase child : childs) {
			child.remove();
		}
	}

	public void addOneChild(final AfterCreationAction action, final ClassInfoBase inf) {
		new Thread() {
			@Override
			public void run() {
				ClassInfoBase info = getInfoToAdd(inf);
				if (info == null)
					return;
				MSMLTreeValueBase newVal = createAndAddOneNode(info, "[" + getDocument().getNextChildIndex(getNode()) + "]");
				if (action == AfterCreationAction.SELECT)
					getDocument().selectNode(newVal.getNode());
				if (action == AfterCreationAction.OPENPATH)
					getDocument().openPathTo(newVal.getNode());
				getDocument().repaintTree();
			}
		}.start();		
	}
	
	protected abstract ClassInfoBase getInfoToAdd(final ClassInfoBase inf);
	
	public void addOneChild(final AfterCreationAction action) {
		addOneChild(action, null);
	}

	public MSMLTreeValueBase createAndAddOneNode(ClassInfoBase info, String prop) {
		return createAndAddOneNode(info, prop, true);
	}

	private MSMLTreeValueBase createAndAddOneNode(ClassInfoBase info, String prop, boolean addToConfig) {
		return createAndAddOneNode(info, prop, addToConfig, true);
	}

	public MSMLTreeValueBase createAndAddOneNode(ClassInfoBase info, String prop, boolean addToConfig, boolean repaint) {
		DefaultMutableTreeNode child = new DefaultMutableTreeNode();
		MSMLTreeValueBase val = createNewValue(prop, info);
		val.setNode(child);
		_childs.add(val);
		val._parent = this;
		if (repaint)
			_doc.addOneNode(_node, child);
		else
			_node.add(child);
		val.buildUp();
		if (addToConfig)
			_config.getChildConfig().add(val.getConfig());
		return val;
	}

	protected abstract void buildUp();

	protected abstract MSMLTreeValueBase createNewValue(String prop, ClassInfoBase info);

	public void remove() {
		if (_parent == null)
			return;
		
		_parent._childs.remove(this);
		_parent.getConfig().getChildConfig().remove(_config);
		_doc.removeFromData(this);
	}

	public void loadConfigIntoNodes(GenericParserConfig config) {
		_config = config;
		if (_info.isListType()) {
			for (GenericParserConfig childConf : _config.getChildConfig()) {
				MSMLTreeValueBase val = createAndAddOneNode(
						ClassInfo.ClassInfoFactory.getClassInfo(childConf.getCanonicalClassName()),
						childConf.getPropertyName(), false);
				val.loadConfigIntoNodes(childConf);
				val.loadConfig();
			}
		} else {
			for (final GenericParserConfig childConf : _config.getChildConfig()) {
				_doc.doForAllChildren(new DoForAllChildrenDelegate(_node) {

					@Override
					public boolean doAndContinue(DefaultMutableTreeNode child) {
						MSMLTreeValueBase val = (MSMLTreeValueBase) child.getUserObject();
						if (!val.getConfig().getPropertyName().equals(childConf.getPropertyName()))
							return true;
						val.loadConfigIntoNodes(childConf);
						val.loadConfig();
						return false;
					}
				});
			}
		}
	}

	public abstract void setInObj(ClassInfoBaseGeneric<?, ?> holder);
	
	public String getParentsText() {
		return getDocument().getTextToVal(this.getParentsValue());
	}
		
	public MSMLTreeValueBase getParentsValue() {
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getNode().getParent();
		if (parent == null)
			return null;
		MSMLTreeValueBase parentVal = (MSMLTreeValueBase) parent.getUserObject();
		return parentVal;
	}
	
	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public void selectNode() {
		_doc.selectNode(_node);
	}

	private void getCleanedConfigs(GenericParserConfig parentConf) {
		GenericParserConfig copyConf = copyConfsWithoutChilds(getConfig());
		updateIsEmpty();
		if (!isEmpty() || _node.getChildCount() > 0)
			parentConf.getChildConfig().add(copyConf);

		for (int i = 0; i < _node.getChildCount(); i++) {
			MSMLTreeValueBase childVal = (MSMLTreeValueBase) ((DefaultMutableTreeNode) _node.getChildAt(i))
					.getUserObject();
			childVal.getCleanedConfigs(copyConf);
		}
	}

	public GenericParserConfig cleanConfigs() {
		GenericParserConfig conf = copyConfsWithoutChilds(getConfig());
		if (_node.getChildCount() == 0)
			return conf;
		for (int i = 0; i < _node.getChildCount(); i++) {
			MSMLTreeValueBase childVal = (MSMLTreeValueBase) ((DefaultMutableTreeNode) _node.getChildAt(i)).getUserObject();
			childVal.getCleanedConfigs(conf);			
		}
		return conf;
	}

	private GenericParserConfig copyConfsWithoutChilds(GenericParserConfig conf) {
		GenericParserConfig copy = createConfig();
		copy.setCanonicalClassName(conf.getCanonicalClassName());
		copy.setPropertyName(conf.getPropertyName());
		for (GenericParserExtensionConfig extConf : conf.getExtensionConfigCollection()) {
			if (!_doc.isExtConfigEmpty(conf, extConf.getExtensionName()))
				copy.getExtensionConfigCollection().add(extConf);
		}
		return copy;
	}
	
	protected abstract void afterCopyConfs(GenericParserConfig conf, GenericParserConfig copy);

	protected GenericParserDocumentBase getDocument() {
		return _doc;
	}

	protected DefaultMutableTreeNode getNode() {
		return _node;
	}

	public ClassInfoBaseGeneric<?, ?> getInfo() {
		return (ClassInfoBaseGeneric<?, ?>) _info;
	}

	public String getPropName() {
		return _propName;
	}
	
	public List<MSMLTreeValueBase> getChilds() {
		return _childs;
	}
	
	public boolean isRoot() {
		return false;
	}

	public abstract boolean getExtensionEnabled(
			MSMLParserExtensionTextBasedBase ext, 
			GeneralPurposeVisitor visitor);

	public String getPrefixToDict(String dict) {
		return getDocument().getPrefixToDict(dict);
	}
}
