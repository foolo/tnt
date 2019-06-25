package rainbow;

import java.util.ArrayList;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;

public class XliffFileValidator {

	public static class ValidationError {

		String message;

		public ValidationError(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return message;
		}
	}

	public static ArrayList<ValidationError> validate(String data) {
		ArrayList<ValidationError> res = new ArrayList<>();
		XLIFFReader reader = new XLIFFReader();
		reader.open(data);
		while (reader.hasNext()) {
			try {
				reader.next();
			}
			catch (Throwable t) {
				res.add(new ValidationError(t.toString()));
			}
		}
		return res;
	}
}
