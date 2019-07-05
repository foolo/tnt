package xliff_model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ValidationError {

	public final String message;
	public final ValidationPath path;

	public ValidationError(String message, ValidationPath path) {
		this.message = message;
		this.path = path;
	}

	static Element findFileParent(Node n) {
		if (n == null) {
			return null;
		}
		if (n.getNodeName().equals("file")) {
			return (Element) n;
		}
		return findFileParent(n.getParentNode());
	}

	public ValidationError(SegmentTag segmentTag, String msg) {
		String segmentId = segmentTag.getId();
		Element unitNode = (Element) segmentTag.getNode().getParentNode();
		String unitId = unitNode.getAttribute("id");
		String codeId = "";
		message = msg;
		Element fileNode = findFileParent(unitNode.getParentNode());
		String fileId = fileNode.getAttribute("id");
		path = new ValidationPath(fileId, unitId, segmentId, codeId);
	}

	@Override
	public String toString() {
		if (path == null) {
			return message;
		}
		return message + " (" + path + ")";
	}
}
