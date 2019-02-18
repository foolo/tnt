package undo_manager;

public class UndoableState {

	UndoableModel model;
	private CaretPosition startPosition = null;
	private CaretPosition endPosition = null;
	boolean modified = false;
	UndoManager undoManager;

	public UndoableState(UndoableModel model, UndoManager undoManager) {
		this.model = model;
		this.undoManager = undoManager;
	}
	public UndoableState(UndoableModel model, CaretPosition startPosition, CaretPosition endPosition, UndoManager undoManager) {
		this.model = model;
		this.startPosition = startPosition;
		this.endPosition = startPosition;
		this.undoManager = undoManager;
	}

	public UndoableModel getModel() {
		return model;
	}

	public UndoableState copy() {
		return new UndoableState(model.copy(), startPosition, endPosition, undoManager);
	}

	public void setModified(CaretPosition position) {
		if (!modified) {
			startPosition = undoManager.getCaretPosition();
			modified = true;
		}
		this.endPosition = position;
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
