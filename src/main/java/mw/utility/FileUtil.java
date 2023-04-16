package mw.utility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.common.base.Charsets;

public class FileUtil {

	public static String readAllText(String file) throws IOException, URISyntaxException {
		return Files.readString(Paths.get(file), Charsets.UTF_8 );
	} 
}
