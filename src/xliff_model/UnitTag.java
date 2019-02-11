package xliff_model;

import java.util.ArrayList;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;
import util.XmlUtil;

public class UnitTag implements Item {

	ArrayList<SegmentTag> segments = new ArrayList<>();

	UnitTag(Node node) throws InvalidXliffFormatException {
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				//System.out.println("Skip non-element child node for <unit>");
				continue;
			}
			if (n.getNodeName().equals("segment")) {
				SegmentTag segmentObj = new SegmentTag(n);
				segments.add(segmentObj);
			}
			else {
				Log.debug("unhandled: " + n.getNodeName(), node);
			}
		}
		if (segments.isEmpty()) {
			throw new InvalidXliffFormatException("No <segment> nodes found in <unit>");
		}
	}

	UnitTag(UnitTag ut) {
		for (SegmentTag s : ut.segments) {
			segments.add(new SegmentTag(s));
		}
	}

	@Override
	public ArrayList<SegmentTag> getSegmentsArray() {
		return segments;
	}

	@Override
	public Item copy() {
		return new UnitTag(this);
	}
}
