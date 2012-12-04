package de.mosgrid.docking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.docking.specials.CustomContextMenuHandler;
import de.mosgrid.docking.uploadpostprocessing.StructurePostprocessorFactory;
import de.mosgrid.gui.DomainPortletWindow;
import de.mosgrid.portlet.AboutInfo;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.portlet.DomainPortlet;

public class DockingPortlet extends DomainPortlet {
	/* constants */
	private static final long serialVersionUID = -446891043348888381L;

	/* instance variables */
	private final Logger log = LoggerFactory.getLogger(DockingPortlet.class);

	public DockingPortlet() {
		super(DomainId.DOCKING);

		// add custom upload postprocessor factory
		setUploadPostprocessorFactory(new StructurePostprocessorFactory());
	}

	@Override
	public String getWelcomeText() {
		return "Hello,<br>this is the alpha version of the Docking portlet. This portlet currently allows docking of ligands against a protein using the CADDSuite toolbox.";
	}

	@Override
	public AboutInfo getAboutInfo() {
		AboutInfo info = new AboutInfo("0.5 alpha", "Charlotta Schaerfe, Andreas Zink");
		return info;
	}

	@Override
	public boolean deleteImportsFromLastSession() {
		return false;
	}

	@Override
	protected void createUI() {
		log.debug(getUser() + " Render GUI request started");
		DomainPortletWindow window = new DomainPortletWindow("Docking Portlet", this);
		window.getMonitoringTab().setContextMenuHanlder(new CustomContextMenuHandler());
		setMainWindow(window);

	}

}
