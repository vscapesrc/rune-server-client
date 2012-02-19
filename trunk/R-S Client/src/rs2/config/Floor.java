package rs2.config;

import java.awt.Color;

import rs2.JagexBuffer;
import rs2.cache.JagexArchive;

public class Floor {

	public static void unpackConfig(JagexArchive streamLoader) {
		JagexBuffer stream = new JagexBuffer(streamLoader.getData("flo.dat"));
		int cacheSize = stream.getUnsignedShort();
		if (cache == null)
			cache = new Floor[cacheSize];
		for (int j = 0; j < cacheSize; j++) {
			if (cache[j] == null)
				cache[j] = new Floor();
			cache[j].readValues(stream);
		}

	}

	private void readValues(JagexBuffer stream) {
		do {
			int i = stream.getUnsignedByte();
			if (i == 0) {
				return;
			} else if (i == 1) {
				terrainColor = new Color(stream.get3Bytes());
				method262(getTerrainColor());
			} else if (i == 2) {
				textureId = stream.getUnsignedByte();
			} else if (i == 3) {
			} else if (i == 5) {
				occlude = false;
			} else if (i == 6) {
				name = stream.getString();
			} else if (i == 7) {
				int j = anInt394;
				int k = saturation;
				int l = lightness;
				int i1 = anInt397;
				minimapColor = new Color(stream.get3Bytes());
				method262(getMinimapColor());
				anInt394 = j;
				saturation = k;
				lightness = l;
				anInt397 = i1;
				anInt398 = i1;
			} else {
				System.out.println("Error unrecognised config code: " + i);
			}
		} while (true);
	}

	private void method262(int color) {
		double r = (double) (color >> 16 & 0xff) / 256D;
		double g = (double) (color >> 8 & 0xff) / 256D;
		double b = (double) (color & 0xff) / 256D;
		double d3 = r;
		if (g < d3) {
			d3 = g;
		}
		if (b < d3) {
			d3 = b;
		}
		double d4 = r;
		if (g > d4) {
			d4 = g;
		}
		if (b > d4) {
			d4 = b;
		}
		double d5 = 0.0D;
		double d6 = 0.0D;
		double d7 = (d3 + d4) / 2D;
		if (d3 != d4) {
			if (d7 < 0.5D)
				d6 = (d4 - d3) / (d4 + d3);
			if (d7 >= 0.5D)
				d6 = (d4 - d3) / (2D - d4 - d3);
			if (r == d4)
				d5 = (g - b) / (d4 - d3);
			else if (g == d4)
				d5 = 2D + (b - r) / (d4 - d3);
			else if (b == d4)
				d5 = 4D + (r - g) / (d4 - d3);
		}
		d5 /= 6D;
		anInt394 = (int) (d5 * 256D);
		saturation = (int) (d6 * 256D);
		lightness = (int) (d7 * 256D);
		if (saturation < 0)
			saturation = 0;
		else if (saturation > 255)
			saturation = 255;
		if (lightness < 0)
			lightness = 0;
		else if (lightness > 255)
			lightness = 255;
		if (d7 > 0.5D)
			anInt398 = (int) ((1.0D - d7) * d6 * 512D);
		else
			anInt398 = (int) (d7 * d6 * 512D);
		if (anInt398 < 1)
			anInt398 = 1;
		anInt397 = (int) (d5 * (double) anInt398);
		int k = (anInt394 + (int) (Math.random() * 16D)) - 8;
		if (k < 0)
			k = 0;
		else if (k > 255)
			k = 255;
		int l = (saturation + (int) (Math.random() * 48D)) - 24;
		if (l < 0)
			l = 0;
		else if (l > 255)
			l = 255;
		int i1 = (lightness + (int) (Math.random() * 48D)) - 24;
		if (i1 < 0)
			i1 = 0;
		else if (i1 > 255)
			i1 = 255;
		anInt399 = method263(k, l, i1);
	}

	private int method263(int i, int j, int k) {
		if (k > 179)
			j /= 2;
		if (k > 192)
			j /= 2;
		if (k > 217)
			j /= 2;
		if (k > 243)
			j /= 2;
		return (i / 4 << 10) + (j / 32 << 7) + k / 2;
	}

	/**
	 * Returns the total amount of floor data.
	 * @return
	 */
	public static int getCount() {
		return cache.length;
	}

	/**
	 * Returns the terrain color in terms of RGB.
	 * @return
	 */
	public int getTerrainColor() {
		return terrainColor.getRGB();
	}

	/**
	 * Returns the minimap color in terms of RGB.
	 * @return
	 */
	public int getMinimapColor() {
		return minimapColor.getRGB();
	}

	private Floor() {
		textureId = -1;
		occlude = true;
	}

	public static Floor cache[];
	public Color terrainColor;
	public Color minimapColor;
	public int textureId;
	public boolean occlude;
	public int anInt394;
	public int saturation;
	public int lightness;
	public int anInt397;
	public int anInt398;
	public int anInt399;
	public String name;
}
