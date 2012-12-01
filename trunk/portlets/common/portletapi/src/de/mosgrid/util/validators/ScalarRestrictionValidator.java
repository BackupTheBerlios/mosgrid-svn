package de.mosgrid.util.validators;

import com.vaadin.data.Validator;

import de.mosgrid.util.properties.ScalarProperty;

/**
 * Validates if the input fulfills the parameter restrictions. Usually this is trivial because the input will be given
 * in a ComboBox which only shows the restricted values.
 * 
 * @author Andreas Zink
 * 
 */
public class ScalarRestrictionValidator implements Validator {
	private static final long serialVersionUID = -412528762219310346L;
	private ScalarProperty property;

	public ScalarRestrictionValidator(ScalarProperty property) {
		this.property = property;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			StringBuilder validValues = new StringBuilder();
			for (String validValue : property.getRestrictions().values()) {
				validValues.append(validValue + " ");
			}
			throw new InvalidValueException("Invalid value! Value must be one of " + validValues.toString());
		}
	}

	@Override
	public boolean isValid(Object value) {
		boolean isValid = false;
		if (value != null && !value.equals("")) {
			if (property.isRestricted()) {
				if (property.getRestrictions().containsValue(value.toString())) {
					isValid = true;
				}
			} else {
				// if not restricted this is always true
				isValid = true;
			}
		} else if (property.isOptional()) {
			// value is null or empty but param is optional, thus valid
			isValid = true;
		}
		return isValid;
	}
}
