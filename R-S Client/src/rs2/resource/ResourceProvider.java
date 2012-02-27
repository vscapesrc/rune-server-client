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
import rs2.constants.Constants;
import rs2.sign.signlink;

public final class ResourceProvider extends ModelProvider implements Runnable {

	private boolean crcMatches(int listVersion, int listCrc, byte data[]) {
		if (data == null || data.length < 2) {
			return false;
		}
		int length = data.length - 2;
		int version = ((data[length] & 0xff) << 8) + (data[length + 1] & 0xff);
		crc32.reset();
		crc32.update(data, 0, length);
		int crc = (int) crc32.getValue();
		return version == listVersion && crc == listCrc;
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

	public void start(JagexArchive archive, Client c) {
		String ver[] = { "model_version", "anim_version", "midi_version", "map_version" };
		for (int type = 0; type < 4; type++) {
			byte data[] = archive.getData(ver[type]);
			int total = data.length / 2;
			JagexBuffer buffer = new JagexBuffer(data);
			versions[type] = new int[total];
			fileStatus[type] = new byte[total];
			for (int id = 0; id < total; id++) {
				versions[type][id] = buffer.getUnsignedShort();
			}
		}
		String crc[] = { "model_crc", "anim_crc", "midi_crc", "map_crc" };
		for (int type = 0; type < 4; type++) {
			byte data[] = archive.getData(crc[type]);
			int total = data.length / 4;
			JagexBuffer buffer = new JagexBuffer(data);
			crcs[type] = new int[total];
			for (int id = 0; id < total; id++) {
				crcs[type][id] = buffer.getInt();
			}
		}
		byte data[] = archive.getData("model_index");
		int total = versions[0].length;
		modelFlags = new byte[total];
		for (int id = 0; id < total; id++) {
			if (id < data.length) {
				modelFlags[id] = data[id];
			} else {
				modelFlags[id] = 0;
			}
		}
		data = archive.getData("map_index");
		JagexBuffer buffer = new JagexBuffer(data);
		total = data.length / 7;
		mapIndices1 = new int[total];
		mapIndices2 = new int[total];
		mapIndices3 = new int[total];
		mapIndices4 = new int[total];
		for (int index = 0; index < total; index++) {
			mapIndices1[index] = buffer.getUnsignedShort();
			mapIndices2[index] = buffer.getUnsignedShort();
			mapIndices3[index] = buffer.getUnsignedShort();
			mapIndices4[index] = buffer.getUnsignedByte();
		}
		data = archive.getData("anim_index");
		buffer = new JagexBuffer(data);
		total = data.length / 2;
		animIndices = new int[total];
		for (int index = 0; index < total; index++) {
			animIndices[index] = buffer.getUnsignedShort();
		}
		data = archive.getData("midi_index");
		buffer = new JagexBuffer(data);
		total = data.length;
		midiIndices = new int[total];
		for (int index = 0; index < total; index++) {
			midiIndices[index] = buffer.getUnsignedByte();
		}
		client = c;
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
		int total = mapIndices1.length;
		for (int index = 0; index < total; index++) {
			if (flag || mapIndices4[index] != 0) {
				method563((byte) 2, 3, mapIndices3[index]);
				method563((byte) 2, 3, mapIndices2[index]);
			}
		}
	}

	public int getVersionCount(int type) {
		return versions[type].length;
	}

	private void closeRequest(Resource resource) {
		try {
			if (socket == null) {
				long l = System.currentTimeMillis();
				if (l - openSocketTime < 4000L) {
					return;
				}
				openSocketTime = l;
				socket = client.openSocket(43594 + Client.portOff);
				in = socket.getInputStream();
				out = socket.getOutputStream();
				out.write(15);
				for (int index = 0; index < 8; index++) {
					in.read();
				}
				connectionIdleTicks = 0;
			}
			ioBuffer[0] = (byte) resource.type;
			ioBuffer[1] = (byte) (resource.id >> 8);
			ioBuffer[2] = (byte) resource.id;
			if (resource.incomplete) {
				ioBuffer[3] = 2;
			} else if (!client.loggedIn) {
				ioBuffer[3] = 1;
			} else {
				ioBuffer[3] = 0;
			}
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
		return animIndices.length;
	}

	public void method558(int type, int id) {
		if (Constants.CHECK_VERSION_AND_CRC) {
			if (type < 0 || type > versions.length || id < 0 || id > versions[type].length) {
				return;
			}
			if (versions[type][id] == 0) {
				return;
			}
		}
		synchronized (nodeSubList) {
			for (Resource resource = (Resource) nodeSubList.reverseGetFirst(); resource != null; resource = (Resource) nodeSubList.reverseGetNext()) {
				if (resource.type == type && resource.id == id) {
					return;
				}
			}
			Resource resource = new Resource();
			resource.type = type;
			resource.id = id;
			resource.incomplete = true;
			synchronized (aClass19_1370) {
				aClass19_1370.append(resource);
			}
			nodeSubList.insertHead(resource);
		}
	}

	public int getModelFlag(int index) {
		return modelFlags[index] & 0xff;
	}

	public void run() {
		try {
			while (running) {
				resourceCycle++;
				int i = 20;
				if (anInt1332 == 0 && client.jagCache[0] != null) {
					i = 50;
				}
				try {
					Thread.sleep(i);
				} catch (Exception _ex) {
				}
				waiting = true;
				for (int j = 0; j < 100; j++) {
					if (!waiting) {
						break;
					}
					waiting = false;
					checkReceived();
					handleFailed();
					if (incompletedCount == 0 && j >= 5) {
						break;
					}
					method568();
					if (in != null) {
						handleResponse();
					}
				}
				boolean flag = false;
				for (Resource resource = (Resource) requested.head(); resource != null; resource = (Resource) requested.next()) {
					if (resource.incomplete) {
						flag = true;
						resource.requestAge++;
						if (resource.requestAge > 50) {
							resource.requestAge = 0;
							closeRequest(resource);
						}
					}
				}
				if (!flag) {
					for (Resource resource = (Resource) requested.head(); resource != null; resource = (Resource) requested.next()) {
						flag = true;
						resource.requestAge++;
						if (resource.requestAge > 50) {
							resource.requestAge = 0;
							closeRequest(resource);
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
				if (client.loggedIn && socket != null && out != null && (anInt1332 > 0 || client.jagCache[0] == null)) {
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

	public void method560(int id, int type) {
		if (client.jagCache[0] == null) {
			return;
		}
		if (Constants.CHECK_VERSION_AND_CRC) {
			if (versions[type][id] == 0) {
				return;
			}
		}
		if (fileStatus[type][id] == 0) {
			return;
		}
		if (anInt1332 == 0) {
			return;
		}
		Resource resource = new Resource();
		resource.type = type;
		resource.id = id;
		resource.incomplete = false;
		synchronized (aClass19_1344) {
			aClass19_1344.append(resource);
		}
	}

	public Resource getNextNode() {
		Resource resource;
		synchronized (completed) {
			resource = (Resource) completed.popFront();
		}
		if (resource == null) {
			return null;
		}
		synchronized (nodeSubList) {
			resource.unlinkSub();
		}
		if (resource.data == null) {
			return resource;
		}
		int i = 0;
		try {
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(resource.data));
			do {
				if (i == gzipInputBuffer.length) {
					throw new RuntimeException("buffer overflow!");
				}
				int k = in.read(gzipInputBuffer, i, gzipInputBuffer.length - i);
				if (k == -1) {
					break;
				}
				i += k;
			} while (true);
		} catch (IOException e) {
			throw new RuntimeException("error unzipping");
		}
		resource.data = new byte[i];
		System.arraycopy(gzipInputBuffer, 0, resource.data, 0, i);
		return resource;
	}

	public int method562(int i, int k, int l) {
		int i1 = (l << 8) + k;
		for (int index = 0; index < mapIndices1.length; index++) {
			if (mapIndices1[index] == i1) {
				if (i == 0) {
					return mapIndices2[index];
				} else {
					return mapIndices3[index];
				}
			}
		}
		return -1;
	}

	public void method548(int i) {
		method558(0, i);
	}

	public void method563(byte status, int type, int id) {
		if (client.jagCache[0] == null) {
			return;
		}
		if (Constants.CHECK_VERSION_AND_CRC) {
			if (versions[type][id] == 0) {
				return;
			}
			byte data[] = client.jagCache[type + 1].get(id);
			if (crcMatches(versions[type][id], crcs[type][id], data)) {
				return;
			}
		}
		fileStatus[type][id] = status;
		if (status > anInt1332) {
			anInt1332 = status;
		}
		totalFiles++;
	}

	public boolean method564(int i) {
		for (int index = 0; index < mapIndices1.length; index++) {
			if (mapIndices3[index] == i) {
				return true;
			}
		}
		return false;
	}

	private void handleFailed() {
		incompletedCount = 0;
		completedCount = 0;
		for (Resource resource = (Resource) requested.head(); resource != null; resource = (Resource) requested.next()) {
			if (resource.incomplete) {
				incompletedCount++;
			} else {
				completedCount++;
			}
		}
		while (incompletedCount < 10) {
			Resource resource = (Resource) aClass19_1368.popFront();
			if (resource == null) {
				break;
			}
			if (fileStatus[resource.type][resource.id] != 0) {
				filesLoaded++;
			}
			fileStatus[resource.type][resource.id] = 0;
			requested.append(resource);
			incompletedCount++;
			closeRequest(resource);
			waiting = true;
		}
	}

	public void method566() {
		synchronized (aClass19_1344) {
			aClass19_1344.removeAll();
		}
	}

	private void checkReceived() {
		Resource resource;
		synchronized (aClass19_1370) {
			resource = (Resource) aClass19_1370.popFront();
		}
		while (resource != null) {
			waiting = true;
			byte data[] = null;
			if (client.jagCache[0] != null) {
				data = client.jagCache[resource.type + 1].get(resource.id);
			}
			if (Constants.CHECK_VERSION_AND_CRC) {
				if (!crcMatches(versions[resource.type][resource.id], crcs[resource.type][resource.id], data)) {
					data = null;
				}
			}
			synchronized (aClass19_1370) {
				if (data == null) {
					aClass19_1368.append(resource);
				} else {
					resource.data = data;
					synchronized (completed) {
						completed.append(resource);
					}
				}
				resource = (Resource) aClass19_1370.popFront();
			}
		}
	}

	private void method568() {
		while (incompletedCount == 0 && completedCount < 10) {
			if (anInt1332 == 0) {
				break;
			}
			Resource resource;
			synchronized (aClass19_1344) {
				resource = (Resource) aClass19_1344.popFront();
			}
			while (resource != null) {
				if (fileStatus[resource.type][resource.id] != 0) {
					fileStatus[resource.type][resource.id] = 0;
					requested.append(resource);
					closeRequest(resource);
					waiting = true;
					if (filesLoaded < totalFiles) {
						filesLoaded++;
					}
					statusString = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
					completedCount++;
					if (completedCount == 10) {
						return;
					}
				}
				synchronized (aClass19_1344) {
					resource = (Resource) aClass19_1344.popFront();
				}
			}
			for (int type = 0; type < 4; type++) {
				byte status[] = fileStatus[type];
				int total = status.length;
				for (int id = 0; id < total; id++) {
					if (status[id] == anInt1332) {
						status[id] = 0;
						Resource extra = new Resource();
						extra.type = type;
						extra.id = id;
						extra.incomplete = false;
						requested.append(extra);
						closeRequest(extra);
						waiting = true;
						if (filesLoaded < totalFiles) {
							filesLoaded++;
						}
						statusString = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
						completedCount++;
						if (completedCount == 10) {
							return;
						}
					}
				}
			}
			anInt1332--;
		}
	}

	public boolean method569(int i) {
		return midiIndices[i] == 1;
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
	public int resourceCycle;
	private final byte[][] fileStatus;
	private Client client;
	private final Deque aClass19_1344;
	private int completedSize;
	private int expectedSize;
	private int[] midiIndices;
	public int anInt1349;
	private int[] mapIndices2;
	private int filesLoaded;
	private boolean running;
	private OutputStream out;
	private int[] mapIndices4;
	private boolean waiting;
	private final Deque completed;
	private final byte[] gzipInputBuffer;
	private int[] animIndices;
	private final Queue nodeSubList;
	private InputStream in;
	private Socket socket;
	private final int[][] versions;
	private final int[][] crcs;
	private int incompletedCount;
	private int completedCount;
	private final Deque aClass19_1368;
	private Resource current;
	private final Deque aClass19_1370;
	private int[] mapIndices1;
	private byte[] modelFlags;
	private int connectionIdleTicks;
}
