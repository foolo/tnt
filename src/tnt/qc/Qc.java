package tnt.qc;

import java.util.ArrayList;
import java.util.Locale;
import tnt.editor.Session;
import tnt.util.RegexUtil;
import tnt.xliff_model.SegmentTag;

public class Qc {

	static void checkLeadingPunctuation(String st, String tt, ArrayList<String> messages) {
		String p1 = RegexUtil.getLeadingPunctuation(st);
		String p2 = RegexUtil.getLeadingPunctuation(tt);
		if (p1.equals(p2) == false) {
			messages.add("Source and target begin with different punctuation");
		}
	}

	static void checkTrailingPunctuation(String st, String tt, ArrayList<String> messages) {
		String p1 = RegexUtil.getTrailingPunctuation(st);
		String p2 = RegexUtil.getTrailingPunctuation(tt);
		if (p1.equals(p2) == false) {
			messages.add("Source and target end with different punctuation");
		}
	}

	static void checkLeadingWhitespace(String st, String tt, ArrayList<String> messages) {
		String p1 = RegexUtil.getLeadingWhiteSpace(st);
		String p2 = RegexUtil.getLeadingWhiteSpace(tt);
		if (p1.equals(p2) == false) {
			messages.add("Source and target begin with different spacing");
		}
	}

	static void checkTrailingWhitespace(String st, String tt, ArrayList<String> messages) {
		String p1 = RegexUtil.getTrailingWhiteSpace(st);
		String p2 = RegexUtil.getTrailingWhiteSpace(tt);
		if (p1.equals(p2) == false) {
			messages.add("Source and target end with different spacing");
		}
	}

	static void checkRepeatedWords(String tt, ArrayList<String> messages) {
		Session session = Session.getInstance();
		Locale locale = (session == null) ? Locale.getDefault() : new Locale(session.properties.getTrgLang());
		ArrayList<String> repeatedWords = RegexUtil.getRepeatedWords(tt, locale);
		for (String word : repeatedWords) {
			messages.add("Repeated word: '" + word + "'");
		}
	}

	static void checkLeadingCasing(String st, String tt, ArrayList<String> messages) {
		String s1 = RegexUtil.getFirstLetter(st);
		String s2 = RegexUtil.getFirstLetter(tt);
		if (s1.isEmpty() == false && s2.isEmpty() == false) {
			boolean s1Uppercase = Character.isUpperCase(s1.codePointAt(0));
			boolean s2Uppercase = Character.isUpperCase(s2.codePointAt(0));
			if (s1Uppercase != s2Uppercase) {
				messages.add("Source and target begin with different case");
			}
		}
	}

	static void checkMultipleSpaces(String tt, ArrayList<String> messages) {
		if (RegexUtil.hasMultipleSpaces(tt)) {
			messages.add("Multiple spaces in target");
		}
	}

	static ArrayList<String> runQc(String st, String tt) {
		ArrayList<String> res = new ArrayList<>();
		checkLeadingPunctuation(st, tt, res);
		checkTrailingPunctuation(st, tt, res);
		checkLeadingWhitespace(st, tt, res);
		checkTrailingWhitespace(st, tt, res);
		checkRepeatedWords(tt, res);
		checkMultipleSpaces(tt, res);
		checkLeadingCasing(st, tt, res);
		return res;
	}

	public static ArrayList<String> runQc(SegmentTag segmentTag) {
		String sourceText = segmentTag.getSourceText().getTextContent();
		String targetText = segmentTag.getTargetText().getTextContent();
		return runQc(sourceText, targetText);
	}
}
