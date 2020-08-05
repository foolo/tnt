package com.example;

public class MainForm extends javax.swing.JFrame {

	public MainForm() {
		initComponents();
		editableMarkupView1.addDocumentListener(); // done after setEditorKit which resets the internal document
	}

	public void load_file() {
		editableMarkupView1.setText("sample text sample text");
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainForm mainForm = new MainForm();
				mainForm.setLocationRelativeTo(null);
				mainForm.setVisible(true);
				mainForm.load_file();
			}
		});
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        editableMarkupView1 = new com.example.EditableMarkupView();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(840, 0));

        editableMarkupView1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                editableMarkupView1CaretUpdate(evt);
            }
        });
        jScrollPane1.setViewportView(editableMarkupView1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(423, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(315, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void editableMarkupView1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_editableMarkupView1CaretUpdate
		editableMarkupView1.caretUpdate();
    }//GEN-LAST:event_editableMarkupView1CaretUpdate


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.example.EditableMarkupView editableMarkupView1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
