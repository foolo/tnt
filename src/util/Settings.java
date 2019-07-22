package util;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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

	public static File getOpenDirectory() {
		String dir = prefs.get("last_opened_file", System.getProperty("user.home"));
		return new File(dir);
	}

	public static void setOpenDirectory(File f) {
		prefs.put("last_opened_file", f.getAbsolutePath());
	}

	public static String getEditorFontName() {
		return prefs.get("markup_view_font_name", Font.SANS_SERIF);
	}

	public static int getEditorFontStyle() {
		return prefs.getInt("markup_view_font_style", Font.PLAIN);
	}

	public static int getEditorFontSize() {
		return prefs.getInt("markup_view_font_size", 14);
	}

	public static void setEditorFont(String name, int style, int size) {
		prefs.put("markup_view_font_name", name);
		prefs.putInt("markup_view_font_style", style);
		prefs.putInt("markup_view_font_size", size);
	}

	static ArrayList<String> bytesToStringArray(byte[] b) {
		ArrayList<String> res = new ArrayList<>();
		if (b == null) {
			return res;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(bais);
			ArrayList<?> recentFiles = (ArrayList<?>) ois.readObject();
			for (Object o : recentFiles) {
				res.add((String) o);
			}
		}
		catch (IOException | ClassNotFoundException ex) {
			Log.err(ex);
		}
		return res;
	}

	public static byte[] stringArrayToByteArray(ArrayList<String> arr) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(arr);
			return baos.toByteArray();
		}
		catch (IOException ex) {
			Log.err(ex);
			return new byte[]{};
		}
	}

	public static ArrayList<String> getRecentFiles() {
		byte[] b = prefs.getByteArray("recent_files", null);
		return bytesToStringArray(b);
	}

	static void setRecentFiles(ArrayList<String> recentFiles) {
		byte[] b = stringArrayToByteArray(recentFiles);
		prefs.putByteArray("recent_files", b);
	}

	public static void addRecentFile(String s) {
		ArrayList<String> recentFiles = getRecentFiles();
		while (recentFiles.contains(s)) {
			recentFiles.remove(s);
		}
		recentFiles.add(s);
		while (recentFiles.size() > 10) {
			recentFiles.remove(0);
		}
		setRecentFiles(recentFiles);
	}

	public static void clearRecentFiles() {
		setRecentFiles(new ArrayList<>());
	}

	public static void removeRecentFile(String s) {
		ArrayList<String> recentFiles = getRecentFiles();
		while (recentFiles.contains(s)) {
			recentFiles.remove(s);
		}
		setRecentFiles(recentFiles);
	}

	public static File getLastOpenedFile() {
		ArrayList<String> recentFiles = getRecentFiles();
		if (recentFiles.isEmpty()) {
			return null;
		}
		return new File(recentFiles.get(recentFiles.size() - 1));
	}

	public static boolean getShowWhitespace() {
		return prefs.getBoolean("show_whitespace", false);
	}

	public static void setShowWhitespace(boolean showWhitespace) {
		prefs.putBoolean("show_whitespace", showWhitespace);
	}
}
