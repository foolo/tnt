package rainbow;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import util.Log;
import static util.Log.getTimestamp;

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

	public void createPackage(ArrayList<String> inputFiles, String commonDir, String packageName) throws IOException {
		Log.debug("createPackage: input files: " + String.join(", ", inputFiles));
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
		// todo check return value
		cl.execute(tempDir, plnTmpFile.getPath(), inputFiles, false);
	}

	public void exportTranslatedFile(File manifestFile) throws IOException {
		String tempDir = Files.createTempDirectory("tnt_tmp_").toString();
		Log.debug("exportTranslatedFile: temporary directory: " + tempDir);
		File plnTmpFile = new File(tempDir, "export.pln");
		copy(getResource("/res/export.pln"), plnTmpFile.getAbsolutePath());
		copy(getResource("/res/languages.xml"), new File(tempDir, "languages.xml").getAbsolutePath());
		copy(getResource("/res/rainbowUtilities.xml"), new File(tempDir, "rainbowUtilities.xml").getAbsolutePath());
		CommandLine2 cl = new CommandLine2();
		ArrayList<String> inputFiles = new ArrayList<>();
		inputFiles.add(manifestFile.getAbsolutePath());
		// todo check return value
		cl.execute(tempDir, plnTmpFile.getAbsolutePath(), inputFiles, true);
	}
}