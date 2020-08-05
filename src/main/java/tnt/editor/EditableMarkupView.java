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

		@Override
		public void insertUpdate(DocumentEvent e) {
			System.out.println("tnt.editor.EditableMarkupView.TargetDocumentListener.insertUpdate()");
			EditableMarkupView.this.update();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			System.out.println("tnt.editor.EditableMarkupView.TargetDocumentListener.removeUpdate()");
			EditableMarkupView.this.update();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	};

	private TargetDocumentListener documentListener;

	public EditableMarkupView() {
	}

	void update() {
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
}
