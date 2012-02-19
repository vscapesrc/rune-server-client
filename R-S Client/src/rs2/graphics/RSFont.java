package rs2.graphics;

import java.util.Random;

import rs2.JagexBuffer;
import rs2.Client;
import rs2.cache.JagexArchive;

public final class RSFont extends RSDrawingArea {

	public RSFont(boolean flag, String name, JagexArchive archive) {
		characterPixels = new byte[256][];
		characterWidth = new int[256];
		characterHeight = new int[256];
		characterOffsetX = new int[256];
		characterOffsetY = new int[256];
		characterScreenWidth = new int[256];
		myRandom = new Random();
		strikeThrough = false;
		JagexBuffer font = new JagexBuffer(archive.getData(name + ".dat"));
		JagexBuffer index = new JagexBuffer(archive.getData("index.dat"));
		index.offset = font.getUnsignedShort() + 4;
		int k = index.getUnsignedByte();
		if (k > 0) {
			index.offset += 3 * (k - 1);
		}
		for (int charIndex = 0; charIndex < 256; charIndex++) {
			characterOffsetX[charIndex] = index.getUnsignedByte();
			characterOffsetY[charIndex] = index.getUnsignedByte();
			int charWidth = characterWidth[charIndex] = index.getUnsignedShort();
			int charHeight = characterHeight[charIndex] = index.getUnsignedShort();
			int mode = index.getUnsignedByte();
			int size = charWidth * charHeight;
			characterPixels[charIndex] = new byte[size];
			if (mode == 0) {
				for (int address = 0; address < size; address++) {
					characterPixels[charIndex][address] = font.getSignedByte();
				}
			} else if (mode == 1) {
				for (int x = 0; x < charWidth; x++) {
					for (int y = 0; y < charHeight; y++) {
						characterPixels[charIndex][x + y * charWidth] = font.getSignedByte();
					}
				}
			}
			if (charHeight > baseHeight && charIndex < 128) {
				baseHeight = charHeight;
			}
			characterOffsetX[charIndex] = 1;
			characterScreenWidth[charIndex] = charWidth + 2;
			int k2 = 0;
			for (int i3 = charHeight / 7; i3 < charHeight; i3++) {
				k2 += characterPixels[charIndex][i3 * charWidth];
			}
			if (k2 <= charHeight / 7) {
				characterScreenWidth[charIndex]--;
				characterOffsetX[charIndex] = 0;
			}
			k2 = 0;
			for (int j3 = charHeight / 7; j3 < charHeight; j3++) {
				k2 += characterPixels[charIndex][(charWidth - 1) + j3 * charWidth];
			}
			if (k2 <= charHeight / 7) {
				characterScreenWidth[charIndex]--;
			}
		}
		if (flag) {
			characterScreenWidth[32] = characterScreenWidth[73];
		} else {
			characterScreenWidth[32] = characterScreenWidth[105];
		}
	}

	public void drawTextRA(String string, int x, int y, int color) {
		drawBasicString(string, x - getTextWidth(string), y, color);
	}

	public void drawText(String string, int x, int y, int color) {
		drawBasicString(string, x - getTextWidth(string) / 2, y, color);
	}

	public void drawCenteredString(String string, int x, int y, int color, boolean shadow) {
		drawShadowedString(string, x - getEffectTextWidth(string) / 2, y, color, shadow);
	}

	public int getEffectTextWidth(String text) {
		if (text == null)
			return 0;
		int width = 0;
		for (int index = 0; index < text.length(); index++) {
			if (text.charAt(index) == '@' && index + 4 < text.length() && text.charAt(index + 4) == '@') {
				index += 4;
			} else {
				width += characterScreenWidth[text.charAt(index)];
			}
		}
		return width;
	}

	public int getTextHeight() {
		if (this == Client.instance.small) {
			return 8;
		}
		if (this == Client.instance.regular) {
			return 10;
		}
		if (this == Client.instance.bold) {
			return 10;
		}
		if (this == Client.instance.fancy) {
			return 11;
		}
		return 10;
	}

	public int getTextWidth(String string) {
		if (string == null) {
			return 0;
		}
		int width = 0;
		for (int index = 0; index < string.length(); index++) {
			width += characterScreenWidth[string.charAt(index)];
		}
		return width;
	}

