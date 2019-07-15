package language;

import java.util.regex.Pattern;

public class Language {

	public final String name;
	public final String[] code;
	public String dictionaryPath;
	final Pattern SPLIT_CODE_PATTERN = Pattern.compile("[\\W_]+", Pattern.UNICODE_CHARACTER_CLASS);

	public Language(String name, String code, String dictionaryLocation) {
		this.name = name;
		this.code = SPLIT_CODE_PATTERN.split(code);
		this.dictionaryPath = dictionaryLocation;
	}

	public String getCodeAsString() {
		return String.join("-", code);
	}

	boolean matchCode(String[] c) {
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
