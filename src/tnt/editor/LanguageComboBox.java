package tnt.editor;

import java.util.ArrayList;
import javax.swing.JComboBox;
import tnt.language.Language;
import tnt.language.LanguageTag;
import tnt.util.Log;

public class LanguageComboBox extends JComboBox<String> {

	ArrayList<Language> languages = new ArrayList<>();

	void setLanguages(ArrayList<Language> languages) {
		this.languages = (ArrayList<Language>) languages.clone();
		removeAllItems();
		addItem("Select language");
		for (Language l : languages) {
			String spelling = (l.dictionaryPath == null) ? "" : " *";
			addItem(l.name + " (" + l.originalTagStr + ")" + spelling);
		}
	}

	public Language getSelectedLanguage() {
		int index = getSelectedIndex();
		if (index == 0) {
			return null;
		}
		return languages.get(index - 1);
	}

	public void setSelectedLanguage(String tagStr) {
		LanguageTag tag = new LanguageTag(tagStr);
		for (int i = 0; i < languages.size(); i++) {
			if (languages.get(i).tag.equals(tag)) {
				setSelectedIndex(i + 1);
				return;
			}
		}
		Log.err("setSelectedLanguage: no matching language for: " + tagStr);
	}
}
