package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import tnt.editor.search.EditorRange;
import tnt.editor.util.MouseWheelScrollListener;
import tnt.editor.util.UnderlinerEditorKit;
import tnt.language.SpellCheck;
import tnt.undo_manager.UndoPosition;
import tnt.util.RegexUtil;
import tnt.util.StringUtil;
import tnt.xliff_model.SegmentTag;
import tnt.xliff_model.TaggedText;

public class SegmentView extends javax.swing.JPanel {

	public enum Column {
		SOURCE, TARGET
	}

	SegmentTag segmentTag;
	private final FileView fileView;
	boolean modifiedFlag = false;
	private int minHeight = 0;
	static final DefaultHighlighter.DefaultHighlightPainter FILTER_MATCH_HIGHLIGHT_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	final DefaultHighlighter.DefaultHighlightPainter selectionPainter;

	SegmentView(FileView fileView, String id) {
		initComponents();
		this.fileView = fileView;
		jScrollPane4.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane4));
		markupViewTarget.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK), "none");
		markupViewTarget.setEditorKit(new UnderlinerEditorKit());
		markupViewTarget.addDocumentListener(); // done after setEditorKit which resets the internal document
		markupViewTarget.addDocumentFilter();
		selectionPainter = new DefaultHighlighter.DefaultHighlightPainter(markupViewTarget.getSelectionColor());
		jScrollPane4.getViewport().setOpaque(false);
	}

	public void updateSegmentTag(SegmentTag segmentTag) {
		this.segmentTag = segmentTag;
		markupViewTarget.updateTaggedText(segmentTag.getTargetText());
		applySpellcheck();
	}

	public void setTargetText(TaggedText t) {
		markupViewTarget.replaceTaggedText(t);
	}

	public void insertText(String s) {
		markupViewTarget.insertText(markupViewTarget.getCaretPosition(), s);
	}

	private boolean setStateField(SegmentTag.State state) {
		boolean res = segmentTag.setState(state);
		return res;
	}

	void setState(SegmentTag.State state) {
		if (setStateField(state)) {
			int pos = markupViewTarget.getCaretPosition();
			notifyUndoManager(pos, pos);
			Session.getUndoManager().markSnapshot();
		}
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	public FileView getFileView() {
		return fileView;
	}

	void notifyUndoManager(int caretPos1, int caretPos2) {
		UndoPosition pos1 = new UndoPosition(this, caretPos1);
		UndoPosition pos2 = new UndoPosition(this, caretPos2);
		Session.getUndoManager().getCurrentState().setModified(pos1, pos2);
	}

	void update(int caretPosition1, int caretPosition2) {
		fileView.scroll_to_segment(this);
		segmentTag.setTargetText(markupViewTarget.getTaggedText());
		setStateField(SegmentTag.State.INITIAL);
		notifyUndoManager(caretPosition1, caretPosition2);
		modifiedFlag = true;
		fileView.searchBar1.notifyUpdate();
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

	void clearHighlighting() {
		markupViewTarget.getHighlighter().removeAllHighlights();
	}

	void highlightMatch(int column, EditorRange range) {
		MarkupView mv = markupViewTarget;
		mv.applyHighlighting(range, FILTER_MATCH_HIGHLIGHT_PAINTER);
	}

	void clearSelection(MarkupView mv) {
		Highlighter.Highlight[] highlights = mv.getHighlighter().getHighlights();
		for (Highlighter.Highlight hl : highlights) {
			if (hl.getPainter() == selectionPainter) {
				mv.getHighlighter().removeHighlight(hl);
			}
		}
	}

	void clearSelection() {
		clearSelection(markupViewTarget);
	}

	void highlightSelection(int column, EditorRange range) {
		MarkupView mv = markupViewTarget;
		mv.applyHighlighting(range, selectionPainter);
	}

	void updateHeight() {
		Dimension d = getPreferredSize();
		int h2 = markupViewTarget.getPreferredSize().height;
		int newHeight = Math.max(minHeight, h2);
		if (newHeight != d.height) {
			d.height = newHeight + 3;
			setPreferredSize(d);
		}
		fileView.validate();
	}

	void reportCaretPosition() {
		fileView.setLastCaretPosition(this, 1, markupViewTarget.getCaret().getDot());
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
		fileView.scroll_to_segment(this);
		reportCaretPosition();
		if (modifiedFlag == false) {
			Session.getUndoManager().markSnapshot();
		}
		if (modifiedFlag) {
			updateHeight();
		}
		modifiedFlag = false;
    }//GEN-LAST:event_markupViewTargetCaretUpdate

	void setEditorFont(Font f, int minHeight) {
		this.minHeight = minHeight;
		markupViewTarget.setFont(f);
	}

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
