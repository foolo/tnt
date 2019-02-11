package undo_manager;

public class UndoableState {

	UndoableModel model;
	private CaretPosition position;
	boolean modified = false;

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

	public void setModified(CaretPosition position) {
		modified = true;
		this.position = position;
	}

	public CaretPosition getPosition() {
		return position;
	}

	public boolean isModified() {
		return modified;
	}

	public void clearModified() {
		modified = false;
	}
}
