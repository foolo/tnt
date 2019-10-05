package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tnt.xliff_model.FileTag;
import tnt.xliff_model.SegmentTag;

public class FileView extends javax.swing.JPanel {

	static class CaretPosition {

		final SegmentView segmentView;
		final int column;
		final int caretPos;

		public CaretPosition(SegmentView segmentView, int column, int caretPos) {
			this.segmentView = segmentView;
			this.column = column;
			this.caretPos = caretPos;
		}
	}

	CaretPosition lastCaretPosition = null;

	ArrayList<MatchLocation> matchLocations = new ArrayList<>();
	int currentMatchIndex = 0;

	public FileView(String fileId) {
		initComponents();
		jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
		jTextFieldSearchText.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				searchAndHighlight();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				searchAndHighlight();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		updateCurrentMatchIndexLabel();
	}

	public void update_model(FileTag fileTag) {
		ArrayList<SegmentTag> segmentTags = fileTag.getSegmentsArray();
		for (int i = 0; i < segmentTags.size(); i++) {
			SegmentView segmentView = (SegmentView) jPanelItems.getComponent(i);
			segmentView.updateSegmentTag(segmentTags.get(i));
		}
	}

	void scroll_to_segment(SegmentView segmentView) {
		int dest_y = segmentView.getBounds().y;
		int dest_h = segmentView.getBounds().height;
		int view_y = jScrollPane1.getVerticalScrollBar().getValue();
		int view_h = jScrollPane1.getVerticalScrollBar().getVisibleAmount();
		if (dest_y < view_y) {
			jScrollPane1.getVerticalScrollBar().setValue(dest_y);
		}
		else if (dest_y + dest_h > view_y + view_h) {
			jScrollPane1.getVerticalScrollBar().setValue(dest_y + dest_h - view_h);
		}
	}

	void populate_segments(ArrayList<SegmentTag> segmentTags) {
		for (SegmentTag st : segmentTags) {
			jPanelItems.add(new SegmentView(this, st.getId()));
		}
	}

	void getSegmentViews(ArrayList<SegmentView> segmentViews) {
		for (Component c : jPanelItems.getComponents()) {
			segmentViews.add((SegmentView) c);
		}
	}

	void jumpToNextSegment(SegmentView currentSegment) {
		boolean found = false;
		for (Component c : jPanelItems.getComponents()) {
			if (found) {
				SegmentView segmentView = ((SegmentView) c);
				segmentView.navigateToView(currentSegment.getActiveColumn(), 0);
				scroll_to_segment(segmentView);
				return;
			}
			if ((SegmentView) c == currentSegment) {
				found = true;
			}
		}
	}

	void jumpToPreviousSegment(SegmentView currentSegment) {
		SegmentView previousSegment = null;
		for (Component c : jPanelItems.getComponents()) {
			if ((SegmentView) c == currentSegment) {
				if (previousSegment != null) {
					previousSegment.navigateToView(currentSegment.getActiveColumn(), 0);
					scroll_to_segment(previousSegment);
				}
				return;
			}
			previousSegment = (SegmentView) c;
		}
	}

