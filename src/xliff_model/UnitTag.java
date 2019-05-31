package xliff_model;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import undo_manager.CaretPosition;
import util.Log;
import util.NodeArray;

public class UnitTag implements Item {

	private ArrayList<SegmentTag> segments = new ArrayList<>();
	private Node node;

	UnitTag(Node node) throws ParseException {
		this.node = node;
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				//System.out.println("Skip non-element child node for <unit>");
				continue;
			}
			if (n.getNodeName().equals("segment")) {
				SegmentTag segmentObj = new SegmentTag((Element) n, this);
				segments.add(segmentObj);
			}
			else {
				Log.debug("unhandled: " + n.getNodeName(), node);
			}
		}
		if (segments.isEmpty()) {
			throw new ParseException("No <segment> nodes found in <unit>");
		}
	}

	UnitTag(UnitTag ut) {
		node = ut.node;
		for (SegmentTag s : ut.segments) {
			segments.add(new SegmentTag(s, this));
		}
	}

	public CaretPosition split(CaretPosition pos, SegmentTag segmentTag) {
		int index = segments.indexOf(segmentTag);
		if (index < 0) {
			System.out.println("segment tag not found in unit");
			return null;
		}
		SegmentTag newSegmentTag = segmentTag.split(pos);
		segments.add(index + 1, newSegmentTag);
		return new CaretPosition(pos.getItemIndex(), CaretPosition.Column.SOURCE, newSegmentTag.getSourceText().getContent().size());
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
	public void save(ArrayList<SegmentError> errors) {
		for (SegmentTag st : segments) {
			st.save(errors);
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
