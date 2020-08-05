package com.example;

import java.awt.Color;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

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

	static final SimpleAttributeSet EXAMPLE_ATTRIBUTE_SET = new SimpleAttributeSet();

	static {
		EXAMPLE_ATTRIBUTE_SET.addAttribute("EXAMPLE_ATTRIBUTE_SET", Color.MAGENTA);
	}

	void update() {
		SwingUtilities.invokeLater(() -> {
			MutableAttributeSet inputAttributes = getInputAttributes();
			inputAttributes.removeAttributes(inputAttributes);
			StyledDocument doc = getStyledDocument();
			doc.setCharacterAttributes(0, doc.getLength(), EXAMPLE_ATTRIBUTE_SET, false);
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
