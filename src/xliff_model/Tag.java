package xliff_model;

import org.w3c.dom.Node;

public class Tag implements TaggedTextContent {

	public enum Type {
		START, END, EMPTY
	};

	private final Node node;
	private final Type type;

	public Tag(Node node, Type type) {
		this.node = node;
		this.type = type;
	}

	public Node getNode() {
		return node;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		switch (type) {
			case START:
				return "[" + node.getNodeName() + "]";
			case END:
				return "[/]";
			case EMPTY:
				return "[" + node.getNodeName() + "/]";
		}
		return null;
	}

	public String getShortString() {
		if (type == Type.START || type == Type.EMPTY) {
			return node.getNodeName();
		}
		else {
			return "";
		}
	}
}
