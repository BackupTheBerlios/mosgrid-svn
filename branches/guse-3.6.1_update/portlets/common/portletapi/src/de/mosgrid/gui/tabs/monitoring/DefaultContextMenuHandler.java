package de.mosgrid.gui.tabs.monitoring;


/**
 * Default right-click context menu for items in the monitoring tab
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultContextMenuHandler extends AbstractContextMenuHandler{
	private static final long serialVersionUID = 935741950007533522L;

	public DefaultContextMenuHandler(MonitoringTab monitoringTab) {
		initialize(monitoringTab);
	}

	@Override
	public void initialize(MonitoringTab tab) {
		addRawTextFormats("txt", "jsdl");		
		addChemdoodleFormats("pdb");		
		addJmolFormats("pdb");		
		addGraphFormats("xvg");		
		addImageFormats("png", "jpg", "jpeg");
		
		super.initialize(tab);
	}
	
	

}
