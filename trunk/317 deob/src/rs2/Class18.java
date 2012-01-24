package rs2;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class Class18 {

	public Class18(ByteBuffer stream) {
		int anInt341 = stream.getUByte();
		anIntArray342 = new int[anInt341];
		anIntArrayArray343 = new int[anInt341][];
		for (int j = 0; j < anInt341; j++)
			anIntArray342[j] = stream.getUByte();

		for (int k = 0; k < anInt341; k++) {
			int l = stream.getUByte();
			anIntArrayArray343[k] = new int[l];
			for (int i1 = 0; i1 < l; i1++)
				anIntArrayArray343[k][i1] = stream.getUByte();

		}

	}

	public final int[] anIntArray342;
	public final int[][] anIntArrayArray343;
}
