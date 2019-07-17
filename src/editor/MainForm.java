package editor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.transform.stream.StreamResult;
import conversion.OpenXliffHandler;
import conversion.ConversionError;
import conversion.OpenXliffValidator;
import xliff_model.ValidationError;
import java.awt.Font;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import language.Language;
import language.LanguageCollection;
import language.SpellCheck;
import undo_manager.CaretPosition;
import undo_manager.UndoEventListener;
import undo_manager.UndoableModel;
import util.Log;
import util.Settings;
import util.XmlUtil;
import xliff_model.FileTag;
import xliff_model.SegmentTag;
import xliff_model.XliffTag;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;
import xliff_model.exceptions.SaveException;

public class MainForm extends javax.swing.JFrame implements UndoEventListener {

	private final LogWindow logWindow;

	private final ArrayList<FileView> fileViews = new ArrayList<>();

	public MainForm() {
		initComponents();
		logWindow = new LogWindow();
	}

	void updateRecentFilesMenu() {
		ArrayList<String> recentFiles = Settings.getRecentFiles();
		jMenuRecentFiles.removeAll();
		for (int i = recentFiles.size() - 1; i >= 0; i--) {
			String s = recentFiles.get(i);
			JMenuItem item = new JMenuItem(s);
			jMenuRecentFiles.add(item);
			item.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					if (okToClose() == false) {
						return;
					}
					load_file(new File(item.getText()), true);
				}
			});
		}
		jMenuRecentFiles.addSeparator();
		jMenuRecentFiles.add(jMenuItemClearRecentFiles);
	}

	void updateMenus() {
		jMenuItemExport.setEnabled(Session.getInstance() != null);
		jMenuItemSave.setEnabled(Session.getInstance() != null);
		jMenuItemCopySrc.setEnabled(Session.getInstance() != null);
		jMenuItemMarkTranslated.setEnabled(Session.getInstance() != null);
		updateRecentFilesMenu();
	}

	void updateTitle() {
		String srcLang = Session.getProperties().getSrcLang();
		String trgLang = Session.getProperties().getTrgLang();
		String languageInfo = "";
		if ((srcLang.isEmpty() == false) && (trgLang.isEmpty() == false)) {
			languageInfo = " (" + srcLang + " -> " + trgLang + ")";
		}
		String fileInfo = getXliffTag().getFile().getName() + " (" + getXliffTag().getFile().getParent() + ")";
		String title = fileInfo + languageInfo;
		setTitle(title);
	}

	public void load_file(File f, boolean promptErrors) {
		try {
			Session.newSession(f, this);
		}
		catch (LoadException ex) {
			Log.debug("load_file: " + ex.toString());
			if (promptErrors) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			}
			Settings.removeRecentFile(f.getAbsolutePath());
			updateRecentFilesMenu();
			return;
		}
		jTabbedPane1.removeAll();
		fileViews.clear();
		for (FileTag fileTag : getXliffTag().getFiles()) {
			FileView fv = new FileView(fileTag.getId());
			fv.setName(fileTag.getAlias());
			fv.populate_segments(fileTag.getSegmentsArray());
			fv.update_model(fileTag);
			jTabbedPane1.add(fv);
			fileViews.add(fv);
		}
		updateTitle();
		Settings.addRecentFile(f.getAbsolutePath());
		updateMenus();
		initializeSpelling(Session.getProperties().getTrgLang());
	}

	void initializeSpelling(String trgLang) {
		if (trgLang.isEmpty()) {
			Log.debug("loadDictionary: languageCode empty, disable spelling");
			return;
		}
		String[] code = Language.stringToCode(trgLang);
		Language l = LanguageCollection.findLanguageWithFallback(code);
		if (l == null) {
			JOptionPane.showMessageDialog(this, "Unrecognized target language code: '" + trgLang + "'\nSpellcheck will not be available", "", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Log.debug("Target language " + trgLang + " mapped to " + l);

		if (l.dictionaryPath == null) {
			Log.debug("No spellcheck dictionary available for target language '" + trgLang + "'");
			return;
		}

		Log.debug("Using spelling language " + l + " for target language " + trgLang);
		try {
			SpellCheck.loadDictionary(l);
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Could not load dictionary for target language '" + l + "'\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	XliffTag getXliffTag() {
		return (XliffTag) Session.getUndoManager().getCurrentState().getModel();
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
		Session.getProperties().encode(getXliffTag().getDocument());
		if (errors.isEmpty() == false) {
			showValidationErrors(errors);
			if (haltOnEncodeError) {
				JOptionPane.showMessageDialog(this, "Could not export. Some segments contain invalid tags.", "", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		try {
			Log.debug("save_to_file: " + getXliffTag().getFile());
			XmlUtil.write_xml(getXliffTag().getDocument(), new StreamResult(getXliffTag().getFile()));
		}
		catch (SaveException ex) {
			JOptionPane.showMessageDialog(this, "Could not save file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		Session.markSaved();
		return true;
	}

	boolean okToClose() {
		if (Session.getInstance() == null) {
			return true;
		}
		if (Session.isModified() == false) {
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
		ArrayList<ValidationError> encodeErrors = new ArrayList<>();
		getXliffTag().encode(encodeErrors, true);
		for (ValidationError e : encodeErrors) {
			// there should be no invalid non-initial segments, log for debugging only
			Log.err("validateFile: ValidationError: " + e.toString());
		}
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
		String xmlData;
		ArrayList<ValidationError> validationErrors;
		try {
			xmlData = save_to_string();
			validationErrors = OpenXliffValidator.validate(xmlData);
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

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemCreatePackage = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuRecentFiles = new javax.swing.JMenu();
        jMenuItemClearRecentFiles = new javax.swing.JMenuItem();
        jMenuItemExport = new javax.swing.JMenuItem();
        jMenuItemProperties = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItemPreferences = new javax.swing.JMenuItem();
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
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
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

        jMenuRecentFiles.setText("Recent files");

        jMenuItemClearRecentFiles.setText("Clear recent files");
        jMenuItemClearRecentFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemClearRecentFilesActionPerformed(evt);
            }
        });
        jMenuRecentFiles.add(jMenuItemClearRecentFiles);

        jMenu1.add(jMenuRecentFiles);

        jMenuItemExport.setText("Export translated file(s)");
        jMenuItemExport.setEnabled(false);
        jMenuItemExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExport);

        jMenuItemProperties.setText("Properties...");
        jMenuItemProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPropertiesActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemProperties);

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

        jMenu4.setText("Edit");

        jMenuItemPreferences.setText("Preferences");
        jMenuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPreferencesActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemPreferences);

        jMenuBar1.add(jMenu4);

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
		JFileChooser fc = new JFileChooser(Settings.getOpenDirectory());
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XLIFF files", "xlf");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		Settings.setOpenDirectory(fc.getSelectedFile().getParentFile());
		load_file(fc.getSelectedFile(), true);
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
		save_file(false);
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemCopySrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopySrcActionPerformed
		Session.getUndoManager().markSnapshot();
		SegmentView segmentView = SegmentView.getActiveSegmentView();
		if (segmentView == null) {
			return;
		}
		SegmentTag segmentTag = segmentView.getSegmentTag();
		segmentView.setTargetText(segmentTag.getSourceText().copy());
		Session.getUndoManager().markSnapshot();
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
		CreateXliffDialog d = new CreateXliffDialog(this, true);
		d.setLocationRelativeTo(this);
		d.setVisible(true);
		if (d.getResult() == false) {
			return;
		}

		File xliffFile;
		OpenXliffHandler converter = new OpenXliffHandler();
		try {
			xliffFile = converter.createPackage(d.getInputFile(), d.getXliffFile(), d.getSkeletonFile(), d.getSourceLanguage(), d.getTargetLanguage());
			load_file(xliffFile, true);
		}
		catch (ConversionError ex) {
			JOptionPane.showMessageDialog(this, "Could not create package:\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
    }//GEN-LAST:event_jMenuItemCreatePackageActionPerformed

    private void jMenuItemExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportActionPerformed

		File f = getXliffTag().getFile().getAbsoluteFile();
		OpenXliffHandler converter = new OpenXliffHandler();
		try {
			File outputDir = converter.exportTranslatedFile(f);
			JOptionPane.showMessageDialog(this, new ExportCompletedPanel(outputDir), "Export result", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException | ConversionError ex) {
			JOptionPane.showMessageDialog(this, "Could not export file: " + f.toString() + "\n" + ex.toString(), "Export result", JOptionPane.ERROR_MESSAGE);
		}
    }//GEN-LAST:event_jMenuItemExportActionPerformed

	void jumpToNextSegment(SegmentView currentSegmentView) {
		FileView fileView = (FileView) jTabbedPane1.getSelectedComponent();
		if (fileView != null) {
			fileView.jumpToNextSegment(currentSegmentView);
		}
	}

    private void jMenuItemMarkTranslatedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMarkTranslatedActionPerformed
		Session.getUndoManager().markSnapshot();
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
				jumpToNextSegment(segmentView);
			}
		}
		else {
			jumpToNextSegment(segmentView);
		}
    }//GEN-LAST:event_jMenuItemMarkTranslatedActionPerformed

	void applyPreferences() {
		Font f = new Font(Settings.getEditorFontName(), Settings.getEditorFontStyle(), Settings.getEditorFontSize());
		for (FileView fileView : fileViews) {
			fileView.setEditorFont(f);
		}
	}

    private void jMenuItemPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPreferencesActionPerformed
		PreferencesForm preferencesForm = new PreferencesForm(this);
		preferencesForm.setLocationRelativeTo(this);
		preferencesForm.setVisible(true);
		if (preferencesForm.getResult()) {
			applyPreferences();
		}
    }//GEN-LAST:event_jMenuItemPreferencesActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
		applyPreferences();
    }//GEN-LAST:event_formWindowActivated

    private void jMenuItemClearRecentFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearRecentFilesActionPerformed
		Settings.clearRecentFiles();
		updateRecentFilesMenu();
    }//GEN-LAST:event_jMenuItemClearRecentFilesActionPerformed

    private void jMenuItemPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPropertiesActionPerformed
		// TODO add your handling code here:
		PropertiesDialog propertiesDialog = new PropertiesDialog(this);
		propertiesDialog.setLocationRelativeTo(this);
		propertiesDialog.setVisible(true);
		if (propertiesDialog.getResult()) {
			Session.getUndoManager().markSnapshot();
			updateTitle();
		}
    }//GEN-LAST:event_jMenuItemPropertiesActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemClearRecentFiles;
    private javax.swing.JMenuItem jMenuItemCopySrc;
    private javax.swing.JMenuItem jMenuItemCreatePackage;
    private javax.swing.JMenuItem jMenuItemExport;
    private javax.swing.JMenuItem jMenuItemLogs;
    private javax.swing.JMenuItem jMenuItemMarkTranslated;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemPreferences;
    private javax.swing.JMenuItem jMenuItemProperties;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenu jMenuRecentFiles;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
