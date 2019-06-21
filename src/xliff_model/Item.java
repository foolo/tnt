package xliff_model;

import java.util.ArrayList;

public interface Item {

	Item copy();

	void encode(ArrayList<SegmentError> errors);

	public ArrayList<UnitTag> getUnitsArray();
}
