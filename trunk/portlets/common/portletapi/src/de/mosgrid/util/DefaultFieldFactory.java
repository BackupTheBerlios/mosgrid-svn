package de.mosgrid.util;

import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;

import de.mosgrid.exceptions.InputFieldCreationException;
import de.mosgrid.gui.inputmask.uploads.DefaultUploadSelectionComponent;
import de.mosgrid.gui.inputmask.uploads.IUploadComponent;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.interfaces.IFieldFactory;
import de.mosgrid.util.properties.ScalarProperty;
import de.mosgrid.util.validators.ScalarDataTypeValidator;
import de.mosgrid.util.validators.ScalarMinMaxValidator;
import de.mosgrid.util.validators.ScalarRestrictionValidator;
import de.mosgrid.util.validators.StringParameterValidator;

/**
 * The default implementation of a field factory.
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultFieldFactory implements IFieldFactory {
	/* constants */
	public static final String MANDATORY_DESCRIPTION = " This parameter is mandatory.";
	public static final String OPTIONAL_DESCRIPTION = " This parameter is optional and will be ignored if left emtpy.";
	// set width of select field to prevent select fields from growing with each selection (Vaadin bug?)
	public static final float DEFAULT_SELECT_WIDTH = 200f;

	@Override
	public Field createScalarField(ImportedWorkflow wkfImport, Job job, ScalarProperty scalarProperty)
			throws InputFieldCreationException {
		Field scalarField = null;

		if (scalarProperty.isRestricted() && !scalarProperty.isReadOnly()) {
			// If property is restricted to some values and is not 'readOnly', then create a selection-list
			scalarField = createScalarSelectionField(scalarProperty);
		} else {
			scalarField = createScalarTextField(scalarProperty);
		}
		createCaption(scalarProperty, scalarField);
		createDescription(scalarProperty, scalarField);

		return scalarField;
	}

	/**
	 * Adds a caption to field. The caption is built of the scalars title, unit symbols and min-max restrictions (if
	 * any).
	 */
	protected void createCaption(ScalarProperty scalarProperty, Field scalarField) {
		String caption = scalarProperty.getTitle();
		if (scalarProperty.hasUnit()) {
			caption += " (" + scalarProperty.getUnitSymbol() + ")";
		}

		if (scalarProperty.hasMinimum() || scalarProperty.hasMaximum()) {
			caption += " [" + scalarProperty.formattedMin() + "..." + scalarProperty.formattedMax() + "]";
		}

		scalarField.setCaption(caption);
	}

	/**
	 * Creates a tooltip which is shown at mouse-over events. The tooltip contains the description from the dictionary
	 * entry and an additional note if parameter is optional.
	 */
	protected void createDescription(ScalarProperty scalarProperty, Field scalarField) {
		StringBuilder descBuilder = new StringBuilder();
		if (scalarProperty.hasDescription()) {
			descBuilder.append(scalarProperty.getDescription());
		}
		if (scalarProperty.isOptional()) {
			descBuilder.append(OPTIONAL_DESCRIPTION);
		} else {
			descBuilder.append(MANDATORY_DESCRIPTION);
		}
		scalarField.setDescription(descBuilder.toString());
	}

	/**
	 * Creates a TextField for parameter input
	 */
	protected Field createScalarTextField(ScalarProperty scalarProperty) {
		// create textfield for manual input
		TextField textField = new TextField();
		// check if optional or required
		if (!scalarProperty.isOptional() && !scalarProperty.isReadOnly()) {
			textField.setRequired(true);
		}
		// set empty string as null value and allow setting them
		textField.setNullRepresentation("");
		textField.setNullSettingAllowed(true);

		textField.setPropertyDataSource(scalarProperty);

		if (scalarProperty.isReadOnly()) {
			textField.setReadOnly(true);
		} else {
			addTextFieldValidators(scalarProperty, textField);
		}

		return textField;
	}

	/**
	 * Adds all desired validators to TextFields.
	 */
	protected void addTextFieldValidators(ScalarProperty scalarProperty, TextField field) {
		field.addValidator(new ScalarDataTypeValidator(scalarProperty));

		if (scalarProperty.hasMinimum() || scalarProperty.hasMaximum()) {
			field.addValidator(new ScalarMinMaxValidator(scalarProperty));
		}

		if (scalarProperty.getType() == String.class) {
			field.addValidator(new StringParameterValidator(scalarProperty));
		}
	}

	/**
	 * Creates a Selection component for parameter input based on restrictions given in dictionaries
	 */
	protected Field createScalarSelectionField(ScalarProperty scalarProperty) {
		// create selection for valid values
		Map<String, String> restrictions = scalarProperty.getRestrictions();
		Select selectField = new Select();
		selectField.setWidth(DEFAULT_SELECT_WIDTH, Field.UNITS_PIXELS);
		// check if optional or required
		if (scalarProperty.isOptional()) {
			selectField.setNullSelectionAllowed(true);
		} else {
			selectField.setRequired(true);
			selectField.setNullSelectionAllowed(false);
		}
		selectField.addContainerProperty("iTitle", String.class, "");
		selectField.setItemCaptionPropertyId("iTitle");
		// set values but change their title which is shown
		for (String itemTitle : restrictions.keySet()) {
			Object itemValue = restrictions.get(itemTitle);
			final Item selectItem = selectField.addItem(itemValue);
			if (selectItem != null) {
				selectItem.getItemProperty("iTitle").setValue(itemTitle);
			}
		}
		selectField.setPropertyDataSource(scalarProperty);

		addSelectionValidators(scalarProperty, selectField);

		return selectField;
	}

	/**
	 * Adds all desired validators to Select fields
	 */
	protected void addSelectionValidators(ScalarProperty scalarProperty, Field select) {
		select.addValidator(new ScalarDataTypeValidator(scalarProperty));
		if (scalarProperty.isRestricted()) {
			select.addValidator(new ScalarRestrictionValidator(scalarProperty));
		}
	}

	@Override
	public IUploadComponent createUploadField(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws InputFieldCreationException {
		return new DefaultUploadSelectionComponent(wkfImport, job, uploadElement);
	}

}
