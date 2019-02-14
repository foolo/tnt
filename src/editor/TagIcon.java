package editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.ImageIcon;

class TagIcon extends ImageIcon {

	final Tag tag;

	public TagIcon(int tagIndex, Tag tag, URL imageUrl) {
		super(imageUrl);
		this.tag = tag;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		super.paintIcon(c, g, x, y + 4);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.white);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int draw_x = x + c.getX() + 3;
		int draw_y = y + c.getY() + g.getFontMetrics().getHeight() + 1;
		g.drawString("" + tag.getIndex(), draw_x, draw_y);
	}

	public Tag getTag() {
		return tag;
	}

}
