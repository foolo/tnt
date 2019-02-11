package undo_manager;

public class UndoableState {

	UndoableModel model;
	private CaretPosition position;

	public UndoableState(UndoableModel model, CaretPosition position) {
		this.model = model;
		this.position = position;
	}

	public UndoableModel getModel() {
		return model;
	}

	public UndoableState copy() {
		return new UndoableState(model.copy(), position.copy());
	}

	public void setPosition(CaretPosition position) {
		this.position = position;
	}

	public CaretPosition getPosition() {
		return position;
	}
}
