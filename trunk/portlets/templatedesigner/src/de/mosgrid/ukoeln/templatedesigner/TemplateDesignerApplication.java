package de.mosgrid.ukoeln.templatedesigner;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.terminal.gwt.server.PortletRequestListener;

import de.mosgrid.exceptions.ImportFailedException;
import de.mosgrid.exceptions.PortletInitializationException;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.util.DictionaryFactory;
import de.mosgrid.portlet.AboutInfo;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.portlet.MoSGridPortlet;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainWindow;

public class TemplateDesignerApplication extends MoSGridPortlet implements PortletRequestListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4847207801077002832L;
	private final Logger LOGGER = LoggerFactory.getLogger(TemplateDesignerApplication.class);
	private TDDocumentManager man;

	
//	public TemplateDesignerApplication() {
//		super();
//		LogManager.getLogManager().reset();
//		SLF4JBridgeHandler.install();
//		java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.FINEST);
//		try {
//			Properties properties = new Properties();
//			InputStream s = TemplateDesignerApplication.class.getResourceAsStream("log4j.properties");
//			properties.load(new BufferedInputStream(s));
//			s.close();
//			PropertyConfigurator.configure(properties);
//		} catch (IOException e) {
//			LOGGER.error("Unable to load properties for Logging.");
//			e.printStackTrace();
//		}
//		
//		Logging.start(Logging.LEVEL_INFO);
//	}

//	@Override
//	public void init() {
//		Logging.start(Logging.LEVEL_INFO, Category.all);
//		if (_request == null) {
//			String message = "Initialization error! Trying to initialize before request start!";
//			Window w = new Window();
//			setMainWindow(w);
//			w.showNotification("Error", message + " Please contact the support.", Notification.TYPE_ERROR_MESSAGE);
//		} else {
//			VaadinXtreemFSSession.initialize(this);
//
//			man = new TDDocumentManager(_user, _request);
//			TDMainWindow window = man.getMainWindow();
//			window.setManager(man);
//			setMainWindow(window);
//		}
//	}
//
//	@Override
//	public void onRequestStart(PortletRequest request, PortletResponse response) {
//		if (_request != null)
//			return;
//		_request = request;
//		String userID = request.getRemoteUser();
//		Principal p = request.getUserPrincipal();
//		_user = new MosgridUser(userID, p);
//	}

	@Override
	public void close() {
		man.getMainDocument().closeAllOpenTemplates(getUser());

		super.close();
	}

	@Override
	public void onRequestEnd(PortletRequest request, PortletResponse response) {}

	@Override
	protected void beforeApplicationInit() throws PortletInitializationException {
		LOGGER.info(getUser() + " Initializing Templatedesigner portlet.");
	}

	@Override
	protected void beforeUiInit() throws PortletInitializationException {
		try {
			DictionaryFactory.getInstance().update();
		} catch (Exception e) {
			// just catch any exceptions...
			String msg = "Portlet initialization failed!";
			LOGGER.error(msg, e);
			throw new PortletInitializationException(msg, e);
		}
		removeNotSubmittedWkfs();
	}

	@Override
	protected void afterApplicationInit() throws PortletInitializationException {}

	@Override
	protected void createUI() {
		man = new TDDocumentManager(getUser(), this);
		TDMainWindow window = man.getMainWindow();
		window.setManager(man);
		setMainWindow(window);
	}

	@Override
	public String getWelcomeText() {
		return "Welcome to the Template Designer.";
	}

	@Override
	public AboutInfo getAboutInfo() {
		return null;
	}

	@Override
	protected ImportedWorkflow retrieveDomainDependandWorkflow(ASMWorkflow workflowInstance, MSMLTemplate template) {
		return new ImportedWorkflow(man.getMainDocument().getSelectedDomain(), workflowInstance, template);
	}

	@Override
	protected void afterImport(ImportedWorkflow newImport) throws ImportFailedException {}
}
