package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	public static File extract(File f) {
		try {
			File tmp_path = Files.createTempDirectory("tntzip_" + f.getName() + "_").toFile();
			unzip(f, tmp_path);
			return tmp_path;
		}
		catch (IOException ex) {
			Log.err(ex.toString());
		}
		return null;
	}

	private static void unzip(File zipFilePath, File destDir) {
		byte[] buffer = new byte[1024];
		try {
			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
				ZipEntry ze = zis.getNextEntry();
				while (ze != null) {
					String fileName = ze.getName();
					File newFile = new File(destDir, File.separator + fileName);
					System.out.println("Unzipping to " + newFile.getAbsolutePath());
					new File(newFile.getParent()).mkdirs();
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
					zis.closeEntry();
					ze = zis.getNextEntry();
				}
				zis.closeEntry();
			}
		}
		catch (IOException ex) {
			Log.err(ex.toString());
		}
	}

	public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
		Path p = Files.createFile(Paths.get(zipFilePath));
		try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
			Path pp = Paths.get(sourceDirPath);
			Files.walk(pp)
					.filter(path -> !Files.isDirectory(path))
					.forEach(path -> {
						ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
						try {
							zs.putNextEntry(zipEntry);
							Files.copy(path, zs);
							zs.closeEntry();
						}
						catch (IOException e) {
							System.err.println(e);
						}
					});
		}
	}
}
