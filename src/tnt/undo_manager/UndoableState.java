package tnt.undo_manager;

public class UndoableState {

	private final UndoableModel model;
	private UndoPosition startPosition = null;
	private UndoPosition endPosition = null;
	private boolean modified = false;
	private final UndoManager undoManager;

	public UndoableState(UndoableModel model, UndoManager undoManager) {
		this.model = model;
		this.undoManager = undoManager;
	}

	public UndoableState(UndoableModel model, UndoPosition startPosition, UndoPosition endPosition, UndoManager undoManager) {
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

	public void setModified(UndoPosition position1, UndoPosition position2) {
		if (!modified) {
			startPosition = position1;
			modified = true;
		}
		this.endPosition = position2;
	}

	public void setEndPosition(UndoPosition position) {
		this.endPosition = position;
	}

	public UndoPosition getEndPosition() {
		return endPosition;
	}

	public UndoPosition getStartPosition() {
		return startPosition;
	}

	public boolean isModified() {
		return modified;
	}

	public void clearModified() {
		modified = false;
	}
}
