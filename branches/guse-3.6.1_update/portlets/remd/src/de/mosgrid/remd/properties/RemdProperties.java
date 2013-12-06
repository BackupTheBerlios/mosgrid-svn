package de.mosgrid.remd.properties;

import java.io.BufferedInputStream;
import java.util.Properties;

public class RemdProperties {
	private static Properties properties;
	
	/* keys*/
	public static final String ID_REMD_INPUT = "ID_REMD_INPUT";
	public static final String ID_REMD_PREP_INPUT = "ID_REMD_PREP_INPUT";
	public static final String ID_REMD_TEMP_DIST = "ID_REMD_TEMP_DIST";
	public static final String ID_REMD_APPEND_INPUT = "ID_REMD_APPEND_INPUT";
	public static final String ID_REMD_DEMUX_INPUT = "ID_REMD_DEMUX_INPUT";
	
	
	//protein prep workflow
	public static final String PROT_PREP_JOB_ID = "PROT_PREP_JOB_ID";
	public static final String PROT_PREP_FILE_NAME = "PROT_PREP_FILE_NAME";
	//remd prep workflow
	public static final String REMD_PREP_PDB_JOB_ID = "REMD_PREP_PDB_JOB_ID";
	public static final String REMD_PREP_PDB_FILE_NAME = "REMD_PREP_PDB_FILE_NAME";
	public static final String REMD_PREP_TOPOL_JOB_ID = "REMD_PREP_TOPOL_JOB_ID";
	public static final String REMD_PREP_TOPOL_FILE_NAME = "REMD_PREP_TOPOL_FILE_NAME";
	public static final String REMD_PREP_POSRES_JOB_ID = "REMD_PREP_POSRES_JOB_ID";
	public static final String REMD_PREP_POSRES_FILE_NAME = "REMD_PREP_POSRES_FILE_NAME";
	//remd workflows
	public static final String REMD_MAIN_JOB_ID = "REMD_MAIN_JOB_ID";
	public static final String REMD_PRECOMPILER1 = "REMD_PRECOMPILER1";
	public static final String REMD_PRECOMPILER1_MDP_PORT ="REMD_PRECOMPILER1_MDP_PORT";
	public static final String REMD_PRECOMPILER2 = "REMD_PRECOMPILER2";
	public static final String REMD_PRECOMPILER2_MDP_PORT ="REMD_PRECOMPILER2_MDP_PORT";
	public static final String REMD_TPR_NAME ="REMD_TPR_NAME";
	public static final String REMD_DEFNM ="REMD_DEFNM";
	//dict entries
	public static final String DICT_ENTRY_TEMP = "DICT_ENTRY_TEMP";
	public static final String DICT_ENTRY_MULTI = "DICT_ENTRY_MULTI";
	

	
	
	static {
		properties = new Properties();

		try {
			BufferedInputStream bis = new BufferedInputStream(RemdProperties.class.getResourceAsStream("remd.properties"));
			properties.load(bis);
			bis.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private RemdProperties() {
		super();
	}

	public static String get(String key) {
		return properties.getProperty(key);
	}
}
