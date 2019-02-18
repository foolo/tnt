package xliff_model;

import java.util.ArrayList;

public interface Item {

	ArrayList<SegmentTag> getSegmentsArray();

	Item copy();

	void save();
}
