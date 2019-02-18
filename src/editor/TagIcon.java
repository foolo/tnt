package editor;

import xliff_model.Tag;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;
import javax.swing.ImageIcon;

class TagIcon implements Icon {

	final Tag tag;
	ImageIcon icon;

	static ImageIcon leftBracketIcon = new ImageIcon(TagIcon.class.getClassLoader().getResource("images/left-bracket.png"));
	static ImageIcon rightBracketIcon = new ImageIcon(TagIcon.class.getClassLoader().getResource("images/right-bracket.png"));
	static ImageIcon singleBracketIcon = new ImageIcon(TagIcon.class.getClassLoader().getResource("images/single-bracket.png"));

	final ImageIcon tagTypeToIcon(Tag.Type type) {
		if (type == Tag.Type.START) {
			return leftBracketIcon;
		}
		else if (type == Tag.Type.END) {
			return rightBracketIcon;
		}
		else if (type == Tag.Type.EMPTY) {
			return singleBracketIcon;
		}
		return null;
	}

	public TagIcon(Tag tag) {
		this.tag = tag;
		icon = tagTypeToIcon(tag.getType());
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x, y + 4);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.white);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int draw_x = x + c.getX() + 1;
		int draw_y = y + c.getY() + g.getFontMetrics().getHeight() - 1;
		g.drawString("" + tag.getShortString(), draw_x, draw_y);
	}

	public Tag getTag() {
		return tag;
	}

	@Override
	public int getIconWidth() {
		return icon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return icon.getIconHeight();
	}
}
