package de.mosgrid.gui.tabs;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import java.util.Collection;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.gui.CardLayout;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.WorkflowHelper;

/**
 * Tab for submitting imported workflows. Shall enable the selection of all workflows which are in initialization
 * status. These can then either be submitted or removed. Before submitting the user is enabled to modify parameters by
 * a provided WorkflowInputMask.
 * 
 * @author Andreas Zink
 * 
 */
public class SubmissionTab extends CustomComponent implements Property.ValueChangeListener{
	private static final long serialVersionUID = -3669847397037080664L;

	public static final String CAPTION = "Submission";
	public static final String CAPTION_PANEL_SELECT = "Select an imported workflow";
	public static final String CAPTION_SELECT = "Imported Workflow";
	public static final String CAPTION_INPUT_MASK = "Please fill the input mask to submit your workflow:";
	public static final String TOOLTIP_SELECT = "Please select one of your imported workflows to show its input mask.";

	private DomainPortlet portlet;
	private VerticalLayout mainLayout;

	private Panel selectPanel;
	private Select importSelect;
	private CardLayout cardLayout;

	public SubmissionTab(DomainPortlet portlet) {
		this.portlet = portlet;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		setWidth("100.0%");
		setHeight("-1px");

		buildSelectPanel();
		buildInputMaskCardLayout();

		// initial update
		update();
	}

	private void buildSelectPanel() {
		importSelect = new Select();
		importSelect.setImmediate(true);
		importSelect.setDescription(TOOLTIP_SELECT);
//		importSelect.setNullSelectionAllowed(true);
		importSelect.setCaption(CAPTION_SELECT);
		importSelect.setItemCaptionMode(Select.ITEM_CAPTION_MODE_EXPLICIT);
		importSelect.addListener(this);
		importSelect.setWidth("30%");
		importSelect.setHeight("-1px");

		selectPanel = new Panel(CAPTION_PANEL_SELECT);
		selectPanel.setWidth("100%");
		selectPanel.setHeight("-1px");
		selectPanel.addComponent(importSelect);

		mainLayout.addComponent(selectPanel);
	}

	private void buildInputMaskCardLayout() {
		cardLayout = new CardLayout();
//		cardLayout.setBorder(true);
		cardLayout.setCaption(CAPTION_INPUT_MASK);
//		cardLayout.setInnerMargin(true);
		cardLayout.setInnerMargin(true, false, false, false);
		mainLayout.addComponent(cardLayout);
	}

	/**
	 * Gets called if user changes selected import. Brings corresponding input mask to front.
	 */
	private void selectionChanged() {
		if (importSelect != null) {
			Object selected = importSelect.getValue();
			if (selected != null && selected instanceof ImportedWorkflow) {
				ImportedWorkflow selectedImport = (ImportedWorkflow) selected;
				cardLayout.showComponent(selectedImport.getInputMask());
			} else {
				cardLayout.showComponent(cardLayout.getEmptyComponent());
			}
		}
	}
	
	/**
	 * Updates the list of imported wkfs. Usually gets called automatically from DomainPortletWindow.
	 */
	public void update() {
		doUpdate();
		// synchronize input mask to selected instance
		selectionChanged();
	}

	private void doUpdate() {
		// disable for the case that no imports are available
		mainLayout.setEnabled(false);
		// clear existing workflow-components
		cardLayout.removeAllComponents();

		// get all imported wkf
		Collection<ImportedWorkflow> imported = portlet.getImportedWorkflowInstances();
		if (imported.size() > 0) {
			// there are imported wkfs, enable component
			mainLayout.setEnabled(true);
			// add input masks
			for (ImportedWorkflow wkfImport : imported) {
				AbstractInputMask inputMask = wkfImport.getInputMask();
				cardLayout.addComponent(inputMask);
			}
			// wrap in container
			BeanItemContainer<ImportedWorkflow> container = new BeanItemContainer<ImportedWorkflow>(ImportedWorkflow.class,
					imported);
			// update selection
			importSelect.setContainerDataSource(container);
			for (ImportedWorkflow item : container.getItemIds()) {
				ASMWorkflow asmInstance = item.getAsmInstance();
				String caption = WorkflowHelper.getInstance().getUserChosenName(asmInstance);
				importSelect.setItemCaption(item, caption);
			}
			// set selection to first instance
			importSelect.setValue(portlet.getLastImportedWorkflowInstance());
		}
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		selectionChanged();
	}
}
