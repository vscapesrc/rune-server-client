package rs2.config.out;

import java.io.IOException;

import rs2.ExtendedByteArrayOutputStream;
import rs2.config.ItemDefinitions;
import rs2.constants.Constants;
import rs2.sign.signlink;
import rs2.util.DataUtils;

public class ItemDefinitionsOutput {

	/**
	 * Writes the byte arrays for the index and data files to the files.
	 * @throws IOException
	 */
	public void write() throws IOException {
		byte[][] repacked = recompile();
		DataUtils.writeFile(repacked[0], signlink.getDirectory() + "obj.idx");
		DataUtils.writeFile(repacked[1], signlink.getDirectory() + "obj.dat");
	}

	/**
	 * Returns the byte arrays for index and data files.
	 * @return
	 * @throws IOException
	 */
    public static byte[][] recompile() throws IOException {
        ExtendedByteArrayOutputStream index = new ExtendedByteArrayOutputStream();
        ExtendedByteArrayOutputStream data = new ExtendedByteArrayOutputStream();
        index.writeShort(ItemDefinitions.totalItems);
		data.writeShort(ItemDefinitions.totalItems);
        for (int i = 0; i < ItemDefinitions.totalItems; i++) {
        	ItemDefinitions item = ItemDefinitions.getDefinition(i);
            byte[] newData = recompileData(item);
            data.write(newData);
            index.writeShort(newData.length);
        }
        index.close();
        data.close();
        return new byte[][] {index.toByteArray(), data.toByteArray()};
    }

    /**
     * Returns the byte array for the obj.dat data.
     * @param item Item that is being written.
     * @return
     * @throws IOException
     */
	public static byte[] recompileData(ItemDefinitions item) throws IOException {
		ExtendedByteArrayOutputStream stream = new ExtendedByteArrayOutputStream();
		if (item.displayModel != 1) {
			stream.write(1);
			stream.writeShort(item.displayModel);
		}
		if (item.name != null) {
			stream.write(2);
			stream.writeString(item.name);
		}
		if (item.description != null) {
			stream.write(3);
			stream.writeString(new String(item.description));
		}
		if (item.modelZoom != 2000) {
			stream.write(4);
			stream.writeShort(item.modelZoom);
		}
		if (item.modelRotationX != 0) {
			stream.write(5);
			stream.writeShort(item.modelRotationX);
		}
		if (item.modelRotationY != 0) {
			stream.write(6);
			stream.writeShort(item.modelRotationY);
		}
		if (item.modelOffsetX != 0) {
			stream.write(7);
			stream.writeShort(item.modelOffsetX);
		}
		if (item.modelOffsetY != 0) {
			stream.write(8);
			stream.writeShort(item.modelOffsetY);
		}
		if (item.stackable) {
			stream.write(11);
		}
		if (item.value != 1) {
			stream.write(12);
			stream.writeInt(item.value);
		}
		if (item.membersObject) {
			stream.write(16);
		}
		if (item.maleModel1 != -1 || item.maleOffset != 0) {
			stream.write(23);
			stream.writeShort(item.maleModel1);
			stream.write(item.maleOffset);
		}
		if (item.maleModel2 != -1) {
			stream.write(24);
			stream.writeShort(item.maleModel2);
		}
		if (item.femaleModel1 != -1 || item.femaleOffset != 0) {
			stream.write(25);
			stream.writeShort(item.femaleModel1);
			stream.write(item.femaleOffset);
		}
		if (item.femaleModel2 != -1) {
			stream.write(26);
			stream.writeShort(item.femaleModel2);
		}
        if (item.groundActions != null) {
            for (int i = 0; i < 5; i++) {
                stream.write(30 + i);
                if (item.groundActions[i] == null) {
                	stream.writeString("hidden");
                } else {
                	stream.writeString(item.groundActions[i]);
                }
            }
        }
        if (item.actions != null) {
			int x = 0;
            for (int i = 0; i < 5; i++) {
				if (item.actions[i] != null) {
                	stream.write(35 + (x++));
                	stream.writeString(item.actions[i]);
				}
            }
        }
        if (item.oldColors != null || item.newColors != null) {
        	stream.write(40);
        	stream.write(item.oldColors.length);
            for (int i = 0; i < item.oldColors.length; i++) {
            	stream.writeShort(item.oldColors[i]);
            	stream.writeShort(item.newColors[i]);
            }
        }
        if (item.maleModel3 != -1) {
            stream.write(78);
            stream.writeShort(item.maleModel3);
        }
        if (item.femaleModel3 != -1) {
            stream.write(79);
            stream.writeShort(item.femaleModel3);
        }
        if (item.maleDialog1 != -1) {
            stream.write(90);
            stream.writeShort(item.maleDialog1);
        }
        if (item.femaleDialog1 != -1) {
            stream.write(91);
            stream.writeShort(item.femaleDialog1);
        }
        if (item.maleDialog2 != -1) {
            stream.write(92);
            stream.writeShort(item.maleDialog2);
        }
        if (item.femaleDialog2 != -1) {
            stream.write(93);
            stream.writeShort(item.femaleDialog2);
        }
        if (item.anInt204 != -1) {
            stream.write(95);
            stream.writeShort(item.anInt204);
        }
        if (item.certId != -1) {
            stream.write(97);
            stream.writeShort(item.certId);
        }
        if (item.certTemplateId != -1) {
            stream.write(98);
            stream.writeShort(item.certTemplateId);
        }
        if (item.stackIDs != null || item.stackAmounts != null) {
            for (int i = 0; i < 9; i++) {
                stream.write(i + 100);
                stream.writeShort(item.stackIDs[i]);
                stream.writeShort(item.stackAmounts[i]);
            }
        }
        if (item.modelScaleX != 128) {
            stream.write(110);
            stream.writeShort(item.modelScaleX);
        }
        if (item.modelScaleY != 128) {
            stream.write(111);
            stream.writeShort(item.modelScaleY);
        }
        if (item.modelScaleZ != 128) {
            stream.write(112);
            stream.writeShort(item.modelScaleZ);
        }
        if (item.modelBrightness != 0) {
            stream.write(113);
            stream.write(item.modelBrightness);
        }
        if (item.modelShadowing != 0) {
            stream.write(114);
            stream.write(item.modelShadowing);
        }
        if (item.team != 0) {
            stream.write(115);
            stream.write(item.team);
        }
        if (Constants.LENT_ITEMS) {
	        if (item.lentId != -1) {
	            stream.write(121);
	            stream.writeShort(item.lentId);
	        }
	        if (item.lentTemplateId != -1) {
	            stream.write(122);
	            stream.writeShort(item.lentTemplateId);
	        }
        }
        stream.write(0);
		stream.close();
		return stream.toByteArray();
	}

}