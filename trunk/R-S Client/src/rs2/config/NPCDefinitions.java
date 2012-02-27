package rs2.config;

import rs2.FrameReader;
import rs2.MemCache;
import rs2.Model;
import rs2.JagexBuffer;
import rs2.Client;
import rs2.cache.JagexArchive;

public final class NPCDefinitions {

	public static NPCDefinitions getDefinition(int id) {
		for (int index = 0; index < 20; index++) {
			if (definitions[index].id == (long) id) {
				return definitions[index];
			}
		}
		cacheIndex = (cacheIndex + 1) % 20;
		NPCDefinitions entityDef = definitions[cacheIndex] = new NPCDefinitions();
		dataBuffer.offset = streamIndices[id];
		entityDef.id = id;
		entityDef.readValues(dataBuffer);
		return entityDef;
	}

	public Model method160() {
		if (childrenIDs != null) {
			NPCDefinitions entityDef = getChildDefinition();
			if (entityDef == null) {
				return null;
			} else {
				return entityDef.method160();
			}
		}
		if (dialogModels == null) {
			return null;
		}
		boolean flag1 = false;
		for (int index = 0; index < dialogModels.length; index++) {
			if (!Model.method463(dialogModels[index])) {
				flag1 = true;
			}
		}
		if (flag1) {
			return null;
		}
		Model models[] = new Model[dialogModels.length];
		for (int index = 0; index < dialogModels.length; index++) {
			models[index] = Model.method462(dialogModels[index]);
		}
		Model model;
		if (models.length == 1) {
			model = models[0];
		} else {
			model = new Model(models.length, models);
		}
		if (oldColors != null) {
			for (int colors = 0; colors < oldColors.length; colors++) {
				model.changeColors(oldColors[colors], newColors[colors]);
			}
		}
		return model;
	}

	public NPCDefinitions getChildDefinition() {
		int child = -1;
		if (varBitChild != -1) {
			VarBit varBit = VarBit.cache[varBitChild];
			int k = varBit.anInt648;
			int l = varBit.anInt649;
			int i1 = varBit.anInt650;
			int j1 = Client.anIntArray1232[i1 - l];
			child = client.variousSettings[k] >> l & j1;
		} else if (configChild != -1) {
			child = client.variousSettings[configChild];
		}
		if (child < 0 || child >= childrenIDs.length || childrenIDs[child] == -1) {
			return null;
		} else {
			return getDefinition(childrenIDs[child]);
		}
	}

	public static void unpackConfig(JagexArchive streamLoader) {
		dataBuffer = new JagexBuffer(streamLoader.getData("npc.dat"));
		JagexBuffer indexBuffer = new JagexBuffer(streamLoader.getData("npc.idx"));
		int length = indexBuffer.getUnsignedShort();
		streamIndices = new int[length];
		int i = 2;
		for (int index = 0; index < length; index++) {
			streamIndices[index] = i;
			i += indexBuffer.getUnsignedShort();
		}
		definitions = new NPCDefinitions[20];
		for (int index = 0; index < 20; index++) {
			definitions[index] = new NPCDefinitions();
		}
	}

	public static void clearCache() {
		memCache = null;
		streamIndices = null;
		definitions = null;
		dataBuffer = null;
	}

	public Model method164(int j, int k, int ai[]) {
		if (childrenIDs != null) {
			NPCDefinitions def = getChildDefinition();
			if (def == null) {
				return null;
			} else {
				return def.method164(j, k, ai);
			}
		}
		Model model = (Model) memCache.get(id);
		if (model == null) {
			boolean flag = false;
			for (int i1 = 0; i1 < entityModels.length; i1++) {
				if (!Model.method463(entityModels[i1])) {
					flag = true;
				}
			}
			if (flag) {
				return null;
			}
			Model models[] = new Model[entityModels.length];
			for (int index = 0; index < entityModels.length; index++) {
				models[index] = Model.method462(entityModels[index]);
			}
			if (models.length == 1) {
				model = models[0];
			} else {
				model = new Model(models.length, models);
			}
			if (oldColors != null) {
				for (int color = 0; color < oldColors.length; color++) {
					model.changeColors(oldColors[color], newColors[color]);
				}
			}
			model.method469();
			model.doLighting(64 + modelBrightness, 850 + modelShadowing, -30, -50, -30, true);
			memCache.put(model, id);
		}
		Model model_1 = Model.aModel_1621;
		model_1.method464(model, FrameReader.method532(k) & FrameReader.method532(j));
		if (k != -1 && j != -1) {
			model_1.method471(ai, j, k);
		} else if (k != -1) {
			model_1.method470(k);
		}
		if (modelScaleXZ != 128 || modelScaleY != 128) {
			model_1.scaleModel(modelScaleXZ, modelScaleY, modelScaleXZ);
		}
		model_1.method466();
		model_1.anIntArrayArray1658 = null;
		model_1.anIntArrayArray1657 = null;
		if (tileSize == 1) {
			model_1.aBoolean1659 = true;
		}
		return model_1;
	}

