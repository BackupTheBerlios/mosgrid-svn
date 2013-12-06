package de.mosgrid.md.specials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator;
import com.vaadin.ui.Field;

/**
 * Walltime (min) must not be smaller than maxh*0.99*60
 * 
 * @author Andreas Zink
 * 
 */
public class WalltimeValidator implements Validator {
	private static final long serialVersionUID = 703220975053346555L;
	private final Logger LOGGER = LoggerFactory.getLogger(WalltimeValidator.class);
	private Field maxhField;

	protected WalltimeValidator(Field maxhField) {
		this.maxhField = maxhField;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		if (!isValid(value)) {
			throw new InvalidValueException("The walltime must not be smaller than the maximal runtime (maxh).");
		}
	}

	@Override
	public boolean isValid(Object value) {
		try {
			Double maxh = getMaxH();
			if (maxh != null) {
				Double walltime = Double.valueOf(value.toString());
				if (walltime < maxh * 0.99d * 60d) {
					return false;
				}
			} 
		} catch (Exception e) {
			LOGGER.warn("Error while validating walltime, but validation will pass. "+e.getMessage());
		}
		return true;
	}

	private Double getMaxH() {
		if (maxhField != null) {
			Object maxhValueObject = maxhField.getValue();
			if (maxhValueObject != null) {
				return Double.valueOf(maxhValueObject.toString());
			}
		}
		return null;
	}

}
