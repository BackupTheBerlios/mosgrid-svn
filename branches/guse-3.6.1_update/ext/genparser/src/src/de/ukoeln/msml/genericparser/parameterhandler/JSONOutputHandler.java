package de.ukoeln.msml.genericparser.parameterhandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.ukoeln.msml.genericparser.GenericParserMainDocument;

public class JSONOutputHandler extends AbstractOutputHandler {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JSONOutputHandler.class);

	public JSONOutputHandler() {
		super();
	}
	
	public JSONOutputHandler(ParameterType param) {
		super(param);
	}

	@Override
	public void handle(GenericParserMainDocument doc) {
		String fileName = calcOutputFileName(doc);
		if (fileName == null || "".equals(fileName))
			fileName = doc.getCurrentHandleInfo().getCurEditor().getPath();

		LOGGER.info("Creating JSON from file: " + fileName);
		try {
			generateJSON(fileName);
		} catch (Exception e) {
			LOGGER.error("JSON could not be created: " + e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private void generateJSON(String filename) throws SAXException,
			IOException, ParserConfigurationException {
		final Map<String, String> mdMap = new HashMap<String, String>();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		DefaultHandler handler = new DefaultHandler() {
			boolean scalar = false;
			String key = "";
			String value = "";
			String jobname = "";

			public void startElement(String uri, String localName,
					String qName, Attributes atts) {
				if (qName.equals("module"))
					for (int i = 0; i < atts.getLength(); i++) {
						if (atts.getValue(i).equals("compchem:job")) {
							jobname = atts.getValue(i + 1);
							mdMap.put(jobname, jobname);
						}
						if (atts.getQName(i).equals("cmlx:displayName")
								|| atts.getQName(i)
										.equals("cmlx:workflowNotes")
								|| atts.getQName(i).equals("title"))
							mdMap.put(atts.getQName(i), atts.getValue(i));
					}
				if (qName.equals("parameter") || qName.equals("property"))
					key = jobname + ":" + atts.getValue(0);
				if (qName.equals("scalar"))
					scalar = true;
			}

			public void endElement(String uri, String localName, String qName) {
				if (qName.equals("scalar"))
					scalar = false;
				if (qName.equals("parameter") || qName.equals("property")) {
					mdMap.put(key, value);
					key = "";
					value = "";
				}
			}

			public void characters(char ch[], int start, int length) {
				if (scalar)
					value = new String(ch, start, length).trim();
			}
		};

		saxParser.parse(filename, handler);

		JSONObject myString = new JSONObject(mdMap);
//		System.out.println(myString.toString());

		LOGGER.info("Creating JSON: " + filename + ".metadata");
		File nf = new File(filename + ".metadata");
		FileWriter writer = new FileWriter(nf, false);
		writer.write(myString.toString());
		writer.flush();
		writer.close();
	}

	@Override
	public float getWeight() {
		return 1001; // must always be bigger than Output-Handler
	}

	@Override
	public IParamHandler getInstance(ParameterType param) {
		return new JSONOutputHandler(param);
	}

}
