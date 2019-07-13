package language;

public class Language {

	public final String name;
	public final String code;
	public final String dictionaryPath;

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
