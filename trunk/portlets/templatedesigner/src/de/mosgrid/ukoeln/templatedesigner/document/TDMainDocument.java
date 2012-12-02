package de.mosgrid.ukoeln.templatedesigner.document;

import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.portlet.ImportableID;
import de.mosgrid.portlet.MosgridUser;
import de.mosgrid.ukoeln.templatedesigner.TDDocumentManager;
import de.mosgrid.ukoeln.templatedesigner.TDDocumentManager.TDViewParam;
import de.mosgrid.ukoeln.templatedesigner.TemplateDesignerApplication;
import de.mosgrid.ukoeln.templatedesigner.asm.MGASMWorkflow;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainTab;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainWindow;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateTab;
import de.mosgrid.ukoeln.templatedesigner.helper.EventArgs;
import de.mosgrid.ukoeln.templatedesigner.helper.EventArgsNonGeneric;
import de.mosgrid.ukoeln.templatedesigner.helper.EventListener;
import de.mosgrid.ukoeln.templatedesigner.helper.StackTraceHelper;
import de.mosgrid.ukoeln.templatedesigner.helper.StringH;
import de.mosgrid.ukoeln.templatedesigner.resources.Resources;
import de.mosgrid.util.DefaultTemplateManager;
import de.mosgrid.util.ImportID;
import de.mosgrid.util.interfaces.ITemplateManager;

public class TDMainDocument extends TDDocumentBaseWithWorkflow<TDMainTab> {
	private final Logger LOGGER = LoggerFactory.getLogger(TDMainDocument.class);
	private BeanItemContainer<TemplateBean> _templates = new BeanItemContainer<TemplateBean>(TemplateBean.class);
	private BeanItemContainer<DomainId> _domains = new BeanItemContainer<DomainId>(DomainId.class,
			Arrays.asList(DomainId.values()));
	private DomainId _curDomain;
	private ASMHelper _asmHelper;
	private Hashtable<String,  TDTemplateDocument> _ownedDocs = new Hashtable<String, TDTemplateDocument>();
	private static Hashtable<String, MosgridUser> _openTemplates = new Hashtable<String, MosgridUser>();
	private TemplateDesignerApplication _app;

	public TDMainDocument(TDMainWindow mainWindow,TemplateDesignerApplication app) {
		super(mainWindow);
		_app = app;
	}

	public BeanItemContainer<DomainId> getDomains() {
		return _domains;
	}
	

	public void updateWorkflows() {
		updateRepoWorkflows();
	}

	public void updateTemplatesToDomain(DomainId val, boolean fullUpdate) {
		if (fullUpdate)
			DefaultTemplateManager.getInstance(val).refreshTemplates();

		ITemplateManager man = DefaultTemplateManager.getInstance(val);

		_templates.removeAllItems();

		for (MSMLTemplate template : man.getAllTemplates()) {
			ImportableID templateId = new ImportableID(template);
			ASMRepositoryItemBean repoWorkflow = null;
			for (ASMRepositoryItemBean wkfl : getAllRepoWorkflowItemBeans()) {
				if (new ImportableID(wkfl).equals(templateId)) {
					repoWorkflow = wkfl;
					break;
				}
			}
			_templates.addBean(new TemplateBean(template, repoWorkflow, val));
		}
		_curDomain = val;
	}

	public Container getTemplateDataSource() {
		return _templates;
	}

	@Override
	void doOnInit() {
		_asmHelper = new ASMHelper(getUser(), getview(), _app);
		_asmHelper.addImportedListener(new EventListener<EventArgs<TemplateBean>>() {

			@Override
			public void eventHappened(EventArgs<TemplateBean> param) {
				synchronized (getview().getApplication()) {
					workflowImported(param);					
				}
			}
		});
		_asmHelper.addImportStartListener(new EventListener<EventArgsNonGeneric>() {

			@Override
			public void eventHappened(EventArgsNonGeneric param) {
				synchronized (getview().getApplication()) {
					workflowImportStarted();
				}
			}
		});
	}

