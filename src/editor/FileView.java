package editor;

import java.awt.Component;
import java.util.ArrayList;
import undo_manager.CaretPosition;
import undo_manager.UndoEventListener;
import undo_manager.UndoManager;
import undo_manager.UndoableState;
import xliff_model.FileTag;
import xliff_model.InvalidXliffFormatException;
import xliff_model.SegmentTag;
import xliff_model.UnitTag;
import xliff_model.XliffTag;

public class FileView extends javax.swing.JPanel implements UndoEventListener {

	UndoManager undoManager;
	XliffTag xliffFile;

	public FileView() {
		initComponents();
	}

	@Override
	public void notify_undo(CaretPosition newEditingPosition) {
		update_model();
		Component c = jPanelItems.getComponent(newEditingPosition.getItemIndex());
		SegmentView segmentView = (SegmentView) c;
		segmentView.setTextPosition(newEditingPosition.getColumn(), newEditingPosition.getTextPosition());
	}

	public void update_model() {
		ArrayList<SegmentTag> segments = ((FileTag) undoManager.getCurrentState().getModel()).getSegmentsArray();
		if (segments.size() < jPanelItems.getComponentCount()) {
			int remove_count = jPanelItems.getComponentCount() - segments.size();
			for (int i = 0; i < remove_count; i++) {
				jPanelItems.remove(jPanelItems.getComponentCount() - 1);
			}
		}
		for (int i = 0; i < segments.size(); i++) {
			SegmentView segmentView;
			if (i > jPanelItems.getComponentCount() - 1) {
				segmentView = new SegmentView(undoManager);
				jPanelItems.add(segmentView);
			}
			else {
				Component c = jPanelItems.getComponent(i);
				segmentView = (SegmentView) c;
			}
			segmentView.setSegmentTag(segments.get(i), i);
		}
	}

	public void load_file(FileTag fileTag) throws InvalidXliffFormatException {
		undoManager = new UndoManager();
		CaretPosition pos = new CaretPosition(0, CaretPosition.Column.SOURCE, 0);
		undoManager.initialize(new UndoableState(fileTag, pos, pos, undoManager), this);
		ArrayList<SegmentTag> segments = fileTag.getSegmentsArray();
		populate_segments(segments);
		update_model();
	}

	void populate_segments(ArrayList<SegmentTag> segments) {
		for (SegmentTag s : segments) {
			jPanelItems.add(new SegmentView(undoManager));
		}
	}

	SegmentTag getSegmentTag(int index) {
		Component c = jPanelItems.getComponent(index);
		SegmentView segmentView = (SegmentView) c;
		return segmentView.getSegmentTag();
	}

	void split() {
		CaretPosition p = undoManager.getCaretPosition();
		if (p.getColumn() != CaretPosition.Column.SOURCE) {
			System.err.println("can only split when caret is in source column");
			return;
		}
		System.out.println("current pos: " + p);
		SegmentTag segmentTag = getSegmentTag(p.getItemIndex());
		UnitTag unitTag = segmentTag.getParent();
		CaretPosition newPosition = unitTag.split(p, segmentTag);
		if (newPosition == null) {
			System.err.println("split failed");
			return;
		}
		undoManager.getCurrentState().setModified(newPosition);
		undoManager.save();
		update_model();
	}

	void save() {
		// TODO add your handling code here:
		FileTag fileTag = (FileTag) undoManager.getCurrentState().getModel();
		ArrayList<FileTag> files = new ArrayList<>();
		files.add(fileTag);
		xliffFile.setFiles(files);
		xliffFile.save();
		//XmlUtil.write_xml(xliffFile.getDocument(), new StreamResult());
		//System.out.println(XmlUtil.getNodeString(xliffFile.getNode()));
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelItems = new javax.swing.JPanel();

        jPanelItems.setLayout(new javax.swing.BoxLayout(jPanelItems, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(jPanelItems);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelItems;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
