package de.mosgrid.util.validators;

import com.vaadin.data.Validator;

import de.mosgrid.util.properties.ScalarProperty;

/**
 * A validator which checks min-max bounds for parameters
 * 
 * @author Andreas Zink
 * 
 */
public class ScalarMinMaxValidator implements Validator {
	private static final long serialVersionUID = -6374351053046880679L;
	private ScalarProperty property;

	public ScalarMinMaxValidator(ScalarProperty property) {
		this.property = property;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			String minSign = property.isMinInclusive() ? "<=" : "<";
			String maxSign = property.isMaxInclusive() ? "<=" : "<";
			String minVal = property.formattedMin();
			String maxVal = property.formattedMax();
			throw new InvalidValueException("Invalid value! Value must be " + minVal + " " + minSign + " VALUE "
					+ maxSign + " " + maxVal);
		}
	}

	@Override
	public boolean isValid(Object value) {
		boolean isValid = true;
		if (value != null && !value.equals("")) {
			if (property.hasMinimum()) {
				if (property.isMinInclusive()) {
					isValid &= property.getMinimum().lesserEqualAs(value);
				} else {
					isValid &= property.getMinimum().lesserAs(value);
				}
			}
			if (isValid && property.hasMaximum()) {
				if (property.isMaxInclusive()) {
					isValid &= property.getMaximum().greaterEqualAs(value);
				} else {
					isValid &= property.getMaximum().greaterAs(value);
				}
			}
		} else if (property.isOptional()) {
			// value is null or empty but param is optional, thus valid
			isValid = true;
		}
		return isValid;
	}

}
