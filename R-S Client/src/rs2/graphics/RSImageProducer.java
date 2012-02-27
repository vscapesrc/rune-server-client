package rs2.graphics;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

import rs2.Client;

public final class RSImageProducer {

	public RSImageProducer(int width, int height, Component component) {
		this.width = width;
		this.height = height;
		this.component = component;
		int count = width * height;
		pixels = new int[count];
		image = new BufferedImage(COLOR_MODEL, Raster.createWritableRaster(COLOR_MODEL.createCompatibleSampleModel(width, height), new DataBufferInt(pixels, count), null), false, new Hashtable<Object, Object>());
		initDrawingArea();
	}

	public void drawGraphics(int x, int y, Graphics gfx) {
		if (Client.getClient().isApplet) {
			if (Client.getClient().isFixed()) {
				gfx.setColor(Color.BLACK);
				int spaceX = (Client.getClient().appletWidth - 765) / 2;
				int spaceY = (Client.getClient().appletHeight - 503);
				gfx.fillRect(0, 0, spaceX + 1, Client.getClient().appletHeight);
				gfx.fillRect(spaceX + 1, 503, (Client.getClient().appletWidth - (spaceX * 2)), spaceY);
				gfx.fillRect(spaceX + Client.getClient().clientWidth + 1, 0, spaceX, Client.getClient().appletHeight);
			}
			draw(x + (Client.getClient().appletWidth / 2) - (Client.getClient().clientWidth / 2), y, gfx);
		} else {
			draw(x, y, gfx);
		}
	}

	public void draw(int x, int y, Graphics gfx) {
		gfx.drawImage(image, x, y, component);
	}

	public void draw(Graphics gfx, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight) {
		Shape tmp = gfx.getClip();
		try {
			clip.x = clipX;
			clip.y = clipY;
			clip.width = clipWidth;
			clip.height = clipHeight;
			gfx.setClip(clip);
			gfx.drawImage(image, x, y, component);
		} finally {
			gfx.setClip(tmp);
		}
	}

	public void initDrawingArea() {
		RSDrawingArea.initDrawingArea(width, height, pixels);
	}

	public final int[] pixels;
	public final int width;
	public final int height;
	public final BufferedImage image;
	public final Component component;
	private final Rectangle clip = new Rectangle();
	private static final ColorModel COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
}