package editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.im.InputContext;
import java.io.IOException;
import java.io.Reader;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

// modified version of javax.swing.plaf.basic.TextTransferHandler

class TextTransferHandler extends TransferHandler implements UIResource {

	private JTextComponent exportComp;
	private boolean shouldRemove;
	private int p0;
	private int p1;

	private boolean modeBetween = false; //Whether or not this is a drop using DropMode.INSERT
	private boolean isDrop = false;
	private int dropAction = MOVE;
	private Position.Bias dropBias;

	/**
	 * Try to find a flavor that can be used to import a Transferable. The set
	 * of usable flavors are tried in the following order:
	 * <ol>
	 * <li>First, an attempt is made to find a flavor matching the content type
	 * of the EditorKit for the component.
	 * <li>Second, an attempt to find a text/plain flavor is made.
	 * <li>Third, an attempt to find a flavor representing a String reference in
	 * the same VM is made.
	 * <li>Lastly, DataFlavor.stringFlavor is searched for.
	 * </ol>
	 */
	protected DataFlavor getImportFlavor(DataFlavor[] flavors, JTextComponent c) {
		for (DataFlavor flavor : flavors) {
			if (flavor.getMimeType().startsWith("text/plain")) {
				return flavor;
			}
		}
		return null;
	}

	/**
	 * Import the given stream data into the text component.
	 */
	protected void handleReaderImport(Reader in, JTextComponent c)
			throws BadLocationException, IOException {

		char[] buff = new char[1024];
		int nch;
		boolean lastWasCR = false;
		int last;
		StringBuffer sbuff = null;

		// Read in a block at a time, mapping \r\n to \n, as well as single
		// \r to \n.
		while ((nch = in.read(buff, 0, buff.length)) != -1) {
			if (sbuff == null) {
				sbuff = new StringBuffer(nch);
			}
			last = 0;
			for (int counter = 0; counter < nch; counter++) {
				switch (buff[counter]) {
					case '\r':
						if (lastWasCR) {
							if (counter == 0) {
								sbuff.append('\n');
							}
							else {
								buff[counter - 1] = '\n';
							}
						}
						else {
							lastWasCR = true;
						}
						break;
					case '\n':
						if (lastWasCR) {
							if (counter > (last + 1)) {
								sbuff.append(buff, last, counter - last - 1);
							}
							// else nothing to do, can skip \r, next write will
							// write \n
							lastWasCR = false;
							last = counter;
						}
						break;
					default:
						if (lastWasCR) {
							if (counter == 0) {
								sbuff.append('\n');
							}
							else {
								buff[counter - 1] = '\n';
							}
							lastWasCR = false;
						}
						break;
				}
			}
			if (last < nch) {
				if (lastWasCR) {
					if (last < (nch - 1)) {
						sbuff.append(buff, last, nch - last - 1);
					}
				}
				else {
					sbuff.append(buff, last, nch - last);
				}
			}
		}
		if (lastWasCR) {
			sbuff.append('\n');
		}
		c.replaceSelection(sbuff != null ? sbuff.toString() : "");
	}

	// --- TransferHandler methods ------------------------------------
	/**
	 * This is the type of transfer actions supported by the source. Some models
	 * are not mutable, so a transfer operation of COPY only should be
	 * advertised in that case.
	 *
	 * @param c The component holding the data to be transfered. This argument
	 * is provided to enable sharing of TransferHandlers by multiple components.
	 * @return This is implemented to return NONE if the component is a
	 * JPasswordField since exporting data via user gestures is not allowed. If
	 * the text component is editable, COPY_OR_MOVE is returned, otherwise just
	 * COPY is allowed.
	 */
	@Override
	public int getSourceActions(JComponent c) {
		return ((JTextComponent) c).isEditable() ? COPY_OR_MOVE : COPY;
	}

	/**
	 * Create a Transferable to use as the source for a data transfer.
	 *
	 * @param comp The component holding the data to be transfered. This
	 * argument is provided to enable sharing of TransferHandlers by multiple
	 * components.
	 * @return The representation of the data to be transfered.
	 *
	 */
	@Override
	protected Transferable createTransferable(JComponent comp) {
		exportComp = (JTextComponent) comp;
		shouldRemove = true;
		p0 = exportComp.getSelectionStart();
		p1 = exportComp.getSelectionEnd();
		return (p0 != p1) ? (new TextTransferable(exportComp, p0, p1)) : null;
	}

