package rs2.config.out;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import rs2.config.NPCDefinitions;
import rs2.sign.signlink;
import rs2.util.DataUtils;

public class NPCDefinitionsOutput {
	
	/**
	 * Writes the NPC definitions (npc.dat, npc.idx).
	 * @throws IOException
	 */
	public static void repackNPCs() throws IOException {
		try {
			DataOutputStream dat = new DataOutputStream(new FileOutputStream(signlink.getDirectory() + "npc.dat"));
			DataOutputStream idx = new DataOutputStream(new FileOutputStream(signlink.getDirectory() + "npc.idx"));
			idx.writeShort(NPCDefinitions.getCount());
			dat.writeShort(NPCDefinitions.getCount());
			for (int index = 0; index < NPCDefinitions.getCount(); index++) {
				int datSize1 = dat.size();
				NPCDefinitions npc = NPCDefinitions.getDefinition(index);
				if (npc.entityModels != null) {
					dat.writeByte(1);
					dat.writeByte(npc.entityModels.length);
					for (int modelArray = 0; modelArray < npc.entityModels.length; modelArray++) {
						dat.writeShort(npc.entityModels[modelArray]);
					}
				}
				if (npc.name != null) {
					dat.writeByte(2);
					DataUtils.writeString(dat, npc.name);
				}
				if (npc.description != null) {
					dat.writeByte(3);
					DataUtils.writeString(dat, new String(npc.description));
				}
				if (npc.tileSize != 1) {
					dat.writeByte(12);
					dat.writeByte(npc.tileSize);
				}
				if (npc.standAnim != -1) {
					dat.writeByte(13);
					dat.writeShort(npc.standAnim);
				}
				if (npc.walkAnim != -1) {
					dat.writeByte(14);
					dat.writeShort(npc.walkAnim);
				}
				if (npc.walkAnim != -1 || npc.turn180Anim != -1 || npc.turn90LeftAnim != -1 || npc.turn90RightAnim != -1) {
					dat.writeByte(17);
					dat.writeShort(npc.walkAnim);
					dat.writeShort(npc.turn180Anim);
					dat.writeShort(npc.turn90LeftAnim);
					dat.writeShort(npc.turn90RightAnim);
				}
				if (npc.actions != null) {
					for (int actionArray = 0; actionArray < npc.actions.length; actionArray++) {
						dat.writeByte(30 + actionArray);
						if (npc.actions[actionArray] == null) {
							DataUtils.writeString(dat, "hidden");
						} else {
							DataUtils.writeString(dat, npc.actions[actionArray]);
						}
					}
				}
				if (npc.oldColors != null) {
					dat.writeByte(40);
					dat.writeByte(npc.oldColors.length);
					for (int colorArray = 0; colorArray < npc.oldColors.length; colorArray++) {
						dat.writeShort(npc.oldColors[colorArray]);
						dat.writeShort(npc.newColors[colorArray]);
					}
				}
				if (npc.dialogModels != null) {
					dat.writeByte(60);
					dat.write(npc.dialogModels.length);
					for (int chatHeadModelArray : npc.dialogModels) {
						dat.writeShort(chatHeadModelArray);
					}
				}
				if (!npc.displayMapMarker) {
					dat.writeByte(93);
				}
				if (npc.combatLevel != -1) {
					dat.writeByte(95);
					dat.writeShort(npc.combatLevel);
				}
				if (npc.modelScaleXZ != 128) {
					dat.writeByte(97);
					dat.writeShort(npc.modelScaleXZ);
				}
				if (npc.modelScaleY != 128) {
					dat.writeByte(98);
					dat.writeShort(npc.modelScaleY);
				}
				if (npc.visible) {
					dat.writeByte(99);
				}
				if (npc.modelBrightness != -1) {
					dat.writeByte(100);
					dat.writeByte(npc.modelBrightness);
				}
				if (npc.modelShadowing != -1) {
					dat.writeByte(101);
					dat.writeByte((npc.modelShadowing / 5));
				}
				if (npc.headIcon != -1) {
					dat.writeByte(102);
					dat.writeShort(npc.headIcon);
				}
				if (npc.getDegreesToTurn != 32) {
					dat.writeByte(103);
					dat.writeShort(npc.getDegreesToTurn);
				}
				if (npc.childrenIDs != null) {
					dat.writeByte(106);
					dat.writeShort(npc.varBitChild);
					dat.writeShort(npc.configChild);
					dat.writeByte(npc.childrenIDs.length - 1);
					for (int childrenArray = 0; childrenArray < npc.childrenIDs.length; childrenArray++) {
						dat.writeShort(npc.childrenIDs[childrenArray]);
					}
				}
				if (!npc.aBoolean84) {
					dat.writeByte(107);
				}
				dat.writeByte(0);
				int datSize2 = dat.size();
				idx.writeShort(datSize2 - datSize1);
			}
			dat.close();
			idx.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
