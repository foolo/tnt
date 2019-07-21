package language;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DictionaryFinder {

	static HashMap<String, String> dictionaries = new HashMap();
	static ArrayList<String> referencedDictionaries = new ArrayList<>();
	static boolean errorFound = false;

	public static void main(String[] args) throws IOException {
		checkGenericVersions();
		File workingDir = new File("dictionaries");
		findDictionaries(workingDir);
		for (Language l : LanguageCollection.getLanguages()) {
			String path = findDictionaryLocationByCode(l.code);
			l.dictionaryPath = path;
			if (path != null) {
				referencedDictionaries.add(path);
			}
		}

		for (Map.Entry<String, String> item : dictionaries.entrySet()) {
			if (referencedDictionaries.contains(item.getValue()) == false) {
				logError("Unreferenced: " + item.getValue());
			}
		}

		ArrayList<String> lines = new ArrayList<>();

		for (Language l : LanguageCollection.getLanguages()) {
			if (l.dictionaryPath.contains(",")) {
				logError("Dictionary path contains comma: " + l.dictionaryPath);
			}
			lines.add(join(new String[]{l.name, l.getCodeAsString(), l.dictionaryPath}, LanguageCollection.LANGUAGE_LIST_FILE_VALUE_DELIMITER));
		}
		if (errorFound) {
			System.out.println("errors found, no list generated");
			return;
		}

		try (FileWriter writer = new FileWriter(LanguageCollection.LANGUAGE_LIST_FILENAME)) {
			writer.write("# Generated by DictionaryFinder.java" + System.lineSeparator());
			for (String l : lines) {
				writer.write(l + System.lineSeparator());
			}
		}
		System.out.println("Language list written to " + LanguageCollection.LANGUAGE_LIST_FILENAME);
	}

	static void checkGenericVersions() {
		for (Language l : LanguageCollection.getLanguages()) {
			if (l.code.length > 1) {
				if (LanguageCollection.findLanguage(new String[]{l.code[0]}) == null) {
					System.out.println("NOTE: No generic entry '" + l.code[0] + "' found for " + l.code);
				}
			}
		}
	}

	static String join(String[] elements, String delimiter) {
		if (String.join("", elements).contains(delimiter)) {
			logError("Values contain delimiter (" + delimiter + "): " + Arrays.toString(elements));
		}
		return String.join(delimiter, elements);
	}

	static void logError(String msg) {
		errorFound = true;
		System.out.println("ERROR: " + msg);
	}

	static String basenameToCode(String code) {
		if (code.endsWith("_frami")) {
			return code.substring(0, code.length() - 6);
		}
		return code;
	}

	private static void findDictionaries(File dir) {
		File[] dicfiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				String lcase = f.getName().toLowerCase();
				return f.isFile() && lcase.endsWith(".dic") && (lcase.startsWith("hyph_") == false) && (f.isHidden() == false);
			}
		});

		for (File f : dicfiles) {
			String filename = f.getName();
			String basename = filename.substring(0, filename.length() - 4);
			String affName = basename + ".aff";
			File affFile = new File(dir, affName);
			if (affFile.exists() == false) {
				System.out.println("WARNING: No corresponding .aff file file found for " + f);
				continue;
			}
			String code = basenameToCode(basename);
			code = code.replace("-", "_");
			String path = new File(dir, basename).getPath();
			dictionaries.put(code, path);
		}
		File[] subdirs;
		try {
			subdirs = dir.listFiles(File::isDirectory);
		}
		catch (SecurityException ex) {
			logError(ex.toString());
			return;
		}
		if (subdirs == null) {
			logError("Could not list subdirectories of " + dir);
			return;
		}
		for (File f : subdirs) {
			findDictionaries(f);
		}
	}

	static ArrayList<String> findByPrefix(String prefix) {
		prefix = prefix + "_"; // find only locales, and avoid matching "ab" with "abc_"
		ArrayList<String> res = new ArrayList<>();
		for (Map.Entry<String, String> item : dictionaries.entrySet()) {
			if (item.getKey().startsWith(prefix)) {
				res.add(item.getValue());
			}
		}
		return res;
	}

	static String alternativeVariant(String code) {
		switch (code) {
			case "en":
				return "en_GB";
			case "de":
				return "de_DE";
			case "sv":
				return "sv_SE";
			case "pt":
				return "pt_PT";
			case "ku":
				return "kmr_Latn";
		}
		return code;
	}

	static String findDictionaryLocationByCode(String[] code) {
		String fullCode = String.join("_", code);
		String path = dictionaries.get(fullCode);
		if (path != null) {
			System.out.println("Exact match found for " + fullCode);
			return path;
		}

		if (code.length > 1) {
			path = dictionaries.get(code[0]);
			if (path != null) {
				System.out.println("using generic version '" + code[0] + "' for " + fullCode);
				return path;
			}
		}

		// generic with no direct match
		// or specific with no direct match
		// or specific with no match for prefix
		ArrayList<String> candidates = findByPrefix(code[0]);
		if (candidates.isEmpty()) {
			System.out.println("INFO: No dictionary found for " + fullCode);
			return "";
		}
		if (candidates.size() == 1) {
			path = candidates.get(0);
			System.out.println("Using the only available language variant for " + fullCode + ": " + path);
			return path;
		}

		// more than one possible dictionary candidate
		String alt = alternativeVariant(fullCode);
		path = dictionaries.get(alt);
		if (path != null) {
			System.out.println("Using alternative language variant '" + alt + " for " + fullCode);
			return path;
		}
		logError("no rule for " + fullCode);
		return "";
	}
}