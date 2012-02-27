package rs2.world.render;
import rs2.Animable;
import rs2.JagexBuffer;
import rs2.Model;
import rs2.config.Floor;
import rs2.config.ObjectDefinitions;
import rs2.constants.Constants;
import rs2.graphics.Rasterizer;
import rs2.resource.ResourceProvider;
import rs2.util.MapUtils;
import rs2.world.ObjectOnTile;
import rs2.world.tile.TileSetting;

public final class MapRegion {

	public MapRegion(byte tileSettings[][][], int heightMap[][][]) {
		setZ = 99;
		xMapSize = 104;
		yMapSize = 104;
		this.heightMap = heightMap;
		this.tileSettings = tileSettings;
		underlay = new byte[4][xMapSize][yMapSize];
		overlay = new byte[4][xMapSize][yMapSize];
		tileShape = new byte[4][xMapSize][yMapSize];
		tileShapeRotation = new byte[4][xMapSize][yMapSize];
		tileCullingBitmap = new int[4][xMapSize + 1][yMapSize + 1];
		objectShadowData = new byte[4][xMapSize + 1][yMapSize + 1];
		tileLightness = new int[xMapSize + 1][yMapSize + 1];
		hue = new int[yMapSize];
		saturation = new int[yMapSize];
		lightness = new int[yMapSize];
		hueDivider = new int[yMapSize];
		colorCount = new int[yMapSize];
	}

	private static int method170(int i, int j) {
		int k = i + j * 57;
		k = k << 13 ^ k;
		int l = k * (k * k * 15731 + 0xc0ae5) + 0x5208dd0d & 0x7fffffff;
		return l >> 19 & 0xff;
	}

