package editor;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import xliff_model.SegmentTag;

public class SegmentTableModel extends AbstractTableModel{

	ArrayList<SegmentTag> segments = new ArrayList<>();

	public void loadSegments(ArrayList<SegmentTag> segments) {
		this.segments = segments;
	}
	
	@Override
	public int getRowCount() {
		return segments.size();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return segments.get(rowIndex);
	}
	
	@Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}
