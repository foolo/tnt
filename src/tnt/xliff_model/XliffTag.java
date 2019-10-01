package xliff_model;

import java.io.File;
import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import undo_manager.UndoableModel;
import util.Log;
import util.NodeArray;
import xliff_model.exceptions.XliffVersionException;

public class XliffTag implements UndoableModel {

	private ArrayList<FileTag> files = new ArrayList<>();
	private final Document document;
	private final String version;
	private final File file;

	public XliffTag(Document doc, File file) throws ParseException {
		this.file = file;
		Element node = doc.getDocumentElement();
		version = node.getAttribute("version");
		if (version.isEmpty()) {
			throw new XliffVersionException("Missing XLIFF version attribute");
		}
		if (version.startsWith("2.") == false) {
			throw new XliffVersionException("Unsupported XLIFF version: " + version);
		}
		this.document = doc;
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (n.getNodeName().equals("file")) {
				FileTag fileObj = new FileTag((Element) n);
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

	public boolean hasMetadata() {
		return document.getElementsByTagName("mda:metadata").getLength() > 0;
	}

	public ArrayList<FileTag> getFiles() {
		return files;
	}

	public File getFile() {
		return file;
	}

	public void encode(ArrayList<String> errors, boolean skipInitialSegments) {
		for (FileTag f : files) {
			f.encode(errors, skipInitialSegments);
		}
	}

	public Document getDocument() {
		return document;
	}

	public String getProgress() {
		int totalWords = 0;
		int completedWords = 0;
		for (FileTag f : files) {
			totalWords += f.countSourceWords(false);
			completedWords += f.countSourceWords(true);
		}
		int percent = 100 * completedWords / totalWords;
		return completedWords + " of " + totalWords + " words completed (" + percent + "%)";
	}

	public XliffTag(XliffTag xt) {
		for (FileTag f : xt.files) {
			files.add(f.copy());
		}
		document = xt.document;
		version = xt.version;
		file = xt.file;
	}

	@Override
	public UndoableModel copy() {
		return new XliffTag(this);
	}
}
