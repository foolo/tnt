package util;

public class StringUtil {

	public static String truncate(String s, int maxLength) {
		if (s.length() > maxLength) {
			return s.substring(0, maxLength - 3) + "...";
		}
		return s;
	}

	public static String leftPad(String s, char c, int length) {
		int padLength = length - s.length();
		StringBuilder sb = new StringBuilder();
		while (padLength-- > 0) {
			sb.append(c);
		}
		sb.append(s);
		return sb.toString();
	}
}