	public void readValues(JagexBuffer buffer) {
		do {
			int opcode = buffer.getUnsignedByte();
			if (opcode == 0) {
				return;
			}
			if (opcode == 1) {
				int total = buffer.getUnsignedByte();
				entityModels = new int[total];
				for (int index = 0; index < total; index++) {
					entityModels[index] = buffer.getUnsignedShort();
				}
			} else if (opcode == 2)
				name = buffer.getString();
			else if (opcode == 3)
				description = buffer.getBytes();
			else if (opcode == 12)
				tileSize = buffer.getSignedByte();
			else if (opcode == 13)
				standAnim = buffer.getUnsignedShort();
			else if (opcode == 14)
				walkAnim = buffer.getUnsignedShort();
			else if (opcode == 17) {
				walkAnim = buffer.getUnsignedShort();
				turn180Anim = buffer.getUnsignedShort();
				turn90LeftAnim = buffer.getUnsignedShort();
				turn90RightAnim = buffer.getUnsignedShort();
			} else if (opcode >= 30 && opcode < 40) {
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
				for (int color = 0; color < totalColors; color++) {
					oldColors[color] = buffer.getUnsignedShort();
					newColors[color] = buffer.getUnsignedShort();
				}
			} else if (opcode == 60) {
				int total = buffer.getUnsignedByte();
				dialogModels = new int[total];
				for (int model = 0; model < total; model++) {
					dialogModels[model] = buffer.getUnsignedShort();
				}
			} else if (opcode == 90)
				buffer.getUnsignedShort();
			else if (opcode == 91)
				buffer.getUnsignedShort();
			else if (opcode == 92)
				buffer.getUnsignedShort();
			else if (opcode == 93)
				displayMapMarker = false;
			else if (opcode == 95)
				combatLevel = buffer.getUnsignedShort();
			else if (opcode == 97)
				modelScaleXZ = buffer.getUnsignedShort();
			else if (opcode == 98)
				modelScaleY = buffer.getUnsignedShort();
			else if (opcode == 99)
				visible = true;
			else if (opcode == 100)
				modelBrightness = buffer.getSignedByte();
			else if (opcode == 101)
				modelShadowing = buffer.getSignedByte() * 5;
			else if (opcode == 102)
				headIcon = buffer.getUnsignedShort();
			else if (opcode == 103)
				getDegreesToTurn = buffer.getUnsignedShort();
			else if (opcode == 106) {
				varBitChild = buffer.getUnsignedShort();
				if (varBitChild == 65535)
					varBitChild = -1;
				configChild = buffer.getUnsignedShort();
				if (configChild == 65535)
					configChild = -1;
				int total = buffer.getUnsignedByte();
				childrenIDs = new int[total + 1];
				for (int child = 0; child <= total; child++) {
					childrenIDs[child] = buffer.getUnsignedShort();
					if (childrenIDs[child] == 65535) {
						childrenIDs[child] = -1;
					}
				}

			} else if (opcode == 107)
				aBoolean84 = false;
		} while (true);
	}

	/**
	 * Returns the total amount of npc definitions.
	 * @return
	 */
	public static int getCount() {
		return streamIndices.length;
	}

	public NPCDefinitions() {
		turn90RightAnim = -1;
		varBitChild = -1;
		turn180Anim = -1;
		configChild = -1;
		combatLevel = -1;
		walkAnim = -1;
		tileSize = 1;
		headIcon = -1;
		standAnim = -1;
		id = -1L;
		getDegreesToTurn = 32;
		turn90LeftAnim = -1;
		aBoolean84 = true;
		modelScaleY = 128;
		displayMapMarker = true;
		modelScaleXZ = 128;
		visible = false;
	}

	public int turn90RightAnim;
	public static int cacheIndex;
	public int varBitChild;
	public int turn180Anim;
	public int configChild;
	public static JagexBuffer dataBuffer;
	public int combatLevel;
	public String name;
	public String actions[];
	public int walkAnim;
	public byte tileSize;
	public int[] newColors;
	public static int[] streamIndices;
	public int[] dialogModels;
	public int headIcon;
	public int[] oldColors;
	public int standAnim;
	public long id;
	public int getDegreesToTurn;
	public static NPCDefinitions[] definitions;
	public static Client client;
	public int turn90LeftAnim;
	public boolean aBoolean84;
	public int modelBrightness;
	public int modelScaleY;
	public boolean displayMapMarker;
	public int childrenIDs[];
	public byte description[];
	public int modelScaleXZ;
	public int modelShadowing;
	public boolean visible;
	public int[] entityModels;
	public static MemCache memCache = new MemCache(30);

}