	boolean match(String term, String text, boolean matchCase) {
		if (matchCase) {
			return text.contains(term);
		}
		else {
			return Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS).matcher(text).find();
		}
	}

	int getIndexOfSegmentView(SegmentView segmentView) {
		Component[] components = jPanelItems.getComponents();
		for (int i = 0; i < components.length; i++) {
			SegmentView sv = (SegmentView) components[i];
			if (sv == segmentView) {
				return i;
			}
		}
		return -1;
	}

	void calculateCurrentMatchIndex() {
		currentMatchIndex = 0;
		if (lastCaretPosition == null) {
			return;
		}
		int lastCaretPositionSegmentIndex = getIndexOfSegmentView(lastCaretPosition.segmentView);
		for (int i = 0; i < matchLocations.size(); i++) {
			MatchLocation ml = matchLocations.get(i);
			int[] matchPos = new int[]{ml.segmentIndex, ml.column, ml.range.start};
			int[] caretPos = new int[]{lastCaretPositionSegmentIndex, lastCaretPosition.column, lastCaretPosition.caretPos};
			if (Arrays.compare(matchPos, caretPos) >= 0) {
				// matchPos same or after caretPos
				currentMatchIndex = i;
				return;
			}
		}
	}

	MatchLocation getCurrentMatchLocation() {
		if (matchLocations.isEmpty()) {
			return null;
		}
		while (currentMatchIndex >= matchLocations.size()) {
			currentMatchIndex -= matchLocations.size();
		}
		while (currentMatchIndex < 0) {
			currentMatchIndex += matchLocations.size();
		}
		return matchLocations.get(currentMatchIndex);
	}

	final void updateCurrentMatchIndexLabel() {
		if (matchLocations.isEmpty()) {
			jLabelCurrentMatchIndex.setText("No matches");
			return;
		}
		jLabelCurrentMatchIndex.setText(currentMatchIndex + 1 + " of " + matchLocations.size());
	}

	void selectMatch() {
		MatchLocation ml = getCurrentMatchLocation();
		updateCurrentMatchIndexLabel();
		if (ml == null) {
			return;
		}
		SegmentView segmentView = (SegmentView) jPanelItems.getComponent(ml.segmentIndex);
		segmentView.select(ml.column, ml.range);
		lastCaretPosition = new CaretPosition(segmentView, ml.column, ml.range.start);
		scroll_to_segment(segmentView);
	}

	void clearSelection() {
		MatchLocation currentMatchLocation = getCurrentMatchLocation();
		if (currentMatchLocation == null) {
			return;
		}
		SegmentView segmentView = (SegmentView) jPanelItems.getComponent(currentMatchLocation.segmentIndex);
		segmentView.clearHighlighting();
		for (MatchLocation ml : matchLocations) {
			if (ml.segmentIndex == currentMatchLocation.segmentIndex) {
				segmentView.applyHighlighting(ml.column, ml.range);
			}
		}
	}

	void findMatches() {
		matchLocations.clear();
		Component[] components = jPanelItems.getComponents();
		for (int i = 0; i < components.length; i++) {
			SegmentView sv = (SegmentView) components[i];
			int flags = jCheckBoxMatchCase.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
			sv.findMatches(jTextFieldSearchText.getText(), flags, i, matchLocations);
		}
	}

	void applyHighlighting() {
		Component[] components = jPanelItems.getComponents();
		for (Component c : components) {
			((SegmentView) c).clearHighlighting();
		}
		for (MatchLocation ml : matchLocations) {
			SegmentView sv = (SegmentView) components[ml.segmentIndex];
			sv.applyHighlighting(ml.column, ml.range);
		}
	}

	void searchAndHighlight() {
		findMatches();
		calculateCurrentMatchIndex();
		applyHighlighting();
		selectMatch();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelItems = new javax.swing.JPanel();
        jCheckBoxMatchCase = new javax.swing.JCheckBox();
        jTextFieldSearchText = new javax.swing.JTextField();
        jButtonSearchNext = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabelCurrentMatchIndex = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(800, 0));

        jPanelItems.setLayout(new javax.swing.BoxLayout(jPanelItems, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(jPanelItems);

        jCheckBoxMatchCase.setText("Aa");
        jCheckBoxMatchCase.setToolTipText("Match case");

        jButtonSearchNext.setText("Next");
        jButtonSearchNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchNextActionPerformed(evt);
            }
        });

        jButton2.setText("Previous");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabelCurrentMatchIndex.setText("0 of 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldSearchText, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSearchNext)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxMatchCase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCurrentMatchIndex)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonSearchNext, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxMatchCase)
                            .addComponent(jLabelCurrentMatchIndex))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSearchNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchNextActionPerformed
		clearSelection();
		currentMatchIndex++;
		selectMatch();
    }//GEN-LAST:event_jButtonSearchNextActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        clearSelection();
		currentMatchIndex--;
		selectMatch();
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonSearchNext;
    private javax.swing.JCheckBox jCheckBoxMatchCase;
    private javax.swing.JLabel jLabelCurrentMatchIndex;
    private javax.swing.JPanel jPanelItems;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldSearchText;
    // End of variables declaration//GEN-END:variables
}
