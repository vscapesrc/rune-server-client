package rs2;

public final class BZip2InputStream {

	public static int resetAndRead(byte decompressedData[], int fileSize, byte rawData[], int length, int fileStart) {
		synchronized (archiveInfo) {
			archiveInfo.rawData = rawData;
			archiveInfo.fileStart = fileStart;
			archiveInfo.decompressedData = decompressedData;
			archiveInfo.anInt569 = 0;
			archiveInfo.fileLength = length;
			archiveInfo.fileSize = fileSize;
			archiveInfo.bitPosition = 0;
			archiveInfo.current2Bytes = 0;
			archiveInfo.dummy2 = 0;
			archiveInfo.dummy3 = 0;
			archiveInfo.anInt571 = 0;
			archiveInfo.anInt572 = 0;
			archiveInfo.anInt579 = 0;
			readNextFile(archiveInfo);
			fileSize -= archiveInfo.fileSize;
			return fileSize;
		}
	}

	private static void determineNextFileHeader(BZip2Block archiveInfo) {
		byte byte4 = archiveInfo.aByte573;
		int i = archiveInfo.anInt574;
		int j = archiveInfo.anInt584;
		int k = archiveInfo.anInt582;
		int ai[] = BZip2Block.buffer;
		int l = archiveInfo.anInt581;
		byte abyte0[] = archiveInfo.decompressedData;
		int i1 = archiveInfo.anInt569;
		int j1 = archiveInfo.fileSize;
		int k1 = j1;
		int l1 = archiveInfo.anInt601 + 1;
		label0: do {
			if (i > 0) {
				do {
					if (j1 == 0)
						break label0;
					if (i == 1)
						break;
					abyte0[i1] = byte4;
					i--;
					i1++;
					j1--;
				} while (true);
				if (j1 == 0) {
					i = 1;
					break;
				}
				abyte0[i1] = byte4;
				i1++;
				j1--;
			}
			boolean flag = true;
			while (flag) {
				flag = false;
				if (j == l1) {
					i = 0;
					break label0;
				}
				byte4 = (byte) k;
				l = ai[l];
				byte byte0 = (byte) (l & 0xff);
				l >>= 8;
				j++;
				if (byte0 != k) {
					k = byte0;
					if (j1 == 0) {
						i = 1;
					} else {
						abyte0[i1] = byte4;
						i1++;
						j1--;
						flag = true;
						continue;
					}
					break label0;
				}
				if (j != l1)
					continue;
				if (j1 == 0) {
					i = 1;
					break label0;
				}
				abyte0[i1] = byte4;
				i1++;
				j1--;
				flag = true;
			}
			i = 2;
			l = ai[l];
			byte byte1 = (byte) (l & 0xff);
			l >>= 8;
			if (++j != l1)
				if (byte1 != k) {
					k = byte1;
				} else {
					i = 3;
					l = ai[l];
					byte byte2 = (byte) (l & 0xff);
					l >>= 8;
					if (++j != l1)
						if (byte2 != k) {
							k = byte2;
						} else {
							l = ai[l];
							byte byte3 = (byte) (l & 0xff);
							l >>= 8;
							j++;
							i = (byte3 & 0xff) + 4;
							l = ai[l];
							k = (byte) (l & 0xff);
							l >>= 8;
							j++;
						}
				}
		} while (true);
		int i2 = archiveInfo.anInt571;
		archiveInfo.anInt571 += k1 - j1;
		if (archiveInfo.anInt571 < i2)
			archiveInfo.anInt572++;
		archiveInfo.aByte573 = byte4;
		archiveInfo.anInt574 = i;
		archiveInfo.anInt584 = j;
		archiveInfo.anInt582 = k;
		BZip2Block.buffer = ai;
		archiveInfo.anInt581 = l;
		archiveInfo.decompressedData = abyte0;
		archiveInfo.anInt569 = i1;
		archiveInfo.fileSize = j1;
	}

