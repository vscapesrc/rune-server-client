package rs2;

final class Sounds {

	private Sounds() {
		aClass6Array329 = new SoundTrack[10];
	}

	public static void unpack(ByteBuffer stream) {
		aByteArray327 = new byte[0x6baa8];
		buffer = new ByteBuffer(aByteArray327);
		SoundTrack.initialize();
		do {
			int j = stream.getShort();
			if (j == 65535) {
				return;
			}
			sounds[j] = new Sounds();
			sounds[j].method242(stream);
			anIntArray326[j] = sounds[j].method243();
		} while (true);
	}

	public static ByteBuffer getSoundBuffer(int i, int id) {
		if (sounds[id] != null) {
			Sounds sound = sounds[id];
			return sound.method244(i);
		} else {
			return null;
		}
	}

	private void method242(ByteBuffer stream) {
		for (int i = 0; i < 10; i++) {
			int j = stream.getUByte();
			if (j != 0) {
				stream.offset--;
				aClass6Array329[i] = new SoundTrack();
				aClass6Array329[i].unpack(stream);
			}
		}
		anInt330 = stream.getShort();
		anInt331 = stream.getShort();
	}

	private int method243() {
		int j = 0x98967f;
		for (int k = 0; k < 10; k++)
			if (aClass6Array329[k] != null
					&& aClass6Array329[k].anInt114 / 20 < j)
				j = aClass6Array329[k].anInt114 / 20;

		if (anInt330 < anInt331 && anInt330 / 20 < j)
			j = anInt330 / 20;
		if (j == 0x98967f || j == 0)
			return 0;
		for (int l = 0; l < 10; l++)
			if (aClass6Array329[l] != null)
				aClass6Array329[l].anInt114 -= j * 20;

		if (anInt330 < anInt331) {
			anInt330 -= j * 20;
			anInt331 -= j * 20;
		}
		return j;
	}

	private ByteBuffer method244(int i) {
		int k = method245(i);
		buffer.offset = 0;
		buffer.putInt(0x52494646);
		buffer.method403(36 + k);
		buffer.putInt(0x57415645);
		buffer.putInt(0x666d7420);
		buffer.method403(16);
		buffer.method400(1);
		buffer.method400(1);
		buffer.method403(22050);
		buffer.method403(22050);
		buffer.method400(1);
		buffer.method400(8);
		buffer.putInt(0x64617461);
		buffer.method403(k);
		buffer.offset += k;
		return buffer;
	}

	private int method245(int i) {
		int j = 0;
		for (int k = 0; k < 10; k++)
			if (aClass6Array329[k] != null
					&& aClass6Array329[k].msLength
							+ aClass6Array329[k].anInt114 > j)
				j = aClass6Array329[k].msLength + aClass6Array329[k].anInt114;

		if (j == 0)
			return 0;
		int l = (22050 * j) / 1000;
		int i1 = (22050 * anInt330) / 1000;
		int j1 = (22050 * anInt331) / 1000;
		if (i1 < 0 || i1 > l || j1 < 0 || j1 > l || i1 >= j1)
			i = 0;
		int k1 = l + (j1 - i1) * (i - 1);
		for (int l1 = 44; l1 < k1 + 44; l1++)
			aByteArray327[l1] = -128;

		for (int i2 = 0; i2 < 10; i2++)
			if (aClass6Array329[i2] != null) {
				int j2 = (aClass6Array329[i2].msLength * 22050) / 1000;
				int i3 = (aClass6Array329[i2].anInt114 * 22050) / 1000;
				int ai[] = aClass6Array329[i2].buildSoundData(j2,
						aClass6Array329[i2].msLength);
				for (int l3 = 0; l3 < j2; l3++)
					aByteArray327[l3 + i3 + 44] += (byte) (ai[l3] >> 8);

			}

		if (i > 1) {
			i1 += 44;
			j1 += 44;
			l += 44;
			int k2 = (k1 += 44) - l;
			for (int j3 = l - 1; j3 >= j1; j3--)
				aByteArray327[j3 + k2] = aByteArray327[j3];

			for (int k3 = 1; k3 < i; k3++) {
				int l2 = (j1 - i1) * k3;
				System.arraycopy(aByteArray327, i1, aByteArray327, i1 + l2, j1
						- i1);

			}

			k1 -= 44;
		}
		return k1;
	}

	private static final Sounds[] sounds = new Sounds[5000];
	public static final int[] anIntArray326 = new int[5000];
	private static byte[] aByteArray327;
	private static ByteBuffer buffer;
	private final SoundTrack[] aClass6Array329;
	private int anInt330;
	private int anInt331;

}
