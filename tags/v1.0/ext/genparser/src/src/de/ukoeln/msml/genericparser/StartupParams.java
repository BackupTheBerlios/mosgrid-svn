package de.ukoeln.msml.genericparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StringH;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public final class StartupParams {
	/**
	 * 
	 */
	private String msmlFile = null;
	private String baseFolder = null;
	private String jobID = null;
	private List<String> unknownArgs = new ArrayList<String>();
	private boolean _useX = true;
	private String output = null;
	
	public StartupParams(String file, String folder, String job, String outputFile) {
		msmlFile = file;
		baseFolder = folder;
		jobID = job;
		output = outputFile;
	}
	
	public StartupParams(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("-f".equals(arg)) {
				msmlFile = args[++i];
			} else if ("-d".equals(arg)) {
				baseFolder = args[++i];
			} else if ("-j".equals(arg)) {
				jobID = args[++i];
			} else if ("-o".equals(arg)) {
				output  = args[++i];
			} else if ("-h".equals(arg)) {
				_useX = false;
			} else if ("--headless".equals(arg)) {
				_useX = false;
			} else {
				unknownArgs.add(args[i]);
			}
		}
		
		if (!StringH.isNullOrEmpty(getJobID()))
			_useX = false;
	}
	
	public boolean isValid() {
		if (unknownArgs.size() != 0) {
			for (String u : unknownArgs) {
				System.out.println("Unkown arg: " + u);
			}
			return false;
		}
		
		try {
			if (!StringH.isNullOrEmpty(jobID)) {
				if (StringH.isNullOrEmpty(msmlFile))
					throw new UnsupportedOperationException("Cannot use job without msmlFile.");
			}
			
			if (!useX()) {
				if (StringH.isNullOrEmpty(msmlFile))
					throw new UnsupportedOperationException("Cannot use headless mode without msmlFile.");
			}
				
			// put other restrictions here

			if (!StringH.isNullOrEmpty(msmlFile) && !new File(msmlFile).exists())
				throw new UnsupportedOperationException("MSML-File does not exist.");
			if (!StringH.isNullOrEmpty(baseFolder) && !new File(baseFolder).exists())
				throw new UnsupportedOperationException("Base-Folder does not exist.");
		} catch (UnsupportedOperationException e) {
			StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT, "Illegal parameter combination.");
		}
		return true;
	}
	
	public void setDefaults() {
		if (StringH.isNullOrEmpty(baseFolder))
			baseFolder = ".";
	}

	public String getBaseFolder() {
		return baseFolder;
	}
		
	public boolean useX() {
		return _useX;
	}

	public void checkValidity() {
		if (!isValid())
			printUsage();
	}
	
	private static void printUsage() {
		System.out.println("USAGE: genericparser [[-h] -f <FILE> | -f <FILE>] [-j <JOBID>] [-d <DIRECTORY>]");
		System.out.println("-f : MSML-File that contains the <JOBID> that has to be processed.");
		System.out.println("-j : JobID contained in the MSML-File. If specified only that job will be processed "+ 
				"otherwise all jobs will be. Specifying the job will run the parser in headless mode.");
		System.out.println("-d : Base-Directory where the parser looks for the output-files.");
		System.out.println("-o : Outputfile if you do not want to overwrite the file specified in -f.");
		System.out.println("-h --headless : Run parser without gui. This is the usual mode for grid-execution. " +
				"In case of headless-mode you need to specify a template-file.");
		System.exit(0);
	}

	public String getMsmlFile() {
		return msmlFile;
	}

	public String getJobID() {
		return jobID;
	}

	public String getOutput() {
		return output;
	}
}