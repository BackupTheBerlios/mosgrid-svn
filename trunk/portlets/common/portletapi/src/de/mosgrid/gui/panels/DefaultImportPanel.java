package de.mosgrid.gui.panels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Component;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.gui.CardLayout;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportableWorkflow;

/**
 * A default implementation of the AbstractImportPanel. Enables the user to select domain specific workflow. A
 * description component of the selected workflow is dynamically loaded from the portlet. The user can then specifiy an
 * import-name and can import the selected workflow under given name.
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultImportPanel extends AbstractImportPanel {
	private static final long serialVersionUID = -2446416634040318527L;

	@SuppressWarnings("unused")
	private final Logger LOGGER = LoggerFactory.getLogger(DefaultImportPanel.class);

	public static final String CAPTION_SELECT_TOOLSUITE = "Toolsuite";
	public static final String CAPTION_SELECT_WORKFLOW = "Workflow";
	public static final String CAPTION_TEXTFIELD_IMPORT_NAME = "Name";
	public static final String TOOLTIP_SELECT_TOOLSUITE = "Please select a toolsuite. This helps you to narrow down the number of available workflows.";
	public static final String TOOLTIP_SELECT_TEMPLATE = "Please select a workflow.";
	public static final String TOOLTIP_TEXTFIELD_IMPORT_NAME = "Please enter a name for your import in order to identify it later. A timestemp will be added automatically.";
	public static final UserError ERROR_EMPTY = new UserError("Must not be empty!");

	private Map<ImportableWorkflow, Component> descComponents;
	// left part for selection, import name field and import button
	private VerticalLayout leftLayout;
	// right part for workflow descriptions
	private CardLayout rightLayout;

	private Select toolSuiteSelect;
	private Select templateSelect;
	private TextField importNameField;

	public DefaultImportPanel(DomainPortlet portlet) {
		super(portlet);
		init();
	}

	private void init() {
		descComponents = portlet.createWkfDescriptions();
		buildLeftLayout();
		setLeftContent(leftLayout);
		buildRightLayout();
		setRightContent(rightLayout);
	}

	private void buildLeftLayout() {
		leftLayout = new VerticalLayout();
		leftLayout.setWidth("100.0%");
		leftLayout.setHeight("-1px");
		leftLayout.setSpacing(true);
		leftLayout.setMargin(false, true, false, false);

		// toolsuiteselect
		toolSuiteSelect = new Select();
		toolSuiteSelect.setImmediate(true);
		toolSuiteSelect.setCaption(CAPTION_SELECT_TOOLSUITE);
		toolSuiteSelect.setDescription(TOOLTIP_SELECT_TOOLSUITE);
		toolSuiteSelect.setItemCaptionMode(Select.ITEM_CAPTION_MODE_ID);
		Collection<IDictionary> dicts = portlet.getToolsuiteDictionaries();
		BeanItemContainer<IDictionary> dictContainer = new BeanItemContainer<IDictionary>(IDictionary.class, dicts);
		toolSuiteSelect.setContainerDataSource(dictContainer);
		toolSuiteSelect.setNullSelectionAllowed(true);
		toolSuiteSelect.setWidth("100.0%");
		toolSuiteSelect.setHeight("-1px");
		toolSuiteSelect.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = -6134345308033101088L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				toolSuiteSelectionChanged();
			}
		});
		leftLayout.addComponent(toolSuiteSelect);

		// wkfSelectField
		templateSelect = new Select();
		templateSelect.setRequired(true);
		templateSelect.setImmediate(true);
		templateSelect.setCaption(CAPTION_SELECT_WORKFLOW);
		templateSelect.setDescription(TOOLTIP_SELECT_TEMPLATE);

		templateSelect.setItemCaptionMode(Select.ITEM_CAPTION_MODE_ID);
		Collection<ImportableWorkflow> importables = portlet.getImportableWorkflows();
		BeanItemContainer<ImportableWorkflow> importableContainer = new BeanItemContainer<ImportableWorkflow>(
				ImportableWorkflow.class, importables);
		templateSelect.setContainerDataSource(importableContainer);

		templateSelect.setNullSelectionAllowed(true);
		templateSelect.setWidth("100.0%");
		templateSelect.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 9217561873441517476L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				templateSelectionChanged();
			}
		});
		leftLayout.addComponent(templateSelect);

		// importNameField
		importNameField = new TextField();
		importNameField.setRequired(true);
		importNameField.setValue("MyNewImport");
		importNameField.setCaption(CAPTION_TEXTFIELD_IMPORT_NAME);
		importNameField.setDescription(TOOLTIP_TEXTFIELD_IMPORT_NAME);
		importNameField.setWidth("100.0%");
		importNameField.addValidator(new ImportNameValidator());
		leftLayout.addComponent(importNameField);

	}

	private void buildRightLayout() {
		rightLayout = new CardLayout();
		for (Component wkfDescription : descComponents.values()) {
			rightLayout.addComponent(wkfDescription);
		}
	}

	/**
	 * Gets called when toolsuite selection changed
	 */
	private void toolSuiteSelectionChanged() {
		// get current selection
		IDictionary selectedToolSuite = getSelectedToolSuite();

		Collection<ImportableWorkflow> selectableTemplates = null;
		if (selectedToolSuite != null) {
			selectableTemplates = new ArrayList<ImportableWorkflow>();
			// find templates for selected toolsuite
			Collection<MSMLTemplate> templates = portlet.getTemplateManager().getTemplatesByDicitonary(
					selectedToolSuite);
			for (ImportableWorkflow importable : portlet.getImportableWorkflows()) {
				for (MSMLTemplate template : templates) {
					if (importable.getTemplate() == template) {
						selectableTemplates.add(importable);
					}
				}
			}
		} else {
			selectableTemplates = portlet.getImportableWorkflows();
		}
		// update template selection
		BeanItemContainer<ImportableWorkflow> templateContainer = new BeanItemContainer<ImportableWorkflow>(
				ImportableWorkflow.class, selectableTemplates);
		templateSelect.setContainerDataSource(templateContainer);
	}

	/**
	 * Gets called when wkf selection changed
	 */
	private void templateSelectionChanged() {
		// get current selection
		ImportableWorkflow selected = getSelectedWorkflow();

		if (selected != null) {
			templateSelect.setComponentError(null);
			Component component = descComponents.get(selected);
			// show component (bring to front)
			rightLayout.showComponent(component);

			importNameField.setValue(selected.getTemplate().getName());
		} else {
			rightLayout.showComponent(rightLayout.getEmptyComponent());
		}
	}

	@Override
	protected void beforeImportHook() {
		importNameField.setComponentError(null);
	}

	@Override
	protected boolean isValid() {
		boolean isValid = true;
		try {
			templateSelect.validate();
		} catch (EmptyValueException e) {
			isValid = false;
			templateSelect.setComponentError(ERROR_EMPTY);
		} catch (InvalidValueException e) {
			isValid = false;
		}
		try {
			importNameField.validate();
		} catch (EmptyValueException e) {
			isValid = false;
			importNameField.setComponentError(ERROR_EMPTY);
		} catch (InvalidValueException e) {
			isValid = false;
		}
		return isValid;
	}

	@Override
	protected IDictionary getSelectedToolSuite() {
		IDictionary selection = null;
		if (toolSuiteSelect.getValue() != null) {
			selection = (IDictionary) toolSuiteSelect.getValue();
		}
		return selection;
	}

	@Override
	public ImportableWorkflow getSelectedWorkflow() {
		ImportableWorkflow selection = null;
		if (templateSelect.getValue() != null) {
			selection = (ImportableWorkflow) templateSelect.getValue();
		}
		return selection;
	}

	@Override
	public String getImportName() {
		return importNameField.getValue().toString();
	}

}
