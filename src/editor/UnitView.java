package editor;

import java.awt.Component;
import java.util.ArrayList;
import xliff_model.SegmentTag;
import xliff_model.UnitTag;
import xliff_model.ValidationPath;

public class UnitView extends javax.swing.JPanel {

	private final MainForm mainForm;
	private final String unitId;

	public UnitView(MainForm mainForm, String unitId) {
		initComponents();
		this.mainForm = mainForm;
		this.unitId = unitId;
	}

	public String getUnitId() {
		return unitId;
	}

	SegmentView getSegmentView(ValidationPath path) {
		if (path.segmentId.isEmpty() == false) {
			for (Component c : getComponents()) {
				if (c instanceof SegmentView) {
					SegmentView segmentView = (SegmentView) c;
					if (segmentView.getSegmentId().equals(path.segmentId)) {
						return segmentView;
					}
				}
			}
		}
		if (path.codeId.isEmpty() == false) {
			for (Component c : getComponents()) {
				if (c instanceof SegmentView) {
					SegmentView segmentView = (SegmentView) c;
					ArrayList<String> ids = segmentView.getSegmentTag().getSourceText().getIds();
					if (ids.contains(path.codeId)) {
						return segmentView;
					}
				}
			}
		}
		return null;
	}

	boolean showValidationError(String message, ValidationPath path) {
		SegmentView segmentView = getSegmentView(path);
		if (segmentView == null) {
			return false;
		}
		segmentView.showValidationError(message, path);
		return true;
	}

	public void setUnitTag(UnitTag unitTag) {
		ArrayList<SegmentTag> segments = unitTag.getSegments();

		for (int i = 0; i < segments.size(); i++) {
			SegmentView segmentView = (SegmentView) getComponent(i);
			segmentView.setSegmentTag(segments.get(i));
		}
	}

	void populateSegments(ArrayList<SegmentTag> segmentTags, FileView fileView) {
		for (SegmentTag st : segmentTags) {
			add(new SegmentView(mainForm, fileView, st.getId()));
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
