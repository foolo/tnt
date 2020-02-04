package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import static java.awt.event.InputEvent.CTRL_MASK;
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
import tnt.qc.Qc;
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
	static final DefaultHighlighter.DefaultHighlightPainter FILTER_MATCH_HIGHLIGHT_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	final DefaultHighlighter.DefaultHighlightPainter selectionPainter;

	SegmentView(FileView fileView, String segmentId) {
		initComponents();
		this.fileView = fileView;
		jScrollPane3.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane3));
		jScrollPane4.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane4));
		markupViewSource.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK), "none");
		markupViewTarget.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK), "none");
		jLabelValidationError.setVisible(false);
		jLabelQc.setVisible(false);
		markupViewTarget.setEditorKit(new UnderlinerEditorKit());
		markupViewTarget.addDocumentListener(); // done after setEditorKit which resets the internal document
		markupViewTarget.addDocumentFilter();
		markupViewSource.setBorder(new CompoundBorder(markupViewSource.getBorder(), PADDING_BORDER));
		markupViewTarget.setBorder(new CompoundBorder(markupViewTarget.getBorder(), PADDING_BORDER));
		jLabelId.setText(StringUtil.leftPad(segmentId, ' ', 3));
		selectionPainter = new DefaultHighlighter.DefaultHighlightPainter(markupViewTarget.getSelectionColor());
		jScrollPane3.getViewport().setOpaque(false);
		jScrollPane4.getViewport().setOpaque(false);
	}

	void updateStateLabel(SegmentTag.State state) {
		Session.getUndoManager().updateProgress();
		jLabelState.setText(state.toString());
		if (state == SegmentTag.State.INITIAL) {
			jLabelState.setForeground(Color.DARK_GRAY);
		}
		else {
			jLabelState.setForeground(NON_INITIAL_LABEL_COLOR);
		}
	}

	public void updateSegmentTag(SegmentTag segmentTag) {
		this.segmentTag = segmentTag;
		markupViewSource.setTaggedText(segmentTag.getSourceText());
		markupViewTarget.updateTaggedText(segmentTag.getTargetText());
		updateStateLabel(segmentTag.getState());
		if (segmentTag.getState() != SegmentTag.State.INITIAL) {
			jLabelValidationError.setVisible(false);
			ArrayList<String> qcRes = Qc.runQc(getSegmentTag());
			showQcMsg(qcRes);
		}
	}

	public void setTargetText(TaggedText t) {
		markupViewTarget.replaceTaggedText(t);
	}

	public void insertText(String s) {
		markupViewTarget.insertText(markupViewTarget.getCaretPosition(), s);
	}

	private boolean setStateField(SegmentTag.State state) {
		boolean res = segmentTag.setState(state);
		if (res) {
			updateStateLabel(segmentTag.getState());
		}
		if (state != SegmentTag.State.INITIAL) {
			jLabelValidationError.setVisible(false);
		}
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

	void showValidationError(String message) {
		jLabelValidationError.setVisible(true);
		jLabelValidationError.setToolTipText("<html><body><b>Target text errors</b><p>" + message + "</p></body></html>");
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

	void showQcMsg(ArrayList<String> msgs) {
		if (msgs.isEmpty()) {
			jLabelQc.setVisible(false);
			jLabelQc.setToolTipText(null);
			return;
		}
		jLabelQc.setVisible(true);
		jLabelQc.setToolTipText(qcMessagesToHtml(msgs));
	}

	void notifyUndoManager(int caretPos1, int caretPos2) {
		UndoPosition pos1 = new UndoPosition(this, caretPos1);
		UndoPosition pos2 = new UndoPosition(this, caretPos2);
		Session.getUndoManager().getCurrentState().setModified(pos1, pos2);
	}

	void update(int caretPosition1, int caretPosition2) {
		segmentTag.setTargetText(markupViewTarget.getTaggedText());
		setStateField(SegmentTag.State.INITIAL);
		notifyUndoManager(caretPosition1, caretPosition2);
		modifiedFlag = true;
		fileView.notifyUpdate();
	}

	void handleKeyPress(KeyEvent evt) {
		fileView.scroll_to_segment(this);
		if (evt.getModifiers() == CTRL_MASK) {
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
					fileView.focusSearchBox();
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
		switch (column) {
			case SOURCE:
				return markupViewSource;
			case TARGET:
			default:
				return markupViewTarget;
		}
	}

	Column getActiveColumn() {
		if (markupViewSource.hasFocus()) {
			return Column.SOURCE;
		}
		return Column.TARGET;
	}

	void navigateToView(Column column, Integer caretPosition) {
		MarkupView markupView = getMarkupView(column);
		markupView.grabFocus();
		if (caretPosition != null) {
			markupView.setCaretPosition(caretPosition);
		}
	}

	private static SegmentView lastActiveSegmentView = null;

	static void setActiveSegmentView(SegmentView segmentView) {
		lastActiveSegmentView = segmentView;
	}

	static SegmentView getActiveSegmentView() {
		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (c instanceof MarkupView) {
			MarkupView mv = (MarkupView) c;
			return mv.getSegmentView();
		}
		return lastActiveSegmentView;
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
		MarkupView mv = (column == 0) ? markupViewSource : markupViewTarget;
		ArrayList<Integer> indexes = new ArrayList<>();
		String text = mv.getPlainText(indexes);
		ArrayList<MatchResult> matchResults = findMatches(term, text, flags);
		ArrayList<EditorRange> editorRanges = toEditorRange(matchResults, indexes);
		for (EditorRange range : editorRanges) {
			searchResultsOut.add(new MatchLocation(segmentIndex, column, range));
		}
	}

	void clearHighlighting() {
		markupViewSource.getHighlighter().removeAllHighlights();
		markupViewTarget.getHighlighter().removeAllHighlights();
	}

	void highlightMatch(int column, EditorRange range) {
		MarkupView mv = (column == 0) ? markupViewSource : markupViewTarget;
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
		clearSelection(markupViewSource);
		clearSelection(markupViewTarget);
	}

	void highlightSelection(int column, EditorRange range) {
		MarkupView mv = (column == 0) ? markupViewSource : markupViewTarget;
		mv.applyHighlighting(range, selectionPainter);
	}

	void updateHeight() {
		Dimension d = getPreferredSize();
		int newHeight = Math.max(minHeight, Math.max(markupViewSource.getPreferredSize().height, markupViewTarget.getPreferredSize().height));
		if (newHeight != d.height) {
			d.height = newHeight + 3;
			setPreferredSize(d);
		}
		fileView.validate();
	}

	void reportCaretPosition(int column, int textPos) {
		fileView.setLastCaretPosition(this, column, textPos);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        markupViewSource = new tnt.editor.MarkupView(this);
        jScrollPane4 = new javax.swing.JScrollPane();
        markupViewTarget = new tnt.editor.EditableMarkupView(this);
        jPanel2 = new javax.swing.JPanel();
        jLabelState = new javax.swing.JLabel();
        jLabelValidationError = new javax.swing.JLabel();
        jLabelQc = new javax.swing.JLabel();
        jLabelId = new javax.swing.JLabel();

        setBackground(BACKGROUND_COLOR);

        jPanel1.setMinimumSize(new java.awt.Dimension(50, 50));
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(50, 50));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jScrollPane3.setOpaque(false);

        markupViewSource.setEditable(false);
        markupViewSource.setOpaque(false);
        markupViewSource.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                markupViewSourceCaretUpdate(evt);
            }
        });
        markupViewSource.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                markupViewSourceFocusGained(evt);
            }
        });
        markupViewSource.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                markupViewSourceKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(markupViewSource);

        jPanel1.add(jScrollPane3);

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
            public void focusLost(java.awt.event.FocusEvent evt) {
                markupViewTargetFocusLost(evt);
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

        jPanel1.add(jScrollPane4);

        jPanel2.setMinimumSize(new java.awt.Dimension(50, 50));
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(50, 50));

        jLabelState.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        jLabelState.setText("jLabel1");

        jLabelValidationError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelValidationError.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tnt/images/dialog-error.png"))); // NOI18N
        jLabelValidationError.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabelQc.setForeground(new java.awt.Color(255, 204, 0));
        jLabelQc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/tnt/images/dialog-information.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelState)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabelValidationError)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelQc)))
                .addContainerGap(67, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelState)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelValidationError)
                    .addComponent(jLabelQc))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabelId.setFont(new java.awt.Font("DejaVu Sans Mono", 1, 12)); // NOI18N
        jLabelId.setText("id");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabelId)
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabelId)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void markupViewSourceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewSourceFocusGained
		Session.getUndoManager().markSnapshot();
		markupViewSource.getCaret().setVisible(true);
		lastActiveSegmentView = this;
    }//GEN-LAST:event_markupViewSourceFocusGained

    private void markupViewTargetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewTargetFocusGained
		Session.getUndoManager().markSnapshot();
		lastActiveSegmentView = this;
		applySpellcheck();
    }//GEN-LAST:event_markupViewTargetFocusGained

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		reportCaretPosition(1, markupViewTarget.getCaret().getDot());
		if (modifiedFlag == false) {
			Session.getUndoManager().markSnapshot();
		}
		applySpellcheck();
		if (modifiedFlag) {
			updateHeight();
		}
		modifiedFlag = false;
    }//GEN-LAST:event_markupViewTargetCaretUpdate

    private void markupViewTargetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewTargetFocusLost
		applySpellcheck();
    }//GEN-LAST:event_markupViewTargetFocusLost

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

    private void markupViewSourceCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewSourceCaretUpdate
		reportCaretPosition(0, markupViewSource.getCaret().getDot());
    }//GEN-LAST:event_markupViewSourceCaretUpdate

    private void markupViewSourceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewSourceKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewSourceKeyPressed

    private void markupViewTargetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewTargetKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewTargetKeyPressed

	void setEditorFont(Font f, int minHeight) {
		this.minHeight = minHeight;
		markupViewSource.setFont(f);
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
    private javax.swing.JLabel jLabelId;
    private javax.swing.JLabel jLabelQc;
    private javax.swing.JLabel jLabelState;
    private javax.swing.JLabel jLabelValidationError;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private tnt.editor.MarkupView markupViewSource;
    private tnt.editor.EditableMarkupView markupViewTarget;
    // End of variables declaration//GEN-END:variables
}
