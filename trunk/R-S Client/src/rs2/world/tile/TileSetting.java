package rs2.world.tile;

public final class TileSetting {

	public TileSetting() {
		anInt290 = 0;
		anInt291 = 0;
		sizeX = 104;
		sizeY = 104;
		clipData = new int[sizeX][sizeY];
		init();
	}

	public void init() {
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				if (x == 0 || y == 0 || x == sizeX - 1 || y == sizeY - 1) {
					clipData[x][y] = 0xffffff;
				} else {
					clipData[x][y] = 0x1000000;
				}
			}
		}
	}

	public void method211(int i, int j, int k, int l, boolean flag) {
		k -= anInt290;
		i -= anInt291;
		if (l == 0) {
			if (j == 0) {
				orClipTable(k, i, 128);
				orClipTable(k - 1, i, 8);
			}
			if (j == 1) {
				orClipTable(k, i, 2);
				orClipTable(k, i + 1, 32);
			}
			if (j == 2) {
				orClipTable(k, i, 8);
				orClipTable(k + 1, i, 128);
			}
			if (j == 3) {
				orClipTable(k, i, 32);
				orClipTable(k, i - 1, 2);
			}
		}
		if (l == 1 || l == 3) {
			if (j == 0) {
				orClipTable(k, i, 1);
				orClipTable(k - 1, i + 1, 16);
			}
			if (j == 1) {
				orClipTable(k, i, 4);
				orClipTable(k + 1, i + 1, 64);
			}
			if (j == 2) {
				orClipTable(k, i, 16);
				orClipTable(k + 1, i - 1, 1);
			}
			if (j == 3) {
				orClipTable(k, i, 64);
				orClipTable(k - 1, i - 1, 4);
			}
		}
		if (l == 2) {
			if (j == 0) {
				orClipTable(k, i, 130);
				orClipTable(k - 1, i, 8);
				orClipTable(k, i + 1, 32);
			}
			if (j == 1) {
				orClipTable(k, i, 10);
				orClipTable(k, i + 1, 32);
				orClipTable(k + 1, i, 128);
			}
			if (j == 2) {
				orClipTable(k, i, 40);
				orClipTable(k + 1, i, 128);
				orClipTable(k, i - 1, 2);
			}
			if (j == 3) {
				orClipTable(k, i, 160);
				orClipTable(k, i - 1, 2);
				orClipTable(k - 1, i, 8);
			}
		}
		if (flag) {
			if (l == 0) {
				if (j == 0) {
					orClipTable(k, i, 0x10000);
					orClipTable(k - 1, i, 4096);
				}
				if (j == 1) {
					orClipTable(k, i, 1024);
					orClipTable(k, i + 1, 16384);
				}
				if (j == 2) {
					orClipTable(k, i, 4096);
					orClipTable(k + 1, i, 0x10000);
				}
				if (j == 3) {
					orClipTable(k, i, 16384);
					orClipTable(k, i - 1, 1024);
				}
			}
			if (l == 1 || l == 3) {
				if (j == 0) {
					orClipTable(k, i, 512);
					orClipTable(k - 1, i + 1, 8192);
				}
				if (j == 1) {
					orClipTable(k, i, 2048);
					orClipTable(k + 1, i + 1, 32768);
				}
				if (j == 2) {
					orClipTable(k, i, 8192);
					orClipTable(k + 1, i - 1, 512);
				}
				if (j == 3) {
					orClipTable(k, i, 32768);
					orClipTable(k - 1, i - 1, 2048);
				}
			}
			if (l == 2) {
				if (j == 0) {
					orClipTable(k, i, 0x10400);
					orClipTable(k - 1, i, 4096);
					orClipTable(k, i + 1, 16384);
				}
				if (j == 1) {
					orClipTable(k, i, 5120);
					orClipTable(k, i + 1, 16384);
					orClipTable(k + 1, i, 0x10000);
				}
				if (j == 2) {
					orClipTable(k, i, 20480);
					orClipTable(k + 1, i, 0x10000);
					orClipTable(k, i - 1, 1024);
				}
				if (j == 3) {
					orClipTable(k, i, 0x14000);
					orClipTable(k, i - 1, 1024);
					orClipTable(k - 1, i, 4096);
				}
			}
		}
	}

	public void method212(boolean flag, int objectSizeX, int objectSizeY, int objectX, int objectY, int j1) {
		int k1 = 256;
		if (flag)
			k1 += 0x20000;
		objectX -= anInt290;
		objectY -= anInt291;
		if (j1 == 1 || j1 == 3) {
			int l1 = objectSizeX;
			objectSizeX = objectSizeY;
			objectSizeY = l1;
		}
		for (int i2 = objectX; i2 < objectX + objectSizeX; i2++) {
			if (i2 >= 0 && i2 < sizeX) {
				for (int j2 = objectY; j2 < objectY + objectSizeY; j2++) {
					if (j2 >= 0 && j2 < sizeY) {
						orClipTable(i2, j2, k1);
					}
				}
			}
		}
	}

	public void orClipTableSet(int y, int x) {
		x -= anInt290;
		y -= anInt291;
		clipData[x][y] |= 0x200000;
	}

	private void orClipTable(int x, int y, int k) {
		clipData[x][y] |= k;
	}

	public void method215(int i, int j, boolean flag, int k, int l) {
		k -= anInt290;
		l -= anInt291;
		if (j == 0) {
			if (i == 0) {
				method217(128, k, l);
				method217(8, k - 1, l);
			}
			if (i == 1) {
				method217(2, k, l);
				method217(32, k, l + 1);
			}
			if (i == 2) {
				method217(8, k, l);
				method217(128, k + 1, l);
			}
			if (i == 3) {
				method217(32, k, l);
				method217(2, k, l - 1);
			}
		}
		if (j == 1 || j == 3) {
			if (i == 0) {
				method217(1, k, l);
				method217(16, k - 1, l + 1);
			}
			if (i == 1) {
				method217(4, k, l);
				method217(64, k + 1, l + 1);
			}
			if (i == 2) {
				method217(16, k, l);
				method217(1, k + 1, l - 1);
			}
			if (i == 3) {
				method217(64, k, l);
				method217(4, k - 1, l - 1);
			}
		}
		if (j == 2) {
			if (i == 0) {
				method217(130, k, l);
				method217(8, k - 1, l);
				method217(32, k, l + 1);
			}
			if (i == 1) {
				method217(10, k, l);
				method217(32, k, l + 1);
				method217(128, k + 1, l);
			}
			if (i == 2) {
				method217(40, k, l);
				method217(128, k + 1, l);
				method217(2, k, l - 1);
			}
			if (i == 3) {
				method217(160, k, l);
				method217(2, k, l - 1);
				method217(8, k - 1, l);
			}
		}
		if (flag) {
			if (j == 0) {
				if (i == 0) {
					method217(0x10000, k, l);
					method217(4096, k - 1, l);
				}
				if (i == 1) {
					method217(1024, k, l);
					method217(16384, k, l + 1);
				}
				if (i == 2) {
					method217(4096, k, l);
					method217(0x10000, k + 1, l);
				}
				if (i == 3) {
					method217(16384, k, l);
					method217(1024, k, l - 1);
				}
			}
			if (j == 1 || j == 3) {
				if (i == 0) {
					method217(512, k, l);
					method217(8192, k - 1, l + 1);
				}
				if (i == 1) {
					method217(2048, k, l);
					method217(32768, k + 1, l + 1);
				}
				if (i == 2) {
					method217(8192, k, l);
					method217(512, k + 1, l - 1);
				}
				if (i == 3) {
					method217(32768, k, l);
					method217(2048, k - 1, l - 1);
				}
			}
			if (j == 2) {
				if (i == 0) {
					method217(0x10400, k, l);
					method217(4096, k - 1, l);
					method217(16384, k, l + 1);
				}
				if (i == 1) {
					method217(5120, k, l);
					method217(16384, k, l + 1);
					method217(0x10000, k + 1, l);
				}
				if (i == 2) {
					method217(20480, k, l);
					method217(0x10000, k + 1, l);
					method217(1024, k, l - 1);
				}
				if (i == 3) {
					method217(0x14000, k, l);
					method217(1024, k, l - 1);
					method217(4096, k - 1, l);
				}
			}
		}
	}

	public void method216(int i, int j, int k, int l, int i1, boolean flag) {
		int j1 = 256;
		if (flag)
			j1 += 0x20000;
		k -= anInt290;
		l -= anInt291;
		if (i == 1 || i == 3) {
			int k1 = j;
			j = i1;
			i1 = k1;
		}
		for (int l1 = k; l1 < k + j; l1++)
			if (l1 >= 0 && l1 < sizeX) {
				for (int i2 = l; i2 < l + i1; i2++)
					if (i2 >= 0 && i2 < sizeY)
						method217(j1, l1, i2);

			}

	}

	private void method217(int i, int x, int y) {
		clipData[x][y] &= 0xffffff - i;
	}

	public void method218(int y, int x) {
		x -= anInt290;
		y -= anInt291;
		clipData[x][y] &= 0xdfffff;
	}

	public boolean isWalkableA(int i, int x, int y, int i1, int j1, int k1) {
		if (x == i && y == k1)
			return true;
		x -= anInt290;
		y -= anInt291;
		i -= anInt290;
		k1 -= anInt291;
		if (j1 == 0)
			if (i1 == 0) {
				if (x == i - 1 && y == k1)
					return true;
				if (x == i && y == k1 + 1 && (clipData[x][y] & 0x1280120) == 0)
					return true;
				if (x == i && y == k1 - 1 && (clipData[x][y] & 0x1280102) == 0)
					return true;
			} else if (i1 == 1) {
				if (x == i && y == k1 + 1)
					return true;
				if (x == i - 1 && y == k1 && (clipData[x][y] & 0x1280108) == 0)
					return true;
				if (x == i + 1 && y == k1 && (clipData[x][y] & 0x1280180) == 0)
					return true;
			} else if (i1 == 2) {
				if (x == i + 1 && y == k1)
					return true;
				if (x == i && y == k1 + 1 && (clipData[x][y] & 0x1280120) == 0)
					return true;
				if (x == i && y == k1 - 1 && (clipData[x][y] & 0x1280102) == 0)
					return true;
			} else if (i1 == 3) {
				if (x == i && y == k1 - 1)
					return true;
				if (x == i - 1 && y == k1 && (clipData[x][y] & 0x1280108) == 0)
					return true;
				if (x == i + 1 && y == k1 && (clipData[x][y] & 0x1280180) == 0)
					return true;
			}
		if (j1 == 2)
			if (i1 == 0) {
				if (x == i - 1 && y == k1)
					return true;
				if (x == i && y == k1 + 1)
					return true;
				if (x == i + 1 && y == k1 && (clipData[x][y] & 0x1280180) == 0)
					return true;
				if (x == i && y == k1 - 1 && (clipData[x][y] & 0x1280102) == 0)
					return true;
			} else if (i1 == 1) {
				if (x == i - 1 && y == k1 && (clipData[x][y] & 0x1280108) == 0)
					return true;
				if (x == i && y == k1 + 1)
					return true;
				if (x == i + 1 && y == k1)
					return true;
				if (x == i && y == k1 - 1 && (clipData[x][y] & 0x1280102) == 0)
					return true;
			} else if (i1 == 2) {
				if (x == i - 1 && y == k1 && (clipData[x][y] & 0x1280108) == 0)
					return true;
				if (x == i && y == k1 + 1 && (clipData[x][y] & 0x1280120) == 0)
					return true;
				if (x == i + 1 && y == k1)
					return true;
				if (x == i && y == k1 - 1)
					return true;
			} else if (i1 == 3) {
				if (x == i - 1 && y == k1)
					return true;
				if (x == i && y == k1 + 1 && (clipData[x][y] & 0x1280120) == 0)
					return true;
				if (x == i + 1 && y == k1 && (clipData[x][y] & 0x1280180) == 0)
					return true;
				if (x == i && y == k1 - 1)
					return true;
			}
		if (j1 == 9) {
			if (x == i && y == k1 + 1 && (clipData[x][y] & 0x20) == 0)
				return true;
			if (x == i && y == k1 - 1 && (clipData[x][y] & 2) == 0)
				return true;
			if (x == i - 1 && y == k1 && (clipData[x][y] & 8) == 0)
				return true;
			if (x == i + 1 && y == k1 && (clipData[x][y] & 0x80) == 0)
				return true;
		}
		return false;
	}

	public boolean isWalkableB(int i, int j, int y, int l, int i1, int x) {
		if (x == i && y == j)
			return true;
		x -= anInt290;
		y -= anInt291;
		i -= anInt290;
		j -= anInt291;
		if (l == 6 || l == 7) {
			if (l == 7)
				i1 = i1 + 2 & 3;
			if (i1 == 0) {
				if (x == i + 1 && y == j && (clipData[x][y] & 0x80) == 0)
					return true;
				if (x == i && y == j - 1 && (clipData[x][y] & 2) == 0)
					return true;
			} else if (i1 == 1) {
				if (x == i - 1 && y == j && (clipData[x][y] & 8) == 0)
					return true;
				if (x == i && y == j - 1 && (clipData[x][y] & 2) == 0)
					return true;
			} else if (i1 == 2) {
				if (x == i - 1 && y == j && (clipData[x][y] & 8) == 0)
					return true;
				if (x == i && y == j + 1 && (clipData[x][y] & 0x20) == 0)
					return true;
			} else if (i1 == 3) {
				if (x == i + 1 && y == j && (clipData[x][y] & 0x80) == 0)
					return true;
				if (x == i && y == j + 1 && (clipData[x][y] & 0x20) == 0)
					return true;
			}
		}
		if (l == 8) {
			if (x == i && y == j + 1 && (clipData[x][y] & 0x20) == 0)
				return true;
			if (x == i && y == j - 1 && (clipData[x][y] & 2) == 0)
				return true;
			if (x == i - 1 && y == j && (clipData[x][y] & 8) == 0)
				return true;
			if (x == i + 1 && y == j && (clipData[x][y] & 0x80) == 0)
				return true;
		}
		return false;
	}

	public boolean isWalkableC(int i, int j, int x, int l, int i1, int j1, int y) {
		int l1 = (j + j1) - 1;
		int i2 = (i + l) - 1;
		if (x >= j && x <= l1 && y >= i && y <= i2)
			return true;
		if (x == j - 1 && y >= i && y <= i2 && (clipData[x - anInt290][y - anInt291] & 8) == 0 && (i1 & 8) == 0)
			return true;
		if (x == l1 + 1 && y >= i && y <= i2 && (clipData[x - anInt290][y - anInt291] & 0x80) == 0 && (i1 & 2) == 0)
			return true;
		return y == i - 1 && x >= j && x <= l1 && (clipData[x - anInt290][y - anInt291] & 2) == 0 && (i1 & 4) == 0 || y == i2 + 1 && x >= j && x <= l1 && (clipData[x - anInt290][y - anInt291] & 0x20) == 0 && (i1 & 1) == 0;
	}

	private final int anInt290;
	private final int anInt291;
	private final int sizeX;
	private final int sizeY;
	public final int[][] clipData;
}
