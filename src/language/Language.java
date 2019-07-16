package language;

import java.util.regex.Pattern;

public class Language {

	public final String name;
	public final String[] code;
	public String dictionaryPath;
	static final Pattern SPLIT_CODE_PATTERN = Pattern.compile("[\\W_]+", Pattern.UNICODE_CHARACTER_CLASS);

	public Language(String name, String code, String dictionaryLocation) {
		this.name = name;
		this.code = stringToCode(code);
		this.dictionaryPath = dictionaryLocation;
	}

	public String getCodeAsString() {
		return String.join("-", code);
	}

	public static String[] stringToCode(String s) {
		return SPLIT_CODE_PATTERN.split(s);
	}

	public boolean matchCode(String[] c) {
		if (c.length != code.length) {
			return false;
		}
		for (int i = 0; i < c.length; i++) {
			if (c[i].toLowerCase().equals(code[i].toLowerCase()) == false) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return name + " (" + getCodeAsString() + ")";
	}
}
