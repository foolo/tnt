package undo_manager;

public class UndoableState {

	private final UndoableModel model;
	private CaretPosition startPosition = null;
	private CaretPosition endPosition = null;
	private boolean modified = false;
	private final UndoManager undoManager;

	public UndoableState(UndoableModel model, UndoManager undoManager) {
		this.model = model;
		this.undoManager = undoManager;
	}

	public UndoableState(UndoableModel model, CaretPosition startPosition, CaretPosition endPosition, UndoManager undoManager) {
		this.model = model;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.undoManager = undoManager;
	}

	public UndoableModel getModel() {
		return model;
	}

	public UndoableState copy() {
		return new UndoableState(model.copy(), startPosition, endPosition, undoManager);
	}

	public void setModified(CaretPosition position1, CaretPosition position2) {
		if (!modified) {
			startPosition = position1;
			modified = true;
		}
		this.endPosition = position2;
	}

	public void setEndPosition(CaretPosition position) {
		this.endPosition = position;
	}

	public CaretPosition getEndPosition() {
		return endPosition;
	}

	public CaretPosition getStartPosition() {
		return startPosition;
	}

	public boolean isModified() {
		return modified;
	}

	public void clearModified() {
		modified = false;
	}
}
