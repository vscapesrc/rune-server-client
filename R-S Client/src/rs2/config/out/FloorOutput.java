package rs2.config.out;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import rs2.config.Floor;
import rs2.sign.signlink;
import rs2.util.DataUtils;

public class FloorOutput {

	/**
	 * Writes the floor data file (flo.dat).
	 * @throws IOException
	 */
	public void write() {
		try {
			DataOutputStream dat = new DataOutputStream(new FileOutputStream(signlink.getDirectory() + "flo.dat"));
			dat.writeShort(Floor.getCount());
			for (Floor floor : Floor.cache) {
	            if (floor.terrainColor != null) {
	                dat.writeByte(1);
	                DataUtils.write3Bytes(dat, floor.terrainColor.getRGB());
	            }
	            if (floor.textureId != -1) {
	            	dat.writeByte(2);
	            	dat.writeByte(floor.textureId);
	            }
	            if (!floor.occlude) {
	                dat.writeByte(5);
	            }
	            if (floor.name != null) {
	                dat.writeByte(6);
	                DataUtils.writeString(dat, floor.name);
	            }
	            if (floor.minimapColor != null) {
	                dat.writeByte(7);
	                DataUtils.write3Bytes(dat, floor.minimapColor.getRGB());
	            }
	            dat.writeByte(0);
			}
			dat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}