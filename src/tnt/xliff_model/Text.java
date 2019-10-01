package xliff_model;

public class Text implements TaggedTextContent {

	private final String content;

	public Text(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "'" + content + "'";
	}

	@Override
	public TaggedTextContent copy() {
		return new Text(content);
	}
}
