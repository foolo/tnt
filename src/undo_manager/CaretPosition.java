package undo_manager;

public class CaretPosition {

	public enum Column {
		SOURCE, TARGET
	}

	private int item_index;
	private Column column;
	private int text_position;

	public CaretPosition(int item_index, Column column, int text_position) {
		this.item_index = item_index;
		this.text_position = text_position;
		this.column = column;
	}

	public CaretPosition copy() {
		return new CaretPosition(item_index, column, text_position);
	}
	
	int getItemIndex() {
		return item_index;
	}
	
}
