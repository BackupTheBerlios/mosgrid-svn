package de.mosgrid.qc.gui.inputmask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator;

import de.mosgrid.msml.converter.nonstandart.GaussianCMLConverter;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;

public class StructureValidator implements Validator {

	private static final long serialVersionUID = -5838358768698634818L;
	private static final Logger LOGGER = LoggerFactory.getLogger(StructureValidator.class);
	private MoleculeType _lastValidatedMolecules;

	@Override
	public void validate(Object value) throws InvalidValueException {
		_lastValidatedMolecules = null;
		LOGGER.trace("Structural validation: " + (value != null ? value.toString() : "<null>"));
		if (value == null || "".equals(value.toString().trim()))
			throw new InvalidValueException("An empty structure is not allowed.");
		
		MoleculeType molecules;
		try {
			
			molecules = GaussianCMLConverter.gaussian2CML(value.toString());
			
			if (molecules == null || molecules.getMolecule() == null ||
				molecules.getMolecule().size() == 0)
				throw new InvalidValueException("No molecules found.");
			for (MoleculeType molecule : molecules.getMolecule()) {
				if (molecule == null ||
					molecule.getAtomArray() == null || 
					molecule.getAtomArray().getAtomID() == null ||
					molecule.getAtomArray().getAtomID().size() == 0) {
					throw new InvalidValueException("No bonds or atoms found.");
				}		
			}	
		} catch (Exception e) {
			String message = "Not a valid SDF-structure: " + value.toString();
			LOGGER.warn(message);
			throw new InvalidValueException(message);
		}
		_lastValidatedMolecules = molecules;
		LOGGER.trace((value != null ? value.toString() : "<null>") + " validated correctly.");
	}

	@Override
	public boolean isValid(Object value) {
		try {
			validate(value);
		} catch (InvalidValueException e) {
			LOGGER.debug((value != null ? value.toString() : "<null>") + " is not a valid value: " + e.getMessage());
			return false;
		}
		return true;
	}

	public MoleculeType getLastValidatedMolecule() {
		return _lastValidatedMolecules;
	}
}

