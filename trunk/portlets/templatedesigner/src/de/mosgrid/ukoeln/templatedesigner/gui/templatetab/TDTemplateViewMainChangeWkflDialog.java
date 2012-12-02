package de.mosgrid.ukoeln.templatedesigner.gui.templatetab;

import com.vaadin.ui.Window;

import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateMainDocument;
import de.mosgrid.ukoeln.templatedesigner.gui.TDMainWindow;


public class TDTemplateViewMainChangeWkflDialog extends Window
	implements Window.CloseListener {

	private static final long serialVersionUID = 1L;
	private TDTemplateViewMainChangeDialogComponent content;
	private Window _mainWindow;
	
	public TDTemplateViewMainChangeWkflDialog(Window mainWindow, TDTemplateMainDocument doc) {
		content = new TDTemplateViewMainChangeDialogComponent(doc);
		content.registerCloseListener(this);
		setContent(content);
		setCaption("Change workflow");
		_mainWindow = mainWindow;
	}
	
	@Override
	public void windowClose(CloseEvent e) {
		_mainWindow.removeWindow(this);
	}

	public static void show(TDMainWindow mainWindow, TDTemplateMainDocument doc) {
		TDTemplateViewMainChangeWkflDialog window = new TDTemplateViewMainChangeWkflDialog(mainWindow, doc);
		mainWindow.addWindow(window);
		window.center();
	}
}
