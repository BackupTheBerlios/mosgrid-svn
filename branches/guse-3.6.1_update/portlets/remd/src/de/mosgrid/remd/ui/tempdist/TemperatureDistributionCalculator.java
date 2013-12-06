package de.mosgrid.remd.ui.tempdist;

import java.io.BufferedReader;
import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.gui.inputmask.AbstractJobForm;
import de.mosgrid.gui.inputmask.IInputMaskComponent;
import de.mosgrid.gui.inputmask.uploads.IUploadComponent;
import de.mosgrid.gui.inputmask.uploads.IUploadListener;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.jaxb.bindings.ArrayType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.remd.properties.RemdProperties;
import de.mosgrid.remd.util.ProteinParser;
import de.mosgrid.remd.util.ProteinParser.Result;
import de.mosgrid.remd.util.TemperaturePredictor;
import de.mosgrid.remd.util.TemperaturePredictor.HydrogensInProtein;
import de.mosgrid.remd.util.TemperaturePredictor.IReplica;
import de.mosgrid.remd.util.TemperaturePredictor.ProteinConstraints;
import de.mosgrid.remd.util.TemperaturePredictor.VirtualSites;
import de.mosgrid.remd.util.TemperaturePredictor.WaterConstraints;
import de.mosgrid.util.UploadCollector;

/**
 * Enables the calculation of the perfect temperature distribution
 * 
 * @author Andreas Zink
 * 
 */
