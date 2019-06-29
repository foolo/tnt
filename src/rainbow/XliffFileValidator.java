package rainbow;

import java.util.ArrayList;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;
import net.sf.okapi.lib.xliff2.reader.XLIFFReaderException;
import xliff_model.exceptions.ParseException;

public class XliffFileValidator {

	public static ArrayList<ValidationError> validate(String data) throws ParseException {
		ArrayList<ValidationError> res = new ArrayList<>();
		XLIFFReader reader = new XLIFFReader();
		reader.open(data);
		while (reader.hasNext()) {
			try {
				reader.next();
			}
			catch (XLIFFReaderException ex) {
				res.add(new ValidationError(ex.getMessage()));
			}
		}
		return res;
	}
}
