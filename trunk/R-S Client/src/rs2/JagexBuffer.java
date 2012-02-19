package rs2;

import java.math.BigInteger;

public final class JagexBuffer extends NodeSub {

	public static JagexBuffer create() {
		synchronized (deque) {
			JagexBuffer buffer = null;
			if (anInt1412 > 0) {
				anInt1412--;
				buffer = (JagexBuffer) deque.popFront();
			}
			if (buffer != null) {
				buffer.offset = 0;
				return buffer;
			}
		}
		JagexBuffer buffer = new JagexBuffer();
		buffer.offset = 0;
		buffer.payload = new byte[5000];
		return buffer;
	}

	private JagexBuffer() {
	}

	public JagexBuffer(byte data[]) {
		payload = data;
		offset = 0;
	}

	public void putOpCode(int opcode) {
		payload[offset++] = (byte) (opcode + cryption.getNextKey());
	}

	public void putByte(int i) {
		payload[offset++] = (byte) i;
	}

	public void putShort(int i) {
		payload[offset++] = (byte) (i >> 8);
		payload[offset++] = (byte) i;
	}

	public void putLEShort_duplicate(int i) {
		payload[offset++] = (byte) i;
		payload[offset++] = (byte) (i >> 8);
	}

	public void put3Bytes(int i) {
		payload[offset++] = (byte) (i >> 16);
		payload[offset++] = (byte) (i >> 8);
		payload[offset++] = (byte) i;
	}

	public void putInt(int val) {
		payload[offset++] = (byte) (val >> 24);
		payload[offset++] = (byte) (val >> 16);
		payload[offset++] = (byte) (val >> 8);
		payload[offset++] = (byte) val;
	}

	public void putLEInt(int val) {
		payload[offset++] = (byte) val;
		payload[offset++] = (byte) (val >> 8);
		payload[offset++] = (byte) (val >> 16);
		payload[offset++] = (byte) (val >> 24);
	}

	public void putLong(long val) {
		payload[offset++] = (byte) (int) (val >> 56);
		payload[offset++] = (byte) (int) (val >> 48);
		payload[offset++] = (byte) (int) (val >> 40);
		payload[offset++] = (byte) (int) (val >> 32);
		payload[offset++] = (byte) (int) (val >> 24);
		payload[offset++] = (byte) (int) (val >> 16);
		payload[offset++] = (byte) (int) (val >> 8);
		payload[offset++] = (byte) (int) val;
	}

	public void putString(String s) {
		System.arraycopy(s.getBytes(), 0, payload, offset, s.length());
		offset += s.length();
		payload[offset++] = 10;
	}

	public void putBytes(byte buff[], int len, int off) {
		for (int index = off; index < off + len; index++) {
			payload[offset++] = buff[index];
		}
	}

	public void putSizeByte(int val) {
		payload[offset - val - 1] = (byte) val;
	}

	public int getUnsignedByte() {
		return payload[offset++] & 0xff;
	}

	public byte getSignedByte() {
		return payload[offset++];
	}

	public int getUnsignedShort() {
		offset += 2;
		return ((payload[offset - 2] & 0xff) << 8) + (payload[offset - 1] & 0xff);
	}

	public int getShort() {
		offset += 2;
		int i = ((payload[offset - 2] & 0xff) << 8) + (payload[offset - 1] & 0xff);
		if (i > 32767) {
			i -= 0x10000;
		}
		return i;
	}

	public int get3Bytes() {
		offset += 3;
		return ((payload[offset - 3] & 0xff) << 16) + ((payload[offset - 2] & 0xff) << 8) + (payload[offset - 1] & 0xff);
	}

	public int getInt() {
		offset += 4;
		return ((payload[offset - 4] & 0xff) << 24) + ((payload[offset - 3] & 0xff) << 16) + ((payload[offset - 2] & 0xff) << 8) + (payload[offset - 1] & 0xff);
	}

