package rs2.world.render;

import rs2.Animable;
import rs2.VertexNormal;
import rs2.Deque;
import rs2.Model;
import rs2.graphics.RSDrawingArea;
import rs2.graphics.Rasterizer;
import rs2.world.GroundDecoration;
import rs2.world.InteractableObject;
import rs2.world.WallDecoration;
import rs2.world.WallObject;
import rs2.world.tile.GroundItemTile;
import rs2.world.tile.PlainTile;
import rs2.world.tile.ShapedTile;
import rs2.world.tile.Tile;

public final class SceneGraph {

	public SceneGraph(int heightMap[][][]) {
		int length = 104;// was parameter
		int width = 104;// was parameter
		int height = 4;// was parameter
		interactableObjectCache = new InteractableObject[5000];
		anIntArray486 = new int[10000];
		anIntArray487 = new int[10000];
		zMapSize = height;
		xMapSize = width;
		yMapSize = length;
		tileArray = new Tile[height][width][length];
		anIntArrayArrayArray445 = new int[height][width + 1][length + 1];
		this.heightMap = heightMap;
		initToNull();
	}

	public static void clearCache() {
		interactableObjects = null;
		cullingClusterPointer = null;
		cullingClusters = null;
		aClass19_477 = null;
		TILE_VISIBILITY_MAPS = null;
		TILE_VISIBILITY_MAP = null;
	}

	public void initToNull() {
		for (int z = 0; z < zMapSize; z++) {
			for (int x = 0; x < xMapSize; x++) {
				for (int y = 0; y < yMapSize; y++) {
					tileArray[z][x][y] = null;
				}
			}
		}
		for (int l = 0; l < anInt472; l++) {
			for (int j1 = 0; j1 < cullingClusterPointer[l]; j1++) {
				cullingClusters[l][j1] = null;
			}
			cullingClusterPointer[l] = 0;
		}

		for (int k1 = 0; k1 < interactableObjectCacheCurrPos; k1++)
			interactableObjectCache[k1] = null;

		interactableObjectCacheCurrPos = 0;
		for (int l1 = 0; l1 < interactableObjects.length; l1++)
			interactableObjects[l1] = null;

	}

	public void setHeightLevel(int z) {
		currentHeight = z;
		for (int x = 0; x < xMapSize; x++) {
			for (int y = 0; y < yMapSize; y++) {
				if (tileArray[z][x][y] == null) {
					tileArray[z][x][y] = new Tile(z, x, y);
				}
			}
		}
	}

	public void applyBridgeMode(int y, int x) {
		Tile tile = tileArray[0][x][y];
		for (int z = 0; z < 3; z++) {
			Tile tile_1 = tileArray[z][x][y] = tileArray[z + 1][x][y];
			if (tile_1 != null) {
				tile_1.tileZ--;
				for (int pointer = 0; pointer < tile_1.count; pointer++) {
					InteractableObject interactableObject = tile_1.interactableObjects[pointer];
					if ((interactableObject.uid >> 29 & 3) == 2 && interactableObject.tileLeft == x && interactableObject.tileTop == y) {
						interactableObject.z--;
					}
				}
			}
		}
		if (tileArray[0][x][y] == null) {
			tileArray[0][x][y] = new Tile(0, x, y);
		}
		tileArray[0][x][y].tileBelow0 = tile;
		tileArray[3][x][y] = null;
	}

	public static void createCullingCluster(int z, int startX, int endZ, int endX, int endY, int startZ, int startY, int searchMask) {
		CullingCluster cullingCluster = new CullingCluster();
		cullingCluster.tileStartX = startX / 128;
		cullingCluster.tileEndX = endX / 128;
		cullingCluster.tileStartY = startY / 128;
		cullingCluster.tileEndY = endY / 128;
		cullingCluster.searchMask = searchMask;
		cullingCluster.worldStartX = startX;
		cullingCluster.worldEndX = endX;
		cullingCluster.worldStartY = startY;
		cullingCluster.worldEndY = endY;
		cullingCluster.worldStartZ = startZ;
		cullingCluster.worldEndZ = endZ;
		cullingClusters[z][cullingClusterPointer[z]++] = cullingCluster;
	}

