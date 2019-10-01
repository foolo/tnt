package undo_manager;

public interface UndoEventListener {

	void notify_undo(UndoableModel model, CaretPosition newEditingPosition);

	public void updateProgress(UndoableModel model);
}
