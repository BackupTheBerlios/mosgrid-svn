package de.mosgrid.portlet;

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import hu.sztaki.lpds.pgportal.services.asm.constants.RepositoryItemTypeConstants;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
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
import de.mosgrid.msml.util.MSMLProperties;
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
	private final DomainId domainId;
	// the adapter factory for creating input files from user input
	private AbstractAdapterFactoryBase adapterFactory;
	private IUploadPostprocessorFactory uploadPostprocessor;
	// maps descriptions for importables
	private Map<ImportableWorkflow, Component> workflowDescriptions;

	/**
	 * Default constructur. Please set the correct domain ID in order to
	 * initialize your domain correctly.
	 */
	public DomainPortlet(final DomainId domainId) {
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
		} catch (final Exception e) {
			// just catch any exceptions...
			final String msg = "Portlet initialization failed!";
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

				workflowList = getAsmService().getWorkflowsFromRepository(getUser().getUserID(), RepositoryItemTypeConstants.Workflow);
			} catch (final Exception e) {
				final String msg = " Unable to refresh workflows from ASM repository";
				LOGGER.error(getUser() + msg, e);
				throw new PortletInitializationException(msg, e);
			}

			// iterate over all workflow beans in repository
			for (final ASMRepositoryItemBean workflowBean : workflowList) {
				// iterate over all templates which belong to this workflow
				final Collection<MSMLTemplate> templatesList = getTemplateManager().getTemplatesByWorkflow(workflowBean);
				for (final MSMLTemplate template : templatesList) {
					final ImportableWorkflow importable = new ImportableWorkflow(domainId, template, workflowBean);
					importableWorkflows.add(importable);
				}
			}

			LOGGER.trace(getUser() + " Loaded " + importableWorkflows.size() + " importable workflows");
		} catch (final Exception e) {
			final String msg = " Error while loading importable workflows. Please make sure that gUSE has been initialized.";
			LOGGER.error(getUser() + msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * Calls a method to delete or load imports from last session, depending on
	 * the return value of deleteImportsFromLastSession()
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
			final Collection<ASMWorkflow> allWorkflows = getAllWorkflows(StatusConstants.INIT, true);
			for (final ASMWorkflow asmInstance : allWorkflows) {
				LOGGER.trace("Loading: " + asmInstance.getWorkflowName());

				// resolve corresponding template
				final MSMLTemplate template = getTemplateManager().getTemplateByWorkflow(asmInstance);

				if (template == null) {
					// delete import if template could not be resolved
					LOGGER.warn("Could not resolve template for " + asmInstance.getWorkflowName()
							+ " (from last user-session). Deleting imported workflow instance.");
					getAsmService().DeleteWorkflow(getUser().getUserID(), asmInstance.getWorkflowName());
					continue;
				} else {
					final ImportedWorkflow imported = new ImportedWorkflow(domainId, asmInstance, template);
					// create input mask
					try {
						final AbstractInputMask inputMask = getInputMaskFactory().createInputMask(imported);
						imported.setInputMask(inputMask);
					} catch (final Exception e) {
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
		} catch (final Exception e) {
			final String msg = " Error while loading imported workflows from last session";
			LOGGER.error(getUser() + msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * This method shall create descriptions for all available templates. You
	 * can get these with 'getAllTemplates()'.
	 */
	public Map<ImportableWorkflow, Component> createWkfDescriptions() {
		if (workflowDescriptions == null) {
			workflowDescriptions = new HashMap<ImportableWorkflow, Component>();
			for (final ImportableWorkflow importable : getImportableWorkflows()) {
				final Component c = createWkfDescription(importable);
				workflowDescriptions.put(importable, c);
			}
		}
		return workflowDescriptions;
	}

	/**
	 * @param importable
	 * @return A description component for given Importable. Creates
	 *         DefaultWorkflowDescription but may be overwridden in subclasses.
	 */
	protected Component createWkfDescription(final ImportableWorkflow importable) {
		final StringBuilder descriptionBuilder = new StringBuilder();
		final DescriptionType descElement = importable.getTemplate().getJobListElement().getDescription();
		if (descElement != null && descElement.getAny().size() > 0) {
			for (final Element child : descElement.getAny()) {
				if (child.getNodeType() == Node.CDATA_SECTION_NODE) {
					descriptionBuilder.append(child.getNodeValue().trim() + "<br>");
				} else {
					descriptionBuilder.append(child.getTextContent().trim() + "<br>");
				}
			}
		}

		String fullGraphPath = null;
		final String workflowGraphImageName = importable.getTemplate().getJobListElement().getWorkflowGraphName();
		if (workflowGraphImageName != null) {
			final String parentPath = domainId.getThemeDir() + MosgridProperties.REL_CUSTOM_THEME_WKF_GRAPHS_PATH.getProperty();
			fullGraphPath = parentPath + workflowGraphImageName;
		}

		return new DefaultWorkflowDescription(descriptionBuilder.toString(), fullGraphPath);
	}

	/**
	 * This method shall return the input mask factory to be used for input mask
	 * creation. This method may be overridden if you want to use any other than
	 * the DefaultInputMaskFactory.
	 */
	protected IInputMaskFactory getInputMaskFactory() {
		return new DefaultInputMaskFactory(this);
	}

	/**
	 * This function returns an instance of the AdapterFactory to be used by the
	 * DomainPortlet. Subclasses of the DomainPortlet may override this method
	 * to instanciate their own implementation.
	 * 
	 * @return An instance of AdapterFactory. Normaly it is the
	 *         {@link DefaultAdapterFactory}.
	 */
	protected AbstractAdapterFactoryBase createAdapterFactoryInstance() {
		return new DefaultAdapterFactory();
	}

	/**
	 * The return value of this method decides if imports from last session will
	 * be deleted or kept
	 * 
	 * @return 'true' if workflow imports from last session shall be deleted
	 *         before startup
	 */
	public abstract boolean deleteImportsFromLastSession();

	/**
	 * @return The TemplateManager for this domain. You may override this method
	 *         to provide any other than the DefaultTemplateManager.
	 */
	public ITemplateManager getTemplateManager() {
		return DefaultTemplateManager.getInstance(getDomainId());
	}

	/**
	 * @return The upload postprocessor factory which is used by upload
	 *         components. By default the DefaultUploadPostprocessorFactory is
	 *         set.
	 */
	public IUploadPostprocessorFactory getUploadPostprocessorFactory() {
		if (uploadPostprocessor == null) {
			uploadPostprocessor = new DefaultUploadPostprocessorFactory();
		}
		return uploadPostprocessor;
	}

	/**
	 * @param Sets
	 *            the upload postprocessor factory to be used by upload
	 *            components
	 */
	public void setUploadPostprocessorFactory(final IUploadPostprocessorFactory factory) {
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
		// Create a new thread for this task in order to prevent UI from
		// 'freezing'
		final Runnable removeTask = new Runnable() {
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
					} catch (final Exception e) {
						final String message = " Failed to remove " + wkfImport.getAsmInstance().getWorkflowName();
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
				} catch (final RemovingFailedException e) {
					// inform listeners
					fireRemovalFailed(wkfImport, e);
				} catch (final Exception e) {
					LOGGER.error(getUser() + " Failed to remove imported workflow!\n" + e.getMessage(), e);
					// inform listeners
					fireRemovalFailed(wkfImport, new RemovingFailedException(e.getMessage(), e));
				}
			}
		};

		getExecutorService().execute(removeTask);
	}

	public void submitWorkflowImport(final ImportedWorkflow wkfImport, final UploadCollector collector, final ProgressIndicator progressIndicator) {
		// Create a new thread for this task in order to prevent UI from
		// 'freezing'
		final Runnable submissionTask = new Runnable() {

			@Override
			public void run() {
				try {
					final String instanceId = wkfImport.getAsmInstance().getWorkflowName();
					LOGGER.debug(getUser() + " Trying to submit workflow instance " + instanceId);

					/*
					 * 1) Start the adaptor and generate for each job 1.1) in
					 * case input-file has to be added to uplaod collector 1.
					 * 1.2) in case parameter is a string -> directly set it
					 * over asm
					 */
					InputInfoHolder infos;
					try {
						infos = getAdapterFactory().createInputInformation(wkfImport.getTemplate());
						infos.addUploads(collector);
					} catch (final AdapterException e) {
						String message = null;
						if (e.getMessage() != null) {
							message = e.getMessage();
						} else {
							message = " Inputinfo could not be created.";
						}
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					} catch (final InputSupplyException e) {
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
					 * 2) Create MSML from the Template -> add to
					 * UploadCollector (Port and Job-Id from the
					 * parserConfig-Block under the job list)
					 */
					createMSML(wkfImport, collector);
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.4));
					}
					/*
					 * 3) Loads all UploadCollectors in XtreemFS and obtains all
					 * URLs added in the Collector.
					 */
					try {
						uploadFilesToXfs(wkfImport.getAsmInstance(), collector);
					} catch (final InputSupplyException e) {
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
					 * 4) Set job Parameter, Remote Path, environment variables
					 * etc.
					 */
					try {
						// set remote urls of job ports
						setPortEnvironment(wkfImport.getAsmInstance(), collector.getAllUploads());
						// set cmd line params
						infos.setInputParams(wkfImport.getAsmInstance(), getUser().getUserID());
						// set environment variables
						setEnvironmentParametersBatch(wkfImport);

						// TODO Make this work.
						// outputBasePath =
						// xtreemfs://<homeDir>/output/<wkfname>
						// String outputBasePath =
						// getXfsWorkflowBasePath(wkfInstance, "output");
						// List<String> pathsToCreate =
						// infos.setOuputRemotes(wkfInstance, outputBasePath);
						// for (String path : pathsToCreate) {
						// createXfsDirs(path);
						// }

					} catch (final SetURLException e) {
						String msg = null;
						if (e.getMessage() != null) {
							msg = e.getMessage();
						} else {
							msg = " URLs could not be set on remote ports.";
						}
						LOGGER.error(getUser() + msg, e);
						throw new SubmissionFailedException(msg, e);
					} catch (final InputSupplyException e) {
						String msg = null;
						if (e.getMessage() != null) {
							msg = e.getMessage();
						} else {
							msg = " Parameter could not be set on jobs.";
						}
						LOGGER.error(getUser() + msg, e);
						throw new SubmissionFailedException(msg, e);
						// } catch (IOException e) {
						// LOGGER.error("Directory for ouputs could not be created.",
						// e);
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
						saveWorkflowSettings(getUser().getUserID(), wkfImport.getAsmInstance());
						// if (!getAsmService().isAutoSave()) {
						// LOGGER.debug(getUser() +
						// " Saving workflow configuration "
						// + wkfImport.getAsmInstance().getWorkflowName());
						// // save manually if auto save is off
						// getAsmService().saveWorkflowSettings(getUser().getUserID(),
						// wkfImport.getAsmInstance());
						// }
						getAsmService().submit(getUser().getUserID(), wkfImport.getAsmInstance().getWorkflowName());
					} catch (final ClassNotFoundException e) {
						final String message = " ClassNotFoundException while submitting workflow.";
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					} catch (final InstantiationException e) {
						final String message = " InstantiationException while submitting workflow.";
						LOGGER.error(getUser() + message, e);
						throw new SubmissionFailedException(message, e);
					} catch (final IllegalAccessException e) {
						final String message = " IllegalAccessException while submitting workflow.";
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

				} catch (final SubmissionFailedException e) {
					// delete asm instance
					removeASMInstance(wkfImport.getAsmInstance());
					// delete from imported lists
					importedWorkflows.remove(wkfImport);
					// inform listeners
					fireSubmissionFailed(wkfImport, e);
				} catch (final Exception e) {
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

	public void saveWorkflowSettings(final String userID, final ASMWorkflow wkfInstance) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		final Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, wkfInstance.getWorkflowName());
		saveConfigData(userID, wkfInstance.getWorkflowName(), jobs);
	}

	/**
	 * Helper method which sets the remote URL of a job to point at a file in
	 * xfs. Additionally sets the number of executions in parameter sweep case
	 * i.e. more than one input file for one port
	 */
	private void setPortEnvironment(final ASMWorkflow wkfInstance, final Collection<UploadBean> uploadBeansList) throws SetURLException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		// clean up remote settings first
		cleanRemoteURLs(wkfInstance);

		for (final UploadBean uploadBean : uploadBeansList) {
			LOGGER.debug("uploadBean=[" + uploadBean.toString() + ']');
			if (uploadBean.getUrl() != null) {
				final ASMJob job = wkfInstance.getJobs().get(uploadBean.getJobname());

				if (job == null) {
					throw new SetURLException("Could not find job " + uploadBean.getJobname());
				}

				final String portNumber = Long.toString(getInputPortNumberByName(getUser().getUserID(), wkfInstance.getWorkflowName(), uploadBean.getJobname(),
						uploadBean.getPort()));

				LOGGER.trace(getUser() + " Setting Remote URL " + uploadBean);

				getAsmService().setRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(), job.getJobname(), portNumber, uploadBean.getUrl());

				// increase number of executions in parameter sweep case
				if (uploadBean.getFiles().size() > 1) {
					final int numberOxExecutions = uploadBean.getFiles().size();
					LOGGER.trace(getUser() + " Setting number of executions to " + numberOxExecutions);
					setNumberOfInputFiles(getUser().getUserID(), wkfInstance.getWorkflowName(), job.getJobname(), portNumber, numberOxExecutions);
				}
			}
		}
	}

	/**
	 * Sets the number of expected input files of an input port (parameter
	 * sweep)
	 */
	public void setNumberOfInputFiles(final String userID, final String workflowID, final String jobID, final String portID, final Integer value) {
		final Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowID);
		for (final JobPropertyBean propertyBean : jobs) {
			if (propertyBean.getName().equals(jobID)) {
				final Vector inputs = propertyBean.getInputs();
				for (final Object obj : inputs) {
					final PortDataBean portBean = (PortDataBean) obj;
					if (portBean.getSeq() == Long.parseLong(portID)) {
						portBean.getData().put("max", value.toString());
						break;
					}
				}
				break;
			}
		}// MoSGrid autosave
		 // couldn't find calls to setAutoSave(boolean), commenting out
		 // if (autoSave) {
		 // saveConfigData(userID, workflowID, jobs);
		 // }
	}

	/**
	 * Returns the port sequence given its name.
	 * 
	 * @param userId
	 *            The user owning the workflow.
	 * @param workflowName
	 *            The name of the workflow.
	 * @param jobName
	 *            The name of the job.
	 * @param portName
	 *            The port name.
	 * @return The name of the input port. If the job or the port does not
	 *         exist, an {@link IllegalArgumentException} will be thrown.
	 */
	public long getInputPortNumberByName(final String userId, final String workflowName, final String jobName, final String portName)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// first, find the job
		final Collection<JobPropertyBean> jobProperties = getWorkflowConfig(userId, workflowName);
		JobPropertyBean job = null;
		for (final JobPropertyBean jobProperty : jobProperties) {
			if (jobName.equals(jobProperty.getName())) {
				job = jobProperty;
				break;
			}
		}
		if (job == null) {
			throw new IllegalArgumentException("job with name " + jobName + " does not exist for workflow " + workflowName + " from user " + userId);
		}
		// now find the input in the already found job
		final PortDataBean portProperty = null;
		final Collection<PortDataBean> inputs = job.getInputs();
		PortDataBean port = null;
		for (final PortDataBean portData : inputs) {
			if (portData.getName().equals(portName)) {
				port = portData;
				break;
			}
		}
		if (port == null) {
			throw new IllegalArgumentException("port with name " + portName + " does not exist in job " + jobName + ", in workflow " + workflowName
					+ " from user " + userId);
		}
		return port.getSeq();
	}

	// FIXME: This is a hack using reflection... perhaps later ASM versions will
	// be expose saveConfigData as public?
	private void saveConfigData(final String userID, final String workflowName, final Vector pJobs) {
		try {
			// first, get the method
			final Method saveConfigDataMethod = getAsmService().getClass().getDeclaredMethod("saveConfigData", String.class, String.class, Vector.class);
			// make it accessible
			saveConfigDataMethod.setAccessible(true);
			// and invoke
			saveConfigDataMethod.invoke(getAsmService(), userID, workflowName, pJobs);
		} catch (final Exception e) {
			throw new RuntimeException("could not invoke ASMService.saveConfigData(String, String, Vector)", e);
		}
	}

	// FIXME: This is a hack using reflection... perhaps later ASM versions will
	// be expose getRuntimeID as public?
	public String getRuntimeID(final String userID, final String workflowName) {
		try {
			// first, get the method
			final Method getRuntimeIDMethod = getAsmService().getClass().getDeclaredMethod("getRuntimeID", String.class, String.class);
			// make it accessible
			getRuntimeIDMethod.setAccessible(true);
			// and invoke
			return (String) getRuntimeIDMethod.invoke(getAsmService(), userID, workflowName);
		} catch (final Exception e) {
			throw new RuntimeException("could not invoke ASMService.getRuntimeID(String, String)", e);
		}
	}

	// FIXME: This is a hack using reflection... perhaps ASM will at some point
	// expose getWorkflowConfig method?
	// we kind of assume that we expect a Vector<JobPropertyBean> as a return
	// type
	@SuppressWarnings("unchecked")
	private Vector<JobPropertyBean> getWorkflowConfig(final String userId, final String workflowId) {
		try {
			// first, get the method
			final Method getRuntimeIDMethod = getAsmService().getClass().getDeclaredMethod("getWorkflowConfig", String.class, String.class);
			// make it accessible
			getRuntimeIDMethod.setAccessible(true);
			// and invoke
			return (Vector<JobPropertyBean>) getRuntimeIDMethod.invoke(getAsmService(), userId, workflowId);
		} catch (final Exception e) {
			throw new RuntimeException("could not invoke ASMService.getWorkflowConfig(String, String)", e);
		}
	}

	/**
	 * Helper method which cleans up all remote settings. If a remote path
	 * points to a xfs url but does not match for the current user it is
	 * deleted. Not deleting it if url fits for current user is good for testing
	 * purposes.
	 */
	private void cleanRemoteURLs(final ASMWorkflow wkfInstance) {
		for (final ASMJob job : wkfInstance.getJobs().values()) {
			for (final String portNumber : job.getInput_ports().keySet()) {

				final String currentRemoteURL = getAsmService().getRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(), job.getJobname(),
						portNumber);
				if (currentRemoteURL != null && currentRemoteURL.startsWith(XfsBridge.XFS_URL_PREFIX)) {
					// remote url is set and points to xfs
					if (!isUrlWhitelisted(currentRemoteURL)) {
						// remote url does not match for current user, thus
						// delete
						LOGGER.trace(getUser() + " Deleting XFS-Remote-URL because it does not match for current user \n\tJOB: " + job.getJobname()
								+ "\n\tPORT: " + portNumber + "\n\tURL: " + currentRemoteURL);
						// TODO: set to "", null or delete port??
						getAsmService().setRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(), job.getJobname(), portNumber, "");
					}
				}
			}
			for (final String portName : job.getOutput_ports().keySet()) {

				final String currentRemoteURL = getAsmService().getRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(), job.getJobname(),
						portName);
				if (currentRemoteURL != null && currentRemoteURL.startsWith(XfsBridge.XFS_URL_PREFIX)) {
					// remote url is set and points to xfs
					if (!isHomeFolder(currentRemoteURL)) {
						// remote url does not match for current user, thus
						// delete
						LOGGER.trace(getUser() + " Deleting XFS-Remote-URL because it does not match for current user \n\tJOB: " + job.getJobname()
								+ "\n\tPORT: " + portName + "\n\tURL: " + currentRemoteURL);
						// TODO: set to "", null or delete port??
						getAsmService().setRemoteInputPath(getUser().getUserID(), wkfInstance.getWorkflowName(), job.getJobname(), portName, "");
					}
				}
			}
		}
	}

	// returns true if the given url is white listed, that is, it represents the
	// home folder or
	// the url is one of the whitelisted xtreemfs folders
	private boolean isUrlWhitelisted(final String url) {
		if (isHomeFolder(url)) {
			return true;
		}
		// if not in the home folder, see if the folder is whitelisted
		final String[] whitelistedFolders = MSMLProperties.WHITE_LIST_FOLDERS.getProperty().split(",");
		for (final String whitelistedFolder : whitelistedFolders) {
			if (url.startsWith(XfsBridge.XFS_URL_PREFIX + whitelistedFolder.trim())) {
				return true;
			}
		}
		// at this point, the given url is neither the home folder nor
		// whitelisted
		return false;
	}

	private boolean isHomeFolder(final String url) {
		final String xfsHomeUrl = XfsBridge.createURL(getXfsBridge().getHomeDir());
		return url.startsWith(xfsHomeUrl);
	}

	/**
	 * Helper method for creating the actual msml file from msml template. At
	 * first the template is 'marshalled' to a temporary file. Next, the
	 * template is searched for the ParserParameter element below the JobList
	 * element in order to retrieve the Id of the first parser job and port.
	 * This is needed to decide to which job the msml file shall be uploaded to.
	 * At last, the msml file gets added to the UploadCollector.
	 * 
	 * @throws SubmissionFailedException
	 */
	private void createMSML(final ImportedWorkflow wkfImport, final UploadCollector collector) throws SubmissionFailedException {
		LOGGER.debug(getUser() + " Creating MSML file from template");

		final String msmlFileName = WorkflowHelper.getInstance().getUserChosenName(wkfImport.getAsmInstance());
		File msmlFile = null;

		try {
			msmlFile = TempFileHelper.createTempFile(msmlFileName, ".xml");
			LOGGER.trace(getUser() + " Writing template to temporary file " + msmlFile);
			// marshall template to temp file
			wkfImport.getTemplate().marshallTo(msmlFile);
		} catch (final IOException e) {
			final String msg = " Unable to create temporary msml file from template";
			LOGGER.error(getUser() + msg, e);
			throw new SubmissionFailedException(msg, e);
		}

		if (msmlFile != null) {
			LOGGER.trace(getUser() + " Parsing job-Id and port of first parser job which shall get the msml file");

			final JobListParserConfig parserConfig = wkfImport.getTemplate().getJobListElement().getParserConfig();
			if (parserConfig == null) {
				LOGGER.warn(getUser() + " No ParserConfig element given below JobList element! MSML will not be passed into workflow!" + " Template file: "
						+ wkfImport.getTemplate().getPath());
				try {
					// delete temporary template file
					msmlFile.delete();
				} catch (final Exception e) {
					LOGGER.info(getUser() + " Unable to delete temporary MSML file.");
				}

			} else {
				final String parserJobId = parserConfig.getJobName();
				final String parserJobPort = parserConfig.getPortName();
				if (parserJobId != null && parserJobPort != null) {
					// Add msml file to upload collector
					collector.addUpload(msmlFile, parserJobPort, parserJobId);
				} else {
					LOGGER.warn(getUser() + " ParserConfig element does not contain desired parameters. MSML will not be passed into workflow!");
					try {
						// delete temporary template file
						msmlFile.delete();
					} catch (final Exception e) {
						LOGGER.info(getUser() + " Unable to delete temporary MSML file.");
					}
				}
			}
		}
	}

	private AbstractAdapterFactoryBase getAdapterFactory() {
		if (adapterFactory != null) {
			return adapterFactory;
		}
		adapterFactory = createAdapterFactoryInstance();
		try {
			adapterFactory.init();
		} catch (final InitializationException e) {
			LOGGER.error(getUser() + " Adapterfactory should be instanciated only once.", e);
		}
		return adapterFactory;
	}

	/**
	 * Helper method which uploads all collected files to xfs. Creates a new
	 * directory with the user given import name below the xfs upload dir and
	 * places all files there.
	 */
	private void uploadFilesToXfs(final ASMWorkflow wkfInstance, final UploadCollector collector) throws InputSupplyException {
		LOGGER.debug(getUser() + " Uploading files to XFS for " + wkfInstance.getWorkflowName());

		for (final UploadBean uploadBean : collector.getAllUploads()) {
			final String jobname = uploadBean.getJobname();
			final ASMJob job = wkfInstance.getJobs().get(jobname);
			if (job != null) {
				// check if this is a real upload. UploadBeans may also just be
				// links to xfs
				if (uploadBean.hasFiles()) {
					for (final File file : uploadBean.getFiles()) {
						final String importName = WorkflowHelper.getInstance().getUserChosenName(wkfInstance);
						final String xfsPath = getXfsBridge().getUploadDir() + "/" + importName + "/" + jobname + "/";
						final String newFilename = TempFileHelper.removeTempSuffix(file);
						String xfsUrl = XfsBridge.createURL(xfsPath + newFilename);

						LOGGER.trace(getUser() + " Uploading:\n\tFILE: " + file + "\n\tURL: " + xfsUrl);
						BufferedInputStream in = null;
						BufferedOutputStream out = null;
						try {
							in = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
							out = new BufferedOutputStream(getXfsBridge().getUploadStream(newFilename, xfsPath));

							final byte[] buf = new byte[4096];
							int len;
							while ((len = in.read(buf)) > 0) {
								out.write(buf, 0, len);
							}

						} catch (final FileNotFoundException e1) {
							LOGGER.error(getUser() + " FileNotFoundException while uploading file to XtreemFS: ", e1);
							throw new InputSupplyException("Could not access " + file.getAbsolutePath());
						} catch (final IOException e1) {
							LOGGER.error(getUser() + " IOException while uploading file to XtreemFS: ", e1);
							throw new InputSupplyException("Could not write " + file.getAbsolutePath() + " to " + xfsUrl);
						} finally {
							try {
								if (in != null) {
									in.close();
								}
								if (out != null) {
									out.close();
								}
							} catch (final IOException e1) {
								LOGGER.error(getUser() + " Could not close stream", e1);
							}
						}

						// set real xfs url of upload which must not contain an
						// index in case of parameter sweep
						// workflows
						if (uploadBean.getUrl() == null) {
							if (uploadBean.getFiles().size() > 1) {
								final String filenameWithoutIndex = TempFileHelper.removeTempSuffixAndIndex(file);
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
	// private void setEnvironmentParameters(ImportedWorkflow wkfImport) {
	// for (Job jobElement :
	// wkfImport.getTemplate().getJobListElement().getJobs()) {
	// if (jobElement.getEnvironment() != null) {
	// ASMJob asmJob =
	// wkfImport.getAsmInstance().getJobs().get(jobElement.getId());
	// if (asmJob == null) {
	// // actually this should never happen
	// LOGGER.warn(getUser() + " Template defines job " + jobElement.getId()
	// + " which can not be resolved in workflow.");
	// continue;
	// }
	// LOGGER.debug(getUser() + " Setting environment parameters for job " +
	// jobElement.getId());
	//
	// Integer numberOfNodes = jobElement.getEnvironment().getNumberOfNodes();
	// if (numberOfNodes != null) {
	// LOGGER.trace(getUser() + " Setting number of nodes to " + numberOfNodes);
	// getAsmService().setNodeNumber(getUser().getUserID(),
	// wkfImport.getAsmInstance().getWorkflowName(),
	// asmJob.getJobname(), numberOfNodes);
	// }
	//
	// Integer numberOfCores = jobElement.getEnvironment().getNumberOfCores();
	// if (numberOfCores != null) {
	// LOGGER.trace(getUser() + " Setting number of cores to " + numberOfCores);
	// setCoreNumber(getUser().getUserID(),
	// wkfImport.getAsmInstance().getWorkflowName(),
	// asmJob.getJobname(), numberOfCores.toString());
	// }
	//
	// Integer memory = jobElement.getEnvironment().getMemoryValue();
	// if (memory != null) {
	// LOGGER.trace(getUser() + " Setting memory to " + memory + " MB");
	// setMemory(getUser().getUserID(),
	// wkfImport.getAsmInstance().getWorkflowName(),
	// asmJob.getJobname(), memory.toString());
	// }
	//
	// Integer walltime = jobElement.getEnvironment().getWalltimeValue();
	// if (walltime != null) {
	// LOGGER.trace(getUser() + " Setting walltime to " + walltime);
	// setWalltime(getUser().getUserID(),
	// wkfImport.getAsmInstance().getWorkflowName(),
	// asmJob.getJobname(), walltime.toString());
	//
	// }
	// // LOGGER.debug(asmJob.dumpProperties());
	// }
	// }
	// }

	// sets environment parameter for the passed workflow, but does it in a
	// batch mode (through the use of methods
	// via reflection, it's extremely slow, so it makes sense to invoke the
	// method via reflection only once!)
	// this supress warnings is because jobPropertyBean.getDesc() uses a
	// non-parametrized HashMap... really
	@SuppressWarnings("unchecked")
	private void setEnvironmentParametersBatch(final ImportedWorkflow importedWorkflow) {
		final ArrayList<String> jobNames = new ArrayList<String>();
		final ArrayList<String> propertyNames = new ArrayList<String>();
		final ArrayList<String> propertyValues = new ArrayList<String>();
		final String workflowName = importedWorkflow.getAsmInstance().getWorkflowName();
		for (final Job jobElement : importedWorkflow.getTemplate().getJobListElement().getJobs()) {
			if (jobElement.getEnvironment() != null) {
				final ASMJob asmJob = importedWorkflow.getAsmInstance().getJobs().get(jobElement.getId());
				if (asmJob == null) {
					// actually this should never happen
					LOGGER.warn(getUser() + " Template defines job " + jobElement.getId() + " which can not be resolved in workflow.");
					continue;
				}
				LOGGER.debug(getUser() + " Setting environment parameters for job " + jobElement.getId());
				// collect the job names, property names and values to set them
				// in batch mode, except for
				// setNodeNumber

				final Integer numberOfNodes = jobElement.getEnvironment().getNumberOfNodes();
				if (numberOfNodes != null) {
					LOGGER.trace(getUser() + " Setting number of nodes to " + numberOfNodes);
					getAsmService().setNodeNumber(getUser().getUserID(), importedWorkflow.getAsmInstance().getWorkflowName(), asmJob.getJobname(),
							numberOfNodes);
				}

				final Integer numberOfCores = jobElement.getEnvironment().getNumberOfCores();
				if (numberOfCores != null) {
					LOGGER.trace(getUser() + " Will set number of cores to " + numberOfCores);
					jobNames.add(asmJob.getJobname());
					propertyNames.add("unicore.keyCores");
					propertyValues.add(numberOfCores.toString());
				}

				final Integer memory = jobElement.getEnvironment().getMemoryValue();
				if (memory != null) {
					LOGGER.trace(getUser() + " Will set memory to " + memory + " MB");
					jobNames.add(asmJob.getJobname());
					propertyNames.add("unicore.keyMemory");
					propertyValues.add(memory.toString());
				}

				final Integer walltime = jobElement.getEnvironment().getWalltimeValue();
				if (walltime != null) {
					LOGGER.trace(getUser() + " Will set walltime to " + walltime);
					jobNames.add(asmJob.getJobname());
					propertyNames.add("unicore.keyWalltime");
					propertyValues.add(walltime.toString());
				}

				// don't forget to set the wf name!
				LOGGER.trace(getUser() + " Will set workflow name to  to " + workflowName);
				jobNames.add(asmJob.getJobname());
				propertyNames.add("unicore.keyWorkflowName");
				propertyValues.add(workflowName);
			}
		}
		// set the properties
		if (!jobNames.isEmpty()) {
			final String userId = getUser().getUserID();
			// get the workflow configuration information
			final Vector<JobPropertyBean> jobs = getWorkflowConfig(userId, workflowName);
			// put the jobs in a map, because come on, we're not in first grade
			final Map<String, JobPropertyBean> jobMap = new HashMap<String, JobPropertyBean>();
			for (final JobPropertyBean jobPropertyBean : jobs) {
				jobMap.put(jobPropertyBean.getName(), jobPropertyBean);
			}
			for (int i = 0; i < jobNames.size(); i++) {
				final String jobName = jobNames.get(i);

				final JobPropertyBean jobPropertyBean = jobMap.get(jobName);
				if (jobPropertyBean != null) {
					final String propertyName = propertyNames.get(i);
					final String propertyValue = propertyValues.get(i);
					LOGGER.debug("Job [" + jobName + "] set job property [" + propertyName + "], value [" + propertyValue + ']');
					jobPropertyBean.getDesc().put(propertyName, propertyValue);
				} else {
					LOGGER.error("Could not find job [" + jobName + "]. Properties will not be set.");
				}
			}
		}
	}

	/**
	 * Set job_desc
	 * 
	 * 
	 * 
	 * @param prop
	 *            - property name to set ("nodenumber" etc.)
	 * @param value
	 *            - property value
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	// private void setJobDesc(String prop, String value, String userID, String
	// workflowID, String jobID) {
	// Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowID);
	//
	// for (JobPropertyBean j : jobs) {
	// if (j.getName().equals(jobID)) {
	// j.getDesc().put(prop, value);
	// System.out.println("job : " + j.getName() + " set job property:" + prop +
	// " value: " + value);
	// break;
	// }
	// }
	// // MoSGrid autosave
	// // NOTE: Found no calls to setAutoSave(boolean)! -delagarza
	// //if (autoSave) {
	// // saveConfigData(userID, workflowID, jobs);
	// //}
	// }

	/**
	 * Set walltime
	 * 
	 * @param value
	 *            - walltime in ?
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	// private void setWalltime(String userID, String workflowID, String jobID,
	// String value) {
	// setJobDesc("unicore.keyWalltime", value, userID, workflowID, jobID);
	// }

	/**
	 * Set number of cores
	 * 
	 * @param value
	 *            - number of cores
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	// private void setCoreNumber(String userID, String workflowID, String
	// jobID, String value) {
	// setJobDesc("unicore.keyCores", value, userID, workflowID, jobID);
	// }

	/**
	 * Set memory
	 * 
	 * @param value
	 *            - memory in ?
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	// private void setMemory(String userID, String workflowID, String jobID,
	// String value) {
	// setJobDesc("unicore.keyMemory", value, userID, workflowID, jobID);
	// }

	/**
	 * Set workflow name
	 * 
	 * @param value
	 *            - name of the workflow
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	// private void setWorkflowName(String userID, String workflowID, String
	// jobID, String value) {
	// setJobDesc("unicore.keyWorkflowName", value, userID, workflowID, jobID);
	// }

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
	 * @return Return a new instance of ImportedWorkflow with the current
	 *         domain.
	 */
	@Override
	protected ImportedWorkflow retrieveDomainDependandWorkflow(final ASMWorkflow workflowInstance, final MSMLTemplate template) {
		return new ImportedWorkflow(getDomainId(), workflowInstance, template);
	}

	@Override
	protected void afterImport(final ImportedWorkflow newImport) throws ImportFailedException {
		// create input mask for newly imported workflow instance
		try {
			LOGGER.trace(getUser() + " Creating Inputmask");
			final AbstractInputMask inputMask = getInputMaskFactory().createInputMask(newImport);
			newImport.setInputMask(inputMask);
		} catch (final Exception e) {
			final String message = getUser() + " Creating inputmask failed for workflow: " + newImport.getUserImportName() + "\n";
			LOGGER.error(message, e);
			throw new ImportFailedException(message, e);
		}
	}
}
