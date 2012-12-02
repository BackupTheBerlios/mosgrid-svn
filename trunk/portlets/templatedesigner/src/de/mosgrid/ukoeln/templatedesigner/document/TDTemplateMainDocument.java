package de.mosgrid.ukoeln.templatedesigner.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;

import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.jaxb.bindings.DescriptionType;
import de.mosgrid.msml.util.wrapper.JobList;
import de.mosgrid.ukoeln.templatedesigner.asm.MGASMJob;
import de.mosgrid.ukoeln.templatedesigner.asm.MGASMPort;
import de.mosgrid.ukoeln.templatedesigner.document.TDMainDocument.TemplateBean;
import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateDocument.TemplateBeanBase;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateViewMain;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateViewMainChangeWkflDialog;
import de.mosgrid.ukoeln.templatedesigner.helper.StringH;

public class TDTemplateMainDocument extends TDTemplateDocumentBase<TDTemplateViewMain> {

	private final ObjectProperty<String> _descriptionDataSource;
	private final ObjectProperty<String> _titleDataSource;
	private final ObjectProperty<String> _selectedJob;
	private final ObjectProperty<Boolean> _hasParserConfig;
	private final BeanItemContainer<String> _allParserJobIDsDataSource;
	private final BeanItemContainer<String> _portsToJobDataSource;
	private final ObjectProperty<String> _selectedPort;
	private final Logger LOGGER = LoggerFactory.getLogger(TDTemplateMainDocument.class);
	
	public TDTemplateMainDocument(TemplateBeanBase tbb) {
		super(tbb);
		
		_descriptionDataSource = new ObjectProperty<String>("");
		_titleDataSource = new ObjectProperty<String>("");
		_selectedJob = new ObjectProperty<String>("");
		_selectedPort = new ObjectProperty<String>("");
		_hasParserConfig = new ObjectProperty<Boolean>(false);
		_allParserJobIDsDataSource = new BeanItemContainer<String>(String.class);
		_portsToJobDataSource = new BeanItemContainer<String>(String.class);
	}

