/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package javax.swing.plaf.basic;

import java.awt.datatransfer.*;
import java.awt.im.InputContext;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.UIResource;

class TextTransferHandler extends TransferHandler implements UIResource {

	private JTextComponent exportComp;
	private boolean shouldRemove;
	private int p0;
	private int p1;

	/**
	 * Whether or not this is a drop using <code>DropMode.INSERT</code>.
	 */
	private boolean modeBetween = false;

	/**
	 * Whether or not this is a drop.
	 */
	private boolean isDrop = false;

	/**
	 * The drop action.
	 */
	private int dropAction = MOVE;

	/**
	 * The drop bias.
	 */
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
		DataFlavor plainFlavor = null;
		DataFlavor refFlavor = null;
		DataFlavor stringFlavor = null;

		if (c instanceof JEditorPane) {
			for (int i = 0; i < flavors.length; i++) {
				String mime = flavors[i].getMimeType();
				if (mime.startsWith(((JEditorPane) c).getEditorKit().getContentType())) {
					return flavors[i];
				}
				else if (plainFlavor == null && mime.startsWith("text/plain")) {
					plainFlavor = flavors[i];
				}
				else if (refFlavor == null && mime.startsWith("application/x-java-jvm-local-objectref")
						&& flavors[i].getRepresentationClass() == java.lang.String.class) {
					refFlavor = flavors[i];
				}
				else if (stringFlavor == null && flavors[i].equals(DataFlavor.stringFlavor)) {
					stringFlavor = flavors[i];
				}
			}
			if (plainFlavor != null) {
				return plainFlavor;
			}
			else if (refFlavor != null) {
				return refFlavor;
			}
			else if (stringFlavor != null) {
				return stringFlavor;
			}
			return null;
		}

		for (int i = 0; i < flavors.length; i++) {
			String mime = flavors[i].getMimeType();
			if (mime.startsWith("text/plain")) {
				return flavors[i];
			}
			else if (refFlavor == null && mime.startsWith("application/x-java-jvm-local-objectref")
					&& flavors[i].getRepresentationClass() == java.lang.String.class) {
				refFlavor = flavors[i];
			}
			else if (stringFlavor == null && flavors[i].equals(DataFlavor.stringFlavor)) {
				stringFlavor = flavors[i];
			}
		}
		if (refFlavor != null) {
			return refFlavor;
		}
		else if (stringFlavor != null) {
			return stringFlavor;
		}
		return null;
	}

	/**
	 * Import the given stream data into the text component.
	 */
	protected void handleReaderImport(Reader in, JTextComponent c, boolean useRead)
			throws BadLocationException, IOException {
		if (useRead) {
			int startPosition = c.getSelectionStart();
			int endPosition = c.getSelectionEnd();
			int length = endPosition - startPosition;
			EditorKit kit = c.getUI().getEditorKit(c);
			Document doc = c.getDocument();
			if (length > 0) {
				doc.remove(startPosition, length);
			}
			kit.read(in, doc, startPosition);
		}
		else {
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
	public int getSourceActions(JComponent c) {
		if (c instanceof JPasswordField
				&& c.getClientProperty("JPasswordField.cutCopyAllowed")
				!= Boolean.TRUE) {
			return NONE;
		}

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
	protected void exportDone(JComponent source, Transferable data, int action) {
		// only remove the text if shouldRemove has not been set to
		// false by importData and only if the action is a move
		if (shouldRemove && action == MOVE) {
			TextTransferable t = (TextTransferable) data;
			t.removeText();
		}

		exportComp = null;
	}

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
				boolean useRead = false;
				if (comp instanceof JEditorPane) {
					JEditorPane ep = (JEditorPane) comp;
					if (!ep.getContentType().startsWith("text/plain")
							&& importFlavor.getMimeType().startsWith(ep.getContentType())) {
						useRead = true;
					}
				}
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

				handleReaderImport(r, c, useRead);

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
			catch (UnsupportedFlavorException ufe) {
			}
			catch (BadLocationException ble) {
			}
			catch (IOException ioe) {
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

			this.c = c;

			Document doc = c.getDocument();

			try {
				p0 = doc.createPosition(start);
				p1 = doc.createPosition(end);

				plainData = c.getSelectedText();

				if (c instanceof JEditorPane) {
					JEditorPane ep = (JEditorPane) c;

					mimeType = ep.getContentType();

					if (mimeType.startsWith("text/plain")) {
						return;
					}

					StringWriter sw = new StringWriter(p1.getOffset() - p0.getOffset());
					ep.getEditorKit().write(sw, doc, p0.getOffset(), p1.getOffset() - p0.getOffset());

					if (mimeType.startsWith("text/html")) {
						htmlData = sw.toString();
					}
					else {
						richText = sw.toString();
					}
				}
			}
			catch (BadLocationException ble) {
			}
			catch (IOException ioe) {
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

		// ---- EditorKit other than plain or HTML text -----------------------
		/**
		 * If the EditorKit is not for text/plain or text/html, that format is
		 * supported through the "richer flavors" part of BasicTransferable.
		 */
		protected DataFlavor[] getRicherFlavors() {
			if (richText == null) {
				return null;
			}

			try {
				DataFlavor[] flavors = new DataFlavor[3];
				flavors[0] = new DataFlavor(mimeType + ";class=java.lang.String");
				flavors[1] = new DataFlavor(mimeType + ";class=java.io.Reader");
				flavors[2] = new DataFlavor(mimeType + ";class=java.io.InputStream;charset=unicode");
				return flavors;
			}
			catch (ClassNotFoundException cle) {
				// fall through to unsupported (should not happen)
			}

			return null;
		}

		/**
		 * The only richer format supported is the file list flavor
		 */
		protected Object getRicherData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (richText == null) {
				return null;
			}

			if (String.class.equals(flavor.getRepresentationClass())) {
				return richText;
			}
			else if (Reader.class.equals(flavor.getRepresentationClass())) {
				return new StringReader(richText);
			}
			else if (InputStream.class.equals(flavor.getRepresentationClass())) {
				return new StringBufferInputStream(richText);
			}
			throw new UnsupportedFlavorException(flavor);
		}

		Position p0;
		Position p1;
		String mimeType;
		String richText;
		JTextComponent c;
	}

}
