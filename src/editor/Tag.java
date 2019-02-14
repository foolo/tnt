package editor;

public class Tag {

	enum Type {
		START, END
	};

	private final int index;
	private final Type type;

	public Tag(int index, Type type) {
		this.index = index;
		this.type = type;
	}

	int getIndex() {
		return index;
	}

	Type getType() {
		return type;
	}
}
