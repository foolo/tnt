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
package editor.javax.swing.plaf.basic;

import editor.MarkupView;
import editor.Session;
import java.awt.datatransfer.*;
import java.awt.im.InputContext;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.UIResource;
import xliff_model.TaggedText;

public class TextTransferHandler extends TransferHandler implements UIResource {

	private MarkupView markupView;
	private boolean shouldRemove;
	private boolean isDrop = false;
	private Position.Bias dropBias;

	protected DataFlavor getImportFlavor(DataFlavor[] flavors, String preferredContentType) {
		DataFlavor plainFlavor = null;
		DataFlavor refFlavor = null;
		DataFlavor stringFlavor = null;
		for (DataFlavor flavor : flavors) {
			String mime = flavor.getMimeType();
			if (mime.startsWith(DataFlavor.javaJVMLocalObjectMimeType) && flavor.getRepresentationClass() == xliff_model.TaggedText.class) {
				return flavor;
			}
			else if (mime.startsWith(preferredContentType)) {
				return flavor;
			}
			else if (plainFlavor == null && mime.startsWith("text/plain")) {
				plainFlavor = flavor;
			}
			else if (refFlavor == null && mime.startsWith("application/x-java-jvm-local-objectref") && flavor.getRepresentationClass() == java.lang.String.class) {
				refFlavor = flavor;
			}
			else if (stringFlavor == null && flavor.equals(DataFlavor.stringFlavor)) {
				stringFlavor = flavor;
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

	/**
	 * Import the given stream data into the text component.
	 */
	protected void handleReaderImport(Reader in, JTextComponent c) throws BadLocationException, IOException {
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
	@Override
	public int getSourceActions(JComponent c) {
		if (c instanceof JPasswordField
				&& c.getClientProperty("JPasswordField.cutCopyAllowed")
				!= Boolean.TRUE) {
			return NONE;
		}

		return ((JTextComponent) c).isEditable() ? COPY_OR_MOVE : COPY;
	}

	@Override
	protected Transferable createTransferable(JComponent comp) {
		markupView = (MarkupView) comp;
		shouldRemove = true;
		int p0 = markupView.getSelectionStart();
		int p1 = markupView.getSelectionEnd();
		if (p0 == p1) {
			return null;
		}
		return new BasicTransferable(markupView, p0, p1);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		// only remove the text if shouldRemove has not been set to
		// false by importData and only if the action is a move
		if (shouldRemove && action == MOVE) {
			BasicTransferable t = (BasicTransferable) data;
			t.removeText();
		}

		markupView = null;
	}

	@Override
	public boolean importData(TransferSupport support) {
		isDrop = support.isDrop();
		if (isDrop) {
			dropBias = ((JTextComponent.DropLocation) support.getDropLocation()).getBias();
		}
		try {
			return super.importData(support);
		}
		finally {
			isDrop = false;
			dropBias = null;
		}
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {

		MarkupView c = (MarkupView) comp;
		DataFlavor importFlavor = getImportFlavor(t.getTransferDataFlavors(), c.getEditorKit().getContentType());
		if (importFlavor == null) {
			return false;
		}

		Object data;
		try {
			data = t.getTransferData(importFlavor);
		}
		catch (UnsupportedFlavorException | IOException ex) {
			System.err.println(ex);
			return false;
		}

		if (data instanceof TaggedText) {
			Session.getUndoManager().markSnapshot();
			TaggedText tt = (TaggedText) data;
			c.insertTaggedText(tt.copy());
			Session.getUndoManager().markSnapshot();
			return true;
		}

		boolean imported = false;
		try {
			InputContext ic = c.getInputContext();
			if (ic != null) {
				ic.endComposition();
			}
			Reader r = importFlavor.getReaderForText(t);

			handleReaderImport(r, c);

			if (isDrop) {
				int pos = c.getCaretPosition();
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
		return imported;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] flavors) {
		JEditorPane c = (JEditorPane) comp;
		if (!(c.isEditable() && c.isEnabled())) {
			return false;
		}
		return (getImportFlavor(flavors, c.getEditorKit().getContentType()) != null);
	}
}
