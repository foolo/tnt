package tnt.editor;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import tnt.language.SpellCheck;

public class EditableMarkupView extends JTextPane {

	private class TargetDocumentListener implements DocumentListener {

		void update(int caretPosition1, int caretPosition2) {
			EditableMarkupView.this.update(caretPosition1, caretPosition2);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			update(e.getOffset(), e.getOffset() + e.getLength());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update(e.getOffset() + e.getLength(), e.getOffset());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	};

	private TargetDocumentListener documentListener;

	public EditableMarkupView() {
	}

	public void updateSegmentTag(String s) {
		updateTaggedText(s);
		applySpellcheck();
	}

	void update(int caretPosition1, int caretPosition2) {
		applySpellcheck();
	}

	void applySpellcheck() {
		SwingUtilities.invokeLater(() -> {
			SpellCheck.spellCheck(this);
		});
	}

	@Override
	public void updateUI() {
		setCaret(null);
		super.updateUI();
		Caret oldCaret = getCaret();
		Caret caret = new DefaultCaret();
		caret.setBlinkRate(oldCaret.getBlinkRate());
		setCaret(caret);
	}

	void caretUpdate() {
		System.out.println("tnt.editor.SegmentView.updateHeight() " + getPreferredSize());
	}

	void addDocumentListener() {
		documentListener = new TargetDocumentListener();
		getDocument().addDocumentListener(documentListener);
	}

	public void updateTaggedText(String s) {
		setText(s);
		setCaretPosition(0);
	}
}
