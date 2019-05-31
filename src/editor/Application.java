package editor;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;

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
			@Override
			public void run() {
				MainForm mainForm = new MainForm();
				mainForm.setVisible(true);
				if (args.length > 0) {
					mainForm.load_file(new File(args[0]));
				}
			}
		});
	}
}
