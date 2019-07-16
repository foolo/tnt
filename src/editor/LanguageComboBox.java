package editor;

import java.util.ArrayList;
import javax.swing.JComboBox;
import language.Language;
import util.Log;

public class LanguageComboBox extends JComboBox<String> {

	ArrayList<Language> languages = new ArrayList<>();

	void setLanguages(ArrayList<Language> languages) {
		this.languages = (ArrayList<Language>) languages.clone();
		removeAllItems();
		addItem("Select language");
		for (Language l : languages) {
			String spelling = (l.dictionaryPath == null) ? "" : " *";
			addItem(l.name + " (" + l.getCodeAsString() + ")" + spelling);
		}
	}

	public String getSelectedLanguageCode() {
		int index = getSelectedIndex();
		if (index == 0) {
			return "";
		}
		return languages.get(index - 1).getCodeAsString();
	}

	public void setSelectedLanguageCode(String codeStr) {
		String[] code = Language.stringToCode(codeStr);
		for (int i = 0; i < languages.size(); i++) {
			if (languages.get(i).matchCode(code)) {
				setSelectedIndex(i + 1);
				return;
			}
		}
		Log.err("setSelectedLanguageCode: no matching language for: " + codeStr);
	}
}
