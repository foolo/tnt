package language;

import dk.dren.hunspell.Hunspell;
import editor.MarkupView;
import editor.UnderlinerEditorKit;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import util.StringUtil;

public class SpellCheck {

	static Pattern WORDS_PATTERN = Pattern.compile("\\b\\w+\\b", Pattern.UNICODE_CHARACTER_CLASS);
	static final SimpleAttributeSet MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();
	static final SimpleAttributeSet CLEAR_MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();
	static Hunspell.Dictionary currentDictionary;

	static {
		MISSPELLED_ATTRIBUTE_SET.addAttribute(UnderlinerEditorKit.UNDERLINE_COLOR_ATTRIBUTE, new UnderlinerEditorKit.UnderlinedAttribute(true, Color.RED));
		CLEAR_MISSPELLED_ATTRIBUTE_SET.addAttribute(UnderlinerEditorKit.UNDERLINE_COLOR_ATTRIBUTE, new UnderlinerEditorKit.UnderlinedAttribute(false, null));
	}

	public static void spellCheck(MarkupView markupView, int caretLocationTagged, boolean modified) {
		if (currentDictionary == null) {
			return;
		}
		clearStyle(markupView);
		ArrayList<Integer> indexes = new ArrayList<>();
		String text = markupView.getPlainText(indexes);
		int caretLocationPlain = StringUtil.taggedToPlainIndex(caretLocationTagged, indexes);
		Matcher m = WORDS_PATTERN.matcher(text);
		while (m.find()) {
			String word = m.group();
			int startPlain = m.start();
			int endPlain = m.end();
			boolean caretIsAtWordEnd = (caretLocationPlain == endPlain);
			boolean skipSpellCheckForWord = (caretIsAtWordEnd && modified);
			if (skipSpellCheckForWord == false) {
				if (currentDictionary.misspelled(word)) {
					int startTagged = StringUtil.plainToTaggedIndex(startPlain, indexes);
					int endTagged = StringUtil.plainToTaggedIndex(endPlain, indexes);
					markText(markupView, startTagged, endTagged);
				}
			}
		}
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
}
