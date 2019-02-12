package xliff_model;

import org.w3c.dom.Node;
import undo_manager.CaretPosition;
import util.Log;
import util.NodeArray;

public class SegmentTag {

	String source;
	String target;
	UnitTag parent;

	public SegmentTag(Node node, UnitTag parent) throws InvalidXliffFormatException {
		this.parent = parent;
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

	public void setSourceText(String s) {
		source = s;
	}

	public void setTargetText(String s) {
		target = s;
	}

	public SegmentTag split(CaretPosition pos) {
		SegmentTag newSegmentTag = new SegmentTag(this, this.parent);
		// todo create new node for new segment
		String src0 = source.substring(0, pos.getTextPosition());
		String src1 = source.substring(pos.getTextPosition(), source.length());

		source = src0;
		newSegmentTag.source = src1;
		newSegmentTag.target = "";
		return newSegmentTag;
	}
}
