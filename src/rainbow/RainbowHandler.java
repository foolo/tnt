package rainbow;

import java.io.BufferedWriter;
import util.FileUtil;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.w3c.dom.Document;
import util.Log;
import util.XmlUtil;
import xliff_model.exceptions.LoadException;
import xliff_model.exceptions.ParseException;

public class RainbowHandler {

	String createPlnData(String srxPath, String packageDirectory, String packageName) {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<rainbowPipeline version=\"1\">\n"
				+ "\n"
				+ "<step class=\"net.sf.okapi.steps.common.RawDocumentToFilterEventsStep\"></step>\n"
				+ "\n"
				+ "<step class=\"net.sf.okapi.steps.segmentation.SegmentationStep\">#v1\n"
				+ "segmentSource.b=true\n"
				+ "segmentTarget.b=false\n"
				+ "renumberCodes.b=false\n"
				+ "sourceSrxPath=" + srxPath + "\n"
				+ "targetSrxPath=\n"
				+ "copySource.b=false\n"
				+ "checkSegments.b=false\n"
				+ "trimSrcLeadingWS.i=-1\n"
				+ "trimSrcTrailingWS.i=-1\n"
				+ "trimTrgLeadingWS.i=-1\n"
				+ "trimTrgTrailingWS.i=-1\n"
				+ "forceSegmentedOutput.b=true\n"
				+ "overwriteSegmentation.b=false\n"
				+ "deepenSegmentation.b=false\n"
				+ "treatIsolatedCodesAsWhitespace.b=false</step>\n"
				+ "\n"
				+ "<step class=\"net.sf.okapi.steps.rainbowkit.creation.ExtractionStep\">#v1\n"
				+ "writerClass=net.sf.okapi.steps.rainbowkit.xliff.XLIFF2PackageWriter\n"
				+ "packageName=" + packageName + "\n"
				+ "packageDirectory=" + packageDirectory + "\n"
				+ "supportFiles=\n"
				+ "message=\n"
				+ "outputManifest.b=true\n"
				+ "createZip.b=false\n"
				+ "sendOutput.b=false\n"
				+ "writerOptions.withOriginalData.b=true\n"
				+ "writerOptions.createTipPackage.b=false</step>\n"
				+ "\n"
				+ "</rainbowPipeline>";
		return s;
	}

	public static void copy(InputStream source, String destination) throws IOException {
		Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
	}

	InputStream getResource(String path) throws IOException {
		InputStream is = getClass().getResourceAsStream(path);
		if (is == null) {
			throw new IOException("getResource: resource not found: " + path);
		}
		return is;
	}

