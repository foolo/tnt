package editor;

import static java.awt.event.InputEvent.CTRL_MASK;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import undo_manager.CaretPosition;
import undo_manager.UndoManager;
import xliff_model.SegmentTag;
import xliff_model.TaggedText;

public class SegmentView extends javax.swing.JPanel {

	DocumentListener targetDocumentListener = new DocumentListener() {

		@Override
		public void insertUpdate(DocumentEvent e) {
			segmentTag.setTargetText(markupViewTarget.getTaggedText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			segmentTag.setTargetText(markupViewTarget.getTaggedText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			segmentTag.setTargetText(markupViewTarget.getTaggedText());
			undoManager.getCurrentState().setModified(new CaretPosition(item_index, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
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
		jScrollPane3.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane3));
		jScrollPane4.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane4));
	}

	public void setSegmentTag(SegmentTag segmentTag, int item_index) {
		this.segmentTag = segmentTag;
		this.item_index = item_index;
		unregisterListeners();
		markupViewSource.setTaggedText(segmentTag.getSourceText());
		markupViewTarget.setTaggedText(segmentTag.getTargetText());
		jLabelIndex.setText("" + this.item_index);
		registerListeners();
	}

	public void setTargetText(TaggedText t) {
		markupViewTarget.setTaggedText(t);
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	public void registerListeners() {
		markupViewTarget.getDocument().addDocumentListener(targetDocumentListener);
	}

	public void unregisterListeners() {
		markupViewTarget.getDocument().removeDocumentListener(targetDocumentListener);
	}

	public void setTextPosition(CaretPosition.Column column, int position) {
		if (column == CaretPosition.Column.SOURCE) {
			markupViewSource.setCaretPosition(position);
			markupViewSource.grabFocus();
		}
		else {
			markupViewTarget.setCaretPosition(position);
			markupViewTarget.grabFocus();
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

        jLabelIndex = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        markupViewSource = new editor.MarkupView();
        jScrollPane4 = new javax.swing.JScrollPane();
        markupViewTarget = new editor.MarkupView();

        jLabelIndex.setText("jLabel1");

        markupViewSource.setEditable(false);
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
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabelIndex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelIndex)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void markupViewSourceCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewSourceCaretUpdate
		undoManager.setCaretPosition(new CaretPosition(item_index, CaretPosition.Column.SOURCE, markupViewSource.getCaretPosition()));
    }//GEN-LAST:event_markupViewSourceCaretUpdate

    private void markupViewSourceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewSourceFocusGained
		undoManager.markSnapshot();
		undoManager.setCaretPosition(new CaretPosition(item_index, CaretPosition.Column.SOURCE, markupViewSource.getCaretPosition()));
		markupViewSource.getCaret().setVisible(true);
    }//GEN-LAST:event_markupViewSourceFocusGained

    private void markupViewSourceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewSourceKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewSourceKeyPressed

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		undoManager.setCaretPosition(new CaretPosition(item_index, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
    }//GEN-LAST:event_markupViewTargetCaretUpdate

    private void markupViewTargetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewTargetFocusGained
		undoManager.markSnapshot();
		undoManager.setCaretPosition(new CaretPosition(item_index, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
    }//GEN-LAST:event_markupViewTargetFocusGained

    private void markupViewTargetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewTargetKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewTargetKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelIndex;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private editor.MarkupView markupViewSource;
    private editor.MarkupView markupViewTarget;
    // End of variables declaration//GEN-END:variables
}
