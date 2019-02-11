package undo_manager;

public interface UndoEventListener {

	void notify_undo(int new_editing_index);

	void stop_editing();
}