	File[] listXliffFiles(File parent) {
		File[] files = parent.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isFile() && f.getName().toLowerCase().endsWith(".xlf");
			}
		});
		if (files == null) {
			return new File[0];
		}
		return files;
	}

	public File createPackage(String inputFile, String commonDir, String packageName, String sourceLanguage, String targetLanguage) throws RainbowError, IOException {
		Log.debug("createPackage: input file: " + inputFile);
		Log.debug("createPackage: common package directory: " + commonDir);
		Log.debug("createPackage: package name: " + packageName);

		// copy srx file from jar to temporary directory
		File srxTmpFile = File.createTempFile("tnt_defaultSegmentation_", ".srx");
		srxTmpFile.deleteOnExit();
		copy(getResource("/res/defaultSegmentation.srx"), srxTmpFile.getPath());
		Log.debug("createPackage: SRX temporary file: " + srxTmpFile);

		// point the pln file to the srx file
		String plnData = createPlnData(srxTmpFile.getPath(), commonDir, packageName);

		// write the pln file
		File plnTmpFile = File.createTempFile("tnt_pipeline_", ".pln");
		plnTmpFile.deleteOnExit();
		Log.debug("createPackage: PLN temporary file: " + plnTmpFile);
		Files.write(Paths.get(plnTmpFile.getPath()), plnData.getBytes());

		//net.sf.okapi.applications.rainbow.Main.main(args.toArray(new String[args.size()]));
		String tempDir = Files.createTempDirectory("tnt_tmp_").toString();
		Log.debug("createPackage: temporary directory: " + tempDir);
		copy(getResource("/res/encodings.xml"), new File(tempDir, "encodings.xml").getAbsolutePath());
		copy(getResource("/res/languages.xml"), new File(tempDir, "languages.xml").getAbsolutePath());
		copy(getResource("/res/rainbowUtilities.xml"), new File(tempDir, "rainbowUtilities.xml").getAbsolutePath());

		CommandLine2 cl = new CommandLine2();
		cl.execute(tempDir, plnTmpFile.getPath(), inputFile, false, sourceLanguage, targetLanguage);
		File workDir = Paths.get(commonDir, packageName, "work").toFile();
		if ((workDir.exists() == false) || (workDir.isDirectory() == false)) {
			throw new RainbowError("Work directory not created or not a directory: " + workDir);
		}
		File[] xliffFiles = listXliffFiles(workDir);
		if (xliffFiles.length == 0) {
			throw new RainbowError("No output XLIFF file was found in " + workDir);
		}
		if (xliffFiles.length > 1) {
			Log.err("createPackage: Multiple XLIFF files found in output folder: " + workDir);
		}
		return xliffFiles[0];
	}

	public File exportTranslatedFile(File tempDir, File xliffFile, String xliffData) throws RainbowError, IOException {
		File workDir = xliffFile.getParentFile();
		if (workDir == null) {
			throw new RainbowError("exportTranslatedFile: workdir == null, file: " + xliffFile);
		}
		File manifestDir = workDir.getParentFile();
		if (manifestDir == null) {
			throw new RainbowError("exportTranslatedFile: manifestDir == null, workDir: " + workDir);
		}
		File manifestFile = new File(manifestDir, "manifest.rkm");
		if (manifestFile.exists() == false) {
			throw new RainbowError("No manifest.rkm found in parent directory (" + manifestDir + ")\n");
		}
		Log.debug("exportTranslatedFile: temporary directory: " + tempDir);
		File plnTmpFile = new File(tempDir, "export.pln");
		copy(getResource("/res/export.pln"), plnTmpFile.getAbsolutePath());
		copy(getResource("/res/languages.xml"), new File(tempDir, "languages.xml").getAbsolutePath());
		copy(getResource("/res/rainbowUtilities.xml"), new File(tempDir, "rainbowUtilities.xml").getAbsolutePath());
		File tmpManifestDir = new File(tempDir, "package");
		FileUtil.copyFolder(manifestDir.toPath(), tmpManifestDir.toPath());

		File tmpManifestFile = new File(tmpManifestDir, "manifest.rkm");
		String inputFile = tmpManifestFile.getAbsolutePath();

		File tmpXliffFile = new File(new File(tmpManifestDir, workDir.getName()), xliffFile.getName());
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpXliffFile))) {
			writer.write(xliffData);
		}

		CommandLine2 cl = new CommandLine2();
		cl.execute(tempDir.toString(), plnTmpFile.getAbsolutePath(), inputFile, true, null, null);

		Manifest manifest;
		try {
			Document doc = XmlUtil.read_xml(manifestFile);
			manifest = new Manifest(doc, manifestFile);
		}
		catch (LoadException | ParseException ex) {
			throw new RainbowError(ex.toString());
		}

		File mergeDir = new File(manifestDir, manifest.getMergeDir());
		mergeDir.mkdirs();
		for (String f : manifest.getTargetFiles()) {
			File tmpMergeDir = new File(tmpManifestDir, manifest.getMergeDir());
			File tmpTargetFile = new File(tmpMergeDir, f);
			File targetFile = new File(new File(manifestDir, manifest.getMergeDir()), f);
			targetFile.delete();
			Files.copy(tmpTargetFile.toPath(), targetFile.toPath());
			if (targetFile.getAbsoluteFile().exists() == false) {
				throw new RainbowError("Expected output file not found: " + targetFile);
			}
		}
		return new File(manifestDir, manifest.getMergeDir());
	}
}
