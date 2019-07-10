package conversion;

import com.maxprograms.converters.Convert;
import com.maxprograms.converters.Merge;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import util.Log;

public class OpenXliffHandler {

	public static void copy(InputStream source, String destination) throws IOException {
		Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
	}

	void checkResource(File f) throws ConversionError {
		if (f.exists() == false) {
			throw new ConversionError("Expected resource not found in working directory: " + f);
		}
	}

	void checkResources() throws ConversionError {
		File dir = new File(System.getProperty("user.dir"));
		Log.debug("checkResources: " + dir);
		checkResource(new File(dir, "catalog/catalog.xml"));
		checkResource(new File(dir, "srx/default.srx"));
		checkResource(new File(dir, "xmlfilter"));
	}

	public File createPackage(File inputFile, File xliffFile, File skeletonFile, String sourceLanguage, String targetLanguage) throws ConversionError {
		Log.debug("createPackage: inputFile: " + inputFile);
		Log.debug("createPackage: xliffFile: " + xliffFile);
		Log.debug("createPackage: skeletonFile: " + skeletonFile);
		checkResources();

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
			throw new ConversionError("No output XLIFF file was found at: " + xliffFile);
		}
		if (skeletonFile.exists() == false) {
			throw new ConversionError("No output skeleton file was found at: " + skeletonFile);
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

	public File exportTranslatedFile(File xliffFile) throws ConversionError, IOException {
		Log.debug("exportTranslatedFile: xliffFile: " + xliffFile);
		checkResources();

		String targetFileName = getTargetFilename(xliffFile.getName(), "sv");
		File targetFile = new File(xliffFile.getParentFile(), targetFileName);
		Log.debug("exportTranslatedFile: targetFile: " + targetFile);

		targetFile.delete();
		String args[] = new String[]{
			"-xliff", xliffFile.getAbsolutePath(),
			"-target", targetFile.getAbsolutePath()
		};
		Log.debug("merge args: " + String.join(" ", args));
		Merge.main(args);

		if (targetFile.getAbsoluteFile().exists() == false || targetFile.getAbsoluteFile().length() == 0) {
			throw new ConversionError("Expected output file not found or empty: " + targetFile);
		}
		return targetFile;
	}
}
