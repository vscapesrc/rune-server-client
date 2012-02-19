package rs2.config;

import rs2.FrameReader;
import rs2.MemCache;
import rs2.Model;
import rs2.JagexBuffer;
import rs2.Client;
import rs2.cache.JagexArchive;
import rs2.resource.ResourceProvider;

public final class ObjectDefinitions {

	public static ObjectDefinitions getDefinition(int i) {
		for (int index = 0; index < 20; index++) {
			if (definitions[index].id == i) {
				return definitions[index];
			}
		}
		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDefinitions def = definitions[cacheIndex];
		dataBuffer.offset = streamIndices[i];
		def.id = i;
		def.setDefaults();
		def.readValues(dataBuffer);
		return def;
	}

	private void setDefaults() {
		modelArray = null;
		anIntArray776 = null;
		name = null;
		description = null;
		oldColors = null;
		newColors = null;
		tileSizeX = 1;
		tileSizeY = 1;
		unwalkable = true;
		aBoolean757 = true;
		hasActions = false;
		conforms = false;
		aBoolean769 = false;
		aBoolean764 = false;
		animationId = -1;
		anInt775 = 16;
		modelBrightness = 0;
		modelShadowing = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		modelScaleX = 128;
		modelScaleY = 128;
		modelScaleZ = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		varBitChild = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	public void method574(ResourceProvider resource) {
		if (modelArray == null) {
			return;
		}
		for (int model = 0; model < modelArray.length; model++) {
			resource.method560(modelArray[model] & 0xffff, 0);
		}
	}

	public static void nullLoader() {
		memCache1 = null;
		memCache2 = null;
		streamIndices = null;
		definitions = null;
		dataBuffer = null;
	}

	public static void unpackConfig(JagexArchive archive) {
		dataBuffer = new JagexBuffer(archive.getData("loc.dat"));
		JagexBuffer indexBuffer = new JagexBuffer(archive.getData("loc.idx"));
		totalObjects = indexBuffer.getUnsignedShort();
		streamIndices = new int[totalObjects];
		int offset = 2;
		for (int index = 0; index < totalObjects; index++) {
			streamIndices[index] = offset;
			offset += indexBuffer.getUnsignedShort();
		}
		definitions = new ObjectDefinitions[20];
		for (int index = 0; index < 20; index++) {
			definitions[index] = new ObjectDefinitions();
		}
	}

	public boolean method577(int i) {
		if (anIntArray776 == null) {
			if (modelArray == null) {
				return true;
			}
			if (i != 10) {
				return true;
			}
			boolean flag1 = true;
			for (int model = 0; model < modelArray.length; model++) {
				flag1 &= Model.method463(modelArray[model] & 0xffff);
			}
			return flag1;
		}
		for (int model = 0; model < anIntArray776.length; model++) {
			if (anIntArray776[model] == i) {
				return Model.method463(modelArray[model] & 0xffff);
			}
		}
		return true;
	}

	public Model renderObject(int i, int j, int k, int l, int i1, int j1, int k1) {
		Model model = method581(i, k1, j);
		if (model == null) {
			return null;
		}
		if (conforms || aBoolean769) {
			model = new Model(conforms, aBoolean769, model);
		}
		if (conforms) {
			int l1 = (k + l + i1 + j1) / 4;
			for (int i2 = 0; i2 < model.totalVertices; i2++) {
				int j2 = model.vertexX[i2];
				int k2 = model.vertexZ[i2];
				int l2 = k + ((l - k) * (j2 + 64)) / 128;
				int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
				model.vertexY[i2] += j3 - l1;
			}
			model.method467();
		}
		return model;
	}

	public boolean method579() {
		if (modelArray == null) {
			return true;
		}
		boolean flag1 = true;
		for (int model = 0; model < modelArray.length; model++) {
			flag1 &= Model.method463(modelArray[model] & 0xffff);
		}
		return flag1;
	}

	public ObjectDefinitions getChildDefinition() {
		int child = -1;
		if (varBitChild != -1) {
			VarBit varBit = VarBit.cache[varBitChild];
			int j = varBit.anInt648;
			int k = varBit.anInt649;
			int l = varBit.anInt650;
			int i1 = Client.anIntArray1232[l - k];
			child = client.variousSettings[j] >> k & i1;
		} else if (anInt749 != -1)
			child = client.variousSettings[anInt749];
		if (child < 0 || child >= childrenIDs.length || childrenIDs[child] == -1) {
			return null;
		} else {
			return getDefinition(childrenIDs[child]);
		}
	}

	private Model method581(int j, int k, int l) {
		Model model = null;
		long l1;
		if (anIntArray776 == null) {
			if (j != 10) {
				return null;
			}
			l1 = (long) ((id << 6) + l) + ((long) (k + 1) << 32);
			Model model_1 = (Model) memCache2.get(l1);
			if (model_1 != null) {
				return model_1;
			}
			if (modelArray == null) {
				return null;
			}
			boolean flag1 = aBoolean751 ^ (l > 3);
			int k1 = modelArray.length;
			for (int index = 0; index < k1; index++) {
				int modelId = modelArray[index];
				if (flag1) {
					modelId += 0x10000;
				}
				model = (Model) memCache1.get(modelId);
				if (model == null) {
					model = Model.method462(modelId & 0xffff);
					if (model == null) {
						return null;
					}
					if (flag1) {
						model.method477();
					}
					memCache1.put(model, modelId);
				}
				if (k1 > 1) {
					models[index] = model;
				}
			}
			if (k1 > 1) {
				model = new Model(k1, models);
			}
		} else {
			int modelIndex = -1;
			for (int index = 0; index < anIntArray776.length; index++) {
				if (anIntArray776[index] != j) {
					continue;
				}
				modelIndex = index;
				break;
			}
			if (modelIndex == -1) {
				return null;
			}
			l1 = (long) ((id << 6) + (modelIndex << 3) + l) + ((long) (k + 1) << 32);
			Model model_2 = (Model) memCache2.get(l1);
			if (model_2 != null) {
				return model_2;
			}
			int modelId = modelArray[modelIndex];
			boolean flag3 = aBoolean751 ^ (l > 3);
			if (flag3) {
				modelId += 0x10000;
			}
			model = (Model) memCache1.get(modelId);
			if (model == null) {
				model = Model.method462(modelId & 0xffff);
				if (model == null) {
					return null;
				}
				if (flag3) {
					model.method477();
				}
				memCache1.put(model, modelId);
			}
		}
		boolean rescaled = modelScaleX != 128 || modelScaleY != 128 || modelScaleZ != 128;
		boolean visible = anInt738 != 0 || anInt745 != 0 || anInt783 != 0;
		Model model_3 = new Model(oldColors == null, FrameReader.method532(k), l == 0 && k == -1 && !rescaled && !visible, model);
		if (k != -1) {
			model_3.method469();
			model_3.method470(k);
			model_3.anIntArrayArray1658 = null;
			model_3.anIntArrayArray1657 = null;
		}
		while (l-- > 0) {
			model_3.method473();
		}
		if (oldColors != null) {
			for (int color = 0; color < oldColors.length; color++) {
				model_3.changeModelColors(oldColors[color], newColors[color]);
			}
		}
		if (rescaled) {
			model_3.scaleModel(modelScaleX, modelScaleY, modelScaleZ);
		}
		if (visible) {
			model_3.method475(anInt738, anInt745, anInt783);
		}
		model_3.method479(64 + modelBrightness, 768 + modelShadowing * 5, -50, -10, -50, !aBoolean769);
		if (anInt760 == 1) {
			model_3.anInt1654 = model_3.modelHeight;
		}
		memCache2.put(model_3, l1);
		return model_3;
	}

	private void readValues(JagexBuffer buffer) {
		int i = -1;
		start: do {
			int opcode;
			do {
				opcode = buffer.getUnsignedByte();
				if (opcode == 0) {
					break start;
				}
				if (opcode == 1) {
					int totalModels = buffer.getUnsignedByte();
					if (totalModels > 0)
						if (modelArray == null || lowMem) {
							anIntArray776 = new int[totalModels];
							modelArray = new int[totalModels];
							for (int index = 0; index < totalModels; index++) {
								modelArray[index] = buffer.getUnsignedShort();
								anIntArray776[index] = buffer.getUnsignedByte();
							}
						} else {
							buffer.offset += totalModels * 3;
						}
				} else if (opcode == 2) {
					name = buffer.getString();
				} else if (opcode == 3) {
					description = buffer.getBytes();
				} else if (opcode == 5) {
					int totalModels = buffer.getUnsignedByte();
					if (totalModels > 0)
						if (modelArray == null || lowMem) {
							anIntArray776 = null;
							modelArray = new int[totalModels];
							for (int index = 0; index < totalModels; index++) {
								modelArray[index] = buffer.getUnsignedShort();
							}
						} else {
							buffer.offset += totalModels * 2;
						}
				} else if (opcode == 14) {
					tileSizeX = buffer.getUnsignedByte();
				} else if (opcode == 15) {
					tileSizeY = buffer.getUnsignedByte();
				} else if (opcode == 17) {
					unwalkable = false;
				} else if (opcode == 18) {
					aBoolean757 = false;
				} else if (opcode == 19) {
					hasActions = buffer.getUnsignedByte() == 1;
				} else if (opcode == 21) {
					conforms = true;
				} else if (opcode == 22) {
					aBoolean769 = true;
				} else if (opcode == 23) {
					aBoolean764 = true;
				} else if (opcode == 24) {
					animationId = buffer.getUnsignedShort();
					if (animationId == 65535) {
						animationId = -1;
					}
				} else if (opcode == 28) {
					anInt775 = buffer.getUnsignedByte();
				} else if (opcode == 29) {
					modelBrightness = buffer.getSignedByte();
				} else if (opcode == 39) {
					modelShadowing = buffer.getSignedByte();
				} else if (opcode >= 30 && opcode < 39) {
					if (actions == null) {
						actions = new String[5];
					}
					actions[opcode - 30] = buffer.getString();
					if (actions[opcode - 30].equalsIgnoreCase("hidden")) {
						actions[opcode - 30] = null;
					}
				} else if (opcode == 40) {
					int totalColors = buffer.getUnsignedByte();
					oldColors = new int[totalColors];
					newColors = new int[totalColors];
					for (int index = 0; index < totalColors; index++) {
						oldColors[index] = buffer.getUnsignedShort();
						newColors[index] = buffer.getUnsignedShort();
					}
				} else if (opcode == 60) {
					anInt746 = buffer.getUnsignedShort();
				} else if (opcode == 62) {
					aBoolean751 = true;
				} else if (opcode == 64) {
					aBoolean779 = false;
				} else if (opcode == 65) {
					modelScaleX = buffer.getUnsignedShort();
				} else if (opcode == 66) {
					modelScaleY = buffer.getUnsignedShort();
				} else if (opcode == 67) {
					modelScaleZ = buffer.getUnsignedShort();
				} else if (opcode == 68) {
					anInt758 = buffer.getUnsignedShort();
				} else if (opcode == 69) {
					anInt768 = buffer.getUnsignedByte();
				} else if (opcode == 70) {
					anInt738 = buffer.getShort();
				} else if (opcode == 71) {
					anInt745 = buffer.getShort();
				} else if (opcode == 72) {
					anInt783 = buffer.getShort();
				} else if (opcode == 73) {
					aBoolean736 = true;
				} else if (opcode == 74) {
					aBoolean766 = true;
				} else {
					if (opcode != 75) {
						continue;
					}
					anInt760 = buffer.getUnsignedByte();
				}
				continue start;
			} while (opcode != 77);
			varBitChild = buffer.getUnsignedShort();
			if (varBitChild == 65535) {
				varBitChild = -1;
			}
			anInt749 = buffer.getUnsignedShort();
			if (anInt749 == 65535) {
				anInt749 = -1;
			}
			int totalChildren = buffer.getUnsignedByte();
			childrenIDs = new int[totalChildren + 1];
			for (int index = 0; index <= totalChildren; index++) {
				childrenIDs[index] = buffer.getUnsignedShort();
				if (childrenIDs[index] == 65535) {
					childrenIDs[index] = -1;
				}
			}
		} while (true);
		if (i == -1) {
			hasActions = modelArray != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null) {
				hasActions = true;
			}
		}
		if (aBoolean766) {
			unwalkable = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1) {
			anInt760 = unwalkable ? 1 : 0;
		}
	}

