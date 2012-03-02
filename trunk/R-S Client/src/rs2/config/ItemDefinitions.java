package rs2.config;

import rs2.MemCache;
import rs2.Model;
import rs2.JagexBuffer;
import rs2.cache.JagexArchive;
import rs2.graphics.RSDrawingArea;
import rs2.graphics.RSImage;
import rs2.graphics.Rasterizer;

public final class ItemDefinitions {

	public static void clearCache() {
		memCache2 = null;
		memCache1 = null;
		streamIndices = null;
		definitions = null;
		dataBuffer = null;
	}

	public boolean method192(int gender) {
		int model1 = maleDialog1;
		int model2 = maleDialog2;
		if (gender == 1) {
			model1 = femaleDialog1;
			model2 = femaleDialog2;
		}
		if (model1 == -1) {
			return true;
		}
		boolean flag = true;
		if (!Model.method463(model1)) {
			flag = false;
		}
		if (model2 != -1 && !Model.method463(model2)) {
			flag = false;
		}
		return flag;
	}

	public static void unpackConfig(JagexArchive archive) {
		dataBuffer = new JagexBuffer(archive.getData("obj.dat"));
		JagexBuffer indexBuffer = new JagexBuffer(archive.getData("obj.idx"));
		totalItems = indexBuffer.getUnsignedShort();
		streamIndices = new int[totalItems];
		int i = 2;
		for (int index = 0; index < totalItems; index++) {
			streamIndices[index] = i;
			i += indexBuffer.getUnsignedShort();
		}
		definitions = new ItemDefinitions[10];
		for (int index = 0; index < 10; index++) {
			definitions[index] = new ItemDefinitions();
		}
	}

	public Model method194(int gender) {
		int model1 = maleDialog1;
		int model2 = maleDialog2;
		if (gender == 1) {
			model1 = femaleDialog1;
			model2 = femaleDialog2;
		}
		if (model1 == -1) {
			return null;
		}
		Model model = Model.method462(model1);
		if (model2 != -1) {
			Model model_1 = Model.method462(model2);
			Model models[] = { model, model_1 };
			model = new Model(2, models);
		}
		if (oldColors != null) {
			for (int index = 0; index < oldColors.length; index++) {
				model.changeColors(oldColors[index], newColors[index]);
			}
		}
		return model;
	}

	public boolean method195(int gender) {
		int model1 = maleModel1;
		int model2 = maleModel2;
		int model3 = maleModel3;
		if (gender == 1) {
			model1 = femaleModel1;
			model2 = femaleModel2;
			model3 = femaleModel3;
		}
		if (model1 == -1) {
			return true;
		}
		boolean flag = true;
		if (!Model.method463(model1)) {
			flag = false;
		}
		if (model2 != -1 && !Model.method463(model2)) {
			flag = false;
		}
		if (model3 != -1 && !Model.method463(model3)) {
			flag = false;
		}
		return flag;
	}

	public Model method196(int gender) {
		int model1 = maleModel1;
		int model2 = maleModel2;
		int model3 = maleModel3;
		if (gender == 1) {
			model1 = femaleModel1;
			model2 = femaleModel2;
			model3 = femaleModel3;
		}
		if (model1 == -1) {
			return null;
		}
		Model model = Model.method462(model1);
		if (model2 != -1)
			if (model3 != -1) {
				Model model_1 = Model.method462(model2);
				Model model_3 = Model.method462(model3);
				Model models[] = { model, model_1, model_3 };
				model = new Model(3, models);
			} else {
				Model model_2 = Model.method462(model2);
				Model models[] = { model, model_2 };
				model = new Model(2, models);
			}
		if (gender == 0 && maleOffset != 0) {
			model.moveVertices(0, maleOffset, 0);
		}
		if (gender == 1 && femaleOffset != 0) {
			model.moveVertices(0, femaleOffset, 0);
		}
		if (oldColors != null) {
			for (int color = 0; color < oldColors.length; color++) {
				model.changeColors(oldColors[color], newColors[color]);
			}
		}
		return model;
	}

