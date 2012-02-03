package rs2.cache;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class ResourceCache {

	private static final byte[] buffer = new byte[520];
	private final RandomAccessFile data;
	private final RandomAccessFile index;
	private final int version;

	/**
	 * Creates a cache hook, to put and get files from the cache.
	 * @param data The data file.
	 * @param index The index file.
	 * @param version The version of the cache un/packer.
	 */
	public ResourceCache(RandomAccessFile data, RandomAccessFile index, int version) {
		this.version = version;
		this.data = data;
		this.index = index;
	}

	/**
	 * Gets some data from the cache.
	 * @param requestedFileID The file ID to retrieve.
	 * @return The data requested, or null if an error occured.
	 */
	public synchronized byte[] get(int requestFileId) {
		try {
			seek(index, requestFileId * 6);
			for (int offset = 0, numBytesRead; offset < 6; offset += numBytesRead) {
				numBytesRead = index.read(buffer, offset, 6 - offset);
				if (numBytesRead == -1) {
					return null;
				}
			}

			int fileLength = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
			int fileIndex = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
			if (fileIndex <= 0 || (long) fileIndex > data.length() / 520L) {
				return null;
			}
			byte fileData[] = new byte[fileLength];
			int bytesRead = 0;
			for (int index1 = 0; bytesRead < fileLength; index1++) {
				if (fileIndex == 0) {
					return null;
				}
				seek(data, fileIndex * 520);
				int dataLeft = fileLength - bytesRead;
				if (dataLeft > 512) {
					dataLeft = 512;
				}
				for (int numBytesRead = 0, offset; numBytesRead < dataLeft + 8; numBytesRead += offset) {
					offset = data.read(buffer, numBytesRead, (dataLeft + 8) - numBytesRead);
					if (offset == -1) {
						return null;
					}
				}
				int fileId = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
				int l2 = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
				int dataFileIndex = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
				int version = buffer[7] & 0xff;
				if (fileId != requestFileId || l2 != index1 || version != this.version) {
					return null;
				}
				if (dataFileIndex < 0 || (long) dataFileIndex > data.length() / 520L) {
					return null;
				}
				for (int index2 = 0; index2 < dataLeft; index2++) {
					fileData[bytesRead++] = buffer[index2 + 8];
				}
				fileIndex = dataFileIndex;
			}
			return fileData;
		} catch (IOException _ex) {
			return null;
		}
	}

	/**
	 * Essentially this creates the entry if it doesn't exist already,
	 * while performing a "put" operation and adding the data to the cache.
	 * @param fileIndex The file index.
	 * @param fileData The file data.
	 * @param requestedFileID The file ID.
	 * @return Whether the put operation was succesful or not.
	 */
	public synchronized boolean put(int fileIndex, byte fileData[], int requestedFileId) {
		boolean exists = put(true, requestedFileId, fileIndex, fileData);
		if (!exists) {
			exists = put(false, requestedFileId, fileIndex, fileData);
		}
		return exists;
	}

	/**
	 * Puts a file into the cache.
	 * @param exists Whether or not the file exists.
	 * @param requestedFileID The file ID.
	 * @param fileIndex The file index.
	 * @param fileData The file data.
	 * @return Whether the put operation was succesful or not.
	 */
	private synchronized boolean put(boolean exists, int requestedFileId, int fileIndex, byte fileData[]) {
		try {
			int fileLength;
			if (exists) {
				seek(index, requestedFileId * 6);
				for (int offset = 0, numBytesRead; offset < 6; offset += numBytesRead) {
					numBytesRead = index.read(buffer, offset, 6 - offset);
					if (numBytesRead == -1) {
						return false;
					}
				}

				fileLength = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
				if (fileLength <= 0 || (long) fileLength > data.length() / 520L) {
					return false;
				}
			} else {
				fileLength = (int) ((data.length() + 519L) / 520L);
				if (fileLength == 0) {
					fileLength = 1;
				}
			}
			buffer[0] = (byte) (fileIndex >> 16);
			buffer[1] = (byte) (fileIndex >> 8);
			buffer[2] = (byte) fileIndex;
			buffer[3] = (byte) (fileLength >> 16);
			buffer[4] = (byte) (fileLength >> 8);
			buffer[5] = (byte) fileLength;
			seek(index, requestedFileId * 6);
			index.write(buffer, 0, 6);
			for (int index1 = 0, writeOffset = 0; writeOffset < fileIndex; index1++) {
				int dataFileIndex = 0;
				if (exists) {
					seek(data, fileLength * 520);
					int offset = 0;
					for (int numBytesRead = 0; offset < 8; offset += numBytesRead) {
						numBytesRead = data.read(buffer, offset, 8 - offset);
						if (numBytesRead == -1) {
							break;
						}
					}
					if (offset == 8) {
						int fileId = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
						int j3 = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
						dataFileIndex = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
						int version = buffer[7] & 0xff;
						if (fileId != requestedFileId || j3 != index1 || version != this.version) {
							return false;
						}
						if (dataFileIndex < 0 || (long) dataFileIndex > data.length() / 520L) {
							return false;
						}
					}
				}
				if (dataFileIndex == 0) {
					exists = false;
					dataFileIndex = (int) ((data.length() + 519L) / 520L);
					if (dataFileIndex == 0) {
						dataFileIndex++;
					}
					if (dataFileIndex == fileLength) {
						dataFileIndex++;
					}
				}
				if (fileIndex - writeOffset <= 512) {
					dataFileIndex = 0;
				}
				buffer[0] = (byte) (requestedFileId >> 8);
				buffer[1] = (byte) requestedFileId;
				buffer[2] = (byte) (index1 >> 8);
				buffer[3] = (byte) index1;
				buffer[4] = (byte) (dataFileIndex >> 16);
				buffer[5] = (byte) (dataFileIndex >> 8);
				buffer[6] = (byte) dataFileIndex;
				buffer[7] = (byte) version;
				seek(data, fileLength * 520);
				data.write(buffer, 0, 8);
				int dataLeft = fileIndex - writeOffset;
				if (dataLeft > 512) {
					dataLeft = 512;
				}
				data.write(fileData, writeOffset, dataLeft);
				writeOffset += dataLeft;
				fileLength = dataFileIndex;
			}

			return true;
		} catch (IOException _ex) {
			return false;
		}
	}

	/**
	 * Seeks the specified file, and ensures the position is within expected parameters.
	 * @param file The random access file to seek on. This is either the index or the data file.
	 * @param position The position to seek to.
	 * @throws IOException If the method was unable to seek on the RAF.
	 */
	private synchronized void seek(RandomAccessFile file, int position)
			throws IOException {
		if (position < 0 || position > 0x3c00000) {
			System.out.println("(Badseek) position:" + position + " length:" + file.length());
			position = 0x3c00000;
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		}
		file.seek(position);
	}

}
