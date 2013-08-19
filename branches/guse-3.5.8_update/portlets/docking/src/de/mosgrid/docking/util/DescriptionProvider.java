package de.mosgrid.docking.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportableWorkflow;

/**
 * This helper class creates the workflow description components for each docking workflow.
 * 
 * @author Charlotta Schaerfe, based on a class by Andreas Zink
 * 
 */
public class DescriptionProvider {
	/* constants */
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(DescriptionProvider.class);
	private static final String CAPTION = "Docking with BALL/CADDSuite";
	private static final String TEXT_DOCKING_STANDARD = "This workflow allows docking of a set of ligands into a protein using the CADDSuite toolbox";
	// private static final String TEXT_SINGLE_TPR =
	// "The singleTPR workflow enables you to submit an existing GROMACS tpr file. Only Gromacs-4.5 or higher is supported. You may alter the name.";

	/* instance variables */
	private DomainPortlet portlet;

	public DescriptionProvider(DomainPortlet portlet) {
		this.portlet = portlet;
	}

	/**
	 * Creates the description components for all templates
	 */
	public Map<ImportableWorkflow, Component> getDescriptions() {
		Map<ImportableWorkflow, Component> desc = new HashMap<ImportableWorkflow, Component>();
		for (ImportableWorkflow importable : portlet.getImportableWorkflows()) {
			MSMLTemplate template = importable.getTemplate();
			String workflowID = template.getJobListElement().getId();
			Component description = getDescription(workflowID);
			desc.put(importable, description);
		}
		return desc;
	}

	private Component getDescription(String workflowID) {
		Component description = null;
		// if (workflowID.startsWith("GROMACS_MIN")) {
		description = createDockingStandardDescription();
		// } else if (workflowID.startsWith("singleTPR")) {
		// description = createSingleTprDescription();
		// }
		return description;
	}

	/**
	 * Description for the standard docking workflow
	 */

	private Component createDockingStandardDescription() {
		VerticalLayout descriptionComponent = new VerticalLayout();
		descriptionComponent.setCaption(CAPTION);
		descriptionComponent.setSpacing(true);
		descriptionComponent.setWidth("100%");

		Label label = new Label(TEXT_DOCKING_STANDARD);
		descriptionComponent.addComponent(label);

		return descriptionComponent;
	}

	/**
	 * Description for gromacs min workflow
	 * 
	 * private Component createGromacsMinDescription() { VerticalLayout descriptionComponent = new VerticalLayout();
	 * descriptionComponent.setCaption(CAPTION); descriptionComponent.setSpacing(true);
	 * descriptionComponent.setWidth("100%");
	 * 
	 * Label label = new Label(TEXT_GROMACS_MIN); descriptionComponent.addComponent(label);
	 * 
	 * try { Embedded workflowImage = new Embedded("Workflow Graph:"); workflowImage.setType(Embedded.TYPE_IMAGE);
	 * ClassResource imageResource = resourceLoader.loadImage("gromacsmin.png"); workflowImage.setSource(imageResource);
	 * //descriptionComponent.addComponent(workflowImage); } catch (Exception e) {
	 * log.warn("Failed to create embedded image. " + e); }
	 * 
	 * return descriptionComponent; }
	 */

	/**
	 * Description for single TPR workflow
	 * 
	 * private Component createSingleTprDescription() { Label label = new Label(TEXT_SINGLE_TPR);
	 * label.setCaption(CAPTION);
	 * 
	 * label.setWidth("100%"); return label; }
	 */

}
