package rainbow;

import com.maxprograms.converters.Convert;
import com.maxprograms.converters.Merge;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import util.Log;

public class OpenXliffHandler {

	public static void copy(InputStream source, String destination) throws IOException {
		Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
	}

	File getResourceDir() {
		try {
			File classLocation = new File(OpenXliffHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (classLocation.isFile() && classLocation.getPath().endsWith(".jar")) {
				return classLocation.getParentFile();
			}
			else {
				return new File(classLocation.getParentFile().getParentFile(), "OpenXLIFF"); // build/classes/../../OpenXLIFF
			}
		}
		catch (URISyntaxException ex) {
			Log.err(ex);
		}
		return new File(System.getProperty("user.dir"));
	}

	public File createPackage(File inputFile, File xliffFile, File skeletonFile, String sourceLanguage, String targetLanguage) throws RainbowError {
		Log.debug("createPackage: inputFile: " + inputFile);
		Log.debug("createPackage: xliffFile: " + xliffFile);
		Log.debug("createPackage: skeletonFile: " + skeletonFile);
		File resourceDir = getResourceDir();
		Log.debug("createPackage: resourceDir: " + resourceDir);

		System.setProperty("user.dir", resourceDir.getAbsolutePath());
		Log.debug("user.dir: " + System.getProperty("user.dir"));

		xliffFile.delete();
		skeletonFile.delete();

		String args[] = new String[]{
			"-file", inputFile.getAbsolutePath(),
			"-srcLang", sourceLanguage, // todo language
			"-tgtLang", targetLanguage,
			"-2.0"
		};
		Log.debug("convert args: " + String.join(" ", args));
		Convert.main(args);

		File skeletonLocalFile = new File(skeletonFile.getName());
		skeletonLocalFile.renameTo(skeletonFile);

		if (xliffFile.exists() == false) {
			throw new RainbowError("No output XLIFF file was found at: " + xliffFile);
		}
		if (skeletonFile.exists() == false) {
			throw new RainbowError("No output skeleton file was found at: " + skeletonFile);
		}

		return xliffFile;
	}

	static String getTargetFilename(String xliffFilename, String targetLanguage) {
		String unknownFilename = xliffFilename + "." + targetLanguage + ".unknown_filetype";
		if (xliffFilename.endsWith(".xlf") == false) {
			return unknownFilename;
		}
		String originalName = xliffFilename.substring(0, xliffFilename.length() - 4);
		int dotPos = originalName.lastIndexOf('.');
		if (dotPos < 0) {
			return unknownFilename;
		}
		String baseName = originalName.substring(0, dotPos);
		String ext = originalName.substring(dotPos, originalName.length());
		return baseName + "_" + targetLanguage + ext;
	}

	public File exportTranslatedFile(File xliffFile) throws RainbowError, IOException {
		Log.debug("exportTranslatedFile: xliffFile: " + xliffFile);

		File resourceDir = getResourceDir();
		System.setProperty("user.dir", resourceDir.getAbsolutePath());
		Log.debug("user.dir: " + System.getProperty("user.dir"));

		String targetFileName = getTargetFilename(xliffFile.getName(), "sv");
		File targetFile = new File(xliffFile.getParentFile(), targetFileName);
		Log.debug("exportTranslatedFile: targetFile: " + targetFile);

		String args[] = new String[]{
			"-xliff", xliffFile.getAbsolutePath(),
			"-target", targetFile.getAbsolutePath()
		};

		Log.debug("merge args: " + String.join(" ", args));
		Merge.main(args);

		if (targetFile.getAbsoluteFile().exists() == false) {
			throw new RainbowError("Expected output file not found: " + targetFile);
		}
		return targetFile;
	}
}
