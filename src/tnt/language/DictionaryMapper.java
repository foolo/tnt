package tnt.language;

import java.util.ArrayList;
import java.util.Map;
import tnt.util.Log;

public class DictionaryMapper {

	static ArrayList<String> referencedPaths = new ArrayList<>();

	public static void mapDictionaries(DictionaryList dl) {
		for (Language l : LanguageCollection.getLanguages()) {
			String path = findDictionaryLocationByTag(l.tag, dl);
			l.dictionaryPath = path;
			if (path != null) {
				referencedPaths.add(path);
			}
		}
		for (Map.Entry<LanguageTag, String> item : dl.dictionaries.entrySet()) {
			LanguageTag tag = item.getKey();
			String path = item.getValue();
			if (referencedPaths.contains(path) == false) {
				String code = String.join("-", tag);
				LanguageCollection.getLanguages().add(new Language(code, code, path));
				Log.warn("Unknown dictionary added to language list: " + code + " : " + path);
			}
		}
	}

	static ArrayList<String> findByPrefix(String prefix, DictionaryList df) {
		ArrayList<String> res = new ArrayList<>();
		for (Map.Entry<LanguageTag, String> item : df.dictionaries.entrySet()) {
			String itemPrefix = item.getKey().get(0);
			if (itemPrefix.equals(prefix)) {
				res.add(item.getValue());
			}
		}
		return res;
	}

	static LanguageTag preferredVariant(String tag) {
		switch (tag) {
			case "en":
				return new LanguageTag("en-GB");
			case "de":
				return new LanguageTag("de-DE");
			case "sv":
				return new LanguageTag("sv-SE");
			case "pt":
				return new LanguageTag("pt-PT");
			case "ku":
				return new LanguageTag("kmr-Latn");
		}
		return null;
	}

	static String findDictionaryLocationByTag(LanguageTag tag, DictionaryList dl) {
		String path = dl.dictionaries.get(tag);
		if (path != null) {
			Log.debug("exact match found for " + tag);
			return path;
		}

		if (tag.size() > 1) {
			LanguageTag primaryTag = tag.primaryTag();
			path = dl.dictionaries.get(tag.primaryTag());
			if (path != null) {
				Log.debug("using primary tag '" + primaryTag + "' for " + tag);
				return path;
			}
		}

		// generic with no direct match
		// or specific with no direct or prefix match
		ArrayList<String> candidates = findByPrefix(tag.get(0), dl);
		if (candidates.isEmpty()) {
			Log.debug("no dictionary found for " + tag);
			return null;
		}
		if (candidates.size() == 1) {
			path = candidates.get(0);
			Log.debug("using the only available language variant for " + tag + ": " + path);
			return path;
		}

		// more than one possible dictionary candidate
		LanguageTag pref = preferredVariant(tag.get(0));
		if (pref != null) {
			path = dl.dictionaries.get(pref);
			if (path != null) {
				Log.debug("using preferred variant '" + pref + " for " + tag);
				return path;
			}
			else {
				path = candidates.get(0);
				Log.warn("could not find preferred variant: " + pref + " for " + tag + ", use first match: " + path);
			}
		}
		else {
			path = candidates.get(0);
			Log.warn("no prefered variant for " + tag + ", use first match: " + path);
		}
		return path;
	}
}
