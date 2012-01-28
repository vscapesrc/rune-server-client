package rs2.graphics;

import rs2.NodeSub;

public class RSDrawingArea extends NodeSub {

	public static void initDrawingArea(int h, int w, int pix[]) {
		pixels = pix;
		width = w;
		height = h;
		setBounds(0, w, 0, h);
	}

	public static void setDefaultArea() {
		startX = 0;
		startY = 0;
		endX = width;
		endY = height;
		centerX = endX;
		centerY = endX / 2;
	}

	public static void setBounds(int x1, int x2, int y1, int y2) {
		if (x1 < 0)
			x1 = 0;
		if (y1 < 0)
			y1 = 0;
		if (x2 > width)
			x2 = width;
		if (y2 > height)
			y2 = height;
		startX = x1;
		startY = y1;
		endX = x2;
		endY = y2;
		centerX = endX;
		centerY = endX / 2;
		anInt1387 = endY / 2;
	}

	public static void setAllPixelsToZero() {
		int i = width * height;
		for (int index = 0; index < i; index++) {
			pixels[index] = 0;
		}
	}

	public static void drawRoundedRectangle(int x, int y, int width,
			int height, int color, int alpha, boolean filled, boolean shadowed) {
		if (shadowed)
			drawRoundedRectangle(x + 1, y + 1, width, height, 0, alpha, filled,
					false);
		if (alpha == -1) {
			if (filled) {
				drawHorizontalLine(y + 1, color, width - 4, x + 2);
				drawHorizontalLine(y + height - 2, color, width - 4, x + 2);
				drawFilledPixels(height - 4, y + 2, x + 1, color, width - 2);
			}
			drawHorizontalLine(y, color, width - 4, x + 2);
			drawHorizontalLine(y + height - 1, color, width - 4, x + 2);
			drawVerticalLine(x, y + 2, height - 4, color);
			drawVerticalLine(x + width - 1, y + 2, height - 4, color);
			drawFilledPixels(x + 1, y + 1, 1, 1, color);
			drawFilledPixels(x + width - 2, y + 1, 1, 1, color);
			drawFilledPixels(x + width - 2, y + height - 2, 1, 1, color);
			drawFilledPixels(x + 1, y + height - 2, 1, 1, color);
		} else if (alpha != -1) {
			if (filled) {
				method340(color, width - 4, y + 1, alpha, x + 2);
				method340(color, width - 4, y + height - 2, alpha, x + 2);
				method335(color, y + 2, width - 2, height - 4, alpha, x + 1);
			}
			method340(color, width - 4, y, alpha, x + 2);
			method340(color, width - 4, y + height - 1, alpha, x + 2);
			method342(color, x, alpha, y + 2, height - 4);
			method342(color, x + width - 1, alpha, y + 2, height - 4);
			method335(color, y + 1, 1, 1, alpha, x + 1);
			method335(color, y + 1, 1, 1, alpha, x + width - 2);
			method335(color, y + height - 2, 1, 1, alpha, x + 1);
			method335(color, y + height - 2, 1, 1, alpha, x + width - 2);
		}
	}

	public static void method335(int i, int j, int k, int l, int i1, int k1) {
		if (k1 < startX) {
			k -= startX - k1;
			k1 = startX;
		}
		if (j < startY) {
			l -= startY - j;
			j = startY;
		}
		if (k1 + k > endX)
			k = endX - k1;
		if (j + l > endY)
			l = endY - j;
		int l1 = 256 - i1;
		int i2 = (i >> 16 & 0xff) * i1;
		int j2 = (i >> 8 & 0xff) * i1;
		int k2 = (i & 0xff) * i1;
		int k3 = width - k;
		int l3 = k1 + j * width;
		for (int i4 = 0; i4 < l; i4++) {
			for (int j4 = -k; j4 < 0; j4++) {
				int l2 = (pixels[l3] >> 16 & 0xff) * l1;
				int i3 = (pixels[l3] >> 8 & 0xff) * l1;
				int j3 = (pixels[l3] & 0xff) * l1;
				int k4 = ((i2 + l2 >> 8) << 16) + ((j2 + i3 >> 8) << 8)
						+ (k2 + j3 >> 8);
				pixels[l3++] = k4;
			}

			l3 += k3;
		}
	}

