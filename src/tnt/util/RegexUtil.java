package tnt.util;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	public static final Pattern WORD_PATTERN = Pattern.compile("\\b(\\w\\S*\\w|\\w)\\b", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern SPELLING_UNIT_PATTERN = Pattern.compile("[\\w-]+", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern LEADING_PUNCTUATION_PATTERN = Pattern.compile("^(\\W*)\\w", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern TRAILING_PUNCTUATION_PATTERN = Pattern.compile("\\w(\\W*)$", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern LEADING_WHITESPACE_PATTERN = Pattern.compile("^(\\s*)\\S", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern TRAILING_WHITESPACE_PATTERN = Pattern.compile("\\S(\\s*)$", Pattern.UNICODE_CHARACTER_CLASS);
	public static final Pattern LETTER_PATTERN = Pattern.compile("\\pL", Pattern.UNICODE_CHARACTER_CLASS);

	public static ArrayList<MatchResult> matchAll(Matcher m) {
		ArrayList<MatchResult> matches = new ArrayList<>();
		while (m.find()) {
			matches.add(m.toMatchResult());
		}
		return matches;
	}

	public static MatchResult findSpellingUnitAtPosition(String s, int pos) {
		Matcher m = SPELLING_UNIT_PATTERN.matcher(s);
		while (m.find()) {
			if (pos >= m.start() && pos < m.end()) {
				return m.toMatchResult();
			}
		}
		return null;
	}

	public static String trimLeadingWhitespace(String s) {
		Matcher m = LEADING_WHITESPACE_PATTERN.matcher(s);
		while (m.find()) {
			s = new StringBuilder(s).replace(m.start(1), m.end(1), "").toString();
		}
		return s;
	}

	public static String trimTrailingWhitespace(String s) {
		Matcher m = TRAILING_WHITESPACE_PATTERN.matcher(s);
		while (m.find()) {
			s = new StringBuilder(s).replace(m.start(1), m.end(1), "").toString();
		}
		return s;
	}

	public static String getLeadingPunctuation(String s) {
		s = trimLeadingWhitespace(s);
		Matcher m = LEADING_PUNCTUATION_PATTERN.matcher(s);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

	public static String getTrailingPunctuation(String s) {
		s = trimTrailingWhitespace(s);
		Matcher m = TRAILING_PUNCTUATION_PATTERN.matcher(s);
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

	public static String getTrailingWhiteSpace(String s) {
		Matcher m = TRAILING_WHITESPACE_PATTERN.matcher(s);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

	public static int countWords(String s) {
		int count = 0;
		Matcher m = WORD_PATTERN.matcher(s);
		while (m.find()) {
			count++;
		}
		return count;
	}

	public static ArrayList<String> getWords(String s) {
		ArrayList<String> res = new ArrayList<>();
		Matcher m = WORD_PATTERN.matcher(s);
		while (m.find()) {
			res.add(m.group(1));
		}
		return res;
	}

	public static String getFirstLetter(String s) {
		Matcher m = LETTER_PATTERN.matcher(s);
		if (m.find()) {
			return m.group();
		}
		return "";
	}
}
