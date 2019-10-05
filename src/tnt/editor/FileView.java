package tnt.editor;

import tnt.editor.search.MatchLocation;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import tnt.xliff_model.FileTag;
import tnt.xliff_model.SegmentTag;

public class FileView extends javax.swing.JPanel {

	static class CaretPosition {

		final SegmentView segmentView;
		final int column;
		final int caretPos;

		CaretPosition(SegmentView segmentView, int column, int caretPos) {
			this.segmentView = segmentView;
			this.column = column;
			this.caretPos = caretPos;
		}
	}

	private CaretPosition lastCaretPosition = null;

	void setLastCaretPosition(SegmentView segmentView, int column, int caretPos) {
		lastCaretPosition = new CaretPosition(segmentView, column, caretPos);
	}

	public FileView(String fileId) {
		initComponents();
		jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
		searchBar1.setFileView(this);
	}

	SegmentView getSegmentView(int index) {
		return (SegmentView) jPanelItems.getComponent(index);
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

	public int calculateCurrentMatchIndex(ArrayList<MatchLocation> matchLocations) {
		if (lastCaretPosition == null) {
			return 0;
		}
		int lastCaretPositionSegmentIndex = getIndexOfSegmentView(lastCaretPosition.segmentView);
		for (int i = 0; i < matchLocations.size(); i++) {
			MatchLocation ml = matchLocations.get(i);
			int[] matchPos = new int[]{ml.segmentIndex, ml.column, ml.range.start};
			int[] caretPos = new int[]{lastCaretPositionSegmentIndex, lastCaretPosition.column, lastCaretPosition.caretPos};
			if (Arrays.compare(matchPos, caretPos) >= 0) {
				// matchPos same or after caretPos
				return i;
			}
		}
		return 0;
	}

	public void selectMatch(MatchLocation ml) {
		SegmentView segmentView = getSegmentView(ml.segmentIndex);
		segmentView.select(ml.column, ml.range);
		lastCaretPosition = new FileView.CaretPosition(segmentView, ml.column, ml.range.start);
		scroll_to_segment(segmentView);
	}

	public void clearSelection(MatchLocation ml) {
		SegmentView segmentView = getSegmentView(ml.segmentIndex);
		segmentView.applyHighlighting(ml.column, ml.range);
	}

	public ArrayList<MatchLocation> findMatches(String term, int flags) {
		ArrayList<MatchLocation> res = new ArrayList<>();
		Component[] components = jPanelItems.getComponents();
		for (int i = 0; i < components.length; i++) {
			SegmentView sv = (SegmentView) components[i];
			sv.findMatches(term, flags, i, res);
		}
		return res;
	}

	public void applyHighlighting(ArrayList<MatchLocation> matchLocations) {
		Component[] components = jPanelItems.getComponents();
		for (Component c : components) {
			((SegmentView) c).clearHighlighting();
		}
		for (MatchLocation ml : matchLocations) {
			SegmentView sv = (SegmentView) components[ml.segmentIndex];
			sv.applyHighlighting(ml.column, ml.range);
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
