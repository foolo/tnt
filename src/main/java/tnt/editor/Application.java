package tnt.editor;

import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import tnt.util.Log;

public class Application {

	public static final String APPLICATION_NAME = "tnt";
	public static final String APPLICATION_VERSION = "0.3";

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

	public static void setUIFont() {
		FontUIResource f = new FontUIResource(Font.SANS_SERIF, Font.PLAIN, 11);
		Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, f);
			}
		}
	}

	public static void main(String args[]) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
			UIManager.put("swing.boldMetal", Boolean.FALSE);
			setUIFont();
		}
		catch (javax.swing.UnsupportedLookAndFeelException ex) {
			Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				Log.initializeLogger();
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
