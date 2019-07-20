package editor;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import language.LanguageCollection;
import util.Settings;

public final class CreateXliffDialog extends BaseDialog {

	File inputFile = null;
	File xliffFile = null;
	File sklFile = null;

	public CreateXliffDialog(java.awt.Frame parent) {
		super(parent);
		initComponents();
		initButtons(jButtonOk, jButtonCancel);
		sourceLanguageComboBox.setLanguages(LanguageCollection.getLanguages());
		targetLanguageComboBox.setLanguages(LanguageCollection.getLanguages());
		update();
	}

	File getInputFile() {
		return inputFile;
	}

	File getXliffFile() {
		return xliffFile;
	}

	File getSkeletonFile() {
		return sklFile;
	}

	String getSourceLanguage() {
		return sourceLanguageComboBox.getSelectedLanguageCode();
	}

	String getTargetLanguage() {
		return targetLanguageComboBox.getSelectedLanguageCode();
	}

	String preValidateInput() {
		if (inputFile == null) {
			return "";
		}
		if (sourceLanguageComboBox.getSelectedLanguageCode().isEmpty()) {
			return "Please select a source language.";
		}
		if (targetLanguageComboBox.getSelectedLanguageCode().isEmpty()) {
			return "Please select a target language.";
		}
		return null;
	}

	void update() {
		if (inputFile == null) {
			jTextFieldInputFile.setText("");
		}
		else {
			jTextFieldInputFile.setText(inputFile.getAbsolutePath());
		}
		String error = preValidateInput();
		jButtonOk.setEnabled(error == null);
		if (error != null) {
			jLabelError.setText(error);
		}
		else {
			jLabelError.setText("");
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButtonChooseInputFiles = new javax.swing.JButton();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelError = new javax.swing.JLabel();
        jTextFieldInputFile = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        sourceLanguageComboBox = new editor.LanguageComboBox();
        targetLanguageComboBox = new editor.LanguageComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Input file");

        jButtonChooseInputFiles.setText("Choose...");
        jButtonChooseInputFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseInputFilesActionPerformed(evt);
            }
        });

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelError.setText("jLabelError");

        jTextFieldInputFile.setEditable(false);

        jLabel2.setText("Source language:");

        jLabel3.setText("Target language:");

        sourceLanguageComboBox.setMaximumRowCount(30);
        sourceLanguageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceLanguageComboBoxActionPerformed(evt);
            }
        });

        targetLanguageComboBox.setMaximumRowCount(30);
        targetLanguageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                targetLanguageComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldInputFile)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 328, Short.MAX_VALUE)
                        .addComponent(jButtonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonChooseInputFiles)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(targetLanguageComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(sourceLanguageComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonChooseInputFiles))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldInputFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(sourceLanguageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetLanguageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addComponent(jLabelError)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonOk))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChooseInputFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseInputFilesActionPerformed
		JFileChooser fc = new JFileChooser(Settings.getInputFileDirectory());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		Settings.setInputFileDirectory(fc.getSelectedFile());
		inputFile = fc.getSelectedFile().getAbsoluteFile();
		xliffFile = new File(inputFile.getAbsolutePath() + ".xlf");
		sklFile = new File(inputFile.getAbsolutePath() + ".skl");
		update();
    }//GEN-LAST:event_jButtonChooseInputFilesActionPerformed

	void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "", JOptionPane.ERROR_MESSAGE);
	}

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
		String preValidateResult = preValidateInput();
		if (preValidateResult != null) {
			JOptionPane.showMessageDialog(this, preValidateResult, "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		/*if (xliffFile.exists()) {
			JOptionPane.showMessageDialog(this, "Output XLIFF file already exists:\n" + xliffFile.getAbsolutePath(), "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (sklFile.exists()) {
			JOptionPane.showMessageDialog(this, "Output skeleton file already exists:\n" + sklFile.getAbsolutePath(), "", JOptionPane.ERROR_MESSAGE);
			return;
		}*/
		result = true;
		setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed

    private void sourceLanguageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceLanguageComboBoxActionPerformed
		update();
    }//GEN-LAST:event_sourceLanguageComboBoxActionPerformed

    private void targetLanguageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_targetLanguageComboBoxActionPerformed
		update();
    }//GEN-LAST:event_targetLanguageComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChooseInputFiles;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JTextField jTextFieldInputFile;
    private editor.LanguageComboBox sourceLanguageComboBox;
    private editor.LanguageComboBox targetLanguageComboBox;
    // End of variables declaration//GEN-END:variables
}
