package de.mosgrid.ukoeln.templatedesigner.document;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.exceptions.ImportFailedException;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.portlet.ImportableWorkflow;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.portlet.MosgridUser;
import de.mosgrid.portlet.listener.IImportListener;
import de.mosgrid.ukoeln.templatedesigner.TemplateDesignerApplication;
import de.mosgrid.ukoeln.templatedesigner.asm.MGASMWorkflow;
import de.mosgrid.ukoeln.templatedesigner.document.TDMainDocument.TemplateBean;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainTab;
import de.mosgrid.ukoeln.templatedesigner.helper.EventArgs;
import de.mosgrid.ukoeln.templatedesigner.helper.EventArgsNonGeneric;
import de.mosgrid.ukoeln.templatedesigner.helper.EventListener;
import de.mosgrid.ukoeln.templatedesigner.helper.ListenerSupport;
import de.mosgrid.ukoeln.templatedesigner.helper.StackTraceHelper;

public class ASMHelper {
	private final Logger LOGGER = LoggerFactory.getLogger(ASMHelper.class);
	private final MosgridUser _user;
	private final TDMainTab _tdMainTab;
	private static final String TDIMPORTNAME = "TDDummyImport";
	private final ListenerSupport<EventArgs<TemplateBean>> _lsForImportEnd = new ListenerSupport<EventArgs<TemplateBean>>();
	private final ListenerSupport<EventArgsNonGeneric> _lsForImportStart = new ListenerSupport<EventArgsNonGeneric>();
	private TemplateDesignerApplication _app;
	private Hashtable<String, TemplateBean> _curWrapper = new Hashtable<String, TDMainDocument.TemplateBean>();

	public ASMHelper(MosgridUser user, TDMainTab tdMainTab, TemplateDesignerApplication app) {
		_user = user;
		_tdMainTab = tdMainTab;
		_app = app;
		_app.addImportListener(new IImportListener() {
			
			@Override
			public void importSucceeded(ImportedWorkflow newImport) {
				TemplateBean imported = _curWrapper.get(newImport.getTemplate().getName());
				imported.setInstance(new MGASMWorkflow(_app.getAsmService(), _app.getUser(), newImport.getAsmInstance()));
				imported.setSuccessfullyOpened(true);
				_lsForImportEnd.fireEvent(new EventArgs<TDMainDocument.TemplateBean>(imported));
				_curWrapper.remove(imported);
			}
			
			@Override
			public void importFailed(MSMLTemplate failedImport, String userImportName, ImportFailedException e) {
				TemplateBean imported = _curWrapper.get(failedImport.getName());
				imported.setSuccessfullyOpened(false);
				_lsForImportEnd.fireEvent(new EventArgs<TDMainDocument.TemplateBean>(imported));
				_curWrapper.remove(imported);
				LOGGER.error("Could not import " + failedImport.getName() + 
						". Reason " + e + ": " + e.getMessage() + "\n" +
						StackTraceHelper.getTrace(e) + "\nInner cause:" + 
						StackTraceHelper.getTrace(e.getCause()));
			}
		});
	}

