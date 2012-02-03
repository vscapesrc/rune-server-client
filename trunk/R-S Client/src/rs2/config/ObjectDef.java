package rs2.config;

import rs2.FrameHeader;
import rs2.MemCache;
import rs2.Model;
import rs2.ByteBuffer;
import rs2.Client;
import rs2.cache.JagexArchive;
import rs2.resource.ResourceProvider;

public final class ObjectDef {

	public static ObjectDef getDef(int i) {
		for (int j = 0; j < 20; j++)
			if (cache[j].type == i)
				return cache[j];

		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDef def = cache[cacheIndex];
		stream.offset = streamIndices[i];
		def.type = i;
		def.setDefaults();
		def.readValues(stream);
		return def;
	}

	private void setDefaults() {
		models = null;
		anIntArray776 = null;
		name = null;
		description = null;
		oldColors = null;
		newColors = null;
		sizeX = 1;
		sizeY = 1;
		isUnwalkable = true;
		aBoolean757 = true;
		hasActions = false;
		conform = false;
		aBoolean769 = false;
		aBoolean764 = false;
		animationId = -1;
		anInt775 = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	public void method574(ResourceProvider class42_sub1) {
		if (models == null)
			return;
		for (int j = 0; j < models.length; j++)
			class42_sub1.method560(models[j] & 0xffff, 0);
	}

	public static void nullLoader() {
		mruNodes1 = null;
		mruNodes2 = null;
		streamIndices = null;
		cache = null;
		stream = null;
	}

	public static void unpackConfig(JagexArchive streamLoader) {
		stream = new ByteBuffer(streamLoader.getData("loc.dat"));
		ByteBuffer stream = new ByteBuffer(streamLoader.getData("loc.idx"));
		totalObjects = stream.getShort();
		streamIndices = new int[totalObjects];
		int i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices[j] = i;
			i += stream.getShort();
		}

		cache = new ObjectDef[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new ObjectDef();

	}

	public boolean method577(int i) {
		if (anIntArray776 == null) {
			if (models == null)
				return true;
			if (i != 10)
				return true;
			boolean flag1 = true;
			for (int k = 0; k < models.length; k++)
				flag1 &= Model.method463(models[k] & 0xffff);

			return flag1;
		}
		for (int j = 0; j < anIntArray776.length; j++)
			if (anIntArray776[j] == i)
				return Model.method463(models[j] & 0xffff);

		return true;
	}

	public Model renderObject(int i, int j, int k, int l, int i1, int j1, int k1) {
		Model model = method581(i, k1, j);
		if (model == null)
			return null;
		if (conform || aBoolean769)
			model = new Model(conform, aBoolean769, model);
		if (conform) {
			int l1 = (k + l + i1 + j1) / 4;
			for (int i2 = 0; i2 < model.anInt1626; i2++) {
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
		if (models == null) {
			return true;
		}
		boolean flag1 = true;
		for (int index = 0; index < models.length; index++) {
			flag1 &= Model.method463(models[index] & 0xffff);
		}
		return flag1;
	}

	public ObjectDef method580() {
		int i = -1;
		if (anInt774 != -1) {
			VarBit varBit = VarBit.cache[anInt774];
			int j = varBit.anInt648;
			int k = varBit.anInt649;
			int l = varBit.anInt650;
			int i1 = Client.anIntArray1232[l - k];
			i = clientInstance.variousSettings[j] >> k & i1;
		} else if (anInt749 != -1)
			i = clientInstance.variousSettings[anInt749];
		if (i < 0 || i >= childrenIDs.length || childrenIDs[i] == -1) {
			return null;
		} else {
			return getDef(childrenIDs[i]);
		}
	}

	private Model method581(int j, int k, int l) {
		Model model = null;
		long l1;
		if (anIntArray776 == null) {
			if (j != 10)
				return null;
			l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
			Model model_1 = (Model) mruNodes2.get(l1);
			if (model_1 != null)
				return model_1;
			if (models == null)
				return null;
			boolean flag1 = aBoolean751 ^ (l > 3);
			int k1 = models.length;
			for (int i2 = 0; i2 < k1; i2++) {
				int l2 = models[i2];
				if (flag1)
					l2 += 0x10000;
				model = (Model) mruNodes1.get(l2);
				if (model == null) {
					model = Model.method462(l2 & 0xffff);
					if (model == null)
						return null;
					if (flag1)
						model.method477();
					mruNodes1.put(model, l2);
				}
				if (k1 > 1)
					aModelArray741s[i2] = model;
			}

			if (k1 > 1)
				model = new Model(k1, aModelArray741s);
		} else {
			int i1 = -1;
			for (int j1 = 0; j1 < anIntArray776.length; j1++) {
				if (anIntArray776[j1] != j)
					continue;
				i1 = j1;
				break;
			}

			if (i1 == -1)
				return null;
			l1 = (long) ((type << 6) + (i1 << 3) + l) + ((long) (k + 1) << 32);
			Model model_2 = (Model) mruNodes2.get(l1);
			if (model_2 != null)
				return model_2;
			int j2 = models[i1];
			boolean flag3 = aBoolean751 ^ (l > 3);
			if (flag3) {
				j2 += 0x10000;
			}
			model = (Model) mruNodes1.get(j2);
			if (model == null) {
				model = Model.method462(j2 & 0xffff);
				if (model == null) {
					return null;
				}
				if (flag3) {
					model.method477();
				}
				mruNodes1.put(model, j2);
			}
		}
		boolean flag;
		flag = anInt748 != 128 || anInt772 != 128 || anInt740 != 128;
		boolean flag2;
		flag2 = anInt738 != 0 || anInt745 != 0 || anInt783 != 0;
		Model model_3 = new Model(oldColors == null, FrameHeader.method532(k), l == 0 && k == -1 && !flag && !flag2, model);
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
			for (int k2 = 0; k2 < oldColors.length; k2++) {
				model_3.changeModelColors(oldColors[k2], newColors[k2]);
			}
		}
		if (flag) {
			model_3.method478(anInt748, anInt740, anInt772);
		}
		if (flag2) {
			model_3.method475(anInt738, anInt745, anInt783);
		}
		model_3.method479(64 + aByte737, 768 + aByte742 * 5, -50, -10, -50, !aBoolean769);
		if (anInt760 == 1) {
			model_3.anInt1654 = model_3.modelHeight;
		}
		mruNodes2.put(model_3, l1);
		return model_3;
	}

	private void readValues(ByteBuffer stream) {
		int i = -1;
		start: do {
			int opcode;
			do {
				opcode = stream.getUByte();
				if (opcode == 0) {
					break start;
				}
				if (opcode == 1) {
					int totalmModels = stream.getUByte();
					if (totalmModels > 0)
						if (models == null || lowMem) {
							anIntArray776 = new int[totalmModels];
							models = new int[totalmModels];
							for (int index = 0; index < totalmModels; index++) {
								models[index] = stream.getShort();
								anIntArray776[index] = stream.getUByte();
							}
						} else {
							stream.offset += totalmModels * 3;
						}
				} else if (opcode == 2) {
					name = stream.getString();
				} else if (opcode == 3) {
					description = stream.getBytes();
				} else if (opcode == 5) {
					int totalModels = stream.getUByte();
					if (totalModels > 0)
						if (models == null || lowMem) {
							anIntArray776 = null;
							models = new int[totalModels];
							for (int index = 0; index < totalModels; index++) {
								models[index] = stream.getShort();
							}
						} else {
							stream.offset += totalModels * 2;
						}
				} else if (opcode == 14) {
					sizeX = stream.getUByte();
				} else if (opcode == 15) {
					sizeY = stream.getUByte();
				} else if (opcode == 17) {
					isUnwalkable = false;
				} else if (opcode == 18) {
					aBoolean757 = false;
				} else if (opcode == 19) {
					hasActions = stream.getUByte() == 1;
				} else if (opcode == 21) {
					conform = true;
				} else if (opcode == 22) {
					aBoolean769 = true;
				} else if (opcode == 23) {
					aBoolean764 = true;
				} else if (opcode == 24) {
					animationId = stream.getShort();
					if (animationId == 65535) {
						animationId = -1;
					}
				} else if (opcode == 28) {
					anInt775 = stream.getUByte();
				} else if (opcode == 29) {
					aByte737 = stream.getByte();
				} else if (opcode == 39) {
					aByte742 = stream.getByte();
				} else if (opcode >= 30 && opcode < 39) {
					if (actions == null) {
						actions = new String[5];
					}
					actions[opcode - 30] = stream.getString();
					if (actions[opcode - 30].equalsIgnoreCase("hidden")) {
						actions[opcode - 30] = null;
					}
				} else if (opcode == 40) {
					int totalColors = stream.getUByte();
					oldColors = new int[totalColors];
					newColors = new int[totalColors];
					for (int index = 0; index < totalColors; index++) {
						oldColors[index] = stream.getShort();
						newColors[index] = stream.getShort();
					}
				} else if (opcode == 60) {
					anInt746 = stream.getShort();
				} else if (opcode == 62) {
					aBoolean751 = true;
				} else if (opcode == 64) {
					aBoolean779 = false;
				} else if (opcode == 65) {
					anInt748 = stream.getShort();
				} else if (opcode == 66) {
					anInt772 = stream.getShort();
				} else if (opcode == 67) {
					anInt740 = stream.getShort();
				} else if (opcode == 68) {
					anInt758 = stream.getShort();
				} else if (opcode == 69) {
					anInt768 = stream.getUByte();
				} else if (opcode == 70) {
					anInt738 = stream.getSignedShort();
				} else if (opcode == 71) {
					anInt745 = stream.getSignedShort();
				} else if (opcode == 72) {
					anInt783 = stream.getSignedShort();
				} else if (opcode == 73) {
					aBoolean736 = true;
				} else if (opcode == 74) {
					aBoolean766 = true;
				} else {
					if (opcode != 75) {
						continue;
					}
					anInt760 = stream.getUByte();
				}
				continue start;
			} while (opcode != 77);
			anInt774 = stream.getShort();
			if (anInt774 == 65535) {
				anInt774 = -1;
			}
			anInt749 = stream.getShort();
			if (anInt749 == 65535) {
				anInt749 = -1;
			}
			int totalChildren = stream.getUByte();
			childrenIDs = new int[totalChildren + 1];
			for (int index = 0; index <= totalChildren; index++) {
				childrenIDs[index] = stream.getShort();
				if (childrenIDs[index] == 65535) {
					childrenIDs[index] = -1;
				}
			}
		} while (true);
		if (i == -1) {
			hasActions = models != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null) {
				hasActions = true;
			}
		}
		if (aBoolean766) {
			isUnwalkable = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1) {
			anInt760 = isUnwalkable ? 1 : 0;
		}
	}

	private ObjectDef() {
		type = -1;
	}

	public static int totalObjects;
	public boolean aBoolean736;
	private byte aByte737;
	private int anInt738;
	public String name;
	private int anInt740;
	private static final Model[] aModelArray741s = new Model[4];
	private byte aByte742;
	public int sizeX;
	private int anInt745;
	public int anInt746;
	private int[] newColors;
	private int anInt748;
	public int anInt749;
	private boolean aBoolean751;
	public static boolean lowMem;
	private static ByteBuffer stream;
	public int type;
	private static int[] streamIndices;
	public boolean aBoolean757;
	public int anInt758;
	public int childrenIDs[];
	private int anInt760;
	public int sizeY;
	public boolean conform;
	public boolean aBoolean764;
	public static Client clientInstance;
	private boolean aBoolean766;
	public boolean isUnwalkable;
	public int anInt768;
	private boolean aBoolean769;
	private static int cacheIndex;
	private int anInt772;
	private int[] models;
	public int anInt774;
	public int anInt775;
	private int[] anIntArray776;
	public byte description[];
	public boolean hasActions;
	public boolean aBoolean779;
	public static MemCache mruNodes2 = new MemCache(30);
	public int animationId;
	private static ObjectDef[] cache;
	private int anInt783;
	private int[] oldColors;
	public static MemCache mruNodes1 = new MemCache(500);
	public String actions[];

}
