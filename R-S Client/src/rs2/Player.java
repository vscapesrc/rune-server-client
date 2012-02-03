package rs2;

import rs2.config.Sequence;
import rs2.config.NPCDef;
import rs2.config.IdentityKit;
import rs2.config.ItemDef;
import rs2.config.SpotAnim;
import rs2.util.TextUtils;

public final class Player extends Entity {

	public Model getRotatedModel() {
		if (!visible)
			return null;
		Model model = method452();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		model.aBoolean1659 = true;
		if (aBoolean1699)
			return model;
		if (super.graphicsId != -1 && super.anInt1521 != -1) {
			SpotAnim spotAnim = SpotAnim.cache[super.graphicsId];
			Model model_2 = spotAnim.getModel();
			if (model_2 != null) {
				Model model_3 = new Model(true,
						FrameHeader.method532(super.anInt1521), false, model_2);
				model_3.method475(0, -super.graphicsHeight, 0);
				model_3.method469();
				model_3.method470(spotAnim.sequence.frames[super.anInt1521]);
				model_3.anIntArrayArray1658 = null;
				model_3.anIntArrayArray1657 = null;
				if (spotAnim.anInt410 != 128 || spotAnim.anInt411 != 128)
					model_3.method478(spotAnim.anInt410, spotAnim.anInt410,
							spotAnim.anInt411);
				model_3.method479(64 + spotAnim.anInt413,
						850 + spotAnim.anInt414, -30, -50, -30, true);
				Model aclass30_sub2_sub4_sub6_1s[] = { model, model_3 };
				model = new Model(aclass30_sub2_sub4_sub6_1s);
			}
		}
		if (aModel_1714 != null) {
			if (Client.currentTime >= anInt1708)
				aModel_1714 = null;
			if (Client.currentTime >= anInt1707 && Client.currentTime < anInt1708) {
				Model model_1 = aModel_1714;
				model_1.method475(anInt1711 - super.currentX, anInt1712 - anInt1709,
						anInt1713 - super.currentY);
				if (super.turnDirection == 512) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536)
					model_1.method473();
				Model aclass30_sub2_sub4_sub6s[] = { model, model_1 };
				model = new Model(aclass30_sub2_sub4_sub6s);
				if (super.turnDirection == 512)
					model_1.method473();
				else if (super.turnDirection == 1024) {
					model_1.method473();
					model_1.method473();
				} else if (super.turnDirection == 1536) {
					model_1.method473();
					model_1.method473();
					model_1.method473();
				}
				model_1.method475(super.currentX - anInt1711, anInt1709 - anInt1712,
						super.currentY - anInt1713);
			}
		}
		model.aBoolean1659 = true;
		return model;
	}

	public void updatePlayer(ByteBuffer buffer) {
		buffer.offset = 0;
		gender = buffer.getUByte();
		prayerId = buffer.getUByte();
		skullId = buffer.getUByte();
		desc = null;
		team = 0;
		for (int j = 0; j < 12; j++) {
			int k = buffer.getUByte();
			if (k == 0) {
				equipment[j] = 0;
				continue;
			}
			int i1 = buffer.getUByte();
			equipment[j] = (k << 8) + i1;
			if (j == 0 && equipment[0] == 65535) {
				desc = NPCDef.getDef(buffer.getShort());
				break;
			}
			if (equipment[j] >= 512 && equipment[j] - 512 < ItemDef.totalItems) {
				int l1 = ItemDef.getDef(equipment[j] - 512).team;
				if (l1 != 0)
					team = l1;
			}
		}

		for (int l = 0; l < 5; l++) {
			int j1 = buffer.getUByte();
			if (j1 < 0 || j1 >= Client.anIntArrayArray1003[l].length)
				j1 = 0;
			anIntArray1700[l] = j1;
		}

		super.standAnimIndex = buffer.getShort();
		if (super.standAnimIndex == 65535)
			super.standAnimIndex = -1;
		super.standTurnAnimIndex = buffer.getShort();
		if (super.standTurnAnimIndex == 65535)
			super.standTurnAnimIndex = -1;
		super.walkAnimIndex = buffer.getShort();
		if (super.walkAnimIndex == 65535)
			super.walkAnimIndex = -1;
		super.turn180AnimIndex = buffer.getShort();
		if (super.turn180AnimIndex == 65535)
			super.turn180AnimIndex = -1;
		super.turn90CWAnimIndex = buffer.getShort();
		if (super.turn90CWAnimIndex == 65535)
			super.turn90CWAnimIndex = -1;
		super.turn90CCWAnimIndex = buffer.getShort();
		if (super.turn90CCWAnimIndex == 65535)
			super.turn90CCWAnimIndex = -1;
		super.runAnimIndex = buffer.getShort();
		if (super.runAnimIndex == 65535)
			super.runAnimIndex = -1;
		name = TextUtils.fixName(TextUtils.nameForLong(buffer.getLong()));
		combatLevel = buffer.getUByte();
		skill = buffer.getShort();
		visible = true;
		aLong1718 = 0L;
		for (int k1 = 0; k1 < 12; k1++) {
			aLong1718 <<= 4;
			if (equipment[k1] >= 256)
				aLong1718 += equipment[k1] - 256;
		}

		if (equipment[0] >= 256)
			aLong1718 += equipment[0] - 256 >> 4;
		if (equipment[1] >= 256)
			aLong1718 += equipment[1] - 256 >> 8;
		for (int i2 = 0; i2 < 5; i2++) {
			aLong1718 <<= 3;
			aLong1718 += anIntArray1700[i2];
		}

		aLong1718 <<= 1;
		aLong1718 += gender;
	}

	private Model method452() {
		if (desc != null) {
			int j = -1;
			if (super.anim >= 0 && super.anInt1529 == 0)
				j = Sequence.getSeq(super.anim).frames[super.anInt1527];
			else if (super.anInt1517 >= 0)
				j = Sequence.getSeq(super.anInt1517).frames[super.anInt1518];
			Model model = desc.method164(-1, j, null);
			return model;
		}
		long l = aLong1718;
		int k = -1;
		int i1 = -1;
		int j1 = -1;
		int k1 = -1;
		if (super.anim >= 0 && super.anInt1529 == 0) {
			Sequence animation = Sequence.getSeq(super.anim);
			k = animation.frames[super.anInt1527];
			if (super.anInt1517 >= 0 && super.anInt1517 != super.standAnimIndex)
				i1 = Sequence.getSeq(super.anInt1517).frames[super.anInt1518];
			if (animation.anInt360 >= 0) {
				j1 = animation.anInt360;
				l += j1 - equipment[5] << 40;
			}
			if (animation.anInt361 >= 0) {
				k1 = animation.anInt361;
				l += k1 - equipment[3] << 48;
			}
		} else if (super.anInt1517 >= 0)
			k = Sequence.getSeq(super.anInt1517).frames[super.anInt1518];
		Model model_1 = (Model) mruNodes.get(l);
		if (model_1 == null) {
			boolean flag = false;
			for (int i2 = 0; i2 < 12; i2++) {
				int k2 = equipment[i2];
				if (k1 >= 0 && i2 == 3)
					k2 = k1;
				if (j1 >= 0 && i2 == 5)
					k2 = j1;
				if (k2 >= 256 && k2 < 512 && !IdentityKit.cache[k2 - 256].method537())
					flag = true;
				if (k2 >= 512 && !ItemDef.getDef(k2 - 512).method195(gender))
					flag = true;
			}

			if (flag) {
				if (aLong1697 != -1L)
					model_1 = (Model) mruNodes.get(aLong1697);
				if (model_1 == null)
					return null;
			}
		}
		if (model_1 == null) {
			Model aclass30_sub2_sub4_sub6s[] = new Model[12];
			int j2 = 0;
			for (int l2 = 0; l2 < 12; l2++) {
				int i3 = equipment[l2];
				if (k1 >= 0 && l2 == 3)
					i3 = k1;
				if (j1 >= 0 && l2 == 5)
					i3 = j1;
				if (i3 >= 256 && i3 < 512) {
					Model model_3 = IdentityKit.cache[i3 - 256].method538();
					if (model_3 != null)
						aclass30_sub2_sub4_sub6s[j2++] = model_3;
				}
				if (i3 >= 512) {
					Model model_4 = ItemDef.getDef(i3 - 512)
							.method196(gender);
					if (model_4 != null)
						aclass30_sub2_sub4_sub6s[j2++] = model_4;
				}
			}

			model_1 = new Model(j2, aclass30_sub2_sub4_sub6s);
			for (int j3 = 0; j3 < 5; j3++)
				if (anIntArray1700[j3] != 0) {
					model_1.changeModelColors(Client.anIntArrayArray1003[j3][0],
							Client.anIntArrayArray1003[j3][anIntArray1700[j3]]);
					if (j3 == 1)
						model_1.changeModelColors(Client.anIntArray1204[0],
								Client.anIntArray1204[anIntArray1700[j3]]);
				}

			model_1.method469();
			model_1.method479(64, 850, -30, -50, -30, true);
			mruNodes.put(model_1, l);
			aLong1697 = l;
		}
		if (aBoolean1699)
			return model_1;
		Model model_2 = Model.aModel_1621;
		model_2.method464(model_1, FrameHeader.method532(k) & FrameHeader.method532(i1));
		if (k != -1 && i1 != -1)
			model_2.method471(Sequence.getSeq(super.anim).anIntArray357, i1, k);
		else if (k != -1)
			model_2.method470(k);
		model_2.method466();
		model_2.anIntArrayArray1658 = null;
		model_2.anIntArrayArray1657 = null;
		return model_2;
	}

	public boolean isVisible() {
		return visible;
	}

	public int rights;

	public Model getPlayerModel() {
		if (!visible)
			return null;
		if (desc != null)
			return desc.method160();
		boolean flag = false;
		for (int i = 0; i < 12; i++) {
			int j = equipment[i];
			if (j >= 256 && j < 512 && !IdentityKit.cache[j - 256].method539())
				flag = true;
			if (j >= 512 && !ItemDef.getDef(j - 512).method192(gender))
				flag = true;
		}

		if (flag)
			return null;
		Model aclass30_sub2_sub4_sub6s[] = new Model[12];
		int k = 0;
		for (int l = 0; l < 12; l++) {
			int i1 = equipment[l];
			if (i1 >= 256 && i1 < 512) {
				Model model_1 = IdentityKit.cache[i1 - 256].method540();
				if (model_1 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_1;
			}
			if (i1 >= 512) {
				Model model_2 = ItemDef.getDef(i1 - 512).method194(gender);
				if (model_2 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_2;
			}
		}

		Model model = new Model(k, aclass30_sub2_sub4_sub6s);
		for (int j1 = 0; j1 < 5; j1++)
			if (anIntArray1700[j1] != 0) {
				model.changeModelColors(Client.anIntArrayArray1003[j1][0],
						Client.anIntArrayArray1003[j1][anIntArray1700[j1]]);
				if (j1 == 1)
					model.changeModelColors(Client.anIntArray1204[0],
							Client.anIntArray1204[anIntArray1700[j1]]);
			}

		return model;
	}

	Player() {
		aLong1697 = -1L;
		aBoolean1699 = false;
		anIntArray1700 = new int[5];
		visible = false;
		equipment = new int[12];
	}

	private long aLong1697;
	public NPCDef desc;
	boolean aBoolean1699;
	final int[] anIntArray1700;
	public int team;
	private int gender;
	public String name;
	static MemCache mruNodes = new MemCache(260);
	public int combatLevel;
	public int prayerId;
	public int anInt1707;
	int anInt1708;
	int anInt1709;
	boolean visible;
	int anInt1711;
	int anInt1712;
	int anInt1713;
	Model aModel_1714;
	public final int[] equipment;
	private long aLong1718;
	int anInt1719;
	int anInt1720;
	int anInt1721;
	int anInt1722;
	int skill;
	public int skullId;

}
