package conversion;

import java.io.File;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.NodeArray;
import xliff_model.exceptions.ParseException;

public class Manifest {

	private final Document document;
	private final File file;
	private final String mergeDir;
	private final ArrayList<String> targetFiles = new ArrayList<>();

	public String getMergeDir() {
		return mergeDir;
	}

	public ArrayList<String> getTargetFiles() {
		return targetFiles;
	}

	private static String getAttribute(Element element, String name) {
		String attr = element.getAttribute(name);
		if (attr.isEmpty()) {
			return null;
		}
		return attr;
	}

	public Manifest(Document doc, File file) throws ParseException {
		this.file = file;
		this.document = doc;
		Element rootElement = doc.getDocumentElement();
		mergeDir = rootElement.getAttribute("mergeSubDir");
		if (mergeDir.isEmpty()) {
			throw new ParseException("Missing mergeSubDir attribute");
		}
		for (Node n : new NodeArray(rootElement.getChildNodes())) {
			if ((n.getNodeType() != Node.ELEMENT_NODE) || (n.getNodeName().equals("doc") == false)) {
				continue;
			}
			Element e = (Element) n;
			String relativeTargetPath = e.getAttribute("relativeTargetPath");
			if (relativeTargetPath.isEmpty() == false) {
				targetFiles.add(relativeTargetPath);
			}
		}
	}
}
