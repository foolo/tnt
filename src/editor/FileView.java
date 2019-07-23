package editor;

import java.awt.Component;
import java.util.ArrayList;
import xliff_model.FileTag;
import xliff_model.SegmentTag;
import xliff_model.ValidationPath;

public class FileView extends javax.swing.JPanel {

	private final String fileId;

	public FileView(String fileId) {
		this.fileId = fileId;
		initComponents();
		jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
	}

	public String getFileId() {
		return fileId;
	}

	boolean showValidiationError(String message, ValidationPath path) {
		for (Component c : jPanelItems.getComponents()) {
			SegmentView segmentView = (SegmentView) c;
			if (segmentView.getSegmentId().equals(path.segmentId)) {
				segmentView.showValidationError(message, path);
				return true;
			}
		}
		return false;
	}

	public void update_model(FileTag fileTag) {
		ArrayList<SegmentTag> segmentTags = fileTag.getSegmentsArray();
		for (int i = 0; i < segmentTags.size(); i++) {
			SegmentView segmentView = (SegmentView) jPanelItems.getComponent(i);
			segmentView.setSegmentTag(segmentTags.get(i));
		}
	}

	void scroll_to_segment(SegmentView segmentView) {
		int dest_y = segmentView.getBounds().y + segmentView.getParent().getBounds().y;
		int dest_h = segmentView.getBounds().height;
		int view_y = jScrollPane1.getVerticalScrollBar().getValue();
		int view_h = jScrollPane1.getVerticalScrollBar().getVisibleAmount();
		if (dest_y < view_y) {
			jScrollPane1.getVerticalScrollBar().setValue(dest_y);
		}
		else if (dest_y + dest_h > view_y + view_h) {
			jScrollPane1.getVerticalScrollBar().setValue(dest_y + dest_h - view_h);
		}
	}

	void populate_segments(ArrayList<SegmentTag> segmentTags) {
		for (SegmentTag st : segmentTags) {
			jPanelItems.add(new SegmentView(this, st.getId()));
		}
	}

	void getSegmentViews(ArrayList<SegmentView> segmentViews) {
		for (Component c : jPanelItems.getComponents()) {
			segmentViews.add((SegmentView) c);
		}
	}

	void jumpToNextSegment(SegmentView sv) {
		boolean found = false;
		for (Component c : jPanelItems.getComponents()) {
			if (found) {
				((SegmentView) c).grabFocusTarget();
				return;
			}
			if ((SegmentView) c == sv) {
				found = true;
			}
		}
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
