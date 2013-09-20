package de.mosgrid.remd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.base.AbstractAdapterFactoryBase;
import de.mosgrid.gui.DomainPortletWindow;
import de.mosgrid.portlet.AboutInfo;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.remd.adapter.RemdAdapterFactory;
import de.mosgrid.remd.uploadpostprocessing.StructurePostprocessorFactory;
import de.mosgrid.remd.util.CustomContextMenuHandler;
import de.mosgrid.remd.util.CustomInputmaskFactory;
import de.mosgrid.util.DefaultUploadPostprocessorFactory;
import de.mosgrid.util.interfaces.IInputMaskFactory;

public class RemdPortlet extends DomainPortlet {
	private static final long serialVersionUID = 2139975962371331870L;
	private final Logger log = LoggerFactory.getLogger(RemdPortlet.class);

	public RemdPortlet() {
		super(DomainId.REPLICA_EXCHANGE_MOLECULAR_DYNAMICS);
		initPostprocessorFactory();
	}

	private void initPostprocessorFactory() {
		DefaultUploadPostprocessorFactory ppFactory = (DefaultUploadPostprocessorFactory) getUploadPostprocessorFactory();
		ppFactory.addFactory(new StructurePostprocessorFactory());
	}

	@Override
	public boolean deleteImportsFromLastSession() {
		return true;
	}

	@Override
	protected void createUI() {
		log.debug(getUser() + " Creating UI");
		DomainPortletWindow window = new DomainPortletWindow("REMD Portlet", this);
		window.getMonitoringTab().setContextMenuHanlder(new CustomContextMenuHandler());
		setMainWindow(window);
	}

	@Override
	public String getWelcomeText() {
		return "This is the Replica Exchange Molecular Dynamics (REMD) portlet. Please visit the help page for further information about REMD (button at the bottom-right).";
	}

	@Override
	public AboutInfo getAboutInfo() {
		AboutInfo info = new AboutInfo("1.2 beta", "Andreas Zink, Jens Krüger");
		return info;
	}

	@Override
	protected AbstractAdapterFactoryBase createAdapterFactoryInstance() {
		return new RemdAdapterFactory();
	}

	@Override
	protected IInputMaskFactory getInputMaskFactory() {
		return new CustomInputmaskFactory(this);
	}

}
