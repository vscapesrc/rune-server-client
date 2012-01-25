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
		centerX = endX - 1;
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
		centerX = endX - 1;
		centerY = endX / 2;
		anInt1387 = endY / 2;
	}

	public static void setAllPixelsToZero() {
		int i = width * height;
		for (int j = 0; j < i; j++)
			pixels[j] = 0;

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

	public static void method336(int i, int j, int k, int l, int i1) {
		if (k < startX) {
			i1 -= startX - k;
			k = startX;
		}
		if (j < startY) {
			i -= startY - j;
			j = startY;
		}
		if (k + i1 > endX)
			i1 = endX - k;
		if (j + i > endY)
			i = endY - j;
		int k1 = width - i1;
		int l1 = k + j * width;
		for (int i2 = -i; i2 < 0; i2++) {
			for (int j2 = -i1; j2 < 0; j2++)
				pixels[l1++] = l;

			l1 += k1;
		}

	}

	public static void fillPixels(int i, int j, int k, int l, int i1) {
		drawHorizontalLine(i, i1, j, l);
		drawHorizontalLine(i, (i1 + k) - 1, j, l);
		method341(i1, l, k, i);
		method341(i1, l, k, (i + j) - 1);
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

	public static void method341(int i, int j, int k, int l) {
		if (l < startX || l >= endX)
			return;
		if (i < startY) {
			k -= startY - i;
			i = startY;
		}
		if (i + k > endY)
			k = endY - i;
		int j1 = l + i * width;
		for (int k1 = 0; k1 < k; k1++)
			pixels[j1 + k1 * width] = j;

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
