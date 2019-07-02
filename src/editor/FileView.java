package editor;

import java.awt.Component;
import java.util.ArrayList;
import rainbow.ValidationError;
import xliff_model.FileTag;
import xliff_model.UnitTag;

public class FileView extends javax.swing.JPanel {

	private final MainForm mainForm;
	private final String fileId;

	public FileView(MainForm mainForm, String fileId) {
		this.mainForm = mainForm;
		this.fileId = fileId;
		initComponents();
		jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
	}

	public String getFileId() {
		return fileId;
	}

	boolean showValidiationError(ValidationError e) {
		for (Component c : jPanelItems.getComponents()) {
			UnitView unitView = (UnitView) c;
			if (unitView.getUnitId().equals(e.getUnitId())) {
				return unitView.showValidationError(e);
			}
		}
		return false;
	}

	public void update_model(FileTag fileTag) {
		ArrayList<UnitTag> units = fileTag.getUnitsArray();
		if (units.size() != jPanelItems.getComponentCount()) {
			// todo
			throw new RuntimeException("units.size() != jPanelItems.getComponentCount()");
		}
		for (int i = 0; i < units.size(); i++) {
			UnitView unitView = (UnitView) jPanelItems.getComponent(i);
			UnitTag unitTag = units.get(i);
			unitView.setUnitTag(unitTag);
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

	void populate_units(ArrayList<UnitTag> unitTags) {
		for (UnitTag u : unitTags) {
			UnitView unitView = new UnitView(mainForm, u.getId());
			unitView.populateSegments(u.getSegments(), this);
			jPanelItems.add(unitView);
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
