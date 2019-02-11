package xliff_model;

import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;

public class SegmentTag {

	String source;
	String target;
	boolean modified = false;

	public SegmentTag(Node node) throws InvalidXliffFormatException {
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

	public SegmentTag(SegmentTag st) {
		source = st.source;
		target = st.target;
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

	public void setSourceText(String s) {
		source = s;
		modified = true;
	}

	public void setTargetText(String s) {
		target = s;
		modified = true;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	void clearModified() {
		modified = false;
	}
}
