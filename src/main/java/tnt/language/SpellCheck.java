package tnt.language;

import java.awt.Color;
import tnt.editor.MarkupView;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class SpellCheck {

	static final SimpleAttributeSet CLEAR_MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();

	static {
		CLEAR_MISSPELLED_ATTRIBUTE_SET.addAttribute("ul_attr", Color.MAGENTA);
	}

	public static void spellCheck(MarkupView markupView) {
		clearStyle(markupView);
	}

	public static void clearStyle(MarkupView markupView) {
		MutableAttributeSet inputAttributes = markupView.getInputAttributes();
		inputAttributes.removeAttributes(inputAttributes);
		StyledDocument doc = markupView.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), CLEAR_MISSPELLED_ATTRIBUTE_SET, false);
	}
}
