package tnt.editor.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import tnt.editor.MarkupView;
import tnt.util.Log;
import tnt.xliff_model.TaggedText;

class TaggedTextTransferable implements Transferable, UIResource {

	protected TaggedText taggedText;
	protected String plainText;

	private static final DataFlavor taggedTextFlavor;
	private static final DataFlavor plainTextFlavor;
	private static DataFlavor[] flavors;

	static DataFlavor createFlavor(String mimeType) {
		try {
			return new DataFlavor(mimeType);
		}
		catch (ClassNotFoundException ex) {
			Log.err(ex);
		}
		return null;
	}

	static {
		ArrayList<DataFlavor> f = new ArrayList<>();
		taggedTextFlavor = createFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + TaggedText.class.getCanonicalName());
		if (taggedTextFlavor != null) {
			f.add(taggedTextFlavor);
		}
		plainTextFlavor = createFlavor("text/plain;class=java.lang.String");
		if (plainTextFlavor != null) {
			f.add(plainTextFlavor);
		}
		flavors = f.toArray(new DataFlavor[f.size()]);
	}

	Position p0;
	Position p1;
	MarkupView markupView;

	public TaggedTextTransferable(MarkupView markupView, int start, int end) {
		this.taggedText = markupView.getSelectedTaggedText();
		this.plainText = taggedText.getTextContent();
		this.markupView = markupView;
		try {
			p0 = markupView.getDocument().createPosition(start);
			p1 = markupView.getDocument().createPosition(end);
		}
		catch (BadLocationException ex) {
			Log.err(ex);
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
