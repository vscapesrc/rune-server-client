package rs2.graphics;

import rs2.FrameReader;
import rs2.MemCache;
import rs2.Model;
import rs2.JagexBuffer;
import rs2.Client;
import rs2.cache.JagexArchive;
import rs2.config.NPCDefinitions;
import rs2.config.ItemDefinitions;
import rs2.util.TextUtils;

public final class RSInterface {

	public void swapInventoryItems(int i, int j) {
		int k = inventory[i];
		inventory[i] = inventory[j];
		inventory[j] = k;
		k = inventoryAmount[i];
		inventoryAmount[i] = inventoryAmount[j];
		inventoryAmount[j] = k;
	}

	public static void unpack(JagexArchive interfaceArchive, RSFont fonts[], JagexArchive mediaArchive) {
		spriteNodes = new MemCache(50000);
		JagexBuffer buffer = new JagexBuffer(interfaceArchive.getData("data"));
		int i = -1;
		int totalInterfaces = buffer.getUnsignedShort();
		cache = new RSInterface[totalInterfaces];
		while (buffer.offset < buffer.payload.length) {
			int k = buffer.getUnsignedShort();
			if (k == 65535) {
				i = buffer.getUnsignedShort();
				k = buffer.getUnsignedShort();
			}
			RSInterface rsi = cache[k] = new RSInterface();
			rsi.id = k;
			rsi.parentId = i;
			rsi.type = buffer.getUnsignedByte();
			rsi.actionType = buffer.getUnsignedByte();
			rsi.contentType = buffer.getUnsignedShort();
			rsi.width = buffer.getUnsignedShort();
			rsi.height = buffer.getUnsignedShort();
			rsi.alpha = (byte) buffer.getUnsignedByte();
			rsi.hoverId = buffer.getUnsignedByte();
			if (rsi.hoverId != 0) {
				rsi.hoverId = (rsi.hoverId - 1 << 8) + buffer.getUnsignedByte();
			} else {
				rsi.hoverId = -1;
			}
			int requiredmentIndex = buffer.getUnsignedByte();
			if (requiredmentIndex > 0) {
				rsi.valueCompareType = new int[requiredmentIndex];
				rsi.requiredValues = new int[requiredmentIndex];
				for (int index = 0; index < requiredmentIndex; index++) {
					rsi.valueCompareType[index] = buffer.getUnsignedByte();
					rsi.requiredValues[index] = buffer.getUnsignedShort();
				}
			}
			int valueType = buffer.getUnsignedByte();
			if (valueType > 0) {
				rsi.valueIndexArray = new int[valueType][];
				for (int valueIndex = 0; valueIndex < valueType; valueIndex++) {
					int size = buffer.getUnsignedShort();
					rsi.valueIndexArray[valueIndex] = new int[size];
					for (int nextIndex = 0; nextIndex < size; nextIndex++) {
						rsi.valueIndexArray[valueIndex][nextIndex] = buffer.getUnsignedShort();
					}
				}
			}
			if (rsi.type == 0) {
				rsi.scrollMax = buffer.getUnsignedShort();
				rsi.showInterface = buffer.getUnsignedByte() == 1;
				int totalChildren = buffer.getUnsignedShort();
				rsi.children = new int[totalChildren];
				rsi.childX = new int[totalChildren];
				rsi.childY = new int[totalChildren];
				for (int index = 0; index < totalChildren; index++) {
					rsi.children[index] = buffer.getUnsignedShort();
					rsi.childX[index] = buffer.getShort();
					rsi.childY[index] = buffer.getShort();
				}
			}
			if (rsi.type == 1) {
				buffer.getUnsignedShort();
				buffer.getUnsignedByte();
			}
			if (rsi.type == 2) {
				rsi.inventory = new int[rsi.width * rsi.height];
				rsi.inventoryAmount = new int[rsi.width * rsi.height];
				rsi.itemsSwappable = buffer.getUnsignedByte() == 1;
				rsi.isInventoryInterface = buffer.getUnsignedByte() == 1;
				rsi.usableItemInterface = buffer.getUnsignedByte() == 1;
				rsi.deletesTargetSlot = buffer.getUnsignedByte() == 1;
				rsi.invSpritePadX = buffer.getUnsignedByte();
				rsi.invSpritePadY = buffer.getUnsignedByte();
				rsi.spritesX = new int[20];
				rsi.spritesY = new int[20];
				rsi.sprites = new RSImage[20];
				for (int index = 0; index < 20; index++) {
					int dummy = buffer.getUnsignedByte();
					if (dummy == 1) {
						rsi.spritesX[index] = buffer.getShort();
						rsi.spritesY[index] = buffer.getShort();
						String spriteInfo = buffer.getString();
						if (mediaArchive != null && spriteInfo.length() > 0) {
							int i5 = spriteInfo.lastIndexOf(",");
							rsi.sprites[index] = getSprite(Integer.parseInt(spriteInfo.substring(i5 + 1)), mediaArchive, spriteInfo.substring(0, i5));
						}
					}
				}
				rsi.actions = new String[5];
				for (int index = 0; index < 5; index++) {
					rsi.actions[index] = buffer.getString();
					if (rsi.actions[index].length() == 0) {
						rsi.actions[index] = null;
					}
				}
			}
			if (rsi.type == 3) {
				rsi.filled = buffer.getUnsignedByte() == 1;
			}
			if (rsi.type == 4 || rsi.type == 1) {
				rsi.centered = buffer.getUnsignedByte() == 1;
				int fontId = buffer.getUnsignedByte();
				if (fonts != null) {
					rsi.font = fonts[fontId];
				}
				rsi.shadowed = buffer.getUnsignedByte() == 1;
			}
			if (rsi.type == 4) {
				rsi.disabledText = buffer.getString();
				rsi.enabledText = buffer.getString();
			}
			if (rsi.type == 1 || rsi.type == 3 || rsi.type == 4) {
				rsi.disabledColor = buffer.getInt();
			}
			if (rsi.type == 3 || rsi.type == 4) {
				rsi.enabledColor = buffer.getInt();
				rsi.disabledHoverColor = buffer.getInt();
				rsi.enabledHoverColor = buffer.getInt();
			}
			if (rsi.type == 5) {
				String spriteName = buffer.getString();
				if (mediaArchive != null && spriteName.length() > 0) {
					int id = spriteName.lastIndexOf(",");
					rsi.disabledSprite = getSprite(Integer.parseInt(spriteName.substring(id + 1)), mediaArchive, spriteName.substring(0, id));
				}
				spriteName = buffer.getString();
				if (mediaArchive != null && spriteName.length() > 0) {
					int id = spriteName.lastIndexOf(",");
					rsi.enabledSprite = getSprite(Integer.parseInt(spriteName.substring(id + 1)), mediaArchive, spriteName.substring(0, id));
				}
			}
			if (rsi.type == 6) {
				int value = buffer.getUnsignedByte();
				if (value != 0) {
					rsi.disabledMediaType = 1;
					rsi.disabledMediaId = (value - 1 << 8) + buffer.getUnsignedByte();
				}
				value = buffer.getUnsignedByte();
				if (value != 0) {
					rsi.enabledMediaType = 1;
					rsi.enabledMediaId = (value - 1 << 8) + buffer.getUnsignedByte();
				}
				value = buffer.getUnsignedByte();
				if (value != 0) {
					rsi.disabledAnimation = (value - 1 << 8) + buffer.getUnsignedByte();
				} else {
					rsi.disabledAnimation = -1;
				}
				value = buffer.getUnsignedByte();
				if (value != 0) {
					rsi.enabledAnimation = (value - 1 << 8) + buffer.getUnsignedByte();
				} else {
					rsi.enabledAnimation = -1;
				}
				rsi.zoom = buffer.getUnsignedShort();
				rsi.rotationX = buffer.getUnsignedShort();
				rsi.rotationY = buffer.getUnsignedShort();
			}
			if (rsi.type == 7) {
				rsi.inventory = new int[rsi.width * rsi.height];
				rsi.inventoryAmount = new int[rsi.width * rsi.height];
				rsi.centered = buffer.getUnsignedByte() == 1;
				int fontId = buffer.getUnsignedByte();
				if (fonts != null) {
					rsi.font = fonts[fontId];
				}
				rsi.shadowed = buffer.getUnsignedByte() == 1;
				rsi.disabledColor = buffer.getInt();
				rsi.invSpritePadX = buffer.getShort();
				rsi.invSpritePadY = buffer.getShort();
				rsi.isInventoryInterface = buffer.getUnsignedByte() == 1;
				rsi.actions = new String[5];
				for (int index = 0; index < 5; index++) {
					rsi.actions[index] = buffer.getString();
					if (rsi.actions[index].length() == 0) {
						rsi.actions[index] = null;
					}
				}
			}
			if (rsi.actionType == 2 || rsi.type == 2) {
				rsi.selectedActionName = buffer.getString();
				rsi.spellName = buffer.getString();
				rsi.spellUsableOn = buffer.getUnsignedShort();
			}
			if (rsi.type == 8) {
				rsi.disabledText = buffer.getString();
			}
			if (rsi.actionType == 1 || rsi.actionType == 4 || rsi.actionType == 5 || rsi.actionType == 6) {
				rsi.tooltip = buffer.getString();
				if (rsi.tooltip.length() == 0) {
					if (rsi.actionType == 1) {
						rsi.tooltip = "Ok";
					}
					if (rsi.actionType == 4) {
						rsi.tooltip = "Select";
					}
					if (rsi.actionType == 5) {
						rsi.tooltip = "Select";
					}
					if (rsi.actionType == 6) {
						rsi.tooltip = "Continue";
					}
				}
			}
		}
		spriteNodes = null;
	}