	public void createNewTemplate(DomainId domain, String wkfl, String note, String displayName) {
		String path = domain.getTemplateDir().getPath() + wkfl
				+ (StringH.isNullOrEmpty(note) ? "" : "_" + note)
				+ (StringH.isNullOrEmpty(displayName) ? "" : "_" + displayName) + ".xml";
		final File file = new File(path);
		if (file.exists()) {
			boolean doOverwrite = getMainWindow().showYesNoDialog("Overwrite?",
					"A template with the following filename allready exists. Overwrite?\n" + file.getAbsolutePath());
			if (doOverwrite)
				file.delete();
			else
				return;
		}

		boolean success = false;
		try {
			FileWriter fw = null;
			InputStream is = null;
			InputStreamReader sr = null;
			try {
				file.createNewFile();
				fw = new FileWriter(file);
				is = Resources.class.getResourceAsStream("template_template.xml");
				sr = new InputStreamReader(is);
				CharBuffer buf = CharBuffer.allocate(1024);
				int len;
				while ((len = sr.read(buf)) != -1)
					fw.write(buf.array(), 0, len);
			} catch (IOException e) {
				LOGGER.error("Could not create template: " + e.getMessage() + "\n" + StackTraceHelper.getTrace(e));
			} finally {
				try {
					if (fw != null)
						fw.close();
					if (sr != null)
						sr.close();
					if (is != null)
						is.close();
				} catch (IOException e) {
					LOGGER.error("Could not close streams: " + e.getMessage() + "\n" + StackTraceHelper.getTrace(e));				}
			}

			JobListEditor ed = new JobListEditor(file);
			ed.getJobListElement().setID(wkfl);
			if (!StringH.isNullOrEmpty(note))
				ed.getJobListElement().setWorkflowNotes(note);
			if (!StringH.isNullOrEmpty(displayName))
				ed.getJobListElement().setDisplayName(displayName);
			ed.marshall();

			MSMLTemplate newTemplate = new MSMLTemplate(file);

			for (int i = 0; i < _templates.size(); i++) {
				TemplateBean template = _templates.getIdByIndex(i);
				if (template.getTemplate().toString().equals(newTemplate.toString())) {
					getMainWindow().showNotification(
							"There allready exists a template with "
									+ "the same resulting display name. Please choose another one.",
							Notification.TYPE_WARNING_MESSAGE);
					return;
				}
			}
			success = true;
		} finally {
			if (!success && file.exists()) {
				file.delete();
				return;
			}
		}

		updateTemplatesToDomain(_curDomain, true);
	}
	
	/**
	 * Returns an absolute path to store the template to. It contains the domain-path/workflowname_displayname.xml
	 * If renameIfExists is false, then it is not checked whether or not the file exists.
	 * If it is true then the existence of the file is checked and the filename is changed accordingly.
	 * 
	 * @param domain DomainId of the domain to which the template/workflow belongs.
	 * @param wkfl Workflow of the template.
	 * @param displayName Displyname of the template.
	 * @param renameIfExists If true, then a filename ending with -1.xml, -2.xml and so on is generated 
	 * as long as the filename exists.
	 * @return A filename for the given combination of domain, workflow and displayname.
	 */
	public static String getTemplateFileName(DomainId domain, String wkfl, String displayName, boolean renameIfExists) {
		String ext = ".xml";
		String resBasePath = domain.getTemplateDir().getPath() + wkfl
				+ (StringH.isNullOrEmpty(displayName) ? "" : "_" + displayName);
		if (!renameIfExists)
			return resBasePath + ext;
		
		int i = 0;
		String res = resBasePath;
		File check = new File(res + ext);
		while (check.exists()) {
			res = resBasePath + "-" + (++i);
			check = new File(res + ext);
		}
			
		return res + ext;
	}

	public void deleteTemplate(TemplateBean msmlTemplateWrapper) {
		String path = msmlTemplateWrapper.getTemplate().getPath();
		if (StringH.isNullOrEmpty(path))
			return;
		File file = new File(path);
		if (!file.exists())
			return;
		file.delete();

		updateTemplatesToDomain(_curDomain, true);
	}

