package editor;

import java.awt.Component;
import java.awt.EventQueue;
import undo_manager.UndoManager;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
		
		segmentTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				System.out.println(".tableChanged() " + e);
			}
		});
		
	}
	
	UndoManager undoManager;
	SegmentTableModel segmentTableModel = new SegmentTableModel();
	
	@Override
	public void notify_undo(int newEditingRow) {
		//FileTag fileTag = (FileTag) undoManager.getCurrentState().getModel();
		FileTag fileTag = (FileTag) undoManager.getCurrentState().getModel();
		segmentTableModel.loadSegments(fileTag.getSegmentsArray());
		segmentTableModel.fireTableDataChanged();
		
		if (jTable1.isEditing()) {
			int currentEditingRow = jTable1.getEditingRow();
			//int newEditingRow = undoManager.getCurrentState().getItemIndex();
			if (newEditingRow != currentEditingRow) {
				jTable1.getCellEditor().stopCellEditing();
				
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						jTable1.editCellAt(newEditingRow, 0);
						jTable1.transferFocus();
					}
				});

			}
			else {
				Component c = jTable1.getEditorComponent();
				SegmentView segmentView = (SegmentView) c;
				SegmentTag segmentTag = (SegmentTag) segmentTableModel.getValueAt(newEditingRow, 0);
				segmentView.unregisterListeners(); // todo possible to move to SegmentView.setSegmentTag ?
				segmentView.setSegmentTag(segmentTag, newEditingRow);
				segmentView.registerListeners(); // todo possible to move to SegmentView.setSegmentTag ?*/
			}
		}
	}
	
	@Override
	public void stop_editing() {
		TableCellEditor tableCellEditor = jTable1.getCellEditor();
		if (tableCellEditor != null) {
			tableCellEditor.stopCellEditing();
		}
	}
	
	public void load_file(File f) throws InvalidXliffFormatException {
		Document doc = XmlUtil.read_xml(f);
		Node root = doc.getDocumentElement();
		System.out.println("root node: " + root);
		XliffTag xliffFile = new XliffTag(root);

		// todo handle multiple files
		FileTag fileTag = xliffFile.getFiles().get(0);
		undoManager = new UndoManager(new UndoableState(fileTag, 0, 0), this);
		notify_undo(0); // todo add wrapper called update_model

		// todo move to init, need to set undo manager
		jTable1.setModel(segmentTableModel);
		TableColumn col = jTable1.getColumnModel().getColumn(0);
		col.setCellEditor(new SegmentCellEditor(undoManager));
		col.setCellRenderer(new SegmentCellRenderer());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null}
            },
            new String [] {
                "null"
            }
        ));
        jTable1.setRowHeight(100);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("push");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("pop");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

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
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addComponent(jButton2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jButton3)))
                .addContainerGap(122, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(73, 73, 73)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(77, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
		menu_open();
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
		undoManager.save();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
		undoManager.undo();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		// TODO add your handling code here:
		jTable1.editCellAt(0, 0);
		jTable1.transferFocus();
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
