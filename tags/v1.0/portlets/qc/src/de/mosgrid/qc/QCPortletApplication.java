package de.mosgrid.qc;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

import de.mosgrid.adapter.AdapterFactoryForAdapConfig;
import de.mosgrid.gui.DomainPortletWindow;
import de.mosgrid.gui.panels.DefaultImportPanel;
import de.mosgrid.portlet.AboutInfo;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportableWorkflow;
import de.mosgrid.util.interfaces.IInputMaskFactory;

public class QCPortletApplication extends DomainPortlet {

	private static final long serialVersionUID = -2526531754243318342L;
//	private static final Logger LOGGER = LoggerFactory.getLogger(MoSGridPortlet.class);
	
	public QCPortletApplication() {
		super(DomainId.QUANTUM_CHEMISTRY);
		initLogger();
	}

	@Override
	public String getWelcomeText() {
		return "Welcome to the Quantum Chemistry portlet.";
	}

	@Override
	public AboutInfo getAboutInfo() {
		AboutInfo info = new AboutInfo("0.1", "Martin Kruse");
		return info;
	}

	private void initLogger() {
//		LogManager.getLogManager().reset();
//		SLF4JBridgeHandler.install();
//		java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.FINEST);
//		try {
//			Properties properties = new Properties();
//			InputStream s = QCPortletApplication.class.getResourceAsStream("log4j.properties");
//			properties.load(new BufferedInputStream(s));
//			s.close();
//			PropertyConfigurator.configure(properties);
//		} catch (IOException e) {
//			LOGGER.error("Unable to load properties for Logging.");
//			e.printStackTrace();
//		}
	}

	@Override
	protected final IInputMaskFactory getInputMaskFactory() {
		return new QCInputMaskFactory(this);
	}

	@Override
	public boolean deleteImportsFromLastSession() {
		return false;
	}

	@Override
	protected AdapterFactoryForAdapConfig createAdapterFactoryInstance() {
		return new QCAdapterFactory();
	}

	@Override
	public Map<ImportableWorkflow, Component> createWkfDescriptions() {
		Map<ImportableWorkflow, Component> res = new HashMap<ImportableWorkflow, Component>();
		for (ImportableWorkflow wkfl : getImportableWorkflows()) {
			
			String desc = "No Workflow Description available.";
			if (wkfl.getTemplate().hasDescription())
				desc = wkfl.getTemplate().getJobListElement().getDescription().getPlainText();
			res.put(wkfl, new Label(desc));
		}
		return res;
	}

	@Override
	protected void createUI() {
		DomainPortletWindow window = new DomainPortletWindow("Quantum Chemistry", this);
		window.getImportTab().setImportPanel(new DefaultImportPanel(this));
		window.getMonitoringTab().setContextMenuHanlder(new CustomContextMenuHandler());
		setMainWindow(window);
	}

	@Override
	public void close() {
		super.close();
//		SLF4JBridgeHandler.uninstall();
	}
	
	
}
