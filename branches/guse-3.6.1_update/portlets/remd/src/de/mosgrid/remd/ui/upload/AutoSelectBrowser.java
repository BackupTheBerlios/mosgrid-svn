package de.mosgrid.remd.ui.upload;

import java.io.IOException;

import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import de.mosgrid.gui.xfs.IXfsFileFilter;
import de.mosgrid.gui.xfs.XfsFileBrowser;

/**
 * A browser which lets the user only select result directories which contain the given job-id
 * 
 * @author Andreas Zink
 * 
 */
public class AutoSelectBrowser extends XfsFileBrowser {
	private static final long serialVersionUID = 5082562884681270712L;

	protected String _jobSearchId;

	protected AutoSelectBrowser(String jobSearchId) {
		super();
		_jobSearchId = jobSearchId;
		init();
	}

	private void init() {
		addFileSelectFilter(new IXfsFileFilter() {

			@Override
			public boolean acceptFile(Object itemID, DirectoryEntry dirEntry, String fullFilename, FileType type) {
				// only root selection only
				if (type == FileType.DIR && contentTable.isRoot(itemID)) {
					return true;
				}
				return false;
			}

			@Override
			public String getPattern() {
				return "Only root directories";
			}
		});
	}

	@Override
	protected void fillContentTable() {
		rootDir = xfsBridge.getResultsDir();
		try {
			for (DirectoryEntry dirEntry : xfsBridge.listEntries(rootDir)) {
				if (checkDirectory(dirEntry)) {
					addItemToContentTable(dirEntry, rootDir, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Only adds result directories which contain results of an REMD preparation workflow
	 */
	protected boolean checkDirectory(DirectoryEntry dirEntry) throws IOException {
		if (!dirEntry.getName().startsWith(".")) {
			
			String path = rootDir + (rootDir.endsWith("/")?"":"/") + dirEntry.getName() + "/0";
			// iterate over job dirs
			for (DirectoryEntry jobEntry : xfsBridge.listEntries(path)) {
				String jobName = jobEntry.getName().trim();
				if (!jobName.startsWith(".")) {
					if (jobName.equals(_jobSearchId)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