	private Model getModelByType(int modelType, int modelId) {
		Model model = (Model) modelNodes.get((modelType << 16) + modelId);
		if (model != null) {
			return model;
		}
		if (modelType == 1) {
			model = Model.method462(modelId);
		}
		if (modelType == 2) {
			model = NPCDefinitions.getDefinition(modelId).method160();
		}
		if (modelType == 3) {
			model = Client.myPlayer.getPlayerModel();
		}
		if (modelType == 4) {
			model = ItemDefinitions.getDefinition(modelId).getInventoryModel(50);
		}
		if (modelType == 5) {
			model = null;
		}
		if (model != null) {
			modelNodes.put(model, (modelType << 16) + modelId);
		}
		return model;
	}

	private static RSImage getSprite(int id, JagexArchive archive, String name) {
		long l = (TextUtils.method585(name) << 8) + (long) id;
		RSImage sprite = (RSImage) spriteNodes.get(l);
		if (sprite != null) {
			return sprite;
		}
		try {
			sprite = new RSImage(archive, name, id);
			spriteNodes.put(sprite, l);
		} catch (Exception _ex) {
			return null;
		}
		return sprite;
	}

	public static void getModel(boolean flag, Model model) {
		int value = 0;
		int type = 5;
		if (flag) {
			return;
		}
		modelNodes.unlinkAll();
		if (model != null && type != 4) {
			modelNodes.put(model, (type << 16) + value);
		}
	}

