package tnt.editor;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import tnt.language.DictionaryList;
import tnt.language.DictionaryMapper;
import tnt.util.Log;
import tnt.util.Settings;

public class Application {

	public static final String APPLICATION_NAME = "tnt";
	public static final String APPLICATION_VERSION = "0.2";

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
		String dir = "dictionaries";
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
				loadDictionaries();
				ToolTipManager.sharedInstance().setInitialDelay(500);
				ToolTipManager.sharedInstance().setDismissDelay(60000);
				MainForm mainForm = new MainForm();
				ArrayList<Image> images = new ArrayList<>();
				images.add(Toolkit.getDefaultToolkit().getImage(mainForm.getClass().getResource("/tnt/images/Gnome-accessories-character-map_48.png")));
				images.add(Toolkit.getDefaultToolkit().getImage(mainForm.getClass().getResource("/tnt/images/Gnome-accessories-character-map_64.png")));
				mainForm.setIconImages(images);
				mainForm.setLocationRelativeTo(null);
				mainForm.setVisible(true);
				if (args.length > 0) {
					mainForm.load_file(new File(args[0]));
				}
				else {
					File lastOpenedFile = Settings.getLastOpenedFile();
					if (lastOpenedFile != null) {
						mainForm.load_file(lastOpenedFile);
					}
				}
			}
		});
	}
}
