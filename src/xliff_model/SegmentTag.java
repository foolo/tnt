package xliff_model;

import org.w3c.dom.Node;
import undo_manager.CaretPosition;
import util.Log;
import util.NodeArray;
import util.XmlUtil;

public class SegmentTag {

	String source;
	String target;
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
				source = n.getTextContent();
			}
			else if (n.getNodeName().equals("target")) {
				if (target != null) {
					throw new InvalidXliffFormatException("Multiple <target> found in <segment>");
				}
				target = n.getTextContent();
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

	public SegmentTag(String source, String target, UnitTag parent, Node node) {
		this.source = source;
		this.target = target;
		this.parent = parent;
		this.node = node;
	}

	public String getSourceText() {
		return source;
	}

	public String getTargetText() {
		if (target != null) {
			return target;
		}
		return "";
	}

	public UnitTag getParent() {
		return parent;
	}

	public Node getNode() {
		return node;
	}

	public void setSourceText(String s) {
		source = s;
	}

	public void setTargetText(String s) {
		target = s;
	}

	public SegmentTag split(CaretPosition pos) {
		String src0 = source.substring(0, pos.getTextPosition());
		String src1 = source.substring(pos.getTextPosition(), source.length());
		source = src0;
		return new SegmentTag(src1, "", this.parent, node.cloneNode(true));
	}

	public void save() {
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeName().equals("source")) {
				System.out.println("set source: " + source);
				
				System.out.println(XmlUtil.getNodeString(n));
				System.out.println(n.getNodeValue());
				System.out.println(n.getTextContent());
				
				n.setTextContent(source);
			}
			else if (n.getNodeName().equals("target")) {
				System.out.println("set target: " + target);
				n.setTextContent(target);
			}
		}
	}
}
