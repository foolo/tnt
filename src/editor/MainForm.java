package editor;

import java.awt.Component;
import java.awt.EventQueue;
import undo_manager.UndoManager;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import undo_manager.CaretPosition;
import undo_manager.UndoEventListener;
import util.MessageBox;
import util.XmlUtil;
import xliff_model.FileTag;
import xliff_model.InvalidXliffFormatException;
import xliff_model.XliffTag;
import undo_manager.UndoableState;
import xliff_model.SegmentTag;

public class MainForm extends javax.swing.JFrame implements UndoEventListener {

	public MainForm() {
		initComponents();
	}

	UndoManager undoManager;

	void populate_segments(FileTag fileTag) {
		for (SegmentTag s : fileTag.getSegmentsArray()) {
			jPanelItems.add(new SegmentView(undoManager));
		}
	}

	@Override
	public void notify_undo(CaretPosition newEditingPosition) {
		update_model();
		Component c = jPanelItems.getComponent(newEditingPosition.getItemIndex());
		SegmentView segmentView = (SegmentView) c;
		segmentView.setTextPosition(newEditingPosition.getColumn(), newEditingPosition.getTextPosition());
	}

	public void update_model() {
		ArrayList<SegmentTag> segments = ((FileTag) undoManager.getCurrentState().getModel()).getSegmentsArray();
		if (segments.size() < jPanelItems.getComponentCount()) {
			int remove_count = jPanelItems.getComponentCount() - segments.size();
			for (int i = 0; i < remove_count; i++) {
				jPanelItems.remove(jPanelItems.getComponentCount() - 1);
			}
		}
		for (int i = 0; i < segments.size(); i++) {
			SegmentView segmentView;
			if (i > jPanelItems.getComponentCount() - 1) {
				segmentView = new SegmentView(undoManager);
				jPanelItems.add(segmentView);
			}
			else {
				Component c = jPanelItems.getComponent(i);
				segmentView = (SegmentView) c;
			}
			segmentView.setSegmentTag(segments.get(i), i);
		}
	}

	public void load_file(File f) throws InvalidXliffFormatException {
		Document doc = XmlUtil.read_xml(f);
		Node root = doc.getDocumentElement();
		System.out.println("root node: " + root);
		XliffTag xliffFile = new XliffTag(root);

		// todo handle multiple files
		FileTag fileTag = xliffFile.getFiles().get(0);
		undoManager = new UndoManager();
		CaretPosition pos = new CaretPosition(0, CaretPosition.Column.SOURCE, 0);
		undoManager.initialize(new UndoableState(fileTag, pos.copy(), pos.copy(), undoManager), this);
		populate_segments(fileTag);
		update_model();
	}

	public void menu_open() {
		// todo close current file
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.home"))); // todo store last dir in preferences
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		try {
			load_file(fc.getSelectedFile());
		}
		catch (InvalidXliffFormatException ex) {
			MessageBox.error(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelItems = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelItems.setLayout(new javax.swing.BoxLayout(jPanelItems, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane2.setViewportView(jPanelItems);

        jMenu1.setText("File");

        jMenuItemOpen.setText("Open XLIFF");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemOpen);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 180, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
		menu_open();
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JPanel jPanelItems;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
