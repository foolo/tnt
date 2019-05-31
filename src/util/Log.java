package util;

import org.w3c.dom.Node;

public class Log {

	public static void err(String msg) {
		System.out.println("ERROR: " + msg);
	}

	public static void debug(String msg) {
		System.out.println(msg);
	}

	public static void debug(String msg, Node location) {
		System.out.println(msg + " (at " + XmlUtil.getPath(location) + ")");
	}
}
