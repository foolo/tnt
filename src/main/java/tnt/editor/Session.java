package tnt.editor;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import tnt.undo_manager.UndoPosition;
import tnt.undo_manager.UndoEventListener;
import tnt.undo_manager.UndoManager;
import tnt.undo_manager.UndoableState;
import tnt.util.Log;
import tnt.util.XmlUtil;
import tnt.xliff_model.XliffTag;
import tnt.xliff_model.exceptions.LoadException;
import tnt.xliff_model.exceptions.ParseException;

public class Session {

	public static class Properties {

		private String srcLang;
		private String trgLang;
		private boolean modified = false;

		public Properties(Document doc) {
			Element node = doc.getDocumentElement();
			srcLang = node.getAttribute("srcLang");
			trgLang = node.getAttribute("trgLang");
		}

		public String getSrcLang() {
			return srcLang;
		}

		public String getTrgLang() {
			return trgLang;
		}

		public void setSrcLang(String s) {
			if (s.equals(srcLang) == false) {
				srcLang = s;
				modified = true;
			}
		}

		public void setTrgLang(String s) {
			if (s.equals(trgLang) == false) {
				trgLang = s;
				modified = true;
			}
		}

		boolean getModified() {
			return modified;
		}

		public void encode(Document doc) {
			Element node = doc.getDocumentElement();
			node.setAttribute("srcLang", srcLang);
			node.setAttribute("trgLang", trgLang);
		}
	}

	private static Session session;
	private static int idSegmentCounter = 1;

	public static void newSession(File f, UndoEventListener undoEventListener) throws LoadException {
		idSegmentCounter = 1;

		Document doc;
		try {
			doc = XmlUtil.read_xml(f);
		}
		catch (IOException ex) {
			Log.debug("newSession: " + ex);
			throw new LoadException(ex.getMessage());
		}
		catch (ParserConfigurationException | SAXException ex) {
			Log.debug("newSession: " + ex);
			throw new LoadException(ex.toString());
		}

		XliffTag xliffTag;
		try {
			xliffTag = new XliffTag(doc, f);
		}
		catch (ParseException ex) {
			Log.debug("newSession: " + ex);
			throw new LoadException(ex.getMessage());
		}
		session = new Session(xliffTag, undoEventListener);
	}

	public static Session getInstance() {
		return session;
	}

	private final UndoManager undoManager;
	public final Properties properties;

	public static UndoManager getUndoManager() {
		return session.undoManager;
	}

	public static Properties getProperties() {
		return session.properties;
	}

	static boolean isModified() {
		return session.properties.modified || session.undoManager.isModified();
	}

	static void markSaved() {
		session.properties.modified = false;
		session.undoManager.markSaved();
	}

	public static String generateSegmentId() {
		return "" + idSegmentCounter++;
	}

	public Session(XliffTag xliffTag, UndoEventListener undoEventListener) {
		undoManager = new UndoManager();
		UndoPosition pos = new UndoPosition(null, 0);
		undoManager.initialize(new UndoableState(xliffTag, pos, pos, undoManager), undoEventListener);
		properties = new Properties(xliffTag.getDocument());
	}
}
