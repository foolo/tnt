package editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.im.InputContext;
import java.io.IOException;
import java.io.Reader;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.TransferHandler;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import util.Log;
import xliff_model.TaggedText;

public class TaggedTextTransferHandler extends TransferHandler implements UIResource {

	protected DataFlavor getImportFlavor(DataFlavor[] flavors, String preferredContentType) {
		for (DataFlavor flavor : flavors) {
			String mime = flavor.getMimeType();
			if (mime.startsWith(DataFlavor.javaJVMLocalObjectMimeType) && flavor.getRepresentationClass() == xliff_model.TaggedText.class) {
				return flavor;
			}
			else if (mime.startsWith(preferredContentType)) {
				return flavor;
			}
		}
		return null;
	}

	protected void handleReaderImport(Reader in, JTextComponent c) throws BadLocationException, IOException {
		char[] buf = new char[1024];
		StringBuilder sb = new StringBuilder();
		int bytesRead;
		while ((bytesRead = in.read(buf, 0, buf.length)) != -1) {
			sb.append(buf, 0, bytesRead);
		}
		c.replaceSelection(sb.toString());
	}

	@Override
	public int getSourceActions(JComponent c) {
		return ((JTextComponent) c).isEditable() ? TransferHandler.COPY_OR_MOVE : TransferHandler.COPY;
	}

	@Override
	protected Transferable createTransferable(JComponent comp) {
		MarkupView markupView = (MarkupView) comp;
		int p0 = markupView.getSelectionStart();
		int p1 = markupView.getSelectionEnd();
		if (p0 == p1) {
			return null;
		}
		return new TaggedTextTransferable(markupView, p0, p1);
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action == MOVE) {
			TaggedTextTransferable t = (TaggedTextTransferable) data;
			t.removeText();
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
			Log.err(ex);
			return false;
		}

		if (data instanceof TaggedText) {
			Session.getUndoManager().markSnapshot();
			TaggedText tt = (TaggedText) data;
			c.insertTaggedText(tt.copy());
			Session.getUndoManager().markSnapshot();
			return true;
		}

		try {
			InputContext ic = c.getInputContext();
			if (ic != null) {
				ic.endComposition();
			}
			Reader r = importFlavor.getReaderForText(t);
			handleReaderImport(r, c);
			c.requestFocus();
			return true;
		}
		catch (UnsupportedFlavorException | BadLocationException | IOException ex) {
			Log.err(ex);
		}
		return false;
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
