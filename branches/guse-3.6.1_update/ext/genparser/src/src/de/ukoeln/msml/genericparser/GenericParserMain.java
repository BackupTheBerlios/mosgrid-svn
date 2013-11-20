package de.ukoeln.msml.genericparser;

import de.ukoeln.msml.genericparser.worker.StackTraceHelper;
import de.ukoeln.msml.genericparser.worker.StackTraceHelper.ON_EXCEPTION;

public class GenericParserMain {

//	private static final Logger LOGGER = LoggerFactory.getLogger(GenericParserMain.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		LogManager.getLogManager().reset();
//		java.util.logging.Logger.getLogger("").setLevel(java.util.logging.Level.FINEST);
//		try {
//			Properties properties = new Properties();
//			InputStream s = GenericParserMain.class.getResourceAsStream("log4j.properties");
//			properties.load(new BufferedInputStream(s));
//			s.close();
//			PropertyConfigurator.configure(properties);
//		} catch (IOException e) {
//			LOGGER.error("Unable to load properties for Logging.");
//			e.printStackTrace();
//		}

		GenericParserMainDocument doc = new GenericParserMainDocument();

		final StartupParams params = new StartupParams(args);
		params.checkValidity();
		params.setDefaults();

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				StackTraceHelper.handleException(e, ON_EXCEPTION.QUIT);
			}
		});
		doc.Init(params);
	}
}
