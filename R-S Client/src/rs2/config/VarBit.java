package rs2.config;

import rs2.ByteBuffer;
import rs2.cache.JagexArchive;

public final class VarBit {

	public static void unpackConfig(JagexArchive streamLoader) {
		ByteBuffer stream = new ByteBuffer(streamLoader.getData("varbit.dat"));
		int cacheSize = stream.getShort();
		if (cache == null)
			cache = new VarBit[cacheSize];
		for (int j = 0; j < cacheSize; j++) {
			if (cache[j] == null)
				cache[j] = new VarBit();
			cache[j].readValues(stream);
			if (cache[j].aBoolean651)
				Varp.cache[cache[j].anInt648].aBoolean713 = true;
		}

		if (stream.offset != stream.buffer.length)
			System.out.println("varbit load mismatch");
	}

	private void readValues(ByteBuffer stream) {
		do {
			int j = stream.getUByte();
			if (j == 0)
				return;
			if (j == 1) {
				anInt648 = stream.getShort();
				anInt649 = stream.getUByte();
				anInt650 = stream.getUByte();
			} else if (j == 10)
				stream.getString();
			else if (j == 2)
				aBoolean651 = true;
			else if (j == 3)
				stream.getInt();
			else if (j == 4)
				stream.getInt();
			else
				System.out.println("Error unrecognised config code: " + j);
		} while (true);
	}

	private VarBit() {
		aBoolean651 = false;
	}

	public static VarBit cache[];
	public int anInt648;
	public int anInt649;
	public int anInt650;
	private boolean aBoolean651;
}
