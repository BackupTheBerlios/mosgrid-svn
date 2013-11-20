package de.mosgrid.gui;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;

import de.mosgrid.util.NotificationFactory;

/**
 * A default component for workflow descriptions which are shown at the import tab. Displays a textual description and a
 * button to open an image of the workflow graph.
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultWorkflowDescription extends CustomComponent {
	private static final long serialVersionUID = -4614144863322419192L;
	private static final String CAPTION = "Description";
	private static final String CAPTION_GRAPH_BUTTON = "See workflow graph...";
	private static final String CAPTION_GRAPH_WINDOW = "Workflow graph";

	private VerticalLayout mainLayout;
	private String workflowGraph;

	/**
	 * @param description
	 *            The textual description of the workflow in XHTML format
	 * @param workflowGraph
	 *            Path to an workflow graph image or null
	 */
	public DefaultWorkflowDescription(String description, String workflowGraph) {
		this.workflowGraph = workflowGraph;
		init(description);
		setCompositionRoot(mainLayout);
	}

	private void init(String description) {
		// at least one of both should not be null to carry a caption
		if ((description != null && description.length() > 0) || workflowGraph != null) {
			this.setCaption(CAPTION);
		}
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);

		if (description != null) {
			Label label = new Label(description);
			label.setContentMode(Label.CONTENT_XHTML);
			mainLayout.addComponent(label);
		}
		if (this.workflowGraph != null) {
			Button graphButton = new Button(CAPTION_GRAPH_BUTTON);
			graphButton.setStyleName(BaseTheme.BUTTON_LINK);
			graphButton.addListener(new Button.ClickListener() {
				private static final long serialVersionUID = -9068651280970775394L;

				@Override
				public void buttonClick(ClickEvent event) {
					showWorkflowGraph();
				}
			});
			mainLayout.addComponent(graphButton);
		}
	}

	/**
	 * Creates and loades the workflow graph if button was clicked. Shows a notification if fails
	 */
	private void showWorkflowGraph() {
		Window mainWindow = this.getWindow();
		try {
			Embedded workflowImage = new Embedded();
			workflowImage.setType(Embedded.TYPE_IMAGE);
			ThemeResource imageResource = new ThemeResource(this.workflowGraph);
			workflowImage.setSource(imageResource);
			//workflowImage.setSizeUndefined();

			Window subWindow = new Window(CAPTION_GRAPH_WINDOW);
			subWindow.getContent().setSizeUndefined();
			subWindow.addComponent(workflowImage);

			mainWindow.addWindow(subWindow);
			subWindow.center();
		} catch (Exception e) {
			Notification notif = NotificationFactory.createWarningNotification("Failed!",
					"Could not load workflow graph...");
			mainWindow.showNotification(notif);
		}
	}

}
