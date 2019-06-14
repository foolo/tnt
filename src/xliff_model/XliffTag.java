package xliff_model;

import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;
import xliff_model.exceptions.XliffVersionException;

public class XliffTag {

	private ArrayList<FileTag> files = new ArrayList<>();
	private Document document;
	String version;

	public XliffTag(Document doc) throws ParseException {
		Element node = doc.getDocumentElement();
		version = node.getAttribute("version");
		if (version.substring(0, 2).equals("2.") == false) {
			throw new XliffVersionException("Unsupported XLIFF version: " + version);
		}
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
				Log.debug("XliffTag: unhandled: " + n.getNodeName(), node);
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
