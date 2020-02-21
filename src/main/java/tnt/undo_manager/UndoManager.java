package tnt.undo_manager;

import java.util.Stack;

public class UndoManager {

	private final Stack<UndoableState> undoBuffer = new Stack<>();
	private final Stack<UndoableState> redoBuffer = new Stack<>();
	private UndoableState currentState;
	private UndoableState savedState;

	public UndoManager() {
	}

	public void initialize(UndoableState currentState) {
		this.currentState = currentState;
		push_snapshot();
		markSaved();
	}

	final void push_snapshot() {
		undoBuffer.push(currentState.copy());
		currentState.clearModified();
		redoBuffer.clear();
	}

	public final void markSnapshot() {
		if (currentState.isModified()) {
			push_snapshot();
		}
	}

	public void markSaved() {
		markSnapshot();
		savedState = undoBuffer.peek();
	}

	public boolean isModified() {
		return (savedState != undoBuffer.peek()) || currentState.isModified();
	}

	private void setNewState(UndoPosition newEditingPosition) {
		currentState = new UndoableState(undoBuffer.peek().getModel().copy(), this);
	}

	public void undo() {
		if (undoBuffer.empty()) {
			return;
		}

		UndoPosition newEditingPosition = null;

		if (currentState.isModified() == false) {
			newEditingPosition = undoBuffer.peek().getStartPosition();
			if (undoBuffer.size() >= 2) {
				UndoableState st = undoBuffer.pop();
				redoBuffer.add(st);
			}
		}
		else {
			newEditingPosition = currentState.getStartPosition();
			redoBuffer.clear();
			redoBuffer.add(currentState);
		}
		setNewState(newEditingPosition);
	}

	public void redo() {
		if (redoBuffer.empty()) {
			return;
		}
		if (currentState.isModified()) {
			return;
		}
		UndoableState st = redoBuffer.pop();
		undoBuffer.add(st);
		currentState = new UndoableState(undoBuffer.peek().getModel().copy(), this);
	}

	public UndoableState getCurrentState() {
		return currentState;
	}
}
