package rs2.graphics;

import rs2.cache.JagexArchive;

public final class Rasterizer extends RSDrawingArea {

	public static void clearCache() {
		shadowDecay = null;
		shadowDecay = null;
		SINE = null;
		COSINE = null;
		lineOffsets = null;
		textureImages = null;
		textureIsTransparent = null;
		averageTextureColor = null;
		texelArrayPool = null;
		texelCache = null;
		textureLastUsed = null;
		hslToRGB = null;
		texturePalettes = null;
	}

	public static void setDefaultBounds() {
		lineOffsets = new int[RSDrawingArea.height];
		for (int j = 0; j < RSDrawingArea.height; j++) {
			lineOffsets[j] = RSDrawingArea.width * j;
		}
		centerX = RSDrawingArea.width / 2;
		centerY = RSDrawingArea.height / 2;
	}

	public static void setBounds(int width, int height) {
		lineOffsets = new int[height];
		for (int l = 0; l < height; l++) {
			lineOffsets[l] = width * l;
		}
		centerX = width / 2;
		centerY = height / 2;
	}

	public static void clearTextureCache() {
		texelArrayPool = null;
		for (int j = 0; j < 50; j++) {
			texelCache[j] = null;
		}
	}

	public static void resetTextures() {
		if (texelArrayPool == null) {
			texelPoolPointer = 20;
			if (lowMem) {
				texelArrayPool = new int[texelPoolPointer][16384];
			} else {
				texelArrayPool = new int[texelPoolPointer][0x10000];
			}
			for (int index = 0; index < 50; index++) {
				texelCache[index] = null;
			}
		}
	}

	public static void unpack(JagexArchive archive) {
		textureCount = 0;
		for (int index = 0; index < 50; index++) {
			try {
				textureImages[index] = new IndexedImage(archive, String.valueOf(index), 0);
				if (lowMem && textureImages[index].anInt1456 == 128) {
					textureImages[index].method356();
				} else {
					textureImages[index].method357();
				}
				textureCount++;
			} catch (Exception _ex) {
			}
		}
	}

	public static int getAverageTextureColor(int id) {
		if (averageTextureColor[id] != 0) {
			return averageTextureColor[id];
		}
		int r = 0;
		int g = 0;
		int b = 0;
		int colorCount = texturePalettes[id].length;
		for (int index = 0; index < colorCount; index++) {
			r += texturePalettes[id][index] >> 16 & 0xff;
			g += texturePalettes[id][index] >> 8 & 0xff;
			b += texturePalettes[id][index] & 0xff;
		}

		int rgb = (r / colorCount << 16) + (g / colorCount << 8) + b / colorCount;
		rgb = adjustBrightness(rgb, 1.3999999999999999D);
		if (rgb == 0) {
			rgb = 1;
		}
		averageTextureColor[id] = rgb;
		return rgb;
	}

	public static void resetTexture(int id) {
		if (texelCache[id] == null) {
			return;
		}
		texelArrayPool[texelPoolPointer++] = texelCache[id];
		texelCache[id] = null;
	}

	private static int[] getTexturePixels(int id) {
		textureLastUsed[id] = textureGetCount++;
		if (texelCache[id] != null) {
			return texelCache[id];
		}
		int texels[];
		if (texelPoolPointer > 0) {
			texels = texelArrayPool[--texelPoolPointer];
			texelArrayPool[texelPoolPointer] = null;
		} else {
			int lastUsed = 0;
			int target = -1;
			for (int index = 0; index < textureCount; index++) {
				if (texelCache[index] != null && (textureLastUsed[index] < lastUsed || target == -1)) {
					lastUsed = textureLastUsed[index];
					target = index;
				}
			}
			texels = texelCache[target];
			texelCache[target] = null;
		}
		texelCache[id] = texels;
		IndexedImage image = textureImages[id];
		int texturePalette[] = texturePalettes[id];
		if (lowMem) {
			textureIsTransparent[id] = false;
			for (int index = 0; index < 4096; index++) {
				int texel = texels[index] = texturePalette[image.myPixels[index]] & 0xf8f8ff;
				if (texel == 0) {
					textureIsTransparent[id] = true;
				}
				texels[4096 + index] = texel - (texel >>> 3) & 0xf8f8ff;
				texels[8192 + index] = texel - (texel >>> 2) & 0xf8f8ff;
				texels[12288 + index] = texel - (texel >>> 2) - (texel >>> 3) & 0xf8f8ff;
			}

		} else {
			if (image.myWidth == 64) {
				for (int y = 0; y < 128; y++) {
					for (int x = 0; x < 128; x++) {
						texels[x + (y << 7)] = texturePalette[image.myPixels[(x >> 1) + ((y >> 1) << 6)]];
					}
				}
			} else {
				for (int index = 0; index < 16384; index++) {
					texels[index] = texturePalette[image.myPixels[index]];
				}
			}
			textureIsTransparent[id] = false;
			for (int index = 0; index < 16384; index++) {
				texels[index] &= 0xf8f8ff;
				int texel = texels[index];
				if (texel == 0) {
					textureIsTransparent[id] = true;
				}
				texels[16384 + index] = texel - (texel >>> 3) & 0xf8f8ff;
				texels[32768 + index] = texel - (texel >>> 2) & 0xf8f8ff;
				texels[49152 + index] = texel - (texel >>> 2) - (texel >>> 3) & 0xf8f8ff;
			}

		}
		return texels;
	}

	public static void calculatePalette(double brightness) {
		brightness += Math.random() * 0.029999999999999999D - 0.014999999999999999D;
		int hsl = 0;
		for (int k = 0; k < 512; k++) {
			double d1 = (double) (k / 8) / 64D + 0.0078125D;
			double d2 = (double) (k & 7) / 8D + 0.0625D;
			for (int k1 = 0; k1 < 128; k1++) {
				double d3 = (double) k1 / 128D;
				double r = d3;
				double g = d3;
				double b = d3;
				if (d2 != 0.0D) {
					double d7;
					if (d3 < 0.5D)
						d7 = d3 * (1.0D + d2);
					else
						d7 = (d3 + d2) - d3 * d2;
					double d8 = 2D * d3 - d7;
					double d9 = d1 + 0.33333333333333331D;
					if (d9 > 1.0D)
						d9--;
					double d10 = d1;
					double d11 = d1 - 0.33333333333333331D;
					if (d11 < 0.0D)
						d11++;
					if (6D * d9 < 1.0D)
						r = d8 + (d7 - d8) * 6D * d9;
					else if (2D * d9 < 1.0D)
						r = d7;
					else if (3D * d9 < 2D)
						r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
					else
						r = d8;
					if (6D * d10 < 1.0D)
						g = d8 + (d7 - d8) * 6D * d10;
					else if (2D * d10 < 1.0D)
						g = d7;
					else if (3D * d10 < 2D)
						g = d8 + (d7 - d8) * (0.66666666666666663D - d10) * 6D;
					else
						g = d8;
					if (6D * d11 < 1.0D)
						b = d8 + (d7 - d8) * 6D * d11;
					else if (2D * d11 < 1.0D)
						b = d7;
					else if (3D * d11 < 2D)
						b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
					else
						b = d8;
				}
				int byteR = (int) (r * 256D);
				int byteG = (int) (g * 256D);
				int byteB = (int) (b * 256D);
				int rgb = (byteR << 16) + (byteG << 8) + byteB;
				rgb = adjustBrightness(rgb, brightness);
				if (rgb == 0) {
					rgb = 1;
				}
				hslToRGB[hsl++] = rgb;
			}
		}
		for (int textureId = 0; textureId < 50; textureId++) {
			if (textureImages[textureId] != null) {
				int ai[] = textureImages[textureId].anIntArray1451;
				texturePalettes[textureId] = new int[ai.length];
				for (int colorIndex = 0; colorIndex < ai.length; colorIndex++) {
					texturePalettes[textureId][colorIndex] = adjustBrightness(ai[colorIndex], brightness);
					if ((texturePalettes[textureId][colorIndex] & 0xf8f8ff) == 0 && colorIndex != 0) {
						texturePalettes[textureId][colorIndex] = 1;
					}
				}
			}
		}
		for (int index = 0; index < 50; index++) {
			resetTexture(index);
		}
	}

