package tnt.editor;

import java.awt.Image;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class Application {

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
			UIManager.put("swing.boldMetal", Boolean.FALSE);
		}
		catch (javax.swing.UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ToolTipManager.sharedInstance().setInitialDelay(500);
				ToolTipManager.sharedInstance().setDismissDelay(60000);
				MainForm mainForm = new MainForm();
				ArrayList<Image> images = new ArrayList<>();
				mainForm.setLocationRelativeTo(null);
				mainForm.setVisible(true);
				mainForm.load_file();
			}
		});
	}
}
