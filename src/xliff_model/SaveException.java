package xliff_model;

public class SaveException extends Exception {

	private final SegmentTag segmentTag;

	SaveException(String msg) {
		super(msg);
		segmentTag = null;
	}

	public SaveException(String message, SegmentTag segmentTag) {
		super(message);
		this.segmentTag = segmentTag;
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}
}
