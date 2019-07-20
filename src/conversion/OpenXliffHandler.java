package conversion;

import com.maxprograms.converters.Constants;
import com.maxprograms.converters.Convert;
import com.maxprograms.converters.EncodingResolver;
import com.maxprograms.converters.FileFormats;
import com.maxprograms.converters.Merge;
import com.maxprograms.converters.Utils;
import com.maxprograms.xliff2.ToXliff2;
import editor.Session;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Hashtable;
import java.util.Vector;
import util.Log;
import xliff_model.XliffTag;

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

	void convert(File inputFile, String srcLang, String tgtLang) throws ConversionError {
		String source = inputFile.getAbsolutePath();
		String xliff = inputFile.getAbsolutePath() + ".xlf";
		String skl = inputFile.getAbsolutePath() + ".skl";

		String type = FileFormats.detectFormat(source);
		if (type == null) {
			throw new ConversionError("Unable to auto-detect file format for " + inputFile);
		}
		Log.debug("convert: detected type: " + type);

		String catalog = new File("catalog", "catalog.xml").getAbsolutePath();

		Charset charset = EncodingResolver.getEncoding(source, type);
		if (charset == null) {
			throw new ConversionError("Unable to auto-detect character set for " + inputFile);
		}
		String enc = charset.name();
		Log.debug("Auto-detected encoding: " + enc);

		String srx = new File("srx", "default.srx").getAbsolutePath();

		try {
			if (Utils.isValidLanguage(srcLang) == false) {
				Log.warn("'" + srcLang + "' is not a valid language code.");
			}
			if (Utils.isValidLanguage(tgtLang) == false) {
				Log.warn("'" + tgtLang + "' is not a valid language code.");
			}
		}
		catch (IOException ex) {
			Log.err(ex);
			throw new ConversionError(ex.toString());
		}

		Hashtable<String, String> params = new Hashtable<>();
		params.put("source", source);
		params.put("xliff", xliff);
		params.put("skeleton", skl);
		params.put("format", type);
		params.put("catalog", catalog);
		params.put("srcEncoding", enc);
		params.put("paragraph", "no");
		params.put("srxFile", srx);
		params.put("srcLang", srcLang);
		params.put("tgtLang", tgtLang);
		Vector<String> result = Convert.run(params);
		if (Constants.SUCCESS.equals(result.get(0))) {
			result = ToXliff2.run(new File(xliff), catalog);
			if (Constants.SUCCESS.equals(result.get(0)) == false) {
				throw new ConversionError(result.get(1));
			}
		}
		else {
			throw new ConversionError(result.get(1));
		}
	}

	public File createPackage(File inputFile, File xliffFile, File skeletonFile, String sourceLanguage, String targetLanguage) throws ConversionError {
		Log.debug("createPackage: inputFile: " + inputFile);
		Log.debug("createPackage: xliffFile: " + xliffFile);
		Log.debug("createPackage: skeletonFile: " + skeletonFile);
		checkResources();

		xliffFile.delete();
		skeletonFile.delete();
		convert(inputFile, sourceLanguage, targetLanguage);

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

	static String getTargetFilename(String originalFilePath) {
		String targetLanguage = Session.getProperties().getTrgLang();
		if (targetLanguage.isEmpty()) {
			targetLanguage = "unknown_target_language";
		}
		String originalName = new File(originalFilePath).getName();
		int dotPos = originalName.lastIndexOf('.');
		if (dotPos < 0) {
			return originalName + "_" + targetLanguage;
		}
		String baseName = originalName.substring(0, dotPos);
		String ext = originalName.substring(dotPos, originalName.length());
		return baseName + "_" + targetLanguage + ext;
	}

	void merge(File xliffFile, File targetFile) throws ConversionError, IOException {
		String xliff = xliffFile.getAbsolutePath();
		String target = targetFile.getAbsolutePath();
		String catalog = new File("catalog", "catalog.xml").getAbsolutePath();
		boolean unapproved = false;
		Vector<String> result = Merge.merge(xliff, target, catalog, unapproved);
		if (Constants.SUCCESS.equals(result.get(0)) == false) {
			throw new ConversionError("Merge error: " + result.get(1));
		}
	}

	public File exportTranslatedFile(XliffTag xliffTag) throws ConversionError, IOException {
		File xliffFile = xliffTag.getFile();
		Log.debug("exportTranslatedFile: xliffFile: " + xliffFile);
		checkResources();

		String originalFilePath = xliffTag.getFiles().get(0).getOriginalFilePath();
		String targetFileName = getTargetFilename(originalFilePath);
		File targetFile = new File(xliffFile.getParentFile(), targetFileName);
		Log.debug("exportTranslatedFile: targetFile: " + targetFile);

		targetFile.delete();
		merge(xliffFile, targetFile);

		if (targetFile.getAbsoluteFile().exists() == false || targetFile.getAbsoluteFile().length() == 0) {
			throw new ConversionError("Expected output file not found or empty: " + targetFile);
		}
		return targetFile;
	}
}
