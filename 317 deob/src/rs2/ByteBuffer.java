package rs2;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

import java.math.BigInteger;

import sign.signlink;

public final class ByteBuffer extends NodeSub {

	public static ByteBuffer create() {
		synchronized (nodeList) {
			ByteBuffer stream = null;
			if (anInt1412 > 0) {
				anInt1412--;
				stream = (ByteBuffer) nodeList.popHead();
			}
			if (stream != null) {
				stream.offset = 0;
				return stream;
			}
		}
		ByteBuffer stream_1 = new ByteBuffer();
		stream_1.offset = 0;
		stream_1.buffer = new byte[5000];
		return stream_1;
	}

	private ByteBuffer() {
	}

	public ByteBuffer(byte abyte0[]) {
		buffer = abyte0;
		offset = 0;
	}

	public void putOpCode(int i) {
		buffer[offset++] = (byte) (i + encryption.getNextKey());
	}

	public void writeWordBigEndian(int i) {
		buffer[offset++] = (byte) i;
	}

	public void putShort(int i) {
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
	}

	public void method400(int i) {
		buffer[offset++] = (byte) i;
		buffer[offset++] = (byte) (i >> 8);
	}

	public void writeDWordBigEndian(int i) {
		buffer[offset++] = (byte) (i >> 16);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
	}

	public void putInt(int i) {
		buffer[offset++] = (byte) (i >> 24);
		buffer[offset++] = (byte) (i >> 16);
		buffer[offset++] = (byte) (i >> 8);
		buffer[offset++] = (byte) i;
	}

	public void method403(int j) {
		buffer[offset++] = (byte) j;
		buffer[offset++] = (byte) (j >> 8);
		buffer[offset++] = (byte) (j >> 16);
		buffer[offset++] = (byte) (j >> 24);
	}

	public void putLong(long l) {
		try {
			buffer[offset++] = (byte) (int) (l >> 56);
			buffer[offset++] = (byte) (int) (l >> 48);
			buffer[offset++] = (byte) (int) (l >> 40);
			buffer[offset++] = (byte) (int) (l >> 32);
			buffer[offset++] = (byte) (int) (l >> 24);
			buffer[offset++] = (byte) (int) (l >> 16);
			buffer[offset++] = (byte) (int) (l >> 8);
			buffer[offset++] = (byte) (int) l;
		} catch (RuntimeException runtimeexception) {
			signlink.reporterror("14395, " + 5 + ", " + l + ", "
					+ runtimeexception.toString());
			throw new RuntimeException();
		}
	}

	public void putString(String s) {
		// s.getBytes(0, s.length(), buffer, currentOffset); //deprecated
		System.arraycopy(s.getBytes(), 0, buffer, offset, s.length());
		offset += s.length();
		buffer[offset++] = 10;
	}

	public void putBytes(byte abyte0[], int i, int j) {
		for (int k = j; k < j + i; k++)
			buffer[offset++] = abyte0[k];

	}

	public void putBytes(int i) {
		buffer[offset - i - 1] = (byte) i;
	}

	public int getUByte() {
		return buffer[offset++] & 0xff;
	}

	public byte getByte() {
		return buffer[offset++];
	}

	public int getShort() {
		offset += 2;
		return ((buffer[offset - 2] & 0xff) << 8)
				+ (buffer[offset - 1] & 0xff);
	}

