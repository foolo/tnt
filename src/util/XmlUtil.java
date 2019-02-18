package util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
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
import org.xml.sax.SAXException;

public class XmlUtil {

	public static Document read_xml(File file) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			ArrayList<String> result = new ArrayList<>();
			Document doc = dbf.newDocumentBuilder().parse(file);
			doc.getDocumentElement().normalize();
			return doc;
		}
		catch (SAXException | ParserConfigurationException | IOException ex) {
			Log.err(ex.toString());
			return null;
		}
	}

	public static StreamResult write_xml(Document doc, StreamResult result) {
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
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

	public static String getNodeString(Node node) {
		try {
			StringWriter writer = new StringWriter();
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			tr.transform(new DOMSource(node), new StreamResult(writer));
			return writer.toString();
		}
		catch (TransformerException e) {
			e.printStackTrace();
		}
		return node.getTextContent();
	}

	public static void clearChildren(Node node) {
		while (node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
	}
}
