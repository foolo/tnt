package undo_manager;

import java.util.Stack;

public class UndoManager {

	Stack<UndoableState> undoBuffer = new Stack<>();

	UndoableState currentState;

	UndoEventListener listener;

	CaretPosition caretPosition;

	public UndoManager() {
	}

	public void initialize(UndoableState currentState, UndoEventListener listener) {
		this.currentState = currentState;
		this.listener = listener;
		//this.listener.notify_undo();
		push_snapshot();
	}

	final void push_snapshot() {
		System.out.println("PUSH");
		undoBuffer.push(currentState.copy());
		currentState.clearModified();
		System.out.println("is mod: " + currentState.isModified());
		print_buffer();
	}

	public final void save() {
		if (currentState.isModified()) {
			push_snapshot();
		}
	}

	void print_buffer() {
		for (UndoableState s : undoBuffer) {
			System.out.println("> start " + s.getStartPosition() + ", end: " + s.getEndPosition());
		}
	}

	public void undo() {
		System.out.println("UNDO");
		print_buffer();
		if (undoBuffer.empty()) {
			System.out.println("undo buffer empty");
			return;
		}

		CaretPosition newEditingPosition = null;

		if (currentState.isModified() == false) {
			System.out.println("nothing is modified, jump to previous index");
			newEditingPosition = undoBuffer.peek().getStartPosition();
			if (undoBuffer.size() >= 2) {
				undoBuffer.pop();
			}
		}
		else {
			System.out.println("undoing current state");
			newEditingPosition = currentState.getStartPosition();
		}
		System.out.println("new item index: " + newEditingPosition);
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
