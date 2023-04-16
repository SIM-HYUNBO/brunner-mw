package mw.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class StringUtil {
	
	static Pattern nemericPattern = Pattern.compile("^[-+]?(0|[1-9][0-9]*)(\\\\.[0-9]+)?([eE][-+]?[0-9]+)?$");

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	public static String getTxnId() {
		Date now = Calendar.getInstance().getTime();
		
		return sdf.format(now) + String.format("%d", System.nanoTime());
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false; 
	    }
	    return nemericPattern.matcher(strNum).matches();
	}
	
	public static boolean isNullOrEmpty(String value) {
	    return value == null || value.isEmpty();
	}	
}

