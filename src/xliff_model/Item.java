package xliff_model;

import java.util.ArrayList;

public interface Item {

	Item copy();

	void encode(ArrayList<ValidationError> errors, boolean skipInitialSegments);

	ArrayList<SegmentTag> getSegmentsArray();
}
