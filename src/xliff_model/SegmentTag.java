package xliff_model;

import xliff_model.exceptions.EncodeException;
import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import rainbow.ValidationError;
import util.XmlUtil;

public class SegmentTag {

	private TaggedText sourceText;
	private TaggedText targetText;
	private final Element node;
	private final Node sourceNode;
	private final Node targetNode;
	private State state;
	private boolean staged = false;
	private final String id;

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

	static int idCounter = 0;

	static String generateId() {
		return "st" + idCounter++;
	}

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
		id = generateId();
	}

	public SegmentTag(SegmentTag st, UnitTag parent) {
		this.sourceText = st.sourceText;
		this.targetText = st.targetText;
		this.node = st.node;
		this.sourceNode = st.sourceNode;
		this.targetNode = st.targetNode;
		this.state = st.state;
		this.id = st.id;
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

	public Element getNode() {
		return node;
	}

	public void setTargetText(TaggedText s) {
		targetText = s;
	}

	public State getState() {
		return state;
	}

	public String getId() {
		return id;
	}

	public ValidationError testEncode() {
		ArrayList<ValidationError> errors = new ArrayList<>();
		encodeContent(targetNode, targetText, errors);
		if (errors.isEmpty() == false) {
			return errors.get(0);
		}
		return null;
	}

	public boolean setState(State state) {
		if (state != this.state) {
			this.state = state;
			return true;
		}
		return false;
	}

	public void stage() {
		staged = true;
	}

	static void replaceChildren(Node node, ArrayList<Node> newNodes) {
		XmlUtil.clearChildren(node);
		for (Node n : newNodes) {
			node.appendChild(n);
		}
	}

	public void encodeContent(Node n, TaggedText text, ArrayList<ValidationError> errors) {
		ArrayList<Node> nodes;
		try {
			nodes = text.toNodes(n.getOwnerDocument());
		}
		catch (EncodeException ex) {
			nodes = text.onlyTextToNodes(n.getOwnerDocument());
			errors.add(new ValidationError(this, ex.getMessage()));
		}
		replaceChildren(n, nodes);
	}

	void removeChild(Node n) {
		try {
			node.removeChild(n);
		}
		catch (DOMException ex) {
			if (ex.code != DOMException.NOT_FOUND_ERR) {
				throw ex;
			}
		}
	}

	public void encode(ArrayList<ValidationError> errors, boolean skipInitialSegments) {
		node.setAttribute(ATTRIBUTE_STATE, state.toString());
		encodeContent(sourceNode, sourceText, errors);

		boolean skipInitial = skipInitialSegments && !staged;
		staged = false;
		boolean skipNode = targetText.getContent().isEmpty() || (skipInitial && (state == State.INITIAL));
		if (skipNode) {
			removeChild(targetNode);
		}
		else {
			encodeContent(targetNode, targetText, errors);
			node.appendChild(targetNode);
		}
	}
}
