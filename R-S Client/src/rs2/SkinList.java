package rs2;

public final class SkinList {

	public SkinList(JagexBuffer stream) {
		int totalSkins = stream.getUnsignedByte();
		anIntArray342 = new int[totalSkins];
		anIntArrayArray343 = new int[totalSkins][];
		for (int index = 0; index < totalSkins; index++) {
			anIntArray342[index] = stream.getUnsignedByte();
		}
		for (int index = 0; index < totalSkins; index++) {
			int l = stream.getUnsignedByte();
			anIntArrayArray343[index] = new int[l];
			for (int index2 = 0; index2 < l; index2++) {
				anIntArrayArray343[index][index2] = stream.getUnsignedByte();
			}
		}
	}

	public final int[] anIntArray342;
	public final int[][] anIntArrayArray343;
}
