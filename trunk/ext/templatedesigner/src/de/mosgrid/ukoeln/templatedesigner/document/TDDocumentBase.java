package de.mosgrid.ukoeln.templatedesigner.document;

import de.mosgrid.portlet.MosgridUser;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainWindow;
import de.mosgrid.ukoeln.templatedesigner.gui.TDViewBaseNonGeneric;

public abstract class TDDocumentBase<T extends TDViewBaseNonGeneric> extends TDDocumentBaseNonGeneric {

	private T _view;
	private MosgridUser _user;
	private TDMainWindow _mainWindow;

	public TDDocumentBase(TDMainWindow mainWindow) {
		_mainWindow = mainWindow;
	}

	@SuppressWarnings("unchecked")
	public void init(TDViewBaseNonGeneric view, MosgridUser user) {
		_view = (T) view;
		_user = user;
		doInit();
	}

	abstract void doInit();

	public T getview() {
		return _view;
	}

	MosgridUser getUser() {
		return _user;
	}

	TDMainWindow getMainWindow() {
		return _mainWindow;
	}

}
