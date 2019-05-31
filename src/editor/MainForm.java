package editor;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import util.Log;
import util.XmlUtil;
import xliff_model.FileTag;
import xliff_model.LoadException;
import xliff_model.ParseException;
import xliff_model.SegmentError;
import xliff_model.XliffTag;

public class MainForm extends javax.swing.JFrame {

	XliffTag xliffFile;

	public MainForm() {
		initComponents();
	}

	public void load_file(File f) {
		try {
			Document doc = XmlUtil.read_xml(f);
			xliffFile = new XliffTag(doc);

			// todo handle multiple files
			FileTag fileTag = xliffFile.getFiles().get(0);
			fileView1.load_file(fileTag);
		}
		catch (LoadException ex) {
			JOptionPane.showMessageDialog(null, "Could not open file\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
		catch (ParseException ex) {
			Log.err(ex.getMessage());
			JOptionPane.showMessageDialog(null, "Invalid XLIFF format", "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void save_file() {
		ArrayList<FileTag> files = new ArrayList<>();
		// todo handle multiple files
		FileTag fileTag = (FileTag) fileView1.undoManager.getCurrentState().getModel();
		files.add(fileTag);
		xliffFile.setFiles(files);

		ArrayList<SegmentError> errors = new ArrayList<>();
		xliffFile.save(errors);

		if (errors.size() > 0) {
			int choice = JOptionPane.showConfirmDialog(this,
					"Some segments have invalid content. They would be saved as empty. Save anyway?",
					"Invalid segments found",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
			}
			else {
				// todo cancel save
				return;
			}
		}
		StringWriter writer = new StringWriter();
		XmlUtil.write_xml(xliffFile.getDocument(), new StreamResult(writer));
		System.out.println(writer.toString());
	}

	public void menu_open() {
		// todo close current file
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.home"))); // todo store last dir in preferences
		int returnVal = fc.showOpenDialog(this);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}
		load_file(fc.getSelectedFile());
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileView1 = new editor.FileView();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemCopySrc = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jMenu1.setText("File");

        jMenuItemOpen.setText("Open XLIFF");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemOpen);

        jMenuItemSave.setText("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSave);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItemCopySrc.setText("Copy source to target");
        jMenuItemCopySrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopySrcActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemCopySrc);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(fileView1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
		menu_open();
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
		save_file();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemCopySrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopySrcActionPerformed
		fileView1.copy_source_to_target();
    }//GEN-LAST:event_jMenuItemCopySrcActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private editor.FileView fileView1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemCopySrc;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSave;
    // End of variables declaration//GEN-END:variables
}
