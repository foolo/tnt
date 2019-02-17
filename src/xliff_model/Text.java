package xliff_model;

public class Text implements TaggedTextContent {

	String content;

	public Text(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
}
