package xliff_model;

import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.Log;
import util.XmlUtil;

public class SegmentTag {

	private TaggedText sourceText;
	private TaggedText targetText;
	private final Element node;
	private final Node sourceNode;
	private final Node targetNode;

	public SegmentTag(Element node, UnitTag parent) throws ParseException {
		this.node = node;
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

	static void replaceChildren(Node node, ArrayList<Node> newNodes) {
		XmlUtil.clearChildren(node);
		for (Node n : newNodes) {
			node.appendChild(n);
		}
	}

	public void save(ArrayList<SegmentError> errors) {
		ArrayList<Node> sourceNodes;
		try {
			sourceNodes = sourceText.toNodes(node.getOwnerDocument());
		}
		catch (SaveException ex) {
			System.out.println(ex.getMessage());
			sourceNodes = new ArrayList<>();
			errors.add(new SegmentError(this, ex.getMessage()));
		}
		replaceChildren(sourceNode, sourceNodes);

		ArrayList<Node> targetNodes;
		try {
			targetNodes = targetText.toNodes(node.getOwnerDocument());
		}
		catch (SaveException ex) {
			targetNodes = new ArrayList<>();
			errors.add(new SegmentError(this, ex.getMessage()));
		}
		replaceChildren(targetNode, targetNodes);
	}
}
