package de.mosgrid.portlet.listener;

import com.vaadin.terminal.ThemeResource;

/**
 * Listener for important events in MoSGrid portlets
 * 
 * @author Andreas Zink
 * 
 */
public interface IStatusMessageListener{
	/**
	 * Gets called whenever the status-message of the portlet changed
	 */
	void statusMessageChanged(String message, ThemeResource icon);
}