	public synchronized void importWorkflow(final TemplateBean wrapper) {
		_lsForImportStart.fireEvent();
		ImportableWorkflow wkf = new ImportableWorkflow(wrapper.getDomain(), wrapper.getTemplate(), wrapper.getWorkflow());
		_curWrapper.put(wkf.getTemplate().getName(), wrapper);
		_app.importWorkflow(wkf, wrapper.getTemplate().getName() + TDIMPORTNAME, _tdMainTab.getPI());			

//		// Create a new thread for this task in order to prevent UI from
//		// 'freezing'
//		Thread importThread = new Thread("ASM Import-Thread") {
//			
//			private void updatePI(float val) {
//				_tdMainTab.updatePI(val);
//			}
//
//			@Override
//			public void run() {
//				MSMLTemplate template = wrapper.getTemplate();
//				ASMRepositoryItemBean wkfBean = wrapper.getWorkflow();
//				ASMWorkflow asmInstance = null;
//				boolean success = false;
//				try {
//					LOGGER.debug(_user + " Trying to import workflow for MSML template "
//							+ template.getJobListElement().getId() + " as " + TDIMPORTNAME);
//
//					// update progress
//					updatePI(0.2f);
//
//					// try to import workflow by ASM
//					String realImportName = WorkflowHelper.getInstance().getFullWorkflowName(TDIMPORTNAME, template);
//					
//					// remove all previous workflows with this dummy-name
//					removeAllDummyWorkflows(realImportName);
//					LOGGER.debug(_user + " Importing workflow " + wkfBean.getItemID() + " as " + realImportName);
//
//					// update progress
//					updatePI(0.25f);
//
//					// import ASM instance
//					String internalWorkflowName = null;
//					try {
//						internalWorkflowName = ASMService.getInstance().ImportWorkflow(_user.getUserID(), realImportName,
//								wkfBean.getUserID(), RepositoryItemTypeConstants.Workflow, wkfBean.getId().toString());
//						if (internalWorkflowName == null) {
//							throw new NullPointerException("ASM service returned empty import name.");
//						}
//					} catch (Exception e) {
//						String message = _user + " Could not import " + wkfBean.getItemID() + "\n";
//						LOGGER.error(message, e);
//						throw new ImportFailedException(message, e);
//					}
//					// update progress
//					updatePI(0.5f);
//
//					LOGGER.trace(_user + " Searching for newly created ASM instance " + internalWorkflowName);
//					// resolve imported workflow instance
//					try {
//						asmInstance = ASMService.getInstance().getASMWorkflow(_user.getUserID(), internalWorkflowName);
//						if (asmInstance == null) {
//							throw new NullPointerException("ASM service returned empty instance.");
//						}
//						wrapper.setInstance(new MGASMWorkflow(
//								ASMService.getInstance(), _user.getUserID(), internalWorkflowName));
//					} catch (Exception e) {
//						String message = _user + " Could not find instance: " + internalWorkflowName + "\n";
//						LOGGER.error(message, e);
//						throw new ImportFailedException(message, e);
//					}
//
//					// update progress
//					updatePI(0.9f);
//
//					String msg = "Successfully imported " + wkfBean.getItemID() + " as " + TDIMPORTNAME;
//					LOGGER.debug(_user + msg);
//					// update progress
//					updatePI(1f);
//					success = true;
//				} catch (ImportFailedException e) {
//					// delete new asm instance if error occurred
//					if (asmInstance != null) {
//						removeASMInstance(asmInstance);
//					}
//					return;
//				} catch (Exception e) {
//					// delete new asm instance if error occurred
//					if (asmInstance != null) {
//						removeASMInstance(asmInstance);
//					}
//					return;
//				} finally {
//					wrapper.setSuccessfullyOpened(success);
//					_lsForImportEnd.fireEvent(new EventArgs<TDMainDocument.TemplateBean>(wrapper));
//				}
//			}
//		};
//		importThread.start();
//		// This will force a repaint of the main tab. Repaint-requests are sent in 
//		// importthread but not handled.
//		_tdMainTab.updatePI(0f);
	}

	public void removeASMInstance(ASMWorkflow wkfInstance) {
		if (wkfInstance != null) {
			String instanceName = wkfInstance.getWorkflowName();
			LOGGER.trace(_user + " Removing imported ASM instance " + instanceName);
			ASMService.getInstance().DeleteWorkflow(_user.getUserID(), instanceName);
		} else {
			LOGGER.info(_user + " Trying to delete not existing ASM workflow instance");
		}
	}

	public void addImportedListener(EventListener<EventArgs<TemplateBean>> l) {
		_lsForImportEnd.addListener(l);
	}
	
	public void addImportStartListener(EventListener<EventArgsNonGeneric> l) {
		_lsForImportStart.addListener(l);
	}

	public List<ASMWorkflow> removeAllDummyWorkflows(String realImportName) {
		ArrayList<ASMWorkflow> workflows = ASMService.getInstance().getASMWorkflows(_user.getUserID());
		for (ASMWorkflow wkfl : workflows) {
			if (!wkfl.getWorkflowName().contains(realImportName))
				continue;
			ASMService.getInstance().DeleteWorkflow(_user.getUserID(), wkfl.getWorkflowName());
		}
		return workflows;
	}
}
