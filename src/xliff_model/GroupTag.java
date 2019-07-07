package xliff_model;

import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;

public class GroupTag implements Item {

	private ArrayList<Item> items = new ArrayList<>();

	GroupTag(Node node) throws ParseException {
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			if (n.getNodeName().equals("unit")) {
				UnitTag unitObj = new UnitTag(n);
				items.add(unitObj);
			}
			else if (n.getNodeName().equals("group")) {
				GroupTag groupObj = new GroupTag(n);
				items.add(groupObj);
			}
			else {
				Log.debug("GroupTag: unhandled: " + n.getNodeName(), node);
			}
		}
	}

	GroupTag(GroupTag gt) {
		for (Item i : gt.items) {
			items.add(i.copy());
		}
	}

	@Override
	public ArrayList<SegmentTag> getSegmentsArray() {
		ArrayList<SegmentTag> res = new ArrayList<>();
		for (Item i : items) {
			res.addAll(i.getSegmentsArray());
		}
		return res;
	}

	@Override
	public Item copy() {
		return new GroupTag(this);
	}

	@Override
	public void encode(ArrayList<ValidationError> errors, boolean skipInitialSegments) {
		for (Item i : items) {
			i.encode(errors, skipInitialSegments);
		}
	}
}
