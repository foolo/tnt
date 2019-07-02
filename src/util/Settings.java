package util;

import java.io.File;
import java.util.prefs.Preferences;

public class Settings {

	private static final Preferences prefs = Preferences.userNodeForPackage(Settings.class);

	public static File getPackageDirectory() {
		File defaultDir = new File(System.getProperty("user.home"), "RainbowPackages");
		String dir = prefs.get("package_directory", defaultDir.getAbsolutePath());
		return new File(dir);
	}

	public static void setPackageDirectory(File dir) {
		prefs.put("package_directory", dir.getAbsolutePath());
	}

	public static File getInputFileDirectory() {
		File defaultDir = new File(System.getProperty("user.home"));
		String dir = prefs.get("input_file_directory", defaultDir.getAbsolutePath());
		return new File(dir);
	}

	public static void setInputFileDirectory(File dir) {
		prefs.put("input_file_directory", dir.getAbsolutePath());
	}

	public static File getLastOpenedFile() {
		File defaultDir = new File(System.getProperty("user.home"));
		String dir = prefs.get("last_opened_file", defaultDir.getAbsolutePath());
		return new File(dir);
	}

	public static void setLastOpenedFile(File f) {
		prefs.put("last_opened_file", f.getAbsolutePath());
	}
}
