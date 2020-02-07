package tnt.editor.util;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class SelectableOptionPane {

	public static void show(Component parentComponent, String title, String message, int messageType) {
		JTextArea textArea = new JTextArea(message);
		int lineWidth = textArea.getPreferredSize().width;
		int width = Math.min(lineWidth, 400);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setSize(new Dimension(width, 10));
		JOptionPane.showMessageDialog(null, textArea, title, messageType);
	}
}