	private static int adjustBrightness(int color, double intensity) {
		double r = (double) (color >> 16) / 256D;
		double g = (double) (color >> 8 & 0xff) / 256D;
		double b = (double) (color & 0xff) / 256D;
		r = Math.pow(r, intensity);
		g = Math.pow(g, intensity);
		b = Math.pow(b, intensity);
		int r_byte = (int) (r * 256D);
		int g_byte = (int) (g * 256D);
		int b_byte = (int) (b * 256D);
		return (r_byte << 16) + (g_byte << 8) + b_byte;
	}

	public static void drawShadedTriangle(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
		int j2 = 0;
		int k2 = 0;
		if (j != i) {
			j2 = (i1 - l << 16) / (j - i);
			k2 = (l1 - k1 << 15) / (j - i);
		}
		int l2 = 0;
		int i3 = 0;
		if (k != j) {
			l2 = (j1 - i1 << 16) / (k - j);
			i3 = (i2 - l1 << 15) / (k - j);
		}
		int j3 = 0;
		int k3 = 0;
		if (k != i) {
			j3 = (l - j1 << 16) / (i - k);
			k3 = (k1 - i2 << 15) / (i - k);
		}
		if (i <= j && i <= k) {
			if (i >= RSDrawingArea.endY)
				return;
			if (j > RSDrawingArea.endY)
				j = RSDrawingArea.endY;
			if (k > RSDrawingArea.endY)
				k = RSDrawingArea.endY;
			if (j < k) {
				j1 = l <<= 16;
				i2 = k1 <<= 15;
				if (i < 0) {
					j1 -= j3 * i;
					l -= j2 * i;
					i2 -= k3 * i;
					k1 -= k2 * i;
					i = 0;
				}
				i1 <<= 16;
				l1 <<= 15;
				if (j < 0) {
					i1 -= l2 * j;
					l1 -= i3 * j;
					j = 0;
				}
				if (i != j && j3 < j2 || i == j && j3 > l2) {
					k -= j;
					j -= i;
					for (i = lineOffsets[i]; --j >= 0; i += RSDrawingArea.width) {
						try {
							drawShadedLine(RSDrawingArea.pixels, i, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
						} catch (Exception e) {
						}
						j1 += j3;
						l += j2;
						i2 += k3;
						k1 += k2;
					}

					while (--k >= 0) {
						try {
							drawShadedLine(RSDrawingArea.pixels, i, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
						} catch (Exception e) {
						}
						j1 += j3;
						i1 += l2;
						i2 += k3;
						l1 += i3;
						i += RSDrawingArea.width;
					}
					return;
				}
				k -= j;
				j -= i;
				for (i = lineOffsets[i]; --j >= 0; i += RSDrawingArea.width) {
					try {
						drawShadedLine(RSDrawingArea.pixels, i, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
					} catch (Exception e) {
					}
					j1 += j3;
					l += j2;
					i2 += k3;
					k1 += k2;
				}

				while (--k >= 0) {
					try {
						drawShadedLine(RSDrawingArea.pixels, i, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
					} catch (Exception e) {
					}
					j1 += j3;
					i1 += l2;
					i2 += k3;
					l1 += i3;
					i += RSDrawingArea.width;
				}
				return;
			}
			i1 = l <<= 16;
			l1 = k1 <<= 15;
			if (i < 0) {
				i1 -= j3 * i;
				l -= j2 * i;
				l1 -= k3 * i;
				k1 -= k2 * i;
				i = 0;
			}
			j1 <<= 16;
			i2 <<= 15;
			if (k < 0) {
				j1 -= l2 * k;
				i2 -= i3 * k;
				k = 0;
			}
			if (i != k && j3 < j2 || i == k && l2 > j2) {
				j -= k;
				k -= i;
				for (i = lineOffsets[i]; --k >= 0; i += RSDrawingArea.width) {
					try {
						drawShadedLine(RSDrawingArea.pixels, i, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
					} catch (Exception e) {
					}
					i1 += j3;
					l += j2;
					l1 += k3;
					k1 += k2;
				}

				while (--j >= 0) {
					try {
						drawShadedLine(RSDrawingArea.pixels, i, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
					} catch (Exception e) {
					}
					j1 += l2;
					l += j2;
					i2 += i3;
					k1 += k2;
					i += RSDrawingArea.width;
				}
				return;
			}
			j -= k;
			k -= i;
			for (i = lineOffsets[i]; --k >= 0; i += RSDrawingArea.width) {
				try {
					drawShadedLine(RSDrawingArea.pixels, i, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
				} catch (Exception e) {
				}
				i1 += j3;
				l += j2;
				l1 += k3;
				k1 += k2;
			}

			while (--j >= 0) {
				try {
					drawShadedLine(RSDrawingArea.pixels, i, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
				} catch (Exception e) {
				}
				j1 += l2;
				l += j2;
				i2 += i3;
				k1 += k2;
				i += RSDrawingArea.width;
			}
			return;
		}
		if (j <= k) {
			if (j >= RSDrawingArea.endY)
				return;
			if (k > RSDrawingArea.endY)
				k = RSDrawingArea.endY;
			if (i > RSDrawingArea.endY)
				i = RSDrawingArea.endY;
			if (k < i) {
				l = i1 <<= 16;
				k1 = l1 <<= 15;
				if (j < 0) {
					l -= j2 * j;
					i1 -= l2 * j;
					k1 -= k2 * j;
					l1 -= i3 * j;
					j = 0;
				}
				j1 <<= 16;
				i2 <<= 15;
				if (k < 0) {
					j1 -= j3 * k;
					i2 -= k3 * k;
					k = 0;
				}
				if (j != k && j2 < l2 || j == k && j2 > j3) {
					i -= k;
					k -= j;
					for (j = lineOffsets[j]; --k >= 0; j += RSDrawingArea.width) {
						try {
							drawShadedLine(RSDrawingArea.pixels, j, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
						} catch (Exception e) {
						}
						l += j2;
						i1 += l2;
						k1 += k2;
						l1 += i3;
					}

					while (--i >= 0) {
						try {
							drawShadedLine(RSDrawingArea.pixels, j, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
						} catch (Exception e) {
						}
						l += j2;
						j1 += j3;
						k1 += k2;
						i2 += k3;
						j += RSDrawingArea.width;
					}
					return;
				}
				i -= k;
				k -= j;
				for (j = lineOffsets[j]; --k >= 0; j += RSDrawingArea.width) {
					try {
						drawShadedLine(RSDrawingArea.pixels, j, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
					} catch (Exception e) {
					}
					l += j2;
					i1 += l2;
					k1 += k2;
					l1 += i3;
				}

				while (--i >= 0) {
					try {
						drawShadedLine(RSDrawingArea.pixels, j, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
					} catch (Exception e) {
					}
					l += j2;
					j1 += j3;
					k1 += k2;
					i2 += k3;
					j += RSDrawingArea.width;
				}
				return;
			}
			j1 = i1 <<= 16;
			i2 = l1 <<= 15;
			if (j < 0) {
				j1 -= j2 * j;
				i1 -= l2 * j;
				i2 -= k2 * j;
				l1 -= i3 * j;
				j = 0;
			}
			l <<= 16;
			k1 <<= 15;
			if (i < 0) {
				l -= j3 * i;
				k1 -= k3 * i;
				i = 0;
			}
			if (j2 < l2) {
				k -= i;
				i -= j;
				for (j = lineOffsets[j]; --i >= 0; j += RSDrawingArea.width) {
					try {
						drawShadedLine(RSDrawingArea.pixels, j, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
					} catch (Exception e) {
					}
					j1 += j2;
					i1 += l2;
					i2 += k2;
					l1 += i3;
				}

				while (--k >= 0) {
					try {
						drawShadedLine(RSDrawingArea.pixels, j, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
					} catch (Exception e) {
					}
					l += j3;
					i1 += l2;
					k1 += k3;
					l1 += i3;
					j += RSDrawingArea.width;
				}
				return;
			}
			k -= i;
			i -= j;
			for (j = lineOffsets[j]; --i >= 0; j += RSDrawingArea.width) {
				try {
					drawShadedLine(RSDrawingArea.pixels, j, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
				} catch (Exception e) {
				}
				j1 += j2;
				i1 += l2;
				i2 += k2;
				l1 += i3;
			}

			while (--k >= 0) {
				try {
					drawShadedLine(RSDrawingArea.pixels, j, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
				} catch (Exception e) {
				}
				l += j3;
				i1 += l2;
				k1 += k3;
				l1 += i3;
				j += RSDrawingArea.width;
			}
			return;
		}
		if (k >= RSDrawingArea.endY)
			return;
		if (i > RSDrawingArea.endY)
			i = RSDrawingArea.endY;
		if (j > RSDrawingArea.endY)
			j = RSDrawingArea.endY;
		if (i < j) {
			i1 = j1 <<= 16;
			l1 = i2 <<= 15;
			if (k < 0) {
				i1 -= l2 * k;
				j1 -= j3 * k;
				l1 -= i3 * k;
				i2 -= k3 * k;
				k = 0;
			}
			l <<= 16;
			k1 <<= 15;
			if (i < 0) {
				l -= j2 * i;
				k1 -= k2 * i;
				i = 0;
			}
			if (l2 < j3) {
				j -= i;
				i -= k;
				for (k = lineOffsets[k]; --i >= 0; k += RSDrawingArea.width) {
					try {
						drawShadedLine(RSDrawingArea.pixels, k, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
					} catch (Exception e) {
					}
					i1 += l2;
					j1 += j3;
					l1 += i3;
					i2 += k3;
				}

				while (--j >= 0) {
					try {
						drawShadedLine(RSDrawingArea.pixels, k, i1 >> 16, l >> 16, l1 >> 7, k1 >> 7);
					} catch (Exception e) {
					}
					i1 += l2;
					l += j2;
					l1 += i3;
					k1 += k2;
					k += RSDrawingArea.width;
				}
				return;
			}
			j -= i;
			i -= k;
			for (k = lineOffsets[k]; --i >= 0; k += RSDrawingArea.width) {
				try {
					drawShadedLine(RSDrawingArea.pixels, k, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
				} catch (Exception e) {
				}
				i1 += l2;
				j1 += j3;
				l1 += i3;
				i2 += k3;
			}

			while (--j >= 0) {
				try {
					drawShadedLine(RSDrawingArea.pixels, k, l >> 16, i1 >> 16, k1 >> 7, l1 >> 7);
				} catch (Exception e) {
				}
				i1 += l2;
				l += j2;
				l1 += i3;
				k1 += k2;
				k += RSDrawingArea.width;
			}
			return;
		}
		l = j1 <<= 16;
		k1 = i2 <<= 15;
		if (k < 0) {
			l -= l2 * k;
			j1 -= j3 * k;
			k1 -= i3 * k;
			i2 -= k3 * k;
			k = 0;
		}
		i1 <<= 16;
		l1 <<= 15;
		if (j < 0) {
			i1 -= j2 * j;
			l1 -= k2 * j;
			j = 0;
		}
		if (l2 < j3) {
			i -= j;
			j -= k;
			for (k = lineOffsets[k]; --j >= 0; k += RSDrawingArea.width) {
				try {
					drawShadedLine(RSDrawingArea.pixels, k, l >> 16, j1 >> 16, k1 >> 7, i2 >> 7);
				} catch (Exception e) {
				}
				l += l2;
				j1 += j3;
				k1 += i3;
				i2 += k3;
			}

			while (--i >= 0) {
				try {
					drawShadedLine(RSDrawingArea.pixels, k, i1 >> 16, j1 >> 16, l1 >> 7, i2 >> 7);
				} catch (Exception e) {
				}
				i1 += j2;
				j1 += j3;
				l1 += k2;
				i2 += k3;
				k += RSDrawingArea.width;
			}
			return;
		}
		i -= j;
		j -= k;
		for (k = lineOffsets[k]; --j >= 0; k += RSDrawingArea.width) {
			try {
				drawShadedLine(RSDrawingArea.pixels, k, j1 >> 16, l >> 16, i2 >> 7, k1 >> 7);
			} catch (Exception e) {
			}
			l += l2;
			j1 += j3;
			k1 += i3;
			i2 += k3;
		}

		while (--i >= 0) {
			try {
				drawShadedLine(RSDrawingArea.pixels, k, j1 >> 16, i1 >> 16, i2 >> 7, l1 >> 7);
			} catch (Exception e) {
			}
			i1 += j2;
			j1 += j3;
			l1 += k2;
			i2 += k3;
			k += RSDrawingArea.width;
		}
	}

	private static void drawShadedLine(int dest[], int dest_off, int start_x, int end_x, int color_index, int grad) {
		int j;// was parameter
		int k;// was parameter
		if (notTextures) {
			int l1;
			if (restrictEdges) {
				if (end_x - start_x > 3)
					l1 = (grad - color_index) / (end_x - start_x);
				else
					l1 = 0;
				if (end_x > RSDrawingArea.centerX)
					end_x = RSDrawingArea.centerX;
				if (start_x < 0) {
					color_index -= start_x * l1;
					start_x = 0;
				}
				if (start_x >= end_x)
					return;
				dest_off += start_x;
				k = end_x - start_x >> 2;
				l1 <<= 2;
			} else {
				if (start_x >= end_x)
					return;
				dest_off += start_x;
				k = end_x - start_x >> 2;
				if (k > 0)
					l1 = (grad - color_index) * shadowDecay[k] >> 15;
				else
					l1 = 0;
			}
			if (alpha == 0) {
				while (--k >= 0) {
					j = hslToRGB[color_index >> 8];
					color_index += l1;
					dest[dest_off++] = j;
					dest[dest_off++] = j;
					dest[dest_off++] = j;
					dest[dest_off++] = j;
				}
				k = end_x - start_x & 3;
				if (k > 0) {
					j = hslToRGB[color_index >> 8];
					do
						dest[dest_off++] = j;
					while (--k > 0);
					return;
				}
			} else {
				int j2 = alpha;
				int l2 = 256 - alpha;
				while (--k >= 0) {
					j = hslToRGB[color_index >> 8];
					color_index += l1;
					j = ((j & 0xff00ff) * l2 >> 8 & 0xff00ff) + ((j & 0xff00) * l2 >> 8 & 0xff00);
					dest[dest_off++] = j + ((dest[dest_off] & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * j2 >> 8 & 0xff00);
					dest[dest_off++] = j + ((dest[dest_off] & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * j2 >> 8 & 0xff00);
					dest[dest_off++] = j + ((dest[dest_off] & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * j2 >> 8 & 0xff00);
					dest[dest_off++] = j + ((dest[dest_off] & 0xff00ff) * j2 >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * j2 >> 8 & 0xff00);
				}
				k = end_x - start_x & 3;
				if (k > 0) {
					j = hslToRGB[color_index >> 8];
					j = ((j & 0xff00ff) * l2 >> 8 & 0xff00ff)
							+ ((j & 0xff00) * l2 >> 8 & 0xff00);
					do
						dest[dest_off++] = j + ((dest[dest_off] & 0xff00ff) * j2 >> 8 & 0xff00ff)
								+ ((dest[dest_off] & 0xff00) * j2 >> 8 & 0xff00);
					while (--k > 0);
				}
			}
			return;
		}
		if (start_x >= end_x)
			return;
		int i2 = (grad - color_index) / (end_x - start_x);
		if (restrictEdges) {
			if (end_x > RSDrawingArea.centerX)
				end_x = RSDrawingArea.centerX;
			if (start_x < 0) {
				color_index -= start_x * i2;
				start_x = 0;
			}
			if (start_x >= end_x)
				return;
		}
		dest_off += start_x;
		k = end_x - start_x;
		if (alpha == 0) {
			do {
				dest[dest_off++] = hslToRGB[color_index >> 8];
				color_index += i2;
			} while (--k > 0);
			return;
		}
		int k2 = alpha;
		int i3 = 256 - alpha;
		do {
			j = hslToRGB[color_index >> 8];
			color_index += i2;
			j = ((j & 0xff00ff) * i3 >> 8 & 0xff00ff)
					+ ((j & 0xff00) * i3 >> 8 & 0xff00);
			dest[dest_off++] = j + ((dest[dest_off] & 0xff00ff) * k2 >> 8 & 0xff00ff)
					+ ((dest[dest_off] & 0xff00) * k2 >> 8 & 0xff00);
		} while (--k > 0);
	}

	public static void drawFlatTriangle(int i, int j, int k, int l, int i1, int j1,
			int k1) {
		int l1 = 0;
		if (j != i)
			l1 = (i1 - l << 16) / (j - i);
		int i2 = 0;
		if (k != j)
			i2 = (j1 - i1 << 16) / (k - j);
		int j2 = 0;
		if (k != i)
			j2 = (l - j1 << 16) / (i - k);
		if (i <= j && i <= k) {
			if (i >= RSDrawingArea.endY)
				return;
			if (j > RSDrawingArea.endY)
				j = RSDrawingArea.endY;
			if (k > RSDrawingArea.endY)
				k = RSDrawingArea.endY;
			if (j < k) {
				j1 = l <<= 16;
				if (i < 0) {
					j1 -= j2 * i;
					l -= l1 * i;
					i = 0;
				}
				i1 <<= 16;
				if (j < 0) {
					i1 -= i2 * j;
					j = 0;
				}
				if (i != j && j2 < l1 || i == j && j2 > i2) {
					k -= j;
					j -= i;
					for (i = lineOffsets[i]; --j >= 0; i += RSDrawingArea.width) {
						drawScanLine(RSDrawingArea.pixels, i, k1, j1 >> 16, l >> 16);
						j1 += j2;
						l += l1;
					}

					while (--k >= 0) {
						drawScanLine(RSDrawingArea.pixels, i, k1, j1 >> 16, i1 >> 16);
						j1 += j2;
						i1 += i2;
						i += RSDrawingArea.width;
					}
					return;
				}
				k -= j;
				j -= i;
				for (i = lineOffsets[i]; --j >= 0; i += RSDrawingArea.width) {
					drawScanLine(RSDrawingArea.pixels, i, k1, l >> 16, j1 >> 16);
					j1 += j2;
					l += l1;
				}

				while (--k >= 0) {
					drawScanLine(RSDrawingArea.pixels, i, k1, i1 >> 16, j1 >> 16);
					j1 += j2;
					i1 += i2;
					i += RSDrawingArea.width;
				}
				return;
			}
			i1 = l <<= 16;
			if (i < 0) {
				i1 -= j2 * i;
				l -= l1 * i;
				i = 0;
			}
			j1 <<= 16;
			if (k < 0) {
				j1 -= i2 * k;
				k = 0;
			}
			if (i != k && j2 < l1 || i == k && i2 > l1) {
				j -= k;
				k -= i;
				for (i = lineOffsets[i]; --k >= 0; i += RSDrawingArea.width) {
					drawScanLine(RSDrawingArea.pixels, i, k1, i1 >> 16, l >> 16);
					i1 += j2;
					l += l1;
				}

				while (--j >= 0) {
					drawScanLine(RSDrawingArea.pixels, i, k1, j1 >> 16, l >> 16);
					j1 += i2;
					l += l1;
					i += RSDrawingArea.width;
				}
				return;
			}
			j -= k;
			k -= i;
			for (i = lineOffsets[i]; --k >= 0; i += RSDrawingArea.width) {
				drawScanLine(RSDrawingArea.pixels, i, k1, l >> 16, i1 >> 16);
				i1 += j2;
				l += l1;
			}

			while (--j >= 0) {
				drawScanLine(RSDrawingArea.pixels, i, k1, l >> 16, j1 >> 16);
				j1 += i2;
				l += l1;
				i += RSDrawingArea.width;
			}
			return;
		}
		if (j <= k) {
			if (j >= RSDrawingArea.endY)
				return;
			if (k > RSDrawingArea.endY)
				k = RSDrawingArea.endY;
			if (i > RSDrawingArea.endY)
				i = RSDrawingArea.endY;
			if (k < i) {
				l = i1 <<= 16;
				if (j < 0) {
					l -= l1 * j;
					i1 -= i2 * j;
					j = 0;
				}
				j1 <<= 16;
				if (k < 0) {
					j1 -= j2 * k;
					k = 0;
				}
				if (j != k && l1 < i2 || j == k && l1 > j2) {
					i -= k;
					k -= j;
					for (j = lineOffsets[j]; --k >= 0; j += RSDrawingArea.width) {
						drawScanLine(RSDrawingArea.pixels, j, k1, l >> 16, i1 >> 16);
						l += l1;
						i1 += i2;
					}

					while (--i >= 0) {
						drawScanLine(RSDrawingArea.pixels, j, k1, l >> 16, j1 >> 16);
						l += l1;
						j1 += j2;
						j += RSDrawingArea.width;
					}
					return;
				}
				i -= k;
				k -= j;
				for (j = lineOffsets[j]; --k >= 0; j += RSDrawingArea.width) {
					drawScanLine(RSDrawingArea.pixels, j, k1, i1 >> 16, l >> 16);
					l += l1;
					i1 += i2;
				}

				while (--i >= 0) {
					drawScanLine(RSDrawingArea.pixels, j, k1, j1 >> 16, l >> 16);
					l += l1;
					j1 += j2;
					j += RSDrawingArea.width;
				}
				return;
			}
			j1 = i1 <<= 16;
			if (j < 0) {
				j1 -= l1 * j;
				i1 -= i2 * j;
				j = 0;
			}
			l <<= 16;
			if (i < 0) {
				l -= j2 * i;
				i = 0;
			}
			if (l1 < i2) {
				k -= i;
				i -= j;
				for (j = lineOffsets[j]; --i >= 0; j += RSDrawingArea.width) {
					drawScanLine(RSDrawingArea.pixels, j, k1, j1 >> 16, i1 >> 16);
					j1 += l1;
					i1 += i2;
				}

				while (--k >= 0) {
					drawScanLine(RSDrawingArea.pixels, j, k1, l >> 16, i1 >> 16);
					l += j2;
					i1 += i2;
					j += RSDrawingArea.width;
				}
				return;
			}
			k -= i;
			i -= j;
			for (j = lineOffsets[j]; --i >= 0; j += RSDrawingArea.width) {
				drawScanLine(RSDrawingArea.pixels, j, k1, i1 >> 16, j1 >> 16);
				j1 += l1;
				i1 += i2;
			}

			while (--k >= 0) {
				drawScanLine(RSDrawingArea.pixels, j, k1, i1 >> 16, l >> 16);
				l += j2;
				i1 += i2;
				j += RSDrawingArea.width;
			}
			return;
		}
		if (k >= RSDrawingArea.endY)
			return;
		if (i > RSDrawingArea.endY)
			i = RSDrawingArea.endY;
		if (j > RSDrawingArea.endY)
			j = RSDrawingArea.endY;
		if (i < j) {
			i1 = j1 <<= 16;
			if (k < 0) {
				i1 -= i2 * k;
				j1 -= j2 * k;
				k = 0;
			}
			l <<= 16;
			if (i < 0) {
				l -= l1 * i;
				i = 0;
			}
			if (i2 < j2) {
				j -= i;
				i -= k;
				for (k = lineOffsets[k]; --i >= 0; k += RSDrawingArea.width) {
					drawScanLine(RSDrawingArea.pixels, k, k1, i1 >> 16, j1 >> 16);
					i1 += i2;
					j1 += j2;
				}

				while (--j >= 0) {
					drawScanLine(RSDrawingArea.pixels, k, k1, i1 >> 16, l >> 16);
					i1 += i2;
					l += l1;
					k += RSDrawingArea.width;
				}
				return;
			}
			j -= i;
			i -= k;
			for (k = lineOffsets[k]; --i >= 0; k += RSDrawingArea.width) {
				drawScanLine(RSDrawingArea.pixels, k, k1, j1 >> 16, i1 >> 16);
				i1 += i2;
				j1 += j2;
			}

			while (--j >= 0) {
				drawScanLine(RSDrawingArea.pixels, k, k1, l >> 16, i1 >> 16);
				i1 += i2;
				l += l1;
				k += RSDrawingArea.width;
			}
			return;
		}
		l = j1 <<= 16;
		if (k < 0) {
			l -= i2 * k;
			j1 -= j2 * k;
			k = 0;
		}
		i1 <<= 16;
		if (j < 0) {
			i1 -= l1 * j;
			j = 0;
		}
		if (i2 < j2) {
			i -= j;
			j -= k;
			for (k = lineOffsets[k]; --j >= 0; k += RSDrawingArea.width) {
				drawScanLine(RSDrawingArea.pixels, k, k1, l >> 16, j1 >> 16);
				l += i2;
				j1 += j2;
			}

			while (--i >= 0) {
				drawScanLine(RSDrawingArea.pixels, k, k1, i1 >> 16, j1 >> 16);
				i1 += l1;
				j1 += j2;
				k += RSDrawingArea.width;
			}
			return;
		}
		i -= j;
		j -= k;
		for (k = lineOffsets[k]; --j >= 0; k += RSDrawingArea.width) {
			drawScanLine(RSDrawingArea.pixels, k, k1, j1 >> 16, l >> 16);
			l += i2;
			j1 += j2;
		}

		while (--i >= 0) {
			drawScanLine(RSDrawingArea.pixels, k, k1, j1 >> 16, i1 >> 16);
			i1 += l1;
			j1 += j2;
			k += RSDrawingArea.width;
		}
	}

	private static void drawScanLine(int ai[], int i, int j, int l, int i1) {
		int k;// was parameter
		if (restrictEdges) {
			if (i1 > RSDrawingArea.centerX)
				i1 = RSDrawingArea.centerX;
			if (l < 0)
				l = 0;
		}
		if (l >= i1)
			return;
		i += l;
		k = i1 - l >> 2;
		if (alpha == 0) {
			while (--k >= 0) {
				ai[i++] = j;
				ai[i++] = j;
				ai[i++] = j;
				ai[i++] = j;
			}
			for (k = i1 - l & 3; --k >= 0;)
				ai[i++] = j;

			return;
		}
		int j1 = alpha;
		int k1 = 256 - alpha;
		j = ((j & 0xff00ff) * k1 >> 8 & 0xff00ff)
				+ ((j & 0xff00) * k1 >> 8 & 0xff00);
		while (--k >= 0) {
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff)
					+ ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff)
					+ ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff)
					+ ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff)
					+ ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);
		}
		for (k = i1 - l & 3; --k >= 0;)
			ai[i++] = j + ((ai[i] & 0xff00ff) * j1 >> 8 & 0xff00ff)
					+ ((ai[i] & 0xff00) * j1 >> 8 & 0xff00);

	}

	public static void drawTexturedTriangle(int i, int j, int k, int l, int i1, int j1,
			int k1, int l1, int i2, int j2, int k2, int l2, int i3, int j3,
			int k3, int l3, int i4, int j4, int k4) {
		int ai[] = getTexturePixels(k4);
		Rasterizer.opaque = !textureIsTransparent[k4];
		k2 = j2 - k2;
		j3 = i3 - j3;
		i4 = l3 - i4;
		l2 -= j2;
		k3 -= i3;
		j4 -= l3;
		int l4 = l2 * i3 - k3 * j2 << 14;
		int i5 = k3 * l3 - j4 * i3 << 8;
		int j5 = j4 * j2 - l2 * l3 << 5;
		int k5 = k2 * i3 - j3 * j2 << 14;
		int l5 = j3 * l3 - i4 * i3 << 8;
		int i6 = i4 * j2 - k2 * l3 << 5;
		int j6 = j3 * l2 - k2 * k3 << 14;
		int k6 = i4 * k3 - j3 * j4 << 8;
		int l6 = k2 * j4 - i4 * l2 << 5;
		int i7 = 0;
		int j7 = 0;
		if (j != i) {
			i7 = (i1 - l << 16) / (j - i);
			j7 = (l1 - k1 << 16) / (j - i);
		}
		int k7 = 0;
		int l7 = 0;
		if (k != j) {
			k7 = (j1 - i1 << 16) / (k - j);
			l7 = (i2 - l1 << 16) / (k - j);
		}
		int i8 = 0;
		int j8 = 0;
		if (k != i) {
			i8 = (l - j1 << 16) / (i - k);
			j8 = (k1 - i2 << 16) / (i - k);
		}
		if (i <= j && i <= k) {
			if (i >= RSDrawingArea.endY)
				return;
			if (j > RSDrawingArea.endY)
				j = RSDrawingArea.endY;
			if (k > RSDrawingArea.endY)
				k = RSDrawingArea.endY;
			if (j < k) {
				j1 = l <<= 16;
				i2 = k1 <<= 16;
				if (i < 0) {
					j1 -= i8 * i;
					l -= i7 * i;
					i2 -= j8 * i;
					k1 -= j7 * i;
					i = 0;
				}
				i1 <<= 16;
				l1 <<= 16;
				if (j < 0) {
					i1 -= k7 * j;
					l1 -= l7 * j;
					j = 0;
				}
				int k8 = i - centerY;
				l4 += j5 * k8;
				k5 += i6 * k8;
				j6 += l6 * k8;
				if (i != j && i8 < i7 || i == j && i8 > k7) {
					k -= j;
					j -= i;
					i = lineOffsets[i];
					while (--j >= 0) {
						drawTexturedLine(RSDrawingArea.pixels, ai, i, j1 >> 16, l >> 16,
								i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
						j1 += i8;
						l += i7;
						i2 += j8;
						k1 += j7;
						i += RSDrawingArea.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					while (--k >= 0) {
						drawTexturedLine(RSDrawingArea.pixels, ai, i, j1 >> 16,
								i1 >> 16, i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5,
								k6);
						j1 += i8;
						i1 += k7;
						i2 += j8;
						l1 += l7;
						i += RSDrawingArea.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					return;
				}
				k -= j;
				j -= i;
				i = lineOffsets[i];
				while (--j >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, i, l >> 16, j1 >> 16,
							k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += i8;
					l += i7;
					i2 += j8;
					k1 += j7;
					i += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--k >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, i, i1 >> 16, j1 >> 16,
							l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += i8;
					i1 += k7;
					i2 += j8;
					l1 += l7;
					i += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			i1 = l <<= 16;
			l1 = k1 <<= 16;
			if (i < 0) {
				i1 -= i8 * i;
				l -= i7 * i;
				l1 -= j8 * i;
				k1 -= j7 * i;
				i = 0;
			}
			j1 <<= 16;
			i2 <<= 16;
			if (k < 0) {
				j1 -= k7 * k;
				i2 -= l7 * k;
				k = 0;
			}
			int l8 = i - centerY;
			l4 += j5 * l8;
			k5 += i6 * l8;
			j6 += l6 * l8;
			if (i != k && i8 < i7 || i == k && k7 > i7) {
				j -= k;
				k -= i;
				i = lineOffsets[i];
				while (--k >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, i, i1 >> 16, l >> 16,
							l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					i1 += i8;
					l += i7;
					l1 += j8;
					k1 += j7;
					i += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--j >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, i, j1 >> 16, l >> 16,
							i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += k7;
					l += i7;
					i2 += l7;
					k1 += j7;
					i += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			j -= k;
			k -= i;
			i = lineOffsets[i];
			while (--k >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, i, l >> 16, i1 >> 16,
						k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += i8;
				l += i7;
				l1 += j8;
				k1 += j7;
				i += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--j >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, i, l >> 16, j1 >> 16,
						k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				j1 += k7;
				l += i7;
				i2 += l7;
				k1 += j7;
				i += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		if (j <= k) {
			if (j >= RSDrawingArea.endY)
				return;
			if (k > RSDrawingArea.endY)
				k = RSDrawingArea.endY;
			if (i > RSDrawingArea.endY)
				i = RSDrawingArea.endY;
			if (k < i) {
				l = i1 <<= 16;
				k1 = l1 <<= 16;
				if (j < 0) {
					l -= i7 * j;
					i1 -= k7 * j;
					k1 -= j7 * j;
					l1 -= l7 * j;
					j = 0;
				}
				j1 <<= 16;
				i2 <<= 16;
				if (k < 0) {
					j1 -= i8 * k;
					i2 -= j8 * k;
					k = 0;
				}
				int i9 = j - centerY;
				l4 += j5 * i9;
				k5 += i6 * i9;
				j6 += l6 * i9;
				if (j != k && i7 < k7 || j == k && i7 > i8) {
					i -= k;
					k -= j;
					j = lineOffsets[j];
					while (--k >= 0) {
						drawTexturedLine(RSDrawingArea.pixels, ai, j, l >> 16, i1 >> 16,
								k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
						l += i7;
						i1 += k7;
						k1 += j7;
						l1 += l7;
						j += RSDrawingArea.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					while (--i >= 0) {
						drawTexturedLine(RSDrawingArea.pixels, ai, j, l >> 16, j1 >> 16,
								k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
						l += i7;
						j1 += i8;
						k1 += j7;
						i2 += j8;
						j += RSDrawingArea.width;
						l4 += j5;
						k5 += i6;
						j6 += l6;
					}
					return;
				}
				i -= k;
				k -= j;
				j = lineOffsets[j];
				while (--k >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, j, i1 >> 16, l >> 16,
							l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					l += i7;
					i1 += k7;
					k1 += j7;
					l1 += l7;
					j += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--i >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, j, j1 >> 16, l >> 16,
							i2 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					l += i7;
					j1 += i8;
					k1 += j7;
					i2 += j8;
					j += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			j1 = i1 <<= 16;
			i2 = l1 <<= 16;
			if (j < 0) {
				j1 -= i7 * j;
				i1 -= k7 * j;
				i2 -= j7 * j;
				l1 -= l7 * j;
				j = 0;
			}
			l <<= 16;
			k1 <<= 16;
			if (i < 0) {
				l -= i8 * i;
				k1 -= j8 * i;
				i = 0;
			}
			int j9 = j - centerY;
			l4 += j5 * j9;
			k5 += i6 * j9;
			j6 += l6 * j9;
			if (i7 < k7) {
				k -= i;
				i -= j;
				j = lineOffsets[j];
				while (--i >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, j, j1 >> 16, i1 >> 16,
							i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
					j1 += i7;
					i1 += k7;
					i2 += j7;
					l1 += l7;
					j += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--k >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, j, l >> 16, i1 >> 16,
							k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
					l += i8;
					i1 += k7;
					k1 += j8;
					l1 += l7;
					j += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			k -= i;
			i -= j;
			j = lineOffsets[j];
			while (--i >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, j, i1 >> 16, j1 >> 16,
						l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				j1 += i7;
				i1 += k7;
				i2 += j7;
				l1 += l7;
				j += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--k >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, j, i1 >> 16, l >> 16,
						l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
				l += i8;
				i1 += k7;
				k1 += j8;
				l1 += l7;
				j += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		if (k >= RSDrawingArea.endY)
			return;
		if (i > RSDrawingArea.endY)
			i = RSDrawingArea.endY;
		if (j > RSDrawingArea.endY)
			j = RSDrawingArea.endY;
		if (i < j) {
			i1 = j1 <<= 16;
			l1 = i2 <<= 16;
			if (k < 0) {
				i1 -= k7 * k;
				j1 -= i8 * k;
				l1 -= l7 * k;
				i2 -= j8 * k;
				k = 0;
			}
			l <<= 16;
			k1 <<= 16;
			if (i < 0) {
				l -= i7 * i;
				k1 -= j7 * i;
				i = 0;
			}
			int k9 = k - centerY;
			l4 += j5 * k9;
			k5 += i6 * k9;
			j6 += l6 * k9;
			if (k7 < i8) {
				j -= i;
				i -= k;
				k = lineOffsets[k];
				while (--i >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, k, i1 >> 16, j1 >> 16,
							l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
					i1 += k7;
					j1 += i8;
					l1 += l7;
					i2 += j8;
					k += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				while (--j >= 0) {
					drawTexturedLine(RSDrawingArea.pixels, ai, k, i1 >> 16, l >> 16,
							l1 >> 8, k1 >> 8, l4, k5, j6, i5, l5, k6);
					i1 += k7;
					l += i7;
					l1 += l7;
					k1 += j7;
					k += RSDrawingArea.width;
					l4 += j5;
					k5 += i6;
					j6 += l6;
				}
				return;
			}
			j -= i;
			i -= k;
			k = lineOffsets[k];
			while (--i >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, k, j1 >> 16, i1 >> 16,
						i2 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += k7;
				j1 += i8;
				l1 += l7;
				i2 += j8;
				k += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--j >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, k, l >> 16, i1 >> 16,
						k1 >> 8, l1 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += k7;
				l += i7;
				l1 += l7;
				k1 += j7;
				k += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		l = j1 <<= 16;
		k1 = i2 <<= 16;
		if (k < 0) {
			l -= k7 * k;
			j1 -= i8 * k;
			k1 -= l7 * k;
			i2 -= j8 * k;
			k = 0;
		}
		i1 <<= 16;
		l1 <<= 16;
		if (j < 0) {
			i1 -= i7 * j;
			l1 -= j7 * j;
			j = 0;
		}
		int l9 = k - centerY;
		l4 += j5 * l9;
		k5 += i6 * l9;
		j6 += l6 * l9;
		if (k7 < i8) {
			i -= j;
			j -= k;
			k = lineOffsets[k];
			while (--j >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, k, l >> 16, j1 >> 16,
						k1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				l += k7;
				j1 += i8;
				k1 += l7;
				i2 += j8;
				k += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			while (--i >= 0) {
				drawTexturedLine(RSDrawingArea.pixels, ai, k, i1 >> 16, j1 >> 16,
						l1 >> 8, i2 >> 8, l4, k5, j6, i5, l5, k6);
				i1 += i7;
				j1 += i8;
				l1 += j7;
				i2 += j8;
				k += RSDrawingArea.width;
				l4 += j5;
				k5 += i6;
				j6 += l6;
			}
			return;
		}
		i -= j;
		j -= k;
		k = lineOffsets[k];
		while (--j >= 0) {
			drawTexturedLine(RSDrawingArea.pixels, ai, k, j1 >> 16, l >> 16, i2 >> 8,
					k1 >> 8, l4, k5, j6, i5, l5, k6);
			l += k7;
			j1 += i8;
			k1 += l7;
			i2 += j8;
			k += RSDrawingArea.width;
			l4 += j5;
			k5 += i6;
			j6 += l6;
		}
		while (--i >= 0) {
			drawTexturedLine(RSDrawingArea.pixels, ai, k, j1 >> 16, i1 >> 16, i2 >> 8,
					l1 >> 8, l4, k5, j6, i5, l5, k6);
			i1 += i7;
			j1 += i8;
			l1 += j7;
			i2 += j8;
			k += RSDrawingArea.width;
			l4 += j5;
			k5 += i6;
			j6 += l6;
		}
	}

	private static void drawTexturedLine(int ai[], int ai1[], int k, int l, int i1,
			int j1, int k1, int l1, int i2, int j2, int k2, int l2, int i3) {
		int i = 0;// was parameter
		int j = 0;// was parameter
		if (l >= i1)
			return;
		int j3;
		int k3;
		if (restrictEdges) {
			j3 = (k1 - j1) / (i1 - l);
			if (i1 > RSDrawingArea.centerX)
				i1 = RSDrawingArea.centerX;
			if (l < 0) {
				j1 -= l * j3;
				l = 0;
			}
			if (l >= i1)
				return;
			k3 = i1 - l >> 3;
			j3 <<= 12;
			j1 <<= 9;
		} else {
			if (i1 - l > 7) {
				k3 = i1 - l >> 3;
				j3 = (k1 - j1) * shadowDecay[k3] >> 6;
			} else {
				k3 = 0;
				j3 = 0;
			}
			j1 <<= 9;
		}
		k += l;
		if (lowMem) {
			int i4 = 0;
			int k4 = 0;
			int k6 = l - centerX;
			l1 += (k2 >> 3) * k6;
			i2 += (l2 >> 3) * k6;
			j2 += (i3 >> 3) * k6;
			int i5 = j2 >> 12;
			if (i5 != 0) {
				i = l1 / i5;
				j = i2 / i5;
				if (i < 0)
					i = 0;
				else if (i > 4032)
					i = 4032;
			}
			l1 += k2;
			i2 += l2;
			j2 += i3;
			i5 = j2 >> 12;
			if (i5 != 0) {
				i4 = l1 / i5;
				k4 = i2 / i5;
				if (i4 < 7)
					i4 = 7;
				else if (i4 > 4032)
					i4 = 4032;
			}
			int i7 = i4 - i >> 3;
			int k7 = k4 - j >> 3;
			i += (j1 & 0x600000) >> 3;
			int i8 = j1 >> 23;
			if (opaque) {
				while (k3-- > 0) {
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i = i4;
					j = k4;
					l1 += k2;
					i2 += l2;
					j2 += i3;
					int j5 = j2 >> 12;
					if (j5 != 0) {
						i4 = l1 / j5;
						k4 = i2 / j5;
						if (i4 < 7)
							i4 = 7;
						else if (i4 > 4032)
							i4 = 4032;
					}
					i7 = i4 - i >> 3;
					k7 = k4 - j >> 3;
					j1 += j3;
					i += (j1 & 0x600000) >> 3;
					i8 = j1 >> 23;
				}
				for (k3 = i1 - l & 7; k3-- > 0;) {
					ai[k++] = ai1[(j & 0xfc0) + (i >> 6)] >>> i8;
					i += i7;
					j += k7;
				}

				return;
			}
			while (k3-- > 0) {
				int k8;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i += i7;
				j += k7;
				if ((k8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = k8;
				k++;
				i = i4;
				j = k4;
				l1 += k2;
				i2 += l2;
				j2 += i3;
				int k5 = j2 >> 12;
				if (k5 != 0) {
					i4 = l1 / k5;
					k4 = i2 / k5;
					if (i4 < 7)
						i4 = 7;
					else if (i4 > 4032)
						i4 = 4032;
				}
				i7 = i4 - i >> 3;
				k7 = k4 - j >> 3;
				j1 += j3;
				i += (j1 & 0x600000) >> 3;
				i8 = j1 >> 23;
			}
			for (k3 = i1 - l & 7; k3-- > 0;) {
				int l8;
				if ((l8 = ai1[(j & 0xfc0) + (i >> 6)] >>> i8) != 0)
					ai[k] = l8;
				k++;
				i += i7;
				j += k7;
			}

			return;
		}
		int j4 = 0;
		int l4 = 0;
		int l6 = l - centerX;
		l1 += (k2 >> 3) * l6;
		i2 += (l2 >> 3) * l6;
		j2 += (i3 >> 3) * l6;
		int l5 = j2 >> 14;
		if (l5 != 0) {
			i = l1 / l5;
			j = i2 / l5;
			if (i < 0)
				i = 0;
			else if (i > 16256)
				i = 16256;
		}
		l1 += k2;
		i2 += l2;
		j2 += i3;
		l5 = j2 >> 14;
		if (l5 != 0) {
			j4 = l1 / l5;
			l4 = i2 / l5;
			if (j4 < 7)
				j4 = 7;
			else if (j4 > 16256)
				j4 = 16256;
		}
		int j7 = j4 - i >> 3;
		int l7 = l4 - j >> 3;
		i += j1 & 0x600000;
		int j8 = j1 >> 23;
		if (opaque) {
			while (k3-- > 0) {
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i = j4;
				j = l4;
				l1 += k2;
				i2 += l2;
				j2 += i3;
				int i6 = j2 >> 14;
				if (i6 != 0) {
					j4 = l1 / i6;
					l4 = i2 / i6;
					if (j4 < 7)
						j4 = 7;
					else if (j4 > 16256)
						j4 = 16256;
				}
				j7 = j4 - i >> 3;
				l7 = l4 - j >> 3;
				j1 += j3;
				i += j1 & 0x600000;
				j8 = j1 >> 23;
			}
			for (k3 = i1 - l & 7; k3-- > 0;) {
				ai[k++] = ai1[(j & 0x3f80) + (i >> 7)] >>> j8;
				i += j7;
				j += l7;
			}

			return;
		}
		while (k3-- > 0) {
			int i9;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i += j7;
			j += l7;
			if ((i9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = i9;
			k++;
			i = j4;
			j = l4;
			l1 += k2;
			i2 += l2;
			j2 += i3;
			int j6 = j2 >> 14;
			if (j6 != 0) {
				j4 = l1 / j6;
				l4 = i2 / j6;
				if (j4 < 7)
					j4 = 7;
				else if (j4 > 16256)
					j4 = 16256;
			}
			j7 = j4 - i >> 3;
			l7 = l4 - j >> 3;
			j1 += j3;
			i += j1 & 0x600000;
			j8 = j1 >> 23;
		}
		for (int l3 = i1 - l & 7; l3-- > 0;) {
			int j9;
			if ((j9 = ai1[(j & 0x3f80) + (i >> 7)] >>> j8) != 0)
				ai[k] = j9;
			k++;
			i += j7;
			j += l7;
		}

	}

	public static boolean lowMem = true;
	public static boolean restrictEdges;
	private static boolean opaque;
	public static boolean notTextures = true;
	public static int alpha;
	public static int centerX;
	public static int centerY;
	private static int[] shadowDecay;
	public static final int[] anIntArray1469;
	public static int SINE[];
	public static int COSINE[];
	public static int lineOffsets[];
	private static int textureCount;
	public static IndexedImage textureImages[] = new IndexedImage[50];
	private static boolean[] textureIsTransparent = new boolean[50];
	private static int[] averageTextureColor = new int[50];
	private static int texelPoolPointer;
	private static int[][] texelArrayPool;
	private static int[][] texelCache = new int[50][];
	public static int textureLastUsed[] = new int[50];
	public static int textureGetCount;
	public static int hslToRGB[] = new int[0x10000];
	private static int[][] texturePalettes = new int[50][];

	static {
		shadowDecay = new int[512];
		anIntArray1469 = new int[2048];
		SINE = new int[2048];
		COSINE = new int[2048];
		for (int i = 1; i < 512; i++) {
			shadowDecay[i] = 32768 / i;
		}
		for (int j = 1; j < 2048; j++) {
			anIntArray1469[j] = 0x10000 / j;
		}
		for (int k = 0; k < 2048; k++) {
			SINE[k] = (int) (65536D * Math.sin((double) k * 0.0030679614999999999D));
			COSINE[k] = (int) (65536D * Math.cos((double) k * 0.0030679614999999999D));
		}

	}
}
