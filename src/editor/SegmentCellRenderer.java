package editor;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import xliff_model.SegmentTag;

public class SegmentCellRenderer extends SegmentView implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setSegmentTag((SegmentTag)value);
		return this;
	}

}
