package util;

public class StringUtil {

	public static String truncate(String s, int maxLength) {
		if (s.length() > maxLength) {
			return s.substring(0, maxLength - 3) + "...";
		}
		return s;
	}
}
