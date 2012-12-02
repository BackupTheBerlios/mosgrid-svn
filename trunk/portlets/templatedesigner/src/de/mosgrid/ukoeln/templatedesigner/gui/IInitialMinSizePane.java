package de.mosgrid.ukoeln.templatedesigner.gui;

import com.vaadin.ui.AbstractLayout;

public interface IInitialMinSizePane {

	/**
	 * This function must be implemented to set the size of the root-component
	 * and its layout together.
	 */
	public AbstractLayout getMainLayout();

}
