package tnt.editor.util;

import java.awt.Color;

// https://stackoverflow.com/questions/12072171/how-do-i-set-different-colors-for-text-and-underline-in-jtextpane/12077917#12077917
public class UnderlinerEditorKit {

	public static class UnderlinedAttribute {

		public final boolean isUnderlined;
		public final Color color;

		public UnderlinedAttribute(boolean isUnderlined, Color color) {
			this.isUnderlined = isUnderlined;
			this.color = color;
		}
	}

	public static final String UNDERLINE_COLOR_ATTRIBUTE = "ul_attr";
}
