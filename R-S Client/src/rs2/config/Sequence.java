package rs2.config;

import rs2.FrameHeader;
import rs2.ByteBuffer;
import rs2.cache.JagexArchive;

public final class Sequence {

	public static void unpackConfig(JagexArchive streamLoader) {
		ByteBuffer stream = new ByteBuffer(streamLoader.getData("seq.dat"));
		total = stream.getShort();
		if (anims == null)
			anims = new Sequence[total];
		for (int j = 0; j < total; j++) {
			if (anims[j] == null)
				anims[j] = new Sequence();
			anims[j].readValues(stream);
		}
	}

	/**
	 * Returns the sequence for a given id.
	 * @param id
	 * @return
	 */
	public static Sequence getSeq(int id) {
		if (id > total - 1) {
			return anims[808];
		}
		if (anims[id] == null) {
			return anims[808];
		}
		return anims[id];
	}

	public int method258(int i) {
		int j = delays[i];
		if (j == 0) {
			FrameHeader frameHeader = FrameHeader.method531(frames[i]);
			if (frameHeader != null) {
				j = delays[i] = frameHeader.anInt636;
			}
		}
		if (j == 0)
			j = 1;
		return j;
	}

	private void readValues(ByteBuffer stream) {
		do {
			int i = stream.getUByte();
			if (i == 0)
				break;
			if (i == 1) {
				totalFrames = stream.getUByte();
				frames = new int[totalFrames];
				anIntArray354 = new int[totalFrames];
				delays = new int[totalFrames];
				for (int j = 0; j < totalFrames; j++) {
					frames[j] = stream.getShort();
					anIntArray354[j] = stream.getShort();
					if (anIntArray354[j] == 65535)
						anIntArray354[j] = -1;
					delays[j] = stream.getShort();
				}

			} else if (i == 2)
				frameStep = stream.getShort();
			else if (i == 3) {
				int k = stream.getUByte();
				anIntArray357 = new int[k + 1];
				for (int l = 0; l < k; l++)
					anIntArray357[l] = stream.getUByte();

				anIntArray357[k] = 0x98967f;
			} else if (i == 4)
				aBoolean358 = true;
			else if (i == 5)
				anInt359 = stream.getUByte();
			else if (i == 6)
				anInt360 = stream.getShort();
			else if (i == 7)
				anInt361 = stream.getShort();
			else if (i == 8)
				anInt362 = stream.getUByte();
			else if (i == 9)
				anInt363 = stream.getUByte();
			else if (i == 10)
				priority = stream.getUByte();
			else if (i == 11)
				anInt365 = stream.getUByte();
			else if (i == 12)
				stream.getInt();
			else
				System.out.println("Error unrecognised seq config code: " + i);
		} while (true);
		if (totalFrames == 0) {
			totalFrames = 1;
			frames = new int[1];
			frames[0] = -1;
			anIntArray354 = new int[1];
			anIntArray354[0] = -1;
			delays = new int[1];
			delays[0] = -1;
		}
		if (anInt363 == -1)
			if (anIntArray357 != null)
				anInt363 = 2;
			else
				anInt363 = 0;
		if (priority == -1) {
			if (anIntArray357 != null) {
				priority = 2;
				return;
			}
			priority = 0;
		}
	}

	private Sequence() {
		frameStep = -1;
		aBoolean358 = false;
		anInt359 = 5;
		anInt360 = -1;
		anInt361 = -1;
		anInt362 = 99;
		anInt363 = -1;
		priority = -1;
		anInt365 = 2;
	}

	public static int total;
	public static Sequence anims[];
	public int totalFrames;
	public int frames[];
	public int anIntArray354[];
	private int[] delays;
	public int frameStep;
	public int anIntArray357[];
	public boolean aBoolean358;
	public int anInt359;
	public int anInt360;
	public int anInt361;
	public int anInt362;
	public int anInt363;
	public int priority;
	public int anInt365;
	public static int anInt367;
}
