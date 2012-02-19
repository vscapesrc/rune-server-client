package rs2.config.out;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import rs2.config.IdentityKit;
import rs2.sign.signlink;

public class IdentityKitOutput {

	/**
	 * Writes the identity kit data file (idk.dat).
	 */
	public void write() {
		try {
			DataOutputStream dat = new DataOutputStream(new FileOutputStream(signlink.getDirectory() + "idk.dat"));
			dat.writeShort(IdentityKit.cache.length);
			for (IdentityKit idk : IdentityKit.cache) {
				if (idk.partId != -1) {
					dat.writeByte(1);
					dat.writeByte(idk.partId);
				}
				if (idk.models != null) {
					dat.writeByte(2);
					dat.writeByte(idk.models.length);
					for (int index = 0; index < idk.models.length; index++) {
						dat.writeShort(idk.models[index]);
					}
				}
				if (idk.disableDisplay) {
					dat.writeByte(3);
				}
				if (idk.oldColors != null) {
					for (int index = 0; index < idk.oldColors.length; index++) {
						dat.writeByte(40 + index);
						dat.writeShort(idk.oldColors[index]);
					}
				}
				if (idk.newColors != null) {
					for (int index = 0; index < idk.newColors.length; index++) {
						dat.writeByte(50 + index);
						dat.writeShort(idk.newColors[index]);
					}
				}
				if (idk.dialogModels != null) {
					for (int index = 0; index < idk.dialogModels.length; index++) {
						dat.writeByte(60 + index);
						dat.writeShort(idk.dialogModels[index]);
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