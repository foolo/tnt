package language;

public class Language {

	public String name;
	public String code;
	public String dictionaryPath;

	public Language(String name, String code, String dictionaryLocation) {
		this.name = name;
		this.code = code;
		this.dictionaryPath = dictionaryLocation;
	}

	@Override
	public String toString() {
		return name + " (" + code + ")";
	}
}
