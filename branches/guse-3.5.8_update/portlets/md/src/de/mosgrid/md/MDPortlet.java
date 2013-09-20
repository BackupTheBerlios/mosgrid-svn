package de.mosgrid.md;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.gui.DomainPortletWindow;
import de.mosgrid.md.specials.CustomContextMenuHandler;
import de.mosgrid.md.specials.MDInputMaskFactory;
import de.mosgrid.md.uploadpostprocessing.StructurePostprocessorFactory;
import de.mosgrid.portlet.AboutInfo;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.util.DefaultUploadPostprocessorFactory;
import de.mosgrid.util.interfaces.IInputMaskFactory;

public class MDPortlet extends DomainPortlet {
	/* constants */
	private static final long serialVersionUID = 4594190353687371006L;

	/* instance variables */
	private final Logger log = LoggerFactory.getLogger(MDPortlet.class);
	private MDInputMaskFactory inputMaskFactory;

	// private SLF4JBridgeHandler julBridgeHandler;

	public MDPortlet() {
		super(DomainId.MOLECULAR_DYNAMICS);

		initLogger();
		initPostprocessorFactory();
	}

	/**
	 * Some Logger initialization. Note that log4j.properties is loaded automatically, if placed below WEB-INF/classes.
	 * Maybe bridge jul logging, but results in great overhead.
	 */
	private void initLogger() {
		// add a bridge handler to jul root logger
		// julBridgeHandler = new SLF4JBridgeHandler();
		// LogManager.getLogManager().getLogger("").addHandler(julBridgeHandler);
	}

	private void initPostprocessorFactory() {
		DefaultUploadPostprocessorFactory ppFactory = (DefaultUploadPostprocessorFactory) getUploadPostprocessorFactory();
		ppFactory.addFactory(new StructurePostprocessorFactory());
	}

	@Override
	protected IInputMaskFactory getInputMaskFactory() {
		if (inputMaskFactory == null) {
			inputMaskFactory = new MDInputMaskFactory(this);
		}
		return inputMaskFactory;
	}

	@Override
	public AboutInfo getAboutInfo() {
		AboutInfo info = new AboutInfo("1.1", "Andreas Zink, Jens Krüger, Martin Kruse");
		return info;
	}

	@Override
	public String getWelcomeText() {
		return "This is the Molecular Dynamics (MD) portlet. Before you start the first time, you might read the tutorials before.";
	}

	@Override
	public boolean deleteImportsFromLastSession() {
		return true;
	}

	@Override
	protected void createUI() {
		log.debug(getUser() + " Creating UI");
		DomainPortletWindow window = new DomainPortletWindow("MD Portlet", this);
		window.getMonitoringTab().setContextMenuHanlder(new CustomContextMenuHandler());
		setMainWindow(window);
	}

	@Override
	public void close() {
		super.close();
		// remove jul bridge handler from jul root logger to avoid memory leak
		// if (julBridgeHandler != null) {
		// LogManager.getLogManager().getLogger("").removeHandler(julBridgeHandler);
		// }
	}

}
