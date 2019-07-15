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

public class SpellCheck {

	static Pattern WORDS_PATTERN = Pattern.compile("\\b(\\w+)\\b", Pattern.UNICODE_CHARACTER_CLASS);
	static final SimpleAttributeSet MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();
	static final SimpleAttributeSet CLEAR_MISSPELLED_ATTRIBUTE_SET = new SimpleAttributeSet();
	static Hunspell.Dictionary currentDictionary;

	static {
		MISSPELLED_ATTRIBUTE_SET.addAttribute(UnderlinerEditorKit.UNDERLINE_COLOR_ATTRIBUTE, new UnderlinerEditorKit.UnderlinedAttribute(true, Color.RED));
		CLEAR_MISSPELLED_ATTRIBUTE_SET.addAttribute(UnderlinerEditorKit.UNDERLINE_COLOR_ATTRIBUTE, new UnderlinerEditorKit.UnderlinedAttribute(false, null));
	}

	static int plainToTaggedIndex(int plainIndex, ArrayList<Integer> indexes) {
		if (plainIndex >= indexes.size()) {
			return indexes.get(indexes.size() - 1) + 1;
		}
		return indexes.get(plainIndex);
	}

	public static void spellCheck(MarkupView markupView, int caretLocationTagged) {
		clearStyle(markupView);
		ArrayList<Integer> indexes = new ArrayList<>();
		String text = markupView.getPlainText(indexes);
		Matcher m = WORDS_PATTERN.matcher(text);
		while (m.find()) {
			String word = m.group(1);
			if (misspelled(word)) {
				int firstLetterTagged = plainToTaggedIndex(m.start(), indexes);
				int lastLetterTagged = plainToTaggedIndex(m.end(), indexes);
				markText(markupView, firstLetterTagged, lastLetterTagged);
			}
		}
	}

	static void clearStyle(MarkupView markupView) {
		MutableAttributeSet inputAttributes = markupView.getInputAttributes();
		inputAttributes.removeAttributes(inputAttributes);
		StyledDocument doc = markupView.getStyledDocument();
		doc.setCharacterAttributes(0, doc.getLength(), CLEAR_MISSPELLED_ATTRIBUTE_SET, false);
	}

	static void markText(MarkupView markupView, int firsttLetter, int lastLetter) {
		StyledDocument doc = markupView.getStyledDocument();
		int offset = firsttLetter;
		int length = lastLetter - firsttLetter;
		doc.setCharacterAttributes(offset, length, MISSPELLED_ATTRIBUTE_SET, false);
	}

	static boolean misspelled(String s) {
		return s.length() > 3;
	}

	public static void loadDictionary(Language l) throws IOException {
		currentDictionary = Hunspell.getInstance().getDictionary(l.dictionaryPath);
	}
}
