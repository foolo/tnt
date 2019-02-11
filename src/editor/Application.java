package editor;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import util.MessageBox;
import xliff_model.InvalidXliffFormatException;

public class Application {

	public static void main(String args[]) {
		try {
			String osVersion = System.getProperty("os.name");
			if (osVersion.contains("Linux")) {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			}
			else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				MainForm mainForm = new MainForm();
				mainForm.setVisible(true);
				
				// todo tmp
				try {
					mainForm.load_file(new File("/home/olof/Downloads/doctest/test_odt.odt.xlf2"));
				}
				catch (InvalidXliffFormatException ex) {
					MessageBox.error(ex.getMessage());
				}
			}
		});
	}
}