	private static void readNextFile(BZip2Block archiveInfo) {
		int tMinLength = 0;
		int limit[] = null;
		int tBase[] = null;
		int tPerm[] = null;
		archiveInfo.anInt578 = 1;
		if (BZip2Block.buffer == null) {
			BZip2Block.buffer = new int[archiveInfo.anInt578 * 0x186a0];
		}
		boolean isReading = true;
		while (isReading) {
			byte curByte = getByte(archiveInfo);
			if (curByte == 23) {
				return;
			}
			curByte = getByte(archiveInfo);
			curByte = getByte(archiveInfo);
			curByte = getByte(archiveInfo);
			curByte = getByte(archiveInfo);
			curByte = getByte(archiveInfo);
			archiveInfo.anInt579++;
			curByte = getByte(archiveInfo);
			curByte = getByte(archiveInfo);
			curByte = getByte(archiveInfo);
			curByte = getByte(archiveInfo);
			curByte = getBit(archiveInfo);
			archiveInfo.isRandomized = curByte != 0;
			if (archiveInfo.isRandomized) {
				System.out.println("PANIC! RANDOMISED BLOCK!");
			}
			archiveInfo.startingPointer = 0;
			curByte = getByte(archiveInfo);
			archiveInfo.startingPointer = archiveInfo.startingPointer << 8 | curByte & 0xff;
			curByte = getByte(archiveInfo);
			archiveInfo.startingPointer = archiveInfo.startingPointer << 8 | curByte & 0xff;
			curByte = getByte(archiveInfo);
			archiveInfo.startingPointer = archiveInfo.startingPointer << 8 | curByte & 0xff;
			for (int index = 0; index < 16; index++) {
				byte byte1 = getBit(archiveInfo);
				archiveInfo.byteBitmap[index] = byte1 == 1;
			}
			for (int index = 0; index < 256; index++) {
				archiveInfo.symbolBitmap[index] = false;
			}
			for (int index = 0; index < 16; index++) {
				if (archiveInfo.byteBitmap[index]) {
					for (int index2 = 0; index2 < 16; index2++) {
						byte byte2 = getBit(archiveInfo);
						if (byte2 == 1)
							archiveInfo.symbolBitmap[index * 16 + index2] = true;
					}
				}
			}
			createBitmaps(archiveInfo);
			int alphaSize = archiveInfo.numSymbolsUsed + 2;
			int numHuffmanTables = getBits(3, archiveInfo);
			int numHuffmanSelectors = getBits(15, archiveInfo);
			for (int curSelectorIndex = 0; curSelectorIndex < numHuffmanSelectors; curSelectorIndex++) {
				int selectorValue = 0;
				do {
					byte bit = getBit(archiveInfo);
					if (bit == 0) {
						break;
					}
					selectorValue++;
				} while (true);
				archiveInfo.huffmanSelectorMTF[curSelectorIndex] = (byte) selectorValue;
			}

			byte pos[] = new byte[6];
			for (byte tableIndex = 0; tableIndex < numHuffmanTables; tableIndex++) {
				pos[tableIndex] = tableIndex;
			}
			for (int selectorIndex = 0; selectorIndex < numHuffmanSelectors; selectorIndex++) {
				byte selectorMTF = archiveInfo.huffmanSelectorMTF[selectorIndex];
				byte selectorUndoneMTF = pos[selectorMTF];
				for (; selectorMTF > 0; selectorMTF--) {
					pos[selectorMTF] = pos[selectorMTF - 1];
				}
				pos[0] = selectorUndoneMTF;
				archiveInfo.huffmanSelectors[selectorIndex] = selectorUndoneMTF;
			}
			for (int tableIndex = 0; tableIndex < numHuffmanTables; tableIndex++) {
				int deltaStartLength = getBits(5, archiveInfo);
				for (int alphaIndex = 0; alphaIndex < alphaSize; alphaIndex++) {
					do {
						byte bitFlag = getBit(archiveInfo);
						if (bitFlag == 0) {
							break;
						}
						bitFlag = getBit(archiveInfo);
						if (bitFlag == 0) {
							deltaStartLength++;
						} else {
							deltaStartLength--;
						}
					} while (true);
					archiveInfo.tableLengths[tableIndex][alphaIndex] = (byte) deltaStartLength;
				}
			}
			for (int tableIndex = 0; tableIndex < numHuffmanTables; tableIndex++) {
				byte minLength = 32;
				int maxLength = 0;
				for (int alphaIndex = 0; alphaIndex < alphaSize; alphaIndex++) {
					if (archiveInfo.tableLengths[tableIndex][alphaIndex] > maxLength) {
						maxLength = archiveInfo.tableLengths[tableIndex][alphaIndex];
					}
					if (archiveInfo.tableLengths[tableIndex][alphaIndex] < minLength) {
						minLength = archiveInfo.tableLengths[tableIndex][alphaIndex];
					}
				}
				createDecodeTables(archiveInfo.tableLimits[tableIndex], archiveInfo.tableBases[tableIndex], archiveInfo.tablePerms[tableIndex], archiveInfo.tableLengths[tableIndex], minLength, maxLength, alphaSize);
				archiveInfo.minLengths[tableIndex] = minLength;
			}
			int endOfBlockChar = archiveInfo.numSymbolsUsed + 1;
			int groupNumber = -1;
			int groupPosition = 0;
			for (int unzftabIndex = 0; unzftabIndex <= 255; unzftabIndex++) {
				archiveInfo.unzftabEntries[unzftabIndex] = 0;
			}
			int counter = 4095;
			for (int index1 = 15; index1 >= 0; index1--) {
				for (int index2 = 15; index2 >= 0; index2--) {
					archiveInfo.decomData[counter] = (byte) (index1 * 16 + index2);
					counter--;
				}
				archiveInfo.anIntArray593[index1] = counter + 1;
			}

			int i6 = 0;
			if (groupPosition == 0) {
				groupNumber++;
				groupPosition = 50;
				byte selector = archiveInfo.huffmanSelectors[groupNumber];
				tMinLength = archiveInfo.minLengths[selector];
				limit = archiveInfo.tableLimits[selector];
				tPerm = archiveInfo.tablePerms[selector];
				tBase = archiveInfo.tableBases[selector];
			}
			groupPosition--;
			int minLength = tMinLength;
			int bitsReadValue;
			byte bitReadValue;
			for (bitsReadValue = getBits(minLength, archiveInfo); bitsReadValue > limit[minLength]; bitsReadValue = bitsReadValue << 1 | bitReadValue) {
				minLength++;
				bitReadValue = getBit(archiveInfo);
			}
			for (int perm = tPerm[bitsReadValue - tBase[minLength]]; perm != endOfBlockChar;) {
				if (perm == 0 || perm == 1) {
					int index = -1;
					int k6 = 1;
					do {
						if (perm == 0) {
							index += k6;
						} else if (perm == 1) {
							index += 2 * k6;
						}
						k6 *= 2;
						if (groupPosition == 0) {
							groupNumber++;
							groupPosition = 50;
							byte selector = archiveInfo.huffmanSelectors[groupNumber];
							tMinLength = archiveInfo.minLengths[selector];
							limit = archiveInfo.tableLimits[selector];
							tPerm = archiveInfo.tablePerms[selector];
							tBase = archiveInfo.tableBases[selector];
						}
						groupPosition--;
						int tMinLen = tMinLength;
						int bitsRead;
						byte byte10;
						for (bitsRead = getBits(tMinLen, archiveInfo); bitsRead > limit[tMinLen]; bitsRead = bitsRead << 1 | byte10) {
							tMinLen++;
							byte10 = getBit(archiveInfo);
						}
						perm = tPerm[bitsRead - tBase[tMinLen]];
					} while (perm == 0 || perm == 1);
					index++;
					byte byte5 = archiveInfo.usedBitmap[archiveInfo.decomData[archiveInfo.anIntArray593[0]] & 0xff];
					archiveInfo.unzftabEntries[byte5 & 0xff] += index;
					for (; index > 0; index--) {
						BZip2Block.buffer[i6] = byte5 & 0xff;
						i6++;
					}
				} else {
					int j11 = perm - 1;
					byte byte6;
					if (j11 < 16) {
						int j10 = archiveInfo.anIntArray593[0];
						byte6 = archiveInfo.decomData[j10 + j11];
						for (; j11 > 3; j11 -= 4) {
							int k11 = j10 + j11;
							archiveInfo.decomData[k11] = archiveInfo.decomData[k11 - 1];
							archiveInfo.decomData[k11 - 1] = archiveInfo.decomData[k11 - 2];
							archiveInfo.decomData[k11 - 2] = archiveInfo.decomData[k11 - 3];
							archiveInfo.decomData[k11 - 3] = archiveInfo.decomData[k11 - 4];
						}

						for (; j11 > 0; j11--) {
							archiveInfo.decomData[j10 + j11] = archiveInfo.decomData[(j10 + j11) - 1];
						}
						archiveInfo.decomData[j10] = byte6;
					} else {
						int l10 = j11 / 16;
						int i11 = j11 % 16;
						int k10 = archiveInfo.anIntArray593[l10] + i11;
						byte6 = archiveInfo.decomData[k10];
						for (; k10 > archiveInfo.anIntArray593[l10]; k10--)
							archiveInfo.decomData[k10] = archiveInfo.decomData[k10 - 1];

						archiveInfo.anIntArray593[l10]++;
						for (; l10 > 0; l10--) {
							archiveInfo.anIntArray593[l10]--;
							archiveInfo.decomData[archiveInfo.anIntArray593[l10]] = archiveInfo.decomData[(archiveInfo.anIntArray593[l10 - 1] + 16) - 1];
						}

						archiveInfo.anIntArray593[0]--;
						archiveInfo.decomData[archiveInfo.anIntArray593[0]] = byte6;
						if (archiveInfo.anIntArray593[0] == 0) {
							int i10 = 4095;
							for (int k9 = 15; k9 >= 0; k9--) {
								for (int l9 = 15; l9 >= 0; l9--) {
									archiveInfo.decomData[i10] = archiveInfo.decomData[archiveInfo.anIntArray593[k9]
											+ l9];
									i10--;
								}

								archiveInfo.anIntArray593[k9] = i10 + 1;
							}

						}
					}
					archiveInfo.unzftabEntries[archiveInfo.usedBitmap[byte6 & 0xff] & 0xff]++;
					BZip2Block.buffer[i6] = archiveInfo.usedBitmap[byte6 & 0xff] & 0xff;
					i6++;
					if (groupPosition == 0) {
						groupNumber++;
						groupPosition = 50;
						byte byte14 = archiveInfo.huffmanSelectors[groupNumber];
						tMinLength = archiveInfo.minLengths[byte14];
						limit = archiveInfo.tableLimits[byte14];
						tPerm = archiveInfo.tablePerms[byte14];
						tBase = archiveInfo.tableBases[byte14];
					}
					groupPosition--;
					int k7 = tMinLength;
					int index;
					byte byte11;
					for (index = getBits(k7, archiveInfo); index > limit[k7]; index = index << 1 | byte11) {
						k7++;
						byte11 = getBit(archiveInfo);
					}
					perm = tPerm[index - tBase[k7]];
				}
			}
			archiveInfo.anInt574 = 0;
			archiveInfo.aByte573 = 0;
			archiveInfo.anIntArray585[0] = 0;
			for (int index = 1; index <= 256; index++) {
				archiveInfo.anIntArray585[index] = archiveInfo.unzftabEntries[index - 1];
			}
			for (int index = 1; index <= 256; index++) {
				archiveInfo.anIntArray585[index] += archiveInfo.anIntArray585[index - 1];
			}
			for (int index = 0; index < i6; index++) {
				byte byte7 = (byte) (BZip2Block.buffer[index] & 0xff);
				BZip2Block.buffer[archiveInfo.anIntArray585[byte7 & 0xff]] |= index << 8;
				archiveInfo.anIntArray585[byte7 & 0xff]++;
			}

			archiveInfo.anInt581 = BZip2Block.buffer[archiveInfo.startingPointer] >> 8;
			archiveInfo.anInt584 = 0;
			archiveInfo.anInt581 = BZip2Block.buffer[archiveInfo.anInt581];
			archiveInfo.anInt582 = (byte) (archiveInfo.anInt581 & 0xff);
			archiveInfo.anInt581 >>= 8;
			archiveInfo.anInt584++;
			archiveInfo.anInt601 = i6;
			determineNextFileHeader(archiveInfo);
			isReading = archiveInfo.anInt584 == archiveInfo.anInt601 + 1 && archiveInfo.anInt574 == 0;
		}
	}