	/**
	 * This method is called after data has been exported. This method should
	 * remove the data that was transfered if the action was MOVE.
	 *
	 * @param source The component that was the source of the data.
	 * @param data The data that was transferred or possibly null if the action
	 * is <code>NONE</code>.
	 * @param action The actual action that was performed.
	 */
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		// only remove the text if shouldRemove has not been set to
		// false by importData and only if the action is a move
		if (shouldRemove && action == MOVE) {
			TextTransferable t = (TextTransferable) data;
			t.removeText();
		}

		exportComp = null;
	}

	@Override
	public boolean importData(TransferSupport support) {
		isDrop = support.isDrop();

		if (isDrop) {
			modeBetween
					= ((JTextComponent) support.getComponent()).getDropMode() == DropMode.INSERT;

			dropBias = ((JTextComponent.DropLocation) support.getDropLocation()).getBias();

			dropAction = support.getDropAction();
		}

		try {
			return super.importData(support);
		}
		finally {
			isDrop = false;
			modeBetween = false;
			dropBias = null;
			dropAction = MOVE;
		}
	}

	/**
	 * This method causes a transfer to a component from a clipboard or a DND
	 * drop operation. The Transferable represents the data to be imported into
	 * the component.
	 *
	 * @param comp The component to receive the transfer. This argument is
	 * provided to enable sharing of TransferHandlers by multiple components.
	 * @param t The data to import
	 * @return true if the data was inserted into the component, false
	 * otherwise.
	 */
	@Override
	public boolean importData(JComponent comp, Transferable t) {
		JTextComponent c = (JTextComponent) comp;

		int pos = modeBetween
				? c.getDropLocation().getIndex() : c.getCaretPosition();

		// if we are importing to the same component that we exported from
		// then don't actually do anything if the drop location is inside
		// the drag location and set shouldRemove to false so that exportDone
		// knows not to remove any data
		if (dropAction == MOVE && c == exportComp && pos >= p0 && pos <= p1) {
			shouldRemove = false;
			return true;
		}

		boolean imported = false;
		DataFlavor importFlavor = getImportFlavor(t.getTransferDataFlavors(), c);
		if (importFlavor != null) {
			try {
				InputContext ic = c.getInputContext();
				if (ic != null) {
					ic.endComposition();
				}
				Reader r = importFlavor.getReaderForText(t);

				if (modeBetween) {
					Caret caret = c.getCaret();
					if (caret instanceof DefaultCaret) {
						((DefaultCaret) caret).setDot(pos, dropBias);
					}
					else {
						c.setCaretPosition(pos);
					}
				}

				handleReaderImport(r, c);

				if (isDrop) {
					c.requestFocus();
					Caret caret = c.getCaret();
					if (caret instanceof DefaultCaret) {
						int newPos = caret.getDot();
						Position.Bias newBias = ((DefaultCaret) caret).getDotBias();

						((DefaultCaret) caret).setDot(pos, dropBias);
						((DefaultCaret) caret).moveDot(newPos, newBias);
					}
					else {
						c.select(pos, c.getCaretPosition());
					}
				}

				imported = true;
			}
			catch (UnsupportedFlavorException | BadLocationException | IOException ex) {
				System.err.println(ex);
			}
		}
		return imported;
	}

	/**
	 * This method indicates if a component would accept an import of the given
	 * set of data flavors prior to actually attempting to import it.
	 *
	 * @param comp The component to receive the transfer. This argument is
	 * provided to enable sharing of TransferHandlers by multiple components.
	 * @param flavors The data formats available
	 * @return true if the data can be inserted into the component, false
	 * otherwise.
	 */
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] flavors) {
		JTextComponent c = (JTextComponent) comp;
		if (!(c.isEditable() && c.isEnabled())) {
			return false;
		}
		return (getImportFlavor(flavors, c) != null);
	}

	/**
	 * A possible implementation of the Transferable interface for text
	 * components. For a JEditorPane with a rich set of EditorKit
	 * implementations, conversions could be made giving a wider set of formats.
	 * This is implemented to offer up only the active content type and
	 * text/plain (if that is not the active format) since that can be extracted
	 * from other formats.
	 */
	static class TextTransferable extends BasicTransferable {

		TextTransferable(JTextComponent c, int start, int end) {
			super(null, null);

			MarkupView mv = (MarkupView)c;
			
			this.c = c;

			Document doc = c.getDocument();

			try {
				p0 = doc.createPosition(start);
				p1 = doc.createPosition(end);

				//plainData = c.getSelectedText();
				plainData = mv.getSelectedContent();
				System.out.println("PLAIN DATA: " + plainData);
			}
			catch (BadLocationException ble) {
			}
		}

		void removeText() {
			if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
				try {
					Document doc = c.getDocument();
					doc.remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
				}
				catch (BadLocationException e) {
				}
			}
		}

		Position p0;
		Position p1;
		JTextComponent c;
	}
}
