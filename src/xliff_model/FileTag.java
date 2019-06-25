package xliff_model;

import xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.Log;
import util.NodeArray;
import undo_manager.UndoableModel;

public class FileTag implements UndoableModel {

	private final ArrayList<Item> items = new ArrayList<>();
	private ArrayList<UnitTag> unitsArray = null;
	private String originalFilePath;
	private String id;

	public FileTag(Node node) throws ParseException {
		originalFilePath = ((Element) node).getAttribute("original");
		id = ((Element) node).getAttribute("id");
		if (id.isEmpty()) {
			throw new ParseException("Mandatory attribute 'id' missing or empty in <file>");
		}
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
				Log.debug("FileTag: unhandled: " + n.getNodeName(), node);
			}
		}
	}

	public FileTag(FileTag ft) {
		for (Item i : ft.items) {
			items.add(i.copy());
		}
		originalFilePath = ft.originalFilePath;
		id = ft.id;
	}

	public String getId() {
		return id;
	}

	public String getOriginalFilePath() {
		return originalFilePath;
	}

	public String getAlias() {
		if (originalFilePath.isEmpty()) {
			return id;
		}
		return originalFilePath;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public ArrayList<UnitTag> getUnitsArray() {
		if (unitsArray == null) {
			unitsArray = new ArrayList<>();
			for (Item i : items) {
				unitsArray.addAll(i.getUnitsArray());
			}
		}
		return unitsArray;
	}

	@Override
	public UndoableModel copy() {
		return new FileTag(this);
	}

	void encode(ArrayList<SegmentError> errors, boolean skipInitialSegments) {
		for (Item i : items) {
			i.encode(errors, skipInitialSegments);
		}
	}
}
