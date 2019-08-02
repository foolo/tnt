package editor;

import java.awt.Component;
import java.util.ArrayList;
import java.util.regex.Pattern;
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
			segmentView.updateSegmentTag(segmentTags.get(i));
		}
	}

	void scroll_to_segment(SegmentView segmentView) {
		int dest_y = segmentView.getBounds().y;
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

	void jumpToNextSegment(SegmentView currentSegment) {
		boolean found = false;
		for (Component c : jPanelItems.getComponents()) {
			if (found) {
				SegmentView segmentView = ((SegmentView) c);
				segmentView.navigateToView(currentSegment.getActiveColumn(), 0);
				scroll_to_segment(segmentView);
				return;
			}
			if ((SegmentView) c == currentSegment) {
				found = true;
			}
		}
	}

	void jumpToPreviousSegment(SegmentView currentSegment) {
		SegmentView previousSegment = null;
		for (Component c : jPanelItems.getComponents()) {
			if ((SegmentView) c == currentSegment) {
				if (previousSegment != null) {
					previousSegment.navigateToView(currentSegment.getActiveColumn(), 0);
					scroll_to_segment(previousSegment);
				}
				return;
			}
			previousSegment = (SegmentView) c;
		}
	}

	boolean match(String term, String text, boolean matchCase) {
		if (matchCase) {
			return text.contains(term);
		}
		else {
			return Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS).matcher(text).find();
		}
	}

	void applyFilter() {
		for (Component c : jPanelItems.getComponents()) {
			SegmentView sv = (SegmentView) c;
			int flags = jCheckBoxMatchCase.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
			sv.applyFilter(jTextFieldSourceFilter.getText(), jTextFieldTargetFilter.getText(), flags);
		}
		Session.getUndoManager().resetUndoBuffer();
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelItems = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jTextFieldSourceFilter = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jTextFieldTargetFilter = new javax.swing.JTextField();
        jCheckBoxMatchCase = new javax.swing.JCheckBox();

        setMinimumSize(new java.awt.Dimension(800, 0));

        jPanelItems.setLayout(new javax.swing.BoxLayout(jPanelItems, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(jPanelItems);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jTextFieldSourceFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSourceFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTextFieldSourceFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jTextFieldSourceFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5);

        jTextFieldTargetFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldTargetFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jTextFieldTargetFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jTextFieldTargetFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6);

        jCheckBoxMatchCase.setText("Aa");
        jCheckBoxMatchCase.setToolTipText("Match case");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxMatchCase)
                .addGap(105, 105, 105))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jCheckBoxMatchCase, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldSourceFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSourceFilterActionPerformed
		applyFilter();
    }//GEN-LAST:event_jTextFieldSourceFilterActionPerformed

    private void jTextFieldTargetFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTargetFilterActionPerformed
		applyFilter();
    }//GEN-LAST:event_jTextFieldTargetFilterActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckBoxMatchCase;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelItems;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldSourceFilter;
    private javax.swing.JTextField jTextFieldTargetFilter;
    // End of variables declaration//GEN-END:variables
}
