package de.mosgrid.util;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.jouni.animator.Animator;
import org.w3c.dom.Element;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.mosgrid.exceptions.InputMaskCreationException;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.gui.inputmask.AbstractJobForm;
import de.mosgrid.gui.inputmask.DefaultInputMask;
import de.mosgrid.gui.inputmask.DefaultJobForm;
import de.mosgrid.gui.inputmask.uploads.IUploadComponent;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobList;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.interfaces.IFieldFactory;

/**
 * Default input mask factory. Uses the default input mask classes.
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultInputMaskFactory extends AbstractInputMaskFactory {
	private final Logger LOGGER = LoggerFactory.getLogger(DefaultInputMaskFactory.class);
	protected DomainPortlet portlet;

	public DefaultInputMaskFactory(DomainPortlet portlet) {
		this.portlet = portlet;
	}
	
	public DefaultInputMaskFactory(DomainPortlet portlet, IFieldFactory fieldFactory) {
		super(fieldFactory);
		this.portlet = portlet;
	}

	@Override
	public AbstractInputMask createInputMask(ImportedWorkflow wkfImport) throws InputMaskCreationException {
		if (wkfImport.getAsmInstance() == null || wkfImport.getTemplate() == null) {
			String msg = "Could not create input mask because of missing template or wkf instance";
			LOGGER.error(portlet.getUser() + msg);
			throw new InputMaskCreationException(msg);
		}
		// create empty input mask instance first
		AbstractInputMask inputMask = createEmptyInputMask(wkfImport);

		// get list of jobs defined in template
		JobList jobList = wkfImport.getTemplate().getJobListElement();
		// iterate over jobs
		for (Job job : jobList.getJobs()) {
			AbstractJobForm jobForm = createJobForm(wkfImport, inputMask, job);
			if (jobForm != null) {
				inputMask.addComponent(jobForm);
			}
		}

		return inputMask;
	}

	/**
	 * Creates an empty input mask. By default DefaultInputMask is created, but can be overridden in subclasses.
	 */
	protected AbstractInputMask createEmptyInputMask(ImportedWorkflow wkfImport) throws InputMaskCreationException {
		return new DefaultInputMask(portlet, wkfImport);
	}

	/**
	 * Creates a job input-form for given job. By default creates a DefaultJobForm, but can be overridden in subclasses.
	 */
	protected AbstractJobForm createJobForm(ImportedWorkflow wkfImport, AbstractInputMask inputMask, Job job)
			throws InputMaskCreationException {
		DefaultJobForm jobForm = null;

		if (job.needsUserInput()) {
			jobForm = new DefaultJobForm(job);
			try {
				// fill left column
				for (IUploadComponent uploadField : createUploadFields(wkfImport, job)) {
					jobForm.addUploadField(uploadField);
				}
				for (Field inputField : createInitInputFields(wkfImport, job)) {
					jobForm.addInputField(inputField);
				}
				for (Field inputField : createEnvironInputFields(wkfImport, job)) {
					jobForm.addInputField(inputField);
				}

				// fill right column
				jobForm.addComponentToRightColumn(createJobDescription(wkfImport, job));

			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				throw new InputMaskCreationException(e.getMessage(), e);
			}
		}
		return jobForm;
	}

	/**
	 * Creates a description comoponent for each job by reading the jobs description element in the template. The first
	 * paragraph of the description will be shown permanently. The rest is hidden behind an expandable component.
	 */
	protected Component createJobDescription(ImportedWorkflow wkfImport, Job job) {
		final String BREAK_LINE = "<br>";
		VerticalLayout descriptionContainer = new VerticalLayout();
		descriptionContainer.setImmediate(true);
		descriptionContainer.setSpacing(true);

		// read textual description
		boolean isHTML = true;
		Label firstParagraphLabel = null;
		StringBuilder descriptionBuilder = new StringBuilder();
		if (job.getDescription() != null) {
			if (job.getDescription().getPlainText() != null &&
					!"".equals(job.getDescription().getPlainText())) {
				String plainText = job.getDescription().getPlainText();
				String[] plainTextParts = plainText.split("\n");
				firstParagraphLabel = new Label(plainTextParts[0]);
				descriptionContainer.addComponent(firstParagraphLabel);
				
				for (int i = 1; i < plainTextParts.length; i++) {
					descriptionBuilder.append(plainTextParts[i]);
				}
				isHTML = false;
			} else {
				for (Element child : job.getDescription().getAny()) {
					if (firstParagraphLabel == null) {
						// first paragraph has its own label
						firstParagraphLabel = new Label(child.getTextContent());
						firstParagraphLabel.setContentMode(Label.CONTENT_XHTML);
						descriptionContainer.addComponent(firstParagraphLabel);
					} else {
						descriptionBuilder.append(child.getTextContent());
						descriptionBuilder.append(BREAK_LINE);
					}
				}		
			}
		}
		// create label for rest of description
		Label descriptionLabel = null;
		if (descriptionBuilder.length() > 0) {
			descriptionLabel = new Label(descriptionBuilder.toString());
			if (isHTML)
				descriptionLabel.setContentMode(Label.CONTENT_XHTML);
		}
		// read fixed parameters
		Collection<Label> fixedParamesLabels = null;
		try {
			fixedParamesLabels = createLabelsForNotEditableParameters(wkfImport, job);
		} catch (Exception e) {
			LOGGER.error("Could not create labels for non-editable parameters...", e);
		}

		// build animator for roll-down or -up
		VerticalLayout animatedDescriptionContainer = new VerticalLayout();
		// animatedDescriptionContainer.setSpacing(true);

		if (descriptionLabel != null) {
			animatedDescriptionContainer.addComponent(descriptionLabel);
		}
		if (fixedParamesLabels != null) {
			for (Label paramLabel : fixedParamesLabels) {
				animatedDescriptionContainer.addComponent(paramLabel);
			}
		}

		if (descriptionLabel != null || (fixedParamesLabels != null && fixedParamesLabels.size() > 0)) {
			final Animator animator = new Animator(animatedDescriptionContainer);
			descriptionContainer.addComponent(animator);
			animator.setWidth("100%");
			animator.setRolledUp(true);

			Button readMoreButton = new Button("Read more...");
			readMoreButton.setImmediate(true);
			readMoreButton.setStyleName(BaseTheme.BUTTON_LINK);
			readMoreButton.addListener(new Button.ClickListener() {
				private static final long serialVersionUID = 599032840233855763L;
				final String readMore = "Read more...";
				final String readLess = "Read less...";

				@Override
				public void buttonClick(ClickEvent event) {
					if (animator.isRolledUp()) {
						animator.rollDown(300, 0);
						event.getButton().setCaption(readLess);
					} else {
						animator.rollUp();
						event.getButton().setCaption(readMore);
					}
				}
			});
			descriptionContainer.addComponent(readMoreButton);
		}

		return descriptionContainer;
	}
}
