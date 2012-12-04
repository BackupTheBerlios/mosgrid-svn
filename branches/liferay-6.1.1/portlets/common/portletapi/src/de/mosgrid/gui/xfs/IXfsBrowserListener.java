package de.mosgrid.gui.xfs;

import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import de.mosgrid.gui.xfs.XfsFileBrowser.FileType;

/**
 * A listener for the ok and cancel button of the XFS browser
 * 
 * @author Andreas Zink
 * 
 */
public interface IXfsBrowserListener {

	/**
	 * Called if selection passes all file filters after ok was clicked
	 * 
	 * @param itemID
	 *            The id of selected item
	 * @param dirEntry
	 *            The selected file
	 * @param fullFilePath
	 *            Full XFS path
	 * @param type
	 *            Dir or File?
	 */
	void clickedFileBrowsersOkButton(Object itemID, DirectoryEntry dirEntry, String fullFilePath, FileType type);

	void clickedFileBrowserCancelButton();
}
