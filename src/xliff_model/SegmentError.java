package xliff_model;

public class SegmentError {

	private final SegmentTag segmentTag;
	private final String message;

	public SegmentError(SegmentTag segmentTag, String message) {
		this.segmentTag = segmentTag;
		this.message = message;
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}
}
