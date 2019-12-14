package tnt.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import org.junit.runner.JUnitCore;

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
		assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test foo bar"));
		assertArrayEquals(new String[]{"Test", "Foo", "bar"}, getSpellingUnits("\"Test. Foo, bar.\""));
		assertArrayEquals(new String[]{"test", "foo", "foo", "bar"}, getSpellingUnits("test/foo foo.bar"));
		assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("  test   foo bar "));
		assertArrayEquals(new String[]{"test", "foo-bar"}, getSpellingUnits("test foo-bar"));
		assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test foo- bar"));
		assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test -foo bar"));
		assertArrayEquals(new String[]{"test", "that's", "all"}, getSpellingUnits("test that's all"));
		assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test foo' bar"));
		assertArrayEquals(new String[]{"test", "foo", "bar"}, getSpellingUnits("test 'foo bar"));
	}

	public static void main(String[] args) {
		JUnitCore.main("tnt.util.RegexUtilTest");
	}
}
