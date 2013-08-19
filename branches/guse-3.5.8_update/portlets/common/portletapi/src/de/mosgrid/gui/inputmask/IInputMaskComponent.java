package de.mosgrid.gui.inputmask;

import com.vaadin.ui.Component;

import de.mosgrid.util.UploadCollector;

/**
 * Defines a component which can be added to the DefaultInputMask
 * 
 * @author Andreas Zink
 * 
 */
public interface IInputMaskComponent extends Component {

	/**
	 * Commit user input to data sources (e.g. ScalarProperty) and validate
	 * 
	 * @return 'true' if all input is valid
	 */
	boolean commitAndValidate();
	
	/**
	 * Hook in submission after all contents have been committed and validated.
	 */
	void afterCommitAndValidate(AbstractInputMask parent);

	/**
	 * Hook in submission procedure.
	 */
	void beforeSubmit(AbstractInputMask parent);

	/**
	 * Hook in remove procedure for cleanup
	 */
	void beforeRemove(AbstractInputMask parent);

	/**
	 * Shall collect all uploads
	 */
	void collectUploads(UploadCollector collector);

	
}