	public synchronized void openTemplate(final TemplateBean wrapper) {
		String templateName = wrapper.getTemplate().getName();
		if (_openTemplates.containsKey(templateName)) {
			getMainWindow().showNotification("Cannot open " + templateName + ". " +
					(_openTemplates.get(templateName) == getUser() ?
							"You have this template allready opened." :
							_openTemplates.get(templateName).getUserName() + " is working on this."));
			return;
		}
		_openTemplates.put(wrapper.getTemplate().getName(), getUser());
		TemplateBean newWrapper = new TemplateBean(wrapper);
		if (newWrapper.getWorkflow() != null)
			_asmHelper.importWorkflow(newWrapper);
		else {
			newWrapper.setCanBeOpened(true);
			workflowImported(new EventArgs<TemplateBean>(newWrapper));
		}
	}

	public void copyTemplate(TemplateBean wrapper) {
		wrapper.copy();
		updateTemplatesToDomain(_curDomain, true);
	}

	private void workflowImported(EventArgs<TemplateBean> param) {
		try {
			final TemplateBean wrapper = param.getParam();	
			if (wrapper.getCanBeOpened()) {
				TDDocumentManager man = getMainWindow().getManager();
				TDTemplateDocument doc = man.createAndAddView(new TDTemplateDocumentInitParam(man, this, wrapper));
				_ownedDocs.put(wrapper.getTemplate().getName(), doc);				
			}
		} finally {
			getview().refreshContent();
			getMainWindow().requestRepaintAll();
		}
		getview().disablePI();
		getview().setOpeningEnabled(true);
	}

	private void workflowImportStarted() {
		getview().enablePI();
		getview().setOpeningEnabled(false);
		getview().refreshContent();
		getMainWindow().requestRepaintAll();
	}

	public class TemplateBean {

		private MSMLTemplate _template;
		private ASMRepositoryItemBean _workflow;
		private final DomainId _domain;
		private MGASMWorkflow _instance;
		private boolean _successfullyImported;
		private TemplateBean _origin;
		private boolean _canBeOpened;

		public TemplateBean(MSMLTemplate template, ASMRepositoryItemBean workflow, DomainId domain) {
			super();
			_template = template;
			_workflow = workflow;
			_domain = domain;
			_successfullyImported = false;
		}
		
		public TemplateBean(TemplateBean bean) {
			this(bean._template.copy(true), bean._workflow, bean._domain);
			_origin = bean;
			_successfullyImported = bean._successfullyImported;
		}

		public void setCanBeOpened(boolean canBeOpened) {
			_canBeOpened = canBeOpened;
		}
		
		public boolean getCanBeOpened() {
			return _canBeOpened;
		}
		
		public MGASMWorkflow getInstance() {
			return _instance;
		}

		public void setInstance(MGASMWorkflow instance) {
			_instance = instance;
		}

		public MSMLTemplate getTemplate() {
			return _template;
		}

		public ASMRepositoryItemBean getWorkflow() {
			return _workflow;
		}

		public DomainId getDomain() {
			return _domain;
		}
		
		public void setSuccessfullyOpened(boolean val) {
			_successfullyImported = val;
			_canBeOpened = getSuccessfullyImported();
		}
		
		public boolean getSuccessfullyImported() {
			return _successfullyImported;
		}

		@Override
		public String toString() {
			if (_workflow == null)
				return _template.toString() + " (Workflow not matching)";
			return _template.toString();
		}

		public String getTabCaption() {
			if (!_successfullyImported)
				return _template.toString() + " (Workflow not imported)";
			return toString();
		}

		public void syncBeanWorkflow(List<ASMRepositoryItemBean> allWorkflows) {
			ImportID templateId = new ImportID(getTemplate());
			ASMRepositoryItemBean repoWorkflow = null;
			for (ASMRepositoryItemBean wkfl : getAllRepoWorkflowItemBeans()) {
				if (new ImportID(wkfl).equals(templateId)) {
					repoWorkflow = wkfl;
					break;
				}
			}
			setWorkflow(repoWorkflow);
		}

		private void setWorkflow(ASMRepositoryItemBean workflow) {
			_workflow = workflow;
			_origin._workflow = _workflow;
		}
		
