package de.mosgrid.portlet;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.WorkflowInstanceStatusBean;
import hu.sztaki.lpds.pgportal.services.asm.constants.RepositoryItemTypeConstants;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.foundation.logging.Logging;
import org.xtreemfs.portlet.util.vaadin.VaadinXtreemFSSession;

import com.vaadin.Application;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.exceptions.ImportFailedException;
import de.mosgrid.exceptions.PortletInitializationException;
import de.mosgrid.exceptions.RemovingFailedException;
import de.mosgrid.exceptions.SubmissionFailedException;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.portlet.listener.IImportListener;
import de.mosgrid.portlet.listener.IStatusMessageListener;
import de.mosgrid.portlet.listener.ISubmissionListener;
import de.mosgrid.util.MosgridProperties;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.WorkflowHelper;
import de.mosgrid.util.XfsBridge;

/**
 * General abstract portlet application. Takes care of very basic initializations.
 * 
 * @author Andreas Zink
 * 
 */
public abstract class MoSGridPortlet extends Application implements PortletRequestListener {
	private static final long serialVersionUID = 1159491526862609105L;
	private final Logger LOGGER = LoggerFactory.getLogger(MoSGridPortlet.class);
	
	private boolean isInitialized = false;
	private PortletRequest firstRequest;
	private MosgridUser user;

	// ASM service for handling gUSE workflows
	private volatile ASMService asmService;
	// XFS connection
	private XfsBridge xfsBridge;
	// ExecutorService enables the execution of subtasks without creating new threads all the time (Thread-Pool)
	private ExecutorService executorService;
	private List<String> statusMsgHistory;
	private List<IStatusMessageListener> statusMessageListenerList;

	// stores all import listeners
	private List<IImportListener> importListenerList;
	// stores all submission listeners
	private List<ISubmissionListener> submissionListenerList;
	// all importable workflows
	protected List<ImportableWorkflow> importableWorkflows;
	// all importet workflows
	protected List<ImportedWorkflow> importedWorkflows;

	public MoSGridPortlet() {
		setTheme(MosgridProperties.CUSTOM_THEME_NAME.getProperty());
		executorService = Executors.newSingleThreadExecutor();
		statusMsgHistory = new ArrayList<String>();
		statusMessageListenerList = new ArrayList<IStatusMessageListener>();

		this.importedWorkflows = new ArrayList<ImportedWorkflow>();
		this.importListenerList = new ArrayList<IImportListener>(5);
		this.submissionListenerList = new ArrayList<ISubmissionListener>(5);
	}

	/*
	 * This method is the first to be called by the framework. It is usually executed after the constructor and before
	 * the initialization method. NOTICE: This method can be called more than once because it is used for every kind of
	 * user request.
	 */
	@Override
	public void onRequestStart(PortletRequest request, PortletResponse response) {
		// set request and user id
		if (this.firstRequest == null) {
			this.firstRequest = request;
			String userID = request.getRemoteUser();
			Principal p = request.getUserPrincipal();
			user = new MosgridUser(userID, p);
			setUser(user);
			LOGGER.info(getUser() + " Portlet request started with session: " + request.getRequestedSessionId());
		}
	}

	/*
	 * Gets called at the end of every request.
	 */
	@Override
	public void onRequestEnd(PortletRequest request, PortletResponse response) {
		// By now not needed
	}

