package editor;

import static java.awt.event.InputEvent.CTRL_MASK;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import undo_manager.CaretPosition;
import util.Log;
import xliff_model.SegmentTag;
import xliff_model.TaggedText;
import xliff_model.exceptions.EncodeException;

public class SegmentView extends javax.swing.JPanel {

	DocumentListener targetDocumentListener = new DocumentListener() {

		void update() {
			segmentTag.setTargetText(markupViewTarget.getTaggedText());
			try {
				setStateField(SegmentTag.State.INITIAL);
			}
			catch (EncodeException ex) {
				Log.err(ex);
			}
			notifyUndoManager();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			update();
		}
	};

	SegmentTag segmentTag;
	FileView fileView;

	SegmentView(FileView fileView) {
		initComponents();
		this.fileView = fileView;
		jScrollPane3.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane3));
		jScrollPane4.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane4));
	}

	public void setSegmentTag(SegmentTag segmentTag) {
		this.segmentTag = segmentTag;
		unregisterListeners();
		markupViewSource.setTaggedText(segmentTag.getSourceText());
		markupViewTarget.setTaggedText(segmentTag.getTargetText());
		jLabelState.setText(segmentTag.getState().toString());
		registerListeners();
	}

	public void setTargetText(TaggedText t) {
		markupViewTarget.setTaggedText(t);
	}

	private void setStateField(SegmentTag.State state) throws EncodeException {
		segmentTag.setState(state);
		jLabelState.setText(state.toString());
	}

	void setState(SegmentTag.State state) throws EncodeException {
		setStateField(state);
		notifyUndoManager();
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	public FileView getFileView() {
		return fileView;
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

	void notifyUndoManager() {
		CaretPosition pos = new CaretPosition(SegmentView.this, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition());
		fileView.getUndoManager().getCurrentState().setModified(pos);
	}

	void handleKeyPress(KeyEvent evt) {
		boolean ctrl = evt.getModifiers() == CTRL_MASK;
		boolean z = evt.getKeyCode() == KeyEvent.VK_Z;
		if (ctrl && z) {
			fileView.getUndoManager().undo();
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        markupViewSource = new editor.MarkupView(this);
        jScrollPane4 = new javax.swing.JScrollPane();
        markupViewTarget = new editor.MarkupView(this);
        jLabelState = new javax.swing.JLabel();

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

        jLabelState.setText("jLabel1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelState)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelState))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void markupViewSourceCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewSourceCaretUpdate
		fileView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.SOURCE, markupViewSource.getCaretPosition()));
    }//GEN-LAST:event_markupViewSourceCaretUpdate

    private void markupViewSourceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewSourceFocusGained
		fileView.getUndoManager().markSnapshot();
		fileView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.SOURCE, markupViewSource.getCaretPosition()));
		markupViewSource.getCaret().setVisible(true);
    }//GEN-LAST:event_markupViewSourceFocusGained

    private void markupViewSourceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewSourceKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewSourceKeyPressed

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		fileView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
    }//GEN-LAST:event_markupViewTargetCaretUpdate

    private void markupViewTargetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewTargetFocusGained
		fileView.getUndoManager().markSnapshot();
		fileView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
    }//GEN-LAST:event_markupViewTargetFocusGained

    private void markupViewTargetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewTargetKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewTargetKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelState;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private editor.MarkupView markupViewSource;
    private editor.MarkupView markupViewTarget;
    // End of variables declaration//GEN-END:variables
}
