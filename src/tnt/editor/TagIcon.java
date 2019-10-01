package tnt.editor;

import tnt.xliff_model.Tag;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;

class TagIcon implements Icon {

	private final Tag tag;
	private int width;
	private int height;
	final MarkupView markupView;
	static final Color TAG_COLOR = new Color(154, 219, 147);

	public TagIcon(Tag tag, int size, MarkupView markupView) {
		this.tag = tag;
		setSize(size);
		this.markupView = markupView;
	}

	static void drawCenteredText(Graphics2D g2, String text, double center_x, double center_y) {
		Rectangle2D bounds = g2.getFontMetrics().getStringBounds(text, g2);
		double ascent = bounds.getHeight() * 0.64;
		int left_x = (int) (center_x - bounds.getWidth() / 2.0 + 0.5);
		int baseline_y = (int) (center_y + ascent / 2.0 + 0.3);
		g2.drawString(text, left_x, baseline_y);
	}

	static int getSelectionY(Graphics2D g2, int y) {
		int correction = (g2.getFont().getSize() < 14) ? 0 : 1;
		return y + correction;
	}

	void drawSelectionBackground(Graphics2D g2, int x, int y) {
		if (markupView.isSelected(this)) {
			g2.setColor(markupView.getSelectionColor());
			int line_height = g2.getFontMetrics().getHeight();
			g2.fillRect(x, y, width, line_height);
		}
	}

	void drawStartBracket(Graphics2D g2, int x, int y) {
		int line_height = g2.getFontMetrics().getHeight();
		int y2 = y + (line_height - height) / 2;
		Polygon p = new Polygon();
		int mid_x = x + height / 3;
		int mid_y = y2 + height / 2;
		p.addPoint(x, mid_y);
		p.addPoint(mid_x, y2);
		p.addPoint(x + width, y2);
		p.addPoint(x + width, y2 + height);
		p.addPoint(mid_x, y2 + height);
		g2.fillPolygon(p);
	}

	void drawEndBracket(Graphics2D g2, int x, int y) {
		int line_height = g2.getFontMetrics().getHeight();
		int y2 = y + (line_height - height) / 2;
		Polygon p = new Polygon();
		int mid_x = x + width - height / 3;
		int mid_y = y2 + height / 2;
		p.addPoint(x, y2);
		p.addPoint(mid_x, y2);
		p.addPoint(x + width, mid_y);
		p.addPoint(mid_x, y2 + height);
		p.addPoint(x, y2 + height);
		g2.fillPolygon(p);
	}

	void drawEmptyBracket(Graphics2D g2, int x, int y) {
		int line_height = g2.getFontMetrics().getHeight();
		int y2 = y + (line_height - height) / 2;
		Polygon p = new Polygon();
		int mid_x1 = x + height / 3;
		int mid_x2 = x + width - height / 3;
		int mid_y = y2 + height / 2;
		p.addPoint(x, mid_y);
		p.addPoint(mid_x1, y2);
		p.addPoint(mid_x2, y2);
		p.addPoint(x + width, mid_y);
		p.addPoint(mid_x2, y2 + height);
		p.addPoint(mid_x1, y2 + height);
		g2.fillPolygon(p);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Font previousFont = g.getFont();
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, g2.getFont().getSize()));
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int selection_y = getSelectionY(g2, y);
		drawSelectionBackground(g2, x, selection_y);

		g2.setColor(TAG_COLOR);
		double relative_center = 0.0;
		if (tag.getType() == Tag.Type.START) {
			drawStartBracket(g2, x, selection_y);
			relative_center = 0.07;
		}
		else if (tag.getType() == Tag.Type.END) {
			drawEndBracket(g2, x, selection_y);
		}
		else if (tag.getType() == Tag.Type.EMPTY) {
			drawEmptyBracket(g2, x, selection_y);
		}

		int line_height = g2.getFontMetrics().getHeight();
		double center_x = x + width * 0.5 + height * relative_center;
		double center_y = selection_y + line_height / 2.0;

		g2.setColor(Color.WHITE);
		g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, g2.getFont().getSize() - 3));
		drawCenteredText(g2, tag.getLabel(), center_x, center_y);
		g.setFont(previousFont);
	}

	public Tag getTag() {
		return tag;
	}

	public final void setSize(int size) {
		boolean hasLabel = tag.getType() == Tag.Type.START || tag.getType() == Tag.Type.EMPTY;
		if (hasLabel) {
			double padding = (tag.getType() == Tag.Type.EMPTY) ? 0.65 : 0.5;
			this.width = (int) (size * (tag.getLabel().length() * 0.68 + padding));
		}
		else {
			this.width = size;
		}
		this.height = size;
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}
}
