package de.mosgrid.portlet;

/**
 * Gathers additional information on the portlet which will be represented in
 * the AboutTab. Any HTML markup is allowed to be used.
 * 
 * @author Andreas Zink
 * 
 */
public class AboutInfo {
	private String version;
	private String developers;
	private String optionalText;

	public AboutInfo(String version, String developers) {
		super();
		this.version = version;
		this.developers = developers;
	}

	public String getVersion() {
		return version;
	}

	public String getDevelopers() {
		return developers;
	}

	public String getOptionalText() {
		return optionalText;
	}

	public void setOptionalText(String additionalText) {
		this.optionalText = additionalText;
	}

}
