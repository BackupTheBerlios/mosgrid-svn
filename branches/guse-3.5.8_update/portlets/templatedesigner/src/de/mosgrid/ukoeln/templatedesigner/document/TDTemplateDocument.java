package de.mosgrid.ukoeln.templatedesigner.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;

import de.mosgrid.msml.enums.DictDir;
import de.mosgrid.msml.enums.Namespaces;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.ukoeln.templatedesigner.asm.MGASMJob;
import de.mosgrid.ukoeln.templatedesigner.document.TDMainDocument.TemplateBean;
import de.mosgrid.ukoeln.templatedesigner.gui.TDViewBase;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateTab;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateViewJob;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateViewMain;
import de.mosgrid.ukoeln.templatedesigner.helper.StringH;

public class TDTemplateDocument extends TDDocumentBaseWithWorkflow<TDTemplateTab> {

	private TemplateBean _bean;
	private final HierarchicalContainer _views = new HierarchicalContainer();
	private final BeanItemContainer<String> _unassignedJobs = new BeanItemContainer<String>(String.class);
	private final BeanItemContainer<String> _envRefsDataSource;
	private final BeanItemContainer<String> _adapterDictsDataSource;
	private final Hashtable<String, BeanItemContainer<String>> _namespace2EntriesDataSource;
	private final BeanItemContainer<IDictionary> _domainDictionaries;
	private final TDMainDocument _mainDoc;

	public TDTemplateDocument(TDMainDocument mainDoc, TemplateBean bean) {
		super(mainDoc.getMainWindow());
		_mainDoc = mainDoc;
		_bean = bean;
		_envRefsDataSource = new BeanItemContainer<String>(String.class);
		_adapterDictsDataSource = new BeanItemContainer<String>(String.class);
		_namespace2EntriesDataSource = new Hashtable<String, BeanItemContainer<String>>();
		_domainDictionaries = new BeanItemContainer<IDictionary>(IDictionary.class);
	}

	@Override
	void doOnInit() {
		_envRefsDataSource.addAll(getDictionaryEntries(DictDir.ENVIRONMENT));
		_adapterDictsDataSource.addAll(getDictionaryEntries(DictDir.ADAPTER, Namespaces.ADAPTER));
		_domainDictionaries.addAll(DictionaryFactory.getInstance().getDictionaries(_bean.getDomain().getDictDir()));

		TDTemplateViewMain mainView = new TDTemplateViewMain();
		TDTemplateMainDocument doc = new TDTemplateMainDocument(new TemplateBeanBase(this));
		doc.init(mainView, getUser());
		mainView.init(doc);
		
		_views.addItem(mainView);
		// create a view for each job
		if (_bean.getSuccessfullyImported())
			for (Job job : _bean.getTemplate().getJobListElement().getJobs())
				createAndAddJob(job, false);
	}
	
	public void addJob(String value) {
		Job newJob = _bean.getTemplate().getJobListElement().addNewJob();
		newJob.setId(value);
		createAndAddJob(newJob, true);
	}
	
	public void removeJob(TDTemplateJobDocument doc) {
		removeJob(doc.getview(), false);
	}

	public void removeJob(TDTemplateViewJob view, boolean removeFromTemmplate) {
		_views.removeItem(view);
		if (removeFromTemmplate)
			getTemplateBean().getTemplate().getJobListElement().removeJobById(view.getJob());
		updateUnassignedJobs();
	}

	private TDTemplateViewJob createAndAddJob(Job job, boolean update) {
		MGASMJob foundJob = null;
		if (job.getId() != null && _bean.getInstance() != null) {
			foundJob = _bean.getInstance().getJob(job.getId());
		}

		TemplateJobBean tb = new TemplateJobBean(this, job, foundJob);
		TDTemplateViewJob jobview = new TDTemplateViewJob();
		TDTemplateJobDocument doc = new TDTemplateJobDocument(tb);
		doc.init(jobview, getUser());
		jobview.init(doc);
		_views.addItem(jobview);
		if (update)
			updateUnassignedJobs();
		return jobview;
	}

	TemplateBean getTemplateBean() {
		return _bean;
	}