	public final void addTiles(TileSetting tileSetting[], SceneGraph sceneGraph) {
		for(int j = 0; j < 4; j++) {
			for(int k = 0; k < 104; k++) {
				for(int i1 = 0; i1 < 104; i1++) {
					if((tileSettings[j][k][i1] & 1) == 1) {
						int k1 = j;
						if((tileSettings[1][k][i1] & 2) == 2) {
							k1--;
						}
						if(k1 >= 0) {
							tileSetting[k1].clipTableSet(i1, k);
						}
					}
				}
			}
		}
		if (Constants.BOT_RANDOMIZATION) {
			hueOffset += (int)(Math.random() * 5D) - 2;
			if(hueOffset < -8) {
				hueOffset = -8;
			}
			if(hueOffset > 8) {
				hueOffset = 8;
			}
			lightnessOffset += (int)(Math.random() * 5D) - 2;
			if(lightnessOffset < -16) {
				lightnessOffset = -16;
			}
			if(lightnessOffset > 16) {
				lightnessOffset = 16;
			}
		}
		for(int z = 0; z < 4; z++) {
			byte abyte0[][] = objectShadowData[z];
			byte light_off = 96;
			char c = '\u0300';
			byte l_x = -50;
			byte l_y = -10;
			byte l_z = -50;
			int l_mag = (int)Math.sqrt(l_x * l_x + l_y * l_y + l_z * l_z);
			int sqrtA = c * l_mag >> 8;
			for(int y = 1; y < yMapSize - 1; y++) {
				for(int x = 1; x < xMapSize - 1; x++) {
					int hDX = heightMap[z][x + 1][y] - heightMap[z][x - 1][y];
					int hDY = heightMap[z][x][y + 1] - heightMap[z][x][y - 1];
					int square = (int)Math.sqrt(hDX * hDX + 0x10000 + hDY * hDY);
					int normal_x = (hDX << 8) / square;
					int normal_y = 0x10000 / square;
					int normal_z = (hDY << 8) / square;
					int j16 = light_off + (l_x * normal_x + l_y * normal_y + l_z * normal_z) / sqrtA;
					int j17 = (abyte0[x - 1][y] >> 2) + (abyte0[x + 1][y] >> 3) + (abyte0[x][y - 1] >> 2) + (abyte0[x][y + 1] >> 3) + (abyte0[x][y] >> 1);
					tileLightness[x][y] = j16 - j17;
				}
			}
			for(int k5 = 0; k5 < yMapSize; k5++) {
				hue[k5] = 0;
				saturation[k5] = 0;
				lightness[k5] = 0;
				hueDivider[k5] = 0;
				colorCount[k5] = 0;
			}
			for(int x = -5; x < xMapSize + 5; x++) {
				for(int y = 0; y < yMapSize; y++) {
					int xPlus5 = x + 5;
					if(xPlus5 >= 0 && xPlus5 < xMapSize) {
						int id = underlay[z][xPlus5][y] & 0xff;
						if(id > 0) {
							Floor floor = Floor.cache[id - 1];
							hue[y] += floor.hueVar;
							saturation[y] += floor.saturation;
							lightness[y] += floor.lightness;
							hueDivider[y] += floor.hueDivider;
							colorCount[y]++;
						}
					}
					int xMinus5 = x - 5;
					if(xMinus5 >= 0 && xMinus5 < xMapSize) {
						int id = underlay[z][xMinus5][y] & 0xff;
						if(id > 0) {
							Floor floor = Floor.cache[id - 1];
							hue[y] -= floor.hueVar;
							saturation[y] -= floor.saturation;
							lightness[y] -= floor.lightness;
							hueDivider[y] -= floor.hueDivider;
							colorCount[y]--;
						}
					}
				}
				if(x >= 1 && x < xMapSize - 1) {
					int tile_hue = 0;
					int tile_saturation = 0;
					int tile_light = 0;
					int tile_hue_shirt = 0;
					int totalColors = 0;
					for(int y = -5; y < yMapSize + 5; y++) {
						int yPlus5 = y + 5;
						if(yPlus5 >= 0 && yPlus5 < yMapSize) {
							tile_hue += hue[yPlus5];
							tile_saturation += saturation[yPlus5];
							tile_light += lightness[yPlus5];
							tile_hue_shirt += hueDivider[yPlus5];
							totalColors += colorCount[yPlus5];
						}
						int yMinus5 = y - 5;
						if(yMinus5 >= 0 && yMinus5 < yMapSize) {
							tile_hue -= hue[yMinus5];
							tile_saturation -= saturation[yMinus5];
							tile_light -= lightness[yMinus5];
							tile_hue_shirt -= hueDivider[yMinus5];
							totalColors -= colorCount[yMinus5];
						}
						if(y >= 1 && y < yMapSize - 1 && (!lowMem || (tileSettings[0][x][y] & 2) != 0 || (tileSettings[z][x][y] & 0x10) == 0 && getLogicHeight(y, z, x) == anInt131)) {
							if(z < setZ) {
								setZ = z;
							}
							int l18 = underlay[z][x][y] & 0xff;
							int i19 = overlay[z][x][y] & 0xff;
							if(l18 > 0 || i19 > 0)
							{
								int j19 = heightMap[z][x][y];
								int k19 = heightMap[z][x + 1][y];
								int l19 = heightMap[z][x + 1][y + 1];
								int i20 = heightMap[z][x][y + 1];
								int j20 = tileLightness[x][y];
								int k20 = tileLightness[x + 1][y];
								int l20 = tileLightness[x + 1][y + 1];
								int i21 = tileLightness[x][y + 1];
								int j21 = -1;
								int k21 = -1;
								if(l18 > 0) {
									int hue = (tile_hue * 256) / tile_hue_shirt;
									int saturation = tile_saturation / totalColors;
									int lightness = tile_light / totalColors;
									j21 = packHSL(hue, saturation, lightness);
									if (Constants.BOT_RANDOMIZATION) {
										hue = hue + hueOffset & 0xff;
										lightness += lightnessOffset;
										if(lightness < 0) {
											lightness = 0;
										} else if(lightness > 255) {
											lightness = 255;
										}
									}
									k21 = packHSL(hue, saturation, lightness);
								}
								if(z > 0) {
									boolean flag = true;
									if(l18 == 0 && tileShape[z][x][y] != 0) {
										flag = false;
									}
									if(i19 > 0 && !Floor.cache[i19 - 1].occlude) {
										flag = false;
									}
									if(flag && j19 == k19 && j19 == l19 && j19 == i20) {
										tileCullingBitmap[z][x][y] |= 0x924;
									}
								}
								int i22 = 0;
								if(j21 != -1) {
									i22 = Rasterizer.hslToRGB[mixLightness(k21, 96)];
								}
								if(i19 == 0) {
									sceneGraph.addTile(z, x, y, 0, 0, -1, j19, k19, l19, i20, mixLightness(j21, j20), mixLightness(j21, k20), mixLightness(j21, l20), mixLightness(j21, i21), 0, 0, 0, 0, i22, 0);
								} else {
									int shape = tileShape[z][x][y] + 1;
									byte rotation = tileShapeRotation[z][x][y];
									Floor floor = Floor.cache[i19 - 1];
									int texture = floor.textureId;
									int hsl;
									int rgb;
									if(texture >= 0) {
										rgb = Rasterizer.getAverageTextureColor(texture);
										hsl = -1;
									} else if (floor.getTerrainColor() == 0xff00ff || floor.getTerrainColor() == -65281) {
										rgb = 0;
										hsl = -2;
										texture = -1;
									} else {
										hsl = packHSL(floor.hue, floor.saturation, floor.lightness);
										rgb = Rasterizer.hslToRGB[method185(floor.hslValue, 96)];
									}
									sceneGraph.addTile(z, x, y, shape, rotation, texture, j19, k19, l19, i20, mixLightness(j21, j20), mixLightness(j21, k20), mixLightness(j21, l20), mixLightness(j21, i21), method185(hsl, j20), method185(hsl, k20), method185(hsl, l20), method185(hsl, i21), i22, rgb);
								}
							}
						}
					}
				}
			}
			for(int y = 1; y < yMapSize - 1; y++) {
				for(int x = 1; x < xMapSize - 1; x++) {
					sceneGraph.setTileLogicHeight(z, x, y, getLogicHeight(y, z, x));
				}
			}
		}
		sceneGraph.shadeModels(-10, -50, -50);
		for(int x = 0; x < xMapSize; x++) {
			for(int y = 0; y < yMapSize; y++) {
				if((tileSettings[1][x][y] & 2) == 2) {
					sceneGraph.applyBridgeMode(y, x);
				}
			}
		}
		int i2 = 1;
		int j2 = 2;
		int k2 = 4;
		for(int height = 0; height < 4; height++) {
			if(height > 0) {
				i2 <<= 3;
				j2 <<= 3;
				k2 <<= 3;
			}
			for(int z = 0; z <= height; z++) {
				for(int y = 0; y <= yMapSize; y++) {
					for(int x = 0; x <= xMapSize; x++) {
						if((tileCullingBitmap[z][x][y] & i2) != 0) {
							int k4 = y;
							int y_ = y;
							int z_ = z;
							int z__ = z;
							for(; k4 > 0 && (tileCullingBitmap[z][x][k4 - 1] & i2) != 0; k4--);
							for(; y_ < yMapSize && (tileCullingBitmap[z][x][y_ + 1] & i2) != 0; y_++);
						label0:
							for(; z_ > 0; z_--) {
								for(int yy = k4; yy <= y_; yy++) {
									if((tileCullingBitmap[z_ - 1][x][yy] & i2) == 0) {
										break label0;
									}
								}
							}
						label1:
							for(; z__ < height; z__++) {
								for(int y__ = k4; y__ <= y_; y__++) {
									if((tileCullingBitmap[z__ + 1][x][y__] & i2) == 0) {
										break label1;
									}
								}
							}

							int l10 = ((z__ + 1) - z_) * ((y_ - k4) + 1);
							if(l10 >= 8) {
								char c1 = '\360';
								int k14 = heightMap[z__][x][k4] - c1;
								int l15 = heightMap[z_][x][k4];
								SceneGraph.createCullingCluster(height, x * 128, l15, x * 128, y_ * 128 + 128, k14, k4 * 128, 1);
								for(int l16 = z_; l16 <= z__; l16++) {
									for(int l17 = k4; l17 <= y_; l17++) {
										tileCullingBitmap[l16][x][l17] &= ~i2;
									}
								}
							}
						}
						if((tileCullingBitmap[z][x][y] & j2) != 0)
						{
							int l4 = x;
							int i6 = x;
							int j7 = z;
							int l8 = z;
							for(; l4 > 0 && (tileCullingBitmap[z][l4 - 1][y] & j2) != 0; l4--);
							for(; i6 < xMapSize && (tileCullingBitmap[z][i6 + 1][y] & j2) != 0; i6++);
label2:
							for(; j7 > 0; j7--)
							{
								for(int i11 = l4; i11 <= i6; i11++)
									if((tileCullingBitmap[j7 - 1][i11][y] & j2) == 0)
										break label2;

							}

label3:
							for(; l8 < height; l8++)
							{
								for(int j11 = l4; j11 <= i6; j11++)
									if((tileCullingBitmap[l8 + 1][j11][y] & j2) == 0)
										break label3;

							}

							int k11 = ((l8 + 1) - j7) * ((i6 - l4) + 1);
							if(k11 >= 8)
							{
								char c2 = '\360';
								int l14 = heightMap[l8][l4][y] - c2;
								int i16 = heightMap[j7][l4][y];
								SceneGraph.createCullingCluster(height, l4 * 128, i16, i6 * 128 + 128, y * 128, l14, y * 128, 2);
								for(int i17 = j7; i17 <= l8; i17++)
								{
									for(int i18 = l4; i18 <= i6; i18++)
										tileCullingBitmap[i17][i18][y] &= ~j2;

								}

							}
						}
						if((tileCullingBitmap[z][x][y] & k2) != 0)
						{
							int i5 = x;
							int j6 = x;
							int k7 = y;
							int i9 = y;
							for(; k7 > 0 && (tileCullingBitmap[z][x][k7 - 1] & k2) != 0; k7--);
							for(; i9 < yMapSize && (tileCullingBitmap[z][x][i9 + 1] & k2) != 0; i9++);
label4:
							for(; i5 > 0; i5--)
							{
								for(int l11 = k7; l11 <= i9; l11++)
									if((tileCullingBitmap[z][i5 - 1][l11] & k2) == 0)
										break label4;

							}

label5:
							for(; j6 < xMapSize; j6++)
							{
								for(int i12 = k7; i12 <= i9; i12++)
									if((tileCullingBitmap[z][j6 + 1][i12] & k2) == 0)
										break label5;

							}

							if(((j6 - i5) + 1) * ((i9 - k7) + 1) >= 4)
							{
								int j12 = heightMap[z][i5][k7];
								SceneGraph.createCullingCluster(height, i5 * 128, j12, j6 * 128 + 128, i9 * 128 + 128, j12, k7 * 128, 4);
								for(int k13 = i5; k13 <= j6; k13++)
								{
									for(int i15 = k7; i15 <= i9; i15++)
										tileCullingBitmap[z][k13][i15] &= ~k2;

								}

							}
						}
					}

				}

			}

		}

	}

