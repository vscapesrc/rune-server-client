package rs2;

import rs2.config.SpotAnim;

final class StillGraphics extends Animable {

	public StillGraphics(int i, int j, int l, int i1, int j1, int k1, int l1) {
		aBoolean1567 = false;
		graphics = SpotAnim.cache[i1];
		anInt1560 = i;
		anInt1561 = l1;
		anInt1562 = k1;
		anInt1563 = j1;
		anInt1564 = j + l;
		aBoolean1567 = false;
	}

	public Model getRotatedModel() {
		Model model = graphics.getModel();
		if (model == null)
			return null;
		int j = graphics.sequence.frames[anInt1569];
		Model model_1 = new Model(true, FrameHeader.method532(j), false, model);
		if (!aBoolean1567) {
			model_1.method469();
			model_1.method470(j);
			model_1.anIntArrayArray1658 = null;
			model_1.anIntArrayArray1657 = null;
		}
		if (graphics.anInt410 != 128 || graphics.anInt411 != 128)
			model_1.method478(graphics.anInt410, graphics.anInt410,
					graphics.anInt411);
		if (graphics.anInt412 != 0) {
			if (graphics.anInt412 == 90)
				model_1.method473();
			if (graphics.anInt412 == 180) {
				model_1.method473();
				model_1.method473();
			}
			if (graphics.anInt412 == 270) {
				model_1.method473();
				model_1.method473();
				model_1.method473();
			}
		}
		model_1.method479(64 + graphics.anInt413, 850 + graphics.anInt414, -30, -50, -30, true);
		return model_1;
	}

	public void method454(int i) {
		for (anInt1570 += i; anInt1570 > graphics.sequence.method258(anInt1569);) {
			anInt1570 -= graphics.sequence.method258(anInt1569) + 1;
			anInt1569++;
			if (anInt1569 >= graphics.sequence.totalFrames && (anInt1569 < 0 || anInt1569 >= graphics.sequence.totalFrames)) {
				anInt1569 = 0;
				aBoolean1567 = true;
			}
		}

	}

	public final int anInt1560;
	public final int anInt1561;
	public final int anInt1562;
	public final int anInt1563;
	public final int anInt1564;
	public boolean aBoolean1567;
	private final SpotAnim graphics;
	private int anInt1569;
	private int anInt1570;
}
