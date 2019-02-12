package editor;

import static java.awt.event.InputEvent.CTRL_MASK;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import undo_manager.CaretPosition;
import undo_manager.UndoManager;
import xliff_model.SegmentTag;

public class SegmentView extends javax.swing.JPanel {

	DocumentListener sourceDocumentListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			segmentTag.setSourceText(jTextPaneSource.getText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.SOURCE, jTextPaneSource.getCaretPosition()));
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			segmentTag.setSourceText(jTextPaneSource.getText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.SOURCE, jTextPaneSource.getCaretPosition()));
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			segmentTag.setSourceText(jTextPaneSource.getText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.SOURCE, jTextPaneSource.getCaretPosition()));
		}
	};

	DocumentListener targetDocumentListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			segmentTag.setTargetText(jTextPaneTarget.getText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.TARGET, jTextPaneTarget.getCaretPosition()));
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			segmentTag.setTargetText(jTextPaneTarget.getText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.TARGET, jTextPaneTarget.getCaretPosition()));
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			segmentTag.setTargetText(jTextPaneTarget.getText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.TARGET, jTextPaneTarget.getCaretPosition()));
		}
	};

	SegmentTag segmentTag;
	int item_index;

	UndoManager undoManager = null;

	public SegmentView() {
		initComponents();
	}

	SegmentView(UndoManager undoManager) {
		initComponents();
		this.undoManager = undoManager;
	}

	public void setSegmentTag(SegmentTag segmentTag, int item_index) {
		this.segmentTag = segmentTag;
		this.item_index = item_index;
		unregisterListeners();
		jTextPaneSource.setText(segmentTag.getSourceText());
		jTextPaneTarget.setText(segmentTag.getTargetText());
		jLabelIndex.setText("" + this.item_index);
		registerListeners();
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	public void registerListeners() {
		jTextPaneSource.getDocument().addDocumentListener(sourceDocumentListener);
		jTextPaneTarget.getDocument().addDocumentListener(targetDocumentListener);
	}

	public void unregisterListeners() {
		jTextPaneSource.getDocument().removeDocumentListener(sourceDocumentListener);
		jTextPaneTarget.getDocument().removeDocumentListener(targetDocumentListener);
	}

	public void setTextPosition(CaretPosition.Column column, int position) {
		if (column == CaretPosition.Column.SOURCE) {
			jTextPaneSource.setCaretPosition(position);
			jTextPaneSource.grabFocus();
		}
		else {
			jTextPaneTarget.setCaretPosition(position);
			jTextPaneTarget.grabFocus();
		}
	}

	void handleKeyPress(KeyEvent evt) {
		boolean ctrl = evt.getModifiers() == CTRL_MASK;
		boolean z = evt.getKeyCode() == KeyEvent.VK_Z;
		if (ctrl && z) {
			undoManager.undo();
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneSource = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPaneTarget = new javax.swing.JTextPane();
        jLabelIndex = new javax.swing.JLabel();

        jTextPaneSource.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextPaneSourceFocusGained(evt);
            }
        });
        jTextPaneSource.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextPaneSourceKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTextPaneSource);
        jTextPaneSource.getAccessibleContext().setAccessibleName("");

        jTextPaneTarget.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextPaneTargetFocusGained(evt);
            }
        });
        jTextPaneTarget.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextPaneTargetKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jTextPaneTarget);

        jLabelIndex.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIndex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelIndex)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                        .addComponent(jScrollPane1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextPaneSourceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextPaneSourceKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_jTextPaneSourceKeyPressed

    private void jTextPaneSourceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextPaneSourceFocusGained
		undoManager.save();
		undoManager.setCaretPosition(new CaretPosition(item_index, CaretPosition.Column.SOURCE, jTextPaneSource.getCaretPosition()));
    }//GEN-LAST:event_jTextPaneSourceFocusGained

    private void jTextPaneTargetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextPaneTargetFocusGained
		undoManager.save();
		undoManager.setCaretPosition(new CaretPosition(item_index, CaretPosition.Column.TARGET, jTextPaneTarget.getCaretPosition()));
    }//GEN-LAST:event_jTextPaneTargetFocusGained

    private void jTextPaneTargetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextPaneTargetKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_jTextPaneTargetKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelIndex;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPaneSource;
    private javax.swing.JTextPane jTextPaneTarget;
    // End of variables declaration//GEN-END:variables
}
