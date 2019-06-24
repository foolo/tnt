package util;

import java.io.File;
import java.util.prefs.Preferences;

public class Settings {

	private static final Preferences prefs = Preferences.userNodeForPackage(Settings.class);

	public static File getOpenDirectory() {
		String dir = prefs.get("open_directory", null);
		if (dir == null) {
			return null;
		}
		return new File(dir);
	}

	public static void setOpenDirectory(File dir) {
		prefs.put("open_directory", dir.getAbsolutePath());
	}

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
}
