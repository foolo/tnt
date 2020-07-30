package tnt.editor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
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

	@Override
	public final void setTransferHandler(TransferHandler newHandler) {
		super.setTransferHandler(newHandler);
	}

	public void setTaggedText(String s) {
		int caretPosition = getCaretPosition();
		setText(s);
		setCaretPosition(Math.min(caretPosition, getText().length()));
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paintComponent(g);
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
