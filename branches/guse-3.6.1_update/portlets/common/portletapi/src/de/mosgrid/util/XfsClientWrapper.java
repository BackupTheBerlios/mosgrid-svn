package de.mosgrid.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtreemfs.common.clients.Client;
import org.xtreemfs.foundation.TimeSync;

/**
 * Just a workaround wrapper class to close XFS client. XfsBridge should be turned to a singleton with ThreadLocal
 * pattern for user credentials etc.
 * 
 * @author Andreas Zink
 * 
 */
public class XfsClientWrapper {
	private final Logger LOGGER = LoggerFactory.getLogger(XfsClientWrapper.class);

	private Client client;

	public XfsClientWrapper(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return client;
	}

	@Override
	protected void finalize() throws Throwable {
		closeClientConnection();
		super.finalize();
	}

	private void closeClientConnection() {
		if (client != null) {
			LOGGER.info("Closing XtreemFS-Connection completely!");
			try {
				client.stop();
				client = null;

				// Time Synchronization is a Singleton - it has to be closed on its own
				TimeSync.close();
			} catch (Exception e) {
				LOGGER.error("Could not close XtreemFS connection!", e);
			}
		}
	}

}
