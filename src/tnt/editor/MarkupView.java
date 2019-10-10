package tnt.editor;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import tnt.xliff_model.TaggedText;
import tnt.xliff_model.Tag;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Position;
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
		setTransferHandler(new TaggedTextTransferHandler());
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

	boolean canMoveCaret(int direction) {
		try {
			int pos = getCaretPosition();
			int nextPos = getUI().getNextVisualPositionFrom(this, pos, Position.Bias.Forward, direction, new Position.Bias[1]);
			return (nextPos != pos);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
		return false;
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

	static TaggedText getTaggedText(int p0, int p1, StyledDocument doc) {
		String docText = getDocText(doc);
		if (docText.isEmpty()) {
			return new TaggedText(new ArrayList<>());
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
		return new TaggedText(res);
	}

	public TaggedText getSelectedTaggedText() {
		int p0 = Math.min(getCaret().getDot(), getCaret().getMark());
		int p1 = Math.max(getCaret().getDot(), getCaret().getMark());
		return getTaggedText(p0, p1, getStyledDocument());
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

	void appendText(String s) {
		try {
			getDocument().insertString(getDocument().getLength(), s, null);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}

	public void setTaggedText(TaggedText t) {
		setText("");
		for (TaggedTextContent c : t.getContent()) {
			if (c instanceof Text) {
				appendText(((Text) c).getContent());
			}
			else if (c instanceof Tag) {
				insertTag((Tag) c);
			}
			else {
				Log.warn("setTaggedText: unexpected instance: " + c.getClass().getName());
			}
		}
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
}
