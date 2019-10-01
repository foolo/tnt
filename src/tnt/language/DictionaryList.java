package tnt.language;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import tnt.util.Log;

public class DictionaryList {

	final HashMap<LanguageTag, String> dictionaries = new HashMap<>();

	public boolean load(File dir) {
		findDictionaries(dir);
		return (dictionaries.isEmpty() == false);
	}

	static ArrayList<String> basenameToCode(String basename) {
		ArrayList<String> res = new ArrayList<>();
		basename = basename.toLowerCase().replace("-", "_");
		String[] code = basename.split("_");
		res.add(code[0]);
		if (code.length > 1) {
			res.add(code[1]);
		}
		return res;
	}

	private void findDictionaries(File dir) {
		File[] dicfiles = dir.listFiles((File f) -> {
			String lcase = f.getName().toLowerCase();
			return f.isFile() && lcase.endsWith(".dic") && (lcase.startsWith("hyph_") == false) && (f.isHidden() == false);
		});
		if (dicfiles == null) {
			Log.err("findDictionaries: dir.listFiles returned null");
			return;
		}
		for (File f : dicfiles) {
			String filename = f.getName();
			String basename = filename.substring(0, filename.length() - 4);
			String affName = basename + ".aff";
			File affFile = new File(dir, affName);
			if (affFile.exists() == false) {
				Log.warn("No corresponding .aff file file found for " + f);
				continue;
			}
			String path = new File(dir, basename).getPath();
			dictionaries.put(new LanguageTag(basename), path);
		}
		File[] subdirs = null;
		try {
			subdirs = dir.listFiles(File::isDirectory);
		}
		catch (SecurityException ex) {
			Log.err(ex);
		}
		if (subdirs == null) {
			Log.err("Could not list subdirectories of " + dir);
		}
		else {
			for (File f : subdirs) {
				findDictionaries(f);
			}
		}
	}
}
