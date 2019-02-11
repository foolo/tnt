package undo_manager;

public interface UndoEventListener {

	void notify_undo(CaretPosition newEditingPosition);

	void stop_editing();
}
