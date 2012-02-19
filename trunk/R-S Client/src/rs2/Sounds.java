package rs2;

final class Sounds {

	private Sounds() {
		soundTrack = new SoundTrack[10];
	}

	public static void unpack(JagexBuffer stream) {
		aByteArray327 = new byte[0x6baa8];
		buffer = new JagexBuffer(aByteArray327);
		SoundTrack.initialize();
		do {
			int j = stream.getUnsignedShort();
			if (j == 65535) {
				return;
			}
			sounds[j] = new Sounds();
			sounds[j].method242(stream);
			anIntArray326[j] = sounds[j].method243();
		} while (true);
	}

	public static JagexBuffer getSoundBuffer(int i, int id) {
		if (sounds[id] != null) {
			Sounds sound = sounds[id];
			return sound.method244(i);
		} else {
			return null;
		}
	}

	private void method242(JagexBuffer stream) {
		for (int sound = 0; sound < 10; sound++) {
			int j = stream.getUnsignedByte();
			if (j != 0) {
				stream.offset--;
				soundTrack[sound] = new SoundTrack();
				soundTrack[sound].unpack(stream);
			}
		}
		anInt330 = stream.getUnsignedShort();
		anInt331 = stream.getUnsignedShort();
	}

	private int method243() {
		int j = 0x98967f;
		for (int index = 0; index < 10; index++) {
			if (soundTrack[index] != null && soundTrack[index].anInt114 / 20 < j) {
				j = soundTrack[index].anInt114 / 20;
			}
		}
		if (anInt330 < anInt331 && anInt330 / 20 < j) {
			j = anInt330 / 20;
		}
		if (j == 0x98967f || j == 0) {
			return 0;
		}
		for (int index = 0; index < 10; index++) {
			if (soundTrack[index] != null) {
				soundTrack[index].anInt114 -= j * 20;
			}
		}
		if (anInt330 < anInt331) {
			anInt330 -= j * 20;
			anInt331 -= j * 20;
		}
		return j;
	}

	private JagexBuffer method244(int i) {
		int k = method245(i);
		buffer.offset = 0;
		buffer.putInt(0x52494646);
		buffer.putLEInt(36 + k);
		buffer.putInt(0x57415645);
		buffer.putInt(0x666d7420);
		buffer.putLEInt(16);
		buffer.putLEShort_duplicate(1);
		buffer.putLEShort_duplicate(1);
		buffer.putLEInt(22050);
		buffer.putLEInt(22050);
		buffer.putLEShort_duplicate(1);
		buffer.putLEShort_duplicate(8);
		buffer.putInt(0x64617461);
		buffer.putLEInt(k);
		buffer.offset += k;
		return buffer;
	}

	private int method245(int i) {
		int j = 0;
		for (int index = 0; index < 10; index++) {
			if (soundTrack[index] != null && soundTrack[index].msLength + soundTrack[index].anInt114 > j) {
				j = soundTrack[index].msLength + soundTrack[index].anInt114;
			}
		}
		if (j == 0) {
			return 0;
		}
		int l = (22050 * j) / 1000;
		int i1 = (22050 * anInt330) / 1000;
		int j1 = (22050 * anInt331) / 1000;
		if (i1 < 0 || i1 > l || j1 < 0 || j1 > l || i1 >= j1)
			i = 0;
		int k1 = l + (j1 - i1) * (i - 1);
		for (int index = 44; index < k1 + 44; index++) {
			aByteArray327[index] = -128;
		}
		for (int sound = 0; sound < 10; sound++) {
			if (soundTrack[sound] != null) {
				int length = (soundTrack[sound].msLength * 22050) / 1000;
				int i3 = (soundTrack[sound].anInt114 * 22050) / 1000;
				int ai[] = soundTrack[sound].buildSoundData(length, soundTrack[sound].msLength);
				for (int index = 0; index < length; index++) {
					aByteArray327[index + i3 + 44] += (byte) (ai[index] >> 8);
				}
			}
		}
		if (i > 1) {
			i1 += 44;
			j1 += 44;
			l += 44;
			int k2 = (k1 += 44) - l;
			for (int index = l - 1; index >= j1; index--) {
				aByteArray327[index + k2] = aByteArray327[index];
			}
			for (int index = 1; index < i; index++) {
				int l2 = (j1 - i1) * index;
				System.arraycopy(aByteArray327, i1, aByteArray327, i1 + l2, j1 - i1);
			}
			k1 -= 44;
		}
		return k1;
	}

	private static final Sounds[] sounds = new Sounds[5000];
	public static final int[] anIntArray326 = new int[5000];
	private static byte[] aByteArray327;
	private static JagexBuffer buffer;
	private final SoundTrack[] soundTrack;
	private int anInt330;
	private int anInt331;

}