package xliff_model;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;

public class XliffTag {

	private ArrayList<FileTag> files = new ArrayList<>();
	private Document document;

	public XliffTag(Document doc) throws ParseException {
		Node node = doc.getDocumentElement();
		this.document = doc;
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (n.getNodeName().equals("file")) {
				FileTag fileObj = new FileTag(n);
				files.add(fileObj);
			}
			else {
				Log.debug("unhandled: " + n.getNodeName(), node);
			}
		}
		if (files.isEmpty()) {
			throw new ParseException("No <file> tags found under <xliff>");
		}
	}

	public ArrayList<FileTag> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<FileTag> files) {
		this.files = files;
	}

	public void save(ArrayList<SegmentError> errors) {
		for (FileTag f : files) {
			f.save(errors);
		}
	}

	public Document getDocument() {
		return document;
	}
}
