package util;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class RegexUtil {

	public static ArrayList<MatchResult> matchAll(Matcher m) {
		ArrayList<MatchResult> matches = new ArrayList<>();
		while (m.find()) {
			matches.add(m.toMatchResult());
		}
		return matches;
	}
}
