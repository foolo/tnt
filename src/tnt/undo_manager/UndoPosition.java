package tnt.undo_manager;

import tnt.editor.SegmentView;

public class UndoPosition {

	private final SegmentView segmentView;
	private final int text_position;

	public UndoPosition(SegmentView segmentView, int text_position) {
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
