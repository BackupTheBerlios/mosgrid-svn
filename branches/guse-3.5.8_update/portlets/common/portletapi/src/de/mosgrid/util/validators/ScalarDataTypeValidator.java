package de.mosgrid.util.validators;

import com.vaadin.data.Validator;

import de.mosgrid.util.properties.ScalarProperty;

/**
 * Validates if the user entered the correct datatype for a ScalarProperty
 * 
 * @author Andreas Zink
 * 
 */
public class ScalarDataTypeValidator implements Validator {
	private static final long serialVersionUID = 7119461773397748995L;

	private ScalarProperty property;

	public ScalarDataTypeValidator(ScalarProperty property) {
		this.property = property;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException("Invalid data type! Value must be of type "
					+ property.getType().getSimpleName());
		}
	}

	@Override
	public boolean isValid(Object value) {
		boolean isValid = false;
		if (value != null && !value.equals("")) {
			if (value.getClass() == property.getType()) {
				isValid = true;
			} else {
				// can be converted to correct type?
				try {
					if (property.getType() == Integer.class) {
						new Integer(value.toString());
					} else if (property.getType() == Double.class) {
						new Double(value.toString());
					}
					isValid = true;
				} catch (NumberFormatException e) {
					isValid = false;
				}
			}
		} else if (property.isOptional()) {
			//value is null or empty but param is optional, thus valid
			isValid=true;
		}
		return isValid;
	}

}
