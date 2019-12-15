package tnt.util;

import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.swing.JOptionPane;

public class FileUtil {

	private static class CopyException extends RuntimeException {

		public CopyException(String message) {
			super(message);
		}
	}

	private static void copy(Path source, Path dest) {
		try {
			Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			throw new CopyException(e.toString());
		}
	}

	public static void copyFolder(Path src, Path dest) throws IOException {
		try {
			Files.walk(src).forEach(source -> copy(source, dest.resolve(src.relativize(source))));
		}
		catch (CopyException ex) {
			throw new IOException(ex.getMessage());
		}
	}

	public static void desktopOpen(Component parent, File f) {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(f);
			}
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(parent, "Could not open direcory: " + f.toString() + "\n" + ex.getMessage(), "", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static File getExistingDirectory(File file) {
		File f = file;
		for (int i = 0; i < 10 && f != null; i++) {
			if (f.exists()) {
				return f;
			}
			f = f.getParentFile();
		}
		return f;
	}
}