	public Model getAnimatedModel(int id, int k, boolean enabled) {
		Model animatedModel;
		if (enabled) {
			animatedModel = getModelByType(enabledMediaType, enabledMediaId);
		} else {
			animatedModel = getModelByType(disabledMediaType, disabledMediaId);
		}
		if (animatedModel == null) {
			return null;
		}
		if (k == -1 && id == -1 && animatedModel.colors == null) {
			return animatedModel;
		}
		Model model = new Model(true, FrameReader.method532(k) & FrameReader.method532(id), false, animatedModel);
		if (k != -1 || id != -1) {
			model.method469();
		}
		if (k != -1) {
			model.method470(k);
		}
		if (id != -1) {
			model.method470(id);
		}
		model.doLighting(64, 768, -50, -10, -50, true);
		return model;
	}

	public RSInterface() {
	}

	public RSImage disabledSprite;
	public int framesLeft;
	public RSImage sprites[];
	public static RSInterface cache[];
	public int requiredValues[];
	public int contentType;
	public int spritesX[];
	public int disabledHoverColor;
	public int actionType;
	public String spellName;
	public int enabledColor;
	public int width;
	public String tooltip;
	public String selectedActionName;
	public boolean centered;
	public int scrollPosition;
	public String actions[];
	public int valueIndexArray[][];
	public boolean filled;
	public String enabledText;
	public int hoverId;
	public int invSpritePadX;
	public int disabledColor;
	public int disabledMediaType;
	public int disabledMediaId;
	public boolean deletesTargetSlot;
	public int parentId;
	public int spellUsableOn;
	private static MemCache spriteNodes;
	public int enabledHoverColor;
	public int children[];
	public int childX[];
	public boolean usableItemInterface;
	public RSFont font;
	public int invSpritePadY;
	public int valueCompareType[];
	public int currentFrame;
	public int spritesY[];
	public String disabledText;
	public boolean isInventoryInterface;
	public int id;
	public int inventoryAmount[];
	public int inventory[];
	public byte alpha;
	private int enabledMediaType;
	private int enabledMediaId;
	public int disabledAnimation;
	public int enabledAnimation;
	public boolean itemsSwappable;
	public RSImage enabledSprite;
	public int scrollMax;
	public int type;
	public int drawOffsetX;
	private static final MemCache modelNodes = new MemCache(30);
	public int drawOffsetY;
	public boolean showInterface;
	public int height;
	public boolean shadowed;
	public int zoom;
	public int rotationX;
	public int rotationY;
	public int childY[];

}
