package rs2;

import rs2.config.Sequence;
import rs2.config.NPCDefinitions;
import rs2.config.SpotAnim;

public final class NPC extends Entity {

	private Model method450() {
		if (super.forcedAnimation >= 0 && super.anInt1529 == 0) {
			int k = Sequence.getSequence(super.forcedAnimation).frames[super.anInt1527];
			int i1 = -1;
			if (super.renderAnimation >= 0 && super.renderAnimation != super.standAnimIndex)
				i1 = Sequence.getSequence(super.renderAnimation).frames[super.anInt1518];
			return desc.method164(i1, k,
					Sequence.getSequence(super.forcedAnimation).anIntArray357);
		}
		int l = -1;
		if (super.renderAnimation >= 0)
			l = Sequence.getSequence(super.renderAnimation).frames[super.anInt1518];
		return desc.method164(-1, l, null);
	}

	public Model getRotatedModel() {
		if (desc == null)
			return null;
		Model model = method450();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		if (super.graphicsId != -1 && super.anInt1521 != -1) {
			SpotAnim spotAnim = SpotAnim.cache[super.graphicsId];
			Model model_1 = spotAnim.getModel();
			if (model_1 != null) {
				int j = spotAnim.sequence.frames[super.anInt1521];
				Model model_2 = new Model(true, FrameReader.method532(j), false,
						model_1);
				model_2.moveVertices(0, -super.graphicsHeight, 0);
				model_2.method469();
				model_2.method470(j);
				model_2.anIntArrayArray1658 = null;
				model_2.anIntArrayArray1657 = null;
				if (spotAnim.modelScaleX != 128 || spotAnim.modelScaleY != 128)
					model_2.scaleModel(spotAnim.modelScaleX, spotAnim.modelScaleY, spotAnim.modelScaleX);
				model_2.doLighting(64 + spotAnim.modelBrightness, 850 + spotAnim.modelShadowing, -30, -50, -30, true);
				Model aModel[] = { model, model_2 };
				model = new Model(aModel);
			}
		}
		if (desc.tileSize == 1)
			model.aBoolean1659 = true;
		return model;
	}

	public boolean isVisible() {
		return desc != null;
	}

	NPC() {
	}

	public NPCDefinitions desc;
}
