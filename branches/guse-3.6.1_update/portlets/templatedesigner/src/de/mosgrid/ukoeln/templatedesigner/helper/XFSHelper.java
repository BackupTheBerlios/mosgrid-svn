package de.mosgrid.ukoeln.templatedesigner.helper;

import java.io.IOException;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.common.clients.Volume;
import org.xtreemfs.pbrpc.generatedinterfaces.MRC.DirectoryEntry;

import de.mosgrid.exceptions.PortletInitializationException;
import de.mosgrid.util.XfsBridge;

public class XFSHelper {

	private final Logger LOGGER = LoggerFactory.getLogger(XFSHelper.class);
	private final Volume _vol;

	public XFSHelper(PortletRequest request) {
		XfsBridge xfs = null;
		Volume vol = null;
		try {
			LOGGER.debug("Initializing XFS bridge.");
			xfs = new XfsBridge(request);
			vol = xfs.getVolume();
		} catch (PortletInitializationException e) {
			LOGGER.error("Could not initialize XFS: " + e.getMessage() + "\n"
					+ StackTraceHelper.getTrace(e));
			_vol = null;
			return;
		}
		_vol = vol;
	}
	
	public void dumpTestEntries() {
		try {
			for (DirectoryEntry entry : _vol.listEntries("test/")) {
				LOGGER.info(entry.getName());
			}
		} catch (IOException e) {
			LOGGER.error("Could not get listing.");
		}		

	}
}
