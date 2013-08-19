package de.mosgrid.ukoeln.templatedesigner.document;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;

import de.mosgrid.msml.enums.DictDir;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.jaxb.bindings.AdapterConfigType;
import de.mosgrid.msml.jaxb.bindings.DescriptionType;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.jaxb.bindings.JobInputUploadType;
import de.mosgrid.msml.jaxb.bindings.MoleculeUploadType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobParserConfig;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.ukoeln.templatedesigner.asm.MGASMJob;
import de.mosgrid.ukoeln.templatedesigner.asm.MGASMPort;
import de.mosgrid.ukoeln.templatedesigner.document.IPropertyBean.DictChangeListener;
import de.mosgrid.ukoeln.templatedesigner.document.IPropertyBean.RefChangeListener;
import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateDocument.TemplateJobBean;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateViewJob;
import de.mosgrid.ukoeln.templatedesigner.helper.ConfigBean;
import de.mosgrid.ukoeln.templatedesigner.helper.ConfigDir;
import de.mosgrid.ukoeln.templatedesigner.helper.StringH;

public class TDTemplateJobDocument extends TDTemplateDocumentBase<TDTemplateViewJob> {

	private final Logger LOGGER = LoggerFactory.getLogger(TDTemplateJobDocument.class);
	private final BeanItemContainer<PropertyBean> _envParamDataSource;
	private final ObjectProperty<Boolean> _hasEnvironment;

	private final BeanItemContainer<ParameterBean> _adapParamDataSource;
	private final ObjectProperty<Boolean> _hasAdapter;
	private final BeanItemContainer<MGASMPort> _portDataSource;
	private final BeanItemContainer<String> _stringPortDataSource;
	private BeanItemContainer<IDictionary> _pseudoAdapterDictionaryDataSource;

	private final BeanItemContainer<ParameterBean> _parameterDataSource;
	private final ObjectProperty<Boolean> _hasParameter;

	private final BeanItemContainer<UploadsBean> _uploadsDataSource;
	private final ObjectProperty<Boolean> _hasUploads;

	private final ObjectProperty<Boolean> _hasParser;
	private final ObjectProperty<String> _parserOutputFileNameDataSource;
	private final BeanItemContainer<ConfigBean> _availableParserConfigsDataSource;
	private final BeanItemContainer<ConfigBean> _addedParserConfigsDataSource;

	private final Job _job;
	private final MGASMJob _asmJob;
	private final ObjectProperty<String> _descriptionDataSource;
	private final ObjectProperty<String> _selectedAdapterIDDataSource;
	private final ObjectProperty<MGASMPort> _selectedAdapterPortDataSource;
	private final ObjectProperty<String> _selectedAdapterExtensionDataSource;

	public TDTemplateJobDocument(TemplateJobBean tb) {
		super(tb);

		_job = tb.getJob();
		_asmJob = tb.getAsmJob();

		_envParamDataSource = new BeanItemContainer<PropertyBean>(PropertyBean.class);
		_hasEnvironment = new ObjectProperty<Boolean>(hasEnvironment());

		_adapParamDataSource = new BeanItemContainer<ParameterBean>(ParameterBean.class);
		_portDataSource = new BeanItemContainer<MGASMPort>(MGASMPort.class);
		_stringPortDataSource = new BeanItemContainer<String>(String.class);
		_hasAdapter = new ObjectProperty<Boolean>(hasAdapter());
		_pseudoAdapterDictionaryDataSource = new BeanItemContainer<IDictionary>(IDictionary.class);

		_parameterDataSource = new BeanItemContainer<ParameterBean>(ParameterBean.class);
		_hasParameter = new ObjectProperty<Boolean>(hasParameter());

		_uploadsDataSource = new BeanItemContainer<UploadsBean>(UploadsBean.class);
		_hasUploads = new ObjectProperty<Boolean>(hasUploads());

		_hasParser = new ObjectProperty<Boolean>(hasParser());
		_availableParserConfigsDataSource = new BeanItemContainer<ConfigBean>(ConfigBean.class);
		_addedParserConfigsDataSource = new BeanItemContainer<ConfigBean>(ConfigBean.class);
		_parserOutputFileNameDataSource = new ObjectProperty<String>("");

		_selectedAdapterIDDataSource = new ObjectProperty<String>("");
		_selectedAdapterPortDataSource = new ObjectProperty<MGASMPort>(null, MGASMPort.class);
		_selectedAdapterExtensionDataSource = new ObjectProperty<String>("");
		
		String description = getDescription();
		_descriptionDataSource = new ObjectProperty<String>(description);
	}

