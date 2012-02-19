package rs2.config;

import rs2.JagexBuffer;
import rs2.cache.JagexArchive;

public final class Varp {

	public static void unpackConfig(JagexArchive archive) {
		JagexBuffer buffer = new JagexBuffer(archive.getData("varp.dat"));
		anInt702 = 0;
		int length = buffer.getUnsignedShort();
		if (cache == null) {
			cache = new Varp[length];
		}			
		if (anIntArray703 == null) {
			anIntArray703 = new int[length];
		}
		for (int index = 0; index < length; index++) {
			if (cache[index] == null)
				cache[index] = new Varp();
			cache[index].readValues(buffer, index);
		}
		if (buffer.offset != buffer.payload.length) {
			System.out.println("varptype load mismatch");
		}
	}

	private void readValues(JagexBuffer stream, int i) {
		do {
			int opcode = stream.getUnsignedByte();
			if (opcode == 0)
				return;
			if (opcode == 1)
				stream.getUnsignedByte();
			else if (opcode == 2)
				stream.getUnsignedByte();
			else if (opcode == 3)
				anIntArray703[anInt702++] = i;
			else if (opcode == 4) {
			} else if (opcode == 5)
				actionId = stream.getUnsignedShort();
			else if (opcode == 6) {
			} else if (opcode == 7)
				stream.getInt();
			else if (opcode == 8)
				aBoolean713 = true;
			else if (opcode == 10)
				stream.getString();
			else if (opcode == 11)
				aBoolean713 = true;
			else if (opcode == 12)
				stream.getInt();
			else if (opcode == 13) {
			} else
				System.out.println("Error unrecognised config code: " + opcode);
		} while (true);
	}

	/**
	 * Returns the total amount of varp data.
	 * @return
	 */
	public static int getCount() {
		return cache.length;
	}

	private Varp() {
		aBoolean713 = false;
	}

	public static Varp cache[];
	private static int anInt702;
	private static int[] anIntArray703;
	public int actionId;
	public boolean aBoolean713;

}
