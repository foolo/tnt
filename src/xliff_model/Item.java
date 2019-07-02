package xliff_model;

import java.util.ArrayList;
import rainbow.ValidationError;

public interface Item {

	Item copy();

	void encode(ArrayList<ValidationError> errors, boolean skipInitialSegments);

	public ArrayList<UnitTag> getUnitsArray();
}
