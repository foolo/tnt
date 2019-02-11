package undo_manager;

import java.util.Stack;

public class UndoManager {

	Stack<UndoableState> undoBuffer = new Stack<>();

	UndoableState currentState;

	UndoEventListener listener;

	public UndoManager(UndoableState currentState, UndoEventListener listener) {
		this.currentState = currentState;
		this.listener = listener;
		//this.listener.notify_undo();
		push_snapshot();
	}

	final void push_snapshot() {
		System.out.println("PUSH");
		undoBuffer.push(currentState.copy());
		currentState.getModel().clearModified();
		System.out.println("is mod: " + currentState.getModel().isModified());
		print_buffer();
	}

	public final void save() {
		System.out.println("SAVE " + currentState.getPosition().getItemIndex());
		if (currentState.getModel().isModified()) {
			push_snapshot();
		}
	}

	void print_buffer() {
		for (UndoableState s : undoBuffer) {
			System.out.println("> " + s.getPosition().getItemIndex());
		}
	}
	
	public void undo() {
		System.out.println("UNDO");
		print_buffer();
		if (undoBuffer.empty()) {
			System.out.println("undo buffer empty");
			return;
		}
		//listener.stop_editing();

		int newEditingIndex = -1;
		
		if (currentState.getModel().isModified() == false) {
			System.out.println("nothing is modified, jump to previous index");
			newEditingIndex = undoBuffer.peek().getPosition().getItemIndex();
			if (undoBuffer.size() >= 2) {
				undoBuffer.pop();
			}
		}
		else {
			System.out.println("undoing current state");
			newEditingIndex = currentState.getPosition().getItemIndex();
		}
		System.out.println("new item index: " + newEditingIndex);
		currentState = undoBuffer.peek().copy();
		listener.notify_undo(newEditingIndex);
	}
	
	public UndoableState getCurrentState() {
		return currentState;
	}
}
