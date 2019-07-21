package language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import util.Log;

public class LanguageCollection {

	static ArrayList<Language> languages = new ArrayList<>();
	static String LANGUAGE_LIST_FILE_VALUE_DELIMITER = ";";
	static String LANGUAGE_LIST_FILENAME = "languages.txt";

	public static ArrayList<Language> getLanguages() {
		return languages;
	}

	static Language decodeLine(String s, int lineNumber) {
		String[] parts = s.split(LANGUAGE_LIST_FILE_VALUE_DELIMITER, -1);
		if (parts.length != 3) {
			return null;
		}
		String name = parts[0];
		String code = parts[1];
		String path = parts[2];
		if (path.isEmpty()) {
			path = null;
		}
		return new Language(name, code, path);
	}

	static Language findLanguage(String[] code) {
		for (Language l : languages) {
			if (l.matchCode(code)) {
				return l;
			}
		}
		return null;
	}

	public static Language findLanguageWithFallback(String[] code) {
		while (code.length > 0) {
			Language l = findLanguage(code);
			if (l != null) {
				return l;
			}
			code = Arrays.copyOf(code, code.length - 1);
		}
		return null;
	}

	public static void loadLanguages() throws IOException {
		File file = new File(LANGUAGE_LIST_FILENAME);

		BufferedReader br = new BufferedReader(new FileReader(file));
		int lineNumber = 0;
		while (br.ready()) {
			lineNumber++;
			String line = br.readLine();
			if (line.trim().startsWith("#")) {
				continue;
			}
			Language decodedLanguage = decodeLine(line, lineNumber);
			if (decodedLanguage == null) {
				Log.err("decodeLine: " + file + " (line " + lineNumber + "): Invalid format: " + line);
				continue;
			}
			Language l = findLanguage(decodedLanguage.code);
			if (l == null) {
				languages.add(decodedLanguage);
				Log.debug("Added external language " + decodedLanguage + " from " + file);
			}
			else {
				if (decodedLanguage.dictionaryPath != null) {
					l.dictionaryPath = decodedLanguage.dictionaryPath;
					Log.debug("Added spelling dictionary for " + l + " (path: " + l.dictionaryPath + ")");
				}
			}
		}
	}

	private static void addLanguage(String name, String code) {
		languages.add(new Language(name, code, null));
	}

