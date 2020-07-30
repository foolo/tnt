package tnt.editor;

import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class Application {

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		}
		catch (javax.swing.UnsupportedLookAndFeelException ex) {
			System.err.println(ex);
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainForm mainForm = new MainForm();
				mainForm.setLocationRelativeTo(null);
				mainForm.setVisible(true);
				mainForm.load_file();
			}
		});
	}
}