	private static byte getByte(BZip2Block archiveInfo) {
		return (byte) getBits(8, archiveInfo);
	}

	private static byte getBit(BZip2Block archiveInfo) {
		return (byte) getBits(1, archiveInfo);
	}

	private static int getBits(int count, BZip2Block archiveInfo) {
		int j;
		do {
			if (archiveInfo.bitPosition >= count) {
				int k = archiveInfo.current2Bytes >> archiveInfo.bitPosition - count & (1 << count) - 1;
				archiveInfo.bitPosition -= count;
				j = k;
				break;
			}
			archiveInfo.current2Bytes = archiveInfo.current2Bytes << 8
					| archiveInfo.rawData[archiveInfo.fileStart] & 0xff;
			archiveInfo.bitPosition += 8;
			archiveInfo.fileStart++;
			archiveInfo.fileLength--;
			archiveInfo.dummy2++;
			if (archiveInfo.dummy2 == 0) {
				archiveInfo.dummy3++;
			}
		} while (true);
		return j;
	}

	private static void createBitmaps(BZip2Block archiveInfo) {
		archiveInfo.numSymbolsUsed = 0;
		for (int index = 0; index < 256; index++) {
			if (archiveInfo.symbolBitmap[index]) {
				archiveInfo.usedBitmap[archiveInfo.numSymbolsUsed] = (byte) index;
				archiveInfo.numSymbolsUsed++;
			}
		}
	}

