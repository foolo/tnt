package util;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	public static final Pattern WORDS_PATTERN = Pattern.compile("\\b\\w+\\b", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern END_PUNCTUATION_PATTERN = Pattern.compile("\\w(\\W*)$", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern LEADING_WHITESPACE_PATTERN = Pattern.compile("^(\\s*)\\S", Pattern.UNICODE_CHARACTER_CLASS);

	public static ArrayList<MatchResult> matchAll(Matcher m) {
		ArrayList<MatchResult> matches = new ArrayList<>();
		while (m.find()) {
			matches.add(m.toMatchResult());
		}
		return matches;
	}

	public static MatchResult findWordAtPosition(String s, int pos) {
		Matcher m = WORDS_PATTERN.matcher(s);
		while (m.find()) {
			if (pos >= m.start() && pos < m.end()) {
				return m.toMatchResult();
			}
		}
		return null;
	}

	public static String getPunctuation(String s) {
		Matcher m = END_PUNCTUATION_PATTERN.matcher(s);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

	public static String getLeadingWhiteSpace(String s) {
		Matcher m = LEADING_WHITESPACE_PATTERN.matcher(s);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}
}
