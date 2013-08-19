package de.mosgrid.remd.ui.upload;

import java.io.IOException;

import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import com.vaadin.ui.Window.Notification;

import de.mosgrid.gui.xfs.IXfsBrowserListener;
import de.mosgrid.util.NotificationFactory;

/**
 * Special AutoSelectBrowser for auto search of single files.
 * 
 * @author Andreas Zink
 * 
 */
public class SingleAutoSelectBrowser extends AutoSelectBrowser {
	private static final long serialVersionUID = 2635949804925515384L;

	protected String _filename;

	public SingleAutoSelectBrowser(String jobId, String filename) {
		super(jobId);
		_filename = filename;
	}

	@Override
	protected void clickedOkButtonNotification(Object currentItemID, DirectoryEntry dirEntry, String fullFilePath,
			FileType fileType) {

		fullFilePath += "/0";
		// iterate over job dirs
		try {
			for (DirectoryEntry jobEntry : xfsBridge.listEntries(fullFilePath)) {
				String jobName = jobEntry.getName().trim();
				if (!jobName.startsWith(".")) {
					if (jobName.equals(_jobSearchId)) {
						AutoSearchFile file = find(_filename, fullFilePath + "/" + jobName);

						if (file!=null) {
							for (IXfsBrowserListener listener : listenerList) {
								listener.clickedFileBrowsersOkButton(currentItemID, file.getDirEntry(),
										file.getFullPath(), FileType.FILE);
							}
						}else{
							Notification notif = NotificationFactory.createFailedNotification(_filename+" could not be found!");
							getWindow().showNotification(notif);
						}
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recursive search for files
	 */
	private AutoSearchFile find(String filename, String parentPath) throws IOException {
		for (DirectoryEntry child : xfsBridge.listEntries(parentPath)) {
			if (!child.getName().startsWith(".")) {
				if (xfsBridge.isDirectory(child)) {
					return find(filename, parentPath + "/" + child.getName());
				} else {
					if (child.getName().trim().equals(filename)) {
						return new AutoSearchFile(parentPath + "/" + filename, child);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Helper class
	 * 
	 */
	private class AutoSearchFile {
		String fullPath;
		DirectoryEntry dirEntry;

		protected AutoSearchFile(String fullPath, DirectoryEntry dirEntry) {
			super();
			this.fullPath = fullPath;
			this.dirEntry = dirEntry;
		}

		public String getFullPath() {
			return fullPath;
		}

		public DirectoryEntry getDirEntry() {
			return dirEntry;
		}

	}
}
