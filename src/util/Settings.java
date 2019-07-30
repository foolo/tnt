package util;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.prefs.Preferences;

public class Settings {

	private static final Preferences prefs = Preferences.userNodeForPackage(Settings.class);

	private static final String PACKAGE_DIRECTORY = "package_directory";
	private static final String INPUT_FILE_DIRECTORY = "input_file_directory";
	private static final String LAST_OPENED_FILE = "last_opened_file";
	private static final String MARKUP_VIEW_FONT_NAME = "markup_view_font_name";
	private static final String MARKUP_VIEW_FONT_STYLE = "markup_view_font_style";
	private static final String MARKUP_VIEW_FONT_SIZE = "markup_view_font_size";
	private static final String RECENT_FILES = "recent_files";
	private static final String SHOW_WHITESPACE = "show_whitespace";
	private static final String WORDLIST = "wordlist";

	public static File getPackageDirectory() {
		File defaultDir = new File(System.getProperty("user.home"), "RainbowPackages");
		String dir = prefs.get(PACKAGE_DIRECTORY, defaultDir.getAbsolutePath());
		return new File(dir);
	}

	public static void setPackageDirectory(File dir) {
		prefs.put(PACKAGE_DIRECTORY, dir.getAbsolutePath());
	}

	public static File getInputFileDirectory() {
		File defaultDir = new File(System.getProperty("user.home"));
		String dir = prefs.get(INPUT_FILE_DIRECTORY, defaultDir.getAbsolutePath());
		return new File(dir);
	}

	public static void setInputFileDirectory(File dir) {
		prefs.put(INPUT_FILE_DIRECTORY, dir.getAbsolutePath());
	}

	public static File getOpenDirectory() {
		String dir = prefs.get(LAST_OPENED_FILE, System.getProperty("user.home"));
		return new File(dir);
	}

	public static void setOpenDirectory(File f) {
		prefs.put(LAST_OPENED_FILE, f.getAbsolutePath());
	}

	public static String getEditorFontName() {
		return prefs.get(MARKUP_VIEW_FONT_NAME, Font.SANS_SERIF);
	}

	public static int getEditorFontStyle() {
		return prefs.getInt(MARKUP_VIEW_FONT_STYLE, Font.PLAIN);
	}

	public static int getEditorFontSize() {
		return prefs.getInt(MARKUP_VIEW_FONT_SIZE, 14);
	}

	public static void setEditorFont(String name, int style, int size) {
		prefs.put(MARKUP_VIEW_FONT_NAME, name);
		prefs.putInt(MARKUP_VIEW_FONT_STYLE, style);
		prefs.putInt(MARKUP_VIEW_FONT_SIZE, size);
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
		byte[] b = prefs.getByteArray(RECENT_FILES, null);
		return bytesToStringArray(b);
	}

	static void setRecentFiles(ArrayList<String> recentFiles) {
		byte[] b = stringArrayToByteArray(recentFiles);
		prefs.putByteArray(RECENT_FILES, b);
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
		return prefs.getBoolean(SHOW_WHITESPACE, false);
	}

	public static void setShowWhitespace(boolean showWhitespace) {
		prefs.putBoolean(SHOW_WHITESPACE, showWhitespace);
	}

	private static TreeSet<String> wordlistCache = null;

	public static String getWordlistData() {
		return prefs.get(WORDLIST, "");
	}

	public static void setWordlistData(String s) {
		wordlistCache = null;
		prefs.put(WORDLIST, s);
	}

	public static TreeSet<String> getWordList() {
		initWordlistCache();
		return wordlistCache;
	}

	private static void initWordlistCache() {
		if (wordlistCache != null) {
			return;
		}
		wordlistCache = new TreeSet<>();
		String data = getWordlistData();
		String[] words = data.split("\n");
		for (String word : words) {
			if (word.isEmpty() == false) {
				wordlistCache.add(word);
			}
		}
	}

	public static void addWordToWordlist(String word) {
		initWordlistCache();
		wordlistCache.add(word);
		String data = String.join("\n", wordlistCache);
		prefs.put(WORDLIST, data);
	}
}