	@Override
	void doInit() {
		if (getTemplate() != null) {
			if (getTemplate().hasTitle())
				_titleDataSource.setValue(getTemplate().getJobListElement().getTitle());
		}

		_titleDataSource.addListener(new ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				MSMLTemplate template = getTemplate();
				template.getJobListElement().setTitle(_titleDataSource.getValue());
			}
		});
		
		if (getTemplate() != null) {
			if (getTemplate().hasDescription())
				_descriptionDataSource.setValue(getTemplate().getJobListElement().getDescription().getPlainText());
		}

		_descriptionDataSource.addListener(new ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				MSMLTemplate template = getTemplate();
				if (StringH.isNullOrEmpty(_descriptionDataSource.getValue()) &&
						template.getJobListElement() != null &&
						template.getJobListElement().getDescription() != null) {
					template.getJobListElement().getDescription().setPlainText(null);
					return;
				}
				DescriptionType desc = template.createDescriptionIfNotExisting();
				desc.setPlainText(_descriptionDataSource.getValue());
			}
		});

		if (!getMainDoc().getTemplateBean().getSuccessfullyImported()) {
			getview().hideAllParserControls();
			return;
		}
		
		_hasParserConfig.setValue(getTemplate().hasParserConfig());
		_hasParserConfig.addListener(new ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (_hasParserConfig.getValue() && getTemplate().getJobListElement().getParserConfig() == null) {
					getTemplate().getJobListElement().addNewParserConfig();
				} else if (!_hasParserConfig.getValue() && 
						getTemplate().getJobListElement().getParserConfig() != null) {
					getTemplate().getJobListElement().removeParserConfig();
				}
				getview().updateToHasParser(_hasParserConfig.getValue());
			}
		});

		updateParserJobIDs();
		if (_hasParserConfig.getValue())
			_selectedJob.setValue(getTemplate().getJobListElement().getParserConfig().getJobName());
		_selectedJob.addListener(new ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updatePortsToJob();
				getTemplate().getJobListElement().getParserConfig().setJobName(_selectedJob.getValue());
			}
		});
		
		updatePortsToJob();
		if (_hasParserConfig.getValue())
			_selectedPort.setValue(getTemplate().getJobListElement().getParserConfig().getPortName());
		_selectedPort.addListener(new ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				getTemplate().getJobListElement().getParserConfig().setPortName(_selectedPort.getValue());
			}
		});
	}

	@Override
	public void onSave() {}

	public void updateParserJobIDs() {
		_allParserJobIDsDataSource.removeAllItems();
		TemplateBean bean = getMainDoc().getTemplateBean();
	 	// Add the jobs id to the datasource that contain parser configs. 
		if (bean.getSuccessfullyImported()) {
			for (MGASMJob job : bean.getInstance().getJobs()) {
//				if (job.hasParserConfig())
					_allParserJobIDsDataSource.addBean(job.getJobName());
			}
		} else {
			// if no jobs have been imported, then just add the id of the job which is configured in the
			// parser config... if there is any.
			if (bean.getTemplate().hasParserConfig())
				_allParserJobIDsDataSource.addBean(bean.getTemplate().getJobListElement().getParserConfig().getJobName());
		}
	}
	
	public void updatePortsToJob() {
		_portsToJobDataSource.removeAllItems();
		TemplateBean bean = getMainDoc().getTemplateBean();

		if (!bean.getSuccessfullyImported() && bean.getTemplate().hasParserConfig()) {
			_portsToJobDataSource.addBean(bean.getTemplate().getJobListElement().getParserConfig().getPortName());
			return;
		}
		
		if (StringH.isNullOrEmpty(_selectedJob.getValue()))
			return;
		
		MGASMJob job = bean.getInstance().getJob(_selectedJob.getValue());
		if (job == null)
			return;
		for (MGASMPort port : job.getInputPorts()) {
			_portsToJobDataSource.addBean(port.getName());
		}
	}

	public String getNotes() {
		if (getTemplateJobList() == null)
			return null;
		return getTemplateJobList().getWorkflowNotes();
	}
	
	public String getDisplayName() {
		if (getTemplate() != null && getTemplate().hasDisplayName())
			return getTemplate().getJobListElement().getDisplayName();
		return null;
	}

	private JobList getTemplateJobList() {
		if (getMainDoc().getTemplateBean().getTemplate() == null)
			return null;
		return getMainDoc().getTemplateBean().getTemplate().getJobListElement();
	}

	public void openChangeDialog() {
		TDTemplateViewMainChangeWkflDialog.show(getMainWindow(), this);
	}

	public BeanItemContainer<String> getWorkflowsDataSource() {
		return getMainDoc().getAllRepoWorkflowItemIdsDataSource();
	}

	public BeanItemContainer<String> getNotesDataSource() {
		return getMainDoc().getNotesDataSource();
	}
	
	public void updateNotesDataSourceToSelectedWorkflow(String workflow) {
		getMainDoc().updateNotes(workflow);
	}

	public String getCurrentWorkflow() {
		return getMainDoc().getTemplateBean().getTemplate() != null ? 
				getMainDoc().getTemplateBean().getTemplate().getJobListElement().getId(): "<null>";
	}

	public void applyWorkflowChanges(String wkfl, String note, String displayName) {
		try {
			JobList jobList = getMainDoc().getTemplateBean().getTemplate().getJobListElement();
			jobList.setID(wkfl);
			jobList.setWorkflowNotes(note);
			jobList.setDisplayName(displayName);
			getMainDoc().renameAndReloadComplete();		
		} catch (Exception e) {
			LOGGER.error("Could not rename workflow.", e);
		}
	}

	public Container getJobsDataSource() {
		return _allParserJobIDsDataSource;
	}

	public Container getPortsToJobDatasource() {
		return _portsToJobDataSource;
	}

	public Property getSelectedJobDataSource() {
		return _selectedJob;
	}

	public Property getSelectedPortDataSource() {
		return _selectedPort;
	}

	public Property getHasParserDataSource() {
		return _hasParserConfig;
	}

	public Property getDescriptionDataSource() {
		return _descriptionDataSource;
	}

	public Property getTitleDataSource() {
		return _titleDataSource;
	}
}
