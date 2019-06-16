package test;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rainbow.RainbowHandler;

public class TestRainbow {

	public static void main(String[] args) {
		RainbowHandler rainbowHandler = new RainbowHandler();
		try {
			ArrayList<String> inputFiles = new ArrayList<>();
			inputFiles.add("rainbow_test/test.docx");
			File packageDirectory = new File(System.getProperty("user.home"), "RainbowPackages");
			String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
			String packageName = "tnt_" + timestamp;
			rainbowHandler.createPackage(inputFiles, packageDirectory.getAbsolutePath(), packageName);
		}
		catch (IOException ex) {
			Logger.getLogger(TestRainbow.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