	private static int generateMapHeight(int i, int j)
	{
		int k = (method176(i + 45365, j + 0x16713, 4) - 128) + (method176(i + 10294, j + 37821, 2) - 128 >> 1) + (method176(i, j, 1) - 128 >> 2);
		k = (int)((double)k * 0.29999999999999999D) + 35;
		if(k < 10)
			k = 10;
		else
		if(k > 60)
			k = 60;
		return k;
	}

	public static void prefetchObjects(JagexBuffer buffer, ResourceProvider resourceProvider) {
		label0: {
			int i = -1;
			do {
				int j = buffer.getSmart();
				if(j == 0) {
					break label0;
				}
				i += j;
				ObjectDefinitions def = ObjectDefinitions.getDefinition(i);
				def.method574(resourceProvider);
				do {
					int k = buffer.getSmart();
					if(k == 0) {
						break;
					}
					buffer.getUnsignedByte();
				} while(true);
			} while(true);
		}
	}

	public final void initMapTables(int y, int j, int l, int x) {
		for(int posY = y; posY <= y + j; posY++) {
			for(int posX = x; posX <= x + l; posX++) {
				if(posX >= 0 && posX < xMapSize && posY >= 0 && posY < yMapSize) {
					objectShadowData[0][posX][posY] = 127;
					if(posX == x && posX > 0) {
						heightMap[0][posX][posY] = heightMap[0][posX - 1][posY];
					}
					if(posX == x + l && posX < xMapSize - 1) {
						heightMap[0][posX][posY] = heightMap[0][posX + 1][posY];
					}
					if(posY == y && posY > 0) {
						heightMap[0][posX][posY] = heightMap[0][posX][posY - 1];
					}
					if(posY == y + j && posY < yMapSize - 1) {
						heightMap[0][posX][posY] = heightMap[0][posX][posY + 1];
					}
				}
			}
		}
	}

