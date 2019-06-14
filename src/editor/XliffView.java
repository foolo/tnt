package editor;

import java.awt.Component;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import util.Log;
import util.XmlUtil;
import xliff_model.FileTag;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;
import xliff_model.SegmentError;
import xliff_model.XliffTag;

public class XliffView extends javax.swing.JPanel {

	private XliffTag xliffTag;

	public XliffView() {
		initComponents();
	}

	FileView getActiveFileView() {
		return ((FileView) jTabbedPane1.getSelectedComponent());
	}

	XliffTag getXliffTag() {
		return xliffTag;
	}

	static String truncate(String s) {
		if (s.length() > 80) {
			return "..." + s.substring(s.length() - 77, s.length());
		}
		return s;
	}

	void load_xliff(File f) throws ParseException, LoadException {
		Document doc = XmlUtil.read_xml(f);
		xliffTag = new XliffTag(doc);
		jTabbedPane1.removeAll();
		for (FileTag fileTag : xliffTag.getFiles()) {
			FileView fv = new FileView(this);
			fv.load_file(fileTag);
			jTabbedPane1.add(fv);
			updateTabTitle(fv);
		}
	}

	void copy_source_to_target() {
		FileView fv = getActiveFileView();
		if (fv == null) {
			return;
		}
		fv.copy_source_to_target();
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
		StringWriter writer = new StringWriter();
		XmlUtil.write_xml(getXliffTag().getDocument(), new StreamResult(writer));

		// todo save to file
		System.out.println(writer.toString());
		markAsSaved();
		return true;
	}

	boolean save() {
		ArrayList<FileTag> fileTags = getAllFileTags();
		xliffTag.setFiles(fileTags);

		ArrayList<SegmentError> errors = new ArrayList<>();
		xliffTag.save(errors);

		if (errors.isEmpty()) {
			return save_to_file();
		}
		else {
			for (SegmentError e : errors) {
				Log.debug("SegmentError: " + XmlUtil.getPath(e.getSegmentTag().getNode()) + ": " + e.getMessage());
			}
			int choice = JOptionPane.showConfirmDialog(this, "Some segments have invalid content. They would be saved as empty. Save anyway?", "Invalid segments found", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				return save_to_file();
			}
			else {
				return false;
			}
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
