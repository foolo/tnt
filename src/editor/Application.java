package editor;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.plaf.metal.MetalLookAndFeel;
import util.Log;
import util.Settings;

public class Application {

	public static void main(String args[]) {
		try {
			javax.swing.UIManager.setLookAndFeel(new MetalLookAndFeel());
		}
		catch (javax.swing.UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Log.initializeLogger();
				MainForm mainForm = new MainForm();
				mainForm.setVisible(true);
				if (args.length > 0) {
					mainForm.load_file(new File(args[0]), true);
				}
				else {
					File lastOpenedFile = Settings.getLastOpenedFile();
					mainForm.load_file(lastOpenedFile, false);
				}
			}
		});
	}
}
