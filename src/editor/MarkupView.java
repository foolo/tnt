package editor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;

public class MarkupView extends JTextPane {

	public MarkupView() {
		setTransferHandler(new TextTransferHandler());
	}

	@Override
	public final void setTransferHandler(TransferHandler newHandler) {
		super.setTransferHandler(newHandler);
	}

	void insertTag(Tag tag) {
		Icon icon = new TagIcon(tag, getClass().getClassLoader().getResource("images/left-bracket.png"));
		insertIcon(icon);
	}

	TagIcon getIcon(Element e) {
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

	public String getDocText() {
		StyledDocument doc = getStyledDocument();
		String s;
		try {
			return doc.getText(0, doc.getLength());
		}
		catch (BadLocationException ex) {
			System.err.println(ex);
			return "";
		}
	}

	public String getSelectedContent() {
		int p0 = Math.min(getCaret().getDot(), getCaret().getMark());
		int p1 = Math.max(getCaret().getDot(), getCaret().getMark());
		if (p0 == p1) {
			return "";
		}
		StyledDocument doc = getStyledDocument();
		StringBuilder sb = new StringBuilder();
		String docText = getDocText();
		for (int i = p0; i < p1; i++) {
			Element e = doc.getCharacterElement(i);
			if (getIcon(e) != null) {
				sb.appendCodePoint(docText.codePointAt(i));
			}
		}
		return sb.toString();
	}

	public TaggedText getTaggedText() {
		ArrayList<Object> res = new ArrayList<>();
		StyledDocument doc = getStyledDocument();
		StringBuilder sb = new StringBuilder();
		String docText = getDocText();
		for (int i = 0; i < docText.length(); i++) {
			Element e = doc.getCharacterElement(i);
			TagIcon icon = getIcon(e);
			if (icon != null) {
				res.add(sb.toString());
				res.add(icon.tag);
				sb = new StringBuilder();
			}
			else {
				sb.appendCodePoint(docText.codePointAt(i));
			}
		}
		res.add(sb.toString());
		return new TaggedText(res);
	}

	void appendText(String s) {
		try {
			getDocument().insertString(getDocument().getLength(), s, null);
		}
		catch (BadLocationException ex) {
			Logger.getLogger(MarkupView.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void setTaggedText(TaggedText t) {
		setText("");
		for (Object o : t.getContent()) {
			if (o instanceof String) {
				appendText((String) o);
			}
			else if (o instanceof Tag) {
				insertTag((Tag) o);
			}
			else {
				System.out.println("unexpected tagged text content: " + o);
			}
		}
	}
}
