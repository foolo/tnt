package xliff_model;

import java.util.ArrayList;
import org.w3c.dom.Node;
import undo_manager.CaretPosition;
import util.Log;
import util.NodeArray;

public class UnitTag implements Item {

	ArrayList<SegmentTag> segments = new ArrayList<>();

	UnitTag(Node node) throws InvalidXliffFormatException {
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				//System.out.println("Skip non-element child node for <unit>");
				continue;
			}
			if (n.getNodeName().equals("segment")) {
				SegmentTag segmentObj = new SegmentTag(n, this);
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
			segments.add(new SegmentTag(s, this));
		}
	}

	public CaretPosition split(CaretPosition pos) {
		SegmentTag segmentTag = pos.getSegmentTag();
		int index = segments.indexOf(segmentTag);
		if (index < 0) {
			System.out.println("segment tag not found in unit");
			return null;
		}
		SegmentTag newSegmentTag = segmentTag.split(pos);
		segments.add(index + 1, newSegmentTag);
		return new CaretPosition(pos.getItemIndex(), CaretPosition.Column.SOURCE, newSegmentTag.getSourceText().length(), newSegmentTag);
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
