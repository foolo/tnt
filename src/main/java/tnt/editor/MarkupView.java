package tnt.editor;

import javax.swing.JTextPane;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;

public class MarkupView extends JTextPane {

	private SegmentView segmentView;

	public MarkupView() {
	}

	public MarkupView(SegmentView segmentView) {
		this.segmentView = segmentView;
		init();
	}

	private void init() {
		((DefaultHighlighter) getHighlighter()).setDrawsLayeredHighlights(false);
	}

	public SegmentView getSegmentView() {
		return segmentView;
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
}
