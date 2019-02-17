package xliff_model;

public class Tag implements TaggedTextContent {

	public enum Type {
		START, END
	};

	private final int index;
	private final Type type;

	public Tag(int index, Type type) {
		this.index = index;
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public Type getType() {
		return type;
	}
}
