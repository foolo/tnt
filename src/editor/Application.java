package editor;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.plaf.metal.MetalLookAndFeel;
import language.LanguageCollection;
import util.Log;
import util.Settings;

public class Application {

	public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

		public void handle(Throwable thrown) {
			handleException(Thread.currentThread().getName(), thrown);
		}

		@Override
		public void uncaughtException(Thread thread, Throwable thrown) {
			handleException(thread.getName(), thrown);
		}

		protected void handleException(String tname, Throwable thrown) {
			Log.err(thrown);
		}
	}

	public static void main(String args[]) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
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
				try {
					LanguageCollection.loadLanguages();
				}
				catch (IOException ex) {
					Log.err(ex);
					JOptionPane.showMessageDialog(null, "Could not load language list. Spelling will not be available.\n" + ex.toString(), "", JOptionPane.ERROR_MESSAGE);
				}
				MainForm mainForm = new MainForm();
				mainForm.setLocationRelativeTo(null);
				mainForm.setVisible(true);
				if (args.length > 0) {
					mainForm.load_file(new File(args[0]), true);
				}
				else {
					File lastOpenedFile = Settings.getLastOpenedFile();
					if (lastOpenedFile != null) {
						mainForm.load_file(lastOpenedFile, false);
					}
				}
			}
		});
	}
}
