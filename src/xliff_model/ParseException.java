package xliff_model;

public class ParseException extends Exception {

	public ParseException() {
		super("Could not decode XLIFF file");
	}

	public ParseException(String msg) {
		super(msg);
	}
}
