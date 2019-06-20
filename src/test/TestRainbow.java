package test;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbow.RainbowError;
import rainbow.RainbowHandler;
import util.Log;

public class TestRainbow {

	public static void main(String[] args) {
		Log.initializeLogger();
		RainbowHandler rainbowHandler = new RainbowHandler();
		try {
			String inputFile = "rainbow_test/test.docx";
			File packageDirectory = new File(System.getProperty("user.home"), "RainbowPackages");
			String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
			String packageName = "tnt_" + timestamp;
			rainbowHandler.createPackage(inputFile, packageDirectory.getAbsolutePath(), packageName);
		}
		catch (IOException | RainbowError ex) {
			Logger.getLogger(TestRainbow.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
