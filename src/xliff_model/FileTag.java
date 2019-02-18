package xliff_model;

import java.util.ArrayList;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;
import undo_manager.UndoableModel;

public class FileTag implements UndoableModel {

	private ArrayList<Item> items = new ArrayList<>();

	public FileTag(Node node) throws InvalidXliffFormatException {
		for (Node n : new NodeArray(node.getChildNodes())) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				//System.out.println("Skip non-element child node for <file>");
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
				Log.debug("unhandled: " + n.getNodeName(), node);
			}
		}
	}

	public FileTag(FileTag ft) {
		for (Item i : ft.items) {
			items.add(i.copy());
		}
	}

	public ArrayList<SegmentTag> getSegmentsArray() {
		ArrayList<SegmentTag> res = new ArrayList<>();
		for (Item i : items) {
			res.addAll(i.getSegmentsArray());
		}
		return res;
	}

	@Override
	public UndoableModel copy() {
		return new FileTag(this);
	}

	void save() {
		for (Item i : items) {
			i.save();
		}
	}
}
