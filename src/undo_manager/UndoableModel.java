package undo_manager;

public interface UndoableModel {

	public void clearModified();

	public boolean isModified();

	public UndoableModel copy();
}