	/*
	 * The init method is called by the framework and should normally be executed after the first request.
	 */
	@Override
	public void init() {
		if (firstRequest == null) {
			// If init is called before request start, the initialization shall fail!
			String message = "Initialization error! Trying to initialize before request start!";
			LOGGER.error(message);
			showFailedInitWindow(message);
		} else if (getUser().getUserID() == null) {
			// If initalization of user failed somehow
			String message = "Initialization error! The user-ID could not be resolved!";
			LOGGER.error(message);
			showFailedInitWindow(message);
		} else {
			try {
				LOGGER.debug(getUser() + " Initializing portlet instance.");
				// before initialization hook
				beforeApplicationInit();

				initXfsBridge();
				initASM();

				beforeUiInit();
				// create gui
				createUI();
				// after initialization hook
				afterApplicationInit();

				isInitialized = true;
			} catch (PortletInitializationException e) {
				showFailedInitWindow(e.getMessage() + "\n\nError-Info: " + getUser().getUserID() + "_" + new Date().getTime());
			} catch (Exception e) {
				LOGGER.error("Unexpected initialization error!", e);
				showFailedInitWindow(e.getMessage() + "\n\nError-Info: " + getUser().getUserID() + "_" + new Date().getTime());
			}
		}
	}

	/**
	 * Creates an empty window and shows an error notification
	 */
	private void showFailedInitWindow(String message) {
		// set a default window and show a error message
		Window w = new Window();
		Label label = new Label("Initialization Error!<br/>" + message.replace("\n", "<br/>"), Label.CONTENT_XHTML);
		w.addComponent(label);
		setMainWindow(w);
		Notification notif = NotificationFactory.createErrorNotification("Initialization Error", message.replace("\n", "<br/>"));
		w.showNotification(notif);
	}

	/**
	 * Hook in Application's initialization method. Gets called at the beginning of the init method.
	 */
	protected abstract void beforeApplicationInit() throws PortletInitializationException;

	/**
	 * Hook in Application's initialization method. Gets called before UI is created.
	 */
	protected abstract void beforeUiInit() throws PortletInitializationException;

	/**
	 * Hook in Application's initialization method. Gets called at the end of the init method.
	 */
	protected abstract void afterApplicationInit() throws PortletInitializationException;

	/**
	 * Hook in Portlet's initialization. This method shall create and render the UI, i.e. create and set a main window.
	 */
	protected abstract void createUI();

