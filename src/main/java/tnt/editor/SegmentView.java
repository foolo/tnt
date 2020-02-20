package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import tnt.editor.search.EditorRange;
import tnt.editor.util.MouseWheelScrollListener;
import tnt.editor.util.UnderlinerEditorKit;
import tnt.language.SpellCheck;
import tnt.undo_manager.UndoPosition;
import tnt.util.RegexUtil;
import tnt.util.Settings;
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
	static final Border PADDING_BORDER = new EmptyBorder(5, 0, 5, 0);
	static final Color NON_INITIAL_LABEL_COLOR = new Color(0, 160, 0);
	static final Color BACKGROUND_COLOR = Color.WHITE;
	static final Color GRID_COLOR = new Color(204, 204, 204);
	static final Color ACTIVE_SEGMENT_COLOR = new Color(241, 247, 255);
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
		markupViewTarget.setBorder(new CompoundBorder(markupViewTarget.getBorder(), PADDING_BORDER));
		selectionPainter = new DefaultHighlighter.DefaultHighlightPainter(markupViewTarget.getSelectionColor());
		jScrollPane4.getViewport().setOpaque(false);
	}

	boolean bypassListeners = false;

	public void updateSegmentTag(SegmentTag segmentTag) {
		this.segmentTag = segmentTag;
		try {
			bypassListeners = true;
			markupViewTarget.updateTaggedText(segmentTag.getTargetText());
		}
		finally {
			bypassListeners = false;
		}
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

	String qcMessagesToHtml(ArrayList<String> msgs) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><b>QC Messages</b>");
		for (String s : msgs) {
			sb.append("<p>").append(s).append("</p>");
		}
		sb.append("</body></html>");
		return sb.toString();
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

	void handleKeyPress(KeyEvent evt) {
		if (evt.getModifiersEx() == InputEvent.CTRL_DOWN_MASK) {
			switch (evt.getKeyCode()) {
				case KeyEvent.VK_Z:
					Session.getUndoManager().undo();
					evt.consume();
					return;
				case KeyEvent.VK_Y:
					Session.getUndoManager().redo();
					evt.consume();
					return;
				case KeyEvent.VK_V:
					Session.getUndoManager().markSnapshot();
					return;
				case KeyEvent.VK_F:
					fileView.searchBar1.focusSearchBox();
					return;
			}
			if (evt.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
				fileView.jumpToNextSegment(this);
				return;
			}
			if (evt.getExtendedKeyCode() == KeyEvent.VK_UP) {
				fileView.jumpToPreviousSegment(this);
				return;
			}
		}
		else if (evt.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
			fileView.jumpToNextSegment(this);
		}
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
			try {
				bypassListeners = bypassCaretListener;
				markupView.setCaretPosition(caretPosition);
			}
			finally {
				bypassListeners = false;
			}
		}
	}

	private static SegmentView activeSegmentView = null;

	static void setActiveSegmentView(SegmentView segmentView) {
		if (activeSegmentView != null) {
			activeSegmentView.setBackground(BACKGROUND_COLOR);
		}
		activeSegmentView = segmentView;
		activeSegmentView.setBackground(ACTIVE_SEGMENT_COLOR);
	}

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

        setBackground(BACKGROUND_COLOR);
        setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, GRID_COLOR));

        jScrollPane4.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 1, 0, 0, GRID_COLOR));
        jScrollPane4.setOpaque(false);

        markupViewTarget.setOpaque(false);
        markupViewTarget.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                markupViewTargetCaretUpdate(evt);
            }
        });
        markupViewTarget.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                markupViewTargetFocusGained(evt);
            }
        });
        markupViewTarget.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                markupViewTargetMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                markupViewTargetMouseReleased(evt);
            }
        });
        markupViewTarget.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                markupViewTargetKeyPressed(evt);
            }
        });
        jScrollPane4.setViewportView(markupViewTarget);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 763, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void markupViewTargetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewTargetFocusGained
		Session.getUndoManager().markSnapshot();
		setActiveSegmentView(this);
    }//GEN-LAST:event_markupViewTargetFocusGained

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		if (bypassListeners) {
			return;
		}
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

	void showTargetPopup(MouseEvent evt) {
		ArrayList<Integer> indexes = new ArrayList<>();
		String plainText = markupViewTarget.getPlainText(indexes);
		int textPositionTagged = markupViewTarget.viewToModel2D(evt.getPoint());
		int textPositionPlain = StringUtil.taggedToPlainIndex(textPositionTagged, indexes);
		MatchResult matchResult = RegexUtil.findSpellingUnitAtPosition(plainText, textPositionPlain);
		if (matchResult == null) {
			return;
		}
		String word = matchResult.group();
		if (SpellCheck.isMisspelled(word) == false) {
			return;
		}
		List<String> suggestions = SpellCheck.getSuggestions(word);
		JPopupMenu popupMenu = new JPopupMenu();
		if (suggestions.isEmpty()) {
			popupMenu.add(new JMenuItem("(No suggestions)"));
		}
		for (String s : suggestions) {
			JMenuItem menuItem = new JMenuItem(s);
			menuItem.addActionListener((ActionEvent e) -> {
				int startTagged = StringUtil.plainToTaggedIndex(matchResult.start(), indexes);
				int endTagged = StringUtil.plainToTaggedIndex(matchResult.end(), indexes);
				Session.getUndoManager().markSnapshot();
				markupViewTarget.replaceTaggedText(startTagged, endTagged, s);
				Session.getUndoManager().markSnapshot();
			});
			popupMenu.add(menuItem);
		}
		popupMenu.addSeparator();
		JMenuItem menuItem = new JMenuItem("Add to dictionary");
		menuItem.addActionListener((ActionEvent e) -> {
			Settings.addWordToWordlist(word);
		});
		popupMenu.add(menuItem);
		popupMenu.show(markupViewTarget, evt.getX(), evt.getY());
	}

    private void markupViewTargetMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_markupViewTargetMousePressed
		if (evt.isPopupTrigger()) {
			showTargetPopup(evt);
		}
    }//GEN-LAST:event_markupViewTargetMousePressed

    private void markupViewTargetMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_markupViewTargetMouseReleased
		if (evt.isPopupTrigger()) {
			showTargetPopup(evt);
		}
    }//GEN-LAST:event_markupViewTargetMouseReleased

    private void markupViewTargetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewTargetKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewTargetKeyPressed

	void setEditorFont(Font f, int minHeight) {
		this.minHeight = minHeight;
		markupViewTarget.setFont(f);
	}

	static int getMinHeightForFont(Font font) {
		JTextPane textPane = new JTextPane();
		textPane.setBorder(new CompoundBorder(textPane.getBorder(), PADDING_BORDER));
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
