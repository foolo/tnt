package rainbow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import xliff_model.exceptions.ParseException;

public class ValidationError {

	private final String fileId;
	private final String unitId;
	private final String codeId;
	private final String details;
	private final Pattern pattern = Pattern.compile(".*Error in <file> id='([^<&']*)', <unit> id='([^<&']*)'.*Code id='([^<&']*)' (.*)", Pattern.DOTALL);

	public ValidationError(String message) throws ParseException {
		System.out.println("DATA" + message);
		Matcher m = pattern.matcher(message);
		if (m.find() == false) {
			throw new ParseException("file/unit/code id not found");
		}
		if (m.groupCount() != 4) {
			throw new ParseException("unexpected group count: " + m.groupCount());
		}
		fileId = m.group(1);
		unitId = m.group(2);
		codeId = m.group(3);
		details = m.group(4);
	}

	public String getFileId() {
		return fileId;
	}

	public String getUnitId() {
		return unitId;
	}

	public String getCodeId() {
		return codeId;
	}

	public String getDetails() {
		return details;
	}

	@Override
	public String toString() {
		return "file=" + fileId + ", unit=" + unitId + ", tag=" + codeId + ", details: " + details;
	}
}
