/*
 * Copyright (c) 2000, 2002, Oracle and/or its affiliates. All rights reserved.
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
import java.io.*;
import java.awt.datatransfer.*;
import java.util.ArrayList;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import util.Log;
import xliff_model.TaggedText;

/**
 * A transferable implementation for the default data transfer of some Swing
 * components.
 *
 * @author Timothy Prinzing
 */
class BasicTransferable implements Transferable, UIResource {

	protected TaggedText taggedText;
	protected String plainText;

	private static final DataFlavor taggedTextFlavor;
	private static final DataFlavor plainTextFlavor;
	private static DataFlavor[] flavors;

	static DataFlavor createTaggedTextFlavor() {
		try {
			return new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=xliff_model.TaggedText");
		}
		catch (ClassNotFoundException ex) {
			Log.err(ex);
		}
		return null;
	}

	static DataFlavor createPlainTextFlavor() {
		try {
			return new DataFlavor("text/plain;class=java.lang.String");
		}
		catch (ClassNotFoundException ex) {
			Log.err(ex);
		}
		return null;
	}

	static {
		ArrayList<DataFlavor> f = new ArrayList<>();
		taggedTextFlavor = createTaggedTextFlavor();
		if (taggedTextFlavor != null) {
			f.add(taggedTextFlavor);
		}
		plainTextFlavor = createPlainTextFlavor();
		if (plainTextFlavor != null) {
			f.add(plainTextFlavor);
		}
		flavors = f.toArray(new DataFlavor[f.size()]);
	}

	Position p0;
	Position p1;
	MarkupView markupView;

	public BasicTransferable(MarkupView markupView, int start, int end) {
		this.taggedText = markupView.getSelectedTaggedText();
		this.plainText = taggedText.getTextContent();
		this.markupView = markupView;
		try {
			p0 = markupView.getDocument().createPosition(start);
			p1 = markupView.getDocument().createPosition(end);
		}
		catch (BadLocationException ble) {
			System.err.println(ble);
		}
	}

	void removeText() {
		if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
			try {
				Document doc = markupView.getDocument();
				doc.remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
			}
			catch (BadLocationException ex) {
				Log.err(ex);
			}
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (DataFlavor f : flavors) {
			if (f.equals(flavor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (taggedTextFlavor != null && taggedTextFlavor.equals(flavor)) {
			return taggedText;
		}
		if (plainTextFlavor != null && plainTextFlavor.equals(flavor)) {
			return plainText;
		}
		throw new UnsupportedFlavorException(flavor);
	}
}
