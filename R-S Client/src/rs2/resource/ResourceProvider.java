package rs2.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;

import rs2.JagexBuffer;
import rs2.Deque;
import rs2.Queue;
import rs2.Client;
import rs2.cache.JagexArchive;
import rs2.sign.signlink;

public final class ResourceProvider extends ModelProvider implements Runnable {

	private boolean crcMatches(int i, int j, byte abyte0[]) {
		if (abyte0 == null || abyte0.length < 2)
			return false;
		int k = abyte0.length - 2;
		int l = ((abyte0[k] & 0xff) << 8) + (abyte0[k + 1] & 0xff);
		crc32.reset();
		crc32.update(abyte0, 0, k);
		int i1 = (int) crc32.getValue();
		return l == i && i1 == j;
	}

	private void handleResponse() {
		try {
			int available = in.available();
			if (expectedSize == 0 && available >= 6) {
				waiting = true;
				for (int index = 0; index < 6; index += in.read(ioBuffer, index, 6 - index));
				int type = ioBuffer[0] & 0xff;
				int id = ((ioBuffer[1] & 0xff) << 8) + (ioBuffer[2] & 0xff);
				int length = ((ioBuffer[3] & 0xff) << 8) + (ioBuffer[4] & 0xff);
				int blockNum = ioBuffer[5] & 0xff;
				current = null;
				for (Resource resource = (Resource) requested.head(); resource != null; resource = (Resource) requested.next()) {
					if (resource.type == type && resource.id == id) {
						current = resource;
					}
					if (current != null) {
						resource.requestAge = 0;
					}
				}
				if (current != null) {
					connectionIdleTicks = 0;
					if (length == 0) {
						signlink.reportError("Rej: " + type + "," + id);
						current.data = null;
						if (current.incomplete) {
							synchronized (completed) {
								completed.append(current);
							}
						} else {
							current.remove();
						}
						current = null;
					} else {
						if (current.data == null && blockNum == 0) {
							current.data = new byte[length];
						}
						if (current.data == null && blockNum != 0) {
							throw new IOException("missing start of file");
						}
					}
				}
				completedSize = blockNum * 500;
				expectedSize = 500;
				if (expectedSize > length - blockNum * 500)
					expectedSize = length - blockNum * 500;
			}
			if (expectedSize > 0 && available >= expectedSize) {
				waiting = true;
				byte data[] = ioBuffer;
				int read = 0;
				if (current != null) {
					data = current.data;
					read = completedSize;
				}
				for (int index = 0; index < expectedSize; index += in.read(data, index + read, expectedSize - index));
				if (expectedSize + completedSize >= data.length && current != null) {
					if (client.jagCache[0] != null) {
						client.jagCache[current.type + 1].put(data.length, data, current.id);
					}
					if (!current.incomplete && current.type == 3) {
						current.incomplete = true;
						current.type = 93;
					}
					if (current.incomplete) {
						synchronized (completed) {
							completed.append(current);
						}
					} else {
						current.remove();
					}
				}
				expectedSize = 0;
			}
		} catch (IOException ioexception) {
			try {
				socket.close();
			} catch (Exception _ex) {
			}
			socket = null;
			in = null;
			out = null;
			expectedSize = 0;
		}
	}

	public void start(JagexArchive streamLoader, Client client1) {
		String as[] = { "model_version", "anim_version", "midi_version", "map_version" };
		for (int i = 0; i < 4; i++) {
			byte abyte0[] = streamLoader.getData(as[i]);
			int j = abyte0.length / 2;
			JagexBuffer stream = new JagexBuffer(abyte0);
			versions[i] = new int[j];
			fileStatus[i] = new byte[j];
			for (int l = 0; l < j; l++)
				versions[i][l] = stream.getUnsignedShort();

		}

		String as1[] = { "model_crc", "anim_crc", "midi_crc", "map_crc" };
		for (int k = 0; k < 4; k++) {
			byte abyte1[] = streamLoader.getData(as1[k]);
			int i1 = abyte1.length / 4;
			JagexBuffer stream_1 = new JagexBuffer(abyte1);
			crcs[k] = new int[i1];
			for (int l1 = 0; l1 < i1; l1++)
				crcs[k][l1] = stream_1.getInt();

		}

		byte abyte2[] = streamLoader.getData("model_index");
		int j1 = versions[0].length;
		modelIndices = new byte[j1];
		for (int k1 = 0; k1 < j1; k1++)
			if (k1 < abyte2.length)
				modelIndices[k1] = abyte2[k1];
			else
				modelIndices[k1] = 0;

		abyte2 = streamLoader.getData("map_index");
		JagexBuffer stream2 = new JagexBuffer(abyte2);
		j1 = abyte2.length / 7;
		mapIndices1 = new int[j1];
		mapIndices2 = new int[j1];
		mapIndices3 = new int[j1];
		mapIndices4 = new int[j1];
		for (int i2 = 0; i2 < j1; i2++) {
			mapIndices1[i2] = stream2.getUnsignedShort();
			mapIndices2[i2] = stream2.getUnsignedShort();
			mapIndices3[i2] = stream2.getUnsignedShort();
			mapIndices4[i2] = stream2.getUnsignedByte();
		}

		abyte2 = streamLoader.getData("anim_index");
		stream2 = new JagexBuffer(abyte2);
		j1 = abyte2.length / 2;
		anIntArray1360 = new int[j1];
		for (int j2 = 0; j2 < j1; j2++)
			anIntArray1360[j2] = stream2.getUnsignedShort();

		abyte2 = streamLoader.getData("midi_index");
		stream2 = new JagexBuffer(abyte2);
		j1 = abyte2.length;
		anIntArray1348 = new int[j1];
		for (int k2 = 0; k2 < j1; k2++)
			anIntArray1348[k2] = stream2.getUnsignedByte();

		client = client1;
		running = true;
		client.startRunnable(this, 2);
	}

