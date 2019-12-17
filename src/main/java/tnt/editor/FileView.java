package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.regex.Pattern;
import tnt.util.Settings;
import tnt.xliff_model.SegmentTag;
import tnt.xliff_model.XliffTag;

public class FileView extends javax.swing.JPanel {

	void setLastCaretPosition(SegmentView segmentView, int column, int caretPos) {
		int segmentIndex = getIndexOfSegmentView(segmentView);
		searchBar1.setSearchPosition(segmentIndex, column, caretPos);
	}

	public FileView() {
		initComponents();
		jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
		searchBar1.setFileView(this);
	}

	SegmentView getSegmentView(int index) {
		return (SegmentView) jPanelItems.getComponent(index);
	}

	public void update_model(XliffTag xliffTag) {
		ArrayList<SegmentTag> segmentTags = xliffTag.getSegmentsArray();
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

	ArrayList<SegmentView> getSegmentViews() {
		ArrayList<SegmentView> res = new ArrayList<>();
		for (Component c : jPanelItems.getComponents()) {
			res.add((SegmentView) c);
		}
		return res;
	}

	void jumpToNextSegment(SegmentView currentSegment) {
		boolean found = false;
		for (Component c : jPanelItems.getComponents()) {
			if (found) {
				SegmentView segmentView = ((SegmentView) c);
				segmentView.navigateToView(currentSegment.getActiveColumn(), null);
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
					previousSegment.navigateToView(currentSegment.getActiveColumn(), null);
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

	public void highlightSelection(MatchLocation ml) {
		SegmentView segmentView = getSegmentView(ml.segmentIndex);
		segmentView.highlightSelection(ml.column, ml.range);
		scroll_to_segment(segmentView);
	}

	public void clearSelection(MatchLocation ml) {
		SegmentView segmentView = getSegmentView(ml.segmentIndex);
		segmentView.clearSelection();
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

	public void highlightMatches(ArrayList<MatchLocation> matchLocations) {
		Component[] components = jPanelItems.getComponents();
		for (Component c : components) {
			((SegmentView) c).clearHighlighting();
		}
		for (MatchLocation ml : matchLocations) {
			SegmentView sv = (SegmentView) components[ml.segmentIndex];
			sv.highlightMatch(ml.column, ml.range);
		}
	}

	void notifyUpdate() {
		searchBar1.notifyUpdate();
	}

	void focusSearchBox() {
		searchBar1.focusSearchBox();
	}

	void focusFirstSegment() {
		if (jPanelItems.getComponentCount() > 0) {
			((SegmentView) jPanelItems.getComponent(0)).navigateToView(SegmentView.Column.TARGET, 0);
		}
	}

	void applyFontPreferences() {
		Font f = new Font(Settings.getEditorFontName(), Font.PLAIN, Settings.getEditorFontSize());
		int minHeight = SegmentView.getMinHeightForFont(f);
		for (Component c : jPanelItems.getComponents()) {
			((SegmentView) c).setEditorFont(f, minHeight);
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
        searchBar1 = new tnt.editor.search.SearchBar();

        setMinimumSize(new java.awt.Dimension(800, 0));

        jPanelItems.setLayout(new javax.swing.BoxLayout(jPanelItems, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(jPanelItems);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
            .addComponent(searchBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(searchBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelItems;
    private javax.swing.JScrollPane jScrollPane1;
    private tnt.editor.search.SearchBar searchBar1;
    // End of variables declaration//GEN-END:variables
}
