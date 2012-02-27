package rs2;

public final class FrameReader {

	public static void method528(int i) {
		frameReader = new FrameReader[i + 1];
		aBooleanArray643 = new boolean[i + 1];
		for (int index = 0; index < i + 1; index++) {
			aBooleanArray643[index] = true;
		}
	}

	public static void readSequence(byte data[]) {
		JagexBuffer buffer = new JagexBuffer(data);
		buffer.offset = data.length - 8;
		int i = buffer.getUnsignedShort();
		int j = buffer.getUnsignedShort();
		int k = buffer.getUnsignedShort();
		int l = buffer.getUnsignedShort();
		int i1 = 0;
		JagexBuffer stream_1 = new JagexBuffer(data);
		stream_1.offset = i1;
		i1 += i + 2;
		JagexBuffer stream_2 = new JagexBuffer(data);
		stream_2.offset = i1;
		i1 += j;
		JagexBuffer stream_3 = new JagexBuffer(data);
		stream_3.offset = i1;
		i1 += k;
		JagexBuffer stream_4 = new JagexBuffer(data);
		stream_4.offset = i1;
		i1 += l;
		JagexBuffer stream_5 = new JagexBuffer(data);
		stream_5.offset = i1;
		SkinList skinList = new SkinList(stream_5);
		int k1 = stream_1.getUnsignedShort();
		int ai[] = new int[500];
		int ai1[] = new int[500];
		int ai2[] = new int[500];
		int ai3[] = new int[500];
		for (int index = 0; index < k1; index++) {
			int i2 = stream_1.getUnsignedShort();
			FrameReader frame = frameReader[i2] = new FrameReader();
			frame.delay = stream_4.getUnsignedByte();
			frame.skinList = skinList;
			int j2 = stream_1.getUnsignedByte();
			int k2 = -1;
			int l2 = 0;
			for (int i3 = 0; i3 < j2; i3++) {
				int j3 = stream_2.getUnsignedByte();
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
						ai1[l2] = stream_3.getUnsignedSmart();
					} else {
						ai1[l2] = c;
					}
					if ((j3 & 2) != 0) {
						ai2[l2] = stream_3.getUnsignedSmart();
					} else {
						ai2[l2] = c;
					}
					if ((j3 & 4) != 0) {
						ai3[l2] = stream_3.getUnsignedSmart();
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

	public static void clearCache() {
		frameReader = null;
	}

	public static FrameReader getFrames(int id) {
		if (frameReader == null) {
			return null;
		} else {
			return frameReader[id];
		}
	}

	public static boolean method532(int i) {
		return i == -1;
	}

	private FrameReader() {
	}

	private static FrameReader[] frameReader;
	public int delay;
	public SkinList skinList;
	public int anInt638;
	public int anIntArray639[];
	public int anIntArray640[];
	public int anIntArray641[];
	public int anIntArray642[];
	private static boolean[] aBooleanArray643;

}
