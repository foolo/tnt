package editor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import rainbow.RainbowError;
import rainbow.RainbowHandler;
import xliff_model.ValidationError;
import rainbow.XliffFileValidator;
import undo_manager.CaretPosition;
import undo_manager.UndoEventListener;
import undo_manager.UndoManager;
import undo_manager.UndoableModel;
import undo_manager.UndoableState;
import util.Log;
import util.Settings;
import util.XmlUtil;
import xliff_model.FileTag;
import xliff_model.SegmentTag;
import xliff_model.XliffTag;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;
import xliff_model.exceptions.SaveException;
import xliff_model.exceptions.XliffVersionException;

public class MainForm extends javax.swing.JFrame implements UndoEventListener {

	private final LogWindow logWindow;
	private UndoManager undoManager;
	private final ArrayList<FileView> fileViews = new ArrayList<>();

	public MainForm() {
		initComponents();
		logWindow = new LogWindow();
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	static String truncate(String s) {
		int max_length = 80;
		if (s.length() > max_length) {
			return "..." + s.substring(s.length() - (max_length - 3), s.length());
		}
		return s;
	}

	void updateMenu() {
		jMenuItemExport.setEnabled(undoManager != null);
		jMenuItemSave.setEnabled(undoManager != null);
		jMenuItemCopySrc.setEnabled(undoManager != null);
		jMenuItemMarkTranslated.setEnabled(undoManager != null);
	}

	XliffTag load_xliff(File f) throws LoadException {
		XliffTag xliffTag = null;
		try {
			Document doc = XmlUtil.read_xml(f);
			xliffTag = new XliffTag(doc, f);
		}
		catch (LoadException ex) {
			throw new LoadException("Could not open file\n" + ex.getMessage());
		}
		catch (XliffVersionException ex) {
			throw new LoadException("Could not open " + f + "\n" + ex.getMessage());
		}
		catch (ParseException ex) {
			Log.debug("load_file: " + ex.toString());
			throw new LoadException("Could not open " + f + "\nUnrecogized format");
		}
		return xliffTag;
	}

	public void load_file(File f, boolean promptErrors) {
		XliffTag xliffTag;
		try {
			xliffTag = load_xliff(f);
		}
		catch (LoadException ex) {
			Log.debug("load_file: " + ex.toString());
			if (promptErrors) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}
		undoManager = new UndoManager();
		updateMenu();
		CaretPosition pos = new CaretPosition(null, CaretPosition.Column.TARGET, 0);
		undoManager.initialize(new UndoableState(xliffTag, pos, pos, undoManager), this);
		jTabbedPane1.removeAll();
		fileViews.clear();
		for (FileTag fileTag : xliffTag.getFiles()) {
			FileView fv = new FileView(this, fileTag.getId());
			fv.setName(truncate(fileTag.getAlias()));
			fv.populate_units(fileTag.getUnitsArray());
			fv.update_model(fileTag);
			jTabbedPane1.add(fv);
			fileViews.add(fv);
		}
		setTitle(f.toString());
		Settings.setLastOpenedFile(f);
	}

	XliffTag getXliffTag() {
		return (XliffTag) undoManager.getCurrentState().getModel();
	}

	boolean save_to_file() {
		try {
			Log.debug("save_to_file: " + getXliffTag().getFile());
			XmlUtil.write_xml(getXliffTag().getDocument(), new StreamResult(getXliffTag().getFile()));
		}
		catch (SaveException ex) {
			JOptionPane.showMessageDialog(this, "Could not save file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		undoManager.markSaved();
		return true;
	}

	boolean showValidiationError(ValidationError e) {
		if (e.path == null) {
			return false;
		}
		FileView fileView = getFileView(e.path.fileId);
		if (fileView == null) {
			return false;
		}
		return fileView.showValidiationError(e.message, e.path);
	}

	void showValidationErrors(ArrayList<ValidationError> errors) {
		StringBuilder undhandledErrors = new StringBuilder();
		for (ValidationError e : errors) {
			Log.debug("showValidationErrors: ValidationError: " + e.toString());
			if (showValidiationError(e) == false) {
				undhandledErrors.append(e.toString());
				undhandledErrors.append('\n');
			}
		}
		if (undhandledErrors.length() > 0) {
			JOptionPane.showMessageDialog(this, "Tag errors found:\n" + undhandledErrors.toString(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean save_file(boolean haltOnEncodeError) {
		ArrayList<ValidationError> errors = new ArrayList<>();
		getXliffTag().encode(errors, false);
		if (errors.isEmpty() == false) {
			showValidationErrors(errors);
			if (haltOnEncodeError) {
				JOptionPane.showMessageDialog(this, "Could not export. Some segments contain invalid tags.", "", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return save_to_file();
	}

	boolean okToClose() {
		if (undoManager == null) {
			return true;
		}
		if (undoManager.isModified() == false) {
			return true;
		}
		int choice = JOptionPane.showConfirmDialog(this, "Save changes before closing?", "Save changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		switch (choice) {
			case JOptionPane.YES_OPTION:
				return save_file(false);
			case JOptionPane.NO_OPTION:
				return true;
			case JOptionPane.CANCEL_OPTION:
			default:
				return false;
		}
	}

	String save_to_string() throws SaveException {
		StringWriter writer = new StringWriter();
		XmlUtil.write_xml(getXliffTag().getDocument(), new StreamResult(writer));
		return writer.toString();
	}

	FileView getFileView(String fileId) {
		for (FileView fileView : fileViews) {
			if (fileView.getFileId().equals(fileId)) {
				return fileView;
			}
		}
		Log.err("getFileView: no fileView with id: " + fileId);
		return null;
	}

	boolean validateFile() {
		ArrayList<ValidationError> encodeErrors = new ArrayList<>();
		getXliffTag().encode(encodeErrors, true);

		for (ValidationError e : encodeErrors) {
			// there should be no invalid non-initial segments, log for debugging only
			Log.err("validateFile: ValidationError: " + e.toString());
		}

		String xmlData;
		ArrayList<ValidationError> validationErrors;
		try {
			xmlData = save_to_string();
			validationErrors = XliffFileValidator.validate(xmlData);
		}
		catch (SaveException | ParseException ex) {
			JOptionPane.showMessageDialog(this, "Could not validate file\n" + ex.toString(), "Unexpected error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (validationErrors.isEmpty() == false) {
			showValidationErrors(validationErrors);
			return false;
		}
		return true;
	}

	@Override
	public void notify_undo(UndoableModel model, CaretPosition newEditingPosition) {
		XliffTag xliffTag = (XliffTag) model;
		if (fileViews.size() != xliffTag.getFiles().size()) {
			Log.err("notify_undo: fileViews.size() != xliffTag.getFiles().size() " + fileViews.size() + ", " + xliffTag.getFiles().size());
			return;
		}
		for (int i = 0; i < fileViews.size(); i++) {
			fileViews.get(i).update_model(xliffTag.getFiles().get(i));
		}

		SegmentView segmentView = newEditingPosition.getSegmentView();
		if (segmentView != null) {
			jTabbedPane1.setSelectedComponent(segmentView.getFileView());
			segmentView.getFileView().scroll_to_segment(segmentView);
			// todo why is invokeLater needed?
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					segmentView.setTextPosition(newEditingPosition.getColumn(), newEditingPosition.getTextPosition());
				}
			});
		}
	}

	@Override
	public void modifiedStatusChanged(UndoableModel model, boolean modified) {
		String title = (undoManager.isModified() ? "* " : "") + getXliffTag().getFile().toString();
		setTitle(title);
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemCreatePackage = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemExport = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemCopySrc = new javax.swing.JMenuItem();
        jMenuItemMarkTranslated = new javax.swing.JMenuItem();
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

        jMenuItemExport.setText("Export translated file(s)");
        jMenuItemExport.setEnabled(false);
        jMenuItemExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExport);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setText("Save");
        jMenuItemSave.setEnabled(false);
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSave);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Segment");

        jMenuItemCopySrc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopySrc.setText("Copy source to target");
        jMenuItemCopySrc.setEnabled(false);
        jMenuItemCopySrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopySrcActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemCopySrc);

        jMenuItemMarkTranslated.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemMarkTranslated.setText("Mark as translated");
        jMenuItemMarkTranslated.setEnabled(false);
        jMenuItemMarkTranslated.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMarkTranslatedActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemMarkTranslated);

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
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
		if (okToClose() == false) {
			return;
		}
		JFileChooser fc = new JFileChooser(Settings.getLastOpenedFile().getParentFile());
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		load_file(fc.getSelectedFile(), true);
		Settings.setLastOpenedFile(fc.getSelectedFile());
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
		save_file(false);
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemCopySrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopySrcActionPerformed
		undoManager.markSnapshot();
		SegmentView segmentView = SegmentView.getActiveSegmentView();
		if (segmentView == null) {
			return;
		}
		SegmentTag segmentTag = segmentView.getSegmentTag();
		segmentView.setTargetText(segmentTag.getSourceText().copy());
		undoManager.markSnapshot();
    }//GEN-LAST:event_jMenuItemCopySrcActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		if (okToClose()) {
			logWindow.dispose();
			dispose();
		}
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemLogsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLogsActionPerformed
		logWindow.open();
    }//GEN-LAST:event_jMenuItemLogsActionPerformed

    private void jMenuItemCreatePackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCreatePackageActionPerformed
		if (okToClose() == false) {
			return;
		}
		CreatePackageDialog d = new CreatePackageDialog(this, true);
		d.setVisible(true);
		if (d.getResult() == false) {
			return;
		}
		String inputFile = d.getInputFile();
		File commonDir = d.getCommonDirectory();
		String packageName = d.getPackageName();

		File xliffFile;
		RainbowHandler rainbowHandler = new RainbowHandler();
		try {
			xliffFile = rainbowHandler.createPackage(inputFile, commonDir.getPath(), packageName);
			load_file(xliffFile, true);
		}
		catch (IOException | RainbowError ex) {
			JOptionPane.showMessageDialog(this, "Could not create package:\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
    }//GEN-LAST:event_jMenuItemCreatePackageActionPerformed

    private void jMenuItemExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportActionPerformed
		File f = getXliffTag().getFile().getAbsoluteFile();
		RainbowHandler rainbowHandler = new RainbowHandler();
		try {
			File tempDir = Files.createTempDirectory("tnt_tmp_").toFile();
			ArrayList<ValidationError> encodeErrors = new ArrayList<>();
			getXliffTag().encode(encodeErrors, true);
			for (ValidationError e : encodeErrors) {
				// there should be no invalid non-initial segments, log for debugging only
				Log.err("validateFile: ValidationError: " + e.toString());
			}
			String xliffData = save_to_string();
			File outputDir = rainbowHandler.exportTranslatedFile(tempDir, f, xliffData);
			JOptionPane.showMessageDialog(this, new ExportCompletedPanel(outputDir), "Export result", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | RainbowError | SaveException ex) {
			JOptionPane.showMessageDialog(this, "Could not export file: " + f.toString() + "\n" + ex.toString(), "Export result", JOptionPane.ERROR_MESSAGE);
		}
    }//GEN-LAST:event_jMenuItemExportActionPerformed

    private void jMenuItemMarkTranslatedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkTranslatedActionPerformed
		undoManager.markSnapshot();
		SegmentView segmentView = SegmentView.getActiveSegmentView();
		if (segmentView == null) {
			return;
		}
		if (segmentView.getSegmentTag().getTargetText().getContent().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Can not mark empty segment as translated", "", JOptionPane.ERROR_MESSAGE);
			return;
		}

		ValidationError validationError = segmentView.getSegmentTag().testEncode();
		if (validationError != null) {
			segmentView.showValidationError(validationError.message, validationError.path);
			return;
		}
		if (segmentView.getSegmentTag().getState() == SegmentTag.State.INITIAL) {
			segmentView.getSegmentTag().stage();
			if (validateFile()) {
				segmentView.setState(SegmentTag.State.TRANSLATED);
			}
		}
    }//GEN-LAST:event_jMenuItemMarkTranslatedActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemCopySrc;
    private javax.swing.JMenuItem jMenuItemCreatePackage;
    private javax.swing.JMenuItem jMenuItemExport;
    private javax.swing.JMenuItem jMenuItemLogs;
    private javax.swing.JMenuItem jMenuItemMarkTranslated;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
