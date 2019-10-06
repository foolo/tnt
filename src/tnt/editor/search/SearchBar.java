package tnt.editor.search;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tnt.editor.FileView;

public class SearchBar extends javax.swing.JPanel {

	FileView fileView;
	SearchContext searchContext = null;

	public SearchBar() {
		initComponents();
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

		ItemListener columnChangedListener = (ItemEvent e) -> {
			searchAndHighlight();
		};
		jRadioButtonSource.addItemListener(columnChangedListener);
		jRadioButtonTarget.addItemListener(columnChangedListener);
		jRadioButtonBoth.addItemListener(columnChangedListener);
	}

	public void setFileView(FileView fv) {
		fileView = fv;
	}

	final void updateCurrentMatchIndexLabel() {
		if (searchContext == null || searchContext.matchLocations.isEmpty()) {
			jLabelCurrentMatchIndex.setText("No matches");
			return;
		}
		jLabelCurrentMatchIndex.setText(searchContext.getCurrentMatchIndex() + 1 + " of " + searchContext.matchLocations.size());
	}

	void showSelection() {
		MatchLocation ml = searchContext.getCurrentMatchLocation();
		updateCurrentMatchIndexLabel();
		if (ml == null) {
			return;
		}
		fileView.highlightSelection(ml);
	}

	public void clearSelection() {
		MatchLocation currentMatchLocation = searchContext.getCurrentMatchLocation();
		if (currentMatchLocation == null) {
			return;
		}
		fileView.clearSelection(currentMatchLocation);
	}

	void searchAndHighlight() {
		int flags = jCheckBoxMatchCase.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
		boolean includeSource = jRadioButtonSource.isSelected() || jRadioButtonBoth.isSelected();
		boolean includeTarget = jRadioButtonTarget.isSelected() || jRadioButtonBoth.isSelected();
		searchContext = fileView.findMatches(jTextFieldSearchText.getText(), flags, includeSource, includeTarget);
		fileView.highlightMatches(searchContext.matchLocations);
		showSelection();
	}

	public void notifyUpdate() {
		searchContext = null;
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupColumn = new javax.swing.ButtonGroup();
        jTextFieldSearchText = new javax.swing.JTextField();
        jButtonSearchPrevious = new javax.swing.JButton();
        jButtonSearchNext = new javax.swing.JButton();
        jCheckBoxMatchCase = new javax.swing.JCheckBox();
        jLabelCurrentMatchIndex = new javax.swing.JLabel();
        jRadioButtonSource = new javax.swing.JRadioButton();
        jRadioButtonTarget = new javax.swing.JRadioButton();
        jRadioButtonBoth = new javax.swing.JRadioButton();

        jButtonSearchPrevious.setText("Previous");
        jButtonSearchPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchPreviousActionPerformed(evt);
            }
        });

        jButtonSearchNext.setText("Next");
        jButtonSearchNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchNextActionPerformed(evt);
            }
        });

        jCheckBoxMatchCase.setText("Aa");
        jCheckBoxMatchCase.setToolTipText("Match case");
        jCheckBoxMatchCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMatchCaseActionPerformed(evt);
            }
        });

        jLabelCurrentMatchIndex.setText("0 of 0");

        buttonGroupColumn.add(jRadioButtonSource);
        jRadioButtonSource.setText("Source");

        buttonGroupColumn.add(jRadioButtonTarget);
        jRadioButtonTarget.setText("Target");

        buttonGroupColumn.add(jRadioButtonBoth);
        jRadioButtonBoth.setSelected(true);
        jRadioButtonBoth.setText("Both");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldSearchText, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonSource)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonTarget)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonBoth)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonSearchPrevious)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSearchNext, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearchPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBoxMatchCase)
                    .addComponent(jLabelCurrentMatchIndex)
                    .addComponent(jRadioButtonBoth)
                    .addComponent(jRadioButtonTarget)
                    .addComponent(jRadioButtonSource)
                    .addComponent(jTextFieldSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSearchPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchPreviousActionPerformed
		if (searchContext == null) {
			searchAndHighlight();
		}
		clearSelection();
		searchContext.previousMatch();
		showSelection();
    }//GEN-LAST:event_jButtonSearchPreviousActionPerformed

    private void jButtonSearchNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchNextActionPerformed
		if (searchContext == null) {
			searchAndHighlight();
		}
		clearSelection();
		searchContext.nextMatch();
		showSelection();
    }//GEN-LAST:event_jButtonSearchNextActionPerformed

    private void jCheckBoxMatchCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMatchCaseActionPerformed
		searchAndHighlight();
    }//GEN-LAST:event_jCheckBoxMatchCaseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupColumn;
    private javax.swing.JButton jButtonSearchNext;
    private javax.swing.JButton jButtonSearchPrevious;
    private javax.swing.JCheckBox jCheckBoxMatchCase;
    private javax.swing.JLabel jLabelCurrentMatchIndex;
    private javax.swing.JRadioButton jRadioButtonBoth;
    private javax.swing.JRadioButton jRadioButtonSource;
    private javax.swing.JRadioButton jRadioButtonTarget;
    private javax.swing.JTextField jTextFieldSearchText;
    // End of variables declaration//GEN-END:variables
}
