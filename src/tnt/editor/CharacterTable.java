package tnt.editor;

import java.awt.Font;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CharacterTable extends JTable {

	public CharacterTable() {
		init();
	}
	private static final String ENTER_ACTION_KEY = "ENTER_ACTION_KEY";

	private void init() {
		setFont(new Font(Font.SERIF, Font.PLAIN, 24));
		((DefaultTableCellRenderer) getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.CENTER);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSelectionAllowed(false);
		setCellSelectionEnabled(true);
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), ENTER_ACTION_KEY);
	}

	void addEnterAction(Action action) {
		getActionMap().put(ENTER_ACTION_KEY, action);
	}

	DefaultTableModel tableModel = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public void addSelectionListener(ListSelectionListener listener) {
		getSelectionModel().addListSelectionListener(listener);
		getColumnModel().getSelectionModel().addListSelectionListener(listener);
	}

	Character getSelectedChar() {
		int row = getSelectedRow();
		int col = getSelectedColumn();
		if (row < 0 || col < 0) {
			return null;
		}
		return (Character) getModel().getValueAt(row, col);
	}

	public void update(String characters) {
		int cols = 8;
		int rows = ((characters.length() - 1) / cols) + 1;
		while (tableModel.getColumnCount() < cols) {
			tableModel.addColumn("");
		}
		tableModel.setRowCount(0);
		for (int i = 0; i < rows; i++) {
			tableModel.addRow(new SpecialCharacterDialog.CharInfo[cols]);
		}
		for (int i = 0; i < characters.length(); i++) {
			int row = i / cols;
			int col = i % cols;
			tableModel.setValueAt(characters.charAt(i), row, col);
		}
		setModel(tableModel);
	}
}
