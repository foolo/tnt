package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import tnt.editor.search.EditorRange;
import tnt.language.SpellCheck;
import tnt.util.RegexUtil;
import tnt.util.StringUtil;

public class SegmentView extends javax.swing.JPanel {

	public enum Column {
		SOURCE, TARGET
	}

	boolean modifiedFlag = false;

	SegmentView(FileView fileView, String id) {
		initComponents();
		markupViewTarget.addDocumentListener(); // done after setEditorKit which resets the internal document
	}

	public void updateSegmentTag(String s) {
		markupViewTarget.updateTaggedText(s);
		applySpellcheck();
	}

	public void insertText(String s) {
		markupViewTarget.insertText(markupViewTarget.getCaretPosition(), s);
	}

	void update(int caretPosition1, int caretPosition2) {
		modifiedFlag = true;
		applySpellcheck();
	}

	MarkupView getMarkupView(Column column) {
		return markupViewTarget;
	}

	Column getActiveColumn() {
		return Column.TARGET;
	}

	void navigateToView(Column column, Integer caretPosition) {
		navigateToView(column, caretPosition, false);
	}

	void navigateToView(Column column, Integer caretPosition, boolean bypassCaretListener) {
		MarkupView markupView = getMarkupView(column);
		markupView.grabFocus();
		if (caretPosition != null) {
			markupView.setCaretPosition(caretPosition);
		}
	}

	private static SegmentView activeSegmentView = null;

	static SegmentView getActiveSegmentView() {
		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (c instanceof MarkupView) {
			MarkupView mv = (MarkupView) c;
			return mv.getSegmentView();
		}
		return activeSegmentView;
	}

	void applySpellcheck() {
		SwingUtilities.invokeLater(() -> {
			SpellCheck.spellCheck(markupViewTarget);
		});
	}

	ArrayList<MatchResult> findMatches(String term, String text, int flags) {
		if (term.isEmpty()) {
			return new ArrayList<>();
		}
		Matcher m = Pattern.compile(Pattern.quote(term), flags | Pattern.UNICODE_CHARACTER_CLASS).matcher(text);
		return RegexUtil.matchAll(m);
	}

	ArrayList<EditorRange> toEditorRange(ArrayList<MatchResult> matchResults, ArrayList<Integer> plainToTaggedIndexes) {
		ArrayList<EditorRange> res = new ArrayList<>();
		for (MatchResult matchResult : matchResults) {
			int startTagged = StringUtil.plainToTaggedIndex(matchResult.start(), plainToTaggedIndexes);
			int endTagged = StringUtil.plainToTaggedIndex(matchResult.end(), plainToTaggedIndexes);
			res.add(new EditorRange(startTagged, endTagged));
		}
		return res;
	}

	void findMatches(String term, int flags, int segmentIndex, int column, ArrayList<MatchLocation> searchResultsOut) {
		MarkupView mv = markupViewTarget;
		ArrayList<Integer> indexes = new ArrayList<>();
		String text = mv.getPlainText(indexes);
		ArrayList<MatchResult> matchResults = findMatches(term, text, flags);
		ArrayList<EditorRange> editorRanges = toEditorRange(matchResults, indexes);
		for (EditorRange range : editorRanges) {
			searchResultsOut.add(new MatchLocation(segmentIndex, column, range));
		}
	}

	void updateHeight() {
		System.out.println("tnt.editor.SegmentView.updateHeight() " + markupViewTarget.getPreferredSize());
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();

        setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane4.setBorder(null);
        jScrollPane4.setOpaque(false);

        markupViewTarget.setOpaque(false);
        markupViewTarget.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                markupViewTargetCaretUpdate(evt);
            }
        });
        jScrollPane4.setViewportView(markupViewTarget);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		if (modifiedFlag == false) {
			Session.getUndoManager().markSnapshot();
		}
		if (modifiedFlag) {
			updateHeight();
		}
		modifiedFlag = false;
    }//GEN-LAST:event_markupViewTargetCaretUpdate

	static int getMinHeightForFont(Font font) {
		JTextPane textPane = new JTextPane();
		textPane.setFont(font);
		textPane.setText("\n");
		return textPane.getPreferredSize().height;
	}

	void clearSpellcheck() {
		SpellCheck.clearStyle(markupViewTarget);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane4;
    public final tnt.editor.EditableMarkupView markupViewTarget = new tnt.editor.EditableMarkupView(this);
    // End of variables declaration//GEN-END:variables
}
