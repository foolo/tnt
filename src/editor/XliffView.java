package editor;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
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
			FileView fv = new FileView();
			String name = fileTag.getOriginalFilePath();
			if (name.isEmpty()) {
				name = fileTag.getId();
			}
			fv.setName(truncate(name));
			fv.load_file(fileTag);
			jTabbedPane1.add(fv);
		}
	}

	void copy_source_to_target() {
		FileView fv = getActiveFileView();
		if (fv == null) {
			return;
		}
		fv.copy_source_to_target();
	}

	ArrayList<FileTag> getAllFileTags() {
		ArrayList<FileTag> files = new ArrayList<>();
		for (Component c : jTabbedPane1.getComponents()) {
			if (!(c instanceof FileView)) {
				Log.err(c.getClass().getName() + "not instance of FileView");
				continue;
			}
			FileView fileView = (FileView) c;
			FileTag fileTag = (FileTag) fileView.undoManager.getCurrentState().getModel();
			files.add(fileTag);
		}
		return files;
	}

	void save(ArrayList<SegmentError> errors) {
		ArrayList<FileTag> fileTags = getAllFileTags();
		xliffTag.setFiles(fileTags);
		xliffTag.save(errors);
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
