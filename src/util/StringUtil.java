package util;

import java.util.ArrayList;

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

	public static int plainToTaggedIndex(int plainIndex, ArrayList<Integer> indexes) {
		if (plainIndex == 0 && indexes.isEmpty()) {
			return 0;
		}
		if (plainIndex >= indexes.size()) {
			return indexes.get(indexes.size() - 1) + 1;
		}
		return indexes.get(plainIndex);
	}

	public static int taggedToPlainIndex(int taggedIndex, ArrayList<Integer> indexes) {
		for (int i = 0; i < indexes.size(); i++) {
			int ti = indexes.get(i);
			if (ti >= taggedIndex) {
				return i;
			}
		}
		return indexes.size();
	}
}
