package de.mosgrid.gui.xfs;

import com.vaadin.ui.Window;

/**
 * A window with a xfs filebrowser
 * 
 * @author Andreas Zink
 * 
 */
public class XfsFileBrowserWindow extends Window {
	private static final long serialVersionUID = -7335551548624509211L;
	private XfsFileBrowser browser;
	private String rootDir;

	public XfsFileBrowserWindow(String caption) {
		super(caption);
		init();
	}

	public XfsFileBrowserWindow(String caption, String rootDir) {
		super(caption);
		this.rootDir = rootDir;
		init();
	}

	private void init() {
		getContent().setSizeUndefined();
//		setWidth("50%");
//		setHeight(33f, UNITS_EM);

		browser = new XfsFileBrowser(rootDir);
		addComponent(browser);
	}

	@Override
	public void attach() {
		super.attach();
		center();
	}

	/**
	 * @return The XFS browser component
	 */
	public XfsFileBrowser getBrowser() {
		return browser;
	}

	public void setBrowser(XfsFileBrowser newBrowser) {
		removeComponent(this.browser);
		addComponent(newBrowser);
		this.browser = newBrowser;
	}

	/**
	 * Adds a file filter to the browser. If ok button is clicked and selected file is filtered, a warning will be shown
	 * and call is not delegated to listeners.
	 */
	public void addFileSelectFilter(IXfsFileFilter filter) {
		browser.addFileSelectFilter(filter);
	}

	/**
	 * Adds a listener for ok + cancel button events
	 */
	public void addButtonListener(IXfsBrowserListener listener) {
		browser.addButtonListener(listener);
	}

	/**
	 * Removes a listener for ok + cancel button events
	 */
	public void removeButtonListener(IXfsBrowserListener listener) {
		browser.removeButtonListener(listener);
	}

}
