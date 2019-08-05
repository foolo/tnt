package qc;

import java.util.ArrayList;
import util.RegexUtil;
import xliff_model.SegmentTag;

public class Qc {

	static void checkPunctuation(String st, String tt, ArrayList<String> messages) {
		String p1 = RegexUtil.getPunctuation(st);
		String p2 = RegexUtil.getPunctuation(tt);
		if (p1.equals(p2) == false) {
			messages.add("Source and target ends with different punctuation");
		}
	}

	static void checkLeadingWhitespace(String st, String tt, ArrayList<String> messages) {
		String p1 = RegexUtil.getLeadingWhiteSpace(st);
		String p2 = RegexUtil.getLeadingWhiteSpace(tt);
		if (p1.equals(p2) == false) {
			messages.add("Source and target begins with difference spacing");
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

	static ArrayList<String> runQc(String st, String tt) {
		ArrayList<String> res = new ArrayList<>();
		checkPunctuation(st, tt, res);
		checkLeadingWhitespace(st, tt, res);
		checkRepeatedWords(tt, res);
		return res;
	}

	public static ArrayList<String> runQc(SegmentTag segmentTag) {
		String sourceText = segmentTag.getSourceText().getTextContent();
		String targetText = segmentTag.getTargetText().getTextContent();
		return runQc(sourceText, targetText);
	}
}
