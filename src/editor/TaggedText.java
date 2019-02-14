package editor;

import java.util.ArrayList;

public class TaggedText {

	private final ArrayList<Object> content;

	public TaggedText(ArrayList<Object> content) {
		this.content = content;
	}

	public ArrayList<Object> getContent() {
		return content;
	}
}
