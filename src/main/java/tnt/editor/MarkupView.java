package tnt.editor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.StyledDocument;
import tnt.editor.search.EditorRange;
import tnt.util.Log;

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

	public static String getDocText(StyledDocument doc) {
		String s;
		try {
			return doc.getText(0, doc.getLength());
		}
		catch (BadLocationException ex) {
			Log.err(ex);
			return "";
		}
	}

	public String getPlainText(ArrayList<Integer> plainToTaggedIndexes) {
		StyledDocument doc = getStyledDocument();
		String docText = getDocText(doc);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < doc.getLength();) {
			int codepoint = docText.codePointAt(i);
			sb.appendCodePoint(docText.codePointAt(i));
			plainToTaggedIndexes.add(i);
			i += Character.charCount(codepoint);
		}
		return sb.toString();
	}

	public void setTaggedText(String s) {
		int caretPosition = getCaretPosition();
		setText(s);
		setCaretPosition(Math.min(caretPosition, getText().length()));
	}

	void applyHighlighting(EditorRange range, Highlighter.HighlightPainter highlightPainter) {
		try {
			getHighlighter().addHighlight(range.start, range.end, highlightPainter);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
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