	public int getNodeCount() {
		synchronized (nodeSubList) {
			return nodeSubList.getNodeCount();
		}
	}

	public void disable() {
		running = false;
	}

	public void method554(boolean flag) {
		int j = mapIndices1.length;
		for (int k = 0; k < j; k++)
			if (flag || mapIndices4[k] != 0) {
				method563((byte) 2, 3, mapIndices3[k]);
				method563((byte) 2, 3, mapIndices2[k]);
			}

	}

	public int getVersionCount(int j) {
		return versions[j].length;
	}

	private void closeRequest(Resource onDemandData) {
		try {
			if (socket == null) {
				long l = System.currentTimeMillis();
				if (l - openSocketTime < 4000L)
					return;
				openSocketTime = l;
				socket = client.openSocket(43594 + Client.portOff);
				in = socket.getInputStream();
				out = socket.getOutputStream();
				out.write(15);
				for (int j = 0; j < 8; j++)
					in.read();

				connectionIdleTicks = 0;
			}
			ioBuffer[0] = (byte) onDemandData.type;
			ioBuffer[1] = (byte) (onDemandData.id >> 8);
			ioBuffer[2] = (byte) onDemandData.id;
			if (onDemandData.incomplete)
				ioBuffer[3] = 2;
			else if (!client.loggedIn)
				ioBuffer[3] = 1;
			else
				ioBuffer[3] = 0;
			out.write(ioBuffer, 0, 4);
			writeLoopCycle = 0;
			anInt1349 = -10000;
			return;
		} catch (IOException ioexception) {
		}
		try {
			socket.close();
		} catch (Exception _ex) {
		}
		socket = null;
		in = null;
		out = null;
		expectedSize = 0;
		anInt1349++;
	}

	public int getAnimCount() {
		return anIntArray1360.length;
	}

	public void method558(int i, int j) {
		if (i < 0 || i > versions.length || j < 0 || j > versions[i].length)
			return;
		if (versions[i][j] == 0)
			return;
		synchronized (nodeSubList) {
			for (Resource onDemandData = (Resource) nodeSubList
					.reverseGetFirst(); onDemandData != null; onDemandData = (Resource) nodeSubList
					.reverseGetNext())
				if (onDemandData.type == i && onDemandData.id == j)
					return;

			Resource onDemandData_1 = new Resource();
			onDemandData_1.type = i;
			onDemandData_1.id = j;
			onDemandData_1.incomplete = true;
			synchronized (aClass19_1370) {
				aClass19_1370.append(onDemandData_1);
			}
			nodeSubList.insertHead(onDemandData_1);
		}
	}

	public int getModelIndex(int i) {
		return modelIndices[i] & 0xff;
	}

