package xliff_model;

import java.util.ArrayList;

public class TaggedText {

	private final ArrayList<TaggedTextContent> content;

	public TaggedText(ArrayList<TaggedTextContent> content) {
		this.content = content;
	}

	public ArrayList<TaggedTextContent> getContent() {
		return content;
	}
}
