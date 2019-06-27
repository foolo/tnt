package editor;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import static java.awt.event.InputEvent.CTRL_MASK;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import undo_manager.CaretPosition;
import xliff_model.SegmentTag;
import xliff_model.TaggedText;
import xliff_model.exceptions.EncodeException;

public class SegmentView extends javax.swing.JPanel {

	private final DocumentListener targetDocumentListener = new DocumentListener() {

		void update(int caretPosition1, int caretPosition2) {
			segmentTag.setTargetText(markupViewTarget.getTaggedText());
			setStateField(SegmentTag.State.INITIAL);
			notifyUndoManager(caretPosition1, caretPosition2);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			update(e.getOffset(), e.getOffset() + e.getLength());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update(e.getOffset() + e.getLength(), e.getOffset());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			update(e.getOffset(), e.getOffset());
		}
	};

	SegmentTag segmentTag;
	private final XliffView xliffView;
	private final FileView fileView;

	SegmentView(XliffView xliffView, FileView fileView) {
		initComponents();
		this.xliffView = xliffView;
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

	void testEncode() throws EncodeException {
		segmentTag.testEncode();
	}

	private void setStateField(SegmentTag.State state) {
		segmentTag.setState(state);
		jLabelState.setText(state.toString());
	}

	void setState(SegmentTag.State state) {
		setStateField(state);
		int pos = markupViewTarget.getCaretPosition();
		notifyUndoManager(pos, pos);
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	public XliffView getXliffView() {
		return xliffView;
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

	void notifyUndoManager(int caretPos1, int caretPos2) {
		CaretPosition pos1 = new CaretPosition(SegmentView.this, CaretPosition.Column.TARGET, caretPos1);
		CaretPosition pos2 = new CaretPosition(SegmentView.this, CaretPosition.Column.TARGET, caretPos2);
		xliffView.getUndoManager().getCurrentState().setModified(pos1, pos2);
	}

	void handleKeyPress(KeyEvent evt) {
		if (evt.getModifiers() == CTRL_MASK) {
			switch (evt.getKeyCode()) {
				case KeyEvent.VK_Z:
					xliffView.getUndoManager().undo();
					evt.consume();
					break;
				case KeyEvent.VK_Y:
					xliffView.getUndoManager().redo();
					evt.consume();
					break;
			}
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
		xliffView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.SOURCE, markupViewSource.getCaretPosition()));
    }//GEN-LAST:event_markupViewSourceCaretUpdate

    private void markupViewSourceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewSourceFocusGained
		xliffView.getUndoManager().markSnapshot();
		xliffView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.SOURCE, markupViewSource.getCaretPosition()));
		markupViewSource.getCaret().setVisible(true);
		lastActiveSegmentView = this;
    }//GEN-LAST:event_markupViewSourceFocusGained

    private void markupViewSourceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewSourceKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewSourceKeyPressed

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		xliffView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
    }//GEN-LAST:event_markupViewTargetCaretUpdate

    private void markupViewTargetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewTargetFocusGained
		xliffView.getUndoManager().markSnapshot();
		xliffView.getUndoManager().setCaretPosition(new CaretPosition(this, CaretPosition.Column.TARGET, markupViewTarget.getCaretPosition()));
		lastActiveSegmentView = this;

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
