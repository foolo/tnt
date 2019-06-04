package undo_manager;

import java.util.Stack;

public class UndoManager {

	private final Stack<UndoableState> undoBuffer = new Stack<>();
	private UndoableState currentState;
	private UndoEventListener listener;
	private CaretPosition caretPosition;

	public UndoManager() {
	}

	public void initialize(UndoableState currentState, UndoEventListener listener) {
		this.currentState = currentState;
		this.listener = listener;
		//this.listener.notify_undo();
		push_snapshot();
	}

	final void push_snapshot() {
		undoBuffer.push(currentState.copy());
		currentState.clearModified();
	}

	public final void save() {
		if (currentState.isModified()) {
			push_snapshot();
		}
	}

	public void undo() {
		if (undoBuffer.empty()) {
			return;
		}

		CaretPosition newEditingPosition = null;

		if (currentState.isModified() == false) {
			newEditingPosition = undoBuffer.peek().getStartPosition();
			if (undoBuffer.size() >= 2) {
				undoBuffer.pop();
			}
		}
		else {
			newEditingPosition = currentState.getStartPosition();
		}
		currentState = new UndoableState(undoBuffer.peek().getModel().copy(), this);
		listener.notify_undo(newEditingPosition);
	}

	public UndoableState getCurrentState() {
		return currentState;
	}

	public void setCaretPosition(CaretPosition position) {
		caretPosition = position;
	}

	public CaretPosition getCaretPosition() {
		return caretPosition;
	}
}
