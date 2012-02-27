package rs2.graphics;

import rs2.NodeSub;

public class RSDrawingArea extends NodeSub {

	public static void initDrawingArea(int w, int h, int pix[]) {
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
				drawHorizontalAlphaLine(x + 2, y + 1, width - 4, color, alpha);
				drawHorizontalAlphaLine(x + 2, y + height - 2, width - 4, color, alpha);
				drawFilledAlphaPixels(x + 1, y + 2, width - 2, height - 4, color, alpha);
			}
			drawHorizontalAlphaLine(x + 2, y, width - 4, color, alpha);
			drawHorizontalAlphaLine(x + 2, y + height - 1, width - 4, color, alpha);
			drawVerticalAlphaLine(x, y + 2, height - 4, color, alpha);
			drawVerticalAlphaLine(x + width - 1, y + 2, height - 4, color, alpha);
			drawFilledAlphaPixels(x + 1, y + 1, 1, 1, color, alpha);
			drawFilledAlphaPixels(x + width - 2, y + 1, 1, 1, color, alpha);
			drawFilledAlphaPixels(x + 1, y + height - 2, 1, 1, color, alpha);
			drawFilledAlphaPixels(x + width - 2, y + height - 2, 1, 1, color, alpha);
		}
	}

	public static void drawFilledAlphaPixels(int x, int y, int w, int h, int color, int alpha) {
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
		int alphaValue = 256 - alpha;
		int red = (color >> 16 & 0xff) * alpha;
		int green = (color >> 8 & 0xff) * alpha;
		int blue = (color & 0xff) * alpha;
		int offset = width - w;
		int pixel = x + y * width;
		for (int heightPointer = 0; heightPointer < h; heightPointer++) {
			for (int widthPointer = -w; widthPointer < 0; widthPointer++) {
				int r = (pixels[pixel] >> 16 & 0xff) * alphaValue;
				int g = (pixels[pixel] >> 8 & 0xff) * alphaValue;
				int b = (pixels[pixel] & 0xff) * alphaValue;
				int pixelColor = ((red + r >> 8) << 16) + ((green + g >> 8) << 8) + (blue + b >> 8);
				pixels[pixel++] = pixelColor;
			}
			pixel += offset;
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
		drawHorizontalAlphaLine(j1, i, i1, l, k);
		drawHorizontalAlphaLine(j1, (i + j) - 1, i1, l, k);
		if (j >= 3) {
			drawVerticalAlphaLine(j1, i + 1, j - 2, l, k);
			drawVerticalAlphaLine((j1 + i1) - 1, i + 1, j - 2, l, k);
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

	public static void drawHorizontalAlphaLine(int x, int y, int length, int color, int alphaValue) {
		if (y < startY || y >= endY)
			return;
		if (x < startX) {
			length -= startX - x;
			x = startX;
		}
		if (x + length > endX)
			length = endX - x;
		int alpha = 256 - alphaValue;
		int red = (color >> 16 & 0xff) * alphaValue;
		int green = (color >> 8 & 0xff) * alphaValue;
		int blue = (color & 0xff) * alphaValue;
		int pixel = x + y * width;
		for (int index = 0; index < length; index++) {
			int r = (pixels[pixel] >> 16 & 0xff) * alpha;
			int g = (pixels[pixel] >> 8 & 0xff) * alpha;
			int b = (pixels[pixel] & 0xff) * alpha;
			int pixelColor = ((red + r >> 8) << 16) + ((green + g >> 8) << 8) + (blue + b >> 8);
			pixels[pixel++] = pixelColor;
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

	public static void drawVerticalAlphaLine(int x, int y, int length, int color, int alpha) {
		if (x < startX || x >= endX) {
			return;
		}
		if (y < startY) {
			length -= startY - y;
			y = startY;
		}
		if (y + length > endY) {
			length = endY - y;
		}
		int alphaValue = 256 - alpha;
		int red = (color >> 16 & 0xff) * alpha;
		int green = (color >> 8 & 0xff) * alpha;
		int blue = (color & 0xff) * alpha;
		int pixel = x + y * width;
		for (int j3 = 0; j3 < length; j3++) {
			int r = (pixels[pixel] >> 16 & 0xff) * alphaValue;
			int g = (pixels[pixel] >> 8 & 0xff) * alphaValue;
			int b = (pixels[pixel] & 0xff) * alphaValue;
			int pixelColor = ((red + r >> 8) << 16) + ((green + g >> 8) << 8) + (blue + b >> 8);
			pixels[pixel] = pixelColor;
			pixel += width;
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
