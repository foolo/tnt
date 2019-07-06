package editor;

import java.util.ArrayList;
import javax.swing.JComboBox;

public class LanguageComboBox extends JComboBox<String> {

	static class Language {

		public final String name;
		public final String code;

		public Language(String name, String code) {
			this.name = name;
			this.code = code;
		}

		@Override
		public String toString() {
			return name + " (" + code + ")";
		}
	}

	ArrayList<Language> languages = new ArrayList<>();

	void setLanguages(ArrayList<Language> languages) {
		this.languages = languages;
		removeAllItems();
		addItem("Select language");
		for (Language l : languages) {
			addItem(l.name + " (" + l.code + ")");
		}
	}

	public String getSelectedLanguageCode() {
		int index = getSelectedIndex();
		if (index == 0) {
			return "";
		}
		return languages.get(index - 1).code;
	}
}
