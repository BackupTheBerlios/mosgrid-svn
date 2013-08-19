package de.mosgrid.ukoeln.templatedesigner.gui;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.DefaultConfirmDialogFactory;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.mosgrid.ukoeln.templatedesigner.TDDocumentManager;
import de.mosgrid.ukoeln.templatedesigner.gui.listener.BoolCDListener;
import de.mosgrid.ukoeln.templatedesigner.gui.templatetab.TDTemplateTab;

public class TDMainWindow extends Window {

	private static final long serialVersionUID = 2778528807980547511L;
	private VerticalLayout mainLayout;
	private TDMainView tdMainView;
	private TDDocumentManager _man;
	public static final String MAINVIEW_HEIGHT = "500px";
	public static final DefaultConfirmDialogFactory _fac = new DefaultConfirmDialogFactory();

	public TDMainWindow() {
		super("Template Designer");
		init();
	}

	private void init() {
		buildMainLayout();
		setContent(mainLayout);

		mainLayout.setHeight(MAINVIEW_HEIGHT);
		setHeight(MAINVIEW_HEIGHT);

	}

	public DefaultConfirmDialogFactory getConfirmDialogFactory() {
		return _fac;
	}

	public boolean showYesNoDialog(String caption, String message) {
		BoolCDListener boolListener = new BoolCDListener();
		ConfirmDialog.show(this, caption, message, "Yes", "No", boolListener);
		return boolListener.getConfirmed();
	}
	
	public void setMainView(TDMainView mainView) {
		removeComponent(tdMainView);
		setContent(mainView);
		tdMainView = mainView;
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100px");
		setWidth("100%");
		setHeight("100px");

		tdMainView = new TDMainView();
		mainLayout.addComponent(tdMainView);
	}

	public void setManager(TDDocumentManager man) {
		_man = man;
	}

	public TDDocumentManager getManager() {
		return _man;
	}

	public void refreshMainTab() {
		tdMainView.refreshMainTab();
	}

	public void removeTab(TDTemplateTab view) {
		tdMainView.removeTab(view);
	}

}
