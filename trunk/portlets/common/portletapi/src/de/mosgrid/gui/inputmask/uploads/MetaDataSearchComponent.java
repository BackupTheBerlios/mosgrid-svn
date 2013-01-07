package de.mosgrid.gui.inputmask.uploads;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;

import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.gui.xfs.XfsFileBrowserWindow;
import de.mosgrid.gui.xfs.XfsMetaDataSearchBrowser;
import de.mosgrid.metadatasearch.DefaultMetaDataSearcher;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.UploadCollector;

public class MetaDataSearchComponent extends CustomComponent implements IUploadComponent {
	private static final long serialVersionUID = -8575794844638325274L;

	private List<IUploadListener> listenerList;

	/* ui components */
	private HorizontalLayout mainLayout;

	public MetaDataSearchComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement) {
		listenerList = new ArrayList<IUploadListener>();
		buildMainLayout();
	}

	private void buildMainLayout() {
		mainLayout = new HorizontalLayout();
		Button searchBtn = new Button("Search");
		searchBtn.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -147260351942956704L;

			@Override
			public void buttonClick(ClickEvent event) {
				clickedSearchButton();

			}

		});
		mainLayout.addComponent(searchBtn);
		setCompositionRoot(mainLayout);
	}

	private void clickedSearchButton() {
		XfsFileBrowserWindow browserWindow = new XfsFileBrowserWindow("Search...");
		XfsMetaDataSearchBrowser browser = new XfsMetaDataSearchBrowser(new DefaultMetaDataSearcher());
		browserWindow.setBrowser(browser);

		getWindow().addWindow(browserWindow);
		browserWindow.center();
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startPostprocessing() throws PostprocessorException {
		// TODO Auto-generated method stub

	}

	@Override
	public void collectUploads(UploadCollector collector) {
		// TODO Auto-generated method stub

	}

	@Override
	public File getUploadedFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileUpload getUploadElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doTemplateIntegrations(JobInitialization initialization) throws MSMLConversionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUploadListener(IUploadListener listener) {
		listenerList.add(listener);

	}

	@Override
	public void removeUploadListener(IUploadListener listener) {
		listenerList.remove(listener);
	}

}
