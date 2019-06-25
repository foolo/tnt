package xliff_model;

import java.util.ArrayList;

public interface Item {

	Item copy();

	void encode(ArrayList<SegmentError> errors, boolean skipInitialSegments);

	public ArrayList<UnitTag> getUnitsArray();
}
