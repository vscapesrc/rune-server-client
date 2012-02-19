package rs2.graphics;

import rs2.JagexBuffer;
import rs2.cache.JagexArchive;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class IndexedImage extends RSDrawingArea {

	public IndexedImage(JagexArchive streamLoader, String s, int i) {
		JagexBuffer stream = new JagexBuffer(streamLoader.getData(s + ".dat"));
		JagexBuffer stream_1 = new JagexBuffer(streamLoader.getData("index.dat"));
		stream_1.offset = stream.getUnsignedShort();
		anInt1456 = stream_1.getUnsignedShort();
		anInt1457 = stream_1.getUnsignedShort();
		int j = stream_1.getUnsignedByte();
		anIntArray1451 = new int[j];
		for (int k = 0; k < j - 1; k++)
			anIntArray1451[k + 1] = stream_1.get3Bytes();

		for (int l = 0; l < i; l++) {
			stream_1.offset += 2;
			stream.offset += stream_1.getUnsignedShort()
					* stream_1.getUnsignedShort();
			stream_1.offset++;
		}

		anInt1454 = stream_1.getUnsignedByte();
		anInt1455 = stream_1.getUnsignedByte();
		myWidth = stream_1.getUnsignedShort();
		myHeight = stream_1.getUnsignedShort();
		int i1 = stream_1.getUnsignedByte();
		int j1 = myWidth * myHeight;
		myPixels = new byte[j1];
		if (i1 == 0) {
			for (int k1 = 0; k1 < j1; k1++)
				myPixels[k1] = stream.getSignedByte();

			return;
		}
		if (i1 == 1) {
			for (int l1 = 0; l1 < myWidth; l1++) {
				for (int i2 = 0; i2 < myHeight; i2++)
					myPixels[l1 + i2 * myWidth] = stream
							.getSignedByte();

			}

		}
	}

	public void method356() {
		anInt1456 /= 2;
		anInt1457 /= 2;
		byte abyte0[] = new byte[anInt1456 * anInt1457];
		int i = 0;
		for (int j = 0; j < myHeight; j++) {
			for (int k = 0; k < myWidth; k++)
				abyte0[(k + anInt1454 >> 1) + (j + anInt1455 >> 1) * anInt1456] = myPixels[i++];

		}

		myPixels = abyte0;
		myWidth = anInt1456;
		myHeight = anInt1457;
		anInt1454 = 0;
		anInt1455 = 0;
	}

	public void method357() {
		if (myWidth == anInt1456 && myHeight == anInt1457)
			return;
		byte abyte0[] = new byte[anInt1456 * anInt1457];
		int i = 0;
		for (int j = 0; j < myHeight; j++) {
			for (int k = 0; k < myWidth; k++)
				abyte0[k + anInt1454 + (j + anInt1455) * anInt1456] = myPixels[i++];

		}

		myPixels = abyte0;
		myWidth = anInt1456;
		myHeight = anInt1457;
		anInt1454 = 0;
		anInt1455 = 0;
	}

	public void method358() {
		byte abyte0[] = new byte[myWidth * myHeight];
		int j = 0;
		for (int k = 0; k < myHeight; k++) {
			for (int l = myWidth - 1; l >= 0; l--)
				abyte0[j++] = myPixels[l + k * myWidth];

		}

		myPixels = abyte0;
		anInt1454 = anInt1456 - myWidth - anInt1454;
	}

	public void method359() {
		byte abyte0[] = new byte[myWidth * myHeight];
		int i = 0;
		for (int j = myHeight - 1; j >= 0; j--) {
			for (int k = 0; k < myWidth; k++)
				abyte0[i++] = myPixels[k + j * myWidth];

		}

		myPixels = abyte0;
		anInt1455 = anInt1457 - myHeight - anInt1455;
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

	public void drawImage(int x, int y) {
		x += anInt1454;
		y += anInt1455;
		int l = x + y * RSDrawingArea.width;
		int i1 = 0;
		int height = myHeight;
		int width = myWidth;
		int l1 = RSDrawingArea.width - width;
		int i2 = 0;
		if (y < RSDrawingArea.startY) {
			int j2 = RSDrawingArea.startY - y;
			height -= j2;
			y = RSDrawingArea.startY;
			i1 += j2 * width;
			l += j2 * RSDrawingArea.width;
		}
		if (y + height > RSDrawingArea.endY)
			height -= (y + height) - RSDrawingArea.endY;
		if (x < RSDrawingArea.startX) {
			int k2 = RSDrawingArea.startX - x;
			width -= k2;
			x = RSDrawingArea.startX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (x + width > RSDrawingArea.endX) {
			int l2 = (x + width) - RSDrawingArea.endX;
			width -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(width <= 0 || height <= 0)) {
			method362(height, RSDrawingArea.pixels, myPixels, l1, l, width, i1, anIntArray1451, i2);
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

	public byte myPixels[];
	public final int[] anIntArray1451;
	public int myWidth;
	public int myHeight;
	public int anInt1454;
	public int anInt1455;
	public int anInt1456;
	private int anInt1457;
}
