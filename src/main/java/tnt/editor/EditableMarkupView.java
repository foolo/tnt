package tnt.editor;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import tnt.util.Log;
import tnt.xliff_model.TaggedText;

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

	public void updateTaggedText(TaggedText t) {
		documentListener.enabled = false;
		setTaggedText(t);
		documentListener.enabled = true;
	}
}
