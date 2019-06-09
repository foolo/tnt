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

	public static void setOpenDirectory(File openDirectory) {
		prefs.put("open_directory", openDirectory.toString());
	}
}
