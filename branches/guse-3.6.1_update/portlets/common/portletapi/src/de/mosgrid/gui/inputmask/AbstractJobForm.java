package de.mosgrid.gui.inputmask;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.msml.util.wrapper.Job;

/**
 * This Component shall gather all input fields for one Job. It uses a Form as main content container in order to have
 * an underlined caption, but this is just a workaround. Consists of two columns which are component containers.
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AbstractJobForm extends CustomComponent implements IInputMaskComponent {
	private static final long serialVersionUID = 6992167387142605982L;

	private Job job;
	private float leftColumnExpandRatio = 0.5f;

	/* ui components */
	// the main component is a form in order to get these horizontal lines (workaround)
	private Form mainComponent;
	// main layout which is broken up in two columns
	private HorizontalLayout mainLayout;
	// left column of main layout with fixed width
	private ComponentContainer leftColumnLayout;
	// right column of main layout with fixed width
	private ComponentContainer rightColumnLayout;

	public AbstractJobForm(Job job) {
		this.job = job;
		buildMainPanel();
		setCompositionRoot(mainComponent);
	}

	private void buildMainPanel() {
		mainComponent = new Form();
		mainComponent.setCaption(job.getTitle());
		mainComponent.setWidth("100%");
		mainComponent.setHeight("-1px");

		buildMainLayout();
	}

	private void buildMainLayout() {
		mainLayout = new HorizontalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setSpacing(true);
		// create margin at top and bottom
		mainLayout.setMargin(true, false, true, false);
		mainLayout.setImmediate(true);

		this.mainComponent.setLayout(mainLayout);
		buildLeftColumnLayout();
		buildRightColumnLayout();
	}

	private void buildLeftColumnLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("-1px");
		layout.setSpacing(true);
		layout.setImmediate(true);
		leftColumnLayout = layout;

		mainLayout.addComponent(leftColumnLayout);
		mainLayout.setExpandRatio(leftColumnLayout, leftColumnExpandRatio);
	}

	private void buildRightColumnLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth("100%");
		layout.setHeight("-1px");
		layout.setSpacing(true);
		layout.setImmediate(true);
		rightColumnLayout = layout;

		mainLayout.addComponent(rightColumnLayout);
		mainLayout.setExpandRatio(rightColumnLayout, 1f - leftColumnExpandRatio);
	}

	/**
	 * Sets the caption of the job form
	 */
	@Override
	public void setCaption(String caption) {
		this.mainComponent.setCaption(caption);
	}

	/**
	 * Adds a component. By default just adds it to the left column
	 */
	@Override
	public void addComponent(Component c) {
		if (c != null) {
			this.leftColumnLayout.addComponent(c);
		}
	}

	/**
	 * Adds a component to left column
	 */
	public void addComponentToLeftColumn(Component c) {
		if (c != null) {
			this.leftColumnLayout.addComponent(c);
		}
	}

	@Override
	public void removeComponent(Component c) {
		if (c != null) {
			this.leftColumnLayout.removeComponent(c);
		}
	}

	public void removeComponentFromLeftColumn(Component c) {
		if (c != null) {
			this.leftColumnLayout.removeComponent(c);
		}
	}

	/**
	 * Adds a component to right column
	 */
	public void addComponentToRightColumn(Component c) {
		if (c != null) {
			this.rightColumnLayout.addComponent(c);
		}
	}

	public void removeComponentFromRightColumn(Component c) {
		if (c != null) {
			this.rightColumnLayout.removeComponent(c);
		}
	}

	/**
	 * @return The left column of the job form (VerticalLayout by default)
	 */
	protected ComponentContainer getLeftComponentContainer() {
		return leftColumnLayout;
	}

	/**
	 * Sets a new component container for the left column
	 */
	protected void setLeftComponentContainer(ComponentContainer newContainer) {
		if (newContainer != null) {
			mainLayout.removeComponent(leftColumnLayout);
			mainLayout.addComponentAsFirst(newContainer);
			mainLayout.setExpandRatio(newContainer, leftColumnExpandRatio);
			this.leftColumnLayout = newContainer;
		}
	}

	/**
	 * @return The right column of the job form (VerticalLayout by default)
	 */
	protected ComponentContainer getRightComponentContainer() {
		return rightColumnLayout;
	}

	/**
	 * Sets a new component container for the right column
	 */
	protected void setRightComponentContainer(ComponentContainer newContainer) {
		if (newContainer != null) {
			mainLayout.removeComponent(rightColumnLayout);
			mainLayout.addComponent(newContainer);
			mainLayout.setExpandRatio(newContainer, 1f - leftColumnExpandRatio);
			this.rightColumnLayout = newContainer;
		}
	}

	/**
	 * Sets the expand ratios (width) of the two columns. By default 0.5
	 * 
	 * @param value
	 *            Expand ratio of the left column. Must be between 0 and 1 (inclusive). The ratio of the right column is
	 *            always 1-value
	 */
	public void setExpandRatio(float value) {
		if (value >= 0 && value <= 1) {
			this.leftColumnExpandRatio = value;
			mainLayout.setExpandRatio(leftColumnLayout, value);
			mainLayout.setExpandRatio(rightColumnLayout, 1f - value);
		}
	}

	/**
	 * @return The expand ratio of the left column.
	 */
	public float getExpandRatio() {
		return leftColumnExpandRatio;
	}

	/**
	 * @return The corresponding Job for this input-form
	 */
	public Job getJob() {
		return job;
	}
}
