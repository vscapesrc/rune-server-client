package rs2.cache;

import rs2.BZip2InputStream;
import rs2.ByteBuffer;

public final class JagexArchive {

	public JagexArchive(byte data[]) {
		ByteBuffer stream = new ByteBuffer(data);
		int decompressedSize = stream.get3Bytes();
		int j = stream.get3Bytes();
		if (j != decompressedSize) {
			byte abyte1[] = new byte[decompressedSize];
			BZip2InputStream.resetAndRead(abyte1, decompressedSize, data, j, 6);
			finalBuffer = abyte1;
			stream = new ByteBuffer(finalBuffer);
			compressedAsWhole = true;
		} else {
			finalBuffer = data;
			compressedAsWhole = false;
		}
		totalFiles = stream.getShort();
		identifiers = new int[totalFiles];
		decompressedSizes = new int[totalFiles];
		compressedSizes = new int[totalFiles];
		startOffsets = new int[totalFiles];
		int offset = stream.offset + totalFiles * 10;
		for (int index = 0; index < totalFiles; index++) {
			identifiers[index] = stream.getInt();
			decompressedSizes[index] = stream.get3Bytes();
			compressedSizes[index] = stream.get3Bytes();
			startOffsets[index] = offset;
			offset += compressedSizes[index];
		}
	}

	public byte[] getData(String file) {
		byte dataBuffer[] = null; 
		int identifier = 0;
		file = file.toUpperCase();
		for (int index = 0; index < file.length(); index++) {
			identifier = (identifier * 61 + file.charAt(index)) - 32;
		}
		for (int index = 0; index < totalFiles; index++)
			if (identifiers[index] == identifier) {
				if (dataBuffer == null) {
					dataBuffer = new byte[decompressedSizes[index]];
				}
				if (!compressedAsWhole) {
					BZip2InputStream.resetAndRead(dataBuffer, decompressedSizes[index], finalBuffer, compressedSizes[index], startOffsets[index]);
				} else {
					System.arraycopy(finalBuffer, startOffsets[index], dataBuffer, 0, decompressedSizes[index]);
				}
				return dataBuffer;
			}
		return null;
	}

	private final byte[] finalBuffer;
	private final int totalFiles;
	private final int[] identifiers;
	private final int[] decompressedSizes;
	private final int[] compressedSizes;
	private final int[] startOffsets;
	private final boolean compressedAsWhole;
}
