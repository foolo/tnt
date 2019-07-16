package editor;

import undo_manager.CaretPosition;
import undo_manager.UndoEventListener;
import undo_manager.UndoManager;
import undo_manager.UndoableState;
import xliff_model.XliffTag;

public class Session {

	private static Session session;

	public static void newSession(XliffTag xliffTag, UndoEventListener undoEventListener) {
		session = new Session(xliffTag, undoEventListener);
	}

	public static Session getInstance() {
		return session;
	}

	private final UndoManager undoManager;

	public static UndoManager getUndoManager() {
		return session.undoManager;
	}

	public Session(XliffTag xliffTag, UndoEventListener undoEventListener) {
		undoManager = new UndoManager();
		CaretPosition pos = new CaretPosition(null, CaretPosition.Column.TARGET, 0);
		undoManager.initialize(new UndoableState(xliffTag, pos, pos, undoManager), undoEventListener);
	}
}