	private ObjectDefinitions() {
		id = -1;
	}

	public static int totalObjects;
	public boolean aBoolean736;
	private byte modelBrightness;
	private int anInt738;
	public String name;
	private int modelScaleZ;
	private static final Model[] models = new Model[4];
	private byte modelShadowing;
	public int tileSizeX;
	private int anInt745;
	public int anInt746;
	private int[] newColors;
	private int modelScaleX;
	public int anInt749;
	private boolean aBoolean751;
	public static boolean lowMem;
	private static JagexBuffer dataBuffer;
	public int id;
	private static int[] streamIndices;
	public boolean aBoolean757;
	public int anInt758;
	public int childrenIDs[];
	private int anInt760;
	public int tileSizeY;
	public boolean conforms;
	public boolean aBoolean764;
	public static Client client;
	private boolean aBoolean766;
	public boolean unwalkable;
	public int anInt768;
	private boolean aBoolean769;
	private static int cacheIndex;
	private int modelScaleY;
	private int[] modelArray;
	public int varBitChild;
	public int anInt775;
	private int[] anIntArray776;
	public byte description[];
	public boolean hasActions;
	public boolean aBoolean779;
	public static MemCache memCache2 = new MemCache(30);
	public int animationId;
	private static ObjectDefinitions[] definitions;
	private int anInt783;
	private int[] oldColors;
	public static MemCache memCache1 = new MemCache(500);
	public String actions[];

}
