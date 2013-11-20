package de.mosgrid.util.interfaces;

import de.mosgrid.exceptions.InputMaskCreationException;
import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Interface for InputMaskFactories.
 * 
 * @author Andreas Zink
 * 
 */
public interface IInputMaskFactory {

	/**
	 * Creates an InputMask for given workflow instance and MSML template.
	 */
	AbstractInputMask createInputMask(ImportedWorkflow wkfImport) throws InputMaskCreationException;
}
