package tnt.language;

import dk.dren.hunspell.Hunspell;
import tnt.editor.MarkupView;
import tnt.editor.util.UnderlinerEditorKit;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class SpellCheck {

	static final SimpleAttributeSet MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();
	static final SimpleAttributeSet CLEAR_MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();
	static Hunspell.Dictionary currentDictionary;

	static {
		MISSPELLED_ATTRIBUTE_SET.addAttribute(UnderlinerEditorKit.UNDERLINE_COLOR_ATTRIBUTE, new UnderlinerEditorKit.UnderlinedAttribute(true, Color.RED));
		CLEAR_MISSPELLED_ATTRIBUTE_SET.addAttribute(UnderlinerEditorKit.UNDERLINE_COLOR_ATTRIBUTE, new UnderlinerEditorKit.UnderlinedAttribute(false, null));
	}

	public static void spellCheck(MarkupView markupView) {
		if (currentDictionary == null) {
			return;
		}
		clearStyle(markupView);
	}

	public static void clearStyle(MarkupView markupView) {
		MutableAttributeSet inputAttributes = markupView.getInputAttributes();
		inputAttributes.removeAttributes(inputAttributes);
		StyledDocument doc = markupView.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), CLEAR_MISSPELLED_ATTRIBUTE_SET, false);
	}

	static void markText(MarkupView markupView, int start, int end) {
		StyledDocument doc = markupView.getStyledDocument();
		int offset = start;
		int length = end - start;
		doc.setCharacterAttributes(offset, length, MISSPELLED_ATTRIBUTE_SET, false);
	}

	public static void loadDictionary(Language l) throws IOException {
		currentDictionary = Hunspell.getInstance().getDictionary(l.dictionaryPath);
	}

	public static void unloadDictionary() {
		currentDictionary = null;
	}

	public static boolean isMisspelled(String word) {
		return false;
	}

	public static List<String> getSuggestions(String word) {
		if (currentDictionary.misspelled(word)) {
			return currentDictionary.suggest(word);
		}
		return new ArrayList<>();
	}
}