	public void drawBasicString(String string, int x, int y, int color) {
		if (string == null) {
			return;
		}
		y -= baseHeight;
		for (int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);
			if (c != ' ') {
				drawCharacter(characterPixels[c], x + characterOffsetX[c], y + characterOffsetY[c], characterWidth[c], characterHeight[c], color);
			}
			x += characterScreenWidth[c];
		}
	}

	public void drawCenteredStringMoveY(String string, int x, int y, int color, int waveAmount) {
		if (string == null) {
			return;
		}
		x -= getTextWidth(string) / 2;
		y -= baseHeight;
		for (int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);
			if (c != ' ') {
				drawCharacter(characterPixels[c], x + characterOffsetX[c], y + characterOffsetY[c] + (int) (Math.sin((double) index / 2D + (double) waveAmount / 5D) * 5D), characterWidth[c], characterHeight[c], color);
			}
			x += characterScreenWidth[c];
		}
	}

	public void drawCenteredStringMoveXY(String string, int x, int y, int color, int waveAmount) {
		if (string == null) {
			return;
		}
		x -= getTextWidth(string) / 2;
		y -= baseHeight;
		for (int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);
			if (c != ' ') {
				drawCharacter(characterPixels[c], x + characterOffsetX[c] + (int) (Math.sin((double) index / 5D + (double) waveAmount / 5D) * 5D), y + characterOffsetY[c] + (int) (Math.sin((double) index / 3D + (double) waveAmount / 5D) * 5D), characterWidth[c], characterHeight[c], color);
			}
			x += characterScreenWidth[c];
		}
	}

	public void drawStringMoveY(int waveSpeed, String string, int waveAmount, int y, int x, int color) {
		if (string == null) {
			return;
		}
		double speed = 7D - (double) waveSpeed / 8D;
		if (speed < 0.0D) {
			speed = 0.0D;
		}
		x -= getTextWidth(string) / 2;
		y -= baseHeight;
		for (int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);
			if (c != ' ') {
				drawCharacter(characterPixels[c], x + characterOffsetX[c], y + characterOffsetY[c] + (int) (Math.sin((double) index / 1.5D + (double) waveAmount) * speed), characterWidth[c], characterHeight[c], color);
			}
			x += characterScreenWidth[c];
		}
	}

	public void drawShadowedString(String string, int x, int y, int color, boolean shadow) {
		strikeThrough = false;
		int lineX = x;
		if (string == null) {
			return;
		}
		y -= baseHeight;
		for (int index = 0; index < string.length(); index++) {
			if (string.charAt(index) == '@' && index + 4 < string.length() && string.charAt(index + 4) == '@') {
				int textColor = getColorByName(string.substring(index + 1, index + 4));
				if (textColor != -1) {
					color = textColor;
				}
				index += 4;
			} else {
				char c = string.charAt(index);
				if (c != ' ') {
					if (shadow) {
						drawCharacter(characterPixels[c], x + characterOffsetX[c] + 1, y + characterOffsetY[c] + 1, characterWidth[c], characterHeight[c], 0);
					}
					drawCharacter(characterPixels[c], x + characterOffsetX[c], y + characterOffsetY[c], characterWidth[c], characterHeight[c], color);
				}
				x += characterScreenWidth[c];
			}
		}
		if (strikeThrough) {
			RSDrawingArea.drawHorizontalLine(lineX, y + (int) ((double) baseHeight * 0.69999999999999996D), x - lineX, 0x800000);
		}
	}

	public void drawShadowedString(String string, int x, int y, int color, int seed) {
		if (string == null) {
			return;
		}
		myRandom.setSeed(seed);
		int opacity = 192 + (myRandom.nextInt() & 0x1f);
		y -= baseHeight;
		for (int index = 0; index < string.length(); index++) {
			if (string.charAt(index) == '@' && index + 4 < string.length() && string.charAt(index + 4) == '@') {
				int textColor = getColorByName(string.substring(index + 1, index + 4));
				if (textColor != -1) {
					color = textColor;
				}
				index += 4;
			} else {
				char c = string.charAt(index);
				if (c != ' ') {
					drawAlphaCharacter(characterPixels[c], x + characterOffsetX[c] + 1, y + characterOffsetY[c] + 1, 192, characterWidth[c], characterHeight[c], 0);
					drawAlphaCharacter(characterPixels[c], x + characterOffsetX[c], y + characterOffsetY[c], opacity, characterWidth[c], characterHeight[c], color);
				}
				x += characterScreenWidth[c];
				if ((myRandom.nextInt() & 3) == 0) {
					x++;
				}
			}
		}
	}

	private int getColorByName(String color) {
		if (color.equals("red")) {
			return 0xff0000;
		}
		if (color.equals("gre")) {
			return 65280;
		}
		if (color.equals("blu")) {
			return 255;
		}
		if (color.equals("yel")) {
			return 0xffff00;
		}
		if (color.equals("cya")) {
			return 65535;
		}
		if (color.equals("mag")) {
			return 0xff00ff;
		}
		if (color.equals("whi")) {
			return 0xffffff;
		}
		if (color.equals("bla")) {
			return 0;
		}
		if (color.equals("lre")) {
			return 0xff9040;
		}
		if (color.equals("dre")) {
			return 0x800000;
		}
		if (color.equals("dbl")) {
			return 128;
		}
		if (color.equals("or1")) {
			return 0xffb000;
		}
		if (color.equals("or2")) {
			return 0xff7000;
		}
		if (color.equals("or3")) {
			return 0xff3000;
		}
		if (color.equals("gr1")) {
			return 0xc0ff00;
		}
		if (color.equals("gr2")) {
			return 0x80ff00;
		}
		if (color.equals("gr3")) {
			return 0x40ff00;
		}
		if (color.equals("str")) {
			strikeThrough = true;
		}
		if (color.equals("end")) {
			strikeThrough = false;
		}
		return -1;
	}

	private void drawCharacter(byte charPixels[], int x, int y, int width, int height, int color) {
		int offset = x + y * RSDrawingArea.width;
		int k1 = RSDrawingArea.width - width;
		int l1 = 0;
		int pixel = 0;
		if (y < RSDrawingArea.startY) {
			int offsetY = RSDrawingArea.startY - y;
			height -= offsetY;
			y = RSDrawingArea.startY;
			pixel += offsetY * width;
			offset += offsetY * RSDrawingArea.width;
		}
		if (y + height >= RSDrawingArea.endY)
			height -= ((y + height) - RSDrawingArea.endY) + 1;
		if (x < RSDrawingArea.startX) {
			int offsetX = RSDrawingArea.startX - x;
			width -= offsetX;
			x = RSDrawingArea.startX;
			pixel += offsetX;
			offset += offsetX;
			l1 += offsetX;
			k1 += offsetX;
		}
		if (x + width >= RSDrawingArea.endX) {
			int l2 = ((x + width) - RSDrawingArea.endX) + 1;
			width -= l2;
			l1 += l2;
			k1 += l2;
		}
		if (!(width <= 0 || height <= 0)) {
			createPixels(RSDrawingArea.pixels, charPixels, color, pixel, offset, width, height, k1, l1);
		}
	}

	private void createPixels(int drawingAreaPixels[], byte charPixels[], int color, int pixel, int offset, int width, int height, int unknown1, int unknown2) {
		int l1 = -(width >> 2);
		width = -(width & 3);
		for (int i2 = -height; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				if (charPixels[pixel++] != 0) {
					drawingAreaPixels[offset++] = color;
				} else {
					offset++;
				}
				if (charPixels[pixel++] != 0) {
					drawingAreaPixels[offset++] = color;
				} else {
					offset++;
				}
				if (charPixels[pixel++] != 0) {
					drawingAreaPixels[offset++] = color;
				} else {
					offset++;
				}
				if (charPixels[pixel++] != 0) {
					drawingAreaPixels[offset++] = color;
				} else {
					offset++;
				}
			}
			for (int k2 = width; k2 < 0; k2++) {
				if (charPixels[pixel++] != 0) {
					drawingAreaPixels[offset++] = color;
				} else {
					offset++;
				}
			}
			offset += unknown1;
			pixel += unknown2;
		}
	}

	private void drawAlphaCharacter(byte pixels[], int x, int y, int alpha, int k, int i1, int j1) {
		int offset = x + y * RSDrawingArea.width;
		int l1 = RSDrawingArea.width - k;
		int i2 = 0;
		int j2 = 0;
		if (y < RSDrawingArea.startY) {
			int k2 = RSDrawingArea.startY - y;
			i1 -= k2;
			y = RSDrawingArea.startY;
			j2 += k2 * k;
			offset += k2 * RSDrawingArea.width;
		}
		if (y + i1 >= RSDrawingArea.endY)
			i1 -= ((y + i1) - RSDrawingArea.endY) + 1;
		if (x < RSDrawingArea.startX) {
			int l2 = RSDrawingArea.startX - x;
			k -= l2;
			x = RSDrawingArea.startX;
			j2 += l2;
			offset += l2;
			i2 += l2;
			l1 += l2;
		}
		if (x + k >= RSDrawingArea.endX) {
			int i3 = ((x + k) - RSDrawingArea.endX) + 1;
			k -= i3;
			i2 += i3;
			l1 += i3;
		}
		if (k <= 0 || i1 <= 0)
			return;
		createAlphaPixels(pixels, i1, offset, RSDrawingArea.pixels, j2, k, i2, l1, j1, alpha);
	}

	private void createAlphaPixels(byte pixels[], int i, int j, int drawingAreaPixels[], int l, int i1, int j1, int k1, int l1, int alpha) {
		l1 = ((l1 & 0xff00ff) * alpha & 0xff00ff00) + ((l1 & 0xff00) * alpha & 0xff0000) >> 8;
		alpha = 256 - alpha;
		for (int j2 = -i; j2 < 0; j2++) {
			for (int k2 = -i1; k2 < 0; k2++) {
				if (pixels[l++] != 0) {
					int l2 = drawingAreaPixels[j];
					drawingAreaPixels[j++] = (((l2 & 0xff00ff) * alpha & 0xff00ff00) + ((l2 & 0xff00) * alpha & 0xff0000) >> 8) + l1;
				} else {
					j++;
				}
			}
			j += k1;
			l += j1;
		}
	}

	private final byte[][] characterPixels;
	private final int[] characterWidth;
	private final int[] characterHeight;
	private final int[] characterOffsetX;
	private final int[] characterOffsetY;
	private final int[] characterScreenWidth;
	public int baseHeight;
	private final Random myRandom;
	private boolean strikeThrough;
}
