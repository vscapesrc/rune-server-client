package rs2.config.out;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import rs2.config.SpotAnim;
import rs2.sign.signlink;

public class SpotAnimOutput {

	/**
	 * Writes the graphics data file (spotanim.dat).
	 * @throws IOException
	 */
	public static void repack() {
		try {
			DataOutputStream dat = new DataOutputStream(new FileOutputStream(signlink.getDirectory() + "spotanim.dat"));
			dat.writeShort(SpotAnim.getCount());
			for (SpotAnim spotAnim : SpotAnim.cache) {
				if(spotAnim.modelId != -1) {
					dat.writeByte(1);
					dat.writeShort(spotAnim.modelId);
				}
				if(spotAnim.animationId != -1) {
					dat.writeByte(2);
					dat.writeShort(spotAnim.animationId);
				}
				if(spotAnim.modelScaleX != 128) {
					dat.writeByte(4);
					dat.writeShort(spotAnim.modelScaleX);
				}
				if(spotAnim.modelScaleY != 128) {
					dat.writeByte(5);
					dat.writeShort(spotAnim.modelScaleY);
				}
				if(spotAnim.modelRotation != -1) {
					dat.writeByte(6);
					dat.writeShort(spotAnim.modelRotation);
				}
				if(spotAnim.modelBrightness != -1) {
					dat.writeByte(7);
					dat.writeByte(spotAnim.modelBrightness);
				}
				if(spotAnim.modelShadowing != -1) {
					dat.writeByte(8);
					dat.writeByte(spotAnim.modelShadowing);
				}
				if (spotAnim.oldColors != null) {
					for (int index = 0; index < spotAnim.oldColors.length; index++) {
						dat.writeByte(40 + index);
						dat.writeShort(spotAnim.oldColors[index]);
					}
				}
				if (spotAnim.newColors != null) {
					for (int index = 0; index < spotAnim.newColors.length; index++) {
						dat.writeByte(50 + index);
						dat.writeShort(spotAnim.newColors[index]);
					}
				}
				dat.writeByte(0);
			}
			dat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}