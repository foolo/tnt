package tnt.editor;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import tnt.util.Log;
import tnt.xliff_model.Tag;
import tnt.xliff_model.TaggedText;
import tnt.xliff_model.TaggedTextContent;
import tnt.xliff_model.Text;

public class EditableMarkupView extends MarkupView {

	private class TargetDocumentListener implements DocumentListener {

		DocumentEvent.EventType lastEventType = null;

		boolean enabled = true;

		void update(int caretPosition1, int caretPosition2, DocumentEvent.EventType eventType) {
			if (eventType != lastEventType) {
				Session.getUndoManager().markSnapshot();
			}
			lastEventType = eventType;
			getSegmentView().update(caretPosition1, caretPosition2);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			if (enabled) {
				update(e.getOffset(), e.getOffset() + e.getLength(), e.getType());
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (enabled) {
				update(e.getOffset() + e.getLength(), e.getOffset(), e.getType());
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	};

	private TargetDocumentListener documentListener;

	public EditableMarkupView() {
	}

	EditableMarkupView(SegmentView segmentView) {
		super(segmentView);
	}

	void addDocumentListener() {
		documentListener = new TargetDocumentListener();
		getDocument().addDocumentListener(documentListener);
	}

	public TaggedText getTaggedText() {
		StyledDocument doc = getStyledDocument();
		return getTaggedText(0, doc.getLength(), doc);
	}

	void insertText(int pos, String s) {
		try {
			getDocument().insertString(pos, s, null);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}

	void removeText(int start, int length) {
		try {
			getDocument().remove(start, length);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}

	void removeSelection() {
		int p0 = Math.min(getCaret().getDot(), getCaret().getMark());
		int p1 = Math.max(getCaret().getDot(), getCaret().getMark());
		try {
			getDocument().remove(p0, p1 - p0);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}

	public void insertTaggedText(TaggedText t) {
		removeSelection();
		for (TaggedTextContent c : t.getContent()) {
			if (c instanceof Text) {
				String text = ((Text) c).getContent();
				insertText(getCaretPosition(), text);
			}
			else if (c instanceof Tag) {
				insertTag((Tag) c);
			}
			else {
				Log.warn("insertTaggedText: unexpected instance: " + c.getClass().getName());
			}
		}
	}

	public void replaceTaggedText(int start, int end, String newText) {
		documentListener.enabled = false;
		removeText(start, end - start);
		documentListener.enabled = true;
		insertText(start, newText);
		setCaretPosition(start + newText.length());
	}

	public void replaceTaggedText(TaggedText t) {
		documentListener.enabled = false;
		setTaggedText(t);
		documentListener.enabled = true;
		documentListener.update(0, getDocument().getLength(), DocumentEvent.EventType.INSERT);
	}

	public void updateTaggedText(TaggedText t) {
		documentListener.enabled = false;
		setTaggedText(t);
		documentListener.enabled = true;
	}
}
