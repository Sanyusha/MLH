package android.mlh.logger;

public class Logger {
	
	public static final String INFO_PRIORITY = "INFO";
	public static final String DEBUG_PRIORITY = "DEBUG";
	public static final String WARN_PRIORITY = "WARN";
	
	public static void log(String logName, String logPriority, String logMessage) {
		
		org.apache.log4j.Logger logger = Log4jHelper.getLogger(logName);
		
		if (logPriority.equals(INFO_PRIORITY)) {
			logger.info(logMessage);
		} else if (logPriority.equals(DEBUG_PRIORITY)) {
			logger.debug(logMessage);
		}
	}
}