	public void run() {
		try {
			while (running) {
				onDemandCycle++;
				int i = 20;
				if (anInt1332 == 0 && client.jagCache[0] != null)
					i = 50;
				try {
					Thread.sleep(i);
				} catch (Exception _ex) {
				}
				waiting = true;
				for (int j = 0; j < 100; j++) {
					if (!waiting)
						break;
					waiting = false;
					checkReceived();
					handleFailed();
					if (uncompletedCount == 0 && j >= 5)
						break;
					method568();
					if (in != null)
						handleResponse();
				}

				boolean flag = false;
				for (Resource onDemandData = (Resource) requested
						.head(); onDemandData != null; onDemandData = (Resource) requested
						.next())
					if (onDemandData.incomplete) {
						flag = true;
						onDemandData.requestAge++;
						if (onDemandData.requestAge > 50) {
							onDemandData.requestAge = 0;
							closeRequest(onDemandData);
						}
					}

				if (!flag) {
					for (Resource onDemandData_1 = (Resource) requested
							.head(); onDemandData_1 != null; onDemandData_1 = (Resource) requested
							.next()) {
						flag = true;
						onDemandData_1.requestAge++;
						if (onDemandData_1.requestAge > 50) {
							onDemandData_1.requestAge = 0;
							closeRequest(onDemandData_1);
						}
					}

				}
				if (flag) {
					connectionIdleTicks++;
					if (connectionIdleTicks > 750) {
						try {
							socket.close();
						} catch (Exception _ex) {
						}
						socket = null;
						in = null;
						out = null;
						expectedSize = 0;
					}
				} else {
					connectionIdleTicks = 0;
					statusString = "";
				}
				if (client.loggedIn
						&& socket != null
						&& out != null
						&& (anInt1332 > 0 || client.jagCache[0] == null)) {
					writeLoopCycle++;
					if (writeLoopCycle > 500) {
						writeLoopCycle = 0;
						ioBuffer[0] = 0;
						ioBuffer[1] = 0;
						ioBuffer[2] = 0;
						ioBuffer[3] = 10;
						try {
							out.write(ioBuffer, 0, 4);
						} catch (IOException _ex) {
							connectionIdleTicks = 5000;
						}
					}
				}
			}
		} catch (Exception exception) {
			signlink.reportError("od_ex " + exception.getMessage());
		}
	}

	public void method560(int i, int j) {
		if (client.jagCache[0] == null)
			return;
		if (versions[j][i] == 0)
			return;
		if (fileStatus[j][i] == 0)
			return;
		if (anInt1332 == 0)
			return;
		Resource onDemandData = new Resource();
		onDemandData.type = j;
		onDemandData.id = i;
		onDemandData.incomplete = false;
		synchronized (aClass19_1344) {
			aClass19_1344.append(onDemandData);
		}
	}

	public Resource getNextNode() {
		Resource onDemandData;
		synchronized (completed) {
			onDemandData = (Resource) completed.popFront();
		}
		if (onDemandData == null)
			return null;
		synchronized (nodeSubList) {
			onDemandData.unlinkSub();
		}
		if (onDemandData.data == null)
			return onDemandData;
		int i = 0;
		try {
			GZIPInputStream gzipinputstream = new GZIPInputStream(
					new ByteArrayInputStream(onDemandData.data));
			do {
				if (i == gzipInputBuffer.length)
					throw new RuntimeException("buffer overflow!");
				int k = gzipinputstream.read(gzipInputBuffer, i,
						gzipInputBuffer.length - i);
				if (k == -1)
					break;
				i += k;
			} while (true);
		} catch (IOException _ex) {
			throw new RuntimeException("error unzipping");
		}
		onDemandData.data = new byte[i];
		System.arraycopy(gzipInputBuffer, 0, onDemandData.data, 0, i);

		return onDemandData;
	}

	public int method562(int i, int k, int l) {
		int i1 = (l << 8) + k;
		for (int j1 = 0; j1 < mapIndices1.length; j1++)
			if (mapIndices1[j1] == i1)
				if (i == 0)
					return mapIndices2[j1];
				else
					return mapIndices3[j1];
		return -1;
	}

	public void method548(int i) {
		method558(0, i);
	}

	public void method563(byte byte0, int i, int j) {
		if (client.jagCache[0] == null)
			return;
		if (versions[i][j] == 0)
			return;
		byte abyte0[] = client.jagCache[i + 1].get(j);
		if (crcMatches(versions[i][j], crcs[i][j], abyte0))
			return;
		fileStatus[i][j] = byte0;
		if (byte0 > anInt1332)
			anInt1332 = byte0;
		totalFiles++;
	}

	public boolean method564(int i) {
		for (int k = 0; k < mapIndices1.length; k++)
			if (mapIndices3[k] == i)
				return true;
		return false;
	}

	private void handleFailed() {
		uncompletedCount = 0;
		completedCount = 0;
		for (Resource onDemandData = (Resource) requested
				.head(); onDemandData != null; onDemandData = (Resource) requested
				.next())
			if (onDemandData.incomplete)
				uncompletedCount++;
			else
				completedCount++;

		while (uncompletedCount < 10) {
			Resource onDemandData_1 = (Resource) aClass19_1368
					.popFront();
			if (onDemandData_1 == null)
				break;
			if (fileStatus[onDemandData_1.type][onDemandData_1.id] != 0)
				filesLoaded++;
			fileStatus[onDemandData_1.type][onDemandData_1.id] = 0;
			requested.append(onDemandData_1);
			uncompletedCount++;
			closeRequest(onDemandData_1);
			waiting = true;
		}
	}

