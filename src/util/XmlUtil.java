package util;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;

public class XmlUtil {

	public static Document read_xml(File file) throws LoadException, ParseException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			Document doc = dbf.newDocumentBuilder().parse(file);
			doc.getDocumentElement().normalize();
			return doc;
		}
		catch (SAXException | ParserConfigurationException ex) {
			throw new ParseException(ex.getMessage());
		}
		catch (IOException ex) {
			throw new LoadException(ex.getMessage());
		}
	}

	public static StreamResult write_xml(Document doc, StreamResult result) {
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.INDENT, "no");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			DOMSource domSource = new DOMSource(doc);
			tr.transform(domSource, result);
			return result;
		}
		catch (TransformerConfigurationException ex) {
			Log.err(ex.toString());
		}
		catch (TransformerException ex) {
			//Log.err(ex.toString());
			System.err.println(ex.toString());
		}
		return null;
	}

	public static String getPath(Node node) {
		Node parent = node.getParentNode();
		if (parent == null) {
			return node.getNodeName();
		}
		String index = ((Element) node).getAttribute("id");
		return getPath(parent) + "." + node.getNodeName() + "[" + index + "]";
	}

	public static void clearChildren(Node node) {
		while (node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
	}

	public static Node getChildByName(Element node, String name) {
		NodeList nl = node.getElementsByTagName(name);
		if (nl.getLength() == 0) {
			return null;
		}
		return nl.item(0);
	}
}
