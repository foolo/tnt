package editor;

import java.awt.Component;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import static java.awt.event.InputEvent.CTRL_MASK;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.event.DocumentListener;
import undo_manager.CaretPosition;
import undo_manager.UndoManager;
import xliff_model.SegmentTag;
import xliff_model.TaggedText;
import xliff_model.ValidationPath;

public class SegmentView extends javax.swing.JPanel {

	private final DocumentListener targetDocumentListener = new DocumentListener() {

		EventType lastEventType = null;

		void update(int caretPosition1, int caretPosition2, EventType eventType) {
			if (eventType != lastEventType) {
				undoManager.markSnapshot();
			}
			lastEventType = eventType;
			segmentTag.setTargetText(markupViewTarget.getTaggedText());
			setStateField(SegmentTag.State.INITIAL);
			notifyUndoManager(caretPosition1, caretPosition2);
			ignoreNextCaretUpdate = true;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			update(e.getOffset(), e.getOffset() + e.getLength(), e.getType());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update(e.getOffset() + e.getLength(), e.getOffset(), e.getType());
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
		}
	};

	SegmentTag segmentTag;
	private final UndoManager undoManager;
	private final FileView fileView;
	private final String segmentId;
	boolean ignoreNextCaretUpdate = false;

	SegmentView(UndoManager undoManager, FileView fileView, String segmentId) {
		initComponents();
		this.undoManager = undoManager;
		this.fileView = fileView;
		this.segmentId = segmentId;
		jScrollPane3.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane3));
		jScrollPane4.addMouseWheelListener(new MouseWheelScrollListener(jScrollPane4));
		markupViewSource.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK), "none");
		markupViewTarget.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK), "none");
		jLabelValidationError.setText("");
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

	private boolean setStateField(SegmentTag.State state) {
		boolean res = segmentTag.setState(state);
		jLabelState.setText(segmentTag.getState().toString());
		if (state != SegmentTag.State.INITIAL) {
			jLabelValidationError.setText("");
		}
		return res;
	}

	void setState(SegmentTag.State state) {
		if (setStateField(state)) {
			int pos = markupViewTarget.getCaretPosition();
			notifyUndoManager(pos, pos);
			undoManager.markSnapshot();
		}
	}

	public SegmentTag getSegmentTag() {
		return segmentTag;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public FileView getFileView() {
		return fileView;
	}

	public String getSegmentId() {
		return segmentId;
	}

	public void registerListeners() {
		markupViewTarget.getDocument().addDocumentListener(targetDocumentListener);
	}

	public void unregisterListeners() {
		markupViewTarget.getDocument().removeDocumentListener(targetDocumentListener);
	}

	void showValidationError(String message, ValidationPath path) {
		jLabelValidationError.setText("Tag errors found");
		String tagIdDetails = "";
		if (path != null && path.codeId.isEmpty() == false) {
			tagIdDetails = "Tag ID=" + path.codeId + ": ";
		}
		jLabelValidationError.setToolTipText(tagIdDetails + message);
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
		undoManager.getCurrentState().setModified(pos1, pos2);
	}

	void handleKeyPress(KeyEvent evt) {
		if (evt.getModifiers() == CTRL_MASK) {
			switch (evt.getKeyCode()) {
				case KeyEvent.VK_Z:
					undoManager.undo();
					evt.consume();
					break;
				case KeyEvent.VK_Y:
					undoManager.redo();
					evt.consume();
					break;
			}
		}
	}

	void grabFocusTarget() {
		markupViewTarget.grabFocus();
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        markupViewSource = new editor.MarkupView(this);
        jScrollPane4 = new javax.swing.JScrollPane();
        markupViewTarget = new editor.MarkupView(this);
        jPanel2 = new javax.swing.JPanel();
        jLabelState = new javax.swing.JLabel();
        jLabelValidationError = new javax.swing.JLabel();

        jPanel1.setMinimumSize(new java.awt.Dimension(50, 50));
        jPanel1.setPreferredSize(new java.awt.Dimension(50, 50));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        markupViewSource.setEditable(false);
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

        jPanel1.add(jScrollPane4);

        jPanel2.setMinimumSize(new java.awt.Dimension(50, 50));
        jPanel2.setPreferredSize(new java.awt.Dimension(50, 50));

        jLabelState.setText("jLabel1");

        jLabelValidationError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelValidationError.setText("jLabel1");
        jLabelValidationError.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelState)
                    .addComponent(jLabelValidationError))
                .addContainerGap(70, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelState)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelValidationError)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void markupViewSourceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewSourceFocusGained
		undoManager.markSnapshot();
		markupViewSource.getCaret().setVisible(true);
		lastActiveSegmentView = this;
    }//GEN-LAST:event_markupViewSourceFocusGained

    private void markupViewSourceKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewSourceKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewSourceKeyPressed

    private void markupViewTargetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_markupViewTargetFocusGained
		undoManager.markSnapshot();
		lastActiveSegmentView = this;
    }//GEN-LAST:event_markupViewTargetFocusGained

    private void markupViewTargetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_markupViewTargetKeyPressed
		handleKeyPress(evt);
    }//GEN-LAST:event_markupViewTargetKeyPressed

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		if (ignoreNextCaretUpdate) {
			ignoreNextCaretUpdate = false;
			return;
		}
		undoManager.markSnapshot();
    }//GEN-LAST:event_markupViewTargetCaretUpdate

	void setEditorFont(Font f) {
		markupViewSource.setFont(f);
		markupViewTarget.setFont(f);
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelState;
    private javax.swing.JLabel jLabelValidationError;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private editor.MarkupView markupViewSource;
    private editor.MarkupView markupViewTarget;
    // End of variables declaration//GEN-END:variables
}
