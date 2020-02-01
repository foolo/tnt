package tnt.editor;

import java.io.File;
import javax.swing.SwingUtilities;
import tnt.util.FileUtil;

public class ExportCompletedPanel extends javax.swing.JPanel {

	private final File outputFile;

	public ExportCompletedPanel(File outputFile) {
		initComponents();
		jTextFieldOutputDir.setText(outputFile.getAbsolutePath());
		jTextFieldOutputDir.setBackground(null);
		this.outputFile = outputFile;
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldOutputDir = new javax.swing.JTextField();
        jButtonLocateInBrowser = new javax.swing.JButton();
        jButtonOpenFile = new javax.swing.JButton();

        jLabel5.setText("Export completed sucecssfully!");

        jLabel1.setText("Output file:");

        jTextFieldOutputDir.setEditable(false);
        jTextFieldOutputDir.setText("jTextFieldOutputDir");

        jButtonLocateInBrowser.setText("Locate in browser");
        jButtonLocateInBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLocateInBrowserActionPerformed(evt);
            }
        });

        jButtonOpenFile.setText("Open file and close dialog");
        jButtonOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldOutputDir)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButtonOpenFile)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonLocateInBrowser)))
                        .addGap(0, 183, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOpenFile)
                    .addComponent(jButtonLocateInBrowser))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonLocateInBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLocateInBrowserActionPerformed
		FileUtil.desktopOpen(this, outputFile.getParentFile());
    }//GEN-LAST:event_jButtonLocateInBrowserActionPerformed

    private void jButtonOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenFileActionPerformed
		FileUtil.desktopOpen(this, outputFile);
		SwingUtilities.getWindowAncestor(this).setVisible(false);
    }//GEN-LAST:event_jButtonOpenFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLocateInBrowser;
    private javax.swing.JButton jButtonOpenFile;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jTextFieldOutputDir;
    // End of variables declaration//GEN-END:variables
}
