package tnt.editor.search;

public class MatchLocation {

	public final int segmentIndex;
	public final int column;
	public final EditorRange range;

	public MatchLocation(int segmentIndex, int column, EditorRange range) {
		this.segmentIndex = segmentIndex;
		this.column = column;
		this.range = range;
	}
}
