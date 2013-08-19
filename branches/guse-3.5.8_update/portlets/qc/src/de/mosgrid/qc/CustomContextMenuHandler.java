package de.mosgrid.qc;

import de.mosgrid.gui.tabs.monitoring.AbstractContextMenuHandler;
import de.mosgrid.qc.MonitoringProperties;

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
		
		getRawTextFormats().addAll(p.getRawTextFormats());
		getImageFormats().addAll(p.getImageFormats());
		
		getChemdoodleFormats().addAll(p.getChemdoodleFormats());
		getGraphFormats().addAll(p.getGraphFormats());
		getJmolFormats().addAll(p.getJmolFormats());
	}
	
}
