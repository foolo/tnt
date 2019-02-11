package xliff_model;

public class InvalidXliffFormatException extends Exception {

	public InvalidXliffFormatException() {
		super("Could not decode XLIFF file");
	}

	InvalidXliffFormatException(String msg) {
		super(msg);
	}


}
