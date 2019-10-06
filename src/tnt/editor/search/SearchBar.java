package tnt.editor.search;

import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tnt.editor.FileView;

public class SearchBar extends javax.swing.JPanel {

	FileView fileView;
	SearchContext searchContext = new SearchContext(new ArrayList<MatchLocation>(), 0);

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
	}

	public void setFileView(FileView fv) {
		fileView = fv;
	}

	final void updateCurrentMatchIndexLabel() {
		jLabelCurrentMatchIndex.setText(searchContext.getIndexStatusLabel());
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
		searchContext = fileView.findMatches(jTextFieldSearchText.getText(), flags);
		fileView.highlightMatches(searchContext.matchLocations);
		showSelection();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldSearchText = new javax.swing.JTextField();
        jButtonSearchPrevious = new javax.swing.JButton();
        jButtonSearchNext = new javax.swing.JButton();
        jCheckBoxMatchCase = new javax.swing.JCheckBox();
        jLabelCurrentMatchIndex = new javax.swing.JLabel();

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

        jLabelCurrentMatchIndex.setText("0 of 0");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldSearchText, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldSearchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonSearchNext, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSearchPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCheckBoxMatchCase)
                        .addComponent(jLabelCurrentMatchIndex)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSearchPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchPreviousActionPerformed
		clearSelection();
		searchContext.previousMatch();
		showSelection();
    }//GEN-LAST:event_jButtonSearchPreviousActionPerformed

    private void jButtonSearchNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchNextActionPerformed
		clearSelection();
		searchContext.nextMatch();
		showSelection();
    }//GEN-LAST:event_jButtonSearchNextActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonSearchNext;
    private javax.swing.JButton jButtonSearchPrevious;
    private javax.swing.JCheckBox jCheckBoxMatchCase;
    private javax.swing.JLabel jLabelCurrentMatchIndex;
    private javax.swing.JTextField jTextFieldSearchText;
    // End of variables declaration//GEN-END:variables
}