	public static void drawFilledPixels(int x, int y, int w, int h, int color) {
		if (x < startX) {
			w -= startX - x;
			x = startX;
		}
		if (y < startY) {
			h -= startY - y;
			y = startY;
		}
		if (x + w > endX) {
			w = endX - x;
		}
		if (y + h > endY) {
			h = endY - y;
		}
		int start = width - w;
		int total = x + y * width;
		for (int index1 = -h; index1 < 0; index1++) {
			for (int index2 = -w; index2 < 0; index2++) {
				pixels[total++] = color;
			}
			total += start;
		}
	}

	public static void drawUnfilledPixels(int x, int y, int width, int height, int color) {
		drawHorizontalLine(x, y, width, color);
		drawHorizontalLine(x, (y + height) - 1, width, color);
		drawVerticalLine(x, y, height, color);
		drawVerticalLine((x + width) - 1, y, height, color);
	}

	public static void method338(int i, int j, int k, int l, int i1, int j1) {
		method340(l, i1, i, k, j1);
		method340(l, i1, (i + j) - 1, k, j1);
		if (j >= 3) {
			method342(l, j1, k, i + 1, j - 2);
			method342(l, (j1 + i1) - 1, k, i + 1, j - 2);
		}
	}

	public static void drawHorizontalLine(int x, int y, int length, int color) {
		if (y < startY || y >= endY)
			return;
		if (x < startX) {
			length -= startX - x;
			x = startX;
		}
		if (x + length > endX)
			length = endX - x;
		int total = x + y * width;
		for (int index = 0; index < length; index++) {
			pixels[total + index] = color;
		}

	}

	private static void method340(int i, int j, int k, int alphaValue, int i1) {
		if (k < startY || k >= endY)
			return;
		if (i1 < startX) {
			j -= startX - i1;
			i1 = startX;
		}
		if (i1 + j > endX)
			j = endX - i1;
		int alpha = 256 - alphaValue;
		int k1 = (i >> 16 & 0xff) * alphaValue;
		int l1 = (i >> 8 & 0xff) * alphaValue;
		int i2 = (i & 0xff) * alphaValue;
		int i3 = i1 + k * width;
		for (int index = 0; index < j; index++) {
			int r = (pixels[i3] >> 16 & 0xff) * alpha;
			int g = (pixels[i3] >> 8 & 0xff) * alpha;
			int b = (pixels[i3] & 0xff) * alpha;
			int color = ((k1 + r >> 8) << 16) + ((l1 + g >> 8) << 8) + (i2 + b >> 8);
			pixels[i3++] = color;
		}

	}

	public static void drawVerticalLine(int x, int y, int h, int color) {
		if (x < startX || x >= endX)
			return;
		if (y < startY) {
			h -= startY - y;
			y = startY;
		}
		if (y + h > endY)
			h = endY - y;
		int j1 = x + y * width;
		for (int k1 = 0; k1 < h; k1++)
			pixels[j1 + k1 * width] = color;

	}

	private static void method342(int i, int j, int k, int l, int i1) {
		if (j < startX || j >= endX)
			return;
		if (l < startY) {
			i1 -= startY - l;
			l = startY;
		}
		if (l + i1 > endY)
			i1 = endY - l;
		int j1 = 256 - k;
		int k1 = (i >> 16 & 0xff) * k;
		int l1 = (i >> 8 & 0xff) * k;
		int i2 = (i & 0xff) * k;
		int i3 = j + l * width;
		for (int j3 = 0; j3 < i1; j3++) {
			int j2 = (pixels[i3] >> 16 & 0xff) * j1;
			int k2 = (pixels[i3] >> 8 & 0xff) * j1;
			int l2 = (pixels[i3] & 0xff) * j1;
			int k3 = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8)
					+ (i2 + l2 >> 8);
			pixels[i3] = k3;
			i3 += width;
		}

	}

	RSDrawingArea() {
	}

	public static int pixels[];
	public static int width;
	public static int height;
	public static int startY;
	public static int endY;
	public static int startX;
	public static int endX;
	public static int centerX;
	public static int centerY;
	public static int anInt1387;

}
