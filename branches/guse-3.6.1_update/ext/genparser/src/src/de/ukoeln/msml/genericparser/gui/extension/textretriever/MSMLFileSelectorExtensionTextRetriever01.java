package de.ukoeln.msml.genericparser.gui.extension.textretriever;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import de.mosgrid.msml.editors.JobListEditor;
import de.mosgrid.msml.jaxb.bindings.ModuleType;
import de.mosgrid.msml.util.wrapper.Job;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;
import de.ukoeln.msml.genericparser.classes.visitors.MSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFileSelectorExtension;
import de.ukoeln.msml.genericparser.gui.extension.MSMLFileSelectorExtensionTag;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionTextRetriever;
import de.ukoeln.msml.genericparser.gui.extension.interfaces.IMSMLExtensionVisitor;
import de.ukoeln.msml.genericparser.worker.FileMatchWalker;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;
import de.ukoeln.msml.genericparser.worker.StringH;

public class MSMLFileSelectorExtensionTextRetriever01 implements IMSMLExtensionTextRetriever {

	@Override
	public void getTextToConfig(IMSMLExtensionVisitor val, HashMap<String, String> config) {
		String filepattern = config.get(MSMLFileSelectorExtension.FILEPATTERN);
		String jobPattern = config.get(MSMLFileSelectorExtension.JOBPATTERN);
		boolean isMSML = Boolean.parseBoolean(config.get(MSMLFileSelectorExtension.ISMSML));
		boolean useParentsText = Boolean.parseBoolean(config.get(MSMLFileSelectorExtension.USEPARENTSTEXT));
		if (useParentsText) {
			val.setText(val.getMSMLTreeValue().getParentsText());
			return;
		}

		if (StringH.isNullOrEmpty(filepattern))
			return;
		
		String text = getTextFromFile(filepattern);
		
		if (isMSML)
			text = extractMSMLJob(jobPattern, text);
		
		val.setText(text);
	}

	private String extractMSMLJob(String jobPattern, String text) {
		Pattern p = Pattern.compile(jobPattern);
		JobListEditor ed = new JobListEditor(text);
		Job foundJob = null;
		for (Job job : ed.getJobListElement().getJobs()) {
			Matcher m = p.matcher(job.getId());
			if (m.find()) {
				if (foundJob != null)
					StackTraceHelper.handleException(
							new Throwable("Did not find exactly one job with pattern " + jobPattern +
									" to retrieve text from MSML."), ON_EXCEPTION.QUITONNONX);						
				foundJob = job;
			}	
		}

		if (foundJob == null)
			StackTraceHelper.handleException(
					new Throwable("Did not find job with pattern " + jobPattern +
							" to retrieve text from MSML."), ON_EXCEPTION.QUITONNONX);

		String res = null;
		JAXBContext jaxbContext;
		try {
			StringWriter w = new StringWriter();
			jaxbContext = JAXBContext.newInstance("de.mosgrid.msml.jaxb.bindings");
			JAXBElement<ModuleType> elem = new JAXBElement<ModuleType>(new QName("job"), ModuleType.class, (ModuleType)foundJob.getJaxBElement());
			javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(elem, w);
			res = w.getBuffer().toString();
		} catch (JAXBException e) {
			StackTraceHelper.handleException(
					new Throwable("Could not extract job to text.", e), ON_EXCEPTION.QUITONNONX);
		}
		return res;
	}

	private String getTextFromFile(String filepattern) {
		Pattern expr = Pattern.compile(filepattern);
		MSMLFileSelectorExtensionTag tag = (MSMLFileSelectorExtensionTag) MSMLExtensionVisitor.getTag(this);
		for (String fileName : tag.getLoadedFiles()) {
			Matcher m = expr.matcher(fileName);
			if (m.matches())
				return tag.getTextToFile(fileName);
		}
		
		// not found in loaded files. try to find file.
		FileMatchWalker walker = new FileMatchWalker(new File(GenericParserMainDocument.getBaseFolder()).getAbsolutePath());

		List<File> matchingFiles = null;
		try {
			matchingFiles = walker.getMatchingFiles(expr);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (matchingFiles == null || matchingFiles.size() != 1) {
			JOptionPane.showMessageDialog(null, "Did not find exactly one file. Count: " + matchingFiles.size());
			return "";
		}

		// read file and replace varying line-separator with constant \n.
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(matchingFiles.get(0));
			br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while((line = br.readLine())!= null) {
				sb.append(line + "\n");
			}			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally  {
			try {
				if (fis != null)
					fis.close();
				if (br != null)
					br.close();
			} catch (IOException e) {}
		}
		
		return sb.toString();
	}

	@Override
	public Object getTag() {
		return new MSMLFileSelectorExtensionTag();
	}

	@Override
	public boolean isEmpty(HashMap<String, String> config) {
		return StringH.isNullOrEmpty(config.get(MSMLFileSelectorExtension.FILEPATTERN)) && 
				!Boolean.parseBoolean(config.get(MSMLFileSelectorExtension.USEPARENTSTEXT));
	}
}