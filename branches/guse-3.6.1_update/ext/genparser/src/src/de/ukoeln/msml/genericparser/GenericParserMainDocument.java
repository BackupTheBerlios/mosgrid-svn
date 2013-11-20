package de.ukoeln.msml.genericparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.jaxb.bindings.Cml;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobParserConfig;
import de.ukoeln.msml.genericparser.classes.CLASSTYPE;
import de.ukoeln.msml.genericparser.classes.ClassInfo;
import de.ukoeln.msml.genericparser.classes.ClassInfoBase;
import de.ukoeln.msml.genericparser.classes.CustomClassInfo;
import de.ukoeln.msml.genericparser.classes.visitors.GeneralPuroposeVistorMode;
import de.ukoeln.msml.genericparser.classes.visitors.VisitorCallBack;
import de.ukoeln.msml.genericparser.gui.GenericParserMainFrame;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigAdvType;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigRootType;
import de.ukoeln.msml.genericparser.gui.configuration.ConfigSimplType;
import de.ukoeln.msml.genericparser.gui.configuration.GenericParserConfig;
import de.ukoeln.msml.genericparser.gui.configuration.ObjectFactory;
import de.ukoeln.msml.genericparser.gui.extension.MSMLCountExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLElementCountExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLElementSeparatorExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFileSelectorExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFixedTextExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLLimiterExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLListExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLMoleculeExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLParserExtensionBase;
import de.ukoeln.msml.genericparser.gui.extension.MSMLPropertyExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLRegexExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLReplaceExtension;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLParserExtension;
import de.ukoeln.msml.genericparser.gui.interfaces.swingimpl.DictTableModelSwingImpl.DictRefData;
import de.ukoeln.msml.genericparser.parameterhandler.ParameterHandler;
import de.ukoeln.msml.genericparser.resources.Resources;
import de.ukoeln.msml.genericparser.worker.MSMLExtensionHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;
import de.ukoeln.msml.genericparser.worker.StringH;

public class GenericParserMainDocument {

	private static Logger LOGGER = LoggerFactory.getLogger(GenericParserMainDocument.class);
	private GenericParserMainFrame _mainview;
	private List<MSMLParserExtensionBase> _msmlFilterExtensions = new ArrayList<MSMLParserExtensionBase>();
	public static ObjectFactory ConfigObjectFactory = new ObjectFactory();
	private static StartupParams _params;
	private static String _baseFolder;
	private GenericParserDocument _advDoc = null;
	private GenericParserDocumentSimple _simplDoc = null;
	private DictionaryDocument _dictDoc;
	private MSMLHandleInfo _curHandleInfo;
	private boolean _msmlTestdrive;

