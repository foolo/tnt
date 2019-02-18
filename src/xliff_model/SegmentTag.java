package xliff_model;

import java.util.ArrayList;
import org.w3c.dom.Node;
import undo_manager.CaretPosition;
import util.Log;
import util.NodeArray;
import util.XmlUtil;

public class SegmentTag {

	TaggedText source;
	TaggedText target;
	UnitTag parent;
	private Node node;

	public SegmentTag(Node node, UnitTag parent) throws InvalidXliffFormatException {
		this.parent = parent;
		this.node = node;
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				//System.out.println("Skip non-element child node for <segment>");
				continue;
			}
			if (n.getNodeName().equals("source")) {
				if (source != null) {
					throw new InvalidXliffFormatException("Multiple <source> found in <segment>");
				}
				source = new TaggedText(n);
				System.out.println("SOURCE: " + source);
			}
			else if (n.getNodeName().equals("target")) {
				if (target != null) {
					throw new InvalidXliffFormatException("Multiple <target> found in <segment>");
				}
				target = new TaggedText(n);
				System.out.println("TARGET: " + target);
			}
			else {
				Log.debug("unhandled: " + n.getNodeName(), node);
			}
		}
		if (source == null) {
			throw new InvalidXliffFormatException("Mandatory <source> missing in <segment>");
		}
	}

	public SegmentTag(SegmentTag st, UnitTag parent) {
		source = st.source;
		target = st.target;
		this.parent = parent;
		node = st.node;
	}

	public SegmentTag(TaggedText source, TaggedText target, UnitTag parent, Node node) {
		this.source = source;
		this.target = target;
		this.parent = parent;
		this.node = node;
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
		return new SegmentTag(new TaggedText(src1), new TaggedText(new ArrayList<>()), this.parent, node.cloneNode(true));
	}

	static void replaceChildren(Node node, ArrayList<Node> newNodes) {
		XmlUtil.clearChildren(node);
		for (Node n : newNodes) {
			node.appendChild(n);
		}
	}

	public void save() {
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeName().equals("source")) {
				System.out.println("set source: " + source);
				replaceChildren(n, source.toNodes(node.getOwnerDocument()));
				System.out.println(XmlUtil.getNodeString(n));
				System.out.println(n.getNodeValue());
				System.out.println(n.getTextContent());
			}
			else if (n.getNodeName().equals("target")) {
				System.out.println("set target: " + target);
				replaceChildren(n, target.toNodes(node.getOwnerDocument()));
			}
		}
	}
}
