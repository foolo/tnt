package editor;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import undo_manager.UndoManager;
import xliff_model.SegmentTag;

public class SegmentCellEditor extends AbstractCellEditor implements TableCellEditor {

	SegmentView segmentView;
	UndoManager undoManager;

	public SegmentCellEditor(UndoManager undoManager) {
		this.undoManager = undoManager;
		segmentView = new SegmentView(this.undoManager);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		segmentView.setSegmentTag((SegmentTag)value, row);
		segmentView.registerListeners();
		return segmentView;
	}

	@Override
	public Object getCellEditorValue() {
		segmentView.unregisterListeners();
		return segmentView.getSegmentTag();
	}
	
	// todo needed?
	@Override
	public boolean stopCellEditing() {
		boolean res = super.stopCellEditing();
		System.out.println("editor.SegmentCellEditor.stopCellEditing()");
		segmentView.unregisterListeners();
		//undoManager.save();
		return res;
	}
}
