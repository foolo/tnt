package tnt.editor;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import tnt.editor.util.SelectableOptionPane;
import tnt.language.Language;
import tnt.language.LanguageCollection;
import tnt.language.LanguageTag;
import tnt.language.SpellCheck;
import tnt.util.Log;
import tnt.util.Settings;
import tnt.xliff_model.exceptions.LoadException;

public class MainForm extends javax.swing.JFrame {

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

	public void load_file(File f) {
		try {
			Session.newSession(f);
		}
		catch (LoadException ex) {
			Log.debug("load_file: " + ex);
			SelectableOptionPane.show(this, "", "Could not open " + f + "\n\n" + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
			Settings.removeRecentFile(f.getAbsolutePath());
			return;
		}
		fileView = new FileView();
		fileView.update_model();
		jPanel2.removeAll();
		jPanel2.add(fileView);
		jPanel2.revalidate();

		Settings.addRecentFile(f.getAbsolutePath());
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

	String getAutosaveTimestamp() {
		return ZonedDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
	}



	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelProgress = new javax.swing.JLabel();
        jLabelSaveStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

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
            .addGap(0, 636, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel3, "card2");

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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
			logWindow.dispose();
			autosaveTimer.cancel();
			dispose();
    }//GEN-LAST:event_formWindowClosing

	void applyPreferences() {
		if (fileView != null) {
			fileView.updateHeights();
		}
	}

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelProgress;
    private javax.swing.JLabel jLabelSaveStatus;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
