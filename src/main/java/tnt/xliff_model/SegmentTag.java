package tnt.xliff_model;

import tnt.editor.Session;
import tnt.xliff_model.exceptions.EncodeException;
import tnt.xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tnt.util.RegexUtil;
import tnt.util.Settings;
import tnt.util.XmlUtil;

public class SegmentTag {

	private TaggedText sourceText;
	private TaggedText targetText;
	private final Element node;
	private final Node sourceNode;
	private final Node targetNode;
	private State state;
	private final String id;
	private final String sourceLeadingWhitepace;
	private final Integer sourceWordCount;

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

		public String toShortString() {
			return new String(Character.toChars(name().codePointAt(0)));
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
		id = Session.generateSegmentId();
		sourceText = new TaggedText(sourceNode, id);
		targetText = new TaggedText(targetNode, id);
		if (Settings.getShowWhitespace()) {
			sourceLeadingWhitepace = "";
		}
		else {
			sourceLeadingWhitepace = sourceText.trim();
			targetText.trim();
		}
		sourceWordCount = RegexUtil.countWords(sourceText.getTextContent());
	}

	public SegmentTag(SegmentTag st) {
		this.sourceText = st.sourceText;
		this.targetText = st.targetText;
		this.node = st.node;
		this.sourceNode = st.sourceNode;
		this.targetNode = st.targetNode;
		this.state = st.state;
		this.id = st.id;
		this.sourceLeadingWhitepace = st.sourceLeadingWhitepace;
		this.sourceWordCount = st.sourceWordCount;
	}

	public TaggedText getSourceText() {
		return sourceText;
	}

	public TaggedText getTargetText() {
		if (targetText != null) {
			return targetText;
		}
		return new TaggedText(new ArrayList<>(), id);
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

	public String testEncodeTarget() {
		return encodeContent(targetNode, targetText);
	}

	public boolean setState(State state) {
		if (state != this.state) {
			this.state = state;
			return true;
		}
		return false;
	}

	static void replaceChildren(Node node, ArrayList<Node> newNodes) {
		XmlUtil.clearChildren(node);
		for (Node n : newNodes) {
			node.appendChild(n);
		}
	}

	public String encodeContent(Node n, TaggedText text) {
		String errMsg = null;
		ArrayList<Node> nodes;
		try {
			nodes = text.toNodes(n.getOwnerDocument(), sourceLeadingWhitepace);
		}
		catch (EncodeException ex) {
			nodes = text.onlyTextToNodes(n.getOwnerDocument());
			errMsg = ex.getMessage();
		}
		replaceChildren(n, nodes);
		return errMsg;
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

	static Element findFileParent(Node n) {
		if (n.getNodeName().equals("file")) {
			return (Element) n;
		}
		return findFileParent(n.getParentNode());
	}

	String getPath() {
		Element unitNode = (Element) getNode().getParentNode();
		String unitId = unitNode.getAttribute("id");
		Element fileNode = findFileParent(unitNode.getParentNode());
		String fileId = fileNode.getAttribute("id");
		return "file='" + fileId + "', unit='" + unitId + "', segment='" + getId();
	}

	private void encodeContent(Node n, TaggedText text, ArrayList<String> errors) {
		String errMsg = encodeContent(n, text);
		if (errMsg != null) {
			errors.add(errMsg + " (" + getPath() + ")");
		}
	}

	public void encode(ArrayList<String> errors, boolean skipInitialSegments) {
		node.setAttribute(ATTRIBUTE_STATE, state.toString());
		encodeContent(sourceNode, sourceText, errors);
		boolean skipNode = targetText.getContent().isEmpty() || (skipInitialSegments && (state == State.INITIAL));
		if (skipNode) {
			removeChild(targetNode);
		}
		else {
			encodeContent(targetNode, targetText, errors);
			node.appendChild(targetNode);
		}
	}

	int countSourceWords() {
		return sourceWordCount;
	}
}
