package de.mosgrid.util;

import java.io.File;
import java.io.IOException;

/**
 * Helper class for creating temporary files. The File.createTempFile() method adds a random number suffix to the
 * filename which shall be removed from the filename in XFS storage. This helper class inserts a separator constant in
 * the filename in order to remove the auto created temp number suffix from the filename afterwards.
 * 
 * @author Andreas Zink
 * 
 */
public class TempFileHelper {
	private static final String DOT = ".";

	// separator between filename and tempfile timestamp
	public static final String TEMP_SUFFIX_SEPARATOR = "___";
	// files for parameter sweep need an index like FILENAME.TYPE_INDEX
	public static final String PARAMETER_SWEEP_SEPARATOR = "_";

	private TempFileHelper() {
		// No instance needed, make singleton if initialization needed
	}

	/**
	 * Creates a temporary file as 'filename+filetype+TEMP_SUFFIX_SEPARATOR+AUTO_NUMBER_SUFFIX.temp' e.g.
	 * 'protein.pdb___324354234.tmp' in the default temp file directory. Use the removeTempSuffix() method to obtain the
	 * real filename and delete the temp suffix.
	 * 
	 * @param filename
	 *            Name of new temp file
	 * @param filetype
	 *            Type of new temp file e.g. '.xml'
	 */
	public static synchronized File createTempFile(String filename, String filetype) throws IOException {
		if (!filetype.startsWith(DOT)) {
			filetype = DOT + filetype;
		}
		return createTempFile(filename + filetype);
	}

	/**
	 * Creates a temporary file as 'fullFilename+TEMP_SUFFIX_SEPARATOR+AUTO_NUMBER_SUFFIX.temp' e.g.
	 * 'protein.pdb___324354234.tmp' in the default temp file directory. Use the removeTempSuffix() method to obtain the
	 * real filename and delete the temp suffix.
	 * 
	 * @param fullFilename
	 *            The full filename inclusive filetype e.g. 'protein.pdb'
	 */
	public static synchronized File createTempFile(String fullFilename) throws IOException {
		return File.createTempFile(fullFilename + TEMP_SUFFIX_SEPARATOR, null);
	}

	/**
	 * @return The filename without temp number suffix.
	 */
	public static synchronized String removeTempSuffix(File tempfile) {
		String[] split = tempfile.getName().split(TEMP_SUFFIX_SEPARATOR);
		return split[0];
	}

	/**
	 * @return The filename without temp suffix and index. A file may have an index if it is used as input for parameter
	 *         sweep workflows
	 */
	public static synchronized String removeTempSuffixAndIndex(File tempfile) {
		String realFileName = removeTempSuffix(tempfile);
		String[] split = realFileName.split(PARAMETER_SWEEP_SEPARATOR);
		return split[0];
	}

}
