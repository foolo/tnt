package editor;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import rainbow.RainbowError;
import rainbow.RainbowHandler;
import rainbow.XliffFileValidator;
import util.Log;
import util.XmlUtil;
import xliff_model.FileTag;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;
import xliff_model.SegmentError;
import xliff_model.SegmentTag;
import xliff_model.XliffTag;
import xliff_model.exceptions.EncodeException;
import xliff_model.exceptions.SaveException;
import xliff_model.exceptions.XliffVersionException;

public class XliffView extends javax.swing.JPanel {

	private XliffTag xliffTag;

	public XliffView() {
		initComponents();
	}

	FileView getActiveFileView() {
		return ((FileView) jTabbedPane1.getSelectedComponent());
	}

	static String truncate(String s) {
		if (s.length() > 80) {
			return "..." + s.substring(s.length() - 77, s.length());
		}
		return s;
	}

	boolean load_xliff(File f) {
		try {
			Document doc = XmlUtil.read_xml(f);
			xliffTag = new XliffTag(doc, f);
			jTabbedPane1.removeAll();
			for (FileTag fileTag : xliffTag.getFiles()) {
				FileView fv = new FileView(this);
				fv.load_file(fileTag);
				jTabbedPane1.add(fv);
				updateTabTitle(fv);
			}
			return true;
		}
		catch (LoadException ex) {
			JOptionPane.showMessageDialog(this, "Could not open file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
		catch (XliffVersionException ex) {
			JOptionPane.showMessageDialog(this, "Could not open " + f + "\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
		catch (ParseException ex) {
			Log.debug("load_file: " + ex.toString());
			JOptionPane.showMessageDialog(this, "Could not open " + f + "\nUnrecogized format", "", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	void copy_source_to_target() {
		FileView fv = getActiveFileView();
		if (fv == null) {
			return;
		}
		fv.copy_source_to_target();
	}

	void markSegmentAsTranslated() {
		SegmentView segmentView = SegmentView.getActiveSegmentView();
		if (segmentView == null) {
			return;
		}
		if (segmentView.getSegmentTag().getTargetText().getContent().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Can not mark empty segment as translated", "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			segmentView.testEncode();
		}
		catch (EncodeException ex) {
			JOptionPane.showMessageDialog(this, "The segemnt contains an error:\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		segmentView.setState(SegmentTag.State.TRANSLATED);
		if (validateFile() == false) {
			segmentView.setState(SegmentTag.State.INITIAL);
		}
	}

	ArrayList<FileView> getAllFileViews() {
		ArrayList<FileView> fileViews = new ArrayList<>();
		for (Component c : jTabbedPane1.getComponents()) {
			if (!(c instanceof FileView)) {
				Log.warn("getAllFileViews: " + c.getClass().getName() + " not instance of FileView");
				continue;
			}
			fileViews.add((FileView) c);
		}
		return fileViews;
	}

	ArrayList<FileTag> getAllFileTags() {
		ArrayList<FileTag> files = new ArrayList<>();
		for (FileView fileView : getAllFileViews()) {
			FileTag fileTag = (FileTag) fileView.getUndoManager().getCurrentState().getModel();
			files.add(fileTag);
		}
		return files;
	}

	void markAsSaved() {
		for (FileView fileView : getAllFileViews()) {
			fileView.getUndoManager().markSaved();
		}
	}

	boolean save_to_file() {
		try {
			Log.debug("save_to_file: " + xliffTag.getFile());
			XmlUtil.write_xml(xliffTag.getDocument(), new StreamResult(xliffTag.getFile()));
		}
		catch (SaveException ex) {
			JOptionPane.showMessageDialog(this, "Could not save file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		markAsSaved();
		return true;
	}

	String save_to_string() throws SaveException {
		StringWriter writer = new StringWriter();
		XmlUtil.write_xml(xliffTag.getDocument(), new StreamResult(writer));
		return writer.toString();
	}

	boolean save() {
		ArrayList<FileTag> fileTags = getAllFileTags();
		xliffTag.setFiles(fileTags);

		ArrayList<SegmentError> errors = new ArrayList<>();
		xliffTag.encode(errors, false);

		if (errors.isEmpty()) {
			return save_to_file();
		}
		else {
			for (SegmentError e : errors) {
				Log.debug("SegmentError: " + XmlUtil.getPath(e.getSegmentTag().getNode()) + ": " + e.getMessage());
			}
			int choice = JOptionPane.showConfirmDialog(this, "Some segments have invalid tags. They would be saved without tags. Save anyway?", "Invalid segments found", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				return save_to_file();
			}
			else {
				return false;
			}
		}
	}

	boolean validateFile() {
		ArrayList<FileTag> fileTags = getAllFileTags();
		xliffTag.setFiles(fileTags);

		ArrayList<SegmentError> errors = new ArrayList<>();
		xliffTag.encode(errors, true);

		for (SegmentError e : errors) {
			// there should be no invalid non-initial segments, log for debugging only
			Log.err("validateFile: SegmentError: " + XmlUtil.getPath(e.getSegmentTag().getNode()) + ": " + e.getMessage());
		}

		String xmlData;
		try {
			xmlData = save_to_string();
		}
		catch (SaveException ex) {
			JOptionPane.showMessageDialog(this, "Could not validate file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		ArrayList<XliffFileValidator.ValidationError> validationErrors = XliffFileValidator.validate(xmlData);
		if (validationErrors.isEmpty() == false) {
			JOptionPane.showMessageDialog(this, "Unit tag errors found", "", JOptionPane.ERROR_MESSAGE);
			for (XliffFileValidator.ValidationError e : validationErrors) {
				Log.debug(e.toString());
			}
			return false;
		}
		return true;
	}

	void export() {
		File f = xliffTag.getFile().getAbsoluteFile();
		RainbowHandler rainbowHandler = new RainbowHandler();
		try {
			rainbowHandler.exportTranslatedFile(f);
		}
		catch (IOException | RainbowError ex) {
			JOptionPane.showMessageDialog(this, "Could not export file: " + f.toString() + "\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	boolean isModified() {
		for (FileView fileView : getAllFileViews()) {
			if (fileView.getUndoManager().isModified()) {
				return true;
			}
		}
		return false;
	}

	public void updateTabTitle(FileView fileView) {
		int index = jTabbedPane1.indexOfComponent(fileView);
		String name = fileView.getName();
		jTabbedPane1.setTitleAt(index, truncate(name));
	}

	boolean okToClose() {
		if (isModified() == false) {
			return true;
		}
		int choice = JOptionPane.showConfirmDialog(this, "Save changes before closing?", "Save changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		switch (choice) {
			case JOptionPane.YES_OPTION:
				return save();
			case JOptionPane.NO_OPTION:
				return true;
			case JOptionPane.CANCEL_OPTION:
			default:
				return false;
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 733, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
