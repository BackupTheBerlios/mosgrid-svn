package de.mosgrid.portlet;

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import hu.sztaki.lpds.pgportal.services.asm.constants.RepositoryItemTypeConstants;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.google.gwt.xml.client.Node;
import com.vaadin.ui.Component;
import com.vaadin.ui.ProgressIndicator;

import de.mosgrid.adapter.AdapterException;
import de.mosgrid.adapter.AdapterFactoryForAdapConfig.InitializationException;
import de.mosgrid.adapter.InputInfoHolder;
import de.mosgrid.adapter.InputSupplyException;
import de.mosgrid.adapter.base.AbstractAdapterFactoryBase;
import de.mosgrid.adapter.defaults.DefaultAdapterFactory;
import de.mosgrid.exceptions.ImportFailedException;
import de.mosgrid.exceptions.PortletInitializationException;
import de.mosgrid.exceptions.RemovingFailedException;
import de.mosgrid.exceptions.SetURLException;
import de.mosgrid.exceptions.SubmissionFailedException;
import de.mosgrid.gui.DefaultWorkflowDescription;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.jaxb.bindings.DescriptionType;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobListParserConfig;
import de.mosgrid.util.DefaultInputMaskFactory;
import de.mosgrid.util.DefaultTemplateManager;
import de.mosgrid.util.DefaultUploadPostprocessorFactory;
import de.mosgrid.util.MosgridProperties;
import de.mosgrid.util.TempFileHelper;
import de.mosgrid.util.UploadBean;
import de.mosgrid.util.UploadCollector;
import de.mosgrid.util.WorkflowHelper;
import de.mosgrid.util.XfsBridge;
import de.mosgrid.util.interfaces.IInputMaskFactory;
import de.mosgrid.util.interfaces.ITemplateManager;
import de.mosgrid.util.interfaces.IUploadPostprocessorFactory;

/**
 * Abstract class for a domain specific portlets.
 * 
 * @author Andreas Zink
 * 
 */

public abstract class DomainPortlet extends MoSGridPortlet {
	private static final long serialVersionUID = -1147149562874129275L;
	private final Logger LOGGER = LoggerFactory.getLogger(DomainPortlet.class);

	// identifies the domain
	private DomainId domainId;
	// the adapter factory for creating input files from user input
	private AbstractAdapterFactoryBase adapterFactory;
	private IUploadPostprocessorFactory uploadPostprocessor;
	// maps descriptions for importables
	private Map<ImportableWorkflow, Component> workflowDescriptions;

	/**
	 * Default constructur. Please set the correct domain ID in order to initialize your domain correctly.
	 */
	public DomainPortlet(DomainId domainId) {
		this.domainId = domainId;
	}

	@Override
	protected void beforeApplicationInit() throws PortletInitializationException {
		LOGGER.info(getUser() + " Initializing " + domainId.getName() + " portlet.");
	}

	@Override
	protected void beforeUiInit() throws PortletInitializationException {
		try {
			DictionaryFactory.getInstance().update();
			getTemplateManager().update();
		} catch (Exception e) {
			// just catch any exceptions...
			String msg = "Portlet initialization failed!";
			LOGGER.error(msg, e);
			throw new PortletInitializationException(msg, e);
		}
		loadImportableWorkflows();
		handleImportsFromLastSession();
	}

	@Override
	protected void afterApplicationInit() throws PortletInitializationException {
		// nothing by now
	}