	private static void createDecodeTables(int tableLimits[], int tableBases[], int tablePerms[], byte tableLengths[], int minLength, int maxLength, int alphaSize) {
		int l = 0;
		for (int index = minLength; index <= maxLength; index++) {
			for (int index2 = 0; index2 < alphaSize; index2++) {
				if (tableLengths[index2] == index) {
					tablePerms[l] = index2;
					l++;
				}
			}
		}
		for (int index = 0; index < 23; index++) {
			tableBases[index] = 0;
		}
		for (int indx = 0; indx < alphaSize; indx++) {
			tableBases[tableLengths[indx] + 1]++;
		}
		for (int index = 1; index < 23; index++) {
			tableBases[index] += tableBases[index - 1];
		}
		for (int index = 0; index < 23; index++) {
			tableLimits[index] = 0;
		}
		int i3 = 0;
		for (int index = minLength; index <= maxLength; index++) {
			i3 += tableBases[index + 1] - tableBases[index];
			tableLimits[index] = i3 - 1;
			i3 <<= 1;
		}
		for (int index = minLength + 1; index <= maxLength; index++) {
			tableBases[index] = (tableLimits[index - 1] + 1 << 1) - tableBases[index];
		}
	}

	private static final BZip2Block archiveInfo = new BZip2Block();

}
