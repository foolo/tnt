package conversion;

import xliff_model.ValidationError;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;
import net.sf.okapi.lib.xliff2.reader.XLIFFReaderException;
import util.Log;
import xliff_model.ValidationPath;
import xliff_model.exceptions.ParseException;

public class RainbowXliffValidator {

	private static final Pattern FILE_UNIT_PATTERN = Pattern.compile(".*Error in <file> id='([^<&']*)', <unit> id='([^<&']*)'.*");
	private static final Pattern CODE_PATTERN = Pattern.compile(".*Code id='([^<&']*)' (.*)");

	static ValidationError getValidationError(String errMsg) throws ParseException {
		System.out.println("DATA" + errMsg);
		String[] lines = errMsg.split("\n");
		if (lines.length < 2) {
			Log.err("getValidationError: lines.length < 2");
			return new ValidationError(errMsg, null);
		}
		Matcher m = FILE_UNIT_PATTERN.matcher(lines[0]);
		if (m.find() == false) {
			Log.err("getValidationError: file/unit/code id not found");
			return new ValidationError(errMsg, null);
		}
		if (m.groupCount() != 2) {
			Log.err("getValidationError: unexpected group count: " + m.groupCount());
			return new ValidationError(errMsg, null);
		}

		String fileId = m.group(1);
		String unitId = m.group(2);

		String segmentId = "";
		String codeId = "";
		String lastLine = lines[lines.length - 1];
		String message = lastLine;

		m = CODE_PATTERN.matcher(message);
		if (m.find() && m.groupCount() == 2) {
			codeId = m.group(1);
			message = m.group(2);
		}
		return new ValidationError(message, new ValidationPath(fileId, unitId, segmentId, codeId));
	}

	public static ArrayList<ValidationError> validate(String data) throws ParseException {
		ArrayList<ValidationError> res = new ArrayList<>();
		XLIFFReader reader = new XLIFFReader();
		reader.open(data);
		while (reader.hasNext()) {
			try {
				reader.next();
			}
			catch (XLIFFReaderException ex) {
				res.add(getValidationError(ex.getMessage()));
			}
		}
		return res;
	}
}
