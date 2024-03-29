package rs2.cryption;

import java.security.MessageDigest;

public class MD5 {

	/**
	 * Converts a byte array to a hexadecimal string.
	 * @param data
	 * @return
	 */
	private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
        	int halfbyte = (data[i] >>> 4) & 0x0F;
        	int two_halfs = 0;
        	do {
	        	if ((0 <= halfbyte) && (halfbyte <= 9))
	                buf.append((char) ('0' + halfbyte));
	            else
	            	buf.append((char) ('a' + (halfbyte - 10)));
	        	halfbyte = data[i] & 0x0F;
        	} while(two_halfs++ < 1);
        }
        return buf.toString();
    }

	/**
	 * Gets the SHA hash of the given text.
	 * @param text
	 * @return
	 */
	public static String getSHA(String text) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("SHA");
			byte[] md5hash = new byte[32];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			md5hash = md.digest();
			return convertToHex(md5hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the MD5 hash of the given text.
	 * @param text
	 * @return
	 */
	public static String getHash(String text) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] md5hash = new byte[32];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			md5hash = md.digest();
			return convertToHex(md5hash);
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
}