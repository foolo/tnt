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
		File defaultPackageDirectory = new File(System.getProperty("user.home"), "RainbowPackages");
		String dir = prefs.get("package_directory", defaultPackageDirectory.getAbsolutePath());
		return new File(dir);
	}

	// todo use
	public static void setPackageDirectory(File dir) {
		prefs.put("package_directory", dir.getAbsolutePath());
	}
}
