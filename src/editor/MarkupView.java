package editor;

import editor.javax.swing.plaf.basic.TextTransferHandler;
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

	public MarkupView() {
		setTransferHandler(new TextTransferHandler());
	}

	@Override
	public final void setTransferHandler(TransferHandler newHandler) {
		super.setTransferHandler(newHandler);
	}

	void insertTag(Tag tag) {
		insertIcon(new TagIcon(tag));
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
			Log.err(ex.toString());
		}
	}

	void insertText(String s) {
		try {
			getDocument().insertString(getCaretPosition(), s, null);
		}
		catch (BadLocationException ex) {
			Log.err(ex.toString());
		}
	}

	public void insertTaggedText(TaggedText t) {
		for (TaggedTextContent c : t.getContent()) {
			if (c instanceof Text) {
				String text = ((Text) c).getContent();
				insertText(text);
			}
			else if (c instanceof Tag) {
				insertTag((Tag) c);
			}
			else {
				Log.warn("unexpected instance: " + c.getClass().getName());
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
				Log.warn("unexpected instance: " + c.getClass().getName());
			}
		}
	}
}
