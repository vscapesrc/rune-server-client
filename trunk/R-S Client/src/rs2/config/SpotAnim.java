package rs2.config;

import rs2.MemCache;
import rs2.Model;
import rs2.JagexBuffer;
import rs2.cache.JagexArchive;

public final class SpotAnim {

	public static void unpackConfig(JagexArchive archive) {
		JagexBuffer buffer = new JagexBuffer(archive.getData("spotanim.dat"));
		int length = buffer.getUnsignedShort();
		if (cache == null) {
			cache = new SpotAnim[length];
		}
		for (int index = 0; index < length; index++) {
			if (cache[index] == null) {
				cache[index] = new SpotAnim();
			}
			cache[index].id = index;
			cache[index].readValues(buffer);
		}
	}

	public static SpotAnim getGraphic(int id) {
		if (id > cache.length - 1) {
			return cache[0];
		}
		if (cache[id] == null) {
			return cache[0];
		}
		return cache[id];
	}

	private void readValues(JagexBuffer stream) {
		do {
			int i = stream.getUnsignedByte();
			if (i == 0)
				return;
			if (i == 1)
				modelId = stream.getUnsignedShort();
			else if (i == 2) {
				animationId = stream.getUnsignedShort();
				if (Sequence.cache != null) {
					sequence = Sequence.getSequence(animationId);
				}
			} else if (i == 4)
				modelScaleX = stream.getUnsignedShort();
			else if (i == 5)
				modelScaleY = stream.getUnsignedShort();
			else if (i == 6)
				modelRotation = stream.getUnsignedShort();
			else if (i == 7)
				modelBrightness = stream.getUnsignedByte();
			else if (i == 8)
				modelShadowing = stream.getUnsignedByte();
			else if (i >= 40 && i < 50)
				oldColors[i - 40] = stream.getUnsignedShort();
			else if (i >= 50 && i < 60)
				newColors[i - 50] = stream.getUnsignedShort();
			else
				System.out.println("Error unrecognised spotanim config code: " + i);
		} while (true);
	}

	public Model getModel() {
		Model model = (Model) aMRUNodes_415.get(id);
		if (model != null)
			return model;
		model = Model.method462(modelId);
		if (model == null) {
			return null;
		}
		for (int color = 0; color < 6; color++) {
			if (oldColors[0] != 0) {
				model.changeColors(oldColors[color], newColors[color]);
			}
		}
		aMRUNodes_415.put(model, id);
		return model;
	}

	/**
	 * Returns the amount of spotanim data.
	 * @return
	 */
	public static int getCount() {
		return cache.length;
	}

	private SpotAnim() {
		animationId = -1;
		oldColors = new int[6];
		newColors = new int[6];
		modelScaleX = 128;
		modelScaleY = 128;
	}

	public static SpotAnim cache[];
	public int id;
	public int modelId;
	public int animationId;
	public Sequence sequence;
	public final int[] oldColors;
	public final int[] newColors;
	public int modelScaleX;
	public int modelScaleY;
	public int modelRotation;
	public int modelBrightness;
	public int modelShadowing;
	public static MemCache aMRUNodes_415 = new MemCache(30);

}
