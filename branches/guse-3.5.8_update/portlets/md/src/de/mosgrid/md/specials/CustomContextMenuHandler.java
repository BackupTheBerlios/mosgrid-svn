package de.mosgrid.md.specials;

import de.mosgrid.gui.tabs.monitoring.AbstractContextMenuHandler;
import de.mosgrid.md.properties.MonitoringProperties;

/**
 * A custom context menu handler for the monitoring tab
 * 
 * @author Andreas Zink
 * 
 */
public class CustomContextMenuHandler extends AbstractContextMenuHandler {
	private static final long serialVersionUID = -8632834849094625440L;

	public CustomContextMenuHandler() {
		super();
		MonitoringProperties p = new MonitoringProperties();
		
		addRawTextFormats(p.getRawTextFormats());
		addImageFormats(p.getImageFormats());
		
		addChemdoodleFormats(p.getChemdoodleFormats());
		addGraphFormats(p.getGraphFormats());
		addJmolFormats(p.getJmolFormats());
	}
	
}
