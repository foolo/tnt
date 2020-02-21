package tnt.editor;

import tnt.editor.util.SelectWordCaret;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import tnt.xliff_model.Tag;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.StyledDocument;
import tnt.editor.search.EditorRange;
import tnt.util.Log;
import tnt.xliff_model.TaggedTextContent;
import tnt.xliff_model.Text;

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

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		StyledDocument doc = getStyledDocument();
		if (doc != null) {
			for (TagIcon tagIcon : getIcons(doc)) {
				tagIcon.setSize(font.getSize());
			}
		}
	}

	void insertTag(Tag tag) {
		insertIcon(new TagIcon(tag, getFont().getSize(), this));
	}

	static TagIcon getIcon(Element e) {
		Enumeration<?> attrnames = e.getAttributes().getAttributeNames();
		while (attrnames.hasMoreElements()) {
			Object o = attrnames.nextElement();
			if (o.toString().equals("icon")) {
				Object attribute = e.getAttributes().getAttribute(o);
				if (attribute instanceof TagIcon) {
					return (TagIcon) attribute;
				}
			}
		}
		return null;
	}

	boolean isSelected(TagIcon icon) {
		int start = getSelectionStart();
		int end = getSelectionEnd();
		StyledDocument doc = getStyledDocument();
		for (int i = start; i < end; i++) {
			if (icon == getIcon(doc.getCharacterElement(i))) {
				return true;
			}
		}
		return false;
	}

	static ArrayList<TagIcon> getIcons(StyledDocument doc) {
		ArrayList<TagIcon> res = new ArrayList<>();
		for (int i = 0; i < doc.getLength(); i++) {
			TagIcon icon = getIcon(doc.getCharacterElement(i));
			if (icon != null) {
				res.add(icon);
			}
		}
		return res;
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

	static ArrayList<TaggedTextContent> getTaggedText(int p0, int p1, StyledDocument doc) {
		String docText = getDocText(doc);
		if (docText.isEmpty()) {
			return new ArrayList<>();
		}
		ArrayList<TaggedTextContent> res = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (int i = p0; i < p1;) {
			int codepoint = docText.codePointAt(i);
			Element e = doc.getCharacterElement(i);
			TagIcon icon = getIcon(e);
			if (icon != null) {
				res.add(new Text(sb.toString()));
				res.add(icon.getTag());
				sb = new StringBuilder();
			}
			else {
				sb.appendCodePoint(codepoint);
			}
			i += Character.charCount(codepoint);
		}
		res.add(new Text(sb.toString()));
		return res;
	}

	public String getPlainText(ArrayList<Integer> plainToTaggedIndexes) {
		StyledDocument doc = getStyledDocument();
		String docText = getDocText(doc);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < doc.getLength();) {
			int codepoint = docText.codePointAt(i);
			Element e = doc.getCharacterElement(i);
			if (getIcon(e) == null) {
				sb.appendCodePoint(docText.codePointAt(i));
				plainToTaggedIndexes.add(i);
			}
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
		Caret caret = new SelectWordCaret();
		caret.setBlinkRate(oldCaret.getBlinkRate());
		setCaret(caret);
	}
}
