package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Component;
import java.util.ArrayList;
import java.util.regex.Pattern;
import tnt.xliff_model.SegmentTag;
import tnt.xliff_model.XliffTag;

public class FileView extends javax.swing.JPanel {

	void setLastCaretPosition(SegmentView segmentView, int column, int caretPos) {
		int segmentIndex = getIndexOfSegmentView(segmentView);
	}

	public FileView() {
		initComponents();
		jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
	}

	SegmentView getSegmentView(int index) {
		return (SegmentView) jPanelItems.getComponent(index);
	}

	public void update_model(XliffTag xliffTag) {
		ArrayList<SegmentTag> segmentTags = xliffTag.getSegmentsArray();
		for (int i = 0; i < segmentTags.size(); i++) {
			SegmentView segmentView = (SegmentView) jPanelItems.getComponent(i);
			segmentView.updateSegmentTag("Så här hanterar man coronaviruset");
		}
	}

	void populate_segments(ArrayList<SegmentTag> segmentTags) {
		for (SegmentTag st : segmentTags) {
			jPanelItems.add(new SegmentView(this, st.getInternalId()));
		}
	}

	ArrayList<SegmentView> getSegmentViews() {
		ArrayList<SegmentView> res = new ArrayList<>();
		for (Component c : jPanelItems.getComponents()) {
			res.add((SegmentView) c);
		}
		return res;
	}

	boolean match(String term, String text, boolean matchCase) {
		if (matchCase) {
			return text.contains(term);
		}
		else {
			return Pattern.compile(Pattern.quote(term), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS).matcher(text).find();
		}
	}

	int getIndexOfSegmentView(SegmentView segmentView) {
		Component[] components = jPanelItems.getComponents();
		for (int i = 0; i < components.length; i++) {
			SegmentView sv = (SegmentView) components[i];
			if (sv == segmentView) {
				return i;
			}
		}
		return -1;
	}

	public ArrayList<MatchLocation> findMatches(String term, int flags, boolean includeSource, boolean includeTarget) {
		ArrayList<MatchLocation> res = new ArrayList<>();
		Component[] components = jPanelItems.getComponents();
		for (int i = 0; i < components.length; i++) {
			SegmentView sv = (SegmentView) components[i];
			if (includeSource) {
				sv.findMatches(term, flags, i, 0, res);
			}
			if (includeTarget) {
				sv.findMatches(term, flags, i, 1, res);
			}
		}
		return res;
	}

	void focusFirstSegment() {
		if (jPanelItems.getComponentCount() > 0) {
			((SegmentView) jPanelItems.getComponent(0)).navigateToView(SegmentView.Column.TARGET, 0);
		}
	}

	void updateHeights() {
		for (Component c : jPanelItems.getComponents()) {
			((SegmentView) c).updateHeight();
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelItems = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(800, 0));

        jPanelItems.setLayout(new javax.swing.BoxLayout(jPanelItems, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(jPanelItems);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelItems;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
