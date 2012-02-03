package rs2;

public final class FrameHeader {

	public static void method528(int i) {
		frameHeader = new FrameHeader[i + 1];
		aBooleanArray643 = new boolean[i + 1];
		for (int index = 0; index < i + 1; index++) {
			aBooleanArray643[index] = true;
		}
	}

	public static void method529(byte data[]) {
		ByteBuffer buffer = new ByteBuffer(data);
		buffer.offset = data.length - 8;
		int i = buffer.getShort();
		int j = buffer.getShort();
		int k = buffer.getShort();
		int l = buffer.getShort();
		int i1 = 0;
		ByteBuffer stream_1 = new ByteBuffer(data);
		stream_1.offset = i1;
		i1 += i + 2;
		ByteBuffer stream_2 = new ByteBuffer(data);
		stream_2.offset = i1;
		i1 += j;
		ByteBuffer stream_3 = new ByteBuffer(data);
		stream_3.offset = i1;
		i1 += k;
		ByteBuffer stream_4 = new ByteBuffer(data);
		stream_4.offset = i1;
		i1 += l;
		ByteBuffer stream_5 = new ByteBuffer(data);
		stream_5.offset = i1;
		SkinList skinList = new SkinList(stream_5);
		int k1 = stream_1.getShort();
		int ai[] = new int[500];
		int ai1[] = new int[500];
		int ai2[] = new int[500];
		int ai3[] = new int[500];
		for (int index = 0; index < k1; index++) {
			int i2 = stream_1.getShort();
			FrameHeader frame = frameHeader[i2] = new FrameHeader();
			frame.anInt636 = stream_4.getUByte();
			frame.skinList = skinList;
			int j2 = stream_1.getUByte();
			int k2 = -1;
			int l2 = 0;
			for (int i3 = 0; i3 < j2; i3++) {
				int j3 = stream_2.getUByte();
				if (j3 > 0) {
					if (skinList.anIntArray342[i3] != 0) {
						for (int l3 = i3 - 1; l3 > k2; l3--) {
							if (skinList.anIntArray342[l3] != 0) {
								continue;
							}
							ai[l2] = l3;
							ai1[l2] = 0;
							ai2[l2] = 0;
							ai3[l2] = 0;
							l2++;
							break;
						}
					}
					ai[l2] = i3;
					char c = '\0';
					if (skinList.anIntArray342[i3] == 3) {
						c = '\200';
					}
					if ((j3 & 1) != 0) {
						ai1[l2] = stream_3.method421();
					} else {
						ai1[l2] = c;
					}
					if ((j3 & 2) != 0) {
						ai2[l2] = stream_3.method421();
					} else {
						ai2[l2] = c;
					}
					if ((j3 & 4) != 0) {
						ai3[l2] = stream_3.method421();
					} else {
						ai3[l2] = c;
					}
					k2 = i3;
					l2++;
					if (skinList.anIntArray342[i3] == 5) {
						aBooleanArray643[i2] = false;
					}
				}
			}
			frame.anInt638 = l2;
			frame.anIntArray639 = new int[l2];
			frame.anIntArray640 = new int[l2];
			frame.anIntArray641 = new int[l2];
			frame.anIntArray642 = new int[l2];
			for (int k3 = 0; k3 < l2; k3++) {
				frame.anIntArray639[k3] = ai[k3];
				frame.anIntArray640[k3] = ai1[k3];
				frame.anIntArray641[k3] = ai2[k3];
				frame.anIntArray642[k3] = ai3[k3];
			}
		}
	}

	public static void nullLoader() {
		frameHeader = null;
	}

	public static FrameHeader method531(int id) {
		if (frameHeader == null) {
			return null;
		} else {
			return frameHeader[id];
		}
	}

	public static boolean method532(int i) {
		return i == -1;
	}

	private FrameHeader() {
	}

	private static FrameHeader[] frameHeader;
	public int anInt636;
	public SkinList skinList;
	public int anInt638;
	public int anIntArray639[];
	public int anIntArray640[];
	public int anIntArray641[];
	public int anIntArray642[];
	private static boolean[] aBooleanArray643;

}
