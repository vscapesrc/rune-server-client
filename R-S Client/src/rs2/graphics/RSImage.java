package rs2.graphics;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import javax.swing.ImageIcon;

import rs2.JagexBuffer;
import rs2.cache.JagexArchive;
import rs2.sign.signlink;

public final class RSImage extends RSDrawingArea {

	public RSImage(int width, int height) {
		myPixels = new int[width * height];
		myWidth = maxWidth = width;
		myHeight = maxHeight = height;
		offsetX = offsetY = 0;
	}

	/**
	 * Sets pixels with the specified RGB values to have an opacity level of 0.
	 * @param red
	 * @param green
	 * @param blue
	 */
	public void setTransparency(int red, int green, int blue) {
		for (int index = 0; index < myPixels.length; index++){
			if (((myPixels[index] >> 16) & 255) == red && ((myPixels[index] >> 8) & 255) == green && (myPixels[index] & 255) == blue) {
				myPixels[index] = 0;
			}
		}
	}

	public String location = signlink.getDirectory() + "rsimg" + System.getProperty("file.separator");

	/**
	 * Creates a sub-image from an external file.
	 * @param name The name of the image file.
	 * @param x The x-axis start of the sub-image.
	 * @param y The y-axis start of the sub-image.
	 * @param w The width of the sub-image.
	 * @param h The height of the sub-image.
	 */
	public RSImage(String name, int x, int y, int w, int h) {
		try {
			ImageIcon i = new ImageIcon(location + name + ".png");
			BufferedImage bi = toBufferedImage(i.getImage()).getSubimage(x, y, w, h);
			ImageIcon b = new ImageIcon(bi);
			Image image = b.getImage();
			ImageIcon sprite = new ImageIcon(image);
			myWidth = sprite.getIconWidth();
			myHeight = sprite.getIconHeight();
			maxWidth = myWidth;
			maxHeight = myHeight;
			offsetX = 0;
			offsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
			setTransparency(255, 0, 255);
			image = null;
		} catch(Exception _ex) {
			//System.out.println(_ex);
		}
	}

	/**
	 * Creates a sub-image from a byte array.
	 * @param data The byte array.
	 * @param x The x-axis start of the sub-image.
	 * @param y The y-axis start of the sub-image.
	 * @param w The width of the sub-image.
	 * @param h The height of the sub-image.
	 */
	public RSImage(byte[] data, int x, int y, int w, int h) {
		try {
			ImageIcon i = new ImageIcon(data);
			BufferedImage bi = toBufferedImage(i.getImage()).getSubimage(x, y, w, h);
			ImageIcon b = new ImageIcon(bi);
			Image image = b.getImage();
			ImageIcon sprite = new ImageIcon(image);
			myWidth = sprite.getIconWidth();
			myHeight = sprite.getIconHeight();
			maxWidth = myWidth;
			maxHeight = myHeight;
			offsetX = 0;
			offsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
			setTransparency(255, 0, 255);
			image = null;
		} catch(Exception _ex) {
			//System.out.println(_ex);
		}
	}

