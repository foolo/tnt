package editor;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import language.DictionaryList;
import language.DictionaryMapper;
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

	static void loadDictionaries() {
		String dir = Settings.getDictionariesLocation();
		if (dir == null) {
			Log.debug("no dictionary directory set, no dictionary search will be performed");
			return;
		}
		File f = new File(dir);
		if (f.exists() == false) {
			JOptionPane.showMessageDialog(null, "Dictionaries path '" + dir + "' not found.\nSpellcheck will not be available.", "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		DictionaryList dictionaryList = new DictionaryList();
		if (dictionaryList.load(f) == false) {
			JOptionPane.showMessageDialog(null, "Could not load dictionaries from " + dir + ".\nSpellcheck will not be available.", "", JOptionPane.ERROR_MESSAGE);
			return;
		}
		DictionaryMapper.mapDictionaries(dictionaryList);
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
				//loadDictionaries();
				ToolTipManager.sharedInstance().setInitialDelay(500);
				ToolTipManager.sharedInstance().setDismissDelay(60000);
				MainForm mainForm = new MainForm();
				ArrayList<Image> images = new ArrayList<>();
				images.add(Toolkit.getDefaultToolkit().getImage(mainForm.getClass().getResource("/images/Gnome-accessories-character-map_48.png")));
				images.add(Toolkit.getDefaultToolkit().getImage(mainForm.getClass().getResource("/images/Gnome-accessories-character-map_64.png")));
				mainForm.setIconImages(images);
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
