package tnt.qc;

import java.util.ArrayList;
import tnt.util.RegexUtil;
import tnt.xliff_model.SegmentTag;

public class Qc {

	static void checkPunctuation(String st, String tt, ArrayList<String> messages) {
		String p1 = RegexUtil.getPunctuation(st);
		String p2 = RegexUtil.getPunctuation(tt);
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

	static void checkRepeatedWords(String tt, ArrayList<String> messages) {
		ArrayList<String> words = RegexUtil.getWords(tt);
		for (int i = 0; i < words.size() - 1; i++) {
			String w1 = words.get(i).toLowerCase();
			String w2 = words.get(i + 1).toLowerCase();
			if (w1.equals(w2)) {
				messages.add("Repeated word: '" + w1 + "'");
			}
		}
	}

	static void checkLeadingCasing(String st, String tt, ArrayList<String> messages) {
		String s1 = RegexUtil.getFirstWordCharacter(st);
		String s2 = RegexUtil.getFirstWordCharacter(tt);
		if (s1.isEmpty() == false && s2.isEmpty() == false) {
			boolean s1Uppercase = Character.isUpperCase(s1.codePointAt(0));
			boolean s2Uppercase = Character.isUpperCase(s2.codePointAt(0));
			if (s1Uppercase != s2Uppercase) {
				messages.add("Source and target begin with different case");
			}
		}
	}

	static ArrayList<String> runQc(String st, String tt) {
		ArrayList<String> res = new ArrayList<>();
		checkPunctuation(st, tt, res);
		checkLeadingWhitespace(st, tt, res);
		checkRepeatedWords(tt, res);
		checkLeadingCasing(st, tt, res);
		return res;
	}

	public static ArrayList<String> runQc(SegmentTag segmentTag) {
		String sourceText = segmentTag.getSourceText().getTextContent();
		String targetText = segmentTag.getTargetText().getTextContent();
		return runQc(sourceText, targetText);
	}
}
