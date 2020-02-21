package tnt.language;

import tnt.editor.MarkupView;
import tnt.editor.util.UnderlinerEditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class SpellCheck {

	static final SimpleAttributeSet CLEAR_MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();

	static {
		CLEAR_MISSPELLED_ATTRIBUTE_SET.addAttribute(UnderlinerEditorKit.UNDERLINE_COLOR_ATTRIBUTE, new UnderlinerEditorKit.UnderlinedAttribute(false, null));
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
