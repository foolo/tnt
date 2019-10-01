package xliff_model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Tag implements TaggedTextContent {

	public enum Type {
		START, END, EMPTY
	};

	private final Node node;
	private final Type type;
	private final String label;

	public Tag(Node node, Type type) {
		this.node = node;
		this.type = type;
		if ((node instanceof Element) && (type == Type.START || type == Type.EMPTY)) {
			label = ((Element) node).getAttribute("id");
		}
		else {
			label = "";
		}
	}

	public Node getNode() {
		return node;
	}

	public Type getType() {
		return type;
	}

	@Override
	public TaggedTextContent copy() {
		if (node == null) {
			return new Tag(null, type);
		}
		return new Tag(node.cloneNode(false), type);
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

	public String getLabel() {
		return label;
	}
}
