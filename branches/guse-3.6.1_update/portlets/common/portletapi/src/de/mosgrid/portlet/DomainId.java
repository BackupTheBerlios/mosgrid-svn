package de.mosgrid.portlet;

import de.mosgrid.msml.enums.DictDir;
import de.mosgrid.msml.enums.TemplateDir;
import de.mosgrid.msml.util.MSMLProperties;
import de.mosgrid.util.MosgridProperties;

/**
 * Enum for all available domains
 * 
 * @author Andreas Zink
 * 
 */
public enum DomainId {

	DOCKING("Docking", "DOC", 
			MSMLProperties.REL_DOCKING_PATH, MosgridProperties.DOCKING_HELP_URL), 
	QUANTUM_CHEMISTRY("Quantum Chemistry", "QC", 
			MSMLProperties.REL_QC_PATH, MosgridProperties.QC_HELP_URL),
	MOLECULAR_DYNAMICS("Molecular Dynamics", "MD", 
			MSMLProperties.REL_MD_PATH, MosgridProperties.MD_HELP_URL),
	REPLICA_EXCHANGE_MOLECULAR_DYNAMICS("Replica Exchange Molecular Dynamics", "REMD",
			MSMLProperties.REL_REMD_PATH, MosgridProperties.REMD_HELP_URL);

	private String _name;
	private String _abbrv;
	private DictDir _dictDir;
	private TemplateDir _templateDir;
	private MSMLProperties _msmlProp;
	private MosgridProperties _mosgridProp;

	private DomainId(String name, String abbreviation, MSMLProperties msmlProp, MosgridProperties mosgridProp) {
		_name = name;
		_abbrv = abbreviation;
		_msmlProp = msmlProp;
		_mosgridProp = mosgridProp;
		_dictDir = DictDir.getByMSMLProperty(_msmlProp);
		_templateDir = TemplateDir.getByMSMLProperty(_msmlProp);
	}

	/**
	 * @return Shall return a human readable name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return An abbreviation string
	 */
	public String getAbbreviation() {
		return _abbrv;
	}

	/**
	 * @return The domain specific template dir
	 */
	public TemplateDir getTemplateDir() {
		return _templateDir;
	}

	/**
	 * @return The domain specific dictionary dir
	 */
	public DictDir getDictDir() {
		return _dictDir;
	}

	/**
	 * @return The domain specific theme dir
	 */
	public String getThemeDir() {
		return _msmlProp.getProperty();
	}

	/**
	 * @return The domain specific help url
	 */
	public String getHelpUrl() {
		return _mosgridProp.getProperty();
	}
}
