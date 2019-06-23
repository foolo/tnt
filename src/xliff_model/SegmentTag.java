package xliff_model;

import xliff_model.exceptions.EncodeException;
import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.XmlUtil;

public class SegmentTag {

	private TaggedText sourceText;
	private TaggedText targetText;
	private final Element node;
	private final Node sourceNode;
	private final Node targetNode;
	private State state;

	public static final String ATTRIBUTE_STATE = "state";

	public enum State {
		INITIAL, TRANSLATED, REVIEWED, FINAL;

		static State fromString(String s) {
			try {
				return State.valueOf(s.toUpperCase());
			}
			catch (IllegalArgumentException ex) {
				return State.INITIAL;
			}
		}

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	};

	public SegmentTag(Element node, UnitTag parent) throws ParseException {
		this.node = node;
		state = State.fromString(node.getAttribute(ATTRIBUTE_STATE));
		sourceNode = XmlUtil.getChildByName(node, "source");
		if (sourceNode == null) {
			throw new ParseException("Mandatory <source> missing in <segment>");
		}
		Node tn = XmlUtil.getChildByName(node, "target");
		if (tn == null) {
			tn = sourceNode.cloneNode(false);
			XmlUtil.clearChildren(tn);
			node.appendChild(tn);
			tn = node.getOwnerDocument().renameNode(tn, null, "target");
		}
		targetNode = tn;
		sourceText = new TaggedText(sourceNode);
		targetText = new TaggedText(targetNode);
	}

	public SegmentTag(SegmentTag st, UnitTag parent) {
		this.sourceText = st.sourceText;
		this.targetText = st.targetText;
		this.node = st.node;
		this.sourceNode = st.sourceNode;
		this.targetNode = st.targetNode;
		this.state = st.state;
	}

	public TaggedText getSourceText() {
		return sourceText;
	}

	public TaggedText getTargetText() {
		if (targetText != null) {
			return targetText;
		}
		return new TaggedText(new ArrayList<>());
	}

	public Node getNode() {
		return node;
	}

	public void setTargetText(TaggedText s) {
		targetText = s;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	static void replaceChildren(Node node, ArrayList<Node> newNodes) {
		XmlUtil.clearChildren(node);
		for (Node n : newNodes) {
			node.appendChild(n);
		}
	}

	public void encodeContent(Node node, TaggedText text, ArrayList<SegmentError> errors) {
		ArrayList<Node> nodes;
		try {
			nodes = text.toNodes(node.getOwnerDocument());
		}
		catch (EncodeException ex) {
			nodes = new ArrayList<>();
			errors.add(new SegmentError(this, ex.getMessage()));
		}
		replaceChildren(node, nodes);
	}

	public void encode(ArrayList<SegmentError> errors) {
		encodeContent(sourceNode, sourceText, errors);

		if (targetText.getContent().isEmpty()) {
			try {
				node.removeChild(targetNode);
			}
			catch (DOMException ex) {
				if (ex.code != DOMException.NOT_FOUND_ERR) {
					throw ex;
				}
			}
		}
		else {
			encodeContent(targetNode, targetText, errors);
			node.setAttribute(ATTRIBUTE_STATE, state.toString());
			node.appendChild(targetNode);
		}
	}
}
