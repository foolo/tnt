package tnt.editor;

import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.Utilities;
import tnt.util.Log;

class SelectWordCaret extends DefaultCaret {
	private boolean wordSelectingMode = false;
	private int p0;
	private int p1;

	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		int nclicks = e.getClickCount();
		if (SwingUtilities.isLeftMouseButton(e) && !e.isConsumed() && nclicks == 2) {
			p0 = Math.min(getDot(), getMark());
			p1 = Math.max(getDot(), getMark());
			wordSelectingMode = true;
		}
		else {
			wordSelectingMode = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (wordSelectingMode && !e.isConsumed() && SwingUtilities.isLeftMouseButton(e)) {
			continuouslySelectWords(e);
		}
		else {
			super.mouseDragged(e);
		}
	}

	private void continuouslySelectWords(MouseEvent e) {
		Position.Bias[] biasRet = new Position.Bias[1];
		JTextComponent c = getComponent();
		int pos = c.getUI().viewToModel2D(c, e.getPoint(), biasRet);
		if (biasRet[0] == null) {
			biasRet[0] = Position.Bias.Forward;
		}
		try {
			if (pos >= p0 && pos <= p1) {
				setDot(p0);
				moveDot(p1, biasRet[0]);
			}
			else if (pos > p1) {
				setDot(p0);
				moveDot(Utilities.getWordEnd(c, pos - 1), biasRet[0]);
			}
			else if (pos < p0) {
				setDot(p1);
				moveDot(Utilities.getWordStart(c, pos), biasRet[0]);
			}
		}
		catch (BadLocationException ex) {
			Log.err(ex);
		}
	}
}
