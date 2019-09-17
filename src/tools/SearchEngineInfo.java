package tools;

public class SearchEngineInfo {

	public final String name;
	private final String urlPattern;

	public SearchEngineInfo(String name, String urlPattern) {
		this.name = name;
		this.urlPattern = urlPattern;
	}

	public String getUrl(String searchTerm) {
		return urlPattern.replace("{WORD}", searchTerm);
	}

	@Override
	public String toString() {
		return name + " - " + urlPattern;
	}
}