public class TemperatureDistributionCalculator extends AbstractJobForm implements IInputMaskComponent,
		ValueChangeListener, IUploadListener {
	private static final long serialVersionUID = -4372039182915499541L;
	private final Logger LOGGER = LoggerFactory.getLogger(TemperatureDistributionCalculator.class);

	private static final String CAPTION_TF_NOPA = "Number of Atoms in Protein";
	private static final String TOOLTIP_TF_NOPA = "The number of protein atoms found in uploaded file.";
	private static final String CAPTION_TF_NOWM = "Number of Water Molecules";
	private static final String TOOLTIP_TF_NOWM = "The number of water molecules found in uploaded file.";
	private static final String CAPTION_TF_PEX = "Exchange Probability";
	private static final String TOOLTIP_TF_PEX = "The desired exchange probability between two replicas.";
	private static final String CAPTION_TF_MAX_REPLICAS = "Max. Replicas";
	private static final String TOOLTIP_TF_MAX_REPLICAS = "The maximum number of replicas.";
	private static final String CAPTION_TF_TEMP_MIN = "Min. Temperature (K)";
	private static final String TOOLTIP_TF_TEMP_MIN = "The lower temperature limit for replicas in Kelvin.";
	private static final String CAPTION_TF_TEMP_MAX = "Max. Temperature (K)";
	private static final String TOOLTIP_TF_TEMP_MAX = "The upper temperature limit for replicas in Kelvin.";
	private static final String CAPTION_BTN_PARSE = "Parse Protein";
	private static final String TOOLTIP_BTN_PARSE = "Reads the protein file from previous step to parse the number of protein atoms and solvent molecules.";
	private static final String CAPTION_BTN_COMPUTE = "Compute Distribution";
	private static final String TOOLTIP_BTN_COMPUTE = "Starts the computation of a temperature distribution. The results will be shown in a subwindow.";
	private static final String CAPTION_LABEL_TEMPS = "Temperatures:";

	private DomainPortlet portlet;
	private ImportedWorkflow wkfImport;
	private TemperaturePredictor predictor;
	private BufferedReader proteinFileReader;
	private List<IReplica> replicas;

	/* ui components */

	private GridLayout gridLayout;

	private TextField pExTextField;
	private TextField maxReplicasTextField;
	private TextField minTempTextField;
	private TextField maxTempTextField;
	private TextField numberOfProteinAtomsTextField;
	private TextField numberOfWaterMoleculesTextField;
	private Button parseProteinButton;
	private Button computeDistButton;
	private Label tempResultsLabel;
	private ProgressIndicator progressIndicator;

	public TemperatureDistributionCalculator(DomainPortlet portlet, ImportedWorkflow wkfImport, Job job) {
		super(job);
		this.portlet = portlet;
		this.wkfImport = wkfImport;
		initTemperaturePredictor();
		fillLeftColumn();
	}

	private void initTemperaturePredictor() {
		predictor = new TemperaturePredictor();
		predictor.setProteinConstraints(ProteinConstraints.ALL_BONDS);
		predictor.setProteinHydrogens(HydrogensInProtein.ALL_H);
		predictor.setVirtualSites(VirtualSites.NONE);
		predictor.setWaterConstraints(WaterConstraints.RIGID);
	}

	@Override
	public void fileArrived(IUploadComponent component, BufferedReader fileReader) {
		reset(false);
		this.proteinFileReader = fileReader;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		// called if upload origin of protein upload changed
		reset(true);
	}

	/**
	 * Resets this component
	 */
	public void reset(boolean fullReset) {
		// delete previous results
		replicas = null;
		numberOfProteinAtomsTextField.setValue(Integer.valueOf(0));
		numberOfWaterMoleculesTextField.setValue(Integer.valueOf(0));
		// update gui
		if (fullReset) {
			// disable parse button on full reset
			if (parseProteinButton.isEnabled()) {
				parseProteinButton.setEnabled(false);
			}
		} else {
			if (!parseProteinButton.isEnabled()) {
				parseProteinButton.setEnabled(true);
			}
		}
		if (computeDistButton.isEnabled()) {
			computeDistButton.setEnabled(false);
		}
		if (tempResultsLabel != null) {
			removeComponent(tempResultsLabel);
			tempResultsLabel = null;
		}
	}

	/**
	 * Called when parse button is clicked
	 */
	protected void parseProtein() {
		Runnable parseTask = new Runnable() {

			@Override
			public void run() {
				LOGGER.trace(portlet.getUser() + " Parsing protein file...");

				// parse protein
				ProteinParser proteinParser = new ProteinParser();
				Result parsingResults = proteinParser.parse(proteinFileReader);

				// update predictor
				predictor.setNumberOfProteinAtoms(parsingResults.getProteinAtoms());
				predictor.setNumberOfWaterMolecues(parsingResults.getWaterMolecules());

				// update textfields
				numberOfProteinAtomsTextField.setValue(parsingResults.getProteinAtoms());
				numberOfWaterMoleculesTextField.setValue(parsingResults.getWaterMolecules());

				// gui updates
				computeDistButton.setEnabled(true);
				parseProteinButton.setEnabled(false);
				toggleProgressIndicator();
				requestRepaint();

				LOGGER.trace(portlet.getUser() + " Found " + predictor.getNumberOfProteinAtoms()
						+ " protein atoms and " + predictor.getNumberOfWaterMolecues() + " water molecules.");

			}
		};
		portlet.getExecutorService().execute(parseTask);

	}

	/**
	 * Called when compute button is clicked and input fields are valid.
	 */
	protected void computeTempDist() {
		Runnable compTaks = new Runnable() {

			@Override
			public void run() {

				replicas = predictor.start();
				TemperatureDistributionResults resultComponent = new TemperatureDistributionResults(replicas);
				Window subWindow = new Window("Results");
				subWindow.setImmediate(true);
				subWindow.setContent(resultComponent);
				subWindow.getContent().setSizeUndefined();
				subWindow.center();
				getWindow().addWindow(subWindow);

				// gui updates
				if (tempResultsLabel != null) {
					removeComponent(tempResultsLabel);
				}
				tempResultsLabel = new Label(createPrettyTempString(replicas, ", "));
				tempResultsLabel.setCaption(CAPTION_LABEL_TEMPS);
				addComponent(tempResultsLabel);

				toggleProgressIndicator();
				requestRepaint();
			}
		};
		portlet.getExecutorService().execute(compTaks);
	}

	/**
	 * Creates a string list of temperature values, separated by given separator
	 */
	private String createPrettyTempString(List<IReplica> replicas, String separator) {
		StringBuilder tempBuilder = new StringBuilder();
		for (IReplica r : replicas) {
			if (tempBuilder.length() > 0) {
				tempBuilder.append(separator);
			}
			tempBuilder.append(r.getTemperatureAsString());
		}
		return tempBuilder.toString();
	}

	private void fillLeftColumn() {
		gridLayout = new GridLayout(2, 4);
		gridLayout.setSpacing(true);

		createExchangeTextField();
		createMaxReplicasTextField();
		createTempTextFields();
		createAtomCountTextFields();
		addComponent(gridLayout);

		createButtonContainer();
	}

	private void createExchangeTextField() {
		pExTextField = new TextField(CAPTION_TF_PEX);
		pExTextField.setDescription(TOOLTIP_TF_PEX);
		pExTextField.setRequired(true);
		pExTextField.setValue(new Double(0.15d));
		pExTextField.addValidator(new ExchangeProbValidator());

		gridLayout.addComponent(pExTextField, 0, 0);
	}

	private void createMaxReplicasTextField() {
		maxReplicasTextField = new TextField(CAPTION_TF_MAX_REPLICAS);
		maxReplicasTextField.setDescription(TOOLTIP_TF_MAX_REPLICAS);
		maxReplicasTextField.setRequired(true);
		maxReplicasTextField.setValue(30);
		maxReplicasTextField.addValidator(new MaxReplicasValidator());

		gridLayout.addComponent(maxReplicasTextField, 0, 1);
	}

	private void createTempTextFields() {
		maxTempTextField = new TextField(CAPTION_TF_TEMP_MAX);
		maxTempTextField.setDescription(TOOLTIP_TF_TEMP_MAX);
		maxTempTextField.setRequired(true);
		maxTempTextField.setValue(350);
		maxTempTextField.addValidator(new TempMaxValidator());

		minTempTextField = new TextField(CAPTION_TF_TEMP_MIN);
		minTempTextField.setDescription(TOOLTIP_TF_TEMP_MIN);
		minTempTextField.setRequired(true);
		minTempTextField.setValue(300);
		minTempTextField.addValidator(new TempMinValidator(maxTempTextField));

		gridLayout.addComponent(minTempTextField, 0, 2);
		gridLayout.addComponent(maxTempTextField, 1, 2);
	}

	private void createAtomCountTextFields() {
		numberOfProteinAtomsTextField = new TextField(CAPTION_TF_NOPA);
		numberOfProteinAtomsTextField.setDescription(TOOLTIP_TF_NOPA);
		numberOfProteinAtomsTextField.setEnabled(false);
		numberOfProteinAtomsTextField.setRequired(true);
		numberOfProteinAtomsTextField.addValidator(new AtomCountValidator());
		gridLayout.addComponent(numberOfProteinAtomsTextField, 0, 3);

		numberOfWaterMoleculesTextField = new TextField(CAPTION_TF_NOWM);
		numberOfWaterMoleculesTextField.setDescription(TOOLTIP_TF_NOWM);
		numberOfWaterMoleculesTextField.setEnabled(false);
		numberOfWaterMoleculesTextField.setRequired(true);
		numberOfWaterMoleculesTextField.addValidator(new AtomCountValidator());
		gridLayout.addComponent(numberOfWaterMoleculesTextField, 1, 3);

	}

	private void createButtonContainer() {
		HorizontalLayout buttonContainer = new HorizontalLayout();
		buttonContainer.setImmediate(true);
		buttonContainer.setSpacing(true);

		parseProteinButton = new Button(CAPTION_BTN_PARSE);
		parseProteinButton.setImmediate(true);
		parseProteinButton.setDescription(TOOLTIP_BTN_PARSE);
		parseProteinButton.setEnabled(false);
		parseProteinButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -770778073112054729L;

			@Override
			public void buttonClick(ClickEvent event) {
				toggleProgressIndicator();
				parseProtein();
			}
		});
		buttonContainer.addComponent(parseProteinButton);

		computeDistButton = new Button(CAPTION_BTN_COMPUTE);
		computeDistButton.setImmediate(true);
		computeDistButton.setDescription(TOOLTIP_BTN_COMPUTE);
		computeDistButton.setEnabled(false);
		computeDistButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -3049772021568233320L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					pExTextField.validate();
					predictor.setDesiredExchangeProbability(Double.valueOf(pExTextField.getValue().toString()));
					maxReplicasTextField.validate();
					predictor.setMaxNumberOfReplicas(Integer.valueOf(maxReplicasTextField.getValue().toString()));
					maxTempTextField.validate();
					predictor.setUpperTemperatureLimit(Double.valueOf(maxTempTextField.getValue().toString()));
					minTempTextField.validate();
					predictor.setLowerTemperatureLimit(Double.valueOf(minTempTextField.getValue().toString()));

					numberOfProteinAtomsTextField.validate();
					numberOfWaterMoleculesTextField.validate();

					toggleProgressIndicator();
					computeTempDist();
				} catch (Exception e) {

				}
			}
		});
		buttonContainer.addComponent(computeDistButton);

		progressIndicator = new ProgressIndicator();
		progressIndicator.setIndeterminate(true);
		progressIndicator.setVisible(false);
		progressIndicator.setEnabled(false);
		buttonContainer.addComponent(progressIndicator);

		addComponent(buttonContainer);

	}

	private void toggleProgressIndicator() {
		if (progressIndicator.isEnabled()) {
			progressIndicator.setEnabled(false);
			progressIndicator.setVisible(false);
		} else {
			progressIndicator.setEnabled(true);
			progressIndicator.setVisible(true);
		}

	}

	@Override
	public boolean commitAndValidate() {
		return replicas != null;
	}

	@Override
	public void afterCommitAndValidate(AbstractInputMask parent) {
		setTemperaturesInTemplate();
		setNumberOfExecutions();
	}

	/**
	 * Creates a temperature string (for array representation) and adds it as parameter to jobs pc1 and pc2
	 */
	private void setTemperaturesInTemplate() {
		// compute temperature string
		String tempValues = createPrettyTempString(replicas, " ");
		LOGGER.trace(portlet.getUser() + tempValues);

		MSMLTemplate template = wkfImport.getTemplate();
		String precompiler1 = RemdProperties.get(RemdProperties.REMD_PRECOMPILER1);
		setTemperatures(tempValues, replicas.size(), template.getJobWithID(precompiler1));

		String precompiler2 = RemdProperties.get(RemdProperties.REMD_PRECOMPILER2);
		setTemperatures(tempValues, replicas.size(), template.getJobWithID(precompiler2));
	}

	/**
	 * Creates and adds a new parameter element with temperature array to parameterlist of job
	 */
	private void setTemperatures(String tempValues, int values, Job job) {
		ArrayType arrayElement = new ArrayType();
		arrayElement.setValue(tempValues);
		arrayElement.setDataType("xsd:double");
		arrayElement.setSize(new BigInteger(String.valueOf(values)));

		ParameterType parameterElement = new ParameterType();
		String tempParamRef = RemdProperties.get(RemdProperties.DICT_ENTRY_TEMP);
		parameterElement.setDictRef(tempParamRef);
		parameterElement.setArray(arrayElement);

		job.getInitialization().getParamList().getParameter().add(parameterElement);
	}

	/**
	 * Sets the number of executions to the number of replicas
	 */
	private void setNumberOfExecutions() {
		int number = replicas.size();
		// set ports
		setNumberOfInputFiles(number, RemdProperties.get(RemdProperties.REMD_PRECOMPILER1),
				RemdProperties.get(RemdProperties.REMD_PRECOMPILER1_MDP_PORT));
		setNumberOfInputFiles(number, RemdProperties.get(RemdProperties.REMD_PRECOMPILER2),
				RemdProperties.get(RemdProperties.REMD_PRECOMPILER2_MDP_PORT));

		// set mdrun param
		String mdrunId = RemdProperties.get(RemdProperties.REMD_MAIN_JOB_ID);
		String multiDictId = RemdProperties.get(RemdProperties.DICT_ENTRY_MULTI);
		Job mdrun = wkfImport.getTemplate().getJobWithID(mdrunId);
		for (ParameterType parameter : mdrun.getInitialization().getParamList().getParameter()) {
			if (parameter.getDictRef().endsWith(multiDictId)) {
				parameter.getScalar().setValue(Integer.valueOf(number).toString());
			}
		}
	}

	private void setNumberOfInputFiles(int number, String jobId, String portId) {
		portlet.setNumberOfInputFiles(portlet.getUser().getUserID(),
				wkfImport.getAsmInstance().getWorkflowName(), jobId, portId, number);
	}

	@Override
	public void beforeSubmit(AbstractInputMask parent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeRemove(AbstractInputMask parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collectUploads(UploadCollector collector) {
		// TODO Auto-generated method stub
	}

	/* ------------------ VALIDATORS --------------------- */

	/**
	 * Validator for exchange probability
	 */
	private class ExchangeProbValidator implements Validator {
		private static final long serialVersionUID = 1449506506223013250L;

		@Override
		public void validate(Object value) throws InvalidValueException {
			if (!isValid(value)) {
				throw new InvalidValueException(
						"The probability value must be numerical, greater than zero and less than one.");
			}
		}

		@Override
		public boolean isValid(Object value) {
			try {
				Double pExValue = Double.valueOf(value.toString());
				if (pExValue > 0 && pExValue < 1) {
					return true;
				}
			} catch (Exception e) {
				// conversion error, wrong input
			}
			return false;
		}
	}

	/**
	 * Validator of min. temperature
	 */
	private class TempMinValidator implements Validator {
		private static final long serialVersionUID = -5315368614695447427L;
		private TextField maxTf;

		protected TempMinValidator(TextField maxTf) {
			super();
			this.maxTf = maxTf;
		}

		@Override
		public void validate(Object value) throws InvalidValueException {
			if (!isValid(value)) {
				throw new InvalidValueException(
						"The min. temperature must be numerical, greater than 0 K and smaller than the max. temperature!");
			}
		}

		@Override
		public boolean isValid(Object value) {
			try {
				Double minValue = Double.valueOf(value.toString());
				Double maxValue = Double.valueOf(maxTf.getValue().toString());
				if (minValue > 0 && minValue < maxValue) {
					return true;
				}
			} catch (Exception e) {
				// conversion error, wrong input
			}
			return false;
		}
	}

	/**
	 * Validator of max. temperature
	 */
	private class TempMaxValidator implements Validator {
		private static final long serialVersionUID = 1609550488273398836L;

		@Override
		public void validate(Object value) throws InvalidValueException {
			if (!isValid(value)) {
				throw new InvalidValueException("The max. temperature must be numerical and greater than 0 K!");
			}
		}

		@Override
		public boolean isValid(Object value) {
			try {
				Double maxValue = Double.valueOf(value.toString());
				if (maxValue > 0) {
					return true;
				}
			} catch (Exception e) {
				// conversion error, wrong input
			}
			return false;
		}
	}

	/**
	 * Validator of max. replicas
	 */
	private class MaxReplicasValidator implements Validator {
		private static final long serialVersionUID = 9121439758032019463L;

		@Override
		public void validate(Object value) throws InvalidValueException {
			if (!isValid(value)) {
				throw new InvalidValueException("The max. number of replicas must be numerical and greater than zero!");
			}
		}

		@Override
		public boolean isValid(Object value) {
			try {
				Integer maxValue = Integer.valueOf(value.toString());
				if (maxValue > 0) {
					return true;
				}
			} catch (Exception e) {
				// conversion error, wrong input
			}
			return false;
		}
	}

	/**
	 * Validator for number of atoms
	 */
	private class AtomCountValidator implements Validator {
		private static final long serialVersionUID = -4833655162718552657L;

		@Override
		public void validate(Object value) throws InvalidValueException {
			if (!isValid(value)) {
				throw new InvalidValueException("Must be greater or equal zero!");
			}
		}

		@Override
		public boolean isValid(Object value) {
			try {
				Integer count = Integer.valueOf(value.toString());
				if (count >= 0) {
					return true;
				}
			} catch (Exception e) {
				// conversion error, wrong input
			}
			return false;
		}
	}

}