	/**
	 * Loads all importable workflows
	 * 
	 * @throws PortletInitializationException
	 */
	private void loadImportableWorkflows() throws PortletInitializationException {
		try {
			LOGGER.debug(getUser() + " Loading importable workflows...");

			this.importableWorkflows = new ArrayList<ImportableWorkflow>();
			Vector<ASMRepositoryItemBean> workflowList = null;
			try {

				workflowList = getAsmService().getWorkflowsFromRepository(getUser().getUserID(),
						RepositoryItemTypeConstants.Workflow);
			} catch (Exception e) {
				String msg = " Unable to refresh workflows from ASM repository";
				LOGGER.error(getUser() + msg, e);
				throw new PortletInitializationException(msg, e);
			}

			// iterate over all workflow beans in repository
			for (ASMRepositoryItemBean workflowBean : workflowList) {
				// iterate over all templates which belong to this workflow
				Collection<MSMLTemplate> templatesList = getTemplateManager().getTemplatesByWorkflow(workflowBean);
				for (MSMLTemplate template : templatesList) {
					ImportableWorkflow importable = new ImportableWorkflow(domainId, template, workflowBean);
					importableWorkflows.add(importable);
				}
			}

			LOGGER.trace(getUser() + " Loaded " + importableWorkflows.size() + " importable workflows");
		} catch (Exception e) {
			String msg = " Error while loading importable workflows. Please make sure that gUSE has been initialized.";
			LOGGER.error(getUser() + msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * Calls a method to delete or load imports from last session, depending on the return value of
	 * deleteImportsFromLastSession()
	 * 
	 * @throws PortletInitializationException
	 */
	private void handleImportsFromLastSession() throws PortletInitializationException {
		if (deleteImportsFromLastSession()) {
			removeNotSubmittedWkfs();
		} else {
			loadNotSubmittedWkfs();
		}
	}

	/**
	 * Helper method for loading wkfs in INIT state from last session
	 * 
	 * @throws PortletInitializationException
	 */
	private void loadNotSubmittedWkfs() throws PortletInitializationException {
		try {
			LOGGER.debug(getUser() + " Loading not submitted workflows from last session...");

			// get all workflows in INIT status
			Collection<ASMWorkflow> allWorkflows = getAllWorkflows(StatusConstants.INIT, true);
			for (ASMWorkflow asmInstance : allWorkflows) {
				LOGGER.trace("Loading: " + asmInstance.getWorkflowName());

				// resolve corresponding template
				MSMLTemplate template = getTemplateManager().getTemplateByWorkflow(asmInstance);

				if (template == null) {
					// delete import if template could not be resolved
					LOGGER.warn("Could not resolve template for " + asmInstance.getWorkflowName()
							+ " (from last user-session). Deleting imported workflow instance.");
					getAsmService().DeleteWorkflow(getUser().getUserID(), asmInstance.getWorkflowName());
					continue;
				} else {
					ImportedWorkflow imported = new ImportedWorkflow(domainId, asmInstance, template);
					// create input mask
					try {
						AbstractInputMask inputMask = getInputMaskFactory().createInputMask(imported);
						imported.setInputMask(inputMask);
					} catch (Exception e) {
						LOGGER.warn("Could not create input mask for " + asmInstance.getWorkflowName()
								+ " (from last user-session). Deleting imported workflow instance.", e);
						getAsmService().DeleteWorkflow(getUser().getUserID(), asmInstance.getWorkflowName());
						continue;
					}
					// add to list of imports and inform listeners
					importedWorkflows.add(imported);
					fireImportSucceeded(imported);
				}
			}
		} catch (Exception e) {
			String msg = " Error while loading imported workflows from last session";
			LOGGER.error(getUser() + msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * This method shall create descriptions for all available templates. You can get these with 'getAllTemplates()'.
	 */
	public Map<ImportableWorkflow, Component> createWkfDescriptions() {
		if (workflowDescriptions == null) {
			workflowDescriptions = new HashMap<ImportableWorkflow, Component>();
			for (ImportableWorkflow importable : getImportableWorkflows()) {
				Component c = createWkfDescription(importable);
				workflowDescriptions.put(importable, c);
			}
		}
		return workflowDescriptions;
	}

	/**
	 * @param importable
	 * @return A description component for given Importable. Creates DefaultWorkflowDescription but may be overwridden
	 *         in subclasses.
	 */
	protected Component createWkfDescription(ImportableWorkflow importable) {
		StringBuilder descriptionBuilder = new StringBuilder();
		DescriptionType descElement = importable.getTemplate().getJobListElement().getDescription();
		if (descElement != null && descElement.getAny().size() > 0) {
			for (Element child : descElement.getAny()) {
				if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
					descriptionBuilder.append(child.getNodeValue().trim() + "<br>");
				} else {
					descriptionBuilder.append(child.getTextContent().trim() + "<br>");
				}
			}
		}

		String fullGraphPath = null;
		String workflowGraphImageName = importable.getTemplate().getJobListElement().getWorkflowGraphName();
		if (workflowGraphImageName != null) {
			String parentPath = domainId.getThemeDir()
					+ MosgridProperties.REL_CUSTOM_THEME_WKF_GRAPHS_PATH.getProperty();
			fullGraphPath = parentPath + workflowGraphImageName;
		}

		return new DefaultWorkflowDescription(descriptionBuilder.toString(), fullGraphPath);
	}

	/**
	 * This method shall return the input mask factory to be used for input mask creation. This method may be overridden
	 * if you want to use any other than the DefaultInputMaskFactory.
	 */
	protected IInputMaskFactory getInputMaskFactory() {
		return new DefaultInputMaskFactory(this);
	}

	/**
	 * This function returns an instance of the AdapterFactory to be used by the DomainPortlet. Subclasses of the
	 * DomainPortlet may override this method to instanciate their own implementation.
	 * 
	 * @return An instance of AdapterFactory. Normaly it is the {@link DefaultAdapterFactory}.
	 */
	protected AbstractAdapterFactoryBase createAdapterFactoryInstance() {
		return new DefaultAdapterFactory();
	}

	/**
	 * The return value of this method decides if imports from last session will be deleted or kept
	 * 
	 * @return 'true' if workflow imports from last session shall be deleted before startup
	 */
	public abstract boolean deleteImportsFromLastSession();

	/**
	 * @return The TemplateManager for this domain. You may override this method to provide any other than the
	 *         DefaultTemplateManager.
	 */
	public ITemplateManager getTemplateManager() {
		return DefaultTemplateManager.getInstance(getDomainId());
	}

	/**
	 * @return The upload postprocessor factory which is used by upload components. By default the
	 *         DefaultUploadPostprocessorFactory is set.
	 */
	public IUploadPostprocessorFactory getUploadPostprocessorFactory() {
		if (uploadPostprocessor == null) {
			uploadPostprocessor = new DefaultUploadPostprocessorFactory();
		}
		return uploadPostprocessor;
	}

	/**
	 * @param Sets
	 *            the upload postprocessor factory to be used by upload components
	 */
	public void setUploadPostprocessorFactory(IUploadPostprocessorFactory factory) {
		this.uploadPostprocessor = factory;
	}

	/**
	 * @return All templates for this domain
	 */
	public Collection<MSMLTemplate> getAllTemplates() {
		return getTemplateManager().getAllTemplates();
	}

	/**
	 * Removes an importet workflow
	 * 
	 * @param wkfImport
	 *            The workflow to remove
	 * @throws RemovingFailedException
	 */
	public void removeWorkflowImport(final ImportedWorkflow wkfImport, final ProgressIndicator progressIndicator) {
		// Create a new thread for this task in order to prevent UI from 'freezing'
		Runnable removeTask = new Runnable() {
			@Override
			public void run() {
				try {
					LOGGER.debug(getUser() + " Trying to remove " + wkfImport.getAsmInstance().getWorkflowName());

					// update progress
					if (progressIndicator != null) {
						// this value seems quite hight, but removing goes fast
						progressIndicator.setValue(new Float(0.3));
					}
					// remove
					try {
						// delete from asm
						removeASMInstance(wkfImport.getAsmInstance());
						// delete from imported lists
						importedWorkflows.remove(wkfImport);
					} catch (Exception e) {
						String message = " Failed to remove " + wkfImport.getAsmInstance().getWorkflowName();
						LOGGER.error(getUser() + message, e);
						throw new RemovingFailedException(message, e);
					}
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(1.0));
					}

					LOGGER.debug(getUser() + "Successfully removed " + wkfImport.getAsmInstance().getWorkflowName());
					// inform listeners
					fireRemovalSucceeded(wkfImport);
				} catch (RemovingFailedException e) {
					// inform listeners
					fireRemovalFailed(wkfImport, e);
				} catch (Exception e) {
					LOGGER.error(getUser() + " Failed to remove imported workflow!\n" + e.getMessage(), e);
					// inform listeners
					fireRemovalFailed(wkfImport, new RemovingFailedException(e.getMessage(), e));
				}
			}
		};

		getExecutorService().execute(removeTask);
	}

	public void submitWorkflowImport(final ImportedWorkflow wkfImport, final UploadCollector collector,
			final ProgressIndicator progressIndicator) {
		// Create a new thread for this task in order to prevent UI from 'freezing'
		Runnable submissionTask = new Runnable() {

			@Override
			public void run() {
				try {
					String instanceId = wkfImport.getAsmInstance().getWorkflowName();
					LOGGER.debug(getUser() + " Trying to submit workflow instance " + instanceId);

					/*
					 * 1) Start the adaptor and generate for each job 
					 * 1.1) in case input-file has to be added to uplaod collector 1. 
					 * 1.2) in case parameter is a string -> directly set it over asm
					 */
					InputInfoHolder infos;
					try {
						infos = getAdapterFactory().createInputInformation(wkfImport.getTemplate());
						infos.addUploads(collector);
					} catch (AdapterException e) {
						String message = null;
						if (e.getMessage() != null) {
							message = e.getMessage();
						} else {
							message = " Inputinfo could not be created.";
						}
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					} catch (InputSupplyException e) {
						String message = null;
						if (e.getMessage() != null) {
							message = e.getMessage();
						} else {
							message = " Inputfiles could not be added to collector.";
						}
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					}
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.2));
					}

					/*
					 * 2) Create MSML from the Template -> add to UploadCollector (Port and Job-Id from the
					 * parserConfig-Block under the job list)
					 */
					createMSML(wkfImport, collector);
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.4));
					}
					/*
					 * 3) Loads all UploadCollectors in XtreemFS and obtains all URLs added in 
					 * the Collector.
					 */
					try {
						uploadFilesToXfs(wkfImport.getAsmInstance(), collector);
					} catch (InputSupplyException e) {
						String message = null;
						if (e.getMessage() != null) {
							message = e.getMessage();
						} else {
							message = " Upload of files to XtreemFS failed.";
						}
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					}
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.6));
					}

					/*
					 * 4) Set job Parameter, Remote Path, environment variables etc.
					 */
					try {
						// set remote urls of job ports
						setPortEnvironment(wkfImport.getAsmInstance(), collector.getAllUploads());
						// set cmd line params
						infos.setInputParams(wkfImport.getAsmInstance());
						// set environment variables
						setEnvironmentParameters(wkfImport);

						// TODO Make this work.
						// outputBasePath = xtreemfs://<homeDir>/output/<wkfname>
						// String outputBasePath = getXfsWorkflowBasePath(wkfInstance, "output");
						// List<String> pathsToCreate = infos.setOuputRemotes(wkfInstance, outputBasePath);
						// for (String path : pathsToCreate) {
						// createXfsDirs(path);
						// }

					} catch (SetURLException e) {
						String msg = null;
						if (e.getMessage() != null) {
							msg = e.getMessage();
						} else {
							msg = " URLs could not be set on remote ports.";
						}
						LOGGER.error(getUser() + msg, e);
						throw new SubmissionFailedException(msg, e);
					} catch (InputSupplyException e) {
						String msg = null;
						if (e.getMessage() != null) {
							msg = e.getMessage();
						} else {
							msg = " Parameter could not be set on jobs.";
						}
						LOGGER.error(getUser() + msg, e);
						throw new SubmissionFailedException(msg, e);
						// } catch (IOException e) {
						// LOGGER.error("Directory for ouputs could not be created.", e);
						// return false;
					}
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.8));
					}
					/*
					 * 5) Save & Submit
					 */

					try {
						if (!getAsmService().isAutoSave()) {
							LOGGER.debug(getUser() + " Saving workflow configuration "
									+ wkfImport.getAsmInstance().getWorkflowName());
							// save manually if auto save is off
							getAsmService().saveWorkflowSettings(getUser().getUserID(), wkfImport.getAsmInstance());
						}
						getAsmService().submit(getUser().getUserID(), wkfImport.getAsmInstance().getWorkflowName());
					} catch (ClassNotFoundException e) {
						String message = " ClassNotFoundException while submitting workflow.";
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					} catch (InstantiationException e) {
						String message = " InstantiationException while submitting workflow.";
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					} catch (IllegalAccessException e) {
						String message = " IllegalAccessException while submitting workflow.";
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					}

					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(1.0));
					}
					// delete from imported lists
					importedWorkflows.remove(wkfImport);

					LOGGER.debug(getUser() + "Successfully submitted " + wkfImport.getAsmInstance().getWorkflowName());
					// inform listeners
					fireSubmissionSucceeded(wkfImport);

				} catch (SubmissionFailedException e) {
					// delete asm instance
					removeASMInstance(wkfImport.getAsmInstance());
					// delete from imported lists
					importedWorkflows.remove(wkfImport);
					// inform listeners
					fireSubmissionFailed(wkfImport, e);
				} catch (Exception e) {
					LOGGER.error(getUser() + " Error while submitting!", e);
					// delete asm instance
					removeASMInstance(wkfImport.getAsmInstance());
					// delete from imported lists
					importedWorkflows.remove(wkfImport);
					// inform listeners
					fireSubmissionFailed(wkfImport, new SubmissionFailedException(e.getMessage(), e));
				} finally {
					// delete temp files
					collector.deleteTempFiles();
				}
			}

		};
		getExecutorService().execute(submissionTask);
	}

	/**
	 * Helper method which sets the remote URL of a job to point at a file in xfs. Additionally sets the number of
	 * executions in parameter sweep case i.e. more than one input file for one port
	 */
	private void setPortEnvironment(ASMWorkflow wkfInstance, Collection<UploadBean> uploadBeansList)
			throws SetURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		// clean up remote settings first
		cleanRemoteURLs(wkfInstance);

		for (UploadBean uploadBean : uploadBeansList) {
			LOGGER.debug("uploadBean=[" + uploadBean.toString() + ']');
			if (uploadBean.getUrl() != null) {
				ASMJob job = wkfInstance.getJobs().get(uploadBean.getJobname());
				
				if (job == null)
					throw new SetURLException("Could not find job " + uploadBean.getJobname());
				
				final String portNumber = Long.toString(getAsmService().getInputPortNumberByName(
						getUser().getUserID(), 
						wkfInstance.getWorkflowName(), 
						uploadBean.getJobname(), 
						uploadBean.getPort()));
				
//				// TODO: refactor port mapping
//				String portNumber = null;
//				for (Entry<String, String> entry : job.getInput_ports().entrySet()) {
//					LOGGER.debug("jobName=" + job.getJobname() + ", entry.key=" + entry.getKey() + ", entry.value=" + entry.getValue());
//					// UploadBean contains the name of the port, not its sequential number, however, ASMJob contains only 
//					// the sequence numbers!					
//					if (("PORT" + entry.getKey()).equals(uploadBean.getPort())) {
//						portNumber = entry.getKey();
//					}
//				}
//				if (portNumber == null) {
//					throw new SetURLException("Could not find port " + uploadBean.getPort() + " on job "
//							+ job.getJobname());
//				}
				LOGGER.trace(getUser() + " Setting Remote URL " + uploadBean);

				getAsmService().setRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(),
						job.getJobname(), portNumber, uploadBean.getUrl());

				// increase number of executions in parameter sweep case
				if (uploadBean.getFiles().size() > 1) {
					int numberOxExecutions = uploadBean.getFiles().size();
					LOGGER.trace(getUser() + " Setting number of executions to " + numberOxExecutions);
					getAsmService().setNumberOfInputFiles(getUser().getUserID(), wkfInstance.getWorkflowName(),
							job.getJobname(), portNumber, numberOxExecutions);
				}
			}
		}
	}

	/**
	 * Helper method which cleans up all remote settings. If a remote path points to a xfs url but does not match for
	 * the current user it is deleted. Not deleting it if url fits for current user is good for testing purposes.
	 */
	private void cleanRemoteURLs(ASMWorkflow wkfInstance) {
		// retrieve xfs home url for current user
		String xfsHomeUrl = XfsBridge.createURL(getXfsBridge().getHomeDir());

		for (ASMJob job : wkfInstance.getJobs().values()) {
			for (String portNumber : job.getInput_ports().keySet()) {

				String currentRemoteURL = getAsmService().getRemoteInputPath(getUser().getUserID(),
						wkfInstance.getWorkflowName(), job.getJobname(), portNumber);
				if (currentRemoteURL != null && currentRemoteURL.startsWith(XfsBridge.XFS_URL_PREFIX)) {
					// remote url is set and points to xfs
					if (!currentRemoteURL.startsWith(xfsHomeUrl) && 
							!currentRemoteURL.startsWith(XfsBridge.XFS_URL_PREFIX + "test/")) {
						// remote url does not match for current user, thus delete
						LOGGER.trace(getUser()
								+ " Deleting XFS-Remote-URL because it does not match for current user \n\tJOB: "
								+ job.getJobname() + "\n\tPORT: " + portNumber + "\n\tURL: " + currentRemoteURL);
						// TODO: set to "", null or delete port??
						getAsmService().setRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(),
								job.getJobname(), portNumber, "");
					}
				}
			}
			for (String portName : job.getOutput_ports().keySet()) {

				String currentRemoteURL = getAsmService().getRemoteInputPath(getUser().getUserID(),
						wkfInstance.getWorkflowName(), job.getJobname(), portName);
				if (currentRemoteURL != null && currentRemoteURL.startsWith(XfsBridge.XFS_URL_PREFIX)) {
					// remote url is set and points to xfs
					if (!currentRemoteURL.startsWith(xfsHomeUrl)) {
						// remote url does not match for current user, thus delete
						LOGGER.trace(getUser()
								+ " Deleting XFS-Remote-URL because it does not match for current user \n\tJOB: "
								+ job.getJobname() + "\n\tPORT: " + portName + "\n\tURL: " + currentRemoteURL);
						// TODO: set to "", null or delete port??
						getAsmService().setRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(),
								job.getJobname(), portName, "");
					}
				}
			}
		}
	}

	/**
	 * Helper method for creating the actual msml file from msml template. At first the template is 'marshalled' to a
	 * temporary file. Next, the template is searched for the ParserParameter element below the JobList element in order
	 * to retrieve the Id of the first parser job and port. This is needed to decide to which job the msml file shall be
	 * uploaded to. At last, the msml file gets added to the UploadCollector.
	 * 
	 * @throws SubmissionFailedException
	 */
	private void createMSML(ImportedWorkflow wkfImport, UploadCollector collector) throws SubmissionFailedException {
		LOGGER.debug(getUser() + " Creating MSML file from template");

		String msmlFileName = WorkflowHelper.getInstance().getUserChosenName(wkfImport.getAsmInstance());
		File msmlFile = null;

		try {
			msmlFile = TempFileHelper.createTempFile(msmlFileName, ".xml");
			LOGGER.trace(getUser() + " Writing template to temporary file " + msmlFile);
			// marshall template to temp file
			wkfImport.getTemplate().marshallTo(msmlFile);
		} catch (IOException e) {
			String msg = " Unable to create temporary msml file from template";
			LOGGER.error(getUser() + msg, e);
			throw new SubmissionFailedException(msg, e);
		}

		if (msmlFile != null) {
			LOGGER.trace(getUser() + " Parsing job-Id and port of first parser job which shall get the msml file");

			JobListParserConfig parserConfig = wkfImport.getTemplate().getJobListElement().getParserConfig();
			if (parserConfig == null) {
				LOGGER.warn(getUser()
						+ " No ParserConfig element given below JobList element! MSML will not be passed into workflow!"
						+ " Template file: " + wkfImport.getTemplate().getPath());
				try {
					// delete temporary template file
					msmlFile.delete();
				} catch (Exception e) {
					LOGGER.info(getUser() + " Unable to delete temporary MSML file.");
				}

			} else {
				String parserJobId = parserConfig.getJobName();
				String parserJobPort = parserConfig.getPortName();
				if (parserJobId != null && parserJobPort != null) {
					// Add msml file to upload collector
					collector.addUpload(msmlFile, parserJobPort, parserJobId);
				} else {
					LOGGER.warn(getUser()
							+ " ParserConfig element does not contain desired parameters. MSML will not be passed into workflow!");
					try {
						// delete temporary template file
						msmlFile.delete();
					} catch (Exception e) {
						LOGGER.info(getUser() + " Unable to delete temporary MSML file.");
					}
				}
			}
		}
	}

	private AbstractAdapterFactoryBase getAdapterFactory() {
		if (adapterFactory != null)
			return adapterFactory;
		adapterFactory = createAdapterFactoryInstance();
		try {
			adapterFactory.init();
		} catch (InitializationException e) {
			LOGGER.error(getUser() + " Adapterfactory should be instanciated only once.", e);
		}
		return adapterFactory;
	}

	/**
	 * Helper method which uploads all collected files to xfs. Creates a new directory with the user given import name
	 * below the xfs upload dir and places all files there.
	 */
	private void uploadFilesToXfs(ASMWorkflow wkfInstance, UploadCollector collector) throws InputSupplyException {
		LOGGER.debug(getUser() + " Uploading files to XFS for " + wkfInstance.getWorkflowName());

		for (UploadBean uploadBean : collector.getAllUploads()) {
			String jobname = uploadBean.getJobname();
			ASMJob job = wkfInstance.getJobs().get(jobname);
			if (job != null) {
				// check if this is a real upload. UploadBeans may also just be links to xfs
				if (uploadBean.hasFiles()) {
					for (File file : uploadBean.getFiles()) {
						String importName = WorkflowHelper.getInstance().getUserChosenName(wkfInstance);
						String xfsPath = getXfsBridge().getUploadDir() + "/" + importName + "/" + jobname + "/";
						String newFilename = TempFileHelper.removeTempSuffix(file);
						String xfsUrl = XfsBridge.createURL(xfsPath + newFilename);

						LOGGER.trace(getUser() + " Uploading:\n\tFILE: " + file + "\n\tURL: " + xfsUrl);
						BufferedInputStream in = null;
						BufferedOutputStream out = null;
						try {
							in = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
							out = new BufferedOutputStream(getXfsBridge().getUploadStream(newFilename, xfsPath));

							byte[] buf = new byte[4096];
							int len;
							while ((len = in.read(buf)) > 0) {
								out.write(buf, 0, len);
							}

						} catch (FileNotFoundException e1) {
							LOGGER.error(getUser() + " FileNotFoundException while uploading file to XtreemFS: ", e1);
							throw new InputSupplyException("Could not access " + file.getAbsolutePath());
						} catch (IOException e1) {
							LOGGER.error(getUser() + " IOException while uploading file to XtreemFS: ", e1);
							throw new InputSupplyException("Could not write " + file.getAbsolutePath() + " to "
									+ xfsUrl);
						} finally {
							try {
								if (in != null) {
									in.close();
								}
								if (out != null) {
									out.close();
								}
							} catch (IOException e1) {
								LOGGER.error(getUser() + " Could not close stream", e1);
							}
						}

						// set real xfs url of upload which must not contain an index in case of parameter sweep
						// workflows
						if (uploadBean.getUrl() == null) {
							if (uploadBean.getFiles().size() > 1) {
								String filenameWithoutIndex = TempFileHelper.removeTempSuffixAndIndex(file);
								xfsUrl = XfsBridge.createURL(xfsPath + filenameWithoutIndex);
							}
							uploadBean.setUrl(xfsUrl);
						}
					}
				}
			} else {
				LOGGER.warn(getUser() + " Could not find job '" + jobname + "' in ASM workflow instance.");
			}
		}
	}

	/**
	 * Helper method which sets the given environmental parameters.
	 */
	private void setEnvironmentParameters(ImportedWorkflow wkfImport) {
		for (Job jobElement : wkfImport.getTemplate().getJobListElement().getJobs()) {
			if (jobElement.getEnvironment() != null) {
				ASMJob asmJob = wkfImport.getAsmInstance().getJobs().get(jobElement.getId());
				if (asmJob == null) {
					// actually this should never happen
					LOGGER.warn(getUser() + " Template defines job " + jobElement.getId()
							+ " which can not be resolved in workflow.");
					continue;
				}
				LOGGER.debug(getUser() + " Setting environment parameters for job " + jobElement.getId());

				Integer numberOfNodes = jobElement.getEnvironment().getNumberOfNodes();
				if (numberOfNodes != null) {
					LOGGER.trace(getUser() + " Setting number of nodes to " + numberOfNodes);
					getAsmService().setNodeNumber(getUser().getUserID(), wkfImport.getAsmInstance().getWorkflowName(),
							asmJob.getJobname(), numberOfNodes);
				}

				Integer numberOfCores = jobElement.getEnvironment().getNumberOfCores();
				if (numberOfCores != null) {
					LOGGER.trace(getUser() + " Setting number of cores to " + numberOfCores);
					getAsmService().setCoreNumber(getUser().getUserID(), wkfImport.getAsmInstance().getWorkflowName(),
							asmJob.getJobname(), numberOfCores.toString());
				}

				Integer memory = jobElement.getEnvironment().getMemoryValue();
				if (memory != null) {
					LOGGER.trace(getUser() + " Setting memory to " + memory + " MB");
					getAsmService().setMemory(getUser().getUserID(), wkfImport.getAsmInstance().getWorkflowName(),
							asmJob.getJobname(), memory.toString());
				}

				Integer walltime = jobElement.getEnvironment().getWalltimeValue();
				if (walltime != null) {
					LOGGER.trace(getUser() + " Setting walltime to " + walltime);
					getAsmService().setWalltime(getUser().getUserID(), wkfImport.getAsmInstance().getWorkflowName(),
							asmJob.getJobname(), walltime.toString());

				}
				// LOGGER.debug(asmJob.dumpProperties());
			}
		}
	}

	/**
	 * @return The given domain id
	 */
	public DomainId getDomainId() {
		return domainId;
	}

	/**
	 * @return All domain specific dictionaries
	 */
	public Collection<IDictionary> getToolsuiteDictionaries() {
		return DictionaryFactory.getInstance().getDictionaries(getDomainId().getDictDir());
	}

	/**
	 * @return Return a new instance of ImportedWorkflow with the current domain.
	 */
	@Override
	protected ImportedWorkflow retrieveDomainDependandWorkflow(ASMWorkflow workflowInstance, MSMLTemplate template) {
		return new ImportedWorkflow(getDomainId(), workflowInstance, template);
	}

	@Override
	protected void afterImport(ImportedWorkflow newImport) throws ImportFailedException {
		// create input mask for newly imported workflow instance
		try {
			LOGGER.trace(getUser() + " Creating Inputmask");
			AbstractInputMask inputMask = getInputMaskFactory().createInputMask(newImport);
			newImport.setInputMask(inputMask);
		} catch (Exception e) {
			String message = getUser() + " Creating inputmask failed for workflow: " + newImport.getUserImportName()
					+ "\n";
			LOGGER.error(message, e);
			throw new ImportFailedException(message, e);
		}
	}
}
