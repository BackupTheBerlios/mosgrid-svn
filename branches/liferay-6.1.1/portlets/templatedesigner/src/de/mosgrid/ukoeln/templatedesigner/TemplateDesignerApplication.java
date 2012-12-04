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
/**
 * Template Designer main application class.
 * 
 * The TemplateDesigner is a portlet to help MoSGrid developers to setup templates. It provides a GUI
 * to define all parameters for all jobs. The user needs to have advanced knowledge on how templates are used as
 * the designer does not provide some kind of wizard to guide the user through the process.
 * 
 * @author mkruse0
 *
 */
public class TemplateDesignerApplication extends MoSGridPortlet implements PortletRequestListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4847207801077002832L;
	private final Logger LOGGER = LoggerFactory.getLogger(TemplateDesignerApplication.class);
	private TDDocumentManager man;

	/**
	 * Close is invoked on session-end. As opening a view is exclusive for one user, all views/templates must be closed
	 * when a user is done editing templates.
	 */
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

	/**
	 * Somewhat the main entry point. Creates the DocumentManager which creates the
	 * main view.
	 */
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
