package de.mosgrid.ukoeln.templatedesigner.gui.templatetab;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateJobDocument;
import de.mosgrid.ukoeln.templatedesigner.gui.TDViewBase;

public class TDJobPartMain extends TDViewBase<TDTemplateJobDocument> {

	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private TextArea txtaDescription;

	@AutoGenerated
	private ComboBox cmbJob;

	private static final long serialVersionUID = 3844597982430879125L;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public TDJobPartMain() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	@Override
	public AbstractLayout getMainLayout() {
		return mainLayout;
	}

	@Override
	protected void doInit() {
		cmbJob.setNullSelectionAllowed(false);
		
		txtaDescription.setPropertyDataSource(getDoc().getDescriptionDataSource());
		updateUnassignedJob();
	}

	public void updateUnassignedJob() {
		BeanItemContainer<String> unassignedJobs = getDoc().getUnassignedJobsDataSource();
		if (unassignedJobs.size() == 0) {
			BeanItemContainer<String> job = new BeanItemContainer<String>(String.class);
			job.addBean(getDoc().getJobName());
			cmbJob.setContainerDataSource(job);
//			cmbJob.setValue(job.getIdByIndex(0));
			cmbJob.setEnabled(false);
		} else {
			cmbJob.setPropertyDataSource(null);
			cmbJob.setContainerDataSource(unassignedJobs);
			cmbJob.setEnabled(true);
			cmbJob.addListener(new ValueChangeListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					getDoc().changeToJob((String) cmbJob.getValue());
					cmbJob.setValue(null);													
				}
			});
		}
	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// cmbJob
		cmbJob = new ComboBox();
		cmbJob.setCaption("Change Job:");
		cmbJob.setImmediate(true);
		cmbJob.setWidth("-1px");
		cmbJob.setHeight("-1px");
		mainLayout.addComponent(cmbJob);
		
		// txtaDescription
		txtaDescription = new TextArea();
		txtaDescription.setCaption("Description (plain text / no HTML)");
		txtaDescription.setImmediate(false);
		txtaDescription.setWidth("100.0%");
		txtaDescription.setHeight("100.0%");
		mainLayout.addComponent(txtaDescription);
		mainLayout.setComponentAlignment(txtaDescription, new Alignment(33));
		
		return mainLayout;
	}
}
