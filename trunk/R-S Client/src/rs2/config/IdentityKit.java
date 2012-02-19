package rs2.config;

import rs2.Model;
import rs2.JagexBuffer;
import rs2.cache.JagexArchive;

public class IdentityKit {

	public static void unpackConfig(JagexArchive archive) {
		JagexBuffer buffer = new JagexBuffer(archive.getData("idk.dat"));
		length = buffer.getUnsignedShort();
		if (cache == null) {
			cache = new IdentityKit[length];
		}
		for (int index = 0; index < length; index++) {
			if (cache[index] == null) {
				cache[index] = new IdentityKit();
			}
			cache[index].readValues(buffer);
		}
	}

	private void readValues(JagexBuffer buffer) {
		do {
			int opcode = buffer.getUnsignedByte();
			if (opcode == 0)
				return;
			if (opcode == 1)
				partId = buffer.getUnsignedByte();
			else if (opcode == 2) {
				int total = buffer.getUnsignedByte();
				models = new int[total];
				for (int model = 0; model < total; model++) {
					models[model] = buffer.getUnsignedShort();
				}
			} else if (opcode == 3)
				disableDisplay = true;
			else if (opcode >= 40 && opcode < 50)
				oldColors[opcode - 40] = buffer.getUnsignedShort();
			else if (opcode >= 50 && opcode < 60)
				newColors[opcode - 50] = buffer.getUnsignedShort();
			else if (opcode >= 60 && opcode < 70)
				dialogModels[opcode - 60] = buffer.getUnsignedShort();
			else
				System.out.println("Error unrecognised config code: " + opcode);
		} while (true);
	}

	public boolean method537() {
		if (models == null) {
			return true;
		}
		boolean flag = true;
		for (int model = 0; model < models.length; model++) {
			if (!Model.method463(models[model])) {
				flag = false;
			}
		}
		return flag;
	}

	public Model method538() {
		if (models == null) {
			return null;
		}
		Model modelArray[] = new Model[models.length];
		for (int i = 0; i < models.length; i++)
			modelArray[i] = Model.method462(models[i]);

		Model model;
		if (modelArray.length == 1) {
			model = modelArray[0];
		} else {
			model = new Model(modelArray.length, modelArray);
		}
		for (int color = 0; color < 6; color++) {
			if (oldColors[color] == 0) {
				break;
			}
			model.changeModelColors(oldColors[color], newColors[color]);
		}
		return model;
	}

	public boolean method539() {
		boolean flag1 = true;
		for (int model = 0; model < 5; model++) {
			if (dialogModels[model] != -1 && !Model.method463(dialogModels[model])) {
				flag1 = false;
			}
		}
		return flag1;
	}

	public Model method540() {
		Model modelArray[] = new Model[5];
		int id = 0;
		for (int model = 0; model < 5; model++) {
			if (dialogModels[model] != -1) {
				modelArray[id++] = Model.method462(dialogModels[model]);
			}
		}
		Model model = new Model(id, modelArray);
		for (int color = 0; color < 6; color++) {
			if (oldColors[color] == 0) {
				break;
			}
			model.changeModelColors(oldColors[color], newColors[color]);
		}

		return model;
	}

	/**
	 * Returns the total amount of identity kit data.
	 * @return
	 */
	public static int getCount() {
		return cache.length;
	}

	public IdentityKit() {
		partId = -1;
		oldColors = new int[6];
		newColors = new int[6];
		disableDisplay = false;
	}

	public static int length;
	public static IdentityKit cache[];
	public int partId;
	public int[] models;
	public final int[] oldColors;
	public final int[] newColors;
	public final int[] dialogModels = { -1, -1, -1, -1, -1 };
	public boolean disableDisplay;
}
