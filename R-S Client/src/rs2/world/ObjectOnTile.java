package rs2.world;

import rs2.Animable;
import rs2.Client;
import rs2.Model;
import rs2.config.Sequence;
import rs2.config.ObjectDef;
import rs2.config.VarBit;

public final class ObjectOnTile extends Animable {

	public Model getRotatedModel() {
		int j = -1;
		if (sequence != null) {
			int k = Client.currentTime - delay;
			if (k > 100 && sequence.frameStep > 0)
				k = 100;
			while (k > sequence.method258(frame)) {
				k -= sequence.method258(frame);
				frame++;
				if (frame < sequence.totalFrames)
					continue;
				frame -= sequence.frameStep;
				if (frame >= 0 && frame < sequence.totalFrames)
					continue;
				sequence = null;
				break;
			}
			delay = Client.currentTime - k;
			if (sequence != null)
				j = sequence.frames[frame];
		}
		ObjectDef def;
		if (anIntArray1600 != null)
			def = method457();
		else
			def = ObjectDef.getDef(objectId);
		if (def == null) {
			return null;
		} else {
			return def.renderObject(anInt1611, anInt1612, anInt1603, anInt1604, anInt1605, anInt1606, j);
		}
	}

	private ObjectDef method457() {
		int i = -1;
		if (anInt1601 != -1) {
			VarBit varBit = VarBit.cache[anInt1601];
			int k = varBit.anInt648;
			int l = varBit.anInt649;
			int i1 = varBit.anInt650;
			int j1 = Client.anIntArray1232[i1 - l];
			i = clientInstance.variousSettings[k] >> l & j1;
		} else if (anInt1602 != -1)
			i = clientInstance.variousSettings[anInt1602];
		if (i < 0 || i >= anIntArray1600.length || anIntArray1600[i] == -1)
			return null;
		else
			return ObjectDef.getDef(anIntArray1600[i]);
	}

	public ObjectOnTile(int i, int j, int k, int l, int i1, int j1, int k1, int animationId, boolean randomize) {
		objectId = i;
		anInt1611 = k;
		anInt1612 = j;
		anInt1603 = j1;
		anInt1604 = l;
		anInt1605 = i1;
		anInt1606 = k1;
		if (animationId != -1) {
			sequence = Sequence.getSeq(animationId);
			frame = 0;
			delay = Client.currentTime;
			if (randomize && sequence.frameStep != -1) {
				frame = (int) (Math.random() * (double) sequence.totalFrames);
				delay -= (int) (Math.random() * (double) sequence.method258(frame));
			}
		}
		ObjectDef def = ObjectDef.getDef(objectId);
		anInt1601 = def.anInt774;
		anInt1602 = def.anInt749;
		anIntArray1600 = def.childrenIDs;
	}

	private int frame;
	private final int[] anIntArray1600;
	private final int anInt1601;
	private final int anInt1602;
	private final int anInt1603;
	private final int anInt1604;
	private final int anInt1605;
	private final int anInt1606;
	private Sequence sequence;
	private int delay;
	public static Client clientInstance;
	private final int objectId;
	private final int anInt1611;
	private final int anInt1612;
}
