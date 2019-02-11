package xliff_model;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DecodeUtils {

	static void assertTagName(String s, String exp) throws InvalidXliffFormatException {
		if (s.equals(exp) == false) {
			throw new InvalidXliffFormatException("Unexpected tag: " + s + " (expected: " + exp + ")");
		}
	}

	static Element toElementClass(Node node) throws InvalidXliffFormatException {
		if ((node instanceof Element) == false) {
			System.out.println("unexpected parentNode class: " + node.getClass().getName());
			throw new InvalidXliffFormatException();
		}
		return (Element) node;
	}

	static Node getMandatoryNode(Node parentNode, String name) throws InvalidXliffFormatException {
		Element element = toElementClass(parentNode);
		NodeList childNodes = element.getElementsByTagName(name);
		if (childNodes.getLength() != 1) {
			throw new InvalidXliffFormatException("Unexpected number of <" + name + "> child nodes: " + childNodes.getLength() + " (expected: 1)");
		}
		return childNodes.item(0);
	}

	static Node getOptionalNode(Node parentNode, String name) throws InvalidXliffFormatException {
		Element element = toElementClass(parentNode);
		NodeList childNodes = element.getElementsByTagName(name);
		if (childNodes.getLength() > 1) {
			throw new InvalidXliffFormatException("Unexpected number of <" + name + "> child nodes: " + childNodes.getLength() + " (expected: 0 or 1)");
		}
		if (childNodes.getLength() == 0) {
			return null;
		}
		return childNodes.item(0);
	}
}
