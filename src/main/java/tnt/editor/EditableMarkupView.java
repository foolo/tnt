package tnt.editor;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import tnt.util.Log;
import tnt.xliff_model.Tag;
import javax.swing.text.DocumentFilter;
import tnt.xliff_model.TaggedText;
import tnt.xliff_model.TaggedTextContent;
import tnt.xliff_model.Text;

public class EditableMarkupView extends MarkupView {

	private class TargetDocumentListener implements DocumentListener {

		boolean enabled = true;

		void update(int caretPosition1, int caretPosition2) {
			getSegmentView().update(caretPosition1, caretPosition2);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			if (enabled) {
				update(e.getOffset(), e.getOffset() + e.getLength());
			}
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (enabled) {
				update(e.getOffset() + e.getLength(), e.getOffset());
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	};

	private static class MyDocumentFilter extends DocumentFilter {

		enum InputState {
			UNDEFINED, INPUT_NON_SPACE, INPUT_STRING, INPUT_SPACE, REMOVE
		}
		InputState lastState = InputState.UNDEFINED;

		boolean isSnapshotNeeded(InputState oldState, InputState newState) {
			if (newState == oldState) {
				return false;
			}
			if (newState == InputState.INPUT_NON_SPACE && oldState == InputState.INPUT_SPACE) {
				return false;
			}
			return true;
		}

		void newInputState(InputState newState) {
			if (isSnapshotNeeded(lastState, newState)) {
				Session.getUndoManager().markSnapshot();
			}
			lastState = newState;
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String s, AttributeSet attrs) throws BadLocationException {
			s = s.replaceAll("\\R|\t", "");
			if (s.codePoints().count() == 1) {
				newInputState(Character.isSpaceChar(s.codePointAt(0)) ? InputState.INPUT_SPACE : InputState.INPUT_NON_SPACE);
			}
			if (s.codePoints().count() > 1) {
				newInputState(InputState.INPUT_STRING);
			}
			super.replace(fb, offset, length, s, attrs);
		}

		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
			newInputState(InputState.INPUT_STRING);
			super.insertString(fb, offset, string, attr);
		}

		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
			newInputState(InputState.REMOVE);
			super.remove(fb, offset, length);
		}
	}

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

	void addDocumentFilter() {
		((AbstractDocument) getDocument()).setDocumentFilter(new MyDocumentFilter());
	}

	public TaggedText getTaggedText() {
		StyledDocument doc = getStyledDocument();
		String segmentInteralId = getSegmentView().getSegmentTag().getInternalId();
		return new TaggedText(getTaggedText(0, doc.getLength(), doc), segmentInteralId);
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

	public void pasteTaggedText(TaggedText t, boolean includeTags) {
		int caretPosition1 = getCaretPosition();
		documentListener.enabled = false;
		removeSelection();
		for (TaggedTextContent c : t.getContent()) {
			if (c instanceof Text) {
				String text = ((Text) c).getContent();
				insertText(getCaretPosition(), text);
			}
			else if (c instanceof Tag) {
				if (includeTags) {
					insertTag((Tag) c);
				}
			}
			else {
				Log.warn("insertTaggedText: unexpected instance: " + c.getClass().getName());
			}
		}
		documentListener.enabled = true;
		int caretPosition2 = getCaretPosition();
		getSegmentView().update(caretPosition1, caretPosition2);
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
		documentListener.update(0, getDocument().getLength());
	}

	public void updateTaggedText(TaggedText t) {
		documentListener.enabled = false;
		setTaggedText(t);
		documentListener.enabled = true;
	}
}
