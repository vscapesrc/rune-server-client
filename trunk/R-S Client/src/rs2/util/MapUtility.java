package rs2.util;

public final class MapUtility {

	public static int getRotatedMapChunkX(int x, int y, int rotation) {
		rotation &= 3;
		if (rotation == 0) {
			return x;
		}
		if (rotation == 1) {
			return y;
		}
		if (rotation == 2) {
			return 7 - x;
		} else {
			return 7 - y;
		}
	}

	public static int getRotatedMapChunkY(int x, int y, int rotation) {
		rotation &= 3;
		if (rotation == 0) {
			return y;
		}
		if (rotation == 1) {
			return 7 - x;
		}
		if (rotation == 2) {
			return 7 - y;
		} else {
			return x;
		}
	}

	public static int getRotatedLandscapeChunkX(int x, int y, int objectSizeX, int objectSizeY, int rotation) {
		rotation &= 3;
		if (rotation == 0) {
			return x;
		}
		if (rotation == 1) {
			return y;
		}
		if (rotation == 2) {
			return 7 - x - (objectSizeX - 1);
		} else {
			return 7 - y - (objectSizeY - 1);
		}
	}

	public static int getRotatedLandscapeChunkY(int x, int y, int objectSizeX, int objectSizeY, int rotation) {
		rotation &= 3;
		if (rotation == 0) {
			return y;
		}
		if (rotation == 1) {
			return 7 - x - (objectSizeX - 1);
		}
		if (rotation == 2) {
			return 7 - y - (objectSizeY - 1);
		} else {
			return x;
		}
	}

}
