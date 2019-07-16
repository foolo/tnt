package editor;

import java.io.File;
import org.w3c.dom.Document;
import undo_manager.CaretPosition;
import undo_manager.UndoEventListener;
import undo_manager.UndoManager;
import undo_manager.UndoableState;
import util.Log;
import util.XmlUtil;
import xliff_model.XliffTag;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;
import xliff_model.exceptions.XliffVersionException;

public class Session {

	private static Session session;

	public static void newSession(File f, UndoEventListener undoEventListener) throws LoadException {
		XliffTag xliffTag = load_xliff(f);
		session = new Session(xliffTag, undoEventListener);
	}

	public static Session getInstance() {
		return session;
	}

	private final UndoManager undoManager;

	public static UndoManager getUndoManager() {
		return session.undoManager;
	}

	static XliffTag load_xliff(File f) throws LoadException {
		XliffTag xliffTag = null;
		try {
			Document doc = XmlUtil.read_xml(f);
			xliffTag = new XliffTag(doc, f);
		}
		catch (LoadException ex) {
			throw new LoadException("Could not open file\n" + ex.getMessage());
		}
		catch (XliffVersionException ex) {
			throw new LoadException("Could not open " + f + "\n" + ex.getMessage());
		}
		catch (ParseException ex) {
			Log.debug("load_file: " + ex.toString());
			throw new LoadException("Could not open " + f + "\nUnrecogized format");
		}
		return xliffTag;
	}

	public Session(XliffTag xliffTag, UndoEventListener undoEventListener) {
		undoManager = new UndoManager();
		CaretPosition pos = new CaretPosition(null, CaretPosition.Column.TARGET, 0);
		undoManager.initialize(new UndoableState(xliffTag, pos, pos, undoManager), undoEventListener);
	}
}