	public void setTileLogicHeight(int z, int x, int y, int l) {
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			tileArray[z][x][y].logicHeight = l;
		}
	}

	public void addTile(int z, int x, int y, int shape, int i1, int j1, int k1,
			int l1, int i2, int j2, int k2, int l2, int i3, int j3, int k3,
			int l3, int i4, int j4, int k4, int l4) {
		if (shape == 0) {
			PlainTile plainTile = new PlainTile(k2, l2, i3, j3, -1, k4, false);
			for (int zz = z; zz >= 0; zz--) {
				if (tileArray[zz][x][y] == null) {
					tileArray[zz][x][y] = new Tile(zz, x, y);
				}
			}
			tileArray[z][x][y].plainTile = plainTile;
			return;
		}
		if (shape == 1) {
			PlainTile plainTile_1 = new PlainTile(k3, l3, i4, j4, j1, l4, k1 == l1 && k1 == i2 && k1 == j2);
			for (int j5 = z; j5 >= 0; j5--) {
				if (tileArray[j5][x][y] == null) {
					tileArray[j5][x][y] = new Tile(j5, x, y);
				}
			}
			tileArray[z][x][y].plainTile = plainTile_1;
			return;
		}
		ShapedTile shapedTile = new ShapedTile(y, k3, j3, i2, j1, i4, i1, k2, k4, i3, j2, l1, k1, shape, j4, l3, l2, x, l4);
		for (int k5 = z; k5 >= 0; k5--) {
			if (tileArray[k5][x][y] == null) {
				tileArray[k5][x][y] = new Tile(k5, x, y);
			}
		}
		tileArray[z][x][y].shapedTile = shapedTile;
	}

	public void addGroundDecoration(int i, int z, int y, Animable animable, byte byte0, int uid, int x) {
		if (animable == null)
			return;
		GroundDecoration groundDecoration = new GroundDecoration();
		groundDecoration.animable = animable;
		groundDecoration.x = x * 128 + 64;
		groundDecoration.y = y * 128 + 64;
		groundDecoration.z = z;
		groundDecoration.uid = uid;
		groundDecoration.objectConfig = byte0;
		if (tileArray[i][x][y] == null) {
			tileArray[i][x][y] = new Tile(i, x, y);
		}
		tileArray[i][x][y].groundDecoration = groundDecoration;
	}

	public void addGroundItemTile(int x, int uid, Animable animable, int drawHeight, Animable animable_1, Animable animable_2, int z, int y) {
		GroundItemTile groundItemTile = new GroundItemTile();
		groundItemTile.firstGroundItem = animable_2;
		groundItemTile.x = x * 128 + 64;
		groundItemTile.y = y * 128 + 64;
		groundItemTile.z = drawHeight;
		groundItemTile.uid = uid;
		groundItemTile.secondGroundItem = animable;
		groundItemTile.thirdGroundItem = animable_1;
		int j1 = 0;
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			for (int index = 0; index < tile.count; index++) {
				if (tile.interactableObjects[index].animable instanceof Model) {
					int l1 = ((Model) tile.interactableObjects[index].animable).anInt1654;
					if (l1 > j1) {
						j1 = l1;
					}
				}
			}
		}
		groundItemTile.anInt52 = j1;
		if (tileArray[z][x][y] == null) {
			tileArray[z][x][y] = new Tile(z, x, y);
		}
		tileArray[z][x][y].groundItemTile = groundItemTile;
	}

	public void addWallObject(int i, Animable animable, int j, int y, byte byte0, int x, Animable animable_1, int drawHeight, int j1, int z) {
		if (animable == null && animable_1 == null) {
			return;
		}
		WallObject wallObject = new WallObject();
		wallObject.uid = j;
		wallObject.aByte281 = byte0;
		wallObject.x = x * 128 + 64;
		wallObject.y = y * 128 + 64;
		wallObject.z = drawHeight;
		wallObject.node1 = animable;
		wallObject.node2 = animable_1;
		wallObject.orientation = i;
		wallObject.orientation1 = j1;
		for (int height = z; height >= 0; height--) {
			if (tileArray[height][x][y] == null) {
				tileArray[height][x][y] = new Tile(height, x, y);
			}
		}
		tileArray[z][x][y].wallObject = wallObject;
	}

	public void addWallDecoration(int i, int y, int k, int z, int j1, int drawHeight, Animable animable, int x, byte byte0, int i2, int j2) {
		if (animable == null) {
			return;
		}
		WallDecoration wallDecoration = new WallDecoration();
		wallDecoration.uid = i;
		wallDecoration.aByte506 = byte0;
		wallDecoration.x = x * 128 + 64 + j1;
		wallDecoration.y = y * 128 + 64 + i2;
		wallDecoration.z = drawHeight;
		wallDecoration.node = animable;
		wallDecoration.configBits = j2;
		wallDecoration.face = k;
		for (int height = z; height >= 0; height--) {
			if (tileArray[height][x][y] == null) {
				tileArray[height][x][y] = new Tile(height, x, y);
			}
		}
		tileArray[z][x][y].wallDecoration = wallDecoration;
	}

	public boolean method284(int i, byte byte0, int j, int k, Animable animable, int l, int i1, int j1, int k1, int l1) {
		if (animable == null) {
			return true;
		} else {
			int i2 = l1 * 128 + 64 * l;
			int j2 = k1 * 128 + 64 * k;
			return method287(i1, l1, k1, l, k, i2, j2, j, animable, j1, false, i, byte0);
		}
	}

	public boolean method285(int i, int j, int k, int l, int i1, int j1, int k1, Animable animable, boolean flag) {
		if (animable == null)
			return true;
		int l1 = k1 - j1;
		int i2 = i1 - j1;
		int j2 = k1 + j1;
		int k2 = i1 + j1;
		if (flag) {
			if (j > 640 && j < 1408)
				k2 += 128;
			if (j > 1152 && j < 1920)
				j2 += 128;
			if (j > 1664 || j < 384)
				i2 -= 128;
			if (j > 128 && j < 896)
				l1 -= 128;
		}
		l1 /= 128;
		i2 /= 128;
		j2 /= 128;
		k2 /= 128;
		return method287(i, l1, i2, (j2 - l1) + 1, (k2 - i2) + 1, k1, i1, k, animable, j, true, l, (byte) 0);
	}

	public boolean method286(int j, int k, Animable animable, int l,
			int i1, int j1, int k1, int l1, int i2, int j2, int k2) {
		return animable == null || method287(j, l1, k2, (i2 - l1) + 1, (i1 - k2) + 1, j1, k, k1, animable, l, true, j2, (byte) 0);
	}

	private boolean method287(int z, int x, int y, int tileWidth, int tileHeight, int worldX,
			int worldY, int worldZ, Animable animable, int rotation, boolean flag, int j2, byte byte0) {
		for (int _x = x; _x < x + tileWidth; _x++) {
			for (int _y = y; _y < y + tileHeight; _y++) {
				if (_x < 0 || _y < 0 || _x >= xMapSize || _y >= yMapSize) {
					return false;
				}
				Tile tile = tileArray[z][_x][_y];
				if (tile != null && tile.count >= 5) {
					return false;
				}
			}
		}
		InteractableObject interactableObject = new InteractableObject();
		interactableObject.uid = j2;
		interactableObject.aByte530 = byte0;
		interactableObject.z = z;
		interactableObject.worldX = worldX;
		interactableObject.worldY = worldY;
		interactableObject.worldZ = worldZ;
		interactableObject.animable = animable;
		interactableObject.rotation = rotation;
		interactableObject.tileLeft = x;
		interactableObject.tileTop = y;
		interactableObject.tileRight = (x + tileWidth) - 1;
		interactableObject.tileBottom = (y + tileHeight) - 1;
		for (int xx = x; xx < x + tileWidth; xx++) {
			for (int yy = y; yy < y + tileHeight; yy++) {
				int k3 = 0;
				if (xx > x)
					k3++;
				if (xx < (x + tileWidth) - 1)
					k3 += 4;
				if (yy > y)
					k3 += 8;
				if (yy < (y + tileHeight) - 1)
					k3 += 2;
				for (int zz = z; zz >= 0; zz--) {
					if (tileArray[zz][xx][yy] == null) {
						tileArray[zz][xx][yy] = new Tile(zz, xx, yy);
					}
				}
				Tile tile = tileArray[z][xx][yy];
				tile.interactableObjects[tile.count] = interactableObject;
				tile.anIntArray1319[tile.count] = k3;
				tile.anInt1320 |= k3;
				tile.count++;
			}
		}
		if (flag) {
			interactableObjectCache[interactableObjectCacheCurrPos++] = interactableObject;
		}
		return true;
	}

	public void clearInteractableObjectCache() {
		for (int index = 0; index < interactableObjectCacheCurrPos; index++) {
			InteractableObject interactableObject = interactableObjectCache[index];
			remove(interactableObject);
			interactableObjectCache[index] = null;
		}
		interactableObjectCacheCurrPos = 0;
	}

	private void remove(InteractableObject interactableObject) {
		for (int x = interactableObject.tileLeft; x <= interactableObject.tileRight; x++) {
			for (int y = interactableObject.tileTop; y <= interactableObject.tileBottom; y++) {
				Tile tile = tileArray[interactableObject.z][x][y];
				if (tile != null) {
					for (int index = 0; index < tile.count; index++) {
						if (tile.interactableObjects[index] != interactableObject) {
							continue;
						}
						tile.count--;
						for (int index_2 = index; index_2 < tile.count; index_2++) {
							tile.interactableObjects[index_2] = tile.interactableObjects[index_2 + 1];
							tile.anIntArray1319[index_2] = tile.anIntArray1319[index_2 + 1];
						}
						tile.interactableObjects[tile.count] = null;
						break;
					}
					tile.anInt1320 = 0;
					for (int index = 0; index < tile.count; index++) {
						tile.anInt1320 |= tile.anIntArray1319[index];
					}
				}
			}
		}
	}

	public void method290(int i, int k, int l, int i1) {
		Tile tile = tileArray[i1][l][i];
		if (tile == null)
			return;
		WallDecoration wallDecoration = tile.wallDecoration;
		if (wallDecoration != null) {
			int j1 = l * 128 + 64;
			int k1 = i * 128 + 64;
			wallDecoration.x = j1 + ((wallDecoration.x - j1) * k) / 16;
			wallDecoration.y = k1 + ((wallDecoration.y - k1) * k) / 16;
		}
	}

	public void removeWallObject(int x, int z, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			tile.wallObject = null;
		}
	}

	public void removeWallDecoration(int y, int z, int x) {
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			tile.wallDecoration = null;
		}
	}

	public void method293(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile == null) {
			return;
		}
		for (int j1 = 0; j1 < tile.count; j1++) {
			InteractableObject interactableObject = tile.interactableObjects[j1];
			if ((interactableObject.uid >> 29 & 3) == 2 && interactableObject.tileLeft == x && interactableObject.tileTop == y) {
				remove(interactableObject);
				return;
			}
		}
	}

	public void removeGroundDecoration(int z, int y, int x) {
		Tile tile = tileArray[z][x][y];
		if (tile == null) {
			return;
		}
		tile.groundDecoration = null;
	}

	public void removeGroundItemTile(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile != null) {
			tile.groundItemTile = null;
		}
	}

	public WallObject getWallObject(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile == null) {
			return null;
		} else {
			return tile.wallObject;
		}
	}

	public WallDecoration getWallDecoration(int x, int y, int z) {
		Tile tile = tileArray[z][x][y];
		if (tile == null) {
			return null;
		} else {
			return tile.wallDecoration;
		}
	}

	public InteractableObject getInteractableObject(int x, int y, int z) {
		Tile tile = tileArray[z][x][y];
		if (tile == null) {
			return null;
		}
		for (int index = 0; index < tile.count; index++) {
			InteractableObject interactableOBject = tile.interactableObjects[index];
			if ((interactableOBject.uid >> 29 & 3) == 2 && interactableOBject.tileLeft == x && interactableOBject.tileTop == y) {
				return interactableOBject;
			}
		}
		return null;
	}

	public GroundDecoration getGroundDecoration(int y, int x, int z) {
		Tile tile = tileArray[z][x][y];
		if (tile == null || tile.groundDecoration == null) {
			return null;
		} else {
			return tile.groundDecoration;
		}
	}

	public int getWallObjectUID(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile == null || tile.wallObject == null)
			return 0;
		else
			return tile.wallObject.uid;
	}

	public int getWallDecorationUID(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile == null || tile.wallDecoration == null)
			return 0;
		else
			return tile.wallDecoration.uid;
	}

	public int getInteractableObjectUID(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile == null)
			return 0;
		for (int l = 0; l < tile.count; l++) {
			InteractableObject interactableObject = tile.interactableObjects[l];
			if ((interactableObject.uid >> 29 & 3) == 2 && interactableObject.tileLeft == x && interactableObject.tileTop == y)
				return interactableObject.uid;
		}
		return 0;
	}

	public int getGroundDecorationUID(int z, int x, int y) {
		Tile tile = tileArray[z][x][y];
		if (tile == null || tile.groundDecoration == null) {
			return 0;
		} else {
			return tile.groundDecoration.uid;
		}
	}

	public int getIdTagForPosition(int z, int x, int y, int interactableObjectUID) {
		Tile tile = tileArray[z][x][y];
		if (tile == null) {
			return -1;
		}
		if (tile.wallObject != null && tile.wallObject.uid == interactableObjectUID) {
			return tile.wallObject.aByte281 & 0xff;
		}
		if (tile.wallDecoration != null && tile.wallDecoration.uid == interactableObjectUID) {
			return tile.wallDecoration.aByte506 & 0xff;
		}
		if (tile.groundDecoration != null && tile.groundDecoration.uid == interactableObjectUID) {
			return tile.groundDecoration.objectConfig & 0xff;
		}
		for (int index = 0; index < tile.count; index++) {
			if (tile.interactableObjects[index].uid == interactableObjectUID) {
				return tile.interactableObjects[index].aByte530 & 0xff;
			}
		}
		return -1;
	}

	public void shadeModels(int y, int x, int z) {
		int lightness = 64;// was parameter
		int mag_multiplier = 768;// was parameter
		int dist_from_origin = (int) Math.sqrt(x * x + y * y + z * z);
		int l_magnitude = mag_multiplier * dist_from_origin >> 8;
		for (int zz = 0; zz < zMapSize; zz++) {
			for (int xx = 0; xx < xMapSize; xx++) {
				for (int yy = 0; yy < yMapSize; yy++) {
					Tile tile = tileArray[zz][xx][yy];
					if (tile != null) {
						WallObject wallObject = tile.wallObject;
						if (wallObject != null && wallObject.node1 != null && wallObject.node1.vertexNormals != null) {
							method307(zz, 1, 1, xx, yy, (Model) wallObject.node1);
							if (wallObject.node2 != null && wallObject.node2.vertexNormals != null) {
								method307(zz, 1, 1, xx, yy, (Model) wallObject.node2);
								method308((Model) wallObject.node1, (Model) wallObject.node2, 0, 0, 0, false);
								((Model) wallObject.node2).doShading(lightness, l_magnitude, x, y, z);
							}
							((Model) wallObject.node1).doShading(lightness, l_magnitude, x, y, z);
						}
						for (int k2 = 0; k2 < tile.count; k2++) {
							InteractableObject interactableObject = tile.interactableObjects[k2];
							if (interactableObject != null && interactableObject.animable != null && interactableObject.animable.vertexNormals != null) {
								method307(zz, (interactableObject.tileRight - interactableObject.tileLeft) + 1, (interactableObject.tileBottom - interactableObject.tileTop) + 1, xx, yy, (Model) interactableObject.animable);
								((Model) interactableObject.animable).doShading(lightness, l_magnitude, x, y, z);
							}
						}
						GroundDecoration groundDecoration = tile.groundDecoration;
						if (groundDecoration != null && groundDecoration.animable.vertexNormals != null) {
							method306(xx, zz, (Model) groundDecoration.animable, yy);
							((Model) groundDecoration.animable).doShading(lightness, l_magnitude, x, y, z);
						}
					}
				}
			}
		}
	}

	private void method306(int x, int z, Model model, int y) {
		if (x < xMapSize) {
			Tile tile = tileArray[z][x + 1][y];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.animable.vertexNormals != null)
				method308(model, (Model) tile.groundDecoration.animable, 128, 0, 0, true);
		}
		if (y < xMapSize) {
			Tile tile = tileArray[z][x][y + 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.animable.vertexNormals != null)
				method308(model, (Model) tile.groundDecoration.animable, 0, 0, 128, true);
		}
		if (x < xMapSize && y < yMapSize) {
			Tile tile = tileArray[z][x + 1][y + 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.animable.vertexNormals != null)
				method308(model, (Model) tile.groundDecoration.animable, 128, 0, 128, true);
		}
		if (x < xMapSize && y > 0) {
			Tile tile = tileArray[z][x + 1][y - 1];
			if (tile != null && tile.groundDecoration != null && tile.groundDecoration.animable.vertexNormals != null)
				method308(model, (Model) tile.groundDecoration.animable, 128, 0, -128, true);
		}
	}

	private void method307(int i, int j, int k, int l, int i1, Model model) {
		boolean flag = true;
		int j1 = l;
		int k1 = l + j;
		int l1 = i1 - 1;
		int i2 = i1 + k;
		for (int z = i; z <= i + 1; z++) {
			if (z != zMapSize) {
				for (int x = j1; x <= k1; x++) {
					if (x >= 0 && x < xMapSize) {
						for (int y = l1; y <= i2; y++) {
							if (y >= 0 && y < yMapSize && (!flag || x >= k1 || y >= i2 || y < i1 && x != l)) {
								Tile tile = tileArray[z][x][y];
								if (tile != null) {
									int i3 = (heightMap[z][x][y] + heightMap[z][x + 1][y] + heightMap[z][x][y + 1] + heightMap[z][x + 1][y + 1]) / 4 - (heightMap[i][l][i1] + heightMap[i][l + 1][i1] + heightMap[i][l][i1 + 1] + heightMap[i][l + 1][i1 + 1]) / 4;
									WallObject wallObject = tile.wallObject;
									if (wallObject != null && wallObject.node1 != null && wallObject.node1.vertexNormals != null)
										method308(model, (Model) wallObject.node1, (x - l) * 128 + (1 - j) * 64, i3, (y - i1) * 128 + (1 - k) * 64, flag);
									if (wallObject != null && wallObject.node2 != null && wallObject.node2.vertexNormals != null)
										method308(model, (Model) wallObject.node2, (x - l) * 128 + (1 - j) * 64, i3, (y - i1) * 128 + (1 - k) * 64, flag);
									for (int index = 0; index < tile.count; index++) {
										InteractableObject interactableObject = tile.interactableObjects[index];
										if (interactableObject != null && interactableObject.animable != null && interactableObject.animable.vertexNormals != null) {
											int k3 = (interactableObject.tileRight - interactableObject.tileLeft) + 1;
											int l3 = (interactableObject.tileBottom - interactableObject.tileTop) + 1;
											method308(model, (Model) interactableObject.animable, (interactableObject.tileLeft - l) * 128 + (k3 - j) * 64, i3, (interactableObject.tileTop - i1) * 128 + (l3 - k) * 64, flag);
										}
									}
								}
							}
						}
					}
				}
				j1--;
				flag = false;
			}
		}
	}

	private void method308(Model model, Model model_1, int i, int j, int k, boolean flag) {
		anInt488++;
		int l = 0;
		int ai[] = model_1.vertexX;
		int i1 = model_1.anInt1626;
		for (int j1 = 0; j1 < model.anInt1626; j1++) {
			VertexNormal vertexNormal = model.vertexNormals[j1];
			VertexNormal vertexNormal_1 = model.vertexNormalOffset[j1];
			if (vertexNormal_1.magnitude != 0) {
				int i2 = model.vertexY[j1] - j;
				if (i2 <= model_1.maxY) {
					int j2 = model.vertexX[j1] - i;
					if (j2 >= model_1.minX && j2 <= model_1.maxX) {
						int k2 = model.vertexZ[j1] - k;
						if (k2 >= model_1.minZ && k2 <= model_1.maxZ) {
							for (int l2 = 0; l2 < i1; l2++) {
								VertexNormal normal = model_1.vertexNormals[l2];
								VertexNormal normal_offsets = model_1.vertexNormalOffset[l2];
								if (j2 == ai[l2] && k2 == model_1.vertexZ[l2] && i2 == model_1.vertexY[l2] && normal_offsets.magnitude != 0) {
									vertexNormal.x += normal_offsets.x;
									vertexNormal.y += normal_offsets.y;
									vertexNormal.z += normal_offsets.z;
									vertexNormal.magnitude += normal_offsets.magnitude;
									normal.x += vertexNormal_1.x;
									normal.y += vertexNormal_1.y;
									normal.z += vertexNormal_1.z;
									normal.magnitude += vertexNormal_1.magnitude;
									l++;
									anIntArray486[j1] = anInt488;
									anIntArray487[l2] = anInt488;
								}
							}

						}
					}
				}
			}
		}
		if (l < 3 || !flag) {
			return;
		}
		for (int index = 0; index < model.triangleCount; index++) {
			if (anIntArray486[model.triangleA[index]] == anInt488 && anIntArray486[model.triangleB[index]] == anInt488 && anIntArray486[model.triangleC[index]] == anInt488) {
				model.triangleDrawType[index] = -1;
			}
		}
		for (int index = 0; index < model_1.triangleCount; index++) {
			if (anIntArray487[model_1.triangleA[index]] == anInt488 && anIntArray487[model_1.triangleB[index]] == anInt488 && anIntArray487[model_1.triangleC[index]] == anInt488) {
				model_1.triangleDrawType[index] = -1;
			}
		}
	}

	public void drawMinimapTile(int pixels[], int pixelPointer, int z, int x, int y) {
		int scanLength = 512;
		Tile tile = tileArray[z][x][y];
		if (tile == null) {
			return;
		}
		PlainTile plainTile = tile.plainTile;
		if (plainTile != null) {
			int tileRGB = plainTile.colorRGB;
			if (tileRGB == 0) {
				return;
			}
			for (int index = 0; index < 4; index++) {
				pixels[pixelPointer] = tileRGB;
				pixels[pixelPointer + 1] = tileRGB;
				pixels[pixelPointer + 2] = tileRGB;
				pixels[pixelPointer + 3] = tileRGB;
				pixelPointer += scanLength;
			}
			return;
		}
		ShapedTile shpedTile = tile.shapedTile;
		if (shpedTile == null) {
			return;
		}
		int shape = shpedTile.shape;
		int rotation = shpedTile.rotation;
		int colorRGB = shpedTile.colorRGB;
		int colorARGB = shpedTile.colorARGB;
		int shapePoints[] = tileShapePoints[shape];
		int shapeIndices[] = tileShapeIndices[rotation];
		int shapePointer = 0;
		if (colorRGB != 0) {
			for (int linePointer = 0; linePointer < 4; linePointer++) {
				pixels[pixelPointer] = shapePoints[shapeIndices[shapePointer++]] != 0 ? colorARGB : colorRGB;
				pixels[pixelPointer + 1] = shapePoints[shapeIndices[shapePointer++]] != 0 ? colorARGB : colorRGB;
				pixels[pixelPointer + 2] = shapePoints[shapeIndices[shapePointer++]] != 0 ? colorARGB : colorRGB;
				pixels[pixelPointer + 3] = shapePoints[shapeIndices[shapePointer++]] != 0 ? colorARGB : colorRGB;
				pixelPointer += scanLength;
			}

			return;
		}
		for (int linePointer = 0; linePointer < 4; linePointer++) {
			if (shapePoints[shapeIndices[shapePointer++]] != 0)
				pixels[pixelPointer] = colorARGB;
			if (shapePoints[shapeIndices[shapePointer++]] != 0)
				pixels[pixelPointer + 1] = colorARGB;
			if (shapePoints[shapeIndices[shapePointer++]] != 0)
				pixels[pixelPointer + 2] = colorARGB;
			if (shapePoints[shapeIndices[shapePointer++]] != 0)
				pixels[pixelPointer + 3] = colorARGB;
			pixelPointer += scanLength;
		}
	}

	public static void setupViewport(int minZ, int maxZ, int width, int height, int pixels[]) {
		left = 0;
		top = 0;
		right = width;
		bottom = height;
		midX = width / 2;
		midY = height / 2;
		boolean isTileOnScreen[][][][] = new boolean[9][32][53][53];
		for (int angleY = 128; angleY <= 384; angleY += 32) {
			for (int angleX = 0; angleX < 2048; angleX += 64) {
				yCurveSine = Model.SINE[angleY];
				yCurveCosine = Model.COSINE[angleY];
				xCurveSine = Model.SINE[angleX];
				xCurveCosine = Model.COSINE[angleX];
				int angleXPointer = (angleY - 128) / 32;
				int angleYPointer = angleX / 64;
				for (int x = -26; x <= 26; x++) {
					for (int y = -26; y <= 26; y++) {
						int worldX = x * 128;
						int worldY = y * 128;
						boolean isVisible = false;
						for (int worldZ = -minZ; worldZ <= maxZ; worldZ += 128) {
							if (!isOnScreen(pixels[angleXPointer] + worldZ, worldY, worldX)) {
								continue;
							}
							isVisible = true;
							break;
						}
						isTileOnScreen[angleXPointer][angleYPointer][x + 25 + 1][y + 25 + 1] = isVisible;
					}
				}
			}
		}
		for (int angleYPointer = 0; angleYPointer < 8; angleYPointer++) {
			for (int angleXPointer = 0; angleXPointer < 32; angleXPointer++) {
				for (int relativeX = -25; relativeX < 25; relativeX++) {
					for (int relativeZ = -25; relativeZ < 25; relativeZ++) {
						boolean isVisible = false;
						label0: for (int l3 = -1; l3 <= 1; l3++) {
							for (int j4 = -1; j4 <= 1; j4++) {
								if (isTileOnScreen[angleYPointer][angleXPointer][relativeX + l3 + 25 + 1][relativeZ + j4 + 25 + 1])
									isVisible = true;
								else if (isTileOnScreen[angleYPointer][(angleXPointer + 1) % 31][relativeX + l3 + 25 + 1][relativeZ + j4 + 25 + 1])
									isVisible = true;
								else if (isTileOnScreen[angleYPointer + 1][angleXPointer][relativeX + l3 + 25 + 1][relativeZ + j4 + 25 + 1]) {
									isVisible = true;
								} else {
									if (!isTileOnScreen[angleYPointer + 1][(angleXPointer + 1) % 31][relativeX + l3 + 25 + 1][relativeZ + j4 + 25 + 1])
										continue;
									isVisible = true;
								}
								break label0;
							}
						}
						TILE_VISIBILITY_MAPS[angleYPointer][angleXPointer][relativeX + 25][relativeZ + 25] = isVisible;
					}
				}
			}
		}
	}

	private static boolean isOnScreen(int z, int y, int x) {
		int l = y * xCurveSine + x * xCurveCosine >> 16;
		int i1 = y * xCurveCosine - x * xCurveSine >> 16;
		int j1 = z * yCurveSine + i1 * yCurveCosine >> 16;
		int k1 = z * yCurveCosine - i1 * yCurveSine >> 16;
		if (j1 < 50 || j1 > 3500)
			return false;
		int l1 = midX + (l << 9) / j1;
		int i2 = midY + (k1 << 9) / j1;
		return l1 >= left && l1 <= right && i2 >= top && i2 <= bottom;
	}

	public void request2DTrace(int i, int j) {
		isClicked = true;
		clickX = j;
		clickY = i;
		clickTileX = -1;
		clickTileY = -1;
	}

	public void render(int cameraPosX, int cameraPosY, int curveX, int cameraPosZ, int plane, int curveY) {
		if (cameraPosX < 0)
			cameraPosX = 0;
		else if (cameraPosX >= xMapSize * 128)
			cameraPosX = xMapSize * 128 - 1;
		if (cameraPosY < 0)
			cameraPosY = 0;
		else if (cameraPosY >= yMapSize * 128)
			cameraPosY = yMapSize * 128 - 1;
		anInt448++;
		yCurveSine = Model.SINE[curveY];
		yCurveCosine = Model.COSINE[curveY];
		xCurveSine = Model.SINE[curveX];
		xCurveCosine = Model.COSINE[curveX];
		TILE_VISIBILITY_MAP = TILE_VISIBILITY_MAPS[(curveY - 128) / 32][curveX / 64];
		cameraPositionX = cameraPosX;
		cameraPositionZ = cameraPosZ;
		cameraPositionY = cameraPosY;
		cameraTilePositionX = cameraPosX / 128;
		cameraTilePositionY = cameraPosY / 128;
		SceneGraph.plane = plane;
		anInt449 = cameraTilePositionX - 25;
		if (anInt449 < 0)
			anInt449 = 0;
		anInt451 = cameraTilePositionY - 25;
		if (anInt451 < 0)
			anInt451 = 0;
		anInt450 = cameraTilePositionX + 25;
		if (anInt450 > xMapSize)
			anInt450 = xMapSize;
		anInt452 = cameraTilePositionY + 25;
		if (anInt452 > yMapSize)
			anInt452 = yMapSize;
		processCulling();
		anInt446 = 0;
		for (int z = currentHeight; z < zMapSize; z++) {
			Tile tiles[][] = tileArray[z];
			for (int x = anInt449; x < anInt450; x++) {
				for (int y = anInt451; y < anInt452; y++) {
					Tile tile = tiles[x][y];
					if (tile != null) {
						if (tile.logicHeight > plane || !TILE_VISIBILITY_MAP[(x - cameraTilePositionX) + 25][(y - cameraTilePositionY) + 25] && heightMap[z][x][y] - cameraPosZ < 2000) {
							tile.aBoolean1322 = false;
							tile.aBoolean1323 = false;
							tile.anInt1325 = 0;
						} else {
							tile.aBoolean1322 = true;
							tile.aBoolean1323 = true;
							tile.aBoolean1324 = tile.count > 0;
							anInt446++;
						}
					}
				}
			}
		}

		for (int z = currentHeight; z < zMapSize; z++) {
			Tile tiles[][] = tileArray[z];
			for (int xx = -25; xx <= 0; xx++) {
				int x = cameraTilePositionX + xx;
				int x2 = cameraTilePositionX - xx;
				if (x >= anInt449 || x2 < anInt450) {
					for (int yy = -25; yy <= 0; yy++) {
						int y = cameraTilePositionY + yy;
						int y2 = cameraTilePositionY - yy;
						if (x >= anInt449) {
							if (y >= anInt451) {
								Tile tile = tiles[x][y];
								if (tile != null && tile.aBoolean1322) {
									renderTile(tile, true);
								}
							}
							if (y2 < anInt452) {
								Tile tile = tiles[x][y2];
								if (tile != null && tile.aBoolean1322) {
									renderTile(tile, true);
								}
							}
						}
						if (x2 < anInt450) {
							if (y >= anInt451) {
								Tile class30_sub3_3 = tiles[x2][y];
								if (class30_sub3_3 != null
										&& class30_sub3_3.aBoolean1322)
									renderTile(class30_sub3_3, true);
							}
							if (y2 < anInt452) {
								Tile class30_sub3_4 = tiles[x2][y2];
								if (class30_sub3_4 != null
										&& class30_sub3_4.aBoolean1322)
									renderTile(class30_sub3_4, true);
							}
						}
						if (anInt446 == 0) {
							isClicked = false;
							return;
						}
					}

				}
			}

		}

		for (int z = currentHeight; z < zMapSize; z++) {
			Tile tiles[][] = tileArray[z];
			for (int xx = -25; xx <= 0; xx++) {
				int x = cameraTilePositionX + xx;
				int x2 = cameraTilePositionX - xx;
				if (x >= anInt449 || x2 < anInt450) {
					for (int yy = -25; yy <= 0; yy++) {
						int y = cameraTilePositionY + yy;
						int y2 = cameraTilePositionY - yy;
						if (x >= anInt449) {
							if (y >= anInt451) {
								Tile tile = tiles[x][y];
								if (tile != null && tile.aBoolean1322)
									renderTile(tile, false);
							}
							if (y2 < anInt452) {
								Tile tile = tiles[x][y2];
								if (tile != null && tile.aBoolean1322)
									renderTile(tile, false);
							}
						}
						if (x2 < anInt450) {
							if (y >= anInt451) {
								Tile tile = tiles[x2][y];
								if (tile != null && tile.aBoolean1322)
									renderTile(tile, false);
							}
							if (y2 < anInt452) {
								Tile tile = tiles[x2][y2];
								if (tile != null && tile.aBoolean1322)
									renderTile(tile, false);
							}
						}
						if (anInt446 == 0) {
							isClicked = false;
							return;
						}
					}
				}
			}
		}
		isClicked = false;
	}

	private void renderTile(Tile tile, boolean flag) {
		aClass19_477.append(tile);
		do {
			Tile newTile;
			do {
				newTile = (Tile) aClass19_477.popHead();
				if (newTile == null) {
					return;
				}
			} while (!newTile.aBoolean1323);
			int x = newTile.anInt1308;
			int y = newTile.anInt1309;
			int k = newTile.tileZ;
			int l = newTile.anInt1310;
			Tile tiles[][] = tileArray[k];
			if (newTile.aBoolean1322) {
				if (flag) {
					if (k > 0) {
						Tile _tile = tileArray[k - 1][x][y];
						if (_tile != null && _tile.aBoolean1323)
							continue;
					}
					if (x <= cameraTilePositionX && x > anInt449) {
						Tile _tile = tiles[x - 1][y];
						if (_tile != null && _tile.aBoolean1323 && (_tile.aBoolean1322 || (newTile.anInt1320 & 1) == 0))
							continue;
					}
					if (x >= cameraTilePositionX && x < anInt450 - 1) {
						Tile _tile = tiles[x + 1][y];
						if (_tile != null && _tile.aBoolean1323 && (_tile.aBoolean1322 || (newTile.anInt1320 & 4) == 0))
							continue;
					}
					if (y <= cameraTilePositionY && y > anInt451) {
						Tile _tile = tiles[x][y - 1];
						if (_tile != null && _tile.aBoolean1323 && (_tile.aBoolean1322 || (newTile.anInt1320 & 8) == 0))
							continue;
					}
					if (y >= cameraTilePositionY && y < anInt452 - 1) {
						Tile _tile = tiles[x][y + 1];
						if (_tile != null && _tile.aBoolean1323 && (_tile.aBoolean1322 || (newTile.anInt1320 & 2) == 0))
							continue;
					}
				} else {
					flag = true;
				}
				newTile.aBoolean1322 = false;
				if (newTile.tileBelow0 != null) {
					Tile ground = newTile.tileBelow0;
					if (ground.plainTile != null) {
						if (!method320(0, x, y)) {
							renderPlainTile(ground.plainTile, 0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, x, y);
						}
					} else if (ground.shapedTile != null && !method320(0, x, y)) {
						renderShapedTile(x, yCurveSine, xCurveSine, ground.shapedTile, yCurveCosine, y, xCurveCosine);
					}
					WallObject wallObject = ground.wallObject;
					if (wallObject != null) {
						wallObject.node1.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, wallObject.x - cameraPositionX, wallObject.z - cameraPositionZ, wallObject.y - cameraPositionY, wallObject.uid);
					}
					for (int index = 0; index < ground.count; index++) {
						InteractableObject interactableObject = ground.interactableObjects[index];
						if (interactableObject != null) {
							interactableObject.animable.renderAtPoint(interactableObject.rotation, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, interactableObject.worldX - cameraPositionX, interactableObject.worldZ - cameraPositionZ, interactableObject.worldY - cameraPositionY, interactableObject.uid);
						}
					}
				}
				boolean flag1 = false;
				if (newTile.plainTile != null) {
					if (!method320(l, x, y)) {
						flag1 = true;
						renderPlainTile(newTile.plainTile, l, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, x, y);
					}
				} else if (newTile.shapedTile != null && !method320(l, x, y)) {
					flag1 = true;
					renderShapedTile(x, yCurveSine, xCurveSine, newTile.shapedTile, yCurveCosine, y, xCurveCosine);
				}
				int j1 = 0;
				int j2 = 0;
				WallObject wallObject = newTile.wallObject;
				WallDecoration wallDecoration = newTile.wallDecoration;
				if (wallObject != null || wallDecoration != null) {
					if (cameraTilePositionX == x)
						j1++;
					else if (cameraTilePositionX < x)
						j1 += 2;
					if (cameraTilePositionY == y)
						j1 += 3;
					else if (cameraTilePositionY > y)
						j1 += 6;
					j2 = anIntArray478[j1];
					newTile.anInt1328 = anIntArray480[j1];
				}
				if (wallObject != null) {
					if ((wallObject.orientation & anIntArray479[j1]) != 0) {
						if (wallObject.orientation == 16) {
							newTile.anInt1325 = 3;
							newTile.anInt1326 = anIntArray481[j1];
							newTile.anInt1327 = 3 - newTile.anInt1326;
						} else if (wallObject.orientation == 32) {
							newTile.anInt1325 = 6;
							newTile.anInt1326 = anIntArray482[j1];
							newTile.anInt1327 = 6 - newTile.anInt1326;
						} else if (wallObject.orientation == 64) {
							newTile.anInt1325 = 12;
							newTile.anInt1326 = anIntArray483[j1];
							newTile.anInt1327 = 12 - newTile.anInt1326;
						} else {
							newTile.anInt1325 = 9;
							newTile.anInt1326 = anIntArray484[j1];
							newTile.anInt1327 = 9 - newTile.anInt1326;
						}
					} else {
						newTile.anInt1325 = 0;
					}
					if ((wallObject.orientation & j2) != 0 && !method321(l, x, y, wallObject.orientation))
						wallObject.node1.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, wallObject.x - cameraPositionX, wallObject.z - cameraPositionZ, wallObject.y - cameraPositionY, wallObject.uid);
					if ((wallObject.orientation1 & j2) != 0 && !method321(l, x, y, wallObject.orientation1))
						wallObject.node2.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, wallObject.x - cameraPositionX, wallObject.z - cameraPositionZ, wallObject.y - cameraPositionY, wallObject.uid);
				}
				if (wallDecoration != null && !method322(l, x, y, wallDecoration.node.modelHeight))
					if ((wallDecoration.configBits & j2) != 0) {
						wallDecoration.node.renderAtPoint(wallDecoration.face, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, wallDecoration.x - cameraPositionX, wallDecoration.z - cameraPositionZ, wallDecoration.y - cameraPositionY, wallDecoration.uid);
					} else if ((wallDecoration.configBits & 0x300) != 0) {
						int j4 = wallDecoration.x - cameraPositionX;
						int l5 = wallDecoration.z - cameraPositionZ;
						int k6 = wallDecoration.y - cameraPositionY;
						int i8 = wallDecoration.face;
						int k9;
						if (i8 == 1 || i8 == 2)
							k9 = -j4;
						else
							k9 = j4;
						int k10;
						if (i8 == 2 || i8 == 3)
							k10 = -k6;
						else
							k10 = k6;
						if ((wallDecoration.configBits & 0x100) != 0 && k10 < k9) {
							int i11 = j4 + anIntArray463[i8];
							int k11 = k6 + anIntArray464[i8];
							wallDecoration.node.renderAtPoint(i8 * 512 + 256, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, i11, l5, k11, wallDecoration.uid);
						}
						if ((wallDecoration.configBits & 0x200) != 0 && k10 > k9) {
							int j11 = j4 + anIntArray465[i8];
							int l11 = k6 + anIntArray466[i8];
							wallDecoration.node.renderAtPoint(i8 * 512 + 1280 & 0x7ff, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, j11, l5, l11, wallDecoration.uid);
						}
					}
				if (flag1) {
					GroundDecoration groundDecoration = newTile.groundDecoration;
					if (groundDecoration != null) {
						groundDecoration.animable.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, groundDecoration.x - cameraPositionX, groundDecoration.z - cameraPositionZ, groundDecoration.y - cameraPositionY, groundDecoration.uid);
					}
					GroundItemTile groundItemTile = newTile.groundItemTile;
					if (groundItemTile != null && groundItemTile.anInt52 == 0) {
						if (groundItemTile.secondGroundItem != null) {
							groundItemTile.secondGroundItem.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, groundItemTile.x - cameraPositionX, groundItemTile.z - cameraPositionZ, groundItemTile.y - cameraPositionY, groundItemTile.uid);
						}
						if (groundItemTile.thirdGroundItem != null) {
							groundItemTile.thirdGroundItem.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, groundItemTile.x - cameraPositionX, groundItemTile.z - cameraPositionZ, groundItemTile.y - cameraPositionY, groundItemTile.uid);
						}
						if (groundItemTile.firstGroundItem != null) {
							groundItemTile.firstGroundItem.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, groundItemTile.x - cameraPositionX, groundItemTile.z - cameraPositionZ, groundItemTile.y - cameraPositionY, groundItemTile.uid);
						}
					}
				}
				int k4 = newTile.anInt1320;
				if (k4 != 0) {
					if (x < cameraTilePositionX && (k4 & 4) != 0) {
						Tile _tile = tiles[x + 1][y];
						if (_tile != null && _tile.aBoolean1323)
							aClass19_477.append(_tile);
					}
					if (y < cameraTilePositionY && (k4 & 2) != 0) {
						Tile _tile = tiles[x][y + 1];
						if (_tile != null && _tile.aBoolean1323)
							aClass19_477.append(_tile);
					}
					if (x > cameraTilePositionX && (k4 & 1) != 0) {
						Tile _tile = tiles[x - 1][y];
						if (_tile != null && _tile.aBoolean1323)
							aClass19_477.append(_tile);
					}
					if (y > cameraTilePositionY && (k4 & 8) != 0) {
						Tile _tile = tiles[x][y - 1];
						if (_tile != null && _tile.aBoolean1323)
							aClass19_477.append(_tile);
					}
				}
			}
			if (newTile.anInt1325 != 0) {
				boolean flag2 = true;
				for (int k1 = 0; k1 < newTile.count; k1++) {
					if (newTile.interactableObjects[k1].anInt528 == anInt448 || (newTile.anIntArray1319[k1] & newTile.anInt1325) != newTile.anInt1326)
						continue;
					flag2 = false;
					break;
				}
				if (flag2) {
					WallObject wallObject = newTile.wallObject;
					if (!method321(l, x, y, wallObject.orientation))
						wallObject.node1.renderAtPoint(0, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, wallObject.x - cameraPositionX, wallObject.z - cameraPositionZ, wallObject.y - cameraPositionY, wallObject.uid);
					newTile.anInt1325 = 0;
				}
			}
			if (newTile.aBoolean1324)
				try {
					int i1 = newTile.count;
					newTile.aBoolean1324 = false;
					int l1 = 0;
					label0: for (int k2 = 0; k2 < i1; k2++) {
						InteractableObject interactableObject = newTile.interactableObjects[k2];
						if (interactableObject.anInt528 == anInt448)
							continue;
						for (int k3 = interactableObject.tileLeft; k3 <= interactableObject.tileRight; k3++) {
							for (int l4 = interactableObject.tileTop; l4 <= interactableObject.tileBottom; l4++) {
								Tile class30_sub3_21 = tiles[k3][l4];
								if (class30_sub3_21.aBoolean1322) {
									newTile.aBoolean1324 = true;
								} else {
									if (class30_sub3_21.anInt1325 == 0)
										continue;
									int l6 = 0;
									if (k3 > interactableObject.tileLeft)
										l6++;
									if (k3 < interactableObject.tileRight)
										l6 += 4;
									if (l4 > interactableObject.tileTop)
										l6 += 8;
									if (l4 < interactableObject.tileBottom)
										l6 += 2;
									if ((l6 & class30_sub3_21.anInt1325) != newTile.anInt1327)
										continue;
									newTile.aBoolean1324 = true;
								}
								continue label0;
							}

						}

						interactableObjects[l1++] = interactableObject;
						int i5 = cameraTilePositionX - interactableObject.tileLeft;
						int i6 = interactableObject.tileRight - cameraTilePositionX;
						if (i6 > i5)
							i5 = i6;
						int i7 = cameraTilePositionY - interactableObject.tileTop;
						int j8 = interactableObject.tileBottom - cameraTilePositionY;
						if (j8 > i7)
							interactableObject.anInt527 = i5 + j8;
						else
							interactableObject.anInt527 = i5 + i7;
					}

					while (l1 > 0) {
						int i3 = -50;
						int l3 = -1;
						for (int j5 = 0; j5 < l1; j5++) {
							InteractableObject interactableObject = interactableObjects[j5];
							if (interactableObject.anInt528 != anInt448)
								if (interactableObject.anInt527 > i3) {
									i3 = interactableObject.anInt527;
									l3 = j5;
								} else if (interactableObject.anInt527 == i3) {
									int j7 = interactableObject.worldX - cameraPositionX;
									int k8 = interactableObject.worldY - cameraPositionY;
									int l9 = interactableObjects[l3].worldX - cameraPositionX;
									int l10 = interactableObjects[l3].worldY - cameraPositionY;
									if (j7 * j7 + k8 * k8 > l9 * l9 + l10 * l10)
										l3 = j5;
								}
						}

						if (l3 == -1)
							break;
						InteractableObject interactableObject = interactableObjects[l3];
						interactableObject.anInt528 = anInt448;
						if (!method323(l, interactableObject.tileLeft, interactableObject.tileRight, interactableObject.tileTop, interactableObject.tileBottom, interactableObject.animable.modelHeight))
							interactableObject.animable.renderAtPoint(interactableObject.rotation, yCurveSine, yCurveCosine, xCurveSine, xCurveCosine, interactableObject.worldX - cameraPositionX, interactableObject.worldZ - cameraPositionZ, interactableObject.worldY - cameraPositionY, interactableObject.uid);
						for (int k7 = interactableObject.tileLeft; k7 <= interactableObject.tileRight; k7++) {
							for (int l8 = interactableObject.tileTop; l8 <= interactableObject.tileBottom; l8++) {
								Tile _tile = tiles[k7][l8];
								if (_tile.anInt1325 != 0)
									aClass19_477.append(_tile);
								else if ((k7 != x || l8 != y) && _tile.aBoolean1323)
									aClass19_477.append(_tile);
							}
						}
					}
					if (newTile.aBoolean1324)
						continue;
				} catch (Exception _ex) {
					newTile.aBoolean1324 = false;
				}
			if (!newTile.aBoolean1323 || newTile.anInt1325 != 0)
				continue;
			if (x <= cameraTilePositionX && x > anInt449) {
				Tile class30_sub3_8 = tiles[x - 1][y];
				if (class30_sub3_8 != null && class30_sub3_8.aBoolean1323)
					continue;
			}
			if (x >= cameraTilePositionX && x < anInt450 - 1) {
				Tile class30_sub3_9 = tiles[x + 1][y];
				if (class30_sub3_9 != null && class30_sub3_9.aBoolean1323)
					continue;
			}
			if (y <= cameraTilePositionY && y > anInt451) {
				Tile class30_sub3_10 = tiles[x][y - 1];
				if (class30_sub3_10 != null && class30_sub3_10.aBoolean1323)
					continue;
			}
			if (y >= cameraTilePositionY && y < anInt452 - 1) {
				Tile class30_sub3_11 = tiles[x][y + 1];
				if (class30_sub3_11 != null && class30_sub3_11.aBoolean1323)
					continue;
			}
			newTile.aBoolean1323 = false;
			anInt446--;
			GroundItemTile object4 = newTile.groundItemTile;
			if (object4 != null && object4.anInt52 != 0) {
				if (object4.secondGroundItem != null)
					object4.secondGroundItem.renderAtPoint(0, yCurveSine,
							yCurveCosine, xCurveSine, xCurveCosine, object4.x
									- cameraPositionX, object4.z - cameraPositionZ
									- object4.anInt52, object4.y
									- cameraPositionY, object4.uid);
				if (object4.thirdGroundItem != null)
					object4.thirdGroundItem.renderAtPoint(0, yCurveSine,
							yCurveCosine, xCurveSine, xCurveCosine, object4.x
									- cameraPositionX, object4.z - cameraPositionZ
									- object4.anInt52, object4.y
									- cameraPositionY, object4.uid);
				if (object4.firstGroundItem != null)
					object4.firstGroundItem.renderAtPoint(0, yCurveSine,
							yCurveCosine, xCurveSine, xCurveCosine, object4.x
									- cameraPositionX, object4.z - cameraPositionZ
									- object4.anInt52, object4.y
									- cameraPositionY, object4.uid);
			}
			if (newTile.anInt1328 != 0) {
				WallDecoration class26 = newTile.wallDecoration;
				if (class26 != null
						&& !method322(l, x, y,
								class26.node.modelHeight))
					if ((class26.configBits & newTile.anInt1328) != 0)
						class26.node.renderAtPoint(
								class26.face, yCurveSine, yCurveCosine, xCurveSine,
								xCurveCosine, class26.x - cameraPositionX,
								class26.z - cameraPositionZ, class26.y
										- cameraPositionY, class26.uid);
					else if ((class26.configBits & 0x300) != 0) {
						int l2 = class26.x - cameraPositionX;
						int j3 = class26.z - cameraPositionZ;
						int i4 = class26.y - cameraPositionY;
						int k5 = class26.face;
						int j6;
						if (k5 == 1 || k5 == 2)
							j6 = -l2;
						else
							j6 = l2;
						int l7;
						if (k5 == 2 || k5 == 3)
							l7 = -i4;
						else
							l7 = i4;
						if ((class26.configBits & 0x100) != 0 && l7 >= j6) {
							int i9 = l2 + anIntArray463[k5];
							int i10 = i4 + anIntArray464[k5];
							class26.node.renderAtPoint(
									k5 * 512 + 256, yCurveSine, yCurveCosine,
									xCurveSine, xCurveCosine, i9, j3, i10,
									class26.uid);
						}
						if ((class26.configBits & 0x200) != 0 && l7 <= j6) {
							int j9 = l2 + anIntArray465[k5];
							int j10 = i4 + anIntArray466[k5];
							class26.node.renderAtPoint(
									k5 * 512 + 1280 & 0x7ff, yCurveSine,
									yCurveCosine, xCurveSine, xCurveCosine, j9, j3, j10,
									class26.uid);
						}
					}
				WallObject class10_2 = newTile.wallObject;
				if (class10_2 != null) {
					if ((class10_2.orientation1 & newTile.anInt1328) != 0
							&& !method321(l, x, y, class10_2.orientation1))
						class10_2.node2.renderAtPoint(0, yCurveSine,
								yCurveCosine, xCurveSine, xCurveCosine,
								class10_2.x - cameraPositionX,
								class10_2.z - cameraPositionZ,
								class10_2.y - cameraPositionY, class10_2.uid);
					if ((class10_2.orientation & newTile.anInt1328) != 0
							&& !method321(l, x, y, class10_2.orientation))
						class10_2.node1.renderAtPoint(0, yCurveSine,
								yCurveCosine, xCurveSine, xCurveCosine,
								class10_2.x - cameraPositionX,
								class10_2.z - cameraPositionZ,
								class10_2.y - cameraPositionY, class10_2.uid);
				}
			}
			if (k < zMapSize - 1) {
				Tile class30_sub3_12 = tileArray[k + 1][x][y];
				if (class30_sub3_12 != null && class30_sub3_12.aBoolean1323)
					aClass19_477.append(class30_sub3_12);
			}
			if (x < cameraTilePositionX) {
				Tile class30_sub3_13 = tiles[x + 1][y];
				if (class30_sub3_13 != null && class30_sub3_13.aBoolean1323)
					aClass19_477.append(class30_sub3_13);
			}
			if (y < cameraTilePositionY) {
				Tile class30_sub3_14 = tiles[x][y + 1];
				if (class30_sub3_14 != null && class30_sub3_14.aBoolean1323)
					aClass19_477.append(class30_sub3_14);
			}
			if (x > cameraTilePositionX) {
				Tile class30_sub3_15 = tiles[x - 1][y];
				if (class30_sub3_15 != null && class30_sub3_15.aBoolean1323)
					aClass19_477.append(class30_sub3_15);
			}
			if (y > cameraTilePositionY) {
				Tile class30_sub3_16 = tiles[x][y - 1];
				if (class30_sub3_16 != null && class30_sub3_16.aBoolean1323)
					aClass19_477.append(class30_sub3_16);
			}
		} while (true);
	}

	private void renderPlainTile(PlainTile plainTile, int i, int j, int k, int l, int i1,
			int j1, int k1) {
		int l1;
		int i2 = l1 = (j1 << 7) - cameraPositionX;
		int j2;
		int k2 = j2 = (k1 << 7) - cameraPositionY;
		int l2;
		int i3 = l2 = i2 + 128;
		int j3;
		int k3 = j3 = k2 + 128;
		int l3 = heightMap[i][j1][k1] - cameraPositionZ;
		int i4 = heightMap[i][j1 + 1][k1] - cameraPositionZ;
		int j4 = heightMap[i][j1 + 1][k1 + 1] - cameraPositionZ;
		int k4 = heightMap[i][j1][k1 + 1] - cameraPositionZ;
		int l4 = k2 * l + i2 * i1 >> 16;
		k2 = k2 * i1 - i2 * l >> 16;
		i2 = l4;
		l4 = l3 * k - k2 * j >> 16;
		k2 = l3 * j + k2 * k >> 16;
		l3 = l4;
		if (k2 < 50)
			return;
		l4 = j2 * l + i3 * i1 >> 16;
		j2 = j2 * i1 - i3 * l >> 16;
		i3 = l4;
		l4 = i4 * k - j2 * j >> 16;
		j2 = i4 * j + j2 * k >> 16;
		i4 = l4;
		if (j2 < 50)
			return;
		l4 = k3 * l + l2 * i1 >> 16;
		k3 = k3 * i1 - l2 * l >> 16;
		l2 = l4;
		l4 = j4 * k - k3 * j >> 16;
		k3 = j4 * j + k3 * k >> 16;
		j4 = l4;
		if (k3 < 50)
			return;
		l4 = j3 * l + l1 * i1 >> 16;
		j3 = j3 * i1 - l1 * l >> 16;
		l1 = l4;
		l4 = k4 * k - j3 * j >> 16;
		j3 = k4 * j + j3 * k >> 16;
		k4 = l4;
		if (j3 < 50)
			return;
		int i5 = Rasterizer.centerX + (i2 << 9) / k2;
		int j5 = Rasterizer.centerY + (l3 << 9) / k2;
		int k5 = Rasterizer.centerX + (i3 << 9) / j2;
		int l5 = Rasterizer.centerY + (i4 << 9) / j2;
		int i6 = Rasterizer.centerX + (l2 << 9) / k3;
		int j6 = Rasterizer.centerY + (j4 << 9) / k3;
		int k6 = Rasterizer.centerX + (l1 << 9) / j3;
		int l6 = Rasterizer.centerY + (k4 << 9) / j3;
		Rasterizer.alpha = 0;
		if ((i6 - k6) * (l5 - l6) - (j6 - l6) * (k5 - k6) > 0) {
			Rasterizer.restrictEdges = i6 < 0 || k6 < 0 || k5 < 0
					|| i6 > RSDrawingArea.centerX || k6 > RSDrawingArea.centerX
					|| k5 > RSDrawingArea.centerX;
			if (isClicked
					&& isMouseWithinTriangle(clickX, clickY, j6, l6, l5, i6, k6, k5)) {
				clickTileX = j1;
				clickTileY = k1;
			}
			if (plainTile.anInt720 == -1) {
				if (plainTile.anInt718 != 0xbc614e)
					Rasterizer.drawShadedTriangle(j6, l6, l5, i6, k6, k5, plainTile.anInt718,
							plainTile.anInt719, plainTile.anInt717);
			} else if (!lowMem) {
				if (plainTile.aBoolean721)
					Rasterizer.drawTexturedTriangle(j6, l6, l5, i6, k6, k5, plainTile.anInt718,
							plainTile.anInt719, plainTile.anInt717, i2, i3, l1, l3,
							i4, k4, k2, j2, j3, plainTile.anInt720);
				else
					Rasterizer.drawTexturedTriangle(j6, l6, l5, i6, k6, k5, plainTile.anInt718,
							plainTile.anInt719, plainTile.anInt717, l2, l1, i3, j4,
							k4, i4, k3, j3, j2, plainTile.anInt720);
			} else {
				int i7 = anIntArray485[plainTile.anInt720];
				Rasterizer.drawShadedTriangle(j6, l6, l5, i6, k6, k5,
						mixColor(i7, plainTile.anInt718),
						mixColor(i7, plainTile.anInt719),
						mixColor(i7, plainTile.anInt717));
			}
		}
		if ((i5 - k5) * (l6 - l5) - (j5 - l5) * (k6 - k5) > 0) {
			Rasterizer.restrictEdges = i5 < 0 || k5 < 0 || k6 < 0
					|| i5 > RSDrawingArea.centerX || k5 > RSDrawingArea.centerX
					|| k6 > RSDrawingArea.centerX;
			if (isClicked
					&& isMouseWithinTriangle(clickX, clickY, j5, l5, l6, i5, k5, k6)) {
				clickTileX = j1;
				clickTileY = k1;
			}
			if (plainTile.anInt720 == -1) {
				if (plainTile.anInt716 != 0xbc614e) {
					Rasterizer.drawShadedTriangle(j5, l5, l6, i5, k5, k6, plainTile.anInt716,
							plainTile.anInt717, plainTile.anInt719);
				}
			} else {
				if (!lowMem) {
					Rasterizer.drawTexturedTriangle(j5, l5, l6, i5, k5, k6, plainTile.anInt716,
							plainTile.anInt717, plainTile.anInt719, i2, i3, l1, l3,
							i4, k4, k2, j2, j3, plainTile.anInt720);
					return;
				}
				int j7 = anIntArray485[plainTile.anInt720];
				Rasterizer.drawShadedTriangle(j5, l5, l6, i5, k5, k6,
						mixColor(j7, plainTile.anInt716),
						mixColor(j7, plainTile.anInt717),
						mixColor(j7, plainTile.anInt719));
			}
		}
	}

	private void renderShapedTile(int i, int j, int k, ShapedTile class40, int l, int i1,
			int j1) {
		int k1 = class40.anIntArray673.length;
		for (int l1 = 0; l1 < k1; l1++) {
			int i2 = class40.anIntArray673[l1] - cameraPositionX;
			int k2 = class40.anIntArray674[l1] - cameraPositionZ;
			int i3 = class40.anIntArray675[l1] - cameraPositionY;
			int k3 = i3 * k + i2 * j1 >> 16;
			i3 = i3 * j1 - i2 * k >> 16;
			i2 = k3;
			k3 = k2 * l - i3 * j >> 16;
			i3 = k2 * j + i3 * l >> 16;
			k2 = k3;
			if (i3 < 50)
				return;
			if (class40.anIntArray682 != null) {
				ShapedTile.anIntArray690[l1] = i2;
				ShapedTile.anIntArray691[l1] = k2;
				ShapedTile.anIntArray692[l1] = i3;
			}
			ShapedTile.anIntArray688[l1] = Rasterizer.centerX + (i2 << 9) / i3;
			ShapedTile.anIntArray689[l1] = Rasterizer.centerY + (k2 << 9) / i3;
		}

		Rasterizer.alpha = 0;
		k1 = class40.anIntArray679.length;
		for (int j2 = 0; j2 < k1; j2++) {
			int l2 = class40.anIntArray679[j2];
			int j3 = class40.anIntArray680[j2];
			int l3 = class40.anIntArray681[j2];
			int i4 = ShapedTile.anIntArray688[l2];
			int j4 = ShapedTile.anIntArray688[j3];
			int k4 = ShapedTile.anIntArray688[l3];
			int l4 = ShapedTile.anIntArray689[l2];
			int i5 = ShapedTile.anIntArray689[j3];
			int j5 = ShapedTile.anIntArray689[l3];
			if ((i4 - j4) * (j5 - i5) - (l4 - i5) * (k4 - j4) > 0) {
				Rasterizer.restrictEdges = i4 < 0 || j4 < 0 || k4 < 0
						|| i4 > RSDrawingArea.centerX || j4 > RSDrawingArea.centerX
						|| k4 > RSDrawingArea.centerX;
				if (isClicked
						&& isMouseWithinTriangle(clickX, clickY, l4, i5, j5, i4, j4, k4)) {
					clickTileX = i;
					clickTileY = i1;
				}
				if (class40.anIntArray682 == null
						|| class40.anIntArray682[j2] == -1) {
					if (class40.anIntArray676[j2] != 0xbc614e)
						Rasterizer.drawShadedTriangle(l4, i5, j5, i4, j4, k4,
								class40.anIntArray676[j2],
								class40.anIntArray677[j2],
								class40.anIntArray678[j2]);
				} else if (!lowMem) {
					if (class40.aBoolean683)
						Rasterizer.drawTexturedTriangle(l4, i5, j5, i4, j4, k4,
								class40.anIntArray676[j2],
								class40.anIntArray677[j2],
								class40.anIntArray678[j2],
								ShapedTile.anIntArray690[0],
								ShapedTile.anIntArray690[1],
								ShapedTile.anIntArray690[3],
								ShapedTile.anIntArray691[0],
								ShapedTile.anIntArray691[1],
								ShapedTile.anIntArray691[3],
								ShapedTile.anIntArray692[0],
								ShapedTile.anIntArray692[1],
								ShapedTile.anIntArray692[3],
								class40.anIntArray682[j2]);
					else
						Rasterizer.drawTexturedTriangle(l4, i5, j5, i4, j4, k4,
								class40.anIntArray676[j2],
								class40.anIntArray677[j2],
								class40.anIntArray678[j2],
								ShapedTile.anIntArray690[l2],
								ShapedTile.anIntArray690[j3],
								ShapedTile.anIntArray690[l3],
								ShapedTile.anIntArray691[l2],
								ShapedTile.anIntArray691[j3],
								ShapedTile.anIntArray691[l3],
								ShapedTile.anIntArray692[l2],
								ShapedTile.anIntArray692[j3],
								ShapedTile.anIntArray692[l3],
								class40.anIntArray682[j2]);
				} else {
					int k5 = anIntArray485[class40.anIntArray682[j2]];
					Rasterizer.drawShadedTriangle(l4, i5, j5, i4, j4, k4,
							mixColor(k5, class40.anIntArray676[j2]),
							mixColor(k5, class40.anIntArray677[j2]),
							mixColor(k5, class40.anIntArray678[j2]));
				}
			}
		}

	}

	private int mixColor(int j, int k) {
		k = 127 - k;
		k = (k * (j & 0x7f)) / 160;
		if (k < 2)
			k = 2;
		else if (k > 126)
			k = 126;
		return (j & 0xff80) + k;
	}

	private boolean isMouseWithinTriangle(int mouseX, int mouseY, int k, int l, int i1, int j1,
			int k1, int l1) {
		if (mouseY < k && mouseY < l && mouseY < i1)
			return false;
		if (mouseY > k && mouseY > l && mouseY > i1)
			return false;
		if (mouseX < j1 && mouseX < k1 && mouseX < l1)
			return false;
		if (mouseX > j1 && mouseX > k1 && mouseX > l1)
			return false;
		int i2 = (mouseY - k) * (k1 - j1) - (mouseX - j1) * (l - k);
		int j2 = (mouseY - i1) * (j1 - l1) - (mouseX - l1) * (k - i1);
		int k2 = (mouseY - l) * (l1 - k1) - (mouseX - k1) * (i1 - l);
		return i2 * k2 > 0 && k2 * j2 > 0;
	}

	private void processCulling() {
		int j = cullingClusterPointer[plane];
		CullingCluster clusters[] = cullingClusters[plane];
		processed_culling_clusters_ptr = 0;
		for (int k = 0; k < j; k++) {
			CullingCluster cullingCluster = clusters[k];
			if (cullingCluster.searchMask == 1) {
				int x_dist_from_camera_start = (cullingCluster.tileStartX - cameraTilePositionX) + 25;
				if (x_dist_from_camera_start < 0 || x_dist_from_camera_start > 50)
					continue;
				int y_dist_from_camera_start = (cullingCluster.tileStartY - cameraTilePositionY) + 25;
				if (y_dist_from_camera_start < 0)
					y_dist_from_camera_start = 0;
				int y_dist_from_camera_end = (cullingCluster.tileEndY - cameraTilePositionY) + 25;
				if (y_dist_from_camera_end > 50)
					y_dist_from_camera_end = 50;
				boolean isVisible = false;
				while (y_dist_from_camera_start <= y_dist_from_camera_end)
					if (TILE_VISIBILITY_MAP[x_dist_from_camera_start][y_dist_from_camera_start++]) {
						isVisible = true;
						break;
					}
				if (!isVisible)
					continue;
				int x_dist_from_camera_start_real = cameraPositionX - cullingCluster.worldStartX;
				if (x_dist_from_camera_start_real > 32) {
					cullingCluster.tileDistanceEnum = 1;
				} else {
					if (x_dist_from_camera_start_real >= -32)
						continue;
					cullingCluster.tileDistanceEnum = 2;
					x_dist_from_camera_start_real = -x_dist_from_camera_start_real;
				}
				cullingCluster.worldDistanceFromCameraStartY = (cullingCluster.worldStartY - cameraPositionY << 8) / x_dist_from_camera_start_real;
				cullingCluster.worldDistanceFromCameraEndY = (cullingCluster.worldEndY - cameraPositionY << 8) / x_dist_from_camera_start_real;
				cullingCluster.worldDistanceFromCameraStartZ = (cullingCluster.worldStartZ - cameraPositionZ << 8) / x_dist_from_camera_start_real;
				cullingCluster.worldDistanceFromCameraEndZ = (cullingCluster.worldEndZ - cameraPositionZ << 8) / x_dist_from_camera_start_real;
				processed_culling_clusters[processed_culling_clusters_ptr++] = cullingCluster;
				continue;
			}
			if (cullingCluster.searchMask == 2) {
				int y_dist_from_camera_start = (cullingCluster.tileStartY - cameraTilePositionY) + 25;
				if (y_dist_from_camera_start < 0 || y_dist_from_camera_start > 50)
					continue;
				int x_dist_from_camera_start = (cullingCluster.tileStartX - cameraTilePositionX) + 25;
				if (x_dist_from_camera_start < 0)
					x_dist_from_camera_start = 0;
				int x_dist_from_camera_end = (cullingCluster.tileEndX - cameraTilePositionX) + 25;
				if (x_dist_from_camera_end > 50)
					x_dist_from_camera_end = 50;
				boolean isVisible = false;
				while (x_dist_from_camera_start <= x_dist_from_camera_end)
					if (TILE_VISIBILITY_MAP[x_dist_from_camera_start++][y_dist_from_camera_start]) {
						isVisible = true;
						break;
					}
				if (!isVisible)
					continue;
				int y_dist_from_camera_start_real = cameraPositionY - cullingCluster.worldStartY;
				if (y_dist_from_camera_start_real > 32) {
					cullingCluster.tileDistanceEnum = 3;
				} else {
					if (y_dist_from_camera_start_real >= -32)
						continue;
					cullingCluster.tileDistanceEnum = 4;
					y_dist_from_camera_start_real = -y_dist_from_camera_start_real;
				}
				cullingCluster.worldDistanceFromCameraStartX = (cullingCluster.worldStartX - cameraPositionX << 8) / y_dist_from_camera_start_real;
				cullingCluster.worldDistanceFromCameraEndX = (cullingCluster.worldEndX - cameraPositionX << 8) / y_dist_from_camera_start_real;
				cullingCluster.worldDistanceFromCameraStartZ = (cullingCluster.worldStartZ - cameraPositionZ << 8) / y_dist_from_camera_start_real;
				cullingCluster.worldDistanceFromCameraEndZ = (cullingCluster.worldEndZ - cameraPositionZ << 8) / y_dist_from_camera_start_real;
				processed_culling_clusters[processed_culling_clusters_ptr++] = cullingCluster;
			} else if (cullingCluster.searchMask == 4) {
				int z_dist_from_camera_start_real = cullingCluster.worldStartZ - cameraPositionZ;
				if (z_dist_from_camera_start_real > 128) {
					int y_dist_from_camera_start = (cullingCluster.tileStartY - cameraTilePositionY) + 25;
					if (y_dist_from_camera_start < 0)
						y_dist_from_camera_start = 0;
					int y_dist_from_camera_end = (cullingCluster.tileEndY - cameraTilePositionY) + 25;
					if (y_dist_from_camera_end > 50)
						y_dist_from_camera_end = 50;
					if (y_dist_from_camera_start <= y_dist_from_camera_end) {
						int x_dist_from_camera_start = (cullingCluster.tileStartX - cameraTilePositionX) + 25;
						if (x_dist_from_camera_start < 0)
							x_dist_from_camera_start = 0;
						int x_dist_from_camera_end = (cullingCluster.tileEndX - cameraTilePositionX) + 25;
						if (x_dist_from_camera_end > 50)
							x_dist_from_camera_end = 50;
						boolean isVisible = false;
						label0: for (int _x = x_dist_from_camera_start; _x <= x_dist_from_camera_end; _x++) {
							for (int _y = y_dist_from_camera_start; _y <= y_dist_from_camera_end; _y++) {
								if (!TILE_VISIBILITY_MAP[_x][_y])
									continue;
								isVisible = true;
								break label0;
							}

						}

						if (isVisible) {
							cullingCluster.tileDistanceEnum = 5;
							cullingCluster.worldDistanceFromCameraStartX = (cullingCluster.worldStartX - cameraPositionX << 8) / z_dist_from_camera_start_real;
							cullingCluster.worldDistanceFromCameraEndX = (cullingCluster.worldEndX - cameraPositionX << 8) / z_dist_from_camera_start_real;
							cullingCluster.worldDistanceFromCameraStartY = (cullingCluster.worldStartY - cameraPositionY << 8) / z_dist_from_camera_start_real;
							cullingCluster.worldDistanceFromCameraEndY = (cullingCluster.worldEndY - cameraPositionY << 8) / z_dist_from_camera_start_real;
							processed_culling_clusters[processed_culling_clusters_ptr++] = cullingCluster;
						}
					}
				}
			}
		}

	}

	private boolean method320(int y, int x, int z) {
		int l = anIntArrayArrayArray445[y][x][z];
		if (l == -anInt448)
			return false;
		if (l == anInt448)
			return true;
		int i1 = x << 7;
		int j1 = z << 7;
		if (method324(i1 + 1, heightMap[y][x][z], j1 + 1) && method324((i1 + 128) - 1, heightMap[y][x + 1][z], j1 + 1) && method324((i1 + 128) - 1, heightMap[y][x + 1][z + 1], (j1 + 128) - 1) && method324(i1 + 1, heightMap[y][x][z + 1], (j1 + 128) - 1)) {
			anIntArrayArrayArray445[y][x][z] = anInt448;
			return true;
		} else {
			anIntArrayArrayArray445[y][x][z] = -anInt448;
			return false;
		}
	}

	private boolean method321(int i, int j, int k, int l) {
		if (!method320(i, j, k))
			return false;
		int i1 = j << 7;
		int j1 = k << 7;
		int k1 = heightMap[i][j][k] - 1;
		int l1 = k1 - 120;
		int i2 = k1 - 230;
		int j2 = k1 - 238;
		if (l < 16) {
			if (l == 1) {
				if (i1 > cameraPositionX) {
					if (!method324(i1, k1, j1))
						return false;
					if (!method324(i1, k1, j1 + 128))
						return false;
				}
				if (i > 0) {
					if (!method324(i1, l1, j1))
						return false;
					if (!method324(i1, l1, j1 + 128))
						return false;
				}
				return method324(i1, i2, j1) && method324(i1, i2, j1 + 128);
			}
			if (l == 2) {
				if (j1 < cameraPositionY) {
					if (!method324(i1, k1, j1 + 128))
						return false;
					if (!method324(i1 + 128, k1, j1 + 128))
						return false;
				}
				if (i > 0) {
					if (!method324(i1, l1, j1 + 128))
						return false;
					if (!method324(i1 + 128, l1, j1 + 128))
						return false;
				}
				return method324(i1, i2, j1 + 128)
						&& method324(i1 + 128, i2, j1 + 128);
			}
			if (l == 4) {
				if (i1 < cameraPositionX) {
					if (!method324(i1 + 128, k1, j1))
						return false;
					if (!method324(i1 + 128, k1, j1 + 128))
						return false;
				}
				if (i > 0) {
					if (!method324(i1 + 128, l1, j1))
						return false;
					if (!method324(i1 + 128, l1, j1 + 128))
						return false;
				}
				return method324(i1 + 128, i2, j1)
						&& method324(i1 + 128, i2, j1 + 128);
			}
			if (l == 8) {
				if (j1 > cameraPositionY) {
					if (!method324(i1, k1, j1))
						return false;
					if (!method324(i1 + 128, k1, j1))
						return false;
				}
				if (i > 0) {
					if (!method324(i1, l1, j1))
						return false;
					if (!method324(i1 + 128, l1, j1))
						return false;
				}
				return method324(i1, i2, j1) && method324(i1 + 128, i2, j1);
			}
		}
		if (!method324(i1 + 64, j2, j1 + 64))
			return false;
		if (l == 16)
			return method324(i1, i2, j1 + 128);
		if (l == 32)
			return method324(i1 + 128, i2, j1 + 128);
		if (l == 64)
			return method324(i1 + 128, i2, j1);
		if (l == 128) {
			return method324(i1, i2, j1);
		} else {
			System.out.println("Warning unsupported wall type");
			return true;
		}
	}

	private boolean method322(int i, int j, int k, int l) {
		if (!method320(i, j, k))
			return false;
		int i1 = j << 7;
		int j1 = k << 7;
		return method324(i1 + 1, heightMap[i][j][k] - l, j1 + 1) && method324((i1 + 128) - 1, heightMap[i][j + 1][k] - l, j1 + 1) && method324((i1 + 128) - 1, heightMap[i][j + 1][k + 1] - l, (j1 + 128) - 1) && method324(i1 + 1, heightMap[i][j][k + 1] - l, (j1 + 128) - 1);
	}

	private boolean method323(int i, int j, int k, int l, int i1, int j1) {
		if (j == k && l == i1) {
			if (!method320(i, j, l))
				return false;
			int k1 = j << 7;
			int i2 = l << 7;
			return method324(k1 + 1, heightMap[i][j][l] - j1, i2 + 1) && method324((k1 + 128) - 1, heightMap[i][j + 1][l] - j1, i2 + 1) && method324((k1 + 128) - 1, heightMap[i][j + 1][l + 1] - j1, (i2 + 128) - 1) && method324(k1 + 1, heightMap[i][j][l + 1] - j1, (i2 + 128) - 1);
		}
		for (int l1 = j; l1 <= k; l1++) {
			for (int j2 = l; j2 <= i1; j2++) {
				if (anIntArrayArrayArray445[i][l1][j2] == -anInt448) {
					return false;
				}
			}
		}

		int k2 = (j << 7) + 1;
		int l2 = (l << 7) + 2;
		int i3 = heightMap[i][j][l] - j1;
		if (!method324(k2, i3, l2))
			return false;
		int j3 = (k << 7) - 1;
		if (!method324(j3, i3, l2))
			return false;
		int k3 = (i1 << 7) - 1;
		return method324(k2, i3, k3) && method324(j3, i3, k3);
	}

	private boolean method324(int i, int j, int k) {
		for (int index = 0; index < processed_culling_clusters_ptr; index++) {
			CullingCluster cullingCluster = processed_culling_clusters[index];
			if (cullingCluster.tileDistanceEnum == 1) {
				int i1 = cullingCluster.worldStartX - i;
				if (i1 > 0) {
					int j2 = cullingCluster.worldStartY + (cullingCluster.worldDistanceFromCameraStartY * i1 >> 8);
					int k3 = cullingCluster.worldEndY + (cullingCluster.worldDistanceFromCameraEndY * i1 >> 8);
					int l4 = cullingCluster.worldStartZ + (cullingCluster.worldDistanceFromCameraStartZ * i1 >> 8);
					int i6 = cullingCluster.worldEndZ + (cullingCluster.worldDistanceFromCameraEndZ * i1 >> 8);
					if (k >= j2 && k <= k3 && j >= l4 && j <= i6)
						return true;
				}
			} else if (cullingCluster.tileDistanceEnum == 2) {
				int j1 = i - cullingCluster.worldStartX;
				if (j1 > 0) {
					int k2 = cullingCluster.worldStartY + (cullingCluster.worldDistanceFromCameraStartY * j1 >> 8);
					int l3 = cullingCluster.worldEndY + (cullingCluster.worldDistanceFromCameraEndY * j1 >> 8);
					int i5 = cullingCluster.worldStartZ + (cullingCluster.worldDistanceFromCameraStartZ * j1 >> 8);
					int j6 = cullingCluster.worldEndZ + (cullingCluster.worldDistanceFromCameraEndZ * j1 >> 8);
					if (k >= k2 && k <= l3 && j >= i5 && j <= j6)
						return true;
				}
			} else if (cullingCluster.tileDistanceEnum == 3) {
				int k1 = cullingCluster.worldStartY - k;
				if (k1 > 0) {
					int l2 = cullingCluster.worldStartX + (cullingCluster.worldDistanceFromCameraStartX * k1 >> 8);
					int i4 = cullingCluster.worldEndX + (cullingCluster.worldDistanceFromCameraEndX * k1 >> 8);
					int j5 = cullingCluster.worldStartZ + (cullingCluster.worldDistanceFromCameraStartZ * k1 >> 8);
					int k6 = cullingCluster.worldEndZ + (cullingCluster.worldDistanceFromCameraEndZ * k1 >> 8);
					if (i >= l2 && i <= i4 && j >= j5 && j <= k6)
						return true;
				}
			} else if (cullingCluster.tileDistanceEnum == 4) {
				int l1 = k - cullingCluster.worldStartY;
				if (l1 > 0) {
					int i3 = cullingCluster.worldStartX + (cullingCluster.worldDistanceFromCameraStartX * l1 >> 8);
					int j4 = cullingCluster.worldEndX + (cullingCluster.worldDistanceFromCameraEndX * l1 >> 8);
					int k5 = cullingCluster.worldStartZ + (cullingCluster.worldDistanceFromCameraStartZ * l1 >> 8);
					int l6 = cullingCluster.worldEndZ + (cullingCluster.worldDistanceFromCameraEndZ * l1 >> 8);
					if (i >= i3 && i <= j4 && j >= k5 && j <= l6)
						return true;
				}
			} else if (cullingCluster.tileDistanceEnum == 5) {
				int i2 = j - cullingCluster.worldStartZ;
				if (i2 > 0) {
					int j3 = cullingCluster.worldStartX + (cullingCluster.worldDistanceFromCameraStartX * i2 >> 8);
					int k4 = cullingCluster.worldEndX + (cullingCluster.worldDistanceFromCameraEndX * i2 >> 8);
					int l5 = cullingCluster.worldStartY + (cullingCluster.worldDistanceFromCameraStartY * i2 >> 8);
					int i7 = cullingCluster.worldEndY + (cullingCluster.worldDistanceFromCameraEndY * i2 >> 8);
					if (i >= j3 && i <= k4 && k >= l5 && k <= i7)
						return true;
				}
			}
		}

		return false;
	}

	public static boolean lowMem = true;
	private final int zMapSize;
	private final int xMapSize;
	private final int yMapSize;
	private final int[][][] heightMap;
	private final Tile[][][] tileArray;
	private int currentHeight;
	private int interactableObjectCacheCurrPos;
	private final InteractableObject[] interactableObjectCache;
	private final int[][][] anIntArrayArrayArray445;
	private static int anInt446;
	private static int plane;
	private static int anInt448;
	private static int anInt449;
	private static int anInt450;
	private static int anInt451;
	private static int anInt452;
	private static int cameraTilePositionX;
	private static int cameraTilePositionY;
	private static int cameraPositionX;
	private static int cameraPositionZ;
	private static int cameraPositionY;
	private static int yCurveSine;
	private static int yCurveCosine;
	private static int xCurveSine;
	private static int xCurveCosine;
	private static InteractableObject[] interactableObjects = new InteractableObject[100];
	private static final int[] anIntArray463 = { 53, -53, -53, 53 };
	private static final int[] anIntArray464 = { -53, -53, 53, 53 };
	private static final int[] anIntArray465 = { -45, 45, 45, -45 };
	private static final int[] anIntArray466 = { 45, 45, -45, -45 };
	private static boolean isClicked;
	private static int clickX;
	private static int clickY;
	public static int clickTileX = -1;
	public static int clickTileY = -1;
	private static final int anInt472;
	private static int[] cullingClusterPointer;
	private static CullingCluster[][] cullingClusters;
	private static int processed_culling_clusters_ptr;
	private static final CullingCluster[] processed_culling_clusters = new CullingCluster[500];
	private static Deque aClass19_477 = new Deque();
	private static final int[] anIntArray478 = { 19, 55, 38, 155, 255, 110,
			137, 205, 76 };
	private static final int[] anIntArray479 = { 160, 192, 80, 96, 0, 144, 80,
			48, 160 };
	private static final int[] anIntArray480 = { 76, 8, 137, 4, 0, 1, 38, 2, 19 };
	private static final int[] anIntArray481 = { 0, 0, 2, 0, 0, 2, 1, 1, 0 };
	private static final int[] anIntArray482 = { 2, 0, 0, 2, 0, 0, 0, 4, 4 };
	private static final int[] anIntArray483 = { 0, 4, 4, 8, 0, 0, 8, 0, 0 };
	private static final int[] anIntArray484 = { 1, 1, 0, 0, 0, 8, 0, 0, 8 };
	private static final int[] anIntArray485 = { 41, 39248, 41, 4643, 41, 41,
			41, 41, 41, 41, 41, 41, 41, 41, 41, 43086, 41, 41, 41, 41, 41, 41,
			41, 8602, 41, 28992, 41, 41, 41, 41, 41, 5056, 41, 41, 41, 7079,
			41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 3131, 41, 41, 41 };
	private final int[] anIntArray486;
	private final int[] anIntArray487;
	private int anInt488;
	private final int[][] tileShapePoints = { new int[16],
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1 },
			{ 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
			{ 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1 },
			{ 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1 },
			{ 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1 } };
	private final int[][] tileShapeIndices = {
			{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
			{ 12, 8, 4, 0, 13, 9, 5, 1, 14, 10, 6, 2, 15, 11, 7, 3 },
			{ 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 },
			{ 3, 7, 11, 15, 2, 6, 10, 14, 1, 5, 9, 13, 0, 4, 8, 12 } };
	private static boolean[][][][] TILE_VISIBILITY_MAPS = new boolean[8][32][51][51];
	private static boolean[][] TILE_VISIBILITY_MAP;
	private static int midX;
	private static int midY;
	private static int left;
	private static int top;
	private static int right;
	private static int bottom;

	static {
		anInt472 = 4;
		cullingClusterPointer = new int[anInt472];
		cullingClusters = new CullingCluster[anInt472][500];
	}
}
