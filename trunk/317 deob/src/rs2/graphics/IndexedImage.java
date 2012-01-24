package rs2.graphics;

import rs2.ByteBuffer;
import rs2.cache.CacheArchive;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class IndexedImage extends RSDrawingArea {

	public IndexedImage(CacheArchive streamLoader, String s, int i) {
		ByteBuffer stream = new ByteBuffer(streamLoader.getData(s + ".dat"));
		ByteBuffer stream_1 = new ByteBuffer(streamLoader.getData("index.dat"));
		stream_1.offset = stream.getShort();
		anInt1456 = stream_1.getShort();
		anInt1457 = stream_1.getShort();
		int j = stream_1.getUByte();
		anIntArray1451 = new int[j];
		for (int k = 0; k < j - 1; k++)
			anIntArray1451[k + 1] = stream_1.get3Bytes();

		for (int l = 0; l < i; l++) {
			stream_1.offset += 2;
			stream.offset += stream_1.getShort()
					* stream_1.getShort();
			stream_1.offset++;
		}

		anInt1454 = stream_1.getUByte();
		anInt1455 = stream_1.getUByte();
		anInt1452 = stream_1.getShort();
		anInt1453 = stream_1.getShort();
		int i1 = stream_1.getUByte();
		int j1 = anInt1452 * anInt1453;
		aByteArray1450 = new byte[j1];
		if (i1 == 0) {
			for (int k1 = 0; k1 < j1; k1++)
				aByteArray1450[k1] = stream.getByte();

			return;
		}
		if (i1 == 1) {
			for (int l1 = 0; l1 < anInt1452; l1++) {
				for (int i2 = 0; i2 < anInt1453; i2++)
					aByteArray1450[l1 + i2 * anInt1452] = stream
							.getByte();

			}

		}
	}

	public void method356() {
		anInt1456 /= 2;
		anInt1457 /= 2;
		byte abyte0[] = new byte[anInt1456 * anInt1457];
		int i = 0;
		for (int j = 0; j < anInt1453; j++) {
			for (int k = 0; k < anInt1452; k++)
				abyte0[(k + anInt1454 >> 1) + (j + anInt1455 >> 1) * anInt1456] = aByteArray1450[i++];

		}

		aByteArray1450 = abyte0;
		anInt1452 = anInt1456;
		anInt1453 = anInt1457;
		anInt1454 = 0;
		anInt1455 = 0;
	}

	public void method357() {
		if (anInt1452 == anInt1456 && anInt1453 == anInt1457)
			return;
		byte abyte0[] = new byte[anInt1456 * anInt1457];
		int i = 0;
		for (int j = 0; j < anInt1453; j++) {
			for (int k = 0; k < anInt1452; k++)
				abyte0[k + anInt1454 + (j + anInt1455) * anInt1456] = aByteArray1450[i++];

		}

		aByteArray1450 = abyte0;
		anInt1452 = anInt1456;
		anInt1453 = anInt1457;
		anInt1454 = 0;
		anInt1455 = 0;
	}

	public void method358() {
		byte abyte0[] = new byte[anInt1452 * anInt1453];
		int j = 0;
		for (int k = 0; k < anInt1453; k++) {
			for (int l = anInt1452 - 1; l >= 0; l--)
				abyte0[j++] = aByteArray1450[l + k * anInt1452];

		}

		aByteArray1450 = abyte0;
		anInt1454 = anInt1456 - anInt1452 - anInt1454;
	}

	public void method359() {
		byte abyte0[] = new byte[anInt1452 * anInt1453];
		int i = 0;
		for (int j = anInt1453 - 1; j >= 0; j--) {
			for (int k = 0; k < anInt1452; k++)
				abyte0[i++] = aByteArray1450[k + j * anInt1452];

		}

		aByteArray1450 = abyte0;
		anInt1455 = anInt1457 - anInt1453 - anInt1455;
	}

	public void method360(int i, int j, int k) {
		for (int i1 = 0; i1 < anIntArray1451.length; i1++) {
			int j1 = anIntArray1451[i1] >> 16 & 0xff;
			j1 += i;
			if (j1 < 0)
				j1 = 0;
			else if (j1 > 255)
				j1 = 255;
			int k1 = anIntArray1451[i1] >> 8 & 0xff;
			k1 += j;
			if (k1 < 0)
				k1 = 0;
			else if (k1 > 255)
				k1 = 255;
			int l1 = anIntArray1451[i1] & 0xff;
			l1 += k;
			if (l1 < 0)
				l1 = 0;
			else if (l1 > 255)
				l1 = 255;
			anIntArray1451[i1] = (j1 << 16) + (k1 << 8) + l1;
		}
	}

	public void method361(int i, int k) {
		i += anInt1454;
		k += anInt1455;
		int l = i + k * RSDrawingArea.width;
		int i1 = 0;
		int j1 = anInt1453;
		int k1 = anInt1452;
		int l1 = RSDrawingArea.width - k1;
		int i2 = 0;
		if (k < RSDrawingArea.topY) {
			int j2 = RSDrawingArea.topY - k;
			j1 -= j2;
			k = RSDrawingArea.topY;
			i1 += j2 * k1;
			l += j2 * RSDrawingArea.width;
		}
		if (k + j1 > RSDrawingArea.bottomY)
			j1 -= (k + j1) - RSDrawingArea.bottomY;
		if (i < RSDrawingArea.topX) {
			int k2 = RSDrawingArea.topX - i;
			k1 -= k2;
			i = RSDrawingArea.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > RSDrawingArea.bottomX) {
			int l2 = (i + k1) - RSDrawingArea.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 <= 0 || j1 <= 0)) {
			method362(j1, RSDrawingArea.pixels, aByteArray1450, l1, l, k1, i1,
					anIntArray1451, i2);
		}
	}

	private void method362(int i, int ai[], byte abyte0[], int j, int k, int l,
			int i1, int ai1[], int j1) {
		int k1 = -(l >> 2);
		l = -(l & 3);
		for (int l1 = -i; l1 < 0; l1++) {
			for (int i2 = k1; i2 < 0; i2++) {
				byte byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
				byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
				byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
				byte1 = abyte0[i1++];
				if (byte1 != 0)
					ai[k++] = ai1[byte1 & 0xff];
				else
					k++;
			}

			for (int j2 = l; j2 < 0; j2++) {
				byte byte2 = abyte0[i1++];
				if (byte2 != 0)
					ai[k++] = ai1[byte2 & 0xff];
				else
					k++;
			}

			k += j;
			i1 += j1;
		}

	}

	public byte aByteArray1450[];
	public final int[] anIntArray1451;
	public int anInt1452;
	public int anInt1453;
	public int anInt1454;
	public int anInt1455;
	public int anInt1456;
	private int anInt1457;
}