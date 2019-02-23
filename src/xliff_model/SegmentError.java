package xliff_model;

public class SegmentError {

	private final SegmentTag segmentTag;
	private final String message;
	private int index = -1;

	public SegmentError(SegmentTag segmentTag, String message) {
		this.segmentTag = segmentTag;
		this.message = message;
	}

	void setIndex(int index) {
		this.index = index;
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	public int getIndex() {
		return index;
	}
}