	private String getDescription() {
		DescriptionType desc = getJob().getDescription();
		if (desc == null)
			return "";
		if (desc.getAny() != null && desc.getAny().size() > 0) {
			LOGGER.debug("Cannot parse HTML description. This will not be touched.");
			return "";
		}
		if (desc.getPlainText() == null)
			return "";
		return desc.getPlainText();
	}

	@Override
	void doInit() {

		if (getASMJob() != null) {
			_portDataSource.addAll(getASMJob().getInputPorts());
			for (MGASMPort port : _portDataSource.getItemIds()) {
				_stringPortDataSource.addBean(port.getName());
			}
		}

		loadEnvironment();
		loadAdapter();
		loadParameter();
		loadParser();
		loadUploads();
	}

	@Override
	public void onSave() {
		if (!_hasEnvironment.getValue())
			getJob().removeEnvironment();
		if (!_hasAdapter.getValue())
			getJob().removeAdapterConfig();
		if (!_hasParameter.getValue())
			getJob().removeParameter();
		saveParser();
		saveUploads();
		saveDescription();
	}

	private MGASMJob getASMJob() {
		return _asmJob;
	}

	public Job getJob() {
		return _job;
	}

	public String getJobName() {
		return getJob().getId();
	}

	private void loadEnvironment() {

		if (!_hasEnvironment.getValue()) {
			getJob().initEnvironment();
			return;
		}

		for (PropertyType envprop : getJob().getEnvironment().getProperties().getProperty()) {
			PropertyBean bean = new PropertyBean(envprop, this);
			addToDataSource(_envParamDataSource, bean, false);
		}
		reindexDatasource(_envParamDataSource);
	}

	private boolean hasEnvironment() {
		return getJob().hasEnvironmentElements();
	}

