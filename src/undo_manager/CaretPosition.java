package undo_manager;

import editor.SegmentView;

public class CaretPosition {

	public enum Column {
		SOURCE, TARGET
	}

	private final SegmentView segmentView;
	private final Column column;
	private final int text_position;

	public CaretPosition(SegmentView segmentView, Column column, int text_position) {
		this.segmentView = segmentView;
		this.text_position = text_position;
		this.column = column;
	}

	public SegmentView getSegmentView() {
		return segmentView;
	}

	public Column getColumn() {
		return column;
	}

	public int getTextPosition() {
		return text_position;
	}
}
