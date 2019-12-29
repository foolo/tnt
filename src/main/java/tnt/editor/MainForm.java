package tnt.editor;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.transform.stream.StreamResult;
import tnt.conversion.OpenXliffHandler;
import tnt.conversion.ConversionError;
import tnt.conversion.OpenXliffValidator;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import tnt.language.Language;
import tnt.language.LanguageCollection;
import tnt.language.LanguageTag;
import tnt.language.SpellCheck;
import tnt.qc.Qc;
import tnt.undo_manager.UndoPosition;
import tnt.undo_manager.UndoEventListener;
import tnt.undo_manager.UndoableModel;
import tnt.util.FileUtil;
import tnt.util.Log;
import tnt.util.SegmentsHtmlEncoder;
import tnt.util.Settings;
import tnt.util.XmlUtil;
import tnt.xliff_model.SegmentTag;
import tnt.xliff_model.Tag;
import tnt.xliff_model.TaggedText;
import tnt.xliff_model.TaggedTextContent;
import tnt.xliff_model.XliffTag;
import tnt.xliff_model.exceptions.LoadException;
import tnt.xliff_model.exceptions.SaveException;

public class MainForm extends javax.swing.JFrame implements UndoEventListener {

	private final LogWindow logWindow;
	private FileView fileView = null;
	private Timer autosaveTimer = new Timer();
	static final Dimension DEFAULT_DIALOG_SIZE = new Dimension(850, 550);

	public MainForm() {
		initComponents();
		logWindow = new LogWindow();
		jLabelProgress.setText("");
		jLabelSaveStatus.setText("");
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
					load_file(new File(item.getText()));
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
		String applicationInfo = " - " + Application.APPLICATION_NAME + " " + Application.APPLICATION_VERSION;
		String title = fileInfo + languageInfo + applicationInfo;
		setTitle(title);
	}

