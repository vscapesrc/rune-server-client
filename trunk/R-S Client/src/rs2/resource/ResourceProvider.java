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

	private boolean verify(int listVersion, int listCrc, byte data[]) {
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
				expectingData = true;
				for (int index = 0; index < 6; index += in.read(inputBuffer, index, 6 - index));
				int type = inputBuffer[0] & 0xff;
				int id = ((inputBuffer[1] & 0xff) << 8) + (inputBuffer[2] & 0xff);
				int length = ((inputBuffer[3] & 0xff) << 8) + (inputBuffer[4] & 0xff);
				int blockNum = inputBuffer[5] & 0xff;
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
				expectingData = true;
				byte data[] = inputBuffer;
				int read = 0;
				if (current != null) {
					data = current.data;
					read = completedSize;
				}
				for (int index = 0; index < expectedSize; index += in.read(data, index + read, expectedSize - index));
				if (expectedSize + completedSize >= data.length && current != null) {
					if (client.resourceCaches[0] != null) {
						client.resourceCaches[current.type + 1].put(data.length, data, current.id);
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
			priorities[type] = new byte[total];
			for (int id = 0; id < total; id++) {
				versions[type][id] = buffer.getUnsignedShort();
			}
		}
		String crc[] = { "model_crc", "anim_crc", "midi_crc", "map_crc" };
		for (int type = 0; type < 4; type++) {
			byte data[] = archive.getData(crc[type]);
			int total = data.length / 4;
			JagexBuffer buffer = new JagexBuffer(data);
			checksums[type] = new int[total];
			for (int id = 0; id < total; id++) {
				checksums[type][id] = buffer.getInt();
			}
		}
		byte data[] = archive.getData("model_index");
		int total = versions[0].length;
		modelIndices = new byte[total];
		for (int id = 0; id < total; id++) {
			if (id < data.length) {
				modelIndices[id] = data[id];
			} else {
				modelIndices[id] = 0;
			}
		}
		data = archive.getData("map_index");
		JagexBuffer buffer = new JagexBuffer(data);
		total = data.length / 7;
		mapRegionIds = new int[total];
		mapTerrains = new int[total];
		mapLandscapes = new int[total];
		regionPreload = new int[total];
		for (int index = 0; index < total; index++) {
			mapRegionIds[index] = buffer.getUnsignedShort();
			mapTerrains[index] = buffer.getUnsignedShort();
			mapLandscapes[index] = buffer.getUnsignedShort();
			regionPreload[index] = buffer.getUnsignedByte();
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

	public int getRemaining() {
		synchronized (remainingMandatory) {
			return remainingMandatory.getNodeCount();
		}
	}

	public void disable() {
		running = false;
	}

	public void method554(boolean members) {
		int total = mapRegionIds.length;
		for (int index = 0; index < total; index++) {
			if (members || regionPreload[index] != 0) {
				setExtraPriority((byte) 2, 3, mapLandscapes[index]);
				setExtraPriority((byte) 2, 3, mapTerrains[index]);
			}
		}
	}

	public int getCount(int type) {
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
			inputBuffer[0] = (byte) resource.type;
			inputBuffer[1] = (byte) (resource.id >> 8);
			inputBuffer[2] = (byte) resource.id;
			if (resource.incomplete) {
				inputBuffer[3] = 2;
			} else if (!client.loggedIn) {
				inputBuffer[3] = 1;
			} else {
				inputBuffer[3] = 0;
			}
			out.write(inputBuffer, 0, 4);
			writeLoopCycle = 0;
			errorCount = -10000;
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
		errorCount++;
	}

	public int getAnimCount() {
		return animIndices.length;
	}

	public void loadMandatory(int type, int id) {
		if (Constants.CHECK_VERSION_AND_CRC) {
			if (type < 0 || type > versions.length || id < 0 || id > versions[type].length) {
				return;
			}
			if (versions[type][id] == 0) {
				return;
			}
		}
		synchronized (remainingMandatory) {
			for (Resource resource = (Resource) remainingMandatory.reverseGetFirst(); resource != null; resource = (Resource) remainingMandatory.reverseGetNext()) {
				if (resource.type == type && resource.id == id) {
					return;
				}
			}
			Resource resource = new Resource();
			resource.type = type;
			resource.id = id;
			resource.incomplete = true;
			synchronized (mandatory) {
				mandatory.append(resource);
			}
			remainingMandatory.insertHead(resource);
		}
	}

	public int getModelFlag(int index) {
		return modelIndices[index] & 0xff;
	}

	public void run() {
		try {
			while (running) {
				resourceCycle++;
				int i = 20;
				if (maxPriority == 0 && client.resourceCaches[0] != null) {
					i = 50;
				}
				try {
					Thread.sleep(i);
				} catch (Exception _ex) {
				}
				expectingData = true;
				for (int j = 0; j < 100; j++) {
					if (!expectingData) {
						break;
					}
					expectingData = false;
					checkReceived();
					handleFailed();
					if (incompletedCount == 0 && j >= 5) {
						break;
					}
					loadExtras();
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
				if (client.loggedIn && socket != null && out != null && (maxPriority > 0 || client.resourceCaches[0] == null)) {
					writeLoopCycle++;
					if (writeLoopCycle > 500) {
						writeLoopCycle = 0;
						inputBuffer[0] = 0;
						inputBuffer[1] = 0;
						inputBuffer[2] = 0;
						inputBuffer[3] = 10;
						try {
							out.write(inputBuffer, 0, 4);
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
		if (client.resourceCaches[0] == null) {
			return;
		}
		if (Constants.CHECK_VERSION_AND_CRC) {
			if (versions[type][id] == 0) {
				return;
			}
		}
		if (priorities[type][id] == 0) {
			return;
		}
		if (maxPriority == 0) {
			return;
		}
		Resource resource = new Resource();
		resource.type = type;
		resource.id = id;
		resource.incomplete = false;
		synchronized (extras) {
			extras.append(resource);
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
		synchronized (remainingMandatory) {
			resource.unlinkSub();
		}
		if (resource.data == null) {
			return resource;
		}
		int i = 0;
		try {
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(resource.data));
			do {
				if (i == inflationBuffer.length) {
					throw new RuntimeException("buffer overflow!");
				}
				int k = in.read(inflationBuffer, i, inflationBuffer.length - i);
				if (k == -1) {
					break;
				}
				i += k;
			} while (true);
		} catch (IOException e) {
			throw new RuntimeException("error unzipping");
		}
		resource.data = new byte[i];
		System.arraycopy(inflationBuffer, 0, resource.data, 0, i);
		return resource;
	}

	public int method562(int i, int k, int l) {
		int i1 = (l << 8) + k;
		for (int index = 0; index < mapRegionIds.length; index++) {
			if (mapRegionIds[index] == i1) {
				if (i == 0) {
					return mapTerrains[index];
				} else {
					return mapLandscapes[index];
				}
			}
		}
		return -1;
	}

	public void loadModel(int index) {
		loadMandatory(0, index);
	}

	public void setExtraPriority(byte priority, int type, int id) {
		if (client.resourceCaches[0] == null) {
			return;
		}
		if (Constants.CHECK_VERSION_AND_CRC) {
			if (versions[type][id] == 0) {
				return;
			}
			byte data[] = client.resourceCaches[type + 1].get(id);
			if (verify(versions[type][id], checksums[type][id], data)) {
				return;
			}
		}
		priorities[type][id] = priority;
		if (priority > maxPriority) {
			maxPriority = priority;
		}
		totalFiles++;
	}

	public boolean method564(int i) {
		for (int index = 0; index < mapRegionIds.length; index++) {
			if (mapLandscapes[index] == i) {
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
			Resource resource = (Resource) toRequest.popFront();
			if (resource == null) {
				break;
			}
			if (priorities[resource.type][resource.id] != 0) {
				filesLoaded++;
			}
			priorities[resource.type][resource.id] = 0;
			requested.append(resource);
			incompletedCount++;
			closeRequest(resource);
			expectingData = true;
		}
	}

	public void ignoreExtras() {
		synchronized (extras) {
			extras.clear();
		}
	}

	private void checkReceived() {
		Resource resource;
		synchronized (mandatory) {
			resource = (Resource) mandatory.popFront();
		}
		while (resource != null) {
			expectingData = true;
			byte data[] = null;
			if (client.resourceCaches[0] != null) {
				data = client.resourceCaches[resource.type + 1].get(resource.id);
			}
			if (Constants.CHECK_VERSION_AND_CRC) {
				if (!verify(versions[resource.type][resource.id], checksums[resource.type][resource.id], data)) {
					data = null;
				}
			}
			synchronized (mandatory) {
				if (data == null) {
					toRequest.append(resource);
				} else {
					resource.data = data;
					synchronized (completed) {
						completed.append(resource);
					}
				}
				resource = (Resource) mandatory.popFront();
			}
		}
	}

	private void loadExtras() {
		while (incompletedCount == 0 && completedCount < 10) {
			if (maxPriority == 0) {
				break;
			}
			Resource resource;
			synchronized (extras) {
				resource = (Resource) extras.popFront();
			}
			while (resource != null) {
				if (priorities[resource.type][resource.id] != 0) {
					priorities[resource.type][resource.id] = 0;
					requested.append(resource);
					closeRequest(resource);
					expectingData = true;
					if (filesLoaded < totalFiles) {
						filesLoaded++;
					}
					statusString = "Loading extra files - " + (filesLoaded * 100) / totalFiles + "%";
					completedCount++;
					if (completedCount == 10) {
						return;
					}
				}
				synchronized (extras) {
					resource = (Resource) extras.popFront();
				}
			}
			for (int type = 0; type < 4; type++) {
				byte priority[] = priorities[type];
				int total = priority.length;
				for (int id = 0; id < total; id++) {
					if (priority[id] == maxPriority) {
						priority[id] = 0;
						Resource extra = new Resource();
						extra.type = type;
						extra.id = id;
						extra.incomplete = false;
						requested.append(extra);
						closeRequest(extra);
						expectingData = true;
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
			maxPriority--;
		}
	}

	public boolean isMidiPreloaded(int index) {
		return midiIndices[index] == 1;
	}

	public ResourceProvider() {
		toRequest = new Deque();
		requested = new Deque();
		mandatory = new Deque();
		completed = new Deque();
		extras = new Deque();
		remainingMandatory = new Queue();
		crc32 = new CRC32();
		priorities = new byte[4][];
		inputBuffer = new byte[500];
		inflationBuffer = new byte[65000];
		versions = new int[4][];
		checksums = new int[4][];
		running = true;
		expectingData = false;
		statusString = "";
	}

	private int totalFiles;
	private final Deque requested;
	private int maxPriority;
	public String statusString;
	private int writeLoopCycle;
	private long openSocketTime;
	private int[] mapLandscapes;
	private final CRC32 crc32;
	private final byte[] inputBuffer;
	public int resourceCycle;
	private final byte[][] priorities;
	private Client client;
	private final Deque extras;
	private int completedSize;
	private int expectedSize;
	private int[] midiIndices;
	public int errorCount;
	private int[] mapTerrains;
	private int filesLoaded;
	private boolean running;
	private OutputStream out;
	private int[] regionPreload;
	private boolean expectingData;
	private final Deque completed;
	private final byte[] inflationBuffer;
	private int[] animIndices;
	private final Queue remainingMandatory;
	private InputStream in;
	private Socket socket;
	private final int[][] versions;
	private final int[][] checksums;
	private int incompletedCount;
	private int completedCount;
	private final Deque toRequest;
	private Resource current;
	private final Deque mandatory;
	private int[] mapRegionIds;
	private byte[] modelIndices;
	private int connectionIdleTicks;
}