	public void addNewEnvProp() {
		PropertyType prop = getJob().createAndAddEnvProperty();
		PropertyBean b = new PropertyBean(prop, this);
		b.addRefChangedListener(new RefChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() == null)
					((PropertyBean) evt.getSource()).setDict(null);
				else {
					String namespace = Namespaces.ENVIRONMENT.getNamespace();
					IDictionary dict = DictionaryFactory.getInstance().getDictionary(namespace);
					((PropertyBean) evt.getSource()).setDict(dict);
				}
			}
		});
		addToDataSource(_envParamDataSource, b);
	}

	public void removeEnvProp(IPropertyBean value) {
		PropertyBean castVal = (PropertyBean) value;
		castVal.removeFromJob(getJob().getEnvironment().getProperties().getProperty());
		removeFromDataSource(_envParamDataSource, castVal);
	}

	public BeanItemContainer<PropertyBean> getEnvironmentDataSource() {
		return _envParamDataSource;
	}

	public Property getHasEnvProperty() {
		return _hasEnvironment;
	}

	public Container getEnvRefs() {
		return getMainDoc().getEnvRefs();
	}

	private void loadAdapter() {
				
		if (!_hasAdapter.getValue()) {
			initAdapListener();
			getJob().initAdapterConfig();
			return;
		}		

		AdapterConfigType adapConfig = getJob().getInitialization().getAdapterConfig();

		_selectedAdapterIDDataSource.setValue(adapConfig.getAdapterID());

		if (!StringH.isNullOrEmpty(adapConfig.getPortName()) && getASMJob() != null) {
			for (MGASMPort p : getASMJob().getInputPorts()) {
				if (adapConfig.getPortName().equals(p.getName())) {
					_selectedAdapterPortDataSource.setValue(p);
					break;
				}
			}
		}

		_selectedAdapterExtensionDataSource.setValue(adapConfig.getFileExtension());

		for (ParameterType param : adapConfig.getParameter()) {
			ParameterBean bean = new ParameterBean(param, this);
			addToDataSource(_adapParamDataSource, bean, false);
		}
		reindexDatasource(_adapParamDataSource);
		
		initAdapListener();
	}

	private void initAdapListener() {
		_selectedAdapterIDDataSource.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				_pseudoAdapterDictionaryDataSource.removeAllItems();
				for (IDictionary dict : DictionaryFactory.getInstance().getDictionaries(DictDir.ADAPTER)) {
					if (dict.getDictPrefix().equals(_selectedAdapterIDDataSource.getValue())) {
						_pseudoAdapterDictionaryDataSource.addBean(dict);
						break;
					}
				}
				refreshTables();

				getJob().getAdapterConfig().setAdapterID(_selectedAdapterIDDataSource.getValue());
				
				if (getPortDataSource().size() > 0)
					_selectedAdapterPortDataSource.setValue(getPortDataSource().getIdByIndex(0));
			}
		});

		_selectedAdapterPortDataSource.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				String res = _selectedAdapterPortDataSource.getValue() == null ? null : 
					_selectedAdapterPortDataSource.getValue().getName();
				getJob().getAdapterConfig().setPortName(res);
			}
		});

		_selectedAdapterExtensionDataSource.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				getJob().getAdapterConfig().setFileExtension(_selectedAdapterExtensionDataSource.getValue());
			}
		});
	}

	private Boolean hasAdapter() {
		return getJob().hasAdapterID();
	}

	public void addNewAdapProp() {
		ParameterType param = getJob().createAndAddAdapParam();
		ParameterBean b = new ParameterBean(param, this);
		if (_pseudoAdapterDictionaryDataSource.size() == 1)
			b.setDict(_pseudoAdapterDictionaryDataSource.getIdByIndex(0));
		addToDataSource(_adapParamDataSource, b);
	}

	public void removeAdapParam(IPropertyBean value) {
		ParameterBean castVal = (ParameterBean) value;
		castVal.removeFromJob(getJob().getAdapterConfig().getParameter());
		removeFromDataSource(_adapParamDataSource, castVal);
	}

	public Property getHasAdapProperty() {
		return _hasAdapter;
	}

	public BeanItemContainer<ParameterBean> getAdapterParamDataSource() {
		return _adapParamDataSource;
	}

	public BeanItemContainer<String> getAdapterDictsDataSource() {
		return getMainDoc().getAdapterDictsDataSource();
	}

	public BeanItemContainer<MGASMPort> getPortDataSource() {
		return _portDataSource;
	}
	
	public BeanItemContainer<String> getStringPortDataSource() {
		return _stringPortDataSource;
	}

	public Property getSelectedAdapterIDDataSource() {
		return _selectedAdapterIDDataSource;
	}

	public Property getSelectedAdapterPortDataSource() {
		return _selectedAdapterPortDataSource;
	}

	public Property getSelectedExtensionDataSource() {
		return _selectedAdapterExtensionDataSource;
	}

	// -------------------------- Parameter -----------------------------
	
	private void loadParameter() {
		if (!_hasParameter.getValue()) {
			getJob().initParameter();
			return;
		}

		for (ParameterType param : getJob().getInitialization().getParamList().getParameter()) {
			ParameterBean bean = new ParameterBean(param, this);
			addToDataSource(_parameterDataSource, bean, false);
		}
		reindexDatasource(_parameterDataSource);
	}

	private boolean hasParameter() {
		return getJob().hasParameterElements();
	}

	public void addNewParameter() {
		ParameterType param = getJob().createAndAddParameter();
		ParameterBean b = new ParameterBean(param, this);
		BeanItemContainer<IDictionary> dicts = getDictionariesToCurrentDomain();
		if (dicts.size() > 0)
			b.setDict(dicts.firstItemId());
		addToDataSource(_parameterDataSource, b);
	}

	public void removeParameter(IPropertyBean value) {
		ParameterBean castVal = (ParameterBean) value;
		castVal.removeFromJob(getJob().getInitialization().getParamList().getParameter());
		removeFromDataSource(_parameterDataSource, castVal);
	}

	public BeanItemContainer<ParameterBean> getParameterDataSource() {
		return _parameterDataSource;
	}

	public Property getHasParameterProperty() {
		return _hasParameter;
	}

	// -------------------------- UPLOADS -----------------------------
	
	private void loadUploads() {
		if (!_hasUploads.getValue()) {
			getJob().initUploads();
			return;
		}

		ArrayList<MGASMPort> unusedPorts = new ArrayList<MGASMPort>();
		for (MGASMPort port : getPortDataSource().getItemIds()) {
			unusedPorts.add(port);				
		}
		for (JobInputUploadType input : getJob().getInitialization().getUploadList().getJobInputUpload()) {
			UploadsBean bean = new UploadsBean(input);
			_uploadsDataSource.addBean(bean);
			bean.setIsMolecule(false);
		}

		for (MoleculeUploadType moleculeUpload : getJob().getInitialization().getUploadList().getMoleculeUpload()) {
			UploadsBean bean = new UploadsBean(moleculeUpload);
			bean.setIsMolecule(true);
			_uploadsDataSource.addBean(bean);
		}
		reindexDatasource(_uploadsDataSource);
	}

	private boolean hasUploads() {
		return getJob().hasUploads();
	}

	public void addNewUpload() {
		UploadsBean bean = new UploadsBean();
		_uploadsDataSource.addBean(bean);
		reindexDatasource(_uploadsDataSource);
	}

	public void removeUpload(UploadsBean value) {
		removeFromDataSource(_uploadsDataSource, value);
		reindexDatasource(_uploadsDataSource);
	}

	public BeanItemContainer<UploadsBean> getUploadsDataSource() {
		return _uploadsDataSource;
	}

	public Property getHasUploadsProperty() {
		return _hasUploads;
	}

	private void saveUploads() {
		getJob().removeUploads();
		if (!_hasUploads.getValue())
			return;
		for (UploadsBean upload : _uploadsDataSource.getItemIds()) {
			FileUpload jaxbUpload = upload.getIsMolecule() ?
					getJob().createAndAddMoleculeUpload() :
					getJob().createAndAddInputUpload();
			upload.fillJAXBElement(jaxbUpload);			
		}
	}

	// -------------------------- PARSER -----------------------------

	private void loadParser() {

		_parserOutputFileNameDataSource.addListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				JobParserConfig pinit = getJob().getInitialization().getParserConfig();
				pinit.setOutputFilename((String) event.getProperty().getValue());
			}
		});
		
		if (!_hasParser.getValue())
			getJob().initParserConfig();

		JobParserConfig pinit = getJob().getInitialization().getParserConfig();
		DomainId domain = getMainDoc().getTemplateBean().getDomain();
		ConfigDir configDir = ConfigDir.getToID(domain);
		configDir.refreshFiles();
		List<ConfigBean> availableConfigs = configDir.getFiles();
		
		List<ConfigBean> addedConfigsRes = new ArrayList<ConfigBean>();
		List<ConfigBean> availableConfigsRes = new ArrayList<ConfigBean>();
		
		List<String> patterns = pinit.getConfigFilePattern();
		for (String pat : patterns) {
			for (ConfigBean config : availableConfigs) {
				if (config.matches(pat)) {
					addedConfigsRes.add(config);
				}
			}
		}
		
		for (ConfigBean conf : availableConfigs) {
			if (!addedConfigsRes.contains(conf))
				availableConfigsRes.add(conf);
		}
		
		_addedParserConfigsDataSource.addAll(addedConfigsRes);
		_availableParserConfigsDataSource.addAll(availableConfigsRes);
		_parserOutputFileNameDataSource.setValue(pinit.getOutputFilename());

	}

	private boolean hasParser() {
		return getJob().hasParserConfig();
	}

	public ObjectProperty<Boolean> getHasParserDataSource() {
		return _hasParser;
	}

	public BeanItemContainer<ConfigBean> getAvailableParserConfigsDataSource() {
		return _availableParserConfigsDataSource;
	}

	public BeanItemContainer<ConfigBean> getAddedParserConfigsDataSource() {
		return _addedParserConfigsDataSource;
	}

	public Property getParserOutputFileNameDataSource() {
		return _parserOutputFileNameDataSource;
	}

	private void saveParser() {
		getJob().getInitialization().removeParser();
		if (!getHasParserDataSource().getValue())
			return;
			
		getJob().getInitialization().initParserConfig();
		JobParserConfig parserConfig = getJob().getInitialization().getParserConfig();
		for (ConfigBean conf : _addedParserConfigsDataSource.getItemIds()) {
			parserConfig.addConfigFilePattern(conf.getPattern());
		}
	}

	public IDictionary resolveDict(String prefix) {
		String namespace = getTemplate().getNamespaceToPrefix(prefix);
		return DictionaryFactory.getInstance().getDictionary(namespace);
	}

	public BeanItemContainer<String> getRefsToDict(IDictionary dict) {
		return getMainDoc().getRefsToDict(dict);
	}

	public BeanItemContainer<String> getRefsToDict(Namespaces namespace) {
		return getMainDoc().getRefsToDict(namespace);
	}

	private <T extends IIndexedPropertyBean> void removeFromDataSource(BeanItemContainer<T> c, T b) {
		if (b instanceof IPropertyBean)
			((IPropertyBean)b).removeRefChangedListeners();
		c.removeItem(b);
		reindexDatasource(c);
	}

	private <T extends IPropertyBean> void addToDataSource(BeanItemContainer<T> c, T b) {
		addToDataSource(c, b, true);
	}

	private <T extends IPropertyBean> void addToDataSource(BeanItemContainer<T> c, T b, boolean reindex) {
		c.addBean(b);
		if (reindex)
			reindexDatasource(c);
		b.addDictChangedListener(new DictChangeListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				T bean = ((T) evt.getSource());
				if (evt.getNewValue() == null) {
					bean.setRef(null);
					return;
				}

				Set<String> ids = ((IDictionary) evt.getNewValue()).getEntryIDs();
				if (!ids.contains(bean.getRef()))
					bean.setRef(null);
				else
					refreshTables();
			}
		});
		b.addRefChangedListener(new RefChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshTables();
			}
		});
	}

	private <T extends IIndexedPropertyBean> void reindexDatasource(BeanItemContainer<T> c) {
		for (int i = 1; i <= c.size(); i++) {
			c.getIdByIndex(i - 1).setIndex(i);
		}
		refreshTables();
	}

	private void refreshTables() {
		getview().refreshEnvironmentTable();
		getview().refreshAdapterTable();
		getview().refreshParameterTable();
		getview().refreshUploadTable();
	}

	public BeanItemContainer<IDictionary> getDictionariesToCurrentDomain() {
		return getMainDoc().getDictionariesToCurrentDomain();
	}

	@Override
	public String toString() {
		return getJob().getId() + (getASMJob() != null ? "" : " (No job)");
	}

	/**
	 * This function is used as a pseudo data source. It's only element should
	 * be the currently selected adapter dictionary.
	 * 
	 * @return
	 */
	public BeanItemContainer<IDictionary> getPseudoAdapterDictionaryDataSource() {
		return _pseudoAdapterDictionaryDataSource;
	}

	public boolean hasValidJob() {
		return getASMJob() != null;
	}

	public class JobLoadBean {
		private final String adapId, adapExt;
		private final MGASMPort adapPort;
		private final String jobName;

		public JobLoadBean(String adapId, MGASMPort adapPort, String adapExt, String jobName) {
			super();
			this.adapId = adapId;
			this.adapPort = adapPort;
			this.adapExt = adapExt;
			this.jobName = jobName;
		}

		public String getAdapId() {
			return adapId;
		}

		public MGASMPort getAdapPort() {
			return adapPort;
		}

		public String getAdapExt() {
			return adapExt;
		}

		public String getJobName() {
			return jobName;
		}
	}

	public BeanItemContainer<String> getUnassignedJobsDataSource() {
		return getMainDoc().getUnassignedJobsDataSource();
	}

	public void changeToJob(String value) {
		if (value == null)
			return;
		getMainDoc().changeToJob(this, value);
	}

	public ObjectProperty<String> getDescriptionDataSource() {
		return _descriptionDataSource;
	}
	
	private void saveDescription() {
		if (!StringH.isNullOrEmpty(_descriptionDataSource.getValue())) {
			if (getJob().getDescription() == null)
				getJob().createDescription();
			getJob().getDescription().setPlainText(_descriptionDataSource.getValue());
		} else if (getJob().getDescription() != null) {
			getJob().getDescription().setPlainText(null);
		}
	}
}
