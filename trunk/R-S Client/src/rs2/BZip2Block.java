package rs2;

final class BZip2Block {

	BZip2Block() {
		unzftabEntries = new int[256];
		anIntArray585 = new int[257];
		symbolBitmap = new boolean[256];
		byteBitmap = new boolean[16];
		usedBitmap = new byte[256];
		decomData = new byte[4096];
		anIntArray593 = new int[16];
		huffmanSelectors = new byte[18002];
		huffmanSelectorMTF = new byte[18002];
		tableLengths = new byte[6][258];
		tableLimits = new int[6][258];
		tableBases = new int[6][258];
		tablePerms = new int[6][258];
		minLengths = new int[6];
	}

	byte rawData[];
	int fileStart;
	int fileLength;
	int dummy2;
	int dummy3;
	byte decompressedData[];
	int anInt569;
	int fileSize;
	int anInt571;
	int anInt572;
	byte aByte573;
	int anInt574;
	boolean isRandomized;
	int current2Bytes;
	int bitPosition;
	int anInt578;
	int anInt579;
	int startingPointer;
	int anInt581;
	int anInt582;
	final int[] unzftabEntries;
	int anInt584;
	final int[] anIntArray585;
	public static int buffer[];
	int numSymbolsUsed;
	final boolean[] symbolBitmap;
	final boolean[] byteBitmap;
	final byte[] usedBitmap;
	final byte[] decomData;
	final int[] anIntArray593;
	final byte[] huffmanSelectors;
	final byte[] huffmanSelectorMTF;
	final byte[][] tableLengths;
	final int[][] tableLimits;
	final int[][] tableBases;
	final int[][] tablePerms;
	final int[] minLengths;
	int anInt601;
}