	public void setDefaults() {
		displayModel = 0;
		name = null;
		description = null;
		oldColors = null;
		newColors = null;
		modelZoom = 2000;
		modelRotationX = 0;
		modelRotationY = 0;
		anInt204 = 0;
		modelOffsetX = 0;
		modelOffsetY = 0;
		stackable = false;
		value = 1;
		membersObject = false;
		groundActions = null;
		actions = null;
		maleModel1 = -1;
		maleModel2 = -1;
		maleOffset = 0;
		femaleModel1 = -1;
		femaleModel2 = -1;
		femaleOffset = 0;
		maleModel3 = -1;
		femaleModel3 = -1;
		maleDialog1 = -1;
		maleDialog2 = -1;
		femaleDialog1 = -1;
		femaleDialog2 = -1;
		stackIDs = null;
		stackAmounts = null;
		certId = -1;
		certTemplateId = -1;
		modelScaleX = 128;
		modelScaleY = 128;
		modelScaleZ = 128;
		modelBrightness = 0;
		modelShadowing = 0;
		team = 0;
	}

	public static ItemDefinitions getDefinition(int id) {
		for (int index = 0; index < 10; index++) {
			if (definitions[index].id == id) {
				return definitions[index];
			}
		}
		cacheIndex = (cacheIndex + 1) % 10;
		ItemDefinitions def = definitions[cacheIndex];
		dataBuffer.offset = streamIndices[id];
		def.id = id;
		def.setDefaults();
		def.readValues(dataBuffer);
		if (def.isNoted()) {
			def.toNote();
		}
		if (!isMembers && def.membersObject) {
			def.name = "Members Object";
			def.description = "Login to a members' server to use this object.".getBytes();
			def.groundActions = null;
			def.actions = null;
			def.team = 0;
		}
		return def;
	}

