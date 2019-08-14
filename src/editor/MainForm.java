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
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import language.Language;
import language.LanguageCollection;
import language.LanguageTag;
import language.SpellCheck;
import qc.Qc;
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
import xliff_model.exceptions.SaveException;

public class MainForm extends javax.swing.JFrame implements UndoEventListener {

	private final LogWindow logWindow;

	private final ArrayList<FileView> fileViews = new ArrayList<>();

	public MainForm() {
		initComponents();
		logWindow = new LogWindow();
		jLabelProgress.setText(" ");
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
		applyFontPreferences();
		updateTitle();
		Settings.addRecentFile(f.getAbsolutePath());
		updateMenus();
		initializeSpelling(Session.getProperties().getTrgLang());
		SwingUtilities.invokeLater(this::updateHeights);
	}

	void initializeSpelling(String trgLang) {
		SpellCheck.unloadDictionary();
		for (SegmentView segmentView : getSegmentViews()) {
			segmentView.clearSpellcheck();
		}
		if (trgLang.isEmpty()) {
			Log.debug("loadDictionary: trgLang empty, disable spelling");
			return;
		}
		Language l = LanguageCollection.findLanguageWithFallback(new LanguageTag(trgLang));
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

	ArrayList<SegmentView> getSegmentViews() {
		ArrayList<SegmentView> res = new ArrayList<>();
		for (FileView fileView : fileViews) {
			fileView.getSegmentViews(res);
		}
		return res;
	}

	void updateHeights() {
		for (SegmentView segmentView : getSegmentViews()) {
			segmentView.updateHeight();
		}
	}

	public boolean save_file() {
		ArrayList<String> errors = new ArrayList<>();
		getXliffTag().encode(errors, false);
		Session.getProperties().encode(getXliffTag().getDocument());
		for (String e : errors) {
			Log.debug("showValidationErrors: ValidationError: " + e);
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
				return save_file();
			case JOptionPane.NO_OPTION:
				return true;
			case JOptionPane.CANCEL_OPTION:
			default:
				return false;
		}
	}

	String save_to_string() throws SaveException {
		ArrayList<String> encodeErrors = new ArrayList<>();
		getXliffTag().encode(encodeErrors, true);
		for (String e : encodeErrors) {
			// there should be no invalid non-initial segments, log for debugging only
			Log.err("validateFile: ValidationError: " + e);
		}
		StringWriter writer = new StringWriter();
		XmlUtil.write_xml(getXliffTag().getDocument(), new StreamResult(writer));
		return writer.toString();
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
					segmentView.navigateToView(SegmentView.Column.TARGET, newEditingPosition.getTextPosition());
				}
			});
		}
	}

	@Override
	public void updateProgress(UndoableModel model) {
		XliffTag xliffTag = (XliffTag) model;
		jLabelProgress.setText(xliffTag.getProgress());
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabelProgress = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemCreatePackage = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuRecentFiles = new javax.swing.JMenu();
        jMenuItemClearRecentFiles = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemExport = new javax.swing.JMenuItem();
        jMenuItemProperties = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItemPreferences = new javax.swing.JMenuItem();
        jMenuItemAddSpecialChar = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemCopySrc = new javax.swing.JMenuItem();
        jMenuItemMarkTranslated = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemLogs = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(840, 0));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));

        jLabelProgress.setText("jLabelProgress");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabelProgress)
                .addGap(0, 823, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabelProgress))
        );

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

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setText("Save");
        jMenuItemSave.setEnabled(false);
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSave);

        jMenuItemExport.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemExport.setText("Export translated file");
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

        jMenuBar1.add(jMenu1);

        jMenu4.setText("Edit");

        jMenuItemPreferences.setText("Preferences");
        jMenuItemPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPreferencesActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemPreferences);

        jMenuItemAddSpecialChar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemAddSpecialChar.setText("Insert special character");
        jMenuItemAddSpecialChar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddSpecialCharActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemAddSpecialChar);

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
            .addComponent(jTabbedPane1)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
		PleaseWaitDialog dialog = new PleaseWaitDialog(this, "Saving... ");
		dialog.run(() -> {
			save_file();
		});
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
		CreateXliffDialog d = new CreateXliffDialog(this);
		d.setLocationRelativeTo(this);
		d.setVisible(true);
		if (d.getResult() == false) {
			return;
		}

		PleaseWaitDialog dialog = new PleaseWaitDialog(this, "Importing " + d.getInputFile().getName() + "...");
		dialog.run(() -> {
			File xliffFile;
			OpenXliffHandler converter = new OpenXliffHandler();
			try {
				xliffFile = converter.createPackage(d.getInputFile(), d.getXliffFile(), d.getSourceLanguage().originalTagStr, d.getTargetLanguage().originalTagStr);
				load_file(xliffFile, true);
			}
			catch (ConversionError ex) {
				JOptionPane.showMessageDialog(this, "Could not create package:\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			}
		});
    }//GEN-LAST:event_jMenuItemCreatePackageActionPerformed

    private void jMenuItemExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportActionPerformed
		if (getXliffTag().hasMetadata() == false) {
			JOptionPane.showMessageDialog(this, "Could not export. No document metadata found in XLIFF file.", "Export result", JOptionPane.ERROR_MESSAGE);
			return;
		}
		PleaseWaitDialog dialog = new PleaseWaitDialog(this, "Exporting " + getXliffTag().getFile().getName() + "...");
		dialog.run(() -> {
			OpenXliffHandler converter = new OpenXliffHandler();
			try {

				String xliffData = save_to_string();
				File outputFile = converter.exportTranslatedFile(getXliffTag(), xliffData);
				JOptionPane.showMessageDialog(this, new ExportCompletedPanel(outputFile), "Export result", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (IOException | ConversionError | SaveException ex) {
				JOptionPane.showMessageDialog(this, "Could not export file:\n" + ex.toString(), "Export result", JOptionPane.ERROR_MESSAGE);
			}
		});
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

		ArrayList<String> qcRes = Qc.runQc(segmentView.getSegmentTag());
		segmentView.showQcMsg(qcRes);

		String errMsg = segmentView.getSegmentTag().testEncodeTarget();
		if (errMsg != null) {
			segmentView.showValidationError(errMsg);
			return;
		}
		if (segmentView.getSegmentTag().getState() == SegmentTag.State.INITIAL) {
			errMsg = OpenXliffValidator.validate(segmentView.getSegmentTag());
			if (errMsg == null) {
				segmentView.setState(SegmentTag.State.TRANSLATED);
				jumpToNextSegment(segmentView);
			}
			else {
				segmentView.showValidationError(errMsg);
			}
		}
		else {
			jumpToNextSegment(segmentView);
		}
    }//GEN-LAST:event_jMenuItemMarkTranslatedActionPerformed

	void applyFontPreferences() {
		Font f = new Font(Settings.getEditorFontName(), Font.PLAIN, Settings.getEditorFontSize());
		int minHeight = SegmentView.getMinHeightForFont(f);
		for (SegmentView segmentView : getSegmentViews()) {
			segmentView.setEditorFont(f, minHeight);
		}
	}

	void applyPreferences() {
		applyFontPreferences();
		updateHeights();
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
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				updateHeights();
			}
		});
    }//GEN-LAST:event_formWindowActivated

    private void jMenuItemClearRecentFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemClearRecentFilesActionPerformed
		Settings.clearRecentFiles();
		updateRecentFilesMenu();
    }//GEN-LAST:event_jMenuItemClearRecentFilesActionPerformed

    private void jMenuItemPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPropertiesActionPerformed
		PropertiesDialog propertiesDialog = new PropertiesDialog(this);
		propertiesDialog.setLocationRelativeTo(this);
		propertiesDialog.setVisible(true);
		if (propertiesDialog.getResult()) {
			Session.getUndoManager().markSnapshot();
			updateTitle();
			initializeSpelling(Session.getProperties().getTrgLang());
		}
    }//GEN-LAST:event_jMenuItemPropertiesActionPerformed

    private void jMenuItemAddSpecialCharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddSpecialCharActionPerformed
		SpecialCharacterDialog dialog = new SpecialCharacterDialog(this);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		if (dialog.getResult()) {
			Session.getUndoManager().markSnapshot();
			SegmentView segmentView = SegmentView.getActiveSegmentView();
			if (segmentView == null) {
				return;
			}
			SegmentTag segmentTag = segmentView.getSegmentTag();
			segmentView.insertText(dialog.getSelectedChar());
			Session.getUndoManager().markSnapshot();
		}
    }//GEN-LAST:event_jMenuItemAddSpecialCharActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelProgress;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemAddSpecialChar;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