	public int getSignedShort() {
		offset += 2;
		int i = ((buffer[offset - 2] & 0xff) << 8)
				+ (buffer[offset - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int get3Bytes() {
		offset += 3;
		return ((buffer[offset - 3] & 0xff) << 16)
				+ ((buffer[offset - 2] & 0xff) << 8)
				+ (buffer[offset - 1] & 0xff);
	}

	public int getInt() {
		offset += 4;
		return ((buffer[offset - 4] & 0xff) << 24)
				+ ((buffer[offset - 3] & 0xff) << 16)
				+ ((buffer[offset - 2] & 0xff) << 8)
				+ (buffer[offset - 1] & 0xff);
	}

	public long getLong() {
		long l = (long) getInt() & 0xffffffffL;
		long l1 = (long) getInt() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String getString() {
		int i = offset;
		while (buffer[offset++] != 10)
			;
		return new String(buffer, i, offset - i - 1);
	}

	public byte[] getBytes() {
		int i = offset;
		while (buffer[offset++] != 10)
			;
		byte abyte0[] = new byte[offset - i - 1];
		System.arraycopy(buffer, i, abyte0, i - i, offset - 1 - i);
		return abyte0;
	}

	public void getBytes(int i, int j, byte abyte0[]) {
		for (int l = j; l < j + i; l++)
			abyte0[l] = buffer[offset++];
	}

	public void initBitAccess() {
		pos = offset * 8;
	}

	public int getBits(int i) {
		int k = pos >> 3;
		int l = 8 - (pos & 7);
		int i1 = 0;
		pos += i;
		for (; i > l; l = 8) {
			i1 += (buffer[k++] & anIntArray1409[l]) << i - l;
			i -= l;
		}
		if (i == l)
			i1 += buffer[k] & anIntArray1409[l];
		else
			i1 += buffer[k] >> l - i & anIntArray1409[i];
		return i1;
	}

	public void finishBitAccess() {
		offset = (pos + 7) / 8;
	}

	public int method421() {
		int i = buffer[offset] & 0xff;
		if (i < 128)
			return getUByte() - 64;
		else
			return getShort() - 49152;
	}

	public int method422() {
		int i = buffer[offset] & 0xff;
		if (i < 128)
			return getUByte();
		else
			return getShort() - 32768;
	}

	public void doKeys() {
		int i = offset;
		offset = 0;
		byte abyte0[] = new byte[i];
		getBytes(i, 0, abyte0);
		BigInteger biginteger2 = new BigInteger(abyte0);
		BigInteger biginteger3 = biginteger2/* .modPow(biginteger, biginteger1) */;
		byte abyte1[] = biginteger3.toByteArray();
		offset = 0;
		writeWordBigEndian(abyte1.length);
		putBytes(abyte1, abyte1.length, 0);
	}

	public void method424(int i) {
		buffer[offset++] = (byte) (-i);
	}

	public void method425(int j) {
		buffer[offset++] = (byte) (128 - j);
	}

	public int method426() {
		return buffer[offset++] - 128 & 0xff;
	}

	public int method427() {
		return -buffer[offset++] & 0xff;
	}

	public int method428() {
		return 128 - buffer[offset++] & 0xff;
	}

	public byte method429() {
		return (byte) (-buffer[offset++]);
	}

	public byte method430() {
		return (byte) (128 - buffer[offset++]);
	}

	public void method431(int i) {
		buffer[offset++] = (byte) i;
		buffer[offset++] = (byte) (i >> 8);
	}

	public void method432(int j) {
		buffer[offset++] = (byte) (j >> 8);
		buffer[offset++] = (byte) (j + 128);
	}

	public void method433(int j) {
		buffer[offset++] = (byte) (j + 128);
		buffer[offset++] = (byte) (j >> 8);
	}

	public int method434() {
		offset += 2;
		return ((buffer[offset - 1] & 0xff) << 8)
				+ (buffer[offset - 2] & 0xff);
	}

	public int method435() {
		offset += 2;
		return ((buffer[offset - 2] & 0xff) << 8)
				+ (buffer[offset - 1] - 128 & 0xff);
	}

	public int method436() {
		offset += 2;
		return ((buffer[offset - 1] & 0xff) << 8)
				+ (buffer[offset - 2] - 128 & 0xff);
	}

	public int method437() {
		offset += 2;
		int j = ((buffer[offset - 1] & 0xff) << 8)
				+ (buffer[offset - 2] & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int method438() {
		offset += 2;
		int j = ((buffer[offset - 1] & 0xff) << 8)
				+ (buffer[offset - 2] - 128 & 0xff);
		if (j > 32767)
			j -= 0x10000;
		return j;
	}

	public int method439() {
		offset += 4;
		return ((buffer[offset - 2] & 0xff) << 24)
				+ ((buffer[offset - 1] & 0xff) << 16)
				+ ((buffer[offset - 4] & 0xff) << 8)
				+ (buffer[offset - 3] & 0xff);
	}

	public int method440() {
		offset += 4;
		return ((buffer[offset - 3] & 0xff) << 24)
				+ ((buffer[offset - 4] & 0xff) << 16)
				+ ((buffer[offset - 1] & 0xff) << 8)
				+ (buffer[offset - 2] & 0xff);
	}

	public void method441(int i, byte abyte0[], int j) {
		for (int k = (i + j) - 1; k >= i; k--)
			buffer[offset++] = (byte) (abyte0[k] + 128);

	}

	public void method442(int i, int j, byte abyte0[]) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = buffer[offset++];

	}

	public byte buffer[];
	public int offset;
	public int pos;
	private static final int[] anIntArray1409 = { 0, 1, 3, 7, 15, 31, 63, 127,
			255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff,
			0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff,
			0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff,
			0x7fffffff, -1 };
	public ISAACRandomGen encryption;
	private static int anInt1412;
	private static final NodeList nodeList = new NodeList();
}
