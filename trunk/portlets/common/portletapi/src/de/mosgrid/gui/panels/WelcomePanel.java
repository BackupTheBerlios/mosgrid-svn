package de.mosgrid.gui.panels;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * This Panel is shown to the user in the SubmissionTab. It shall contain a short welcome message and description of the
 * Portlet. Furthermore it can be extended by a footer component.
 * 
 * @author Andreas Zink
 * 
 */
public class WelcomePanel extends CustomComponent {
	private static final long serialVersionUID = 2062677739953750531L;
	public static final String CAPTION = "Welcome";

	private Panel mainPanel;
	private VerticalLayout mainLayout;
	private Label welcomeLabel;
	private Component footerComponent;

	public WelcomePanel() {
		init();
	}

	public WelcomePanel(String welcomeText) {
		init();
		setWelcomeText(welcomeText, Label.CONTENT_XHTML);
	}

	private void init() {
		buildWelcomePanel();
		setCompositionRoot(mainPanel);
	}

	/**
	 * Sets the welcome text and allows to define the content mode of the label. Use constants defined in Label class.
	 */
	public void setWelcomeText(String welcomeText, int contentMode) {
		welcomeLabel.setContentMode(contentMode);
		welcomeLabel.setValue(welcomeText);
	}

	/**
	 * Adds a footer component below the welcome text.
	 * 
	 * @param footer
	 *            Footer to be added
	 */
	public void setFooter(Component footer) {
		removeFooter();
		this.footerComponent = footer;
		mainLayout.addComponent(footer);
	}

	/**
	 * Removes the current footer if set
	 */
	public void removeFooter() {
		if (this.footerComponent != null) {
			mainLayout.removeComponent(this.footerComponent);
		}
	}

	private Panel buildWelcomePanel() {
		mainPanel = new Panel();
		mainPanel.setCaption(CAPTION);
		mainPanel.setImmediate(true);
		mainPanel.setWidth("100.0%");
		mainPanel.setHeight("-1px");

		setWidth("100.0%");
		setHeight("-1px");

		mainLayout = buildMainLayout();
		mainPanel.setContent(mainLayout);

		return mainPanel;
	}

	private VerticalLayout buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100.0%");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		buildWelcomeLabel();

		return mainLayout;
	}

	private void buildWelcomeLabel() {

		welcomeLabel = new Label();
		welcomeLabel.setImmediate(true);
		welcomeLabel.setWidth("100%");
		welcomeLabel.setHeight("-1px");
		mainLayout.addComponent(welcomeLabel);
	}

}
