package editor;

import editor.javax.swing.plaf.basic.TextTransferHandler;
import java.awt.Font;
import xliff_model.TaggedText;
import xliff_model.Tag;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import util.Log;
import xliff_model.TaggedTextContent;
import xliff_model.Text;

public class MarkupView extends JTextPane {

	private SegmentView segmentView;

	public MarkupView() {
	}

	public MarkupView(SegmentView segmentView) {
		this.segmentView = segmentView;
		setTransferHandler(new TextTransferHandler());
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
			System.err.println(ex);
			return "";
		}
	}

	private static TaggedText getTaggedText(int p0, int p1, StyledDocument doc) {
		String docText = getDocText(doc);
		if (docText.isEmpty()) {
			return new TaggedText(new ArrayList<>());
		}
		ArrayList<TaggedTextContent> res = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (int i = p0; i < p1; i++) {
			Element e = doc.getCharacterElement(i);
			TagIcon icon = getIcon(e);
			if (icon != null) {
				res.add(new Text(sb.toString()));
				res.add(icon.getTag());
				sb = new StringBuilder();
			}
			else {
				sb.appendCodePoint(docText.codePointAt(i));
			}
		}
		res.add(new Text(sb.toString()));
		return new TaggedText(res);
	}

	public TaggedText getTaggedText() {
		StyledDocument doc = getStyledDocument();
		return getTaggedText(0, doc.getLength(), doc);
	}

	public TaggedText getSelectedTaggedText() {
		int p0 = Math.min(getCaret().getDot(), getCaret().getMark());
		int p1 = Math.max(getCaret().getDot(), getCaret().getMark());
		return getTaggedText(p0, p1, getStyledDocument());
	}

	void appendText(String s) {
		try {
			getDocument().insertString(getDocument().getLength(), s, null);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}

	void insertText(String s) {
		try {
			getDocument().insertString(getCaretPosition(), s, null);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}

	void removeSelection() {
		int p0 = Math.min(getCaret().getDot(), getCaret().getMark());
		int p1 = Math.max(getCaret().getDot(), getCaret().getMark());
		try {
			getDocument().remove(p0, p1 - p0);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}

	public void insertTaggedText(TaggedText t) {
		removeSelection();
		for (TaggedTextContent c : t.getContent()) {
			if (c instanceof Text) {
				String text = ((Text) c).getContent();
				insertText(text);
			}
			else if (c instanceof Tag) {
				insertTag((Tag) c);
			}
			else {
				Log.warn("insertTaggedText: unexpected instance: " + c.getClass().getName());
			}
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
}
