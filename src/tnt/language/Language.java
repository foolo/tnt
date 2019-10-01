package tnt.language;

public class Language {

	public final String name;
	public final String originalTagStr;
	public final LanguageTag tag;
	public String dictionaryPath;

	public Language(String name, String tagStr, String dictionaryPath) {
		this.name = name;
		originalTagStr = tagStr;
		tag = new LanguageTag(tagStr);
		this.dictionaryPath = dictionaryPath;
	}
}
