package de.mosgrid.qc.gui.inputmask;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.gui.inputmask.IInputMaskComponent;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.qc.adapter.GaussianAdapter03;
import de.mosgrid.qc.adapter.GaussianAdapter09;
import de.mosgrid.util.UploadCollector;

public class QCStructureInputForm extends CustomComponent implements IInputMaskComponent {

	private static final long serialVersionUID = -3431259312419970324L;
	private Panel _mainPanel;
	private StructureValidator _validator;
	private TextArea _structureField;
	private ComponentContainer _componentContainer;

	private QCStructureInputForm() {
	}

	public static QCStructureInputForm createInstance() {
		QCStructureInputForm form = new QCStructureInputForm();
		form.init();
		return form;
	}
	
	private void init() {
		buildMainPanel();
		setCompositionRoot(_mainPanel);
	}

	private void buildMainPanel() {
		_mainPanel = new Panel();
		_mainPanel.setStyleName(Reindeer.PANEL_LIGHT);
		_mainPanel.setWidth("100%");
		_mainPanel.setHeight("-1px");
		
		buildComponentContainer();
		_mainPanel.setContent(_componentContainer);
	}

	private void buildComponentContainer() {
		VerticalLayout _structureInputLayout = new VerticalLayout();
		_structureInputLayout.setImmediate(true);
		_structureInputLayout.setMargin(true, false, false, false);
		_structureInputLayout.setSpacing(true);
		_structureInputLayout.setWidth("100%");
		_structureInputLayout.setHeight("-1px");
		_structureInputLayout.setStyleName(Reindeer.PANEL_LIGHT);
		
		Label _structureLabel = new Label();
		_structureLabel.setContentMode(Label.CONTENT_XHTML);
		_structureLabel.setValue(
				"<b>molecule structure in xyz format</b><br/><br/>e.g.:<br/>" +
				"O  -0.464   0.177   0.0<br/>" +
				"H  -0.464   1.137   0.0<br/>" +
				"H   0.441  -0.143   0.0");
		_structureInputLayout.setSizeFull();
		_structureInputLayout.addComponent(_structureLabel);
		
		_structureField = new TextArea();
		_validator = new StructureValidator();
		// should be referenced in css... not done right now.
		_structureField.setStyleName("v-textarea-notresizeable");
		_structureField.setSizeFull();
		_structureField.addValidator(_validator);

		_structureInputLayout.addComponent(_structureField);
		_componentContainer = _structureInputLayout;
	}

	@Override
	public boolean commitAndValidate() {
		_structureField.commit();
		return _structureField.isValid();
	}

	@Override
	public void beforeSubmit(AbstractInputMask parent) {

	}

	@Override
	public void beforeRemove(AbstractInputMask parent) {
		
	}

	@Override
	public void collectUploads(UploadCollector collector) {
		
	}

	@Override
	public void afterCommitAndValidate(AbstractInputMask parent) {
		for (Job job : parent.getTemplate().getJobListElement().getJobs()) {
			if (job.getInitialization() == null ||
					job.getInitialization().getAdapterConfig() == null ||
					(!GaussianAdapter03.ID.equals(job.getInitialization().getAdapterConfig().getAdapterID()) &&
							!GaussianAdapter09.ID.equals(job.getInitialization().getAdapterConfig().getAdapterID())))
				continue;
			
			job.getInitialization().setMolecule(_validator.getLastValidatedMolecule());
			break;
		}
	}

}
