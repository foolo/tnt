package editor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import language.LanguageCollection;
import util.Log;
import util.Settings;

public final class CreatePackageDialog extends javax.swing.JDialog {

	class ValueChangedDocumentListener implements DocumentListener {

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
	}

	private boolean result = false;

	boolean getResult() {
		return result;
	}

	public CreatePackageDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();

		jTextFieldInputFile.getDocument().addDocumentListener(new ValueChangedDocumentListener());
		jTextFieldCommonDir.getDocument().addDocumentListener(new ValueChangedDocumentListener());
		jTextFieldPackageName.getDocument().addDocumentListener(new ValueChangedDocumentListener());

		jTextFieldCommonDir.setText(Settings.getPackageDirectory().getAbsolutePath());

		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
		String suggestedPackageName = "tnt_" + timestamp;
		jTextFieldPackageName.setText(suggestedPackageName);

		sourceLanguageComboBox.setLanguages(LanguageCollection.getLanguages());
		targetLanguageComboBox.setLanguages(LanguageCollection.getLanguages());

		update();
	}

	File getCommonDirectory() {
		return new File(jTextFieldCommonDir.getText()).getAbsoluteFile();
	}

	File getPackagePath() {
		return new File(getCommonDirectory(), jTextFieldPackageName.getText());
	}

	String getPackageName() {
		return jTextFieldPackageName.getText();
	}

	String getInputFile() {
		return jTextFieldInputFile.getText();
	}

	String getSourceLanguage() {
		return sourceLanguageComboBox.getSelectedLanguageCode();
	}

	String getTargetLanguage() {
		return targetLanguageComboBox.getSelectedLanguageCode();
	}

	String preValidateInput() {
		if (jTextFieldInputFile.getText().isEmpty()) {
			return "";
		}
		if (sourceLanguageComboBox.getSelectedLanguageCode().isEmpty()) {
			return "Please select a source language.";
		}
		if (targetLanguageComboBox.getSelectedLanguageCode().isEmpty()) {
			return "Please select a target language.";
		}
		if (new File(jTextFieldInputFile.getText()).exists() == false) {
			return "Input file does not exist.";
		}
		if (jTextFieldCommonDir.getText().isEmpty()) {
			return "Common package directory must not be empty.";
		}
		if (jTextFieldPackageName.getText().isEmpty()) {
			return "Package name must not be empty.";
		}
		File packagePath = getPackagePath();
		if (Files.exists(packagePath.toPath())) {
			return "Package path already exists: " + packagePath.getAbsolutePath();
		}
		return null;
	}

	void update() {
		File packagePath = getPackagePath();
		jLabelPackageDir.setText(packagePath.getAbsolutePath());
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
        jLabel2 = new javax.swing.JLabel();
        jTextFieldCommonDir = new javax.swing.JTextField();
        jButtonChoosePackageDirectory = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldPackageName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabelPackageDir = new javax.swing.JLabel();
        jButtonOk = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jLabelError = new javax.swing.JLabel();
        jTextFieldInputFile = new javax.swing.JTextField();
        sourceLanguageComboBox = new editor.LanguageComboBox();
        targetLanguageComboBox = new editor.LanguageComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Input file");

        jButtonChooseInputFiles.setText("Choose...");
        jButtonChooseInputFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChooseInputFilesActionPerformed(evt);
            }
        });

        jLabel2.setText("Common package directory");

        jButtonChoosePackageDirectory.setText("Choose...");
        jButtonChoosePackageDirectory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChoosePackageDirectoryActionPerformed(evt);
            }
        });

        jLabel3.setText("Package name");

        jLabel4.setText("Package path");

        jLabelPackageDir.setText("jLabelPackageDir");
        jLabelPackageDir.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButtonOk.setText("OK");
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelError.setText("jLabelError");

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

        jLabel5.setText("Source language:");

        jLabel6.setText("Target language:");

        jLabel7.setText("* = Spellcheck available");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldInputFile)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sourceLanguageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(targetLanguageComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldCommonDir)
                    .addComponent(jLabelPackageDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(137, 280, Short.MAX_VALUE)
                        .addComponent(jButtonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonChooseInputFiles))
                            .addComponent(jLabel3)
                            .addComponent(jTextFieldPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonChoosePackageDirectory))
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(sourceLanguageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(targetLanguageComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButtonChoosePackageDirectory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldCommonDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPackageDir)
                .addGap(18, 18, 18)
                .addComponent(jLabelError)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOk)
                    .addComponent(jButtonCancel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChooseInputFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseInputFilesActionPerformed
		JFileChooser fc = new JFileChooser(Settings.getInputFileDirectory());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Documents", "docx", "doc", "odt");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		Settings.setInputFileDirectory(fc.getSelectedFile());
		jTextFieldInputFile.setText(fc.getSelectedFile().getAbsolutePath());
		update();
    }//GEN-LAST:event_jButtonChooseInputFilesActionPerformed

    private void jButtonChoosePackageDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChoosePackageDirectoryActionPerformed
		JFileChooser fc = new JFileChooser(Settings.getPackageDirectory());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		jTextFieldCommonDir.setText(fc.getSelectedFile().getAbsolutePath());
		update();
    }//GEN-LAST:event_jButtonChoosePackageDirectoryActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
		setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

	void showError(String msg) {
		JOptionPane.showMessageDialog(this, msg, "", JOptionPane.ERROR_MESSAGE);
	}

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
		String preValidateResult = preValidateInput();
		if (preValidateResult != null) {
			JOptionPane.showMessageDialog(this, preValidateResult, "", JOptionPane.ERROR_MESSAGE);
			return;
		}

		Path commonDir = getCommonDirectory().toPath();
		if (Files.exists(commonDir)) {
			if (Files.isWritable(commonDir) == false) {
				showError("Directory is not writable: " + commonDir);
				return;
			}
		}
		else {
			try {
				Files.createDirectories(commonDir);
			}
			catch (IOException ex) {
				showError("Common package directory can not be created: " + commonDir);
				return;
			}
		}

		// create the package directory and delete it again to make sure it can be created
		Path packageDirectory = getPackagePath().toPath();
		try {
			Files.createDirectories(packageDirectory);
		}
		catch (IOException ex) {
			showError("Package directory can not be created: " + packageDirectory);
			return;
		}
		try {
			Files.deleteIfExists(packageDirectory);
		}
		catch (IOException ex) {
			Log.warn("Could not delete directory: " + packageDirectory + " (" + ex.toString() + ")");
		}

		Settings.setPackageDirectory(commonDir.toFile());
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
    private javax.swing.JButton jButtonChoosePackageDirectory;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelPackageDir;
    private javax.swing.JTextField jTextFieldCommonDir;
    private javax.swing.JTextField jTextFieldInputFile;
    private javax.swing.JTextField jTextFieldPackageName;
    private editor.LanguageComboBox sourceLanguageComboBox;
    private editor.LanguageComboBox targetLanguageComboBox;
    // End of variables declaration//GEN-END:variables
}