	public long getLong() {
		long l = (long) getInt() & 0xffffffffL;
		long l1 = (long) getInt() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public String getString() {
		int i = offset;
		while (payload[offset++] != 10)
			;
		return new String(payload, i, offset - i - 1);
	}

	public byte[] getBytes() {
		int off = offset;
		while (payload[offset++] != 10);
		byte dest[] = new byte[offset - off - 1];
		System.arraycopy(payload, off, dest, off - off, offset - 1 - off);
		return dest;
	}

	public void getBytes(int len, int off, byte buff[]) {
		for (int index = off; index < off + len; index++) {
			buff[index] = payload[offset++];
		}
	}

	public void initBitAccess() {
		position = offset * 8;
	}

	public int getBits(int off) {
		int offset = position >> 3;
		int max_bit_len = 8 - (position & 7);
		int dest = 0;
		position += off;
		for (; off > max_bit_len; max_bit_len = 8) {
			dest += (payload[offset++] & bitMasks[max_bit_len]) << off - max_bit_len;
			off -= max_bit_len;
		}
		if (off == max_bit_len)
			dest += payload[offset] & bitMasks[max_bit_len];
		else
			dest += payload[offset] >> max_bit_len - off & bitMasks[off];
		return dest;
	}

	public void finishBitAccess() {
		offset = (position + 7) / 8;
	}

	public int getUnsignedSmart() {
		int i = payload[offset] & 0xff;
		if (i < 128)
			return getUnsignedByte() - 64;
		else
			return getUnsignedShort() - 49152;
	}

	public int getSmart() {
		int i = payload[offset] & 0xff;
		if (i < 128) {
			return getUnsignedByte();
		} else {
			return getUnsignedShort() - 32768;
		}
	}

	public void encodeRSA() {
		int off = offset;
		offset = 0;
		byte buff[] = new byte[off];
		getBytes(off, 0, buff);
		BigInteger biginteger2 = new BigInteger(buff);
		BigInteger biginteger3 = biginteger2/* .modPow(biginteger, biginteger1) */;
		byte dest[] = biginteger3.toByteArray();
		offset = 0;
		putByte(dest.length);
		putBytes(dest, dest.length, 0);
	}

	public void putByteC(int val) {
		payload[offset++] = (byte) (-val);
	}

	public void putByteS(int val) {
		payload[offset++] = (byte) (128 - val);
	}

	public int getUnsignedByteA() {
		return payload[offset++] - 128 & 0xff;
	}

	public int getUnsignedByteC() {
		return -payload[offset++] & 0xff;
	}

	public int getUnsignedByteS() {
		return 128 - payload[offset++] & 0xff;
	}

	public byte getByteC() {
		return (byte) (-payload[offset++]);
	}

	public byte getByteS() {
		return (byte) (128 - payload[offset++]);
	}

	public void putLEShort(int val) {
		payload[offset++] = (byte) val;
		payload[offset++] = (byte) (val >> 8);
	}

	public void putShortA(int val) {
		payload[offset++] = (byte) (val >> 8);
		payload[offset++] = (byte) (val + 128);
	}

	public void putLEShortA(int val) {
		payload[offset++] = (byte) (val + 128);
		payload[offset++] = (byte) (val >> 8);
	}

	public int getUnsignedLEShort() {
		offset += 2;
		return ((payload[offset - 1] & 0xff) << 8) + (payload[offset - 2] & 0xff);
	}

	public int getUnsignedLEShortA() {
		offset += 2;
		return ((payload[offset - 2] & 0xff) << 8) + (payload[offset - 1] - 128 & 0xff);
	}

	public int getUnsignedShortA() {
		offset += 2;
		return ((payload[offset - 1] & 0xff) << 8) + (payload[offset - 2] - 128 & 0xff);
	}

	public int getLEShort() {
		offset += 2;
		int val = ((payload[offset - 1] & 0xff) << 8) + (payload[offset - 2] & 0xff);
		if (val > 32767) {
			val -= 0x10000;
		}
		return val;
	}

	public int getLEShortA() {
		offset += 2;
		int val = ((payload[offset - 1] & 0xff) << 8) + (payload[offset - 2] - 128 & 0xff);
		if (val > 32767) {
			val -= 0x10000;
		}
		return val;
	}

	public int getInt2() {
		offset += 4;
		return ((payload[offset - 2] & 0xff) << 24) + ((payload[offset - 1] & 0xff) << 16) + ((payload[offset - 4] & 0xff) << 8) + (payload[offset - 3] & 0xff);
	}

	public int getInt1() {
		offset += 4;
		return ((payload[offset - 3] & 0xff) << 24) + ((payload[offset - 4] & 0xff) << 16) + ((payload[offset - 1] & 0xff) << 8) + (payload[offset - 2] & 0xff);
	}

	public void getBytesCA(int len, byte buff[], int off) {
		for (int index = (len + off) - 1; index >= len; index--) {
			payload[offset++] = (byte) (buff[index] + 128);
		}

	}

	public void getBytesC(int off, int len, byte buff[]) {
		for (int index = (len + off) - 1; index >= len; index--) {
			buff[index] = payload[offset++];
		}
	}

	public byte payload[];
	public int offset;
	public int position;
	private static final int[] bitMasks = { 0, 1, 3, 7, 15, 31, 63, 127,
			255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff,
			0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff,
			0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff,
			0x7fffffff, -1 };
	public ISAACRandomGen cryption;
	private static int anInt1412;
	private static final Deque deque = new Deque();
}
