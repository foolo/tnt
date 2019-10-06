package tnt.editor.search;

import java.util.ArrayList;

public class SearchContext {

	final ArrayList<MatchLocation> matchLocations;
	private int currentMatchIndex;

	public SearchContext(ArrayList<MatchLocation> matchLocations, int currentMatchIndex) {
		this.matchLocations = matchLocations;
		this.currentMatchIndex = currentMatchIndex;
	}

	public int getCurrentMatchIndex() {
		return currentMatchIndex;
	}

	void wrapCurrentMatchIndex() {
		if (matchLocations.isEmpty()) {
			currentMatchIndex = 0;
			return;
		}
		while (currentMatchIndex >= matchLocations.size()) {
			currentMatchIndex -= matchLocations.size();
		}
		while (currentMatchIndex < 0) {
			currentMatchIndex += matchLocations.size();
		}
	}

	void previousMatch() {
		currentMatchIndex--;
		wrapCurrentMatchIndex();
	}

	void nextMatch() {
		currentMatchIndex++;
		wrapCurrentMatchIndex();
	}

	MatchLocation getCurrentMatchLocation() {
		if (matchLocations.isEmpty()) {
			return null;
		}
		return matchLocations.get(currentMatchIndex);
	}

	String getIndexStatusLabel() {
		if (matchLocations.isEmpty()) {
			return "No matches";
		}
		return currentMatchIndex + 1 + " of " + matchLocations.size();
	}
}
