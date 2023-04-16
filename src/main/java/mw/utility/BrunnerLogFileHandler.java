package mw.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.ErrorManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class BrunnerLogFileHandler extends StreamHandler {

    private static String logFormat = "yyyy-MM-dd HH"; //default

    private String directory = null;

    static ByteArrayOutputStream bas = new ByteArrayOutputStream(1024*200*25);  

    /**
     * Constructor.
     */
    public BrunnerLogFileHandler(String directory) {
        super();
        this.directory = directory;
        setOutputStream(bas);
        flush();
    }

    /**
     * 
     */
    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        super.publish(record);
        flush();
    }


    @Override
    public synchronized void flush() {

        // Puffer ist leer
        if (bas.size() == 0) {
            return;
        }

        super.flush();

        File file = null;

        try {
            String dateString = null;

            SimpleDateFormat sdf = new SimpleDateFormat(logFormat, Locale.getDefault());
            dateString = sdf.format(new Date());
            String fileName = dateString + ".log";

            try {
                boolean windows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0 ;

                if (!windows && directory != null) {

                    File unixLogDir = new File(directory);

                    if (unixLogDir.exists()) {
                        File logfile = new File (directory + "/" + fileName);
                        if (!logfile.exists()) {
                            logfile.createNewFile();
                        }
                        file = logfile;
                    }
                } 

            } catch(Exception e) {
                e.printStackTrace();
            }


            if (file == null) {
            	 File dir = new File(this.directory);
            	    if (! dir.exists()){
            	    	dir.mkdir();
            	    }
            			
                // Fallback - Umzug hat nicht geklappt
                file = new File(this.directory + "/" + fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
            }

            FileOutputStream fos = new FileOutputStream(file,true);
            bas.flush();    
            bas.writeTo(fos);
            bas.reset();
            fos.close();
	    } catch (Exception fnfe) {
	        reportError(null, fnfe, ErrorManager.GENERIC_FAILURE);
	        fnfe.printStackTrace(System.err);
	        setOutputStream(System.out); //fallback stream
	        try {
	            bas.writeTo(System.out);
	        } catch (IOException e) {
	            // Da kann man nichts machen
	        }
	
	    }
	}
}
