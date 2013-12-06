package de.mosgrid.gui.inputmask.uploads;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.util.UploadCollector;

/**
 * Gives the opportunity to select an upload source. These can be added with the addSelectable() method. This component
 * will be created by DefaultInputmaskFactory. A custom InputmaskFactory may only create XFS or local upload-components.
 * This is possible because all fullfill the contract of IUploadComponent.
 * 
 * @author Andreas Zink
 * 
 */
public class UploadSelectionComponent extends CustomComponent implements ValueChangeListener, IUploadComponent {
	private static final long serialVersionUID = -4026800500854054506L;
	private String selectionCaption;
	private String selectionTooltip;

	// GUI Components
	private VerticalLayout mainLayout;
	private OptionGroup uploadSelect;
	private VerticalLayout uploadComponentContainer;

	private Map<String, Constructor<? extends IUploadComponent>> selectableComponentsMap;
	private Map<String, Object[]> constructorArgumentsMap;
	private List<IUploadListener> listenerList;
	private String firstItemsKey;
	private IUploadComponent lastSelected;

	public UploadSelectionComponent(String selectionCaption, String selectionTooltip) {
		this.selectionCaption = selectionCaption;
		this.selectionTooltip = selectionTooltip;
		this.selectableComponentsMap = new HashMap<String, Constructor<? extends IUploadComponent>>();
		this.constructorArgumentsMap = new HashMap<String, Object[]>();
		this.listenerList = new ArrayList<IUploadListener>();
		init();
		setCompositionRoot(mainLayout);
	}

	private void init() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setImmediate(true);

		buildSelect();

		uploadComponentContainer = new VerticalLayout();
		uploadComponentContainer.setImmediate(true);
		mainLayout.addComponent(uploadComponentContainer);
	}

	@Override
	public void attach() {
		uploadSelect.select(firstItemsKey);
		uploadSelect.setNullSelectionAllowed(false);
	}

	private void buildSelect() {
		uploadSelect = new OptionGroup(selectionCaption);
		uploadSelect.setDescription(selectionTooltip);
		uploadSelect.setRequired(true);
		uploadSelect.addStyleName("horizontal");
		uploadSelect.setImmediate(true);

		uploadSelect.addListener((ValueChangeListener) this);
		mainLayout.addComponent(uploadSelect);
	}

	/**
	 * Adds a selectable upload component
	 */
	public void addSelectable(String caption, Constructor<? extends IUploadComponent> componentConstructor, Object[] arguments) {
		if (firstItemsKey == null) {
			firstItemsKey = caption;
		}

		selectableComponentsMap.put(caption, componentConstructor);
		constructorArgumentsMap.put(caption, arguments);
		uploadSelect.addItem(caption);
	}

	/**
	 * Creates a new IUploadComponent for given key
	 */
	protected IUploadComponent createSelectable(String key) throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		IUploadComponent uploadComponent = null;
		Constructor<? extends IUploadComponent> constructor = selectableComponentsMap.get(key);
		Object[] args = constructorArgumentsMap.get(key);
		if (constructor != null && args != null) {
			uploadComponent = constructor.newInstance(args);
			for (IUploadListener l : listenerList) {
				uploadComponent.addUploadListener(l);
			}
		}
		return uploadComponent;
	}

	/**
	 * @return The currently selected upload component
	 */
	public IUploadComponent getSelected() {
		return lastSelected;
	}

	/**
	 * Add custom listeners for selection events
	 */
	public void addSelectionListener(ValueChangeListener listener) {
		uploadSelect.addListener(listener);
	}

	public void removeSelectionListener(ValueChangeListener listener) {
		uploadSelect.removeListener(listener);
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		uploadComponentContainer.removeAllComponents();
		try {
			lastSelected = createSelectable(uploadSelect.getValue().toString());
			uploadComponentContainer.addComponent(lastSelected);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid() {
		if (getSelected() != null && getSelected().isValid()) {
			// prevent further switch after successful validation
			uploadSelect.setEnabled(false);
			return true;
		}
		return false;
	}

	@Override
	public void startPostprocessing() throws PostprocessorException {
		getSelected().startPostprocessing();
	}

	@Override
	public void collectUploads(UploadCollector collector) {
		getSelected().collectUploads(collector);

	}

	@Override
	public File getUploadedFile() {
		return getSelected().getUploadedFile();
	}

	@Override
	public FileUpload getUploadElement() {
		return getSelected().getUploadElement();
	}

	@Override
	public void doTemplateIntegrations(JobInitialization initialization) throws MSMLConversionException {
		getSelected().doTemplateIntegrations(initialization);
	}

	@Override
	public void addUploadListener(IUploadListener listener) {
		listenerList.add(listener);
		if (lastSelected != null) {
			lastSelected.addUploadListener(listener);
		}
	}

	@Override
	public void removeUploadListener(IUploadListener listener) {
		listenerList.remove(listener);
		if (lastSelected != null) {
			lastSelected.removeUploadListener(listener);
		}
	}

}
