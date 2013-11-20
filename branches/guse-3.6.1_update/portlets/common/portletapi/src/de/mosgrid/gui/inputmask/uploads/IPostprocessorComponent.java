package de.mosgrid.gui.inputmask.uploads;

import com.vaadin.ui.Component;

import de.mosgrid.exceptions.PostprocessorValidationException;

/**
 * Additional ui component of an Postprocessor which gets added below the upload field
 * 
 * @author Andreas Zink
 * 
 */
public interface IPostprocessorComponent extends Component {

	/**
	 * Commit and validate user input
	 * 
	 * @return 'true' if user input is valid
	 * 
	 * @throws PostprocessorValidationException
	 *             May be thrown if errors occur while validation
	 */
	boolean isValid() throws PostprocessorValidationException;

}
