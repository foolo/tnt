package util;

import java.io.OutputStream;

public class LogOutputStream extends OutputStream {

	private final int lineLength;
	private final byte buf[];
	private int count = 0;

	String prefix;

	public LogOutputStream(String prefix, int lineWidth) {
		this.prefix = prefix;
		this.lineLength = lineWidth;
		buf = new byte[lineWidth];
	}

	@Override
	public String toString() {
		return new String(buf, 0, count);
	}

	@Override
	public synchronized void write(int c) {
		if (c == '\n' || count >= lineLength) {
			Log.debug(prefix + toString());
			count = 0;
			return;
		}
		buf[count] = (byte) c;
		count++;
	}
}
