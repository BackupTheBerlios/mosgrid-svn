package de.mosgrid.gui.tabs;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.portlet.AboutInfo;
import de.mosgrid.portlet.MoSGridPortlet;

/**
 * This element shall contain information about the Portlet e.g. Version, Developer, etc. Any HTML markup is allowed to
 * be used.
 * 
 * @author Andreas Zink
 * 
 */
public class AboutTab extends CustomComponent {
	private static final long serialVersionUID = -3349951217115472913L;
	public static final String CAPTION = "About";
	private static final String CAPTION_LABEL_VERSION = "<b>Version: </b>";
	private static final String CAPTION_LABEL_DEVELOPER = "<b>Developers: </b>";

	private MoSGridPortlet portlet;
	private VerticalLayout mainLayout;

	public AboutTab(MoSGridPortlet portlet) {
		this.portlet = portlet;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");

		setWidth("100%");
		setHeight("-1px");

		buildAboutLabels();

	}

	private void buildAboutLabels() {
		AboutInfo about = portlet.getAboutInfo();

		Label versionLabel = new Label(CAPTION_LABEL_VERSION + about.getVersion(), Label.CONTENT_XHTML);
		mainLayout.addComponent(versionLabel);

		Label developerLabel = new Label(CAPTION_LABEL_DEVELOPER + about.getDevelopers(), Label.CONTENT_XHTML);
		mainLayout.addComponent(developerLabel);

		String optText = about.getOptionalText();
		if (optText != null && !optText.equals("")) {
			Label additionalLabel = new Label(optText, Label.CONTENT_XHTML);
			mainLayout.addComponent(additionalLabel);
		}

	}
}
