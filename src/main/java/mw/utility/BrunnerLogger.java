package mw.utility;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BrunnerLogger {
	static Logger logger = Logger.getLogger("BrunnerLogger");
	static BrunnerLogFileHandler logFile;
	
	public static Logger getLogger(String directory) {
		
		if(logFile == null) {
			logFile = new BrunnerLogFileHandler(directory);
			logFile.setLevel(Level.ALL);
			logFile.setFormatter(new SimpleFormatter());
			
			logger.setLevel(Level.ALL);
			logger.addHandler(logFile);			
		}
		return logger;
	}
}
