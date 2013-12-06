package de.mosgrid.portlet;

import com.vaadin.terminal.ThemeResource;

import de.mosgrid.util.IconProvider;
import de.mosgrid.util.IconProvider.ICONS;

/**
 * Some portlet status constants and their icons.
 * 
 * @author Andreas Zink
 * 
 */
public enum PortletStatus {
	SUCCEEDED, FAILED, ATTENTION, DONT_KNOW;

	public ThemeResource getIcon() {
		switch (this) {
		case SUCCEEDED:
			return IconProvider.getIcon(ICONS.OK);
		case FAILED:
			return IconProvider.getIcon(ICONS.CANCEL);
		case ATTENTION:
			return IconProvider.getIcon(ICONS.ATTENTION);
		default:
			return IconProvider.getIcon(ICONS.HELP);
		}

	}
}
