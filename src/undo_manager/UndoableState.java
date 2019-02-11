package undo_manager;

public class UndoableState {

	UndoableModel model;
	int item_index;
	int text_position;

	public UndoableState(UndoableModel model, int item_index, int text_position) {
		this.model = model;
		this.item_index = item_index;
		this.text_position = text_position;
	}

	public UndoableModel getModel() {
		return model;
	}

	public UndoableState copy() {
		return new UndoableState(model.copy(), item_index, text_position);
	}

	public void setTextPosition(int position) {
		this.text_position = position;
	}

	public void setItemIndex(int item_index) {
		this.item_index = item_index;
	}

	public int getItemIndex() {
		return item_index;
	}

	public int getTextPosition() {
		return text_position;
	}
}