		/**
		 * Copies the current template.
		 */
		public void copy() {
			MSMLTemplate copy = _template.copy(true);
			if (copy.getJobListElement() == null)
				copy.addDefaultJobListIfNoneExists();
			String displName = copy.getJobListElement().getDisplayName();
			if (displName != null && "".equals(displName))
				displName += "_Copy";
			else
				displName = "Dummy_Copy";
			copy.getJobListElement().setDisplayName(displName);
			String newPath = getTemplateFileName(_domain, _workflow.getItemID(), displName, true);
			copy.marshallTo(new File(newPath));

		}

		/**
		 * Renames the current template. It therefore saves the current state of the template to a new
		 * name according to the current displayname and workflow. That's why those attributes have to be
		 * modified before rename is invoked.
		 */
		public void rename() {
			String oldPath = _template.getPath();
			if (oldPath == null && "".equals(oldPath))
				throw new UnsupportedOperationException("File cannot be renamed from an unset state.");

			File tmpFile = null;
			try {
				if (oldPath != null && !"".equals(oldPath)) {
					File oldFile = new File(oldPath);
					tmpFile = File.createTempFile(oldPath, ".tmp", new File(oldFile.getAbsolutePath()));
					oldFile.renameTo(tmpFile);
				}
				
				String path = TDMainDocument.getTemplateFileName(_domain, 
						_workflow.getItemID(), _template.getJobListElement().getDisplayName(), true);
				if (path == null && "".equals(path)) // something is wrong
					throw new UnsupportedOperationException("Could not create new templatename.");
				
				_template.marshallTo(new File(path));
			} catch (IOException e) {
				LOGGER.error("Could not rename template.", e);
			} finally {
				if (tmpFile != null)
					tmpFile.delete();
			}
		}

		/**
		 * Saves the current template to the current location.
		 */
		public void save() {
			_template.marshall();
			afterFileOp();
		}

		/**
		 * Called after marshalling to update the origin.
		 */
		private void afterFileOp() {
			_origin._template = _template;
			_origin._workflow = _workflow;
		}
	}
	
	private class TDTemplateDocumentInitParam extends TDViewParam<TDTemplateTab, TDTemplateDocument> {
		
		private TDMainDocument _doc;
		private TemplateBean _wrapper;

		public TDTemplateDocumentInitParam(TDDocumentManager man, TDMainDocument doc, TemplateBean wrapper) {
			man.super();
			_doc = doc;
			_wrapper = wrapper;
		}

		@Override
		public TDTemplateDocument createDoc(TDDocumentManager man) {
			return new TDTemplateDocument(_doc, _wrapper);
		}

		@Override
		public TDTemplateTab doCreateView() {
			return new TDTemplateTab(_wrapper.getTabCaption());
		}

		@Override
		public boolean getIsClosable() {
			return true;
		}
	}

	public void closeTemplate(TDTemplateDocument doc) {
		getMainWindow().removeTab(doc.getview());
		onClose(doc);
	}

	public void onClose(TDTemplateDocument doc) {
		String templateName = doc.getTemplateBean().getTemplate().getName();
		_openTemplates.remove(templateName);
		_ownedDocs.remove(templateName);
//		_xfsHelper.close();
	}

	public void closeAllOpenTemplates(MosgridUser user) {
		ArrayList<TDTemplateDocument> list = new ArrayList<TDTemplateDocument>(_ownedDocs.values());
		for (TDTemplateDocument doc : list) {
			getMainWindow().removeTab(doc.getview());
			onClose(doc);
		}
		
		if (user == null)
			return;
		ArrayList<String> entriesToRemove = new ArrayList<String>();
		for (Entry<String, MosgridUser> pair : _openTemplates.entrySet()) {
			if (pair.getValue() == user)
				entriesToRemove.add(pair.getKey());
		}
		
		for (String entry : entriesToRemove) {
			_openTemplates.remove(entry);
		}
	}

	public void syncBeanWorkflows(TemplateBean bean) {
		bean.syncBeanWorkflow(getAllRepoWorkflowItemBeans());
	}

	public DomainId getSelectedDomain() {
		return _curDomain;
	}
}
