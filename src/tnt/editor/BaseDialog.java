package tnt.editor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public abstract class BaseDialog extends javax.swing.JDialog {

	public static final String WINDOW_CLOSING_ACTION_KEY = "window_closing";

	public final void installEscapeCloseOperation() {
		Action dispatchClosing = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				close();
			}
		};
		JRootPane root = getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), WINDOW_CLOSING_ACTION_KEY);
		root.getActionMap().put(WINDOW_CLOSING_ACTION_KEY, dispatchClosing);
	}

	protected boolean result = false;

	public BaseDialog(java.awt.Frame parent) {
		super(parent, true);
		installEscapeCloseOperation();
	}

	boolean getResult() {
		return result;
	}

	final void initButtons(JButton okButton, JButton cancelButton) {
		getRootPane().setDefaultButton(okButton);
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				close();
			}
		});
	}

	void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
