package de.mosgrid.gui.xfs;

import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import de.mosgrid.gui.xfs.XfsFileBrowser.FileType;

/**
 * A filter for XFS browser
 * 
 * @author Andreas Zink
 * 
 */
public interface IXfsFileFilter {

	/**
	 * @param itemID
	 *            The selected tree item
	 * @param dirEntry
	 *            The selected xfs file
	 * @param fullFilename
	 *            The full xfs path
	 * @param type
	 *            Directory or File?
	 * @return 'true' if selection is accepted
	 */
	boolean acceptFile(Object itemID, DirectoryEntry dirEntry, String fullFilename, FileType type);

	/**
	 * @return A human readable text which describes filtering e.g. "Files only". This will be shown to the user if
	 *         filter does not accept selection.
	 */
	String getPattern();
}
