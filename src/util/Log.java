package util;

import org.w3c.dom.Node;

public class Log {

	public interface LogListener {

		void log(String msg);
	}

	static LogListener logListener;

	public static void set_log_listener(LogListener logListener) {
		Log.logListener = logListener;
	}

	public static void err(String msg) {
		logListener.log("ERROR: " + msg);
	}

	public static void debug(String msg, Node location) {
		System.out.println(msg + " (at " + XmlUtil.getPath(location) + ")");
	}
}
