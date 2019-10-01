package tnt.language;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LanguageTag extends ArrayList<String> {

	static final Pattern SPLIT_TAG_PATTERN = Pattern.compile("[\\W_]+", Pattern.UNICODE_CHARACTER_CLASS);

	public LanguageTag(String tag) {
		String[] parts = SPLIT_TAG_PATTERN.split(tag.toLowerCase());
		add(parts[0]);
		if (parts.length > 1) {
			add(parts[1]);
		}
	}

	private LanguageTag(List<String> rhs) {
		super(rhs);
	}

	public LanguageTag primaryTag() {
		return new LanguageTag(subList(0, 1));
	}
}
