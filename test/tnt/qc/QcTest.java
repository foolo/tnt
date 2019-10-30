package tnt.qc;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class QcTest {

	@Test
	public void testCheckLeadingPunctuation() {
		assertEquals("[]", Qc.runQc("-Test", "–Test").toString());
		assertEquals("[]", Qc.runQc("'Test", "’Test").toString());
		assertEquals("[]", Qc.runQc("“Test", "‛Test").toString());
		assertEquals("[]", Qc.runQc("Test", "Test").toString());
		assertEquals("[]", Qc.runQc("('Test", "('Test").toString());
		assertEquals("[Source and target begin with different spacing]", Qc.runQc("  –Test", "-Test").toString());

		assertEquals("[Source and target begin with different punctuation]", Qc.runQc("Test", "–Test").toString());
		assertEquals("[Source and target begin with different punctuation]", Qc.runQc("-Test", "Test").toString());
		assertEquals("[Source and target begin with different punctuation]", Qc.runQc("'Test", "Test").toString());

		assertEquals("[Source and target begin with different punctuation, Source and target begin with different spacing]", Qc.runQc("  Test", " -Test").toString());
	}

	@Test
	public void testCheckTrailingPunctuation() {
		assertEquals("[]", Qc.runQc("Test.", "Test.").toString());
		assertEquals("[]", Qc.runQc("Test", "Test").toString());
		assertEquals("[]", Qc.runQc("Test'", "Test‛").toString());
		assertEquals("[]", Qc.runQc("Test“", "Test’").toString());
		assertEquals("[Source and target end with different spacing]", Qc.runQc("Test.", "Test. ").toString());

		assertEquals("[Source and target end with different punctuation]", Qc.runQc("Test.", "Test...").toString());
		assertEquals("[Source and target end with different punctuation]", Qc.runQc("Test", "Test.").toString());
		assertEquals("[Source and target end with different punctuation]", Qc.runQc("Test.", "Test .").toString());
		assertEquals("[Source and target end with different punctuation]", Qc.runQc("Test!", "Test !").toString());
		assertEquals("[Source and target end with different punctuation]", Qc.runQc("Test,", "Test'").toString());
	}

	@Test
	public void testCheckLeadingWhitespace() {
		assertEquals("[]", Qc.runQc("Test", "Test").toString());
		assertEquals("[]", Qc.runQc(" Test", " Test").toString());
		assertEquals("[Source and target begin with different punctuation]", Qc.runQc("-Test", "Test").toString());

		assertEquals("[Source and target begin with different spacing]", Qc.runQc("  –Test", "-Test").toString());
		assertEquals("[Source and target begin with different punctuation, Source and target begin with different spacing]", Qc.runQc("Test", " -Test").toString());
		assertEquals("[Source and target begin with different spacing]", Qc.runQc("Test", " Test").toString());
		assertEquals("[Source and target begin with different spacing]", Qc.runQc(" Test", "Test").toString());
		assertEquals("[Source and target begin with different spacing]", Qc.runQc("  Test", " Test").toString());

		assertEquals("[Source and target begin with different spacing, Source and target end with different spacing]", Qc.runQc("Test", " Test ").toString());
	}

	@Test
	public void testCheckTrailingWhitespace() {
		assertEquals("[]", Qc.runQc("Test", "Test").toString());
		assertEquals("[]", Qc.runQc("Test ", "Test ").toString());
		assertEquals("[Source and target end with different punctuation]", Qc.runQc("Test.", "Test...").toString());

		assertEquals("[Source and target end with different spacing]", Qc.runQc("–Test  ", "-Test").toString());
		assertEquals("[Source and target end with different punctuation, Source and target end with different spacing]", Qc.runQc("Test", "Test. ").toString());
		assertEquals("[Source and target end with different spacing]", Qc.runQc("Test", "Test ").toString());
		assertEquals("[Source and target end with different spacing]", Qc.runQc("Test ", "Test").toString());
		assertEquals("[Source and target end with different spacing]", Qc.runQc("Test  ", "Test ").toString());
	}

	@Test
	public void testCheckRepeatedWords() {
		assertEquals("[]", Qc.runQc("", "Test foo").toString());
		assertEquals("[]", Qc.runQc("", "Test testing").toString());
		assertEquals("[]", Qc.runQc("", "test test-test").toString());
		assertEquals("[]", Qc.runQc("", "test-test test").toString());
		assertEquals("[]", Qc.runQc("", "test'test test").toString());
		assertEquals("[]", Qc.runQc("'test", "'test test").toString());
		assertEquals("[]", Qc.runQc("Test.", "Test, test.").toString());
		assertEquals("[]", Qc.runQc("Test.", "Test. Test.").toString());
		assertEquals("[]", Qc.runQc("Test.", "A test, test.").toString());
		assertEquals("[]", Qc.runQc("Test.", "A test testing stuff.").toString());
		assertEquals("[]", Qc.runQc("Test.", "A testing test stuff.").toString());
		assertEquals("[]", Qc.runQc("Test.", "A testing ing stuff.").toString());
		assertEquals("[]", Qc.runQc("Test.", "A ing testing stuff.").toString());

		assertEquals("[Repeated word: 'test']", Qc.runQc("Test.", "Test test.").toString());
		assertEquals("[Repeated word: 'test']", Qc.runQc("Test", "Test test").toString());
		assertEquals("[Repeated word: 'test']", Qc.runQc("Test.", "A test test foo.").toString());
		assertEquals("[Repeated word: 'test']", Qc.runQc("Test.", "A test Test foo.").toString());
		assertEquals("[Source and target end with different punctuation, Repeated word: 'test'']", Qc.runQc("Test.", "A test' test'.").toString());
		assertEquals("[Repeated word: ''test']", Qc.runQc("Test.", "A 'test 'test.").toString());
		assertEquals("[Repeated word: 'te-st']", Qc.runQc("Test.", "A te-st te-st.").toString());
		assertEquals("[Repeated word: '-test']", Qc.runQc("Test.", "A -test -test.").toString());
		assertEquals("[Source and target end with different punctuation, Repeated word: 'test-']", Qc.runQc("Test.", "A test- test-.").toString());
	}

	@Test
	public void testCheckLeadingCasing() {
		assertEquals("[]", Qc.runQc("Test", "Foo").toString());
		assertEquals("[]", Qc.runQc("Test", "FOO").toString());
		assertEquals("[]", Qc.runQc("test", "foo").toString());
		assertEquals("[]", Qc.runQc("tEST", "foo").toString());

		assertEquals("[Source and target begin with different case]", Qc.runQc("test", "Foo").toString());
		assertEquals("[Source and target begin with different case]", Qc.runQc("Test", "foo").toString());
		assertEquals("[Source and target begin with different case]", Qc.runQc("tEST", "FOO").toString());
	}

	@Test
	public void testCheckMultipleSpaces() {
		assertEquals("[]", Qc.runQc("Test", "Test foo").toString());
		assertEquals("[]", Qc.runQc("Test", "Test foo").toString()); // EM QUAD
		assertEquals("[Source and target begin with different spacing]", Qc.runQc("Test", "  Test foo").toString());
		assertEquals("[Source and target end with different spacing]", Qc.runQc("Test", "Test foo  ").toString());

		assertEquals("[Multiple spaces in target]", Qc.runQc("Test", "Test  foo").toString()); // SPACE x 2
		assertEquals("[Multiple spaces in target]", Qc.runQc("Test", "Test  foo").toString()); // EM QUAD + SPACE
		assertEquals("[Multiple spaces in target]", Qc.runQc("Test", "Test  foo").toString()); // EM QUAD x 2
	}

	public static void main(String[] args) {
		JUnitCore.main("tnt.qc.QcTest");
	}
}
