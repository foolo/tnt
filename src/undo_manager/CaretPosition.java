package undo_manager;

import xliff_model.SegmentTag;

public class CaretPosition {

	public enum Column {
		SOURCE, TARGET
	}

	private final int item_index;
	private final Column column;
	private final int text_position;
	private final SegmentTag segmentTag;

	public CaretPosition(int item_index, Column column, int text_position, SegmentTag segmentTag) {
		this.item_index = item_index;
		this.text_position = text_position;
		this.column = column;
		this.segmentTag = segmentTag;
	}

	// todo coule be immutable, no need for copy
	public CaretPosition copy() {
		return new CaretPosition(item_index, column, text_position, segmentTag);
	}

	public int getItemIndex() {
		return item_index;
	}

	public Column getColumn() {
		return column;
	}

	public int getTextPosition() {
		return text_position;
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	@Override
	public String toString() {
		return "#" + item_index + ", " + column + ", " + text_position;
	}
}
