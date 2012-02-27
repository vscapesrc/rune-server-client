package rs2.config;

import java.awt.Color;

import rs2.JagexBuffer;
import rs2.cache.JagexArchive;
import rs2.constants.Constants;

public class Floor {

	public static void unpackConfig(JagexArchive archive) {
		JagexBuffer buffer = new JagexBuffer(archive.getData("flo.dat"));
		int length = buffer.getUnsignedShort();
		if (cache == null) {
			cache = new Floor[length];
		}
		for (int index = 0; index < length; index++) {
			if (cache[index] == null) {
				cache[index] = new Floor();
			}
			cache[index].readValues(buffer);
		}
	}

	private void readValues(JagexBuffer buffer) {
		do {
			int opcode = buffer.getUnsignedByte();
			if (opcode == 0) {
				return;
			} else if (opcode == 1) {
				terrainColor = new Color(buffer.get3Bytes());
				hslToRgb(getTerrainColor());
			} else if (opcode == 2) {
				textureId = buffer.getUnsignedByte();
			} else if (opcode == 3) {
			} else if (opcode == 5) {
				occlude = false;
			} else if (opcode == 6) {
				name = buffer.getString();
			} else if (opcode == 7) {
				int hue = this.hue;
				int saturation = this.saturation;
				int lightness = this.lightness;
				int hueVar = this.hueVar;
				minimapColor = new Color(buffer.get3Bytes());
				hslToRgb(getMinimapColor());
				this.hue = hue;
				this.saturation = saturation;
				this.lightness = lightness;
				this.hueVar = hueVar;
				hueDivider = hueVar;
			} else {
				System.out.println("Error unrecognised config code: " + opcode);
			}
		} while (true);
	}

	private void hslToRgb(int color) {
		double r = (double) (color >> 16 & 0xff) / 256D;
		double g = (double) (color >> 8 & 0xff) / 256D;
		double b = (double) (color & 0xff) / 256D;
		double red_val1 = r;
		if (g < red_val1) {
			red_val1 = g;
		}
		if (b < red_val1) {
			red_val1 = b;
		}
		double red_val2 = r;
		if (g > red_val2) {
			red_val2 = g;
		}
		if (b > red_val2) {
			red_val2 = b;
		}
		double hueCalc = 0.0D;
		double satCalc = 0.0D;
		double lightCalc = (red_val1 + red_val2) / 2D;
		if (red_val1 != red_val2) {
			if (lightCalc < 0.5D) {
				satCalc = (red_val2 - red_val1) / (red_val2 + red_val1);
			}
			if (lightCalc >= 0.5D) {
				satCalc = (red_val2 - red_val1) / (2D - red_val2 - red_val1);
			}
			if (r == red_val2) {
				hueCalc = (g - b) / (red_val2 - red_val1);
			} else if (g == red_val2) {
				hueCalc = 2D + (b - r) / (red_val2 - red_val1);
			} else if (b == red_val2) {
				hueCalc = 4D + (r - g) / (red_val2 - red_val1);
			}
		}
		hueCalc /= 6D;
		hue = (int) (hueCalc * 256D);
		saturation = (int) (satCalc * 256D);
		lightness = (int) (lightCalc * 256D);
		if (saturation < 0) {
			saturation = 0;
		} else if (saturation > 255) {
			saturation = 255;
		}
		if (lightness < 0) {
			lightness = 0;
		} else if (lightness > 255) {
			lightness = 255;
		}
		if (lightCalc > 0.5D) {
			hueDivider = (int) ((1.0D - lightCalc) * satCalc * 512D);
		} else {
			hueDivider = (int) (lightCalc * satCalc * 512D);
		}
		if (hueDivider < 1) {
			hueDivider = 1;
		}
		hueVar = (int) (hueCalc * (double) hueDivider);
		int hueOffset = hue;
		int saturationOffset = saturation;
		int lightnessOffset = lightness;
		if (Constants.BOT_RANDOMIZATION) {
			hueOffset = (hue + (int) (Math.random() * 16D)) - 8;
			if (hueOffset < 0) {
				hueOffset = 0;
			} else if (hueOffset > 255) {
				hueOffset = 255;
			}
			saturationOffset = (saturation + (int) (Math.random() * 48D)) - 24;
			if (saturationOffset < 0) {
				saturationOffset = 0;
			} else if (saturationOffset > 255) {
				saturationOffset = 255;
			}
			lightnessOffset = (lightness + (int) (Math.random() * 48D)) - 24;
			if (lightnessOffset < 0) {
				lightnessOffset = 0;
			} else if (lightnessOffset > 255) {
				lightnessOffset = 255;
			}
		}
		hslValue = getHSLValue(hueOffset, saturationOffset, lightnessOffset);
	}

	private int getHSLValue(int hue, int saturation, int lightness) {
		if (lightness > 179) {
			saturation /= 2;
		}
		if (lightness > 192) {
			saturation /= 2;
		}
		if (lightness > 217) {
			saturation /= 2;
		}
		if (lightness > 243) {
			saturation /= 2;
		}
		return (hue / 4 << 10) + (saturation / 32 << 7) + lightness / 2;
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
	public int hue;
	public int saturation;
	public int lightness;
	public int hueVar;
	public int hueDivider;
	public int hslValue;
	public String name;
}
