package editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.plaf.basic.BasicTextPaneUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

// https://stackoverflow.com/questions/12072171/how-do-i-set-different-colors-for-text-and-underline-in-jtextpane/12077917#12077917
public class UnderlinerEditorKit extends StyledEditorKit {

	public static class UnderlinedAttribute {

		public final boolean isUnderlined;
		public final Color color;

		public UnderlinedAttribute(boolean isUnderlined, Color color) {
			this.isUnderlined = isUnderlined;
			this.color = color;
		}
	}

	public static final String UNDERLINE_COLOR_ATTRIBUTE = "ul_attr";

	@Override
	public ViewFactory getViewFactory() {
		return new CustomUI();
	}

	public static class CustomUI extends BasicTextPaneUI {

		@Override
		public View create(Element elem) {
			View result = null;
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					result = new MyLabelView(elem);
				}
				else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					result = new ParagraphView(elem);
				}
				else if (kind.equals(AbstractDocument.SectionElementName)) {
					result = new BoxView(elem, View.Y_AXIS);
				}
				else if (kind.equals(StyleConstants.ComponentElementName)) {
					result = new ComponentView(elem);
				}
				else if (kind.equals(StyleConstants.IconElementName)) {
					result = new IconView(elem);
				}
				else {
					result = new LabelView(elem);
				}
			}
			else {
				result = super.create(elem);
			}
			return result;
		}
	}

	public static class MyLabelView extends LabelView {

		public MyLabelView(Element arg0) {
			super(arg0);
		}

		@Override
		public void paint(Graphics g, Shape a) {
			super.paint(g, a);
			UnderlinedAttribute attr = (UnderlinedAttribute) getElement().getAttributes().getAttribute(UNDERLINE_COLOR_ATTRIBUTE);
			if ((attr == null) || (attr.isUnderlined == false) || (attr.color == null)) {
				return;
			}
			int y = a.getBounds().y + (int) getGlyphPainter().getAscent(this);
			int x1 = a.getBounds().x;
			int x2 = a.getBounds().width + x1;
			g.setColor(attr.color);
			g.drawLine(x1, y, x2, y);
		}
	}
}
