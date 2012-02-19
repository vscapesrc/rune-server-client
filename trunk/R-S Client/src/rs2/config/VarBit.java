package rs2.config;

import rs2.JagexBuffer;
import rs2.cache.JagexArchive;

public final class VarBit {

	public static void unpackConfig(JagexArchive archive) {
		JagexBuffer buffer = new JagexBuffer(archive.getData("varbit.dat"));
		int length = buffer.getUnsignedShort();
		if (cache == null) {
			cache = new VarBit[length];
		}
		for (int index = 0; index < length; index++) {
			if (cache[index] == null) {
				cache[index] = new VarBit();
			}
			cache[index].readValues(buffer);
			if (cache[index].aBoolean651) {
				Varp.cache[cache[index].anInt648].aBoolean713 = true;
			}
		}
		if (buffer.offset != buffer.payload.length) {
			System.out.println("varbit load mismatch");
		}
	}

	private void readValues(JagexBuffer stream) {
		do {
			int j = stream.getUnsignedByte();
			if (j == 0)
				return;
			if (j == 1) {
				anInt648 = stream.getUnsignedShort();
				anInt649 = stream.getUnsignedByte();
				anInt650 = stream.getUnsignedByte();
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
