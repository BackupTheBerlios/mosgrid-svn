package de.mosgrid.ukoeln.templatedesigner.gui.listener;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.ConfirmDialog.Listener;

public class BoolCDListener implements Listener {

	private static final long serialVersionUID = 1L;
	boolean confirmed = false;

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed())
			confirmed = true;
	}

	public boolean getConfirmed() {
		return confirmed;
	}

}
