package editor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

	DefaultListModel inputFiles = new DefaultListModel();

	public CreatePackageDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		jList1.setModel(inputFiles);

		jTextFieldCommonDir.getDocument().addDocumentListener(new ValueChangedDocumentListener());
		jTextFieldPackageName.getDocument().addDocumentListener(new ValueChangedDocumentListener());

		jTextFieldCommonDir.setText(Settings.getPackageDirectory().getAbsolutePath());

		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
		String suggestedPackageName = "tnt_" + timestamp;
		jTextFieldPackageName.setText(suggestedPackageName);

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

	ArrayList<String> getInputFiles() {
		ArrayList<String> result = new ArrayList<>();
		for (Object o : inputFiles.toArray()) {
			o = new File("");
			if (o instanceof String) {
				result.add((String) o);
			}
			else {
				Log.warn("getInputFiles: object not instance of String (" + ((o == null) ? "null" : o.getClass().getName()));
			}
		}
		return result;
	}

	String preValidateInput() {
		if (inputFiles.isEmpty()) {
			return "";
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabelError = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Input files");

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

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jLabelError.setForeground(new java.awt.Color(255, 0, 0));
        jLabelError.setText("jLabelError");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelError, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelPackageDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextFieldCommonDir)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jTextFieldPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonChooseInputFiles))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonChoosePackageDirectory)))
                        .addGap(0, 172, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButtonChooseInputFiles))
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
                .addComponent(jLabelError)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonOk)
                    .addComponent(jButtonCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChooseInputFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChooseInputFilesActionPerformed
		JFileChooser fc = new JFileChooser(Settings.getPackageDirectory());
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File[] files = fc.getSelectedFiles();
		inputFiles.removeAllElements();
		for (File f : files) {
			inputFiles.addElement(f.getAbsolutePath());
		}
		update();
    }//GEN-LAST:event_jButtonChooseInputFilesActionPerformed

    private void jButtonChoosePackageDirectoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChoosePackageDirectoryActionPerformed
		JFileChooser fc = new JFileChooser(Settings.getPackageDirectory());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		fc.getSelectedFile();
		jTextFieldCommonDir.setText(fc.getSelectedFile().getAbsolutePath());
		update();
    }//GEN-LAST:event_jButtonChoosePackageDirectoryActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
		setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

	void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, "", JOptionPane.ERROR_MESSAGE);
	}

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
		String preValidateResult = preValidateInput();
		if (preValidateResult != null) {
			JOptionPane.showMessageDialog(null, preValidateResult, "", JOptionPane.ERROR_MESSAGE);
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

		setVisible(false);
    }//GEN-LAST:event_jButtonOkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonChooseInputFiles;
    private javax.swing.JButton jButtonChoosePackageDirectory;
    private javax.swing.JButton jButtonOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelError;
    private javax.swing.JLabel jLabelPackageDir;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldCommonDir;
    private javax.swing.JTextField jTextFieldPackageName;
    // End of variables declaration//GEN-END:variables
}
