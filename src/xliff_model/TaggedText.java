package xliff_model;

import java.util.ArrayList;
import java.util.Arrays;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;
import util.XmlUtil;

public class TaggedText {

	static final ArrayList<String> EMPTY_TAG_NAMES = new ArrayList<>(Arrays.asList(new String[]{"cp", "ph", "sc", "ec", "sm", "em"}));
	static final ArrayList<String> COMPOSITE_TAG_NAMES = new ArrayList<>(Arrays.asList(new String[]{"pc", "mrk"}));

	private final ArrayList<TaggedTextContent> content;

	static Node createRefNode(Node node) {
		Node newNode = node.cloneNode(false);
		XmlUtil.clearChildren(newNode);
		return newNode;
	}

	public static ArrayList<TaggedTextContent> decode(Node node) {
		ArrayList<TaggedTextContent> res = new ArrayList<>();
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() == Node.TEXT_NODE) {
				res.add(new Text(n.getTextContent()));
			}
			else if (n.getNodeType() == Node.ELEMENT_NODE) {
				if (COMPOSITE_TAG_NAMES.contains(n.getNodeName())) {
					res.add(new Tag(createRefNode(n), Tag.Type.START));
					res.addAll(decode(n));
					res.add(new Tag(null, Tag.Type.END));
				}
				else if (EMPTY_TAG_NAMES.contains(n.getNodeName())) {
					res.add(new Tag(createRefNode(n), Tag.Type.EMPTY));
				}
				else {
					Log.debug("unhandled node name: " + n.getNodeName(), node);
				}
			}
			else {
				Log.debug("unhandled node type: " + n.getNodeType(), node);
			}
		}
		return res;
	}

	public TaggedText(Node node) {
		content = decode(node);
	}

	public TaggedText(ArrayList<TaggedTextContent> content) {
		this.content = content;
	}

	public ArrayList<TaggedTextContent> getContent() {
		return content;
	}

	public ArrayList<Node> toNodes(Document document) {
		ArrayList<TaggedTextContent> tmpContent = new ArrayList<>(content);
		tmpContent.add(new Tag(null, Tag.Type.END));
		ArrayList<Node> result = getNodeList(tmpContent, document);
		if (tmpContent.isEmpty() == false) {
			// todo handle invalid text
			System.err.println("invalid tagged text");
			return null;
		}
		return result;
	}

	private static ArrayList<Node> getNodeList(ArrayList<TaggedTextContent> content, Document document) {
		ArrayList<Node> res = new ArrayList<>();
		while (content.isEmpty() == false) {
			TaggedTextContent c = content.get(0);
			content.remove(0); // todo more efficient way?
			if (c instanceof Tag) {
				Tag tag = (Tag) c;
				if (tag.getType() == Tag.Type.EMPTY) {
					res.add(tag.getNode());
				}
				if (tag.getType() == Tag.Type.START) {
					Node node = tag.getNode().cloneNode(false);
					ArrayList<Node> childNodes = getNodeList(content, document);
					for (Node n : childNodes) {
						node.appendChild(n);
					}
					res.add(node);
				}
				else if (tag.getType() == Tag.Type.END) {
					return res;
				}
			}
			else if (c instanceof Text) {
				Text text = (Text) c;
				res.add(document.createTextNode(text.getContent()));
			}
			else {
				System.err.println("unexpected class: " + c.getClass());
			}
		}
		return res;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TaggedTextContent c : content) {
			sb.append(c.toString());
		}
		return sb.toString();
	}
}
