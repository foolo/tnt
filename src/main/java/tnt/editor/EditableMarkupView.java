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

		boolean enabled = true;

		void update(int caretPosition1, int caretPosition2) {
			EditableMarkupView.this.update(caretPosition1, caretPosition2);
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
	boolean modifiedFlag = false;

	public EditableMarkupView() {
	}

	public void updateSegmentTag(String s) {
		updateTaggedText(s);
		applySpellcheck();
	}

	void update(int caretPosition1, int caretPosition2) {
		modifiedFlag = true;
		applySpellcheck();
	}

	void applySpellcheck() {
		SwingUtilities.invokeLater(() -> {
			SpellCheck.spellCheck(this);
		});
	}

	void updateHeight() {
		System.out.println("tnt.editor.SegmentView.updateHeight() " + getPreferredSize());
	}

	public void setTaggedText(String s) {
		int caretPosition = getCaretPosition();
		setText(s);
		setCaretPosition(Math.min(caretPosition, getText().length()));
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
		if (modifiedFlag) {
			updateHeight();
		}
		modifiedFlag = false;
	}

	void addDocumentListener() {
		documentListener = new TargetDocumentListener();
		getDocument().addDocumentListener(documentListener);
	}

	public void updateTaggedText(String s) {
		documentListener.enabled = false;
		setTaggedText(s);
		documentListener.enabled = true;
	}
}
