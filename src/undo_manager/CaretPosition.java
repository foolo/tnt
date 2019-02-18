package undo_manager;

public class CaretPosition {

	public enum Column {
		SOURCE, TARGET
	}

	private final int item_index;
	private final Column column;
	private final int text_position;

	public CaretPosition(int item_index, Column column, int text_position) {
		this.item_index = item_index;
		this.text_position = text_position;
		this.column = column;
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

	@Override
	public String toString() {
		return "#" + item_index + ", " + column + ", " + text_position;
	}
}
