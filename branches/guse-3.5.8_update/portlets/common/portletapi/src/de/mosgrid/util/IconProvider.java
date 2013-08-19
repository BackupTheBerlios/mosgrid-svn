package de.mosgrid.util;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.ThemeResource;

/**
 * Provides GUI elements with icons TODO: iconsize, more icons if needed
 * 
 * @author Andreas Zink
 * 
 */
public class IconProvider {
	private static final Map<IconProvider.ICONS, ThemeResource> iconMap = new HashMap<IconProvider.ICONS, ThemeResource>();

	private static final String RUNO = "../runo/icons/16/";
	private static final String PORTLET_API = MosgridProperties.REL_CUSTOM_THEME_ICONS_PATH.getProperty();

	private IconProvider() {
		// no instance needed, make singleton if initialization needed
	}

	/**
	 * Gets the ThemeResource (Icon) for given Icon-ID
	 */
	public static synchronized ThemeResource getIcon(ICONS iconId) {
		if (!iconMap.containsKey(iconId)) {
			ThemeResource iconResource = new ThemeResource(iconId.getFileName());
			iconMap.put(iconId, iconResource);
		}
		return iconMap.get(iconId);
	}

	public enum ICONS {
		// TODO: add more if needed
		ARROW_RIGHT, ARROW_LEFT, ARROW_UP, ARROW_DOWN, CANCEL, HELP, OK, ATTENTION, RELOAD, LOCK, FOLDER, TRASH, USERS, GLOBE, DOCUMENT, GREEN_DOT, YELLOW_DOT, RED_DOT, GRAY_DOT,BLUE_DOT;

		public String getFileName() {
			switch (this) {
			case ARROW_RIGHT:
				return RUNO + "arrow-right.png";
			case ARROW_LEFT:
				return RUNO + "arrow-left.png";
			case ARROW_UP:
				return RUNO + "arrow-up.png";
			case ARROW_DOWN:
				return RUNO + "arrow-down.png";
			case CANCEL:
				return RUNO + "cancel.png";
			case ATTENTION:
				return RUNO + "attention.png";
			case OK:
				return RUNO + "ok.png";
			case RELOAD:
				return RUNO + "reload.png";
			case LOCK:
				return RUNO + "lock.png";
			case FOLDER:
				return RUNO + "folder.png";
			case TRASH:
				return RUNO + "trash.png";
			case USERS:
				return RUNO + "users.png";
			case GLOBE:
				return RUNO + "globe.png";
			case DOCUMENT:
				return RUNO + "document.png";
			case GREEN_DOT:
				return PORTLET_API + "succeeded.png";
			case YELLOW_DOT:
				return PORTLET_API + "running.png";
			case RED_DOT:
				return PORTLET_API + "failed.png";
			case GRAY_DOT:
				return PORTLET_API + "submitted.png";
			case BLUE_DOT:
				return PORTLET_API + "suspended.png";
			default:
				return RUNO + "help.png";
			}
		}
	}

}
