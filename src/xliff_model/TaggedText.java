package xliff_model;

import xliff_model.exceptions.EncodeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;
import util.XmlUtil;

public class TaggedText {

	private static final ArrayList<String> EMPTY_TAG_NAMES = new ArrayList<>(Arrays.asList(new String[]{"cp", "ph", "sc", "ec", "sm", "em"}));
	private static final ArrayList<String> COMPOSITE_TAG_NAMES = new ArrayList<>(Arrays.asList(new String[]{"pc", "mrk"}));

	private final ArrayList<TaggedTextContent> content;
	private String textContentCache = null;

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
					Log.debug("TaggedText: unhandled node name: " + n.getNodeName(), node);
				}
			}
			else {
				Log.debug("TaggedText: unhandled node type: " + n.getNodeType(), node);
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

	public TaggedText(ArrayList<TaggedTextContent> content, String textContent) {
		this.content = content;
	}

	public ArrayList<TaggedTextContent> getContent() {
		return content;
	}

	public String getTextContent() {
		if (textContentCache != null) {
			return textContentCache;
		}
		StringBuilder sb = new StringBuilder();
		for (TaggedTextContent c : content) {
			if (c instanceof Text) {
				sb.append(((Text) c).getContent());
			}
		}
		textContentCache = sb.toString();
		return textContentCache;
	}

	public ArrayList<Tag> getTags() {
		ArrayList<Tag> res = new ArrayList<>();
		for (TaggedTextContent c : content) {
			if (c instanceof Tag) {
				res.add((Tag) c);
			}
		}
		return res;
	}

	public TaggedText copy() {
		ArrayList<TaggedTextContent> newContent = new ArrayList<>();
		for (TaggedTextContent t : content) {
			newContent.add(t.copy());
		}
		return new TaggedText(newContent, textContentCache);
	}

	public ArrayList<Node> onlyTextToNodes(Document document) {
		ArrayList<Node> result = new ArrayList<>();
		result.add(document.createTextNode(getTextContent()));
		return result;
	}

	public ArrayList<Node> toNodes(Document document, String leadingWhitespace) throws EncodeException {
		ArrayList<TaggedTextContent> tmpContent = new ArrayList<>(content);
		if ((tmpContent.isEmpty() == false) && (tmpContent.get(0) instanceof Text)) {
			Text text = new Text(leadingWhitespace + ((Text) tmpContent.get(0)).getContent());
			tmpContent.set(0, text);
		}
		tmpContent.add(new Tag(null, Tag.Type.END));
		ArrayList<Node> result = getNodeList(tmpContent, document);
		if (tmpContent.isEmpty() == false) {
			throw new EncodeException("Unexpected end tag");
		}
		return result;
	}

	private static ArrayList<Node> getNodeList(ArrayList<TaggedTextContent> content, Document document) throws EncodeException {
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
				Log.warn("getNodeList: unexpected instance: " + c.getClass().getName());
			}
		}
		throw new EncodeException("Missing end tag");
	}

	String trim() {
		if (content.isEmpty()) {
			return "";
		}
		TaggedTextContent start = content.get(0);
		if (start instanceof Text) {
			Text text = (Text) start;
			Pattern p = Pattern.compile("^(\\s*)\\S", Pattern.UNICODE_CHARACTER_CLASS);
			Matcher m = p.matcher(text.getContent());
			if (m.find() && m.groupCount() == 1 && m.start(1) == 0) {
				content.set(0, new Text(text.getContent().substring(m.end(1), text.getContent().length())));
				return m.group(1);
			}
		}
		return "";
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