	public void Init(StartupParams params) {
		try {
			InputStream stream = GenericParserMainDocument.class.getResourceAsStream("date.txt");
			if (stream != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));
				String line = br.readLine();
				LOGGER.info(line);
			}
		} catch (IOException e) {
			// doesn't matter.
		}

		_params = params;

		ClassInfo.ClassInfoFactory.initFromFactory(de.mosgrid.msml.jaxb.bindings.ObjectFactory.class);
		_advDoc = new GenericParserDocument(this);
		_simplDoc = new GenericParserDocumentSimple(this);
		_dictDoc = new DictionaryDocument(this);
		_advDoc.Init(params);
		_simplDoc.Init(params);
		_dictDoc.Init(params);

		// TODO make extensions load and add automatically
		MSMLExtensionHelper helper = new MSMLExtensionHelper(this);
		_msmlFilterExtensions.add(new MSMLListExtension(helper));
		_msmlFilterExtensions.add(new MSMLFileSelectorExtension(helper));
		_msmlFilterExtensions.add(new MSMLElementSeparatorExtension(helper));
		_msmlFilterExtensions.add(new MSMLLimiterExtension(helper));
		_msmlFilterExtensions.add(new MSMLRegexExtension(helper));
		_msmlFilterExtensions.add(new MSMLReplaceExtension(helper));
		_msmlFilterExtensions.add(new MSMLCountExtension(helper));
		_msmlFilterExtensions.add(new MSMLFixedTextExtension(helper));
		_msmlFilterExtensions.add(new MSMLPropertyExtension(helper));
		_msmlFilterExtensions.add(new MSMLMoleculeExtension(helper));
		_msmlFilterExtensions.add(new MSMLElementCountExtension(helper));

		if (_params.useX()) {
			_mainview = new GenericParserMainFrame(this, _advDoc.getData(), _simplDoc.getData(), _dictDoc.getData());
			_mainview.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setMainView(_mainview);

			afterInit();

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getMainView().setVisible(true);
				}
			});

		} else {
			loadAndHandleMSML();
			// _mainview.dispose();
		}
		LOGGER.info("Done parsing.");
	}

	void loadAndHandleMSML() {
		JobListEditor msmlEdit = new JobListEditor(new File(getParams().getMsmlFile()));

		if (!StringH.isNullOrEmpty(getParams().getJobID()))
			loadAndHandleMSMLForSingleJob(msmlEdit, getParams().getJobID());
		else
			loadAndHandleMSMLForAllJobs(msmlEdit);
	}

	private void loadAndHandleMSMLForAllJobs(JobListEditor msmlEdit) {
		for (Job job : msmlEdit.getJobListElement().getJobs())
			loadAndHandleMSMLForJob(msmlEdit, job);
	}

	private void loadAndHandleMSMLForSingleJob(JobListEditor msmlEdit, String jobID) {
		Job foundJob = msmlEdit.getJobWithID(getParams().getJobID());

		loadAndHandleMSMLForJob(msmlEdit, foundJob);
	}

	private void loadAndHandleMSMLForJob(JobListEditor msmlEdit, Job job) {
		if (job == null || job.getInitialization() == null || job.getInitialization().getParserConfig() == null)
			return;

		JobParserConfig parserConf = job.getInitialization().getParserConfig();
		_curHandleInfo = new MSMLHandleInfo(job, msmlEdit);

		if (_msmlTestdrive)
			ParameterHandler.handleTestRun(this);
		else
			ParameterHandler.handle(this, msmlEdit, parserConf);
	}

	public MSMLHandleInfo getCurrentHandleInfo() {
		if (_curHandleInfo == null)
			StackTraceHelper.handleException(new Throwable("Current job is null. This should never happen."),
					ON_EXCEPTION.QUIT);
		return _curHandleInfo;
	}

	private void afterInit() {
		loadConfig(Resources.class.getResourceAsStream("base_config.xml"));
		_advDoc.afterInit();
		_simplDoc.afterInit();

	}

	public static String getBaseFolder() {
		if (!StringH.isNullOrEmpty(_baseFolder))
			return _baseFolder;
		return _params.getBaseFolder();
	}

	public void setMainView(GenericParserMainFrame mainview) {
		_mainview = mainview;
	}

	public void saveMSML() {
		String file;
		final JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("MSML-Files (*.xml)", "xml"));
		int returnVal = fc.showSaveDialog(_mainview);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile().getAbsolutePath();
		} else {
			return;
		}

		// Cml tmpCML = null;
		// JAXBContext context;
		JobListEditor jle = new JobListEditor(fc.getSelectedFile());
		// try {
		// context = JAXBContext.newInstance(Cml.class);
		// Unmarshaller m = context.createUnmarshaller();
		// tmpCML = (Cml) m.unmarshal(fc.getSelectedFile());
		//
		// } catch (JAXBException e) {
		// StackTraceHelper.handleException(e, ON_EXCEPTION.CONTINUE);
		// }
		//
		// JobListEditor jle = new JobListEditor(tmpCML);
		List<Job> jobs = jle.getJobListElement().getJobs();
		String[] options = new String[jobs.size()];
		for (int i = 0; i < jobs.size(); i++) {
			options[i] = jobs.get(i).getId();
		}

		String s = (String) JOptionPane.showInputDialog(_mainview, "Choose job to handle.", "Choose job",
				JOptionPane.PLAIN_MESSAGE, null, options, options.length == 0 ? null : options[0]);

		if (s == null || s.length() == 0)
			return;

		StartupParams tmpParams = _params;
		_msmlTestdrive = true;
		try {
			_params = new StartupParams(file, getBaseFolder(), s, file + ".out");
			loadAndHandleMSML();
		} catch (Exception e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.CONTINUE);
		} finally {
			_params = tmpParams;
			_msmlTestdrive = false;
		}
	}

	public Cml getCml() {
		Cml cml = _advDoc.getCml();
		_simplDoc.adjustCml(cml);
		return cml;
	}

	public List<IMSMLParserExtension> getExtensionsToVal(MSMLTreeValueBase newVal) {
		List<IMSMLParserExtension> result = new ArrayList<IMSMLParserExtension>();
		ClassInfoBase inf = newVal.getInfo();
		for (IMSMLParserExtension cmp : _msmlFilterExtensions) {
			if (componentApplysToClassInfo(cmp, inf, newVal))
				result.add(cmp);
		}
		return result;
	}

	public boolean componentApplysToClassInfo(IMSMLParserExtension cmp, ClassInfoBase inf, MSMLTreeValueBase value) {
		if (inf.getType().equals(CLASSTYPE.CUSTOM)) {
			if (!((CustomClassInfo<?, ?>) inf).applysTo(cmp))
				return false;
			return cmp.shouldBeVisibleToValue(value);
		}
		CLASSTYPE t = cmp.getClassInfoTypeToApply();
		if (!t.contains(inf.getType()))
			return false;
		return true;
	}

	public void saveConfig() {
		ConfigRootType root = ConfigObjectFactory.createConfigRootType();

		ConfigAdvType conf = _advDoc.getConfig();
		root.setAdv(conf);

		ConfigSimplType simplConf = _simplDoc.getConfig();
		root.setSimpl(simplConf);

		root.getDicts().clear();
		for (DictRefData dict : getDictDoc().getData().getActiveDicts()) {
			root.getDicts().add(dict.getDict());
		}

		JAXBElement<ConfigRootType> rootConf = ConfigObjectFactory.createRoot(root);

		final JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("config-Files (*.xml)", "xml"));
		int returnVal = fc.showSaveDialog(_mainview);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(ConfigRootType.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				m.marshal(rootConf, fc.getSelectedFile());
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadConfig() {
		// loading config
		final JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("config-Files (*.xml)", "xml"));
		int returnVal = fc.showOpenDialog(_mainview);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loadConfig(fc.getSelectedFile());
		} else {
			return;
		}

		String path = fc.getSelectedFile().getParentFile().getAbsolutePath();
		Object[] options = { "Yes", "No" };
		int n = JOptionPane.showOptionDialog(_mainview, "Do you want to set " + path + " as base-directory?",
				"Set base path?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n == JOptionPane.YES_OPTION) {
			_baseFolder = path;
		}
	}

	public void loadConfig(File file) {
		try {
			InputStream is = new FileInputStream(file);
			loadConfig(is);
		} catch (FileNotFoundException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
		}
	}

	public void loadConfig(InputStream stream) {
		ConfigRootType rootConfig = null;
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(GenericParserConfig.class.getCanonicalName().replaceAll(
					"\\." + GenericParserConfig.class.getSimpleName() + "$", ""));
			Unmarshaller m = context.createUnmarshaller();
			rootConfig = ((ConfigRootType) ((JAXBElement<?>) m.unmarshal(stream)).getValue());
		} catch (JAXBException e) {
			JOptionPane.showMessageDialog(null, "Error loading configuration. Wrong version? "
					+ "Did you forget to put @XmlRootElement on your root element?\n\nMessage: " + e.getMessage()
					+ "\n\nStackTrace: " + StackTraceHelper.getTrace(e), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}

		_advDoc.loadConfig(rootConfig.getAdv());

		_simplDoc.loadConfig(rootConfig.getSimpl());

		getDictDoc().getData().removeAllData();
		for (String dict : rootConfig.getDicts()) {
			getDictDoc().getData().addData(dict);
		}
	}

	public void chooseBasePath() {
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showSaveDialog(_mainview);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			_baseFolder = fc.getSelectedFile().getAbsolutePath();
		}
	}

	public StartupParams getParams() {
		return _params;
	}

	public GenericParserMainFrame getMainView() {
		return _mainview;
	}

	public List<MSMLParserExtensionBase> getExtensions() {
		return _msmlFilterExtensions;
	}

	public void triggerGeneralPurposeVisitor(GeneralPuroposeVistorMode mode, VisitorCallBack initCallback) {
		_mainview.triggerGeneralPurposeVisitor(mode, initCallback);
	}

	public GenericParserDocument getAdvDoc() {
		return _advDoc;
	}

	public GenericParserDocumentSimple getSimpleDoc() {
		return _simplDoc;
	}

	public DictionaryDocument getDictDoc() {
		return _dictDoc;
	}

	public String getTextToVal(MSMLTreeValueBase val) {
		if (val instanceof MSMLSimpleTreeValue)
			return getSimpleDoc().getTextToVal(val);
		else if (val instanceof MSMLTreeValue)
			return getAdvDoc().getTextToVal(val);
		return null;
	}

	public boolean isMSMLTestdrive() {
		return _msmlTestdrive;
	}

	public void addLayer() {
		_simplDoc.addLayer();
	}

	public void resetLayer() {
		_simplDoc.resetLayer();
	}

	public void selectLayer(int rowAtPoint) {
		_simplDoc.selectLayer(rowAtPoint);
	}

	public static boolean useX() {
		return _params.useX();
	}
}
