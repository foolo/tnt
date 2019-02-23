package xliff_model;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import undo_manager.CaretPosition;
import util.XmlUtil;

public class SegmentTag {

	private TaggedText source;
	private TaggedText target;
	private final UnitTag parent;
	private final Element node;
	private final Node sourceNode;
	private final Node targetNode;

	public SegmentTag(Element node, UnitTag parent) throws InvalidXliffFormatException {
		this.parent = parent;
		this.node = node;
		sourceNode = XmlUtil.getChildByName(node, "source");
		if (sourceNode == null) {
			throw new InvalidXliffFormatException("Mandatory <source> missing in <segment>");
		}
		Node tn = XmlUtil.getChildByName(node, "target");
		if (tn == null) {
			tn = sourceNode.cloneNode(false);
			XmlUtil.clearChildren(tn);
			node.appendChild(tn);
			tn = node.getOwnerDocument().renameNode(tn, null, "target");
		}
		targetNode = tn;
		source = new TaggedText(sourceNode);
		target = new TaggedText(targetNode);
	}

	public SegmentTag(SegmentTag st, UnitTag parent) {
		this.source = st.source;
		this.target = st.target;
		this.parent = parent;
		this.node = st.node;
		this.sourceNode = st.sourceNode;
		this.targetNode = st.targetNode;
	}

	public SegmentTag(TaggedText source, TaggedText target, Element node, SegmentTag st) {
		this.source = source;
		this.target = target;
		this.parent = st.parent;
		this.node = node;
		this.sourceNode = st.sourceNode;
		this.targetNode = st.targetNode;
	}

	public TaggedText getSourceText() {
		return source;
	}

	public TaggedText getTargetText() {
		if (target != null) {
			return target;
		}
		return new TaggedText(new ArrayList<>());
	}

	public UnitTag getParent() {
		return parent;
	}

	public Node getNode() {
		return node;
	}

	public void setSourceText(TaggedText s) {
		source = s;
	}

	public void setTargetText(TaggedText s) {
		target = s;
	}

	public SegmentTag split(CaretPosition pos) {
		ArrayList<TaggedTextContent> src0 = new ArrayList<>(source.getContent().subList(0, pos.getTextPosition()));
		ArrayList<TaggedTextContent> src1 = new ArrayList<>(source.getContent().subList(pos.getTextPosition(), source.getContent().size()));
		source = new TaggedText(src0);
		return new SegmentTag(new TaggedText(src1), new TaggedText(new ArrayList<>()), (Element) node.cloneNode(true), this);
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
			sourceNodes = source.toNodes(node.getOwnerDocument());
			System.out.println("set source: " + source);
		}
		catch (SaveException ex) {
			System.out.println(ex.getMessage());
			sourceNodes = new ArrayList<>();
			errors.add(new SegmentError(this, ex.getMessage()));
		}
		replaceChildren(sourceNode, sourceNodes);

		ArrayList<Node> targetNodes;
		try {
			targetNodes = target.toNodes(node.getOwnerDocument());
			System.out.println("set target: " + target);
		}
		catch (SaveException ex) {
			System.out.println(ex.getMessage());
			targetNodes = new ArrayList<>();
			errors.add(new SegmentError(this, ex.getMessage()));
		}
		replaceChildren(targetNode, targetNodes);
	}
}