	public BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage)image;
		}
		image = new ImageIcon(image).getImage();
		boolean hasAlpha = false;
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency); 
		} catch (HeadlessException e) {
		} 
		if (bimage == null) {
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB; 
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		Graphics g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null); 
		g.dispose();
		return bimage;
	}

	/**
	 * Creates an image from an external location.
	 * @param name The name of the image file.
	 */
	public RSImage(String name) {
		try {
			Image image = Toolkit.getDefaultToolkit().getImage(location + name + ".png");
			ImageIcon sprite = new ImageIcon(image);
			myWidth = sprite.getIconWidth();
			myHeight = sprite.getIconHeight();
			maxWidth = myWidth;
			maxHeight = myHeight;
			offsetX = 0;
			offsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
			setTransparency(255, 0, 255);
			image = null;
		} catch (Exception _ex) {
			System.out.println(_ex);
		}
	}

	public RSImage(byte data[], Component component) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(data);
			MediaTracker mediatracker = new MediaTracker(component);
			mediatracker.addImage(image, 0);
			mediatracker.waitForAll();
			myWidth = image.getWidth(component);
			myHeight = image.getHeight(component);
			maxWidth = myWidth;
			maxHeight = myHeight;
			offsetX = 0;
			offsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
		} catch (Exception _ex) {
			System.out.println("Error converting jpg");
		}
	}

	public RSImage(JagexArchive streamLoader, String s, int i) {
		JagexBuffer stream = new JagexBuffer(streamLoader.getData(s + ".dat"));
		JagexBuffer stream_1 = new JagexBuffer(streamLoader.getData("index.dat"));
		stream_1.offset = stream.getUnsignedShort();
		maxWidth = stream_1.getUnsignedShort();
		maxHeight = stream_1.getUnsignedShort();
		int j = stream_1.getUnsignedByte();
		int pixels[] = new int[j];
		for (int index = 0; index < j - 1; index++) {
			pixels[index + 1] = stream_1.get3Bytes();
			if (pixels[index + 1] == 0) {
				pixels[index + 1] = 1;
			}
		}
		for (int index = 0; index < i; index++) {
			stream_1.offset += 2;
			stream.offset += stream_1.getUnsignedShort() * stream_1.getUnsignedShort();
			stream_1.offset++;
		}

		offsetX = stream_1.getUnsignedByte();
		offsetY = stream_1.getUnsignedByte();
		myWidth = stream_1.getUnsignedShort();
		myHeight = stream_1.getUnsignedShort();
		int i1 = stream_1.getUnsignedByte();
		int totalPixels = myWidth * myHeight;
		myPixels = new int[totalPixels];
		if (i1 == 0) {
			for (int index = 0; index < totalPixels; index++) {
				myPixels[index] = pixels[stream.getUnsignedByte()];
			}
			return;
		}
		if (i1 == 1) {
			for (int width = 0; width < myWidth; width++) {
				for (int height = 0; height < myHeight; height++) {
					myPixels[width + height * myWidth] = pixels[stream.getUnsignedByte()];
				}
			}
		}
	}

	public void method343() {
		RSDrawingArea.initDrawingArea(myHeight, myWidth, myPixels);
	}

	public void adjustColors(int redOffset, int greenOffset, int blueOffset) {
		for (int index = 0; index < myPixels.length; index++) {
			int color = myPixels[index];
			if (color != 0) {
				int red = color >> 16 & 0xff;
				red += redOffset;
				if (red < 1) {
					red = 1;
				} else if (red > 255) {
					red = 255;
				}
				int green = color >> 8 & 0xff;
				green += greenOffset;
				if (green < 1) {
					green = 1;
				} else if (green > 255) {
					green = 255;
				}
				int blue = color & 0xff;
				blue += blueOffset;
				if (blue < 1) {
					blue = 1;
				} else if (blue > 255) {
					blue = 255;
				}
				myPixels[index] = (red << 16) + (green << 8) + blue;
			}
		}
	}

	public void trim() {
		int pixels[] = new int[maxWidth * maxHeight];
		for (int index = 0; index < myHeight; index++) {
			System.arraycopy(myPixels, index * myWidth, pixels, index + offsetY * maxWidth + offsetX, myWidth);
		}
		myPixels = pixels;
		myWidth = maxWidth;
		myHeight = maxHeight;
		offsetX = 0;
		offsetY = 0;
	}

	public void drawInverse(int x, int y) {
		x += offsetX;
		y += offsetY;
		int offset = x + y * RSDrawingArea.width;
		int originalOffset = 0;
		int height = myHeight;
		int width = myWidth;
		int deviation = RSDrawingArea.width - width;
		int originalDeviation = 0;
		if (y < RSDrawingArea.startY) {
			int j2 = RSDrawingArea.startY - y;
			height -= j2;
			y = RSDrawingArea.startY;
			originalOffset += j2 * width;
			offset += j2 * RSDrawingArea.width;
		}
		if (y + height > RSDrawingArea.endY)
			height -= (y + height) - RSDrawingArea.endY;
		if (x < RSDrawingArea.startX) {
			int k2 = RSDrawingArea.startX - x;
			width -= k2;
			x = RSDrawingArea.startX;
			originalOffset += k2;
			offset += k2;
			originalDeviation += k2;
			deviation += k2;
		}
		if (x + width > RSDrawingArea.endX) {
			int l2 = (x + width) - RSDrawingArea.endX;
			width -= l2;
			originalDeviation += l2;
			deviation += l2;
		}
		if (width <= 0 || height <= 0) {
		} else {
			copyPixels(offset, width, height, originalDeviation, originalOffset, deviation, myPixels, RSDrawingArea.pixels);
		}
	}

	private void copyPixels(int i, int width, int height, int l, int i1, int k1, int ai[], int pixels[]) {
		int shiftedWidth = -(width >> 2);
		width = -(width & 3);
		for (int i2 = -height; i2 < 0; i2++) {
			for (int j2 = shiftedWidth; j2 < 0; j2++) {
				pixels[i++] = ai[i1++];
				pixels[i++] = ai[i1++];
				pixels[i++] = ai[i1++];
				pixels[i++] = ai[i1++];
			}
			for (int k2 = width; k2 < 0; k2++) {
				pixels[i++] = ai[i1++];
			}
			i += k1;
			i1 += l;
		}
	}

	public void drawCenteredImage(int x, int y) {
		drawImage(x - (this.myWidth / 2), y - (this.myHeight / 2));
	}

	public void drawImage(int x, int y) {
		drawImage(x, y, 256);
	}

	public void drawImage(int x, int y, int alpha) {
		x += offsetX;
		y += offsetY;
		int i1 = x + y * RSDrawingArea.width;
		int j1 = 0;
		int height = myHeight;
		int width = myWidth;
		int i2 = RSDrawingArea.width - width;
		int j2 = 0;
		if (y < RSDrawingArea.startY) {
			int k2 = RSDrawingArea.startY - y;
			height -= k2;
			y = RSDrawingArea.startY;
			j1 += k2 * width;
			i1 += k2 * RSDrawingArea.width;
		}
		if (y + height > RSDrawingArea.endY)
			height -= (y + height) - RSDrawingArea.endY;
		if (x < RSDrawingArea.startX) {
			int l2 = RSDrawingArea.startX - x;
			width -= l2;
			x = RSDrawingArea.startX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (x + width > RSDrawingArea.endX) {
			int i3 = (x + width) - RSDrawingArea.endX;
			width -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(width <= 0 || height <= 0)) {
			setPixels(j1, width, RSDrawingArea.pixels, myPixels, j2, height, i2, alpha, i1);
		}
	}

	@SuppressWarnings("unused")
	private void method349(int ai[], int ai1[], int j, int k, int l, int i1, int j1, int k1) {
		int i;// was parameter
		int l1 = -(l >> 2);
		l = -(l & 3);
		for (int i2 = -i1; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				i = ai1[j++];
				if (i != 0)
					ai[k++] = i;
				else
					k++;
				i = ai1[j++];
				if (i != 0)
					ai[k++] = i;
				else
					k++;
				i = ai1[j++];
				if (i != 0)
					ai[k++] = i;
				else
					k++;
				i = ai1[j++];
				if (i != 0)
					ai[k++] = i;
				else
					k++;
			}

			for (int k2 = l; k2 < 0; k2++) {
				i = ai1[j++];
				if (i != 0)
					ai[k++] = i;
				else
					k++;
			}

			k += j1;
			j += k1;
		}

	}

	private void setPixels(int originalOffset, int width, int pixels[], int originalPixels[], int originalDeviation, int height, int deviation, int alpha, int offset) {
		int color;
		int alphaValue = 256 - alpha;
		for (int k2 = -height; k2 < 0; k2++) {
			for (int l2 = -width; l2 < 0; l2++) {
				color = originalPixels[originalOffset++];
				if (color != 0) {
					int i3 = pixels[offset];
					pixels[offset++] = ((color & 0xff00ff) * alpha + (i3 & 0xff00ff) * alphaValue & 0xff00ff00) + ((color & 0xff00) * alpha + (i3 & 0xff00) * alphaValue & 0xff0000) >> 8;
				} else {
					offset++;
				}
			}
			offset += deviation;
			originalOffset += originalDeviation;
		}
	}

	public void shapeImageToPixels(int i, int j, int ai[], int k, int originalPixels[], int i1, int y, int x, int l1, int i2) {
		try {
			int j2 = -l1 / 2;
			int k2 = -i / 2;
			int l2 = (int) (Math.sin((double) j / 326.11000000000001D) * 65536D);
			int i3 = (int) (Math.cos((double) j / 326.11000000000001D) * 65536D);
			l2 = l2 * k >> 8;
			i3 = i3 * k >> 8;
			int j3 = (i2 << 16) + (k2 * l2 + j2 * i3);
			int k3 = (i1 << 16) + (k2 * i3 - j2 * l2);
			int offset = x + y * RSDrawingArea.width;
			for (y = 0; y < i; y++) {
				int i4 = originalPixels[y];
				int j4 = offset + i4;
				int k4 = j3 + i3 * i4;
				int l4 = k3 - l2 * i4;
				for (x = -ai[y]; x < 0; x++) {
					RSDrawingArea.pixels[j4++] = myPixels[(k4 >> 16) + (l4 >> 16) * myWidth];
					k4 += i3;
					l4 -= l2;
				}
				j3 += l2;
				k3 += i3;
				offset += RSDrawingArea.width;
			}

		} catch (Exception _ex) {
		}
	}

	public void method353(int i, double d, int l1) {
		// all of the following were parameters
		int j = 15;
		int k = 20;
		int l = 15;
		int j1 = 256;
		int k1 = 20;
		// all of the previous were parameters
		try {
			int i2 = -k / 2;
			int j2 = -k1 / 2;
			int k2 = (int) (Math.sin(d) * 65536D);
			int l2 = (int) (Math.cos(d) * 65536D);
			k2 = k2 * j1 >> 8;
			l2 = l2 * j1 >> 8;
			int i3 = (l << 16) + (j2 * k2 + i2 * l2);
			int j3 = (j << 16) + (j2 * l2 - i2 * k2);
			int k3 = l1 + i * RSDrawingArea.width;
			for (i = 0; i < k1; i++) {
				int l3 = k3;
				int i4 = i3;
				int j4 = j3;
				for (l1 = -k; l1 < 0; l1++) {
					int k4 = myPixels[(i4 >> 16) + (j4 >> 16) * myWidth];
					if (k4 != 0)
						RSDrawingArea.pixels[l3++] = k4;
					else
						l3++;
					i4 += l2;
					j4 -= k2;
				}

				i3 += k2;
				j3 += l2;
				k3 += RSDrawingArea.width;
			}

		} catch (Exception _ex) {
		}
	}

	public void method354(IndexedImage image, int i, int j) {
		j += offsetX;
		i += offsetY;
		int k = j + i * RSDrawingArea.width;
		int l = 0;
		int height = myHeight;
		int width = myWidth;
		int k1 = RSDrawingArea.width - width;
		int l1 = 0;
		if (i < RSDrawingArea.startY) {
			int i2 = RSDrawingArea.startY - i;
			height -= i2;
			i = RSDrawingArea.startY;
			l += i2 * width;
			k += i2 * RSDrawingArea.width;
		}
		if (i + height > RSDrawingArea.endY) {
			height -= (i + height) - RSDrawingArea.endY;
		}
		if (j < RSDrawingArea.startX) {
			int j2 = RSDrawingArea.startX - j;
			width -= j2;
			j = RSDrawingArea.startX;
			l += j2;
			k += j2;
			l1 += j2;
			k1 += j2;
		}
		if (j + width > RSDrawingArea.endX) {
			int k2 = (j + width) - RSDrawingArea.endX;
			width -= k2;
			l1 += k2;
			k1 += k2;
		}
		if (!(width <= 0 || height <= 0)) {
			method355(myPixels, width, image.myPixels, height, RSDrawingArea.pixels, 0, k1, k, l1, l);
		}
	}

	private void method355(int ai[], int i, byte abyte0[], int j, int ai1[], int k, int l, int i1, int j1, int k1) {
		int l1 = -(i >> 2);
		i = -(i & 3);
		for (int j2 = -j; j2 < 0; j2++) {
			for (int k2 = l1; k2 < 0; k2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0) {
					ai1[i1++] = k;
				} else {
					i1++;
				}
			}

			for (int l2 = i; l2 < 0; l2++) {
				k = ai[k1++];
				if (k != 0 && abyte0[i1] == 0)
					ai1[i1++] = k;
				else
					i1++;
			}

			i1 += l;
			k1 += j1;
		}
	}

	public void drawCenteredARGBImage(int x, int y) {
		drawARGBImage(x - (this.myWidth / 2), y - (this.myHeight / 2));
	}

	public void drawARGBImage(int xPos, int yPos) {
		drawARGBSprite(xPos, yPos, 256);
	}

	public void drawARGBSprite(int xPos, int yPos, int alpha) {
		int alphaValue = alpha;
		xPos += offsetX;
		yPos += offsetY;
		int i1 = xPos + yPos * RSDrawingArea.width;
		int j1 = 0;
		int spriteHeight = myHeight;
		int spriteWidth = myWidth;
		int i2 = RSDrawingArea.width - spriteWidth;
		int j2 = 0;
		if (yPos < RSDrawingArea.startY) {
			int k2 = RSDrawingArea.startY - yPos;
			spriteHeight -= k2;
			yPos = RSDrawingArea.startY;
			j1 += k2 * spriteWidth;
			i1 += k2 * RSDrawingArea.width;
		}
		if (yPos + spriteHeight > RSDrawingArea.endY)
			spriteHeight -= (yPos + spriteHeight) - RSDrawingArea.endY;
			if (xPos < RSDrawingArea.startX) {
			int l2 = RSDrawingArea.startX - xPos;
			spriteWidth -= l2;
			xPos = RSDrawingArea.startX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (xPos + spriteWidth > RSDrawingArea.endX) {
			int i3 = (xPos + spriteWidth) - RSDrawingArea.endX;
			spriteWidth -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(spriteWidth <= 0 || spriteHeight <= 0)) {
			renderARGBPixels(spriteWidth, spriteHeight, myPixels, RSDrawingArea.pixels, i1, alphaValue, j1, j2, i2);
		}
	}

    private void renderARGBPixels(int spriteWidth, int spriteHeight, int spritePixels[], int renderAreaPixels[], int pixel, int alphaValue, int i, int l, int j1) {
    	int pixelLevel;
    	int alphaLevel;
    	for (int height = -spriteHeight; height < 0; height++) {
    		for (int width = -spriteWidth; width < 0; width++) {
    			alphaValue = ((myPixels[i] >> 24) & 255);
    			alphaLevel = 256 - alphaValue;
    			pixelLevel = spritePixels[i++];
    			if (pixelLevel != 0) {
    				int pixelValue = renderAreaPixels[pixel];
    				renderAreaPixels[pixel++] = ((pixelLevel & 0xff00ff) * alphaValue + (pixelValue & 0xff00ff) * alphaLevel & 0xff00ff00) + ((pixelLevel & 0xff00) * alphaValue + (pixelValue & 0xff00) * alphaLevel & 0xff0000) >> 8;
    			} else {
    				pixel++;
    			}
    		}
    		pixel += j1;
    		i += l;
    	}
    }

	public int myPixels[];
	public int myWidth;
	public int myHeight;
	private int offsetX;
	private int offsetY;
	public int maxWidth;
	public int maxHeight;
}
