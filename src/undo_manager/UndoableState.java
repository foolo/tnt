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
			undoManager.updateModifiedStatus();
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
