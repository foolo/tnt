package rainbow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import xliff_model.SegmentTag;
import xliff_model.exceptions.ParseException;

public class ValidationError {

	private final String fileId;
	private final String unitId;
	private final String segmentId;
	private final String codeId;
	private final String message;
	private final Pattern pattern = Pattern.compile(".*Error in <file> id='([^<&']*)', <unit> id='([^<&']*)'.*Code id='([^<&']*)' (.*)", Pattern.DOTALL);

	public ValidationError(String message) throws ParseException {
		System.out.println("DATA" + message);
		Matcher m = pattern.matcher(message);
		if (m.find() == false) {
			throw new ParseException("file/unit/code id not found");
		}
		if (m.groupCount() != 4) {
			throw new ParseException("unexpected group count: " + m.groupCount());
		}
		fileId = m.group(1);
		unitId = m.group(2);
		segmentId = "";
		codeId = m.group(3);
		this.message = m.group(4);
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
		segmentId = segmentTag.getId();
		Element unitNode = (Element) segmentTag.getNode().getParentNode();
		unitId = unitNode.getAttribute("id");
		codeId = "";
		message = msg;
		Element fileNode = findFileParent(unitNode.getParentNode());
		fileId = fileNode.getAttribute("id");
	}

	public String getFileId() {
		return fileId;
	}

	public String getUnitId() {
		return unitId;
	}

	public String getCodeId() {
		return codeId;
	}

	public String getSegmentId() {
		return segmentId;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "file=" + fileId + ", unit=" + unitId + ", tag=" + codeId + ", details: " + message;
	}
}
