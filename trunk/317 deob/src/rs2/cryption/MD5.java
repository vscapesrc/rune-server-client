package rs2.cryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	/**
	 * Returns the byte array as a hexadecimal string.
	 * @param data
	 * @return
	 */
	private static String getHex(byte[] data) {
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
	 * Returns the text as an SHA string.
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String getSHA(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return getHex(md5hash);
	}

	/**
	 * Returns the text as an MD5 hash.
	 * @param text
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String getMD5(String text)  {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] hash = new byte[32];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			hash = md.digest();
			return getHex(hash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}