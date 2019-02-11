package util;

import javax.swing.JOptionPane;

public class MessageBox {

	public static void error(String s) {
		JOptionPane.showMessageDialog(null, s);
	}

	public static void error_unexpected_tag(String s, String exp) {
		JOptionPane.showMessageDialog(null, "Unexpected tag: " + s + " (expected: " + exp + ")");
	}
}