	public void toNote() {
		ItemDefinitions itemDef = getDefinition(certTemplateId);
		displayModel = itemDef.displayModel;
		modelZoom = itemDef.modelZoom;
		modelRotationX = itemDef.modelRotationX;
		modelRotationY = itemDef.modelRotationY;
		anInt204 = itemDef.anInt204;
		modelOffsetX = itemDef.modelOffsetX;
		modelOffsetY = itemDef.modelOffsetY;
		oldColors = itemDef.oldColors;
		newColors = itemDef.newColors;
		ItemDefinitions itemDef_1 = getDefinition(certId);
		name = itemDef_1.name;
		membersObject = itemDef_1.membersObject;
		value = itemDef_1.value;
		String s = "a";
		char c = itemDef_1.name.charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
			s = "an";
		}
		description = ("Swap this note at any bank for " + s + " " + itemDef_1.name + ".").getBytes();
		stackable = true;
	}

	public static RSImage getSprite(int i, int j, int k) {
		if (k == 0) {
			RSImage sprite = (RSImage) memCache1.get(i);
			if (sprite != null && sprite.maxHeight != j && sprite.maxHeight != -1) {
				sprite.remove();
				sprite = null;
			}
			if (sprite != null)
				return sprite;
		}
		ItemDefinitions def = getDefinition(i);
		if (def.stackIDs == null) {
			j = -1;
		}
		if (j > 1) {
			int i1 = -1;
			for (int index = 0; index < 10; index++) {
				if (j >= def.stackAmounts[index] && def.stackAmounts[index] != 0) {
					i1 = def.stackIDs[index];
				}
			}
			if (i1 != -1) {
				def = getDefinition(i1);
			}
		}
		Model model = def.method201(1);
		if (model == null) {
			return null;
		}
		RSImage sprite = null;
		if (def.certTemplateId != -1) {
			sprite = getSprite(def.certId, 10, -1);
			if (sprite == null) {
				return null;
			}
		}
		RSImage sprite2 = new RSImage(32, 32);
		int k1 = Rasterizer.centerX;
		int l1 = Rasterizer.centerY;
		int ai[] = Rasterizer.lineOffsets;
		int ai1[] = RSDrawingArea.pixels;
		int i2 = RSDrawingArea.width;
		int j2 = RSDrawingArea.height;
		int k2 = RSDrawingArea.startX;
		int l2 = RSDrawingArea.endX;
		int i3 = RSDrawingArea.startY;
		int j3 = RSDrawingArea.endY;
		Rasterizer.notTextures = false;
		RSDrawingArea.initDrawingArea(32, 32, sprite2.myPixels);
		RSDrawingArea.drawFilledPixels(0, 0, 32, 32, 0);
		Rasterizer.setDefaultBounds();
		int k3 = def.modelZoom;
		if (k == -1) {
			k3 = (int) ((double) k3 * 1.5D);
		}
		if (k > 0) {
			k3 = (int) ((double) k3 * 1.04D);
		}
		int l3 = Rasterizer.SINE[def.modelRotationX] * k3 >> 16;
		int i4 = Rasterizer.COSINE[def.modelRotationX] * k3 >> 16;
		model.method482(def.modelRotationY, def.anInt204, def.modelRotationX, def.modelOffsetX, l3 + model.modelHeight / 2 + def.modelOffsetY, i4 + def.modelOffsetY);
		for (int i5 = 31; i5 >= 0; i5--) {
			for (int j4 = 31; j4 >= 0; j4--)
				if (sprite2.myPixels[i5 + j4 * 32] == 0)
					if (i5 > 0 && sprite2.myPixels[(i5 - 1) + j4 * 32] > 1)
						sprite2.myPixels[i5 + j4 * 32] = 1;
					else if (j4 > 0 && sprite2.myPixels[i5 + (j4 - 1) * 32] > 1)
						sprite2.myPixels[i5 + j4 * 32] = 1;
					else if (i5 < 31 && sprite2.myPixels[i5 + 1 + j4 * 32] > 1)
						sprite2.myPixels[i5 + j4 * 32] = 1;
					else if (j4 < 31
							&& sprite2.myPixels[i5 + (j4 + 1) * 32] > 1)
						sprite2.myPixels[i5 + j4 * 32] = 1;

		}

		if (k > 0) {
			for (int j5 = 31; j5 >= 0; j5--) {
				for (int k4 = 31; k4 >= 0; k4--)
					if (sprite2.myPixels[j5 + k4 * 32] == 0)
						if (j5 > 0 && sprite2.myPixels[(j5 - 1) + k4 * 32] == 1)
							sprite2.myPixels[j5 + k4 * 32] = k;
						else if (k4 > 0
								&& sprite2.myPixels[j5 + (k4 - 1) * 32] == 1)
							sprite2.myPixels[j5 + k4 * 32] = k;
						else if (j5 < 31
								&& sprite2.myPixels[j5 + 1 + k4 * 32] == 1)
							sprite2.myPixels[j5 + k4 * 32] = k;
						else if (k4 < 31
								&& sprite2.myPixels[j5 + (k4 + 1) * 32] == 1)
							sprite2.myPixels[j5 + k4 * 32] = k;

			}

		} else if (k == 0) {
			for (int k5 = 31; k5 >= 0; k5--) {
				for (int l4 = 31; l4 >= 0; l4--)
					if (sprite2.myPixels[k5 + l4 * 32] == 0 && k5 > 0 && l4 > 0
							&& sprite2.myPixels[(k5 - 1) + (l4 - 1) * 32] > 0)
						sprite2.myPixels[k5 + l4 * 32] = 0x302020;

			}

		}
		if (def.certTemplateId != -1) {
			int l5 = sprite.maxWidth;
			int j6 = sprite.maxHeight;
			sprite.maxWidth = 32;
			sprite.maxHeight = 32;
			sprite.drawImage(0, 0);
			sprite.maxWidth = l5;
			sprite.maxHeight = j6;
		}
		if (k == 0)
			memCache1.put(sprite2, i);
		RSDrawingArea.initDrawingArea(i2, j2, ai1);
		RSDrawingArea.setBounds(k2, l2, i3, j3);
		Rasterizer.centerX = k1;
		Rasterizer.centerY = l1;
		Rasterizer.lineOffsets = ai;
		Rasterizer.notTextures = true;
		if (def.stackable) {
			sprite2.maxWidth = 33;
		} else {
			sprite2.maxWidth = 32;
		}
		sprite2.maxHeight = j;
		return sprite2;
	}

	public Model method201(int i) {
		if (stackIDs != null && i > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++) {
				if (i >= stackAmounts[k] && stackAmounts[k] != 0) {
					j = stackIDs[k];
				}
			}
			if (j != -1) {
				return getDefinition(j).method201(1);
			}
		}
		Model model = (Model) memCache2.get(id);
		if (model != null)
			return model;
		model = Model.method462(displayModel);
		if (model == null)
			return null;
		if (modelScaleX != 128 || modelScaleY != 128 || modelScaleZ != 128) {
			model.scaleModel(modelScaleX, modelScaleY, modelScaleZ);
		}
		if (oldColors != null) {
			for (int color = 0; color < oldColors.length; color++) {
				model.changeColors(oldColors[color], newColors[color]);
			}
		}
		model.doLighting(64 + modelBrightness, 768 + modelShadowing, -50, -10, -50, true);
		model.aBoolean1659 = true;
		memCache2.put(model, id);
		return model;
	}

	public Model getInventoryModel(int i) {
		if (stackIDs != null && i > 1) {
			int id = -1;
			for (int index = 0; index < 10; index++) {
				if (i >= stackAmounts[index] && stackAmounts[index] != 0) {
					id = stackIDs[index];
				}
			}
			if (id != -1) {
				return getDefinition(id).getInventoryModel(1);
			}
		}
		Model model = Model.method462(displayModel);
		if (model == null)
			return null;
		if (oldColors != null) {
			for (int color = 0; color < oldColors.length; color++) {
				model.changeColors(oldColors[color], newColors[color]);
			}
		}
		return model;
	}

	public void readValues(JagexBuffer buffer) {
		do {
			int opcode = buffer.getUnsignedByte();
			if (opcode == 0) {
				return;
			}
			if (opcode == 1) {
				displayModel = buffer.getUnsignedShort();
			} else if (opcode == 2) {
				name = buffer.getString();
			} else if (opcode == 3) {
				description = buffer.getBytes();
			} else if (opcode == 4) {
				modelZoom = buffer.getUnsignedShort();
			} else if (opcode == 5) {
				modelRotationX = buffer.getUnsignedShort();
			} else if (opcode == 6) {
				modelRotationY = buffer.getUnsignedShort();
			} else if (opcode == 7) {
				modelOffsetX = buffer.getUnsignedShort();
				if (modelOffsetX > 32767) {
					modelOffsetX -= 0x10000;
				}
			} else if (opcode == 8) {
				modelOffsetY = buffer.getUnsignedShort();
				if (modelOffsetY > 32767) {
					modelOffsetY -= 0x10000;
				}
			} else if (opcode == 10) {
				buffer.getUnsignedShort();
			} else if (opcode == 11) {
				stackable = true;
			} else if (opcode == 12) {
				value = buffer.getInt();
			} else if (opcode == 16) {
				membersObject = true;
			} else if (opcode == 23) {
				maleModel1 = buffer.getUnsignedShort();
				maleOffset = buffer.getSignedByte();
			} else if (opcode == 24) {
				maleModel2 = buffer.getUnsignedShort();
			} else if (opcode == 25) {
				femaleModel1 = buffer.getUnsignedShort();
				femaleOffset = buffer.getSignedByte();
			} else if (opcode == 26) {
				femaleModel2 = buffer.getUnsignedShort();
			} else if (opcode >= 30 && opcode < 35) {
				if (groundActions == null) {
					groundActions = new String[5];
				}
				groundActions[opcode - 30] = buffer.getString();
				if (groundActions[opcode - 30].equalsIgnoreCase("hidden")) {
					groundActions[opcode - 30] = null;
				}
			} else if (opcode >= 35 && opcode < 40) {
				if (actions == null)
					actions = new String[5];
				actions[opcode - 35] = buffer.getString();
			} else if (opcode == 40) {
				int j = buffer.getUnsignedByte();
				oldColors = new int[j];
				newColors = new int[j];
				for (int k = 0; k < j; k++) {
					oldColors[k] = buffer.getUnsignedShort();
					newColors[k] = buffer.getUnsignedShort();
				}

			} else if (opcode == 78) {
				maleModel3 = buffer.getUnsignedShort();
			} else if (opcode == 79) {
				femaleModel3 = buffer.getUnsignedShort();
			} else if (opcode == 90) {
				maleDialog1 = buffer.getUnsignedShort();
			} else if (opcode == 91) {
				femaleDialog1 = buffer.getUnsignedShort();
			} else if (opcode == 92) {
				maleDialog2 = buffer.getUnsignedShort();
			} else if (opcode == 93) {
				femaleDialog2 = buffer.getUnsignedShort();
			} else if (opcode == 95) {
				anInt204 = buffer.getUnsignedShort();
				anInt204 = anInt204 * 10;
			} else if (opcode == 97) {
				certId = buffer.getUnsignedShort();
			} else if (opcode == 98) {
				certTemplateId = buffer.getUnsignedShort();
			} else if (opcode >= 100 && opcode < 110) {
				if (stackIDs == null) {
					stackIDs = new int[10];
					stackAmounts = new int[10];
				}
				stackIDs[opcode - 100] = buffer.getUnsignedShort();
				stackAmounts[opcode - 100] = buffer.getUnsignedShort();
			} else if (opcode == 110) {
				modelScaleX = buffer.getUnsignedShort();
			} else if (opcode == 111) {
				modelScaleY = buffer.getUnsignedShort();
			} else if (opcode == 112) {
				modelScaleZ = buffer.getUnsignedShort();
			} else if (opcode == 113) {
				modelBrightness = buffer.getSignedByte();
			} else if (opcode == 114) {
				modelShadowing = buffer.getSignedByte() * 5;
			} else if (opcode == 115) {
				team = buffer.getUnsignedByte();
			}
		} while (true);
	}

	/**
	 * Is the item noted?
	 * @return
	 */
	public boolean isNoted() {
		return certTemplateId != -1;
	}

	public ItemDefinitions() {
		id = -1;
	}

	public byte femaleOffset;
	public int value;
	public int[] oldColors;
	public int id;
	public static MemCache memCache1 = new MemCache(100);
	public static MemCache memCache2 = new MemCache(50);
	public int[] newColors;
	public boolean membersObject;
	public int femaleModel3;
	public int certTemplateId;
	public int femaleModel2;
	public int maleModel1;
	public int maleDialog2;
	public int modelScaleX;
	public String groundActions[];
	public int modelOffsetX;
	public String name;
	public static ItemDefinitions[] definitions;
	public int femaleDialog2;
	public int displayModel;
	public int maleDialog1;
	public boolean stackable;
	public byte description[];
	public int certId;
	public static int cacheIndex;
	public int modelZoom;
	public static boolean isMembers = true;
	public static JagexBuffer dataBuffer;
	public int modelShadowing;
	public int maleModel3;
	public int maleModel2;
	public String actions[];
	public int modelRotationX;
	public int modelScaleZ;
	public int modelScaleY;
	public int[] stackIDs;
	public int modelOffsetY;
	public static int[] streamIndices;
	public int modelBrightness;
	public int femaleDialog1;
	public int modelRotationY;
	public int femaleModel1;
	public int[] stackAmounts;
	public int team;
	public static int totalItems;
	public int anInt204;
	public byte maleOffset;
	public int lentId;
	public int lentTemplateId;

}
