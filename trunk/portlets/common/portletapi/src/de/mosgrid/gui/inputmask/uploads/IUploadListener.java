package de.mosgrid.gui.inputmask.uploads;

import java.io.BufferedReader;

/**
 * Listens for arrived uploads from the input mask
 * 
 * @author Andreas Zink
 * 
 */
public interface IUploadListener {

	void fileArrived(IUploadComponent component, BufferedReader fileReader);
}
