package tnt.editor;

import javax.swing.SwingUtilities;

import tnt.language.SpellCheck;

public class SegmentView extends javax.swing.JPanel {

	public enum Column {
		SOURCE, TARGET
	}

	boolean modifiedFlag = false;

	public SegmentView() {
		initComponents();
		markupViewTarget.addDocumentListener(); // done after setEditorKit which resets the internal document
	}

	public void updateSegmentTag(String s) {
		markupViewTarget.updateTaggedText(s);
		applySpellcheck();
	}

	void update(int caretPosition1, int caretPosition2) {
		modifiedFlag = true;
		applySpellcheck();
	}

	void applySpellcheck() {
		SwingUtilities.invokeLater(() -> {
			SpellCheck.spellCheck(markupViewTarget);
		});
	}

	void updateHeight() {
		System.out.println("tnt.editor.SegmentView.updateHeight() " + markupViewTarget.getPreferredSize());
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();

        setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane4.setBorder(null);
        jScrollPane4.setOpaque(false);

        markupViewTarget.setOpaque(false);
        markupViewTarget.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                markupViewTargetCaretUpdate(evt);
            }
        });
        jScrollPane4.setViewportView(markupViewTarget);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void markupViewTargetCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_markupViewTargetCaretUpdate
		if (modifiedFlag) {
			updateHeight();
		}
		modifiedFlag = false;
    }//GEN-LAST:event_markupViewTargetCaretUpdate

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane4;
    public final tnt.editor.EditableMarkupView markupViewTarget = new tnt.editor.EditableMarkupView(this);
    // End of variables declaration//GEN-END:variables
}
