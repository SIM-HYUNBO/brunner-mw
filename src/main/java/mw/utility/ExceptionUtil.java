package mw.utility;

public class ExceptionUtil {
	public static String getFullMessage(Throwable e) {
		StringBuilder logs = new StringBuilder();
		logs.append(e.getMessage());
		
		for (StackTraceElement element : e.getStackTrace()) {
			logs.append("\n");
			logs.append(element);
		}
		return logs.toString();		
	}
}