	private void addObjectToRenderer(int x, int y, int z, int objectId, int objectType, SceneGraph sceneGraph, TileSetting tileSetting, int objectFace) {
		if(lowMem && (tileSettings[0][x][y] & 2) == 0) {
			if((tileSettings[z][x][y] & 0x10) != 0) {
				return;
			}
			if(getLogicHeight(y, z, x) != anInt131) {
				return;
			}
		}
		if(z < setZ) {
			setZ = z;
		}
		int k1 = heightMap[z][x][y];
		int l1 = heightMap[z][x + 1][y];
		int i2 = heightMap[z][x + 1][y + 1];
		int j2 = heightMap[z][x][y + 1];
		int k2 = k1 + l1 + i2 + j2 >> 2;
		ObjectDefinitions def = ObjectDefinitions.getDefinition(objectId);
		int l2 = x + (y << 7) + (objectId << 14) + 0x40000000;
		if(!def.hasActions)
			l2 += 0x80000000;
		byte byte0 = (byte)((objectFace << 6) + objectType);
		if(objectType == 22) {
			if(lowMem && !def.hasActions && !def.aBoolean736)
				return;
			Object obj;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj = def.renderObject(22, objectFace, k1, l1, i2, j2, -1);
			else
				obj = new ObjectOnTile(objectId, objectFace, 22, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addGroundDecoration(z, k2, y, ((Animable) (obj)), byte0, l2, x);
			if(def.unwalkable && def.hasActions && tileSetting != null)
				tileSetting.clipTableSet(y, x);
			return;
		}
		if(objectType == 10 || objectType == 11) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(10, objectFace, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, objectFace, 10, l1, i2, k1, j2, def.animationId, true);
			if(object != null) {
				int i5 = 0;
				if(objectType == 11)
					i5 += 256;
				int j4;
				int l4;
				if(objectFace == 1 || objectFace == 3) {
					j4 = def.tileSizeY;
					l4 = def.tileSizeX;
				} else {
					j4 = def.tileSizeX;
					l4 = def.tileSizeY;
				}
				if(sceneGraph.method284(l2, byte0, k2, l4, ((Animable) (object)), j4, z, i5, y, x) && def.aBoolean779) {
					Model model;
					if(object instanceof Model)
						model = (Model)object;
					else
						model = def.renderObject(10, objectFace, k1, l1, i2, j2, -1);
					if(model != null) {
						for(int j5 = 0; j5 <= j4; j5++) {
							for(int k5 = 0; k5 <= l4; k5++) {
								int l5 = model.anInt1650 / 4;
								if(l5 > 30)
									l5 = 30;
								if(l5 > objectShadowData[z][x + j5][y + k5])
									objectShadowData[z][x + j5][y + k5] = (byte)l5;
							}
						}
					}
				}
			}
			if(def.unwalkable && tileSetting != null)
				tileSetting.method212(def.aBoolean757, def.tileSizeX, def.tileSizeY, x, y, objectFace);
			return;
		}
		if(objectType >= 12) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(objectType, objectFace, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, objectFace, objectType, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.method284(l2, byte0, k2, 1, ((Animable) (object)), 1, z, 0, y, x);
			if(objectType >= 12 && objectType <= 17 && objectType != 13 && z > 0)
				tileCullingBitmap[z][x][y] |= 0x924;
			if(def.unwalkable && tileSetting != null)
				tileSetting.method212(def.aBoolean757, def.tileSizeX, def.tileSizeY, x, y, objectFace);
			return;
		}
		if(objectType == 0) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(0, objectFace, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, objectFace, 0, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallObject(bitValues[objectFace], ((Animable) (object)), l2, y, byte0, x, null, k2, 0, z);
			if(objectFace == 0) {
				if(def.aBoolean779) {
					objectShadowData[z][x][y] = 50;
					objectShadowData[z][x][y + 1] = 50;
				}
				if(def.aBoolean764)
					tileCullingBitmap[z][x][y] |= 0x249;
			} else
			if(objectFace == 1) {
				if(def.aBoolean779) {
					objectShadowData[z][x][y + 1] = 50;
					objectShadowData[z][x + 1][y + 1] = 50;
				}
				if(def.aBoolean764)
					tileCullingBitmap[z][x][y + 1] |= 0x492;
			} else if(objectFace == 2) {
				if(def.aBoolean779) {
					objectShadowData[z][x + 1][y] = 50;
					objectShadowData[z][x + 1][y + 1] = 50;
				}
				if(def.aBoolean764)
					tileCullingBitmap[z][x + 1][y] |= 0x249;
			} else if(objectFace == 3) {
				if(def.aBoolean779) {
					objectShadowData[z][x][y] = 50;
					objectShadowData[z][x + 1][y] = 50;
				}
				if(def.aBoolean764)
					tileCullingBitmap[z][x][y] |= 0x492;
			}
			if(def.unwalkable && tileSetting != null) {
				tileSetting.method211(y, objectFace, x, objectType, def.aBoolean757);
			}
			if(def.renderOffset != 16) {
				sceneGraph.setWallDecorationOffset(y, def.renderOffset, x, z);
			}
			return;
		}
		if(objectType == 1) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(1, objectFace, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, objectFace, 1, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallObject(highNybbleBitValues[objectFace], ((Animable) (object)), l2, y, byte0, x, null, k2, 0, z);
			if(def.aBoolean779)
				if(objectFace == 0)
					objectShadowData[z][x][y + 1] = 50;
				else
				if(objectFace == 1)
					objectShadowData[z][x + 1][y + 1] = 50;
				else
				if(objectFace == 2)
					objectShadowData[z][x + 1][y] = 50;
				else
				if(objectFace == 3)
					objectShadowData[z][x][y] = 50;
			if(def.unwalkable && tileSetting != null)
				tileSetting.method211(y, objectFace, x, objectType, def.aBoolean757);
			return;
		}
		if(objectType == 2) {
			int i3 = objectFace + 1 & 3;
			Object object1;
			Object object2;
			if(def.animationId == -1 && def.childrenIDs == null) {
				object1 = def.renderObject(2, 4 + objectFace, k1, l1, i2, j2, -1);
				object2 = def.renderObject(2, i3, k1, l1, i2, j2, -1);
			} else {
				object1 = new ObjectOnTile(objectId, 4 + objectFace, 2, l1, i2, k1, j2, def.animationId, true);
				object2 = new ObjectOnTile(objectId, i3, 2, l1, i2, k1, j2, def.animationId, true);
			}
			sceneGraph.addWallObject(bitValues[objectFace], ((Animable) (object1)), l2, y, byte0, x, ((Animable) (object2)), k2, bitValues[i3], z);
			if(def.aBoolean764)
				if(objectFace == 0) {
					tileCullingBitmap[z][x][y] |= 0x249;
					tileCullingBitmap[z][x][y + 1] |= 0x492;
				} else if(objectFace == 1) {
					tileCullingBitmap[z][x][y + 1] |= 0x492;
					tileCullingBitmap[z][x + 1][y] |= 0x249;
				} else if(objectFace == 2) {
					tileCullingBitmap[z][x + 1][y] |= 0x249;
					tileCullingBitmap[z][x][y] |= 0x492;
				} else if(objectFace == 3) {
					tileCullingBitmap[z][x][y] |= 0x492;
					tileCullingBitmap[z][x][y] |= 0x249;
				}
			if(def.unwalkable && tileSetting != null)
				tileSetting.method211(y, objectFace, x, objectType, def.aBoolean757);
			if(def.renderOffset != 16)
				sceneGraph.setWallDecorationOffset(y, def.renderOffset, x, z);
			return;
		}
		if(objectType == 3) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(3, objectFace, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, objectFace, 3, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallObject(highNybbleBitValues[objectFace], ((Animable) (object)), l2, y, byte0, x, null, k2, 0, z);
			if(def.aBoolean779)
				if(objectFace == 0)
					objectShadowData[z][x][y + 1] = 50;
				else if(objectFace == 1)
					objectShadowData[z][x + 1][y + 1] = 50;
				else if(objectFace == 2)
					objectShadowData[z][x + 1][y] = 50;
				else if(objectFace == 3)
					objectShadowData[z][x][y] = 50;
			if(def.unwalkable && tileSetting != null)
				tileSetting.method211(y, objectFace, x, objectType, def.aBoolean757);
			return;
		}
		if(objectType == 9) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(objectType, objectFace, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, objectFace, objectType, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.method284(l2, byte0, k2, 1, ((Animable) (object)), 1, z, 0, y, x);
			if(def.unwalkable && tileSetting != null)
				tileSetting.method212(def.aBoolean757, def.tileSizeX, def.tileSizeY, x, y, objectFace);
			return;
		}
		if(def.conforms)
			if(objectFace == 1) {
				int j3 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k1;
				k1 = j3;
			} else if(objectFace == 2) {
				int k3 = j2;
				j2 = l1;
				l1 = k3;
				k3 = i2;
				i2 = k1;
				k1 = k3;
			} else if(objectFace == 3) {
				int l3 = j2;
				j2 = k1;
				k1 = l1;
				l1 = i2;
				i2 = l3;
			}
		if(objectType == 4) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, 0, 4, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallDecoration(l2, y, objectFace * 512, z, 0, k2, ((Animable) (object)), x, byte0, 0, bitValues[objectFace]);
			return;
		}
		if(objectType == 5) {
			int renderOffset = 16;
			int uid = sceneGraph.getWallObjectUID(z, x, y);
			if(uid > 0) {
				renderOffset = ObjectDefinitions.getDefinition(uid >> 14 & 0x7fff).renderOffset;
			}
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, 0, 4, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallDecoration(l2, y, objectFace * 512, z, faceOffsetX[objectFace] * renderOffset, k2, ((Animable) (object)), x, byte0, faceOffsetY[objectFace] * renderOffset, bitValues[objectFace]);
			return;
		}
		if(objectType == 6) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, 0, 4, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallDecoration(l2, y, objectFace, z, 0, k2, ((Animable) (object)), x, byte0, 0, 256);
			return;
		}
		if(objectType == 7) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, 0, 4, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallDecoration(l2, y, objectFace, z, 0, k2, ((Animable) (object)), x, byte0, 0, 512);
			return;
		}
		if(objectType == 8) {
			Object object;
			if(def.animationId == -1 && def.childrenIDs == null)
				object = def.renderObject(4, 0, k1, l1, i2, j2, -1);
			else
				object = new ObjectOnTile(objectId, 0, 4, l1, i2, k1, j2, def.animationId, true);
			sceneGraph.addWallDecoration(l2, y, objectFace, z, 0, k2, ((Animable) (object)), x, byte0, 0, 768);
		}
	}

	private static int method176(int i, int j, int k) {
		int l = i / k;
		int i1 = i & k - 1;
		int j1 = j / k;
		int k1 = j & k - 1;
		int l1 = method186(l, j1);
		int i2 = method186(l + 1, j1);
		int j2 = method186(l, j1 + 1);
		int k2 = method186(l + 1, j1 + 1);
		int l2 = method184(l1, i2, i1, k);
		int i3 = method184(j2, k2, i1, k);
		return method184(l2, i3, k1, k);
	}

	private int packHSL(int i, int j, int k) {
		if(k > 179)
			j /= 2;
		if(k > 192)
			j /= 2;
		if(k > 217)
			j /= 2;
		if(k > 243)
			j /= 2;
		return (i / 4 << 10) + (j / 32 << 7) + k / 2;
	}

	public static boolean method178(int id, int j) {
		ObjectDefinitions def = ObjectDefinitions.getDefinition(id);
		if(j == 11)
			j = 10;
		if(j >= 5 && j <= 8)
			j = 4;
		return def.method577(j);
	}

	public final void loadMapChunk(int tileZ, int tileRotation, TileSetting tileSetting[], int x, int i1, byte abyte0[], int j1, int k1, int y) {
		for(int i2 = 0; i2 < 8; i2++) {
			for(int j2 = 0; j2 < 8; j2++) {
				if(x + i2 > 0 && x + i2 < 103 && y + j2 > 0 && y + j2 < 103) {
					tileSetting[k1].clipData[x + i2][y + j2] &= 0xfeffffff;
				}
			}
		}
		JagexBuffer stream = new JagexBuffer(abyte0);
		for(int l2 = 0; l2 < 4; l2++) {
			for(int i3 = 0; i3 < 64; i3++) {
				for(int j3 = 0; j3 < 64; j3++) {
					if(l2 == tileZ && i3 >= i1 && i3 < i1 + 8 && j3 >= j1 && j3 < j1 + 8) {
						readTile(y + MapUtils.getRotatedMapChunkY(i3 & 7, j3 & 7, tileRotation), 0, stream, x + MapUtils.getRotatedMapChunkX(i3 & 7, j3 & 7, tileRotation), k1, tileRotation, 0);
					} else {
						readTile(-1, 0, stream, -1, 0, 0, 0);
					}
				}
			}
		}

	}

	public final void loadTerrain(byte abyte0[], int offsetY, int offsetX, int k, int l, TileSetting tileSetting[]) {
		for(int z = 0; z < 4; z++) {
			for(int x = 0; x < 64; x++) {
				for(int y = 0; y < 64; y++) {
					if(offsetX + x > 0 && offsetX + x < 103 && offsetY + y > 0 && offsetY + y < 103) {
						tileSetting[z].clipData[offsetX + x][offsetY + y] &= 0xfeffffff;
					}
				}
			}
		}
		JagexBuffer stream = new JagexBuffer(abyte0);
		for(int z = 0; z < 4; z++) {
			for(int x = 0; x < 64; x++) {
				for(int y = 0; y < 64; y++) {
					readTile(y + offsetY, l, stream, x + offsetX, z, 0, k);
				}
			}
		}
	}

	private void readTile(int z, int offsetZ, JagexBuffer buffer, int x, int l, int shapeBOffset, int offsetX) {
		if(x >= 0 && x < 104 && z >= 0 && z < 104) {
			tileSettings[l][x][z] = 0;
			do {
				int l1 = buffer.getUnsignedByte();
				if(l1 == 0)
					if(l == 0) {
						heightMap[0][x][z] = -generateMapHeight(0xe3b7b + x + offsetX, 0x87cce + z + offsetZ) * 8;
						return;
					} else {
						heightMap[l][x][z] = heightMap[l - 1][x][z] - 240;
						return;
					}
				if(l1 == 1) {
					int j2 = buffer.getUnsignedByte();
					if(j2 == 1)
						j2 = 0;
					if(l == 0) {
						heightMap[0][x][z] = -j2 * 8;
						return;
					} else {
						heightMap[l][x][z] = heightMap[l - 1][x][z] - j2 * 8;
						return;
					}
				}
				if(l1 <= 49) {
					overlay[l][x][z] = buffer.getSignedByte();
					tileShape[l][x][z] = (byte)((l1 - 2) / 4);
					tileShapeRotation[l][x][z] = (byte)((l1 - 2) + shapeBOffset & 3);
				} else if(l1 <= 81)
					tileSettings[l][x][z] = (byte)(l1 - 49);
				else
					underlay[l][x][z] = (byte)(l1 - 81);
			} while(true);
		}
		do {
			int i2 = buffer.getUnsignedByte();
			if(i2 == 0)
				break;
			if(i2 == 1) {
				buffer.getUnsignedByte();
				return;
			}
			if(i2 <= 49)
				buffer.getUnsignedByte();
		} while(true);
	}

	private int getLogicHeight(int y, int z, int x) {
		if((tileSettings[z][x][y] & 8) != 0) {
			return 0;
		}
		if(z > 0 && (tileSettings[1][x][y] & 2) != 0) {
			return z - 1;
		} else {
			return z;
		}
	}

	public final void method183(TileSetting aclass11[], SceneGraph worldController, int i, int j, int k, int l,
								byte abyte0[], int i1, int j1, int k1)
	{
label0:
		{
			JagexBuffer stream = new JagexBuffer(abyte0);
			int l1 = -1;
			do
			{
				int i2 = stream.getSmart();
				if(i2 == 0)
					break label0;
				l1 += i2;
				int j2 = 0;
				do
				{
					int k2 = stream.getSmart();
					if(k2 == 0)
						break;
					j2 += k2 - 1;
					int l2 = j2 & 0x3f;
					int i3 = j2 >> 6 & 0x3f;
					int j3 = j2 >> 12;
					int k3 = stream.getUnsignedByte();
					int l3 = k3 >> 2;
					int i4 = k3 & 3;
					if(j3 == i && i3 >= i1 && i3 < i1 + 8 && l2 >= k && l2 < k + 8)
					{
						ObjectDefinitions class46 = ObjectDefinitions.getDefinition(l1);
						int j4 = j + MapUtils.getRotatedLandscapeChunkX(i3 & 7, l2 & 7, class46.tileSizeX, class46.tileSizeY, j1);
						int k4 = k1 + MapUtils.getRotatedLandscapeChunkY(i3 & 7, l2 & 7, class46.tileSizeX, class46.tileSizeY, j1);
						if(j4 > 0 && k4 > 0 && j4 < 103 && k4 < 103)
						{
							int l4 = j3;
							if((tileSettings[1][j4][k4] & 2) == 2)
								l4--;
							TileSetting class11 = null;
							if(l4 >= 0)
								class11 = aclass11[l4];
							addObjectToRenderer(j4, k4, l, l1, l3, worldController, class11, i4 + j1 & 3);
						}
					}
				} while(true);
			} while(true);
		}
	}

	private static int method184(int i, int j, int k, int l)
	{
		int i1 = 0x10000 - Rasterizer.COSINE[(k * 1024) / l] >> 1;
		return (i * (0x10000 - i1) >> 16) + (j * i1 >> 16);
	}

	private int method185(int i, int j)
	{
		if(i == -2)
			return 0xbc614e;
		if(i == -1)
		{
			if(j < 0)
				j = 0;
			else
			if(j > 127)
				j = 127;
			j = 127 - j;
			return j;
		}
		j = (j * (i & 0x7f)) / 128;
		if(j < 2)
			j = 2;
		else
		if(j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	private static int method186(int i, int j)
	{
		int k = method170(i - 1, j - 1) + method170(i + 1, j - 1) + method170(i - 1, j + 1) + method170(i + 1, j + 1);
		int l = method170(i - 1, j) + method170(i + 1, j) + method170(i, j - 1) + method170(i, j + 1);
		int i1 = method170(i, j);
		return k / 16 + l / 8 + i1 / 4;
	}

	private static int mixLightness(int i, int j) {
		if(i == -1)
			return 0xbc614e;
		j = (j * (i & 0x7f)) / 128;
		if(j < 2)
			j = 2;
		else
		if(j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	public static void method188(SceneGraph sceneGraph, int i, int j, int k, int l, TileSetting class11, int ai[][][], int i1,
								 int j1, int k1)
	{
		int l1 = ai[l][i1][j];
		int i2 = ai[l][i1 + 1][j];
		int j2 = ai[l][i1 + 1][j + 1];
		int k2 = ai[l][i1][j + 1];
		int l2 = l1 + i2 + j2 + k2 >> 2;
		ObjectDefinitions def = ObjectDefinitions.getDefinition(j1);
		int i3 = i1 + (j << 7) + (j1 << 14) + 0x40000000;
		if(!def.hasActions)
			i3 += 0x80000000;
		byte byte1 = (byte)((i << 6) + k);
		if(k == 22)
		{
			Object obj;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj = def.renderObject(22, i, l1, i2, j2, k2, -1);
			else
				obj = new ObjectOnTile(j1, i, 22, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addGroundDecoration(k1, l2, j, ((Animable) (obj)), byte1, i3, i1);
			if(def.unwalkable && def.hasActions)
				class11.clipTableSet(j, i1);
			return;
		}
		if(k == 10 || k == 11)
		{
			Object obj1;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj1 = def.renderObject(10, i, l1, i2, j2, k2, -1);
			else
				obj1 = new ObjectOnTile(j1, i, 10, i2, j2, l1, k2, def.animationId, true);
			if(obj1 != null)
			{
				int j5 = 0;
				if(k == 11)
					j5 += 256;
				int k4;
				int i5;
				if(i == 1 || i == 3)
				{
					k4 = def.tileSizeY;
					i5 = def.tileSizeX;
				} else
				{
					k4 = def.tileSizeX;
					i5 = def.tileSizeY;
				}
				sceneGraph.method284(i3, byte1, l2, i5, ((Animable) (obj1)), k4, k1, j5, j, i1);
			}
			if(def.unwalkable)
				class11.method212(def.aBoolean757, def.tileSizeX, def.tileSizeY, i1, j, i);
			return;
		}
		if(k >= 12)
		{
			Object obj2;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj2 = def.renderObject(k, i, l1, i2, j2, k2, -1);
			else
				obj2 = new ObjectOnTile(j1, i, k, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.method284(i3, byte1, l2, 1, ((Animable) (obj2)), 1, k1, 0, j, i1);
			if(def.unwalkable)
				class11.method212(def.aBoolean757, def.tileSizeX, def.tileSizeY, i1, j, i);
			return;
		}
		if(k == 0)
		{
			Object obj3;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj3 = def.renderObject(0, i, l1, i2, j2, k2, -1);
			else
				obj3 = new ObjectOnTile(j1, i, 0, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallObject(bitValues[i], ((Animable) (obj3)), i3, j, byte1, i1, null, l2, 0, k1);
			if(def.unwalkable)
				class11.method211(j, i, i1, k, def.aBoolean757);
			return;
		}
		if(k == 1)
		{
			Object obj4;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj4 = def.renderObject(1, i, l1, i2, j2, k2, -1);
			else
				obj4 = new ObjectOnTile(j1, i, 1, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallObject(highNybbleBitValues[i], ((Animable) (obj4)), i3, j, byte1, i1, null, l2, 0, k1);
			if(def.unwalkable)
				class11.method211(j, i, i1, k, def.aBoolean757);
			return;
		}
		if(k == 2)
		{
			int j3 = i + 1 & 3;
			Object obj11;
			Object obj12;
			if(def.animationId == -1 && def.childrenIDs == null)
			{
				obj11 = def.renderObject(2, 4 + i, l1, i2, j2, k2, -1);
				obj12 = def.renderObject(2, j3, l1, i2, j2, k2, -1);
			} else
			{
				obj11 = new ObjectOnTile(j1, 4 + i, 2, i2, j2, l1, k2, def.animationId, true);
				obj12 = new ObjectOnTile(j1, j3, 2, i2, j2, l1, k2, def.animationId, true);
			}
			sceneGraph.addWallObject(bitValues[i], ((Animable) (obj11)), i3, j, byte1, i1, ((Animable) (obj12)), l2, bitValues[j3], k1);
			if(def.unwalkable)
				class11.method211(j, i, i1, k, def.aBoolean757);
			return;
		}
		if(k == 3)
		{
			Object obj5;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj5 = def.renderObject(3, i, l1, i2, j2, k2, -1);
			else
				obj5 = new ObjectOnTile(j1, i, 3, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallObject(highNybbleBitValues[i], ((Animable) (obj5)), i3, j, byte1, i1, null, l2, 0, k1);
			if(def.unwalkable)
				class11.method211(j, i, i1, k, def.aBoolean757);
			return;
		}
		if(k == 9)
		{
			Object obj6;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj6 = def.renderObject(k, i, l1, i2, j2, k2, -1);
			else
				obj6 = new ObjectOnTile(j1, i, k, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.method284(i3, byte1, l2, 1, ((Animable) (obj6)), 1, k1, 0, j, i1);
			if(def.unwalkable)
				class11.method212(def.aBoolean757, def.tileSizeX, def.tileSizeY, i1, j, i);
			return;
		}
		if(def.conforms)
			if(i == 1)
			{
				int k3 = k2;
				k2 = j2;
				j2 = i2;
				i2 = l1;
				l1 = k3;
			} else
			if(i == 2)
			{
				int l3 = k2;
				k2 = i2;
				i2 = l3;
				l3 = j2;
				j2 = l1;
				l1 = l3;
			} else
			if(i == 3)
			{
				int i4 = k2;
				k2 = l1;
				l1 = i2;
				i2 = j2;
				j2 = i4;
			}
		if(k == 4)
		{
			Object obj7;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj7 = def.renderObject(4, 0, l1, i2, j2, k2, -1);
			else
				obj7 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallDecoration(i3, j, i * 512, k1, 0, l2, ((Animable) (obj7)), i1, byte1, 0, bitValues[i]);
			return;
		}
		if(k == 5)
		{
			int j4 = 16;
			int l4 = sceneGraph.getWallObjectUID(k1, i1, j);
			if(l4 > 0)
				j4 = ObjectDefinitions.getDefinition(l4 >> 14 & 0x7fff).renderOffset;
			Object obj13;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj13 = def.renderObject(4, 0, l1, i2, j2, k2, -1);
			else
				obj13 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallDecoration(i3, j, i * 512, k1, faceOffsetX[i] * j4, l2, ((Animable) (obj13)), i1, byte1, faceOffsetY[i] * j4, bitValues[i]);
			return;
		}
		if(k == 6)
		{
			Object obj8;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj8 = def.renderObject(4, 0, l1, i2, j2, k2, -1);
			else
				obj8 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallDecoration(i3, j, i, k1, 0, l2, ((Animable) (obj8)), i1, byte1, 0, 256);
			return;
		}
		if(k == 7)
		{
			Object obj9;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj9 = def.renderObject(4, 0, l1, i2, j2, k2, -1);
			else
				obj9 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallDecoration(i3, j, i, k1, 0, l2, ((Animable) (obj9)), i1, byte1, 0, 512);
			return;
		}
		if(k == 8)
		{
			Object obj10;
			if(def.animationId == -1 && def.childrenIDs == null)
				obj10 = def.renderObject(4, 0, l1, i2, j2, k2, -1);
			else
				obj10 = new ObjectOnTile(j1, 0, 4, i2, j2, l1, k2, def.animationId, true);
			sceneGraph.addWallDecoration(i3, j, i, k1, 0, l2, ((Animable) (obj10)), i1, byte1, 0, 768);
		}
	}

  public static boolean method189(int i, byte[] is, int i_250_
  ) //xxx bad method, decompiled with JODE
  {
	boolean bool = true;
	JagexBuffer stream = new JagexBuffer(is);
	int i_252_ = -1;
	for (;;)
	  {
	int i_253_ = stream.getSmart ();
	if (i_253_ == 0)
	  break;
	i_252_ += i_253_;
	int i_254_ = 0;
	boolean bool_255_ = false;
	for (;;)
	  {
		if (bool_255_)
		  {
		int i_256_ = stream.getSmart ();
		if (i_256_ == 0)
		  break;
		stream.getUnsignedByte();
		  }
		else
		  {
		int i_257_ = stream.getSmart ();
		if (i_257_ == 0)
		  break;
		i_254_ += i_257_ - 1;
		int i_258_ = i_254_ & 0x3f;
		int i_259_ = i_254_ >> 6 & 0x3f;
		int i_260_ = stream.getUnsignedByte() >> 2;
		int i_261_ = i_259_ + i;
		int i_262_ = i_258_ + i_250_;
		if (i_261_ > 0 && i_262_ > 0 && i_261_ < 103 && i_262_ < 103)
		  {
			ObjectDefinitions class46 = ObjectDefinitions.getDefinition (i_252_);
			if (i_260_ != 22 || !lowMem || class46.hasActions
					|| class46.aBoolean736)
			  {
			bool &= class46.method579 ();
			bool_255_ = true;
			  }
		  }
		  }
	  }
	  }
	return bool;
  }

	public final void loadObjects(int offsetX, TileSetting tileSetting[], int offsetY, SceneGraph sceneGraph, byte abyte0[]) {
		label0: {
			JagexBuffer buffer = new JagexBuffer(abyte0);
			int id = -1;
			do {
				int idOffset = buffer.getSmart();
				if(idOffset == 0) {
					break label0;
				}
				id += idOffset;
				int loc = 0;
				do {
					int posOffset = buffer.getSmart();
					if(posOffset == 0) {
						break;
					}
					loc += posOffset - 1;
					int localY = loc & 0x3f;
					int localX = loc >> 6 & 0x3f;
					int height = loc >> 12;
					int data = buffer.getUnsignedByte();
					int type = data >> 2;
					int direction = data & 3;
					int x = localX + offsetX;
					int y = localY + offsetY;
					if(x > 0 && y > 0 && x < 103 && y < 103) {
						int z = height;
						if((tileSettings[1][x][y] & 2) == 2) {
							z--;
						}
						TileSetting tile = null;
						if(z >= 0) {
							tile = tileSetting[z];
						}
						addObjectToRenderer(x, y, height, id, type, sceneGraph, tile, direction);
					}
				} while(true);
			} while(true);
		}
	}

	private static int hueOffset = (int)(Math.random() * 17D) - 8;
	private final int[] hue;
	private final int[] saturation;
	private final int[] lightness;
	private final int[] hueDivider;
	private final int[] colorCount;
	private final int[][][] heightMap;
	public final byte[][][] overlay;
	public static int anInt131;
	private static int lightnessOffset = (int)(Math.random() * 33D) - 16;
	private final byte[][][] objectShadowData;
	private final int[][][] tileCullingBitmap;
	private final byte[][][] tileShape;
	private static final int faceOffsetX[] = {
		1, 0, -1, 0
	};
	private final int[][] tileLightness;
	private static final int highNybbleBitValues[] = {
		16, 32, 64, 128
	};
	private final byte[][][] underlay;
	private static final int faceOffsetY[] = {
		0, -1, 0, 1
	};
	public static int setZ = 99;
	private final int xMapSize;
	private final int yMapSize;
	private final byte[][][] tileShapeRotation;
	private final byte[][][] tileSettings;
	public static boolean lowMem = true;
	private static final int bitValues[] = {
		1, 2, 4, 8
	};

}
