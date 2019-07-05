package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
}
