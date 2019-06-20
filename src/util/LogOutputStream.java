package util;

import java.io.OutputStream;

public class LogOutputStream extends OutputStream {

	private static final int BUFSIZE = 100;
	byte buf[] = new byte[BUFSIZE];
	int count = 0;

	String prefix;

	public LogOutputStream(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String toString() {
		return new String(buf, 0, count);
	}

	@Override
	public synchronized void write(int c) {
		if (c == '\n' || count >= BUFSIZE) {
			Log.debug(prefix + toString());
			count = 0;
			return;
		}
		buf[count] = (byte) c;
		count++;
	}
}
