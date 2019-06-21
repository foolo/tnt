package editor;

import java.util.ArrayList;
import xliff_model.SegmentTag;
import xliff_model.UnitTag;

public class UnitView extends javax.swing.JPanel {

	FileView fileView;

	UnitTag unitTag;

	public UnitView() {
		initComponents();
	}

	public UnitView(FileView fileView) {
		initComponents();
		this.fileView = fileView;
	}

	public void setUnitTag(UnitTag unitTag) {
		this.unitTag = unitTag;
		ArrayList<SegmentTag> segments = unitTag.getSegments();

		for (int i = 0; i < segments.size(); i++) {
			SegmentView segmentView = (SegmentView) getComponent(i);
			segmentView.setSegmentTag(segments.get(i));
		}
	}

	void populateSegments(int count) {
		for (int i = 0; i < count; i++) {
			add(new SegmentView(fileView));
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