	public void method566() {
		synchronized (aClass19_1344) {
			aClass19_1344.removeAll();
		}
	}

	private void checkReceived() {
		Resource onDemandData;
		synchronized (aClass19_1370) {
			onDemandData = (Resource) aClass19_1370.popFront();
		}
		while (onDemandData != null) {
			waiting = true;
			byte abyte0[] = null;
			if (client.jagCache[0] != null)
				abyte0 = client.jagCache[onDemandData.type + 1]
						.get(onDemandData.id);
			if (!crcMatches(versions[onDemandData.type][onDemandData.id],
					crcs[onDemandData.type][onDemandData.id], abyte0))
				abyte0 = null;
			synchronized (aClass19_1370) {
				if (abyte0 == null) {
					aClass19_1368.append(onDemandData);
				} else {
					onDemandData.data = abyte0;
					synchronized (completed) {
						completed.append(onDemandData);
					}
				}
				onDemandData = (Resource) aClass19_1370.popFront();
			}
		}
	}

	private void method568() {
		while (uncompletedCount == 0 && completedCount < 10) {
			if (anInt1332 == 0)
				break;
			Resource onDemandData;
			synchronized (aClass19_1344) {
				onDemandData = (Resource) aClass19_1344.popFront();
			}
			while (onDemandData != null) {
				if (fileStatus[onDemandData.type][onDemandData.id] != 0) {
					fileStatus[onDemandData.type][onDemandData.id] = 0;
					requested.append(onDemandData);
					closeRequest(onDemandData);
					waiting = true;
					if (filesLoaded < totalFiles)
						filesLoaded++;
					statusString = "Loading extra files - "
							+ (filesLoaded * 100) / totalFiles + "%";
					completedCount++;
					if (completedCount == 10)
						return;
				}
				synchronized (aClass19_1344) {
					onDemandData = (Resource) aClass19_1344.popFront();
				}
			}
			for (int j = 0; j < 4; j++) {
				byte abyte0[] = fileStatus[j];
				int k = abyte0.length;
				for (int l = 0; l < k; l++)
					if (abyte0[l] == anInt1332) {
						abyte0[l] = 0;
						Resource onDemandData_1 = new Resource();
						onDemandData_1.type = j;
						onDemandData_1.id = l;
						onDemandData_1.incomplete = false;
						requested.append(onDemandData_1);
						closeRequest(onDemandData_1);
						waiting = true;
						if (filesLoaded < totalFiles)
							filesLoaded++;
						statusString = "Loading extra files - "
								+ (filesLoaded * 100) / totalFiles + "%";
						completedCount++;
						if (completedCount == 10)
							return;
					}

			}

			anInt1332--;
		}
	}

	public boolean method569(int i) {
		return anIntArray1348[i] == 1;
	}

	public ResourceProvider() {
		requested = new Deque();
		statusString = "";
		crc32 = new CRC32();
		ioBuffer = new byte[500];
		fileStatus = new byte[4][];
		aClass19_1344 = new Deque();
		running = true;
		waiting = false;
		completed = new Deque();
		gzipInputBuffer = new byte[65000];
		nodeSubList = new Queue();
		versions = new int[4][];
		crcs = new int[4][];
		aClass19_1368 = new Deque();
		aClass19_1370 = new Deque();
	}

	private int totalFiles;
	private final Deque requested;
	private int anInt1332;
	public String statusString;
	private int writeLoopCycle;
	private long openSocketTime;
	private int[] mapIndices3;
	private final CRC32 crc32;
	private final byte[] ioBuffer;
	public int onDemandCycle;
	private final byte[][] fileStatus;
	private Client client;
	private final Deque aClass19_1344;
	private int completedSize;
	private int expectedSize;
	private int[] anIntArray1348;
	public int anInt1349;
	private int[] mapIndices2;
	private int filesLoaded;
	private boolean running;
	private OutputStream out;
	private int[] mapIndices4;
	private boolean waiting;
	private final Deque completed;
	private final byte[] gzipInputBuffer;
	private int[] anIntArray1360;
	private final Queue nodeSubList;
	private InputStream in;
	private Socket socket;
	private final int[][] versions;
	private final int[][] crcs;
	private int uncompletedCount;
	private int completedCount;
	private final Deque aClass19_1368;
	private Resource current;
	private final Deque aClass19_1370;
	private int[] mapIndices1;
	private byte[] modelIndices;
	private int connectionIdleTicks;
}