	public void updateUnassignedJobs() {
		_unassignedJobs.removeAllItems();
		if (_bean.getInstance() != null && _bean.getSuccessfullyImported()) {
//		if (_bean.getInstance() == null || !_bean.getSuccessfullyImported()) {
//			for (Job job : _bean.getTemplate().getJobListElement().getJobs()) {
//				_unassignedJobs.addBean(job.getId());
//			}
//		} else {
			for (MGASMJob asmJob : _bean.getInstance().getJobs()) {
				boolean foundJob = false;
				for (Job job : _bean.getTemplate().getJobListElement().getJobs()) {
					if (StringH.equals(job.getId(), asmJob.getJobName())) {
						foundJob = true;
						break;
					}
				}
				if (!foundJob)
					_unassignedJobs.addBean(asmJob.getJobName());
			}			
		}
			
		for (int i = 0; i < _views.size(); i++) {
			if (!(_views.getIdByIndex(i) instanceof TDTemplateViewJob))
				continue;
			((TDTemplateViewJob) _views.getIdByIndex(i)).updateUnassignedJobs();
		}
		getview().updateUnassignedJobs();
	}

	public Container getViews() {
		return _views;
	}

	public BeanItemContainer<String> getUnassignedJobsDataSource() {
		return _unassignedJobs;
	}

	public String getCaption() {
		return _bean.toString();
	}
	
	public Container getEnvRefs() {
		return _envRefsDataSource;
	}
	
	public BeanItemContainer<String> getAdapterIDDataSource() {
		return _namespace2EntriesDataSource.get(Namespaces.ADAPTER);
	}

	public BeanItemContainer<String> getAdapterDictsDataSource() {
		return _adapterDictsDataSource;
	}
	
	public BeanItemContainer<String> getRefsToDict(IDictionary dict) {
		String param = dict == null ? null : dict.getNamespace();
		return getRefsToDict(param);
	}

	public BeanItemContainer<String> getRefsToDict(Namespaces namespace) {
		return getRefsToDict(namespace.getNamespace());
	}
	
	private BeanItemContainer<String> getRefsToDict(String namespace) {
		if (namespace == null)
			namespace = "";
		initDict2Refs(namespace);
		return _namespace2EntriesDataSource.get(namespace);
	}

	private void initDict2Refs(String namespace) {
		if (_namespace2EntriesDataSource.containsKey(namespace))
			return;
		BeanItemContainer<String> entries = new BeanItemContainer<String>(String.class);
		IDictionary dict = DictionaryFactory.getInstance().getDictionary(namespace);
		if (dict != null) {
			List<String> entriesUnsorted = new ArrayList<String>(dict.getEntryIDs());
			Collections.sort(entriesUnsorted);
			entries.addAll(entriesUnsorted);
		}
			
		_namespace2EntriesDataSource.put(namespace, entries);
	}
	
	public BeanItemContainer<IDictionary> getDictionariesToCurrentDomain() {
		return _domainDictionaries;
	}

	public void changeToJob(TDTemplateJobDocument doc, String value) {
		Job job = doc.getJob();
		job.setId(value);
		updateUnassignedJobs();
		getview().selectJob(doc.getview());
	}

	public void onClose() {
		_mainDoc.onClose(this);
	}

	/**
	 * Saves template and reloads all information. Therefore the current tab must be
	 * closed.
	 */
	public void saveAndReloadComplete() {
		_mainDoc.closeTemplate(this);
		saveTemplate();
		_mainDoc.syncBeanWorkflows(_bean);
		_mainDoc.openTemplate(_bean);
	}
	
	/**
	 * Renames the template and reloads all information.
	 */
	public void renameAndReloadComplete() {
		_mainDoc.closeTemplate(this);
		renameTemplate();
		_mainDoc.syncBeanWorkflows(_bean);
		_mainDoc.openTemplate(_bean);
	}

	public void saveTemplate() {
		notifyViewsOfSave();
		_bean.save();
	}

	public void renameTemplate() {
		notifyViewsOfSave();
		_bean.rename();
	}

	@SuppressWarnings("unchecked")
	private void notifyViewsOfSave() {
		for (TDViewBase<?> view : ((Collection<TDViewBase<?>>)_views.getItemIds()))
			view.onSave();
	}

	public class TemplateBeanBase {
		private final TDTemplateDocument _tdoc;

		public TemplateBeanBase(TDTemplateDocument tdoc) {
			super();
			_tdoc = tdoc;
		}

		public TDTemplateDocument getTdoc() {
			return _tdoc;
		}
	}

	public class TemplateJobBean extends TemplateBeanBase {
		
		private final Job _job;
		private final MGASMJob _asmJob;
		
		public TemplateJobBean(TDTemplateDocument tdoc, Job job, MGASMJob asmJob) {
			super(tdoc);
			_job = job;
			_asmJob = asmJob;			
		}
	
		public Job getJob() {
			return _job;
		}

		public MGASMJob getAsmJob() {
			return _asmJob;
		}
	}
}
