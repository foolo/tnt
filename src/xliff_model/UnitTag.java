package xliff_model;

import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import util.Log;
import util.NodeArray;

public class UnitTag implements Item {

	private ArrayList<SegmentTag> segments = new ArrayList<>();
	private Node node;
	private final String id;

	UnitTag(Node node) throws ParseException {
		this.node = node;
		id = ((Element) node).getAttribute("id");
		if (id.isEmpty()) {
			throw new ParseException("Mandatory attribute 'id' missing or empty in <unit>");
		}
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (n.getNodeName().equals("segment")) {
				SegmentTag segmentObj = new SegmentTag((Element) n, this);
				segments.add(segmentObj);
			}
			else {
				Log.debug("UnitTag: unhandled: " + n.getNodeName(), node);
			}
		}
		if (segments.isEmpty()) {
			throw new ParseException("No <segment> nodes found in <unit>");
		}
	}

	UnitTag(UnitTag ut) {
		node = ut.node;
		id = ut.id;
		for (SegmentTag s : ut.segments) {
			segments.add(new SegmentTag(s));
		}
	}

	public String getId() {
		return id;
	}

	@Override
	public ArrayList<SegmentTag> getSegmentsArray() {
		return segments;
	}

	@Override
	public Item copy() {
		return new UnitTag(this);
	}

	Node findNode(Node n) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			return childNodes.item(i);
		}
		return null;
	}

	void insertAfter(Node newNode, Node refNode) {
		node.insertBefore(newNode, refNode.getNextSibling());
	}

	@Override
	public void encode(ArrayList<ValidationError> errors, boolean skipInitialSegments) {
		for (SegmentTag st : segments) {
			st.encode(errors, skipInitialSegments);
		}
		Node lastFoundNode = null;
		for (int i = 0; i < segments.size(); i++) {
			SegmentTag st = segments.get(i);
			Node n = st.getNode();
			Node foundNode = findNode(n);
			if (foundNode != null) {
				lastFoundNode = foundNode;
			}
			else {
				insertAfter(n, lastFoundNode);
			}
		}
		// todo remove removed segments
	}
}