	void restartAutosaveTimer() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				autosave_file();
			}
		};
		autosaveTimer.cancel();
		autosaveTimer = new Timer();
		long delay = 600000;
		autosaveTimer.scheduleAtFixedRate(timerTask, delay, delay);
	}

	public void load_file(File f) {
		try {
			Session.newSession(f, this);
		}
		catch (LoadException ex) {
			Log.debug("load_file: " + ex.toString());
			PleaseWaitDialog.showMessageDialog(this, ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			Settings.removeRecentFile(f.getAbsolutePath());
			updateRecentFilesMenu();
			return;
		}
		fileView = new FileView();
		fileView.populate_segments(getXliffTag().getSegmentsArray());
		fileView.update_model(getXliffTag());
		jPanel2.removeAll();
		jPanel2.add(fileView);
		jPanel2.revalidate();

		fileView.applyFontPreferences();
		fileView.focusFirstSegment();
		updateTitle();
		Settings.addRecentFile(f.getAbsolutePath());
		updateMenus();
		restartAutosaveTimer();
		initializeSpelling(Session.getProperties().getTrgLang());
		SwingUtilities.invokeLater(fileView::updateHeights);
	}

	void initializeSpelling(String trgLang) {
		SpellCheck.unloadDictionary();
		for (SegmentView segmentView : fileView.getSegmentViews()) {
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

	String getAutosaveTimestamp() {
		return ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}

	boolean manual_save_file() {
		try {
			save_file();
			jLabelSaveStatus.setText("Saved at " + getAutosaveTimestamp());
			restartAutosaveTimer();
		}
		catch (SaveException ex) {
			jLabelSaveStatus.setText("Save failed");
			PleaseWaitDialog.showMessageDialog(this, "Could not save file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	void autosave_file() {
		try {
			save_file();
			jLabelSaveStatus.setText("Autosaved at " + getAutosaveTimestamp());
		}
		catch (SaveException ex) {
			Log.err(ex);
			jLabelSaveStatus.setText("Autosave failed " + getAutosaveTimestamp());
		}
	}

	void save_file() throws SaveException {
		ArrayList<String> errors = new ArrayList<>();
		getXliffTag().encode(errors, false);
		Session.getProperties().encode(getXliffTag().getDocument());
		for (String e : errors) {
			Log.debug("showValidationErrors: ValidationError: " + e);
		}
		Log.debug("save_to_file: " + getXliffTag().getFile());
		XmlUtil.write_xml(getXliffTag().getDocument(), new StreamResult(getXliffTag().getFile()));
		Session.markSaved();
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
				return manual_save_file();
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
	public void notify_undo(UndoableModel model, UndoPosition newEditingPosition) {
		XliffTag xliffTag = (XliffTag) model;
		fileView.update_model(xliffTag);

		SegmentView segmentView = newEditingPosition.getSegmentView();
		if (segmentView != null) {
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

        jPanel1 = new javax.swing.JPanel();
        jLabelProgress = new javax.swing.JLabel();
        jLabelSaveStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemCreatePackage = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuRecentFiles = new javax.swing.JMenu();
        jMenuItemClearRecentFiles = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemExport = new javax.swing.JMenuItem();
        jMenuExportTable = new javax.swing.JMenu();
        jMenuItemExportTableBoth = new javax.swing.JMenuItem();
        jMenuItemExportTableTarget = new javax.swing.JMenuItem();
        jMenuItemLocateInFileBrowser = new javax.swing.JMenuItem();
        jMenuItemProperties = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItemPreferences = new javax.swing.JMenuItem();
        jMenuItemAddSpecialChar = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemCopySrc = new javax.swing.JMenuItem();
        jMenuItemCopySrcTags = new javax.swing.JMenuItem();
        jMenuItemMarkTranslated = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemLogs = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

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

        jLabelSaveStatus.setText("jLabelSaveStatus");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabelProgress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelSaveStatus))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelProgress)
                    .addComponent(jLabelSaveStatus)))
        );

        jPanel2.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 934, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 615, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel3, "card2");

        jMenu1.setText("Project");

        jMenuItemCreatePackage.setText("New project...");
        jMenuItemCreatePackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCreatePackageActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemCreatePackage);

        jMenuItemOpen.setText("Open project...");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemOpen);

        jMenuRecentFiles.setText("Recent projects");

        jMenuItemClearRecentFiles.setText("Clear recent projects");
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

        jMenuExportTable.setText("Export as table");

        jMenuItemExportTableBoth.setText("Source and target");
        jMenuItemExportTableBoth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportTableBothActionPerformed(evt);
            }
        });
        jMenuExportTable.add(jMenuItemExportTableBoth);

        jMenuItemExportTableTarget.setText("Only target");
        jMenuItemExportTableTarget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExportTableTargetActionPerformed(evt);
            }
        });
        jMenuExportTable.add(jMenuItemExportTableTarget);

        jMenu1.add(jMenuExportTable);

        jMenuItemLocateInFileBrowser.setText("Locate in file browser");
        jMenuItemLocateInFileBrowser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLocateInFileBrowserActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemLocateInFileBrowser);

        jMenuItemProperties.setText("Project properties");
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
        jMenuItemAddSpecialChar.setText("Insert special character...");
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

        jMenuItemCopySrcTags.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopySrcTags.setText("Copy tags from source to target");
        jMenuItemCopySrcTags.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopySrcTagsActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemCopySrcTags);

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

        jMenu3.setText("Help");

        jMenuItemLogs.setText("Show logs");
        jMenuItemLogs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemLogsActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemLogs);

        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemAbout);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
		if (okToClose() == false) {
			return;
		}
		File dir = FileUtil.getExistingDirectory(Settings.getOpenDirectory());
		JFileChooser fc = new JFileChooser(dir);
		fc.setPreferredSize(DEFAULT_DIALOG_SIZE);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XLIFF files", "xlf");
		fc.addChoosableFileFilter(filter);
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		Settings.setOpenDirectory(fc.getSelectedFile().getParentFile());
		load_file(fc.getSelectedFile());
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
		PleaseWaitDialog dialog = new PleaseWaitDialog(this, "Saving... ");
		dialog.run(() -> {
			manual_save_file();
		});
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemCopySrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopySrcActionPerformed
		SegmentView segmentView = SegmentView.getActiveSegmentView();
		if (segmentView == null) {
			return;
		}
		Session.getUndoManager().markSnapshot();
		SegmentTag segmentTag = segmentView.getSegmentTag();
		segmentView.setTargetText(segmentTag.getSourceText().copy());
		Session.getUndoManager().markSnapshot();
    }//GEN-LAST:event_jMenuItemCopySrcActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		if (okToClose()) {
			logWindow.dispose();
			autosaveTimer.cancel();
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
				load_file(xliffFile);
			}
			catch (ConversionError ex) {
				PleaseWaitDialog.showMessageDialog(this, "Could not create package:\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
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
				manual_save_file(); // save file first in case export crashes/hangs
				String xliffData = save_to_string();
				File outputFile = converter.exportTranslatedFile(getXliffTag(), xliffData);
				PleaseWaitDialog.showMessageDialog(this, new ExportCompletedPanel(outputFile), "Export result", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (IOException | ConversionError | SaveException ex) {
				PleaseWaitDialog.showMessageDialog(this, "Could not export file:\n" + ex.toString(), "Export result", JOptionPane.ERROR_MESSAGE);
			}
		});
    }//GEN-LAST:event_jMenuItemExportActionPerformed

	void jumpToNextSegment(SegmentView currentSegmentView) {
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

	void applyPreferences() {
		if (fileView != null) {
			fileView.applyFontPreferences();
			fileView.updateHeights();
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
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				if (fileView != null) {
					fileView.updateHeights();
				}
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
			segmentView.insertText(dialog.getSelectedChar());
			Session.getUndoManager().markSnapshot();
		}
    }//GEN-LAST:event_jMenuItemAddSpecialCharActionPerformed

    private void jMenuItemLocateInFileBrowserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemLocateInFileBrowserActionPerformed
		File dir = getXliffTag().getFile().getParentFile();
		FileUtil.desktopOpen(this, dir);
    }//GEN-LAST:event_jMenuItemLocateInFileBrowserActionPerformed

	void exportTable(boolean includeSource) {
		SegmentsHtmlEncoder encoder = new SegmentsHtmlEncoder();
		XliffTag xliffTag = getXliffTag();
		String htmlData = encoder.encode(xliffTag, includeSource);
		String originalFilename = new File(xliffTag.getFiles().get(0).getOriginalFilePath()).getName();
		String sourceLanguage = includeSource ? (Session.getProperties().getSrcLang() + "-") : "";
		String targetLanguage = Session.getProperties().getTrgLang();
		String tableFilename = originalFilename + " (" + sourceLanguage + targetLanguage + " table).html";
		File targetFile = new File(xliffTag.getFile().getParentFile(), tableFilename);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
			writer.write(htmlData);
			writer.close();
			JOptionPane.showMessageDialog(this, new ExportCompletedPanel(targetFile), "Export result", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Could not export table:\n" + ex.toString(), "Export result", JOptionPane.ERROR_MESSAGE);
		}
	}

    private void jMenuItemExportTableTargetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportTableTargetActionPerformed
		exportTable(false);
    }//GEN-LAST:event_jMenuItemExportTableTargetActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
		JOptionPane.showMessageDialog(this, new AboutPanel(), "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemExportTableBothActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExportTableBothActionPerformed
		exportTable(true);
    }//GEN-LAST:event_jMenuItemExportTableBothActionPerformed

    private void jMenuItemCopySrcTagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopySrcTagsActionPerformed
		SegmentView segmentView = SegmentView.getActiveSegmentView();
		if (segmentView == null) {
			return;
		}
		Session.getUndoManager().markSnapshot();
		SegmentTag segmentTag = segmentView.getSegmentTag();
		ArrayList<Tag> tags = segmentTag.getSourceText().copy().getTags();
		ArrayList<TaggedTextContent> ttc = (ArrayList<TaggedTextContent>) segmentTag.getTargetText().getContent().clone();
		ttc.addAll(new ArrayList<TaggedTextContent>(tags));
		segmentView.setTargetText(new TaggedText(ttc, segmentTag.getSourceText().getSegmentId()));
		Session.getUndoManager().markSnapshot();
    }//GEN-LAST:event_jMenuItemCopySrcTagsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelProgress;
    private javax.swing.JLabel jLabelSaveStatus;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuExportTable;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemAddSpecialChar;
    private javax.swing.JMenuItem jMenuItemClearRecentFiles;
    private javax.swing.JMenuItem jMenuItemCopySrc;
    private javax.swing.JMenuItem jMenuItemCopySrcTags;
    private javax.swing.JMenuItem jMenuItemCreatePackage;
    private javax.swing.JMenuItem jMenuItemExport;
    private javax.swing.JMenuItem jMenuItemExportTableBoth;
    private javax.swing.JMenuItem jMenuItemExportTableTarget;
    private javax.swing.JMenuItem jMenuItemLocateInFileBrowser;
    private javax.swing.JMenuItem jMenuItemLogs;
    private javax.swing.JMenuItem jMenuItemMarkTranslated;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemPreferences;
    private javax.swing.JMenuItem jMenuItemProperties;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenu jMenuRecentFiles;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
