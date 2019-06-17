package editor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import rainbow.RainbowHandler;
import util.Log;
import util.Settings;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;
import xliff_model.exceptions.XliffVersionException;

public class MainForm extends javax.swing.JFrame {

	LogWindow logWindow;

	public MainForm() {
		initComponents();
		logWindow = new LogWindow();
	}

	public void load_file(File f) {
		try {
			xliffView1.load_xliff(f);
		}
		catch (LoadException ex) {
			JOptionPane.showMessageDialog(null, "Could not open file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
		catch (XliffVersionException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
		catch (ParseException ex) {
			Log.debug("load_file: " + ex.toString());
			JOptionPane.showMessageDialog(null, "Invalid XLIFF format", "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void save_file() {
		xliffView1.save();
	}

	public void menu_open() {
		if (xliffView1.okToClose() == false) {
			return;
		}
		JFileChooser fc = new JFileChooser(Settings.getOpenDirectory());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		load_file(fc.getSelectedFile());
		Settings.setOpenDirectory(fc.getCurrentDirectory());
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xliffView1 = new editor.XliffView();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemCreatePackage = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemCopySrc = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemLogs = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jMenu1.setText("File");

        jMenuItemCreatePackage.setText("Create XLIFF package");
        jMenuItemCreatePackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCreatePackageActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemCreatePackage);

        jMenuItemOpen.setText("Open XLIFF");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemOpen);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setText("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSave);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItemCopySrc.setText("Copy source to target");
        jMenuItemCopySrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopySrcActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemCopySrc);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("View");

        jMenuItemLogs.setText("Log");
        jMenuItemLogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLogsActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemLogs);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xliffView1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xliffView1, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
		menu_open();
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
		save_file();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemCopySrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopySrcActionPerformed
		xliffView1.copy_source_to_target();
    }//GEN-LAST:event_jMenuItemCopySrcActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		if (xliffView1.okToClose()) {
			logWindow.dispose();
			dispose();
		}
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemLogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLogsActionPerformed
		logWindow.open();
    }//GEN-LAST:event_jMenuItemLogsActionPerformed

    private void jMenuItemCreatePackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreatePackageActionPerformed
		CreatePackageDialog d = new CreatePackageDialog(this, true);
		d.setVisible(true);
		ArrayList<String> inputFiles = d.getInputFiles();
		File commonDir = d.getCommonDirectory();
		String packageName = d.getPackageName();

		RainbowHandler rainbowHandler = new RainbowHandler();
		try {
			rainbowHandler.createPackage(inputFiles, commonDir.getPath(), packageName);
		}
		catch (IOException ex) {
			Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
		}

    }//GEN-LAST:event_jMenuItemCreatePackageActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemCopySrc;
    private javax.swing.JMenuItem jMenuItemCreatePackage;
    private javax.swing.JMenuItem jMenuItemLogs;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSave;
    private editor.XliffView xliffView1;
    // End of variables declaration//GEN-END:variables
}