	static {
		addLanguage("Abkhazian", "ab");
		addLanguage("Afar", "aa");
		addLanguage("Afrikaans", "af");
		addLanguage("Akan", "ak");
		addLanguage("Albanian", "sq");
		addLanguage("Amharic", "am");
		addLanguage("Arabic", "ar");
		addLanguage("Arabic (Algeria)", "ar-DZ");
		addLanguage("Arabic (Bahrain)", "ar-BH");
		addLanguage("Arabic (Egypt)", "ar-EG");
		addLanguage("Arabic (Iraq)", "ar-IQ");
		addLanguage("Arabic (Jordan)", "ar-JO");
		addLanguage("Arabic (Kuwait)", "ar-KW");
		addLanguage("Arabic (Lebanon)", "ar-LB");
		addLanguage("Arabic (Libya)", "ar-LY");
		addLanguage("Arabic (Morocco)", "ar-MA");
		addLanguage("Arabic (Oman)", "ar-OM");
		addLanguage("Arabic (Qatar)", "ar-QA");
		addLanguage("Arabic (Saudi Arabia)", "ar-SA");
		addLanguage("Arabic (Syrian Arab Republic)", "ar-SY");
		addLanguage("Arabic (Tunisia)", "ar-TN");
		addLanguage("Arabic (United Arab Emirates)", "ar-AE");
		addLanguage("Arabic (Yemen)", "ar-YE");
		addLanguage("Aragonese", "an");
		addLanguage("Armenian", "hy");
		addLanguage("Assamese", "as");
		addLanguage("Avaric", "av");
		addLanguage("Avestan", "ae");
		addLanguage("Aymara", "ay");
		addLanguage("Azerbaijani", "az");
		addLanguage("Bambara", "bm");
		addLanguage("Bashkir", "ba");
		addLanguage("Basque", "eu");
		addLanguage("Belarusian", "be");
		addLanguage("Bengali", "bn");
		addLanguage("Bihari languages", "bh");
		addLanguage("Bislama", "bi");
		addLanguage("Bosnian", "bs");
		addLanguage("Breton", "br");
		addLanguage("Bulgarian", "bg");
		addLanguage("Burmese", "my");
		addLanguage("Catalan", "ca");
		addLanguage("Catalan (Valencian)", "ca-valencia");
		addLanguage("Chamorro", "ch");
		addLanguage("Chechen", "ce");
		addLanguage("Chinese", "zh");
		addLanguage("Chinese (China)", "zh-CN");
		addLanguage("Chinese (Han [Simplified variant])", "zh-Hans");
		addLanguage("Chinese (Han [Traditional variant])", "zh-Hant");
		addLanguage("Chinese (Hong Kong)", "zh-HK");
		addLanguage("Chinese (Singapore)", "zh-SG");
		addLanguage("Chinese (Taiwan, Province of China)", "zh-TW");
		addLanguage("Church Slavic", "cu");
		addLanguage("Chuvash", "cv");
		addLanguage("Cornish", "kw");
		addLanguage("Corsican", "co");
		addLanguage("Cree", "cr");
		addLanguage("Croatian", "hr");
		addLanguage("Czech", "cs");
		addLanguage("Danish", "da");
		addLanguage("Dhivehi", "dv");
		addLanguage("Dutch", "nl");
		addLanguage("Dutch (Belgium)", "nl-BE");
		addLanguage("Dzongkha", "dz");
		addLanguage("English", "en");
		addLanguage("English (Australia)", "en-AU");
		addLanguage("English (Canada)", "en-CA");
		addLanguage("English (South Africa)", "en-ZA");
		addLanguage("English (United Kingdom)", "en-GB");
		addLanguage("English (United States)", "en-US");
		addLanguage("Esperanto", "eo");
		addLanguage("Estonian", "et");
		addLanguage("Ewe", "ee");
		addLanguage("Faroese", "fo");
		addLanguage("Fijian", "fj");
		addLanguage("Filipino", "fil");
		addLanguage("Finnish", "fi");
		addLanguage("French", "fr");
		addLanguage("French (Belgium)", "fr-BE");
		addLanguage("French (Canada)", "fr-CA");
		addLanguage("French (France)", "fr-FR");
		addLanguage("French (Luxembourg)", "fr-LU");
		addLanguage("French (Switzerland)", "fr-CH");
		addLanguage("Fulah", "ff");
		addLanguage("Galician", "gl");
		addLanguage("Ganda", "lg");
		addLanguage("Georgian", "ka");
		addLanguage("German", "de");
		addLanguage("German (Austria)", "de-AT");
		addLanguage("German (Germany)", "de-DE");
		addLanguage("German (Switzerland)", "de-CH");
		addLanguage("Greek", "el");
		addLanguage("Guarani", "gug");
		addLanguage("Gujarati", "gu");
		addLanguage("Haitian", "ht");
		addLanguage("Hausa", "ha");
		addLanguage("Hebrew", "he");
		addLanguage("Herero", "hz");
		addLanguage("Hindi", "hi");
		addLanguage("Hiri Motu", "ho");
		addLanguage("Hungarian", "hu");
		addLanguage("Icelandic", "is");
		addLanguage("Ido", "io");
		addLanguage("Igbo", "ig");
		addLanguage("Indonesian", "id");
		addLanguage("Indonesian", "in");
		addLanguage("Interlingua", "ia");
		addLanguage("Interlingue", "ie");
		addLanguage("Inuktitut", "iu");
		addLanguage("Inupiaq", "ik");
		addLanguage("Irish", "ga");
		addLanguage("Italian", "it");
		addLanguage("Italian (Italy)", "it-IT");
		addLanguage("Italian (Switzerland)", "it-CH");
		addLanguage("Japanese", "ja");
		addLanguage("Japanese (Japan)", "ja-JP");
		addLanguage("Javanese", "jv");
		addLanguage("Kalaallisut", "kl");
		addLanguage("Kannada", "kn");
		addLanguage("Kanuri", "kr");
		addLanguage("Kashmiri", "ks");
		addLanguage("Kazakh", "kk");
		addLanguage("Khmer", "km");
		addLanguage("Kikuyu", "ki");
		addLanguage("Kinyarwanda", "rw");
		addLanguage("Kirghiz", "ky");
		addLanguage("Komi", "kv");
		addLanguage("Kongo", "kg");
		addLanguage("Korean", "ko");
		addLanguage("Kuanyama", "kj");
		addLanguage("Kurdish, Central", "ckb");
		addLanguage("Kurdish, Northern (Latin)", "kmr-Latn");
		addLanguage("Kurdish, Southern", "sdh");
		addLanguage("Lao", "lo");
		addLanguage("Latin", "la");
		addLanguage("Latvian", "lv");
		addLanguage("Limburgan", "li");
		addLanguage("Lingala", "ln");
		addLanguage("Lithuanian", "lt");
		addLanguage("Luba-Katanga", "lu");
		addLanguage("Luxembourgish", "lb");
		addLanguage("Macedonian", "mk");
		addLanguage("Malagasy", "mg");
		addLanguage("Malay", "ms");
		addLanguage("Malayalam", "ml");
		addLanguage("Maltese", "mt");
		addLanguage("Manx", "gv");
		addLanguage("Maori", "mi");
		addLanguage("Marathi", "mr");
		addLanguage("Marshallese", "mh");
		addLanguage("Moldavian", "mo");
		addLanguage("Mongolian", "mn");
		addLanguage("Nauru", "na");
		addLanguage("Navajo", "nv");
		addLanguage("Ndonga", "ng");
		addLanguage("Nepali", "ne");
		addLanguage("North Ndebele", "nd");
		addLanguage("Northern Sami", "se");
		addLanguage("Norwegian", "no");
		addLanguage("Norwegian Bokmål", "nb");
		addLanguage("Norwegian Nynorsk", "nn");
		addLanguage("Nyanja", "ny");
		addLanguage("Occitan", "oc");
		addLanguage("Ojibwa", "oj");
		addLanguage("Oriya", "or");
		addLanguage("Oromo", "om");
		addLanguage("Ossetian", "os");
		addLanguage("Pali", "pi");
		addLanguage("Panjabi", "pa");
		addLanguage("Persian", "fa");
		addLanguage("Polish", "pl");
		addLanguage("Portuguese", "pt");
		addLanguage("Portuguese (Brazil)", "pt-BR");
		addLanguage("Pushto", "ps");
		addLanguage("Quechua", "qu");
		addLanguage("Romanian", "ro");
		addLanguage("Romansh", "rm");
		addLanguage("Rundi", "rn");
		addLanguage("Russian", "ru");
		addLanguage("Samoan", "sm");
		addLanguage("Sango", "sg");
		addLanguage("Sanskrit", "sa");
		addLanguage("Sardinian", "sc");
		addLanguage("Scottish Gaelic", "gd");
		addLanguage("Serbian", "sr");
		addLanguage("Serbian (Latin)", "sr-Latn");
		addLanguage("Shona", "sn");
		addLanguage("Sichuan Yi", "ii");
		addLanguage("Sindhi", "sd");
		addLanguage("Sinhala", "si");
		addLanguage("Slovak", "sk");
		addLanguage("Slovenian", "sl");
		addLanguage("Somali", "so");
		addLanguage("South Ndebele", "nr");
		addLanguage("Southern Sotho", "st");
		addLanguage("Spanish", "es");
		addLanguage("Spanish (Argentina)", "es-AR");
		addLanguage("Spanish (Bolivia)", "es-BO");
		addLanguage("Spanish (Chile)", "es-CL");
		addLanguage("Spanish (Colombia)", "es-CO");
		addLanguage("Spanish (Costa Rica)", "es-CR");
		addLanguage("Spanish (Dominican Republic)", "es-DO");
		addLanguage("Spanish (Ecuador)", "es-EC");
		addLanguage("Spanish (El Salvador)", "es-SV");
		addLanguage("Spanish (Guatemala)", "es-GT");
		addLanguage("Spanish (Honduras)", "es-HN");
		addLanguage("Spanish (Latin America and the Caribbean)", "es-419");
		addLanguage("Spanish (Mexico)", "es-MX");
		addLanguage("Spanish (Nicaragua)", "es-NI");
		addLanguage("Spanish (Panama)", "es-PA");
		addLanguage("Spanish (Paraguay)", "es-PY");
		addLanguage("Spanish (Peru)", "es-PE");
		addLanguage("Spanish (Puerto Rico)", "es-PR");
		addLanguage("Spanish (Spain)", "es-ES");
		addLanguage("Spanish (Uruguay)", "es-UY");
		addLanguage("Spanish (Venezuela)", "es-VE");
		addLanguage("Sundanese", "su");
		addLanguage("Swahili", "sw");
		addLanguage("Swati", "ss");
		addLanguage("Swedish", "sv");
		addLanguage("Swedish (Finland)", "sv-FI");
		addLanguage("Tagalog", "tl");
		addLanguage("Tahitian", "ty");
		addLanguage("Tajik", "tg");
		addLanguage("Tamil", "ta");
		addLanguage("Tatar", "tt");
		addLanguage("Telugu", "te");
		addLanguage("Thai", "th");
		addLanguage("Tibetan", "bo");
		addLanguage("Tigrinya", "ti");
		addLanguage("Tonga", "to");
		addLanguage("Tsonga", "ts");
		addLanguage("Tswana", "tn");
		addLanguage("Turkish", "tr");
		addLanguage("Turkmen", "tk");
		addLanguage("Twi", "tw");
		addLanguage("Uighur", "ug");
		addLanguage("Ukrainian", "uk");
		addLanguage("Urdu", "ur");
		addLanguage("Uzbek", "uz");
		addLanguage("Venda", "ve");
		addLanguage("Vietnamese", "vi");
		addLanguage("Volapük", "vo");
		addLanguage("Walloon", "wa");
		addLanguage("Welsh", "cy");
		addLanguage("Western Frisian", "fy");
		addLanguage("Wolof", "wo");
		addLanguage("Xhosa", "xh");
		addLanguage("Yiddish", "yi");
		addLanguage("Yoruba", "yo");
		addLanguage("Zhuang", "za");
		addLanguage("Zulu", "zu");
	}
}