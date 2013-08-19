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
		getRawTextFormats().add("txt");
		getRawTextFormats().add("jsdl");
		
		getChemdoodleFormats().add("pdb");
		
		getJmolFormats().add("pdb");
		
		getGraphFormats().add("xvg");
		
		getImageFormats().add("png");
		getImageFormats().add("jpg");
		getImageFormats().add("jpeg");
		
		super.initialize(tab);
	}
	
	

}
