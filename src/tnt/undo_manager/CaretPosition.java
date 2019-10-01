package tnt.undo_manager;

import tnt.editor.SegmentView;

public class CaretPosition {

	private final SegmentView segmentView;
	private final int text_position;

	public CaretPosition(SegmentView segmentView, int text_position) {
		this.segmentView = segmentView;
		this.text_position = text_position;
	}

	public SegmentView getSegmentView() {
		return segmentView;
	}

	public int getTextPosition() {
		return text_position;
	}
}
