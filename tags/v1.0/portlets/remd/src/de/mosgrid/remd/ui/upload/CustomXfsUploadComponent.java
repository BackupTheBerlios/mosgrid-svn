package de.mosgrid.remd.ui.upload;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import de.mosgrid.gui.inputmask.uploads.XfsUploadComponent;
import de.mosgrid.gui.xfs.IXfsBrowserListener;
import de.mosgrid.gui.xfs.XfsFileBrowserWindow;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Custom XFS upload component with a special auto search button
 * 
 * @author Andreas Zink
 * 
 */
public class CustomXfsUploadComponent extends XfsUploadComponent {
	private static final long serialVersionUID = 2902242490881155235L;

	private final String CAPTION_BUTTON_AUTO_SELECT = "Auto Search";
	private final String TOOLTIP_BUTTON_AUTO_SELECT = "Detects matching files from previously submitted jobs.";

	private Button autoSelectButton;
	private SingleAutoSelectBrowser browser;

	public CustomXfsUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement,
			SingleAutoSelectBrowser browser) {
		super(wkfImport, job, uploadElement);
		this.browser=browser;
	}

	@Override
	protected void buildUploadComponent() {
		super.buildUploadComponent();

		autoSelectButton = new Button(CAPTION_BUTTON_AUTO_SELECT);
		autoSelectButton.setDescription(TOOLTIP_BUTTON_AUTO_SELECT);
		autoSelectButton.addListener((Button.ClickListener) this);

		uploadLayout.addComponent(autoSelectButton);
		uploadLayout.setComponentAlignment(autoSelectButton, Alignment.BOTTOM_LEFT);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (event.getButton() == autoSelectButton) {
			cancelledDownload = false;
			browserWindow = new XfsFileBrowserWindow("Please select a result directory");
			browserWindow.setBrowser(browser);
			browserWindow.addButtonListener((IXfsBrowserListener) this);
			getWindow().addWindow(browserWindow);
			
		} else {
			super.buttonClick(event);
		}
	}

}
