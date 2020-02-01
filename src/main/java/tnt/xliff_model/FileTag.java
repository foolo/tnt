package tnt.xliff_model;

import java.io.File;
import tnt.xliff_model.exceptions.ParseException;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import tnt.util.Log;
import tnt.util.NodeArray;
import tnt.util.StringUtil;

public class FileTag {

	private final ArrayList<Item> items = new ArrayList<>();
	private ArrayList<SegmentTag> segmentArray = null;
	private final String originalFilePath;
	private final String id;

	public FileTag(Element node) throws ParseException {
		originalFilePath = node.getAttribute("original");
		id = node.getAttribute("id");
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

	public String getAlias() {
		if (originalFilePath.isEmpty()) {
			return id;
		}
		String filename = new File(originalFilePath).getName();
		return StringUtil.truncate(filename, 80) + " (" + id + ")";
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public ArrayList<SegmentTag> getSegmentsArray() {
		if (segmentArray == null) {
			segmentArray = new ArrayList<>();
			for (Item i : items) {
				segmentArray.addAll(i.getSegmentsArray());
			}
		}
		return segmentArray;
	}

	public String getOriginalFilePath() {
		return originalFilePath;
	}

	public FileTag copy() {
		return new FileTag(this);
	}

	void encode(ArrayList<String> errors, boolean replaceIncompleteSegments) {
		for (Item i : items) {
			i.encode(errors, replaceIncompleteSegments);
		}
	}

	int countSourceWords(boolean skipInitialSegments) {
		int sum = 0;
		for (Item i : items) {
			sum += i.countSourceWords(skipInitialSegments);
		}
		return sum;
	}
}