	/**
	 * Initialization of the ASM-API
	 * 
	 * @throws PortletInitializationException
	 */
	private void initASM() throws PortletInitializationException {
		try {
			LOGGER.debug(getUser() + " Initializing ASM");

			this.asmService = ASMService.getInstance();
			// ASMService.init has been deprecated
			//this.asmService.init();
		} catch (Exception e) {
			String msg = " Error while initializing ASM service";
			LOGGER.error(getUser() + msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * Helper method for xfs initialization
	 * 
	 * @throws PortletInitializationException
	 */
	private void initXfsBridge() throws PortletInitializationException {
		try {
			// start xfs looging
			LOGGER.debug(getUser() + " Initializing XFS bridge.");
			Logging.start(Logging.LEVEL_INFO);
			VaadinXtreemFSSession.initialize(this);
			this.xfsBridge = new XfsBridge(firstRequest);
		} catch (PortletInitializationException e) {
			throw e;
		} catch (Exception e) {
			String msg = " Failed to initialize XFS connection";
			LOGGER.error(getUser() + msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * @return XTreemFS-Bridge
	 */
	public XfsBridge getXfsBridge() {
		if (xfsBridge == null) {
			String message = "Trying to get XfsBridge before initialization!";
			LOGGER.error(message);
			throw new IllegalStateException(message);
		}
		return this.xfsBridge;

	}

	/**
	 * @return ASM-Service
	 */
	public ASMService getAsmService() {
		if (asmService == null) {
			String message = "Trying to get ASM-Service before initialization!";
			LOGGER.error(message);
			throw new IllegalStateException(message);
		}
		return asmService;
	}

	@Override
	public MosgridUser getUser() {
		if (user == null) {
			String message = "Trying to get User before initialization!";
			LOGGER.error(message);
			throw new IllegalStateException(message);
		}
		return user;
	}

	/**
	 * Imports a workflow. Create a new thread for this task in order to prevent UI from 'freezing'
	 * 
	 * @param importable
	 *            The user selected importable instance
	 * @param importName
	 *            The given import name
	 * 
	 * @param progressIndicator
	 *            The ProgressIndicator to be updated or null if not used
	 */
	public void importWorkflow(final ImportableWorkflow importable, final String importName,
			final ProgressIndicator progressIndicator) {
		Runnable importTask = new Runnable() {

			@Override
			public void run() {
				// first create a copy of template in order to import template more than once
				MSMLTemplate template = importable.getTemplate().copy();
				ASMWorkflow workflowInstance = null;
				ImportedWorkflow newImport = null;
				try {
					LOGGER.debug(getUser() + " Trying to import workflow " + template.getJobListElement().getId());

					ASMRepositoryItemBean wkfBean = importable.getWorkflowBean();

					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.2));
					}

					// try to import workflow by ASM
					final String desiredWorkflowName = WorkflowHelper.getInstance().generateFullWorkflowName(importName, template, getUser().getUserID());
					LOGGER.debug(getUser() + " Importing workflow bean as " + desiredWorkflowName);

					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.25));
					}

					// import ASM instance
					final String givenWorkflowName;
					try {
						givenWorkflowName = getAsmService().ImportWorkflow(getUser().getUserID(), desiredWorkflowName,
								wkfBean.getUserID(), RepositoryItemTypeConstants.Workflow, wkfBean.getId().toString());

						// getAsmService().test(getUser().getUserID(), internalJobName);
						if (givenWorkflowName == null) {
							throw new NullPointerException("ASM service returned empty import name.");
						}
					} catch (Exception e) {
						String message = getUser() + " Could not import " + wkfBean.getItemID() + "\n";
						LOGGER.error(message, e);
						throw new ImportFailedException(message, e);
					}
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.5));
					}

					LOGGER.trace(getUser() + " Searching for newly created ASM workflow instance " + givenWorkflowName);
					// resolve imported workflow instance
					try {
						workflowInstance = getAsmService().getASMWorkflow(getUser().getUserID(), givenWorkflowName);
						if (workflowInstance == null) {
							throw new NullPointerException("ASM service returned empty instance.");
						}
					} catch (Exception e) {
						String message = getUser() + " Could not find instance: " + givenWorkflowName + "\n";
						LOGGER.error(message, e);
						throw new ImportFailedException(message, e);
					}

					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.75));
					}

					newImport = retrieveDomainDependandWorkflow(workflowInstance, template);
					LOGGER.trace(getUser() + " Found workflow instance: " + dumpASMInstance(workflowInstance));

					afterImport(newImport);

					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(0.9));
					}

					// add to list of imports
					importedWorkflows.add(newImport);
					LOGGER.debug("Successfully imported " + wkfBean.getItemID() + " as "
							+ WorkflowHelper.getInstance().getUserChosenName(workflowInstance));
					// update progress
					if (progressIndicator != null) {
						progressIndicator.setValue(new Float(1.0));
					}

					// inform listeners
					fireImportSucceeded(newImport);

				} catch (ImportFailedException e) {
					// delete new asm instance if error occurred
					if (workflowInstance != null) {
						removeASMInstance(workflowInstance);
					}
					// delete import instance
					if (newImport != null) {
						importedWorkflows.remove(newImport);
					}
					// inform listeners
					fireImportFailed(template, importName, e);
				} catch (Exception e) {
					// delete new asm instance if error occurred
					if (workflowInstance != null) {
						removeASMInstance(workflowInstance);
					}
					// delete import instance
					if (newImport != null) {
						importedWorkflows.remove(newImport);
					}
					// inform listeners
					fireImportFailed(template, importName, new ImportFailedException(e.getMessage(), e));
				}
			}
		};
		getExecutorService().execute(importTask);
	}

	protected abstract ImportedWorkflow retrieveDomainDependandWorkflow(ASMWorkflow workflowInstance, MSMLTemplate template);

	protected abstract void afterImport(ImportedWorkflow newImport) throws ImportFailedException;

	/**
	 * Gets all available workflows which can be retrieved for the current user by ASM which are in the given status
	 * (inclusive=true) or all, exclusive these in given status (incluse=false)
	 * 
	 * @param status
	 *            Use constants in StatusConstants
	 */
	public Collection<ASMWorkflow> getAllWorkflows(String status, boolean inclusive) {
	    	final StatusConstants statusConstants = new StatusConstants(); 
		String statusLiteral = statusConstants.getStatus(status);		
		if (inclusive) {
			LOGGER.trace(getUser() + " Retrieving all ASMWorkflows which are in status: " + status + " - " + statusLiteral);
		} else {
			LOGGER.trace(getUser() + " Retrieving all ASMWorkflows which are not in status: " + status + " - " + statusLiteral);
		}
		Collection<ASMWorkflow> workflowList = new ArrayList<ASMWorkflow>();

		// get all workflows first
		Collection<ASMWorkflow> allWorkflows = getAllWorkflows();

		if (allWorkflows != null) {
			// catch wkfs with desired status
			for (ASMWorkflow wkf : allWorkflows) {
				WorkflowInstanceStatusBean statusBean = wkf.getStatusbean();
				if (statusBean != null) {
					if (inclusive) {
						// catch wkfs with ARE in given status
						if (statusBean.getStatus().equals(statusLiteral)) {
							workflowList.add(wkf);
						}
					} else {
						// catch wkfs with ARE NOT in given status
						if (!statusBean.getStatus().equals(statusLiteral)) {
							workflowList.add(wkf);
						}
					}
				} else {
					// if status bean is null this workflow should be deleted! This is because ASM stinks...
					removeASMInstance(wkf);
				}
			}
		}
		return Collections.unmodifiableCollection(workflowList);
	}

	/**
	 * Gets all available workflows which can be retrieved for the current user by ASM
	 */
	public Collection<ASMWorkflow> getAllWorkflows() {
		LOGGER.trace(getUser() + " Retrieving all ASMWorkflows");

		ArrayList<ASMWorkflow> workflowList = new ArrayList<ASMWorkflow>();
		try {
			workflowList.addAll(getAsmService().getASMWorkflows(getUser().getUserID()));
		} catch (Exception e) {
			LOGGER.error(getUser() + " Could not retrieve workflow instances!", e);
		}

		return Collections.unmodifiableCollection(workflowList);
	}

	/**
	 * Helper method for deleting wkfs in INIT state from last session
	 * 
	 * @throws PortletInitializationException
	 */
	protected void removeNotSubmittedWkfs() throws PortletInitializationException {
		try {
			LOGGER.debug(getUser() + " Removing not submitted workflows from last session...");

			Collection<ASMWorkflow> wkfs = getAllWorkflows(StatusConstants.INIT, true);
			for (ASMWorkflow instance : wkfs) {
				LOGGER.trace("Removing: " + instance.getWorkflowName());
				getAsmService().DeleteWorkflow(getUser().getUserID(), instance.getWorkflowName());
			}
		} catch (Exception e) {
			String msg = " Error while removing imported workflows from last user session";
			LOGGER.error(getUser() + msg, e);
			throw new PortletInitializationException(msg, e);
		}
	}

	/**
	 * Helper method for deleting an asm workflow instance
	 */
	public void removeASMInstance(ASMWorkflow wkfInstance) {
		if (wkfInstance != null) {
			String instanceName = wkfInstance.getWorkflowName();
			LOGGER.trace(getUser() + " Removing imported ASM instance " + instanceName);
			getAsmService().DeleteWorkflow(getUser().getUserID(), instanceName);
		} else {
			LOGGER.info(getUser() + " Trying to delete not existing ASM workflow instance");
		}
	}

	/**
	 * @return 'true' after the complete Application initialization has finished
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * @return Shall return a welcome message for this portlet. Any HTML tags are allowed to be used.
	 */
	public abstract String getWelcomeText();

	/**
	 * @return Additional information on the portlet
	 */
	public abstract AboutInfo getAboutInfo();

	/**
	 * Sets a status message which describes the current status of the portlet. Shall be used to inform the user about
	 * important events. Additionally, an icon may be set to underline the message.
	 * 
	 * @param msg
	 *            The new status message
	 * @param icon
	 *            An appropriate icon for the message or 'null'
	 */
	public void setStatusMessage(String msg, ThemeResource icon) {
		this.statusMsgHistory.add(msg);
		fireStatusMsgChange(msg, icon);
	}

	/**
	 * Sets a status message which describes the current status of the portlet. Shall be used to inform the user about
	 * important events. Additionally, an icon may be set to underline the message.
	 * 
	 * @param msg
	 *            The new status message
	 * @param status
	 *            One of the built-in StatusConstants which provide a proper icon.
	 */
	public void setStatusMessage(String msg, PortletStatus status) {
		this.setStatusMessage(msg, status.getIcon());
	}

	/**
	 * @return The history of status messages
	 */
	public List<String> getStatusMsgHistory() {
		return Collections.unmodifiableList(statusMsgHistory);
	}

	/**
	 * @return The ExecutorService which can be used to start concurrent tasks. The ExecutorService takes care of the
	 *         lifecycle of all child threads and thus prevents the overhead of creating new threads for every subtask.
	 */
	public ExecutorService getExecutorService() {
		return executorService;
	}

	@Override
	public void close() {
		VaadinXtreemFSSession.destroy();
		executorService.shutdown();
	}

	/**
	 * Adds a listener
	 */
	public void addStatusMessageListener(IStatusMessageListener l) {
		if (l != null) {
			statusMessageListenerList.add(l);
		}
	}

	/**
	 * Removes a listener
	 */
	public void removeStatusMessageListener(IStatusMessageListener l) {
		if (l != null && statusMessageListenerList.contains(l)) {
			statusMessageListenerList.remove(l);
		}
	}

	/**
	 * Notifies listeners of status msg changes
	 */
	private void fireStatusMsgChange(String msg, ThemeResource icon) {
		for (IStatusMessageListener l : new ArrayList<IStatusMessageListener>(statusMessageListenerList)) {
		    try {
			l.statusMessageChanged(msg, icon);
		    } catch (Exception e) {
			LOGGER.error("Could not notify about status message changing. StatusMessageListener [" + l + ']', e);
		    }
		}
	}

	/**
	 * @return All importable workflows
	 */
	public Collection<ImportableWorkflow> getImportableWorkflows() {
		return Collections.unmodifiableCollection(importableWorkflows);
	}

	/**
	 * @return All imported workflows
	 */
	public Collection<ImportedWorkflow> getImportedWorkflowInstances() {
		return Collections.unmodifiableCollection(importedWorkflows);
	}

	/**
	 * @return All imported workflows
	 */
	public ImportedWorkflow getLastImportedWorkflowInstance() {
		return importedWorkflows.get(importedWorkflows.size() - 1);
	}

	/**
	 * Adds ImportListener
	 */
	public void addImportListener(IImportListener l) {
		if (l != null) {
			importListenerList.add(l);
		}
	}

	/**
	 * Removes a ImportListener
	 */
	public void removeImportListener(IImportListener l) {
		if (l != null && importListenerList.contains(l)) {
			importListenerList.remove(l);
		}
	}

	/**
	 * Adds SubmssionListener
	 */
	public void addSubmissionListener(ISubmissionListener l) {
		if (l != null) {
			submissionListenerList.add(l);
		}
	}

	/**
	 * Removes a SubmissionListener
	 */
	public void removeSubmissionListener(ISubmissionListener l) {
		if (l != null && submissionListenerList.contains(l)) {
			submissionListenerList.remove(l);
		}
	}

	/**
	 * Notifies ImportListeners if wkf was successfully imported
	 */
	protected void fireImportSucceeded(ImportedWorkflow wkfImport) {
		for (IImportListener l : new ArrayList<IImportListener>(importListenerList)) {
		    try {
			l.importSucceeded(wkfImport);
		    } catch (Exception e) {
			LOGGER.error("Could not notify about import succeeding. ImportListener [" + l + ']', e);
		    }
		}
	}

	/**
	 * Notifies ImportListeners if wkf was successfully imported
	 */
	protected void fireImportFailed(MSMLTemplate failedImport, String userImportName, ImportFailedException e) {
		for (IImportListener l : new ArrayList<IImportListener>(importListenerList)) {
		    try {
			l.importFailed(failedImport, userImportName, e);
		    } catch (Exception ex) {
			LOGGER.error("Could not notify about import failing. ImportListener [" + l + ']', ex);
		    }
		}
	}

	/**
	 * Notifies SubmissionListener if wkf was successfully submitted
	 */
	protected void fireSubmissionSucceeded(ImportedWorkflow wkfImport) {
		for (ISubmissionListener l : new ArrayList<ISubmissionListener>(submissionListenerList)) {
		    try {
			l.submissionSucceeded(wkfImport);
		    } catch (Exception e) {
			LOGGER.error("Could not notify about submission succeeding. SubmissionListener [" + l + ']', e);
		    }
		}
	}

	/**
	 * Notifies SubmissionListener if wkf submission failed
	 */
	protected void fireSubmissionFailed(ImportedWorkflow failedImport, SubmissionFailedException e) {
	    	for (ISubmissionListener l : new ArrayList<ISubmissionListener>(submissionListenerList)) {
		    try {
			l.submissionFailed(failedImport, e);
		    } catch (Exception ex) {
			LOGGER.error("Could not notify about submission failing. SubmissionListener [" + l + ']', ex);
		    }
		}
	}

	/**
	 * Notifies SubmissionListener if wkf was successfully removed
	 */
	protected void fireRemovalSucceeded(ImportedWorkflow wkfImport) {
		for (ISubmissionListener l : new ArrayList<ISubmissionListener>(submissionListenerList)) {
		    try {
			l.removalSucceeded(wkfImport);
		    } catch (Exception e) {
			LOGGER.error("Could not notify about removal succeeding. SubmissionListener [" + l + ']', e);
		    }
		}
	}

	/**
	 * Notifies SubmissionListener if wkf removal failed
	 */
	protected void fireRemovalFailed(ImportedWorkflow failedImport, RemovingFailedException e) {
		for (ISubmissionListener l : new ArrayList<ISubmissionListener>(submissionListenerList)) {
		    try {
			l.removalFailed(failedImport, e);
		    } catch (Exception ex) {
			LOGGER.error("Could not notify about removal failing. SubmissionListener [" + l + ']', ex);
		    }
		}
	}

	/**
	 * Helper method which creates a representative String for a workflow instance.
	 */
	private String dumpASMInstance(ASMWorkflow wkfInstance) {
		if (!LOGGER.isDebugEnabled()) {
			return wkfInstance.getWorkflowName();
		}
		StringBuilder dumpBuilder = new StringBuilder();
		dumpBuilder.append("\n\tWkfInstanceID: " + wkfInstance.getWorkflow_instanceId());
		dumpBuilder.append("\n\tWorkflowID: " + wkfInstance.getWorkflowID());
		dumpBuilder.append("\n\tWorkflowName: " + wkfInstance.getWorkflowName());
		dumpBuilder.append("\n\tStatus (old): " + wkfInstance.getStatusbean().getStatus());
		dumpBuilder.append("\n\tJobcount: " + wkfInstance.getJobs().size());

		return dumpBuilder.toString();
	}
}
