package editor;

import java.util.ArrayList;
import javax.swing.JComboBox;
import language.Language;

public class LanguageComboBox extends JComboBox<String> {

	ArrayList<Language> languages = new ArrayList<>();

	void setLanguages(ArrayList<Language> languages) {
		this.languages = (ArrayList<Language>) languages.clone();
		removeAllItems();
		addItem("Select language");
		for (Language l : languages) {
			String spelling = (l.dictionaryPath == null) ? "" : " *";
			addItem(l.name + " (" + l.code + ")" + spelling);
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
