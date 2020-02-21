package tnt.editor;

import java.util.ArrayList;
import tnt.xliff_model.XliffTag;

public class FileView extends javax.swing.JPanel {

	public FileView() {
		initComponents();
	}

	SegmentView getSegmentView(int index) {
		return segmentView1;
	}

	ArrayList<SegmentView> getSegmentViews() {
		ArrayList<SegmentView> res = new ArrayList<>();
		res.add(segmentView1);
		return res;
	}

	public void update_model(XliffTag xliffTag) {
		segmentView1.updateSegmentTag("Så här hanterar man coronaviruset");
	}

	void updateHeights() {
		segmentView1.updateHeight();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        segmentView1 = new tnt.editor.SegmentView();

        setMinimumSize(new java.awt.Dimension(800, 0));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentView1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(segmentView1, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private tnt.editor.SegmentView segmentView1;
    // End of variables declaration//GEN-END:variables
}
