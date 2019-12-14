package tnt.undo_manager;

public interface UndoEventListener {

	void notify_undo(UndoableModel model, UndoPosition newEditingPosition);

	public void updateProgress(UndoableModel model);
}
