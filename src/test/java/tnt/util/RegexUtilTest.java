package tnt.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegexUtilTest {

	public RegexUtilTest() {
	}

	private String[] getSpellingUnits(String s) {
		ArrayList<String> words = new ArrayList<>();
		Matcher m = RegexUtil.SPELLING_UNIT_PATTERN.matcher(s);
		while (m.find()) {
			words.add(m.group());
		}
		return words.toArray(new String[words.size()]);
	}

	@Test
	public void testSpellingUnitPattern() {
		Assertions.assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test foo bar"));
		Assertions.assertArrayEquals(new String[]{"Test", "Foo", "bar"}, getSpellingUnits("\"Test. Foo, bar.\""));
		Assertions.assertArrayEquals(new String[]{"test", "foo", "foo", "bar"}, getSpellingUnits("test/foo foo.bar"));
		Assertions.assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("  test   foo bar "));
		Assertions.assertArrayEquals(new String[]{"test", "foo-bar"}, getSpellingUnits("test foo-bar"));
		Assertions.assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test foo- bar"));
		Assertions.assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test -foo bar"));
		Assertions.assertArrayEquals(new String[]{"test", "that's", "all"}, getSpellingUnits("test that's all"));
		Assertions.assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test foo' bar"));
		Assertions.assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test 'foo bar"));
	}
}
